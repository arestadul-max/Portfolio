import json
from pathlib import Path


METADATA_DIR = Path("metadata")
DATA_DIR = Path("data")
MAPPING_FILE = Path(
    "storage/dostabase_hnsw_mapping.json"
)

MODEL_NAME = "microsoft/codebert-base"


def load_json(path):
    with open(
        path,
        "r",
        encoding="utf-8"
    ) as file:
        return json.load(file)


def find_corpus_file(filename):
    matches = [
        path
        for path in DATA_DIR.rglob("*")
        if path.is_file()
        and path.name == filename
    ]

    if len(matches) != 1:
        raise ValueError(
            f"Expected exactly one corpus file for "
            f"{filename}, found {len(matches)}."
        )

    return matches[0]


def main():
    # -------------------------------------------------
    # Load persisted HNSW mapping
    # -------------------------------------------------

    if not MAPPING_FILE.exists():
        raise FileNotFoundError(
            f"Mapping file not found: {MAPPING_FILE}"
        )

    mapping = load_json(
        MAPPING_FILE
    )

    metadata_files = sorted(
        METADATA_DIR.glob("*.json")
    )

    corpus_files = [
        path
        for path in DATA_DIR.rglob("*")
        if path.is_file()
    ]

    # -------------------------------------------------
    # Global validation before changing any file
    # -------------------------------------------------

    if len(mapping) != 15:
        raise ValueError(
            f"Expected 15 mapping entries, "
            f"found {len(mapping)}."
        )

    if len(metadata_files) != 15:
        raise ValueError(
            f"Expected 15 metadata files, "
            f"found {len(metadata_files)}."
        )

    if len(corpus_files) != 15:
        raise ValueError(
            f"Expected 15 corpus files, "
            f"found {len(corpus_files)}."
        )

    labels = sorted(
        int(item["label"])
        for item in mapping
    )

    if labels != list(range(15)):
        raise ValueError(
            f"Unexpected HNSW labels: {labels}"
        )

    # -------------------------------------------------
    # Validate every document first.
    #
    # Nothing is written during this phase.
    # -------------------------------------------------

    migration_records = []

    for item in mapping:
        filename = item["filename"]
        hnsw_label = int(
            item["label"]
        )

        metadata_file = (
            METADATA_DIR /
            f"{Path(filename).stem}.json"
        )

        if not metadata_file.exists():
            raise FileNotFoundError(
                f"Metadata file not found: "
                f"{metadata_file}"
            )

        metadata = load_json(
            metadata_file
        )

        if metadata.get("filename") != filename:
            raise ValueError(
                f"Filename mismatch in "
                f"{metadata_file}"
            )

        embedding = metadata.get(
            "embedding"
        )

        if not isinstance(
            embedding,
            list
        ):
            raise ValueError(
                f"Embedding missing or invalid in "
                f"{metadata_file}"
            )

        if len(embedding) != 768:
            raise ValueError(
                f"Expected 768 embedding values in "
                f"{metadata_file}, found "
                f"{len(embedding)}."
            )

        if (
            metadata.get(
                "embedding_dimension"
            )
            != 768
        ):
            raise ValueError(
                f"Embedding dimension mismatch in "
                f"{metadata_file}"
            )

        corpus_file = find_corpus_file(
            filename
        )

        migration_records.append(
            {
                "metadata_file": metadata_file,
                "metadata": metadata,
                "corpus_file": corpus_file,
                "hnsw_label": hnsw_label
            }
        )

    print(
        "Pre-migration validation successful."
    )
    print(
        "Validated documents:",
        len(migration_records)
    )

    # -------------------------------------------------
    # Migration
    #
    # Existing experimental values are preserved.
    # Only verified factual fields are added.
    # -------------------------------------------------

    for record in migration_records:
        old = record["metadata"]

        migrated = {
            "path": str(
                record["corpus_file"]
            ),
            "filename": old["filename"],
            "format": old["format"],
            "model_name": MODEL_NAME,
            "text_length": old[
                "text_length"
            ],
            "embedding_dimension": old[
                "embedding_dimension"
            ],
            "indexed": True,
            "hnsw_label": record[
                "hnsw_label"
            ],
            "embedding": old[
                "embedding"
            ]
        }

        output_file = record[
            "metadata_file"
        ]

        temporary_file = (
            output_file.with_suffix(
                ".json.tmp"
            )
        )

        with open(
            temporary_file,
            "w",
            encoding="utf-8"
        ) as file:
            json.dump(
                migrated,
                file,
                indent=4
            )

        temporary_file.replace(
            output_file
        )

        print(
            "Migrated:",
            old["filename"],
            "| HNSW label:",
            record["hnsw_label"]
        )

    print("")
    print(
        "Metadata migration completed successfully."
    )


if __name__ == "__main__":
    main()