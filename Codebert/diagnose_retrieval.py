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


def cosine_distance(a, b):
    a = np.asarray(a, dtype=np.float32)
    b = np.asarray(b, dtype=np.float32)

    denominator = np.linalg.norm(a) * np.linalg.norm(b)

    if denominator == 0:
        return 1.0

    similarity = np.dot(a, b) / denominator
    return float(1.0 - similarity)


metadata_records = []

for metadata_file in sorted(Path("metadata").glob("*.json")):
    with open(metadata_file, "r", encoding="utf-8") as file:
        metadata = json.load(file)

    metadata_records.append(
        {
            "filename": metadata["filename"],
            "embedding": np.asarray(
                metadata["embedding"],
                dtype=np.float32
            ),
        }
    )


print("Metadata documents loaded:", len(metadata_records))

encoder = CodeBERTEncoder()

for query_number, query in enumerate(QUERIES, start=1):

    query_vector = encoder.encode(query)

    results = []

    for record in metadata_records:
        distance = cosine_distance(
            query_vector,
            record["embedding"]
        )

        results.append(
            (record["filename"], distance)
        )

    results.sort(key=lambda item: item[1])

    print()
    print("=" * 70)
    print(f"QUERY {query_number}: {query}")
    print("=" * 70)

    for rank, (filename, distance) in enumerate(results[:5], start=1):
        print(
            f"{rank}. {filename} | "
            f"Exact cosine distance: {distance:.10f}"
        )