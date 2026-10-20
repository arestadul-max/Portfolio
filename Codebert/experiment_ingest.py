from pathlib import Path

from src.pipeline import DocumentPipeline


documents = [
    "data/academic/academic_01_ai_research.pdf",
    "data/academic/academic_02_information_retrieval.pdf",
    "data/academic/academic_03_nlp_study.pdf",

    "data/invoice/invoice_01_retail_receipt.jpg",
    "data/invoice/invoice_02_medical_bill.jpg",
    "data/invoice/invoice_03_sample_invoice.jpg",

    "data/legal/legal_01_service_agreement.pdf",
    "data/legal/legal_02_terms_conditions.pdf",
    "data/legal/legal_03_non_disclosure_agreement.pdf",

    "data/scanned/scanned_01_printed_page.pdf",
    "data/scanned/scanned_02_low_quality_scan.pdf",
    "data/scanned/scanned_03_image_based_document.pdf",

    "data/technical/technical_01_machine_learning.pdf",
    "data/technical/technical_02_software_architecture.pdf",
    "data/technical/technical_03_database_systems.pdf",
]


pipeline = DocumentPipeline()


print("DOSTABASE - FULL DATASET INGESTION EXPERIMENT")
print("")


for file in documents:

    result = pipeline.process(file)

    print(
        f"{Path(file).name} | "
        f"format={result['format']} | "
        f"text={result['text_length']} | "
        f"embedding={result['embedding_dimension']}"
    )


print("")
print(
    "Total documents processed:",
    len(documents)
)