# Dostabase CLI

A Python document-ingestion and search project. It extracts text from PDF, image, and plain-text files, creates CodeBERT embeddings, stores document metadata, and uses an HNSW index for semantic retrieval.

## Setup

From this directory, install the Python dependencies:

```bash
pip install -r requirements.txt
```

Image OCR uses Tesseract; install the Tesseract OCR application separately and make it available to `pytesseract`.

## Run

```bash
python main.py
```

The CLI starts the document pipeline. The project also includes tests and scripts for ingestion, retrieval evaluation, and metadata migration.
