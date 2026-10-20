from pathlib import Path
from src.ocr_processor import OCRProcessor
import pymupdf


class Document:
    """
    Represents a document entering the Dostabase ingestion pipeline.

    Stage 1 capabilities:
    - path validation
    - file-format detection
    - plain-text extraction
    - native PDF text extraction
    - image OCR extraction
    - scanned-PDF OCR extraction
    """

    SUPPORTED_FORMATS = {
        ".pdf": "pdf",
        ".jpg": "image",
        ".jpeg": "image",
        ".png": "image",
        ".txt": "text",
    }

    def __init__(self, path):
        self.path = Path(path)
        self.text = ""
        self.format = None
        self.page_count = 0
        self.requires_ocr = False
        self.ocr_processor = OCRProcessor()

    def validate_path(self):
        if not self.path.exists():
            raise FileNotFoundError(
                f"Document not found: {self.path}"
            )

        if not self.path.is_file():
            raise ValueError(
                f"Path is not a file: {self.path}"
            )

    def detect_format(self):
        self.validate_path()

        extension = self.path.suffix.lower()

        if extension not in self.SUPPORTED_FORMATS:
            raise ValueError(
                f"Unsupported document format: "
                f"{extension or 'no extension'}"
            )

        self.format = self.SUPPORTED_FORMATS[extension]
        return self.format
    def extract_plain_text(self):
        """
        Read a plain-text document directly.

        The methodology specifies direct reading for
        plain-text input without OCR.
        """

        if self.format is None:
            self.detect_format()

        if self.format != "text":
            raise ValueError(
                "Plain-text extraction requires a text document."
            )

        with open(
            self.path,
            "r",
            encoding="utf-8"
        ) as file:
            self.text = file.read()

        self.text = self.text.strip()
        self.requires_ocr = False

        return self.text
    def extract_pdf_text(self):
        """
        Extract native text from a PDF using PyMuPDF.

        No OCR fallback is performed here.
        """
        if self.format is None:
            self.detect_format()

        if self.format != "pdf":
            raise ValueError(
                "Native PDF extraction requires a PDF document."
            )

        page_texts = []

        with pymupdf.open(self.path) as pdf:
            self.page_count = pdf.page_count

            for page in pdf:
                text = page.get_text("text", sort=True)
                page_texts.append(text)

        self.text = "\n".join(page_texts).strip()
        self.requires_ocr = not bool(self.text)

        return self.text


    def determine_extraction_route(self):
        """
        Determine whether the document can use native text
        or requires OCR.

        This method does not perform OCR.
        """
        if self.format is None:
            self.detect_format()

        if self.format == "image":
            self.requires_ocr = True
            return "ocr"

        if self.format == "pdf":
            if self.text:
                self.requires_ocr = False
                return "native_pdf"

            self.requires_ocr = True
            return "ocr"

        if self.format == "text":
            self.requires_ocr = False
            return "plain_text"

        raise ValueError(
            f"No extraction route for format: {self.format}"
        )
    def extract_ocr_text(self):
        """
        Perform OCR extraction for image documents.

        PDF page rendering will be added separately
        for image-only PDFs.
        """

        if self.format is None:
            self.detect_format()

        if self.format != "image":
            raise ValueError(
                "OCR image extraction requires an image file."
            )

        self.text = self.ocr_processor.extract_text(
            self.path
        )

        return self.text


    def extract_scanned_pdf_ocr_text(self):
        """
        Render PDF pages as images and apply OCR.

        Used when native PDF extraction
        produces no text.
        """

        if self.format != "pdf":
            raise ValueError(
                "Scanned PDF OCR requires a PDF document."
            )

        import pymupdf

        page_texts = []

        with pymupdf.open(self.path) as pdf:

            self.page_count = pdf.page_count

            for page_number, page in enumerate(pdf):

                pix = page.get_pixmap(
                    dpi=300
                )

                image_path = (
                    Path("storage")
                    / f"{self.path.stem}_ocr_page_{page_number}.png"
                )

                pix.save(image_path)

                text = self.ocr_processor.extract_text(
                    image_path
                )

                page_texts.append(text)

        self.text = "\n".join(page_texts).strip()

        return self.text


