from pathlib import Path

import cv2
import pytesseract
from PIL import Image


class OCRProcessor:
    """
    OCR processing pipeline.

    Steps:
    1. Image loading
    2. Preprocessing
    3. Tesseract recognition
    4. Text post-processing
    """

    def __init__(self):
        self.raw_text = ""
        self.cleaned_text = ""

    def preprocess_image(self, image_path):
        """
        Image preprocessing:
        - grayscale conversion
        - noise reduction
        - binarisation
        """

        image = cv2.imread(str(image_path))

        if image is None:
            raise ValueError(
                f"Unable to load image: {image_path}"
            )

        gray = cv2.cvtColor(
            image,
            cv2.COLOR_BGR2GRAY
        )

        denoised = cv2.medianBlur(
            gray,
            3
        )

        _, binary = cv2.threshold(
            denoised,
            0,
            255,
            cv2.THRESH_BINARY + cv2.THRESH_OTSU
        )

        return binary

    def recognize(self, image):
        """
        OCR recognition using Tesseract.
        """

        pil_image = Image.fromarray(image)

        self.raw_text = pytesseract.image_to_string(
            pil_image
        )

        return self.raw_text

    def post_process(self, text):
        """
        Basic text cleanup.
        """

        lines = [
            line.strip()
            for line in text.splitlines()
            if line.strip()
        ]

        self.cleaned_text = "\n".join(lines)

        return self.cleaned_text

    def extract_text(self, image_path):

        processed = self.preprocess_image(
            image_path
        )

        raw = self.recognize(
            processed
        )

        cleaned = self.post_process(
            raw
        )

        return cleaned