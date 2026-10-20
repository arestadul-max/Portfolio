import argparse
import json
from pathlib import Path

from src.document import Document
from src.codebert_encoder import CodeBERTEncoder
from src.metadata import MetadataStore
from src.hnsw_index import HNSWIndex

class DocumentPipeline:
    """
    Document ingestion and indexing pipeline.

    Flow:
    Document extraction
        ->
    CodeBERT embedding
        ->
    Metadata storage
        ->
    Incremental HNSW indexing
        ->
    Persistent index storage
        ->
    Metadata indexing-state update
    """

    def __init__(
        self,
        metadata_dir="metadata",
        index_path="storage/dostabase_hnsw.bin",
        mapping_path="storage/dostabase_hnsw_mapping.json"
    ):
        self.metadata_dir = Path(metadata_dir)
        self.index_path = Path(index_path)
        self.mapping_path = Path(mapping_path)

        self.encoder = CodeBERTEncoder()

        self.metadata_store = MetadataStore(
            output_dir=self.metadata_dir
        )

    def process(self, file_path):
        document = Document(file_path)
        document.detect_format()

        # -------------------------------------------------
        # Check the persisted index before processing
        # -------------------------------------------------

        index_exists = (
            self.index_path.exists()
            and self.mapping_path.exists()
        )

        hnsw_index = HNSWIndex()

        if index_exists:
            hnsw_index.load(
                index_path=self.index_path,
                mapping_path=self.mapping_path
            )

            for indexed_document in hnsw_index.documents:
                if (
                    indexed_document["filename"]
                    == document.path.name
                ):
                    raise ValueError(
                        "Document already indexed: "
                        f"{document.path.name}"
                    )

        # -------------------------------------------------
        # Native PDF extraction
        # -------------------------------------------------

        if document.format == "pdf":
            document.extract_pdf_text()

            if document.requires_ocr:
                document.extract_scanned_pdf_ocr_text()

        elif document.format == "image":
            document.extract_ocr_text()

        elif document.format == "text":
            document.extract_plain_text()
        text = document.text

        if not text.strip():
            raise ValueError(
                "No text extracted from document."
            )

        # -------------------------------------------------
        # CodeBERT document representation
        # -------------------------------------------------

        embedding = self.encoder.encode(
            text
        )

        # -------------------------------------------------
        # Initial metadata persistence
        #
        # At this point HNSW insertion has not yet completed,
        # so indexed=False and hnsw_label=None are stored.
        # -------------------------------------------------

        metadata_file = self.metadata_store.save(
            path=document.path,
            filename=document.path.name,
            document_format=document.format,
            text=text,
            embedding=embedding,
            model_name=self.encoder.model_name
        )

        # -------------------------------------------------
        # HNSW indexing
        # -------------------------------------------------

        if index_exists:
            hnsw_label = hnsw_index.add_document(
                filename=document.path.name,
                embedding=embedding
            )

            indexing_mode = "incremental"

        else:
            hnsw_index.build(
                metadata_dir=self.metadata_dir
            )

            matching_document = next(
                item
                for item in hnsw_index.documents
                if item["filename"] == document.path.name
            )

            hnsw_label = int(
                matching_document["label"]
            )

            indexing_mode = "initial"

        # -------------------------------------------------
        # Persist updated HNSW graph and mapping
        # -------------------------------------------------

        hnsw_index.save(
            index_path=self.index_path,
            mapping_path=self.mapping_path
        )

        # -------------------------------------------------
        # Mark metadata as successfully indexed
        #
        # This is performed only after HNSW persistence
        # succeeds.
        # -------------------------------------------------

        self.metadata_store.mark_indexed(
            metadata_file=metadata_file,
            hnsw_label=hnsw_label
        )

        return {
            "file": document.path.name,
            "format": document.format,
            "text_length": len(text),
            "embedding_dimension": len(embedding),
            "metadata_file": str(metadata_file),
            "hnsw_label": hnsw_label,
            "indexing_mode": indexing_mode,
            "indexed_documents": (
                hnsw_index.index.get_current_count()
            )
        }
class DostabasePipeline:
    """
    Top-level Dostabase pipeline orchestrator.

    Provides the ingestion and semantic-search operations
    described by the system methodology while retaining the
    verified lower-level ingestion and retrieval components.
    """

    def __init__(
        self,
        metadata_dir="metadata",
        index_path="storage/dostabase_hnsw.bin",
        mapping_path="storage/dostabase_hnsw_mapping.json"
    ):
        self.metadata_dir = metadata_dir
        self.index_path = index_path
        self.mapping_path = mapping_path

    def ingest(self, file_path):
        """
        Ingest one document through extraction, CodeBERT
        encoding, metadata persistence, and HNSW indexing.
        """

        document = Document(file_path)
        document.detect_format()

        index_exists = (
            Path(self.index_path).exists()
            and Path(self.mapping_path).exists()
        )

        hnsw_index = HNSWIndex()

        if index_exists:
            hnsw_index.load(
                index_path=self.index_path,
                mapping_path=self.mapping_path
            )

            for indexed_document in hnsw_index.documents:
                if (
                    indexed_document["filename"]
                    == document.path.name
                ):
                    raise ValueError(
                        "Document already indexed: "
                        f"{document.path.name}"
                    )

        if document.format == "pdf":
            document.extract_pdf_text()

            if document.requires_ocr:
                document.extract_scanned_pdf_ocr_text()

        elif document.format == "image":
            document.extract_ocr_text()

        elif document.format == "text":
            document.extract_plain_text()

        text = document.text

        if not text.strip():
            raise ValueError(
                "No text extracted from document."
            )

        encoder = CodeBERTEncoder()

        embedding = encoder.encode(
            text
        )

        metadata_store = MetadataStore(
            output_dir=self.metadata_dir
        )

        metadata_file = metadata_store.save(
            path=document.path,
            filename=document.path.name,
            document_format=document.format,
            text=text,
            embedding=embedding,
            model_name=encoder.model_name
        )

        if index_exists:
            hnsw_label = hnsw_index.add_document(
                filename=document.path.name,
                embedding=embedding
            )

            indexing_mode = "incremental"

        else:
            hnsw_index.build(
                metadata_dir=self.metadata_dir
            )

            matching_document = next(
                item
                for item in hnsw_index.documents
                if (
                    item["filename"]
                    == document.path.name
                )
            )

            hnsw_label = int(
                matching_document["label"]
            )

            indexing_mode = "initial"

        hnsw_index.save(
            index_path=self.index_path,
            mapping_path=self.mapping_path
        )

        metadata_store.mark_indexed(
            metadata_file=metadata_file,
            hnsw_label=hnsw_label
        )

        return {
            "file": document.path.name,
            "format": document.format,
            "text_length": len(text),
            "embedding_dimension": len(embedding),
            "metadata_file": str(metadata_file),
            "hnsw_label": hnsw_label,
            "indexing_mode": indexing_mode,
            "indexed_documents": (
                hnsw_index.index.get_current_count()
            )
        }

        return pipeline.process(
            file_path
        )

    def search(self, query, k=5):
        """
        Perform semantic search by encoding the natural-language
        query with CodeBERT, searching the persisted HNSW index,
        and resolving ranked results against stored metadata.
        """

        if not query.strip():
            raise ValueError(
                "Search query cannot be empty."
            )

        encoder = CodeBERTEncoder()

        query_vector = encoder.encode(
            query
        )

        hnsw_index = HNSWIndex()

        hnsw_index.load(
            index_path=self.index_path,
            mapping_path=self.mapping_path
        )

        hnsw_results = hnsw_index.search(
            query_vector,
            k=k
        )

        metadata_by_filename = {}

        for metadata_file in Path(
            self.metadata_dir
        ).glob("*.json"):

            with open(
                metadata_file,
                "r",
                encoding="utf-8"
            ) as file:
                metadata = json.load(file)

            metadata_by_filename[
                metadata["filename"]
            ] = metadata

        results = []

        for result in hnsw_results:

            filename = result["filename"]
            distance = float(
                result["distance"]
            )

            if filename not in metadata_by_filename:
                raise ValueError(
                    "Metadata not found for indexed document: "
                    f"{filename}"
                )

            metadata = metadata_by_filename[
                filename
            ]

            results.append(
                {
                    "filename": filename,
                    "path": metadata["path"],
                    "format": metadata["format"],
                    "model_name": metadata["model_name"],
                    "text_length": metadata["text_length"],
                    "embedding_dimension": (
                        metadata["embedding_dimension"]
                    ),
                    "indexed": metadata["indexed"],
                    "hnsw_label": metadata["hnsw_label"],
                    "distance": distance,
                    "similarity": 1.0 - distance
                }
            )

        return results

        return results
    def run_cli(self):
        """
        Run the command-line interface for document ingestion,
        semantic search, and local pipeline status.
        """

        parser = argparse.ArgumentParser(
            prog="dostabase",
            description=(
                "Dostabase: An AI-Driven Automated Document "
                "Management System"
            )
        )

        subparsers = parser.add_subparsers(
            dest="command",
            title="commands"
        )

        ingest_parser = subparsers.add_parser(
            "ingest",
            help="Ingest and process a document"
        )

        ingest_parser.add_argument(
            "path",
            help="Path to the document to ingest"
        )

        search_parser = subparsers.add_parser(
            "search",
            help="Perform semantic document search"
        )

        search_parser.add_argument(
            "query",
            nargs="?",
            help="Natural-language search query"
        )

        subparsers.add_parser(
            "status",
            help="Display local Dostabase status"
        )

        args = parser.parse_args()

        if args.command is None:
            parser.print_help()
            return

        if args.command == "ingest":
            try:
                print(
                    "Dostabase document ingestion"
                )

                result = self.ingest(
                    args.path
                )

                print(
                    f"File: {result['file']}"
                )
                print(
                    f"Format: {result['format']}"
                )
                print(
                    "Extracted characters: "
                    f"{result['text_length']}"
                )
                print(
                    "Embedding dimension: "
                    f"{result['embedding_dimension']}"
                )
                print(
                    "Metadata file: "
                    f"{result['metadata_file']}"
                )
                print(
                    "HNSW label: "
                    f"{result['hnsw_label']}"
                )
                print(
                    "Indexing mode: "
                    f"{result['indexing_mode']}"
                )
                print(
                    "Documents in index: "
                    f"{result['indexed_documents']}"
                )
                print(
                    "Status: successful"
                )

            except Exception as error:
                print(
                    f"Error: {error}"
                )

        elif args.command == "search":
            try:
                if not args.query:
                    print(
                        "Please provide a search query."
                    )
                    return

                results = self.search(
                    args.query
                )

                print(
                    "Dostabase semantic search"
                )
                print(
                    f"Query: {args.query}"
                )
                print("")

                for index, result in enumerate(
                    results,
                    start=1
                ):
                    print(
                        f"{index}. "
                        f"{result['filename']}"
                    )

                    print(
                        "   Path: "
                        f"{result['path']}"
                    )

                    print(
                        "   Format: "
                        f"{result['format']}"
                    )

                    print(
                        "   HNSW label: "
                        f"{result['hnsw_label']}"
                    )

                    print(
                        "   Cosine distance: "
                        f"{result['distance']:.6f}"
                    )

                    print(
                        "   Cosine similarity: "
                        f"{result['similarity']:.6f}"
                    )

            except Exception as error:
                print(
                    f"Search error: {error}"
                )

        elif args.command == "status":
            print(
                "Dostabase CLI is operational."
            )
            print(
                "Pipeline: document ingestion and "
                "semantic search available."
            )