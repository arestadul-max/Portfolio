import json
from pathlib import Path

import hnswlib
import numpy as np


class HNSWIndex:

    def __init__(self, dimension=768, space="cosine"):
        self.dimension = dimension
        self.space = space

        self.index = hnswlib.Index(
            space=self.space,
            dim=self.dimension
        )

        self.documents = []


    def build(self, metadata_dir="metadata"):

        metadata_files = sorted(
            Path(metadata_dir).glob("*.json")
        )

        if not metadata_files:
            raise ValueError("No metadata files found.")

        vectors = []
        labels = []

        # Important for reproducible rebuilding
        self.documents = []

        for label, metadata_file in enumerate(metadata_files):

            with open(
                metadata_file,
                "r",
                encoding="utf-8"
            ) as file:

                metadata = json.load(file)

            vector = np.asarray(
                metadata["embedding"],
                dtype=np.float32
            )

            if len(vector) != self.dimension:
                raise ValueError(
                    f"Invalid embedding dimension in "
                    f"{metadata_file.name}: "
                    f"{len(vector)}"
                )

            vectors.append(vector)
            labels.append(label)

            self.documents.append(
                {
                    "label": label,
                    "filename": metadata["filename"]
                }
            )

        vectors = np.asarray(
            vectors,
            dtype=np.float32
        )

        labels = np.asarray(
            labels,
            dtype=np.int64
        )

        self.index.init_index(
            max_elements=len(vectors),
            ef_construction=200,
            M=16
        )

        self.index.add_items(
            vectors,
            labels
        )

        self.index.set_ef(50)


    def save(
        self,
        index_path="storage/dostabase_hnsw.bin",
        mapping_path="storage/dostabase_hnsw_mapping.json"
    ):

        index_path = Path(index_path)
        mapping_path = Path(mapping_path)

        index_path.parent.mkdir(
            parents=True,
            exist_ok=True
        )

        mapping_path.parent.mkdir(
            parents=True,
            exist_ok=True
        )

        self.index.save_index(
            str(index_path)
        )

        with open(
            mapping_path,
            "w",
            encoding="utf-8"
        ) as file:

            json.dump(
                self.documents,
                file,
                indent=4
            )


    def load(
        self,
        index_path="storage/dostabase_hnsw.bin",
        mapping_path="storage/dostabase_hnsw_mapping.json"
    ):

        index_path = Path(index_path)
        mapping_path = Path(mapping_path)

        if not index_path.exists():
            raise FileNotFoundError(
                f"HNSW index not found: {index_path}"
            )

        if not mapping_path.exists():
            raise FileNotFoundError(
                f"HNSW mapping not found: {mapping_path}"
            )

        self.index.load_index(
            str(index_path)
        )

        self.index.set_ef(50)

        with open(
            mapping_path,
            "r",
            encoding="utf-8"
        ) as file:

            self.documents = json.load(file)
    def add_document(self, filename, embedding):

        vector = np.asarray(
            embedding,
            dtype=np.float32
        )

        if len(vector) != self.dimension:
            raise ValueError(
                f"Invalid embedding dimension: "
                f"{len(vector)}"
            )

        # Prevent the same document from being inserted twice.
        for document in self.documents:
            if document["filename"] == filename:
                raise ValueError(
                    f"Document already indexed: {filename}"
                )

        # Assign the next available integer label.
        if self.documents:
            new_label = max(
                int(document["label"])
                for document in self.documents
            ) + 1
        else:
            new_label = 0

        current_count = self.index.get_current_count()
        current_capacity = self.index.get_max_elements()

        # Increase capacity only when the existing index is full.
        if current_count >= current_capacity:

            new_capacity = max(
                current_capacity * 2,
                current_count + 1
            )

            self.index.resize_index(
                new_capacity
            )

        self.index.add_items(
            np.asarray(
                [vector],
                dtype=np.float32
            ),
            np.asarray(
                [new_label],
                dtype=np.int64
            )
        )

        self.documents.append(
            {
                "label": new_label,
                "filename": filename
            }
        )

        return new_label
    def search(self, query_vector, k=5):

        total_items = self.index.get_current_count()

        if total_items == 0:
            raise ValueError("HNSW index is empty.")

        k = min(k, total_items)

        labels, distances = self.index.knn_query(
            np.asarray(
                [query_vector],
                dtype=np.float32
            ),
            k=k
        )

        results = []

        document_map = {
            int(document["label"]): document["filename"]
            for document in self.documents
        }

        for label, distance in zip(
            labels[0],
            distances[0]
        ):

            label = int(label)

            results.append(
                {
                    "filename": document_map[label],
                    "distance": float(distance)
                }
            )

        return results