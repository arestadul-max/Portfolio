import numpy as np

from src.metadata import MetadataStore


store = MetadataStore()

vector = np.zeros(768)

path = store.save(
    filename="sample_document.pdf",
    document_format="pdf",
    text="Sample document text",
    embedding=vector
)

print("Metadata saved:")
print(path)