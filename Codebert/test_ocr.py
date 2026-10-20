from src.ocr_processor import OCRProcessor


processor = OCRProcessor()

text = processor.extract_text(
    "data/invoice/invoice_01_retail_receipt.jpg"
)

print("OCR extraction completed")
print("Characters extracted:", len(text))
print("Preview:")
print(text[:500])