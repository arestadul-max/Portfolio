import json
from pathlib import Path


class MetadataStore:
    """
    Stores document representation metadata produced by the
    implemented Dostabase pipeline.
    """

    def __init__(self, output_dir="metadata"):
        self.output_dir = Path(output_dir)
        self.output_dir.mkdir(
            parents=True,
            exist_ok=True
        )

    def save(
        self,
        path,
        filename,
        document_format,
        text,
        embedding,
        model_name
    ):
        metadata = {
            "path": str(path),
            "filename": filename,
            "format": document_format,
            "model_name": model_name,
            "text_length": len(text),
            "embedding_dimension": len(embedding),
            "indexed": False,
            "hnsw_label": None,
            "embedding": embedding.tolist()
        }

        output_file = (
            self.output_dir /
            f"{Path(filename).stem}.json"
        )

        with open(
            output_file,
            "w",
            encoding="utf-8"
        ) as file:
            json.dump(
                metadata,
                file,
                indent=4
            )

        return output_file

    def mark_indexed(
        self,
        metadata_file,
        hnsw_label
    ):
        metadata_file = Path(metadata_file)

        with open(
            metadata_file,
            "r",
            encoding="utf-8"
        ) as file:
            metadata = json.load(file)

        metadata["indexed"] = True
        metadata["hnsw_label"] = int(hnsw_label)

        with open(
            metadata_file,
            "w",
            encoding="utf-8"
        ) as file:
            json.dump(
                metadata,
                file,
                indent=4
            )

        return metadata_file