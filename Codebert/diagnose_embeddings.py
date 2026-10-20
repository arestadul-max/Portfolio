import json
from pathlib import Path

import numpy as np

from src.codebert_encoder import CodeBERTEncoder


QUERIES = [
    "artificial intelligence research and machine learning",
    "service agreement and contract conditions",
    "database architecture and management systems",
    "medical bill and payment information",
    "patient case history document",
]


def cosine_similarity(a, b):
    a = np.asarray(a, dtype=np.float32)
    b = np.asarray(b, dtype=np.float32)

    denominator = np.linalg.norm(a) * np.linalg.norm(b)

    if denominator == 0:
        return 0.0

    return float(np.dot(a, b) / denominator)


# ---------------------------------------------------------
# 1. Encode all five experimental queries
# ---------------------------------------------------------

encoder = CodeBERTEncoder()

query_vectors = []

for query in QUERIES:
    vector = encoder.encode(query)
    query_vectors.append(vector)


# ---------------------------------------------------------
# 2. Compare query embeddings with each other
# ---------------------------------------------------------

print()
print("=" * 70)
print("QUERY-TO-QUERY COSINE SIMILARITY")
print("=" * 70)

for i in range(len(QUERIES)):
    for j in range(i + 1, len(QUERIES)):

        similarity = cosine_similarity(
            query_vectors[i],
            query_vectors[j]
        )

        print(
            f"Q{i + 1} vs Q{j + 1}: "
            f"{similarity:.10f}"
        )


# ---------------------------------------------------------
# 3. Load the 15 stored document embeddings
# ---------------------------------------------------------

documents = []

for metadata_file in sorted(Path("metadata").glob("*.json")):

    with open(metadata_file, "r", encoding="utf-8") as file:
        metadata = json.load(file)

    documents.append(
        {
            "filename": metadata["filename"],
            "vector": np.asarray(
                metadata["embedding"],
                dtype=np.float32
            )
        }
    )


print()
print("=" * 70)
print("DOCUMENT EMBEDDING NORMS")
print("=" * 70)

for document in documents:

    norm = np.linalg.norm(document["vector"])

    print(
        f"{document['filename']} | "
        f"Norm: {norm:.10f}"
    )


# ---------------------------------------------------------
# 4. Measure document-to-document similarity distribution
# ---------------------------------------------------------

similarities = []

for i in range(len(documents)):
    for j in range(i + 1, len(documents)):

        similarity = cosine_similarity(
            documents[i]["vector"],
            documents[j]["vector"]
        )

        similarities.append(similarity)


print()
print("=" * 70)
print("DOCUMENT-TO-DOCUMENT COSINE SIMILARITY")
print("=" * 70)

print(f"Pairs measured: {len(similarities)}")
print(f"Minimum similarity: {min(similarities):.10f}")
print(f"Maximum similarity: {max(similarities):.10f}")
print(f"Mean similarity:    {np.mean(similarities):.10f}")


# ---------------------------------------------------------
# 5. Specifically inspect scanned_02 against every document
# ---------------------------------------------------------

target = None

for document in documents:
    if document["filename"] == "scanned_02_low_quality_scan.pdf":
        target = document
        break


if target is not None:

    comparisons = []

    for document in documents:

        if document["filename"] == target["filename"]:
            continue

        similarity = cosine_similarity(
            target["vector"],
            document["vector"]
        )

        comparisons.append(
            (document["filename"], similarity)
        )

    comparisons.sort(
        key=lambda item: item[1],
        reverse=True
    )

    print()
    print("=" * 70)
    print("SIMILARITY OF scanned_02 TO OTHER DOCUMENTS")
    print("=" * 70)

    for filename, similarity in comparisons:
        print(
            f"{filename} | "
            f"Similarity: {similarity:.10f}"
        )