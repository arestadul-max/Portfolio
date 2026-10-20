import argparse
from pathlib import Path

from src.document import Document
from src.codebert_encoder import CodeBERTEncoder
from src.metadata import MetadataStore
from src.hnsw_index import HNSWIndex
from src.search_engine import SearchEngine

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
        Ingest one document through the verified document
        extraction, CodeBERT encoding, metadata persistence,
        and HNSW indexing pipeline.
        """

        pipeline = DocumentPipeline(
            metadata_dir=self.metadata_dir,
            index_path=self.index_path,
            mapping_path=self.mapping_path
        )

        return pipeline.process(
            file_path
        )

    def search(self, query, k=5):
        """
        Perform semantic search using CodeBERT query encoding
        and the persisted HNSW index.
        """

        engine = SearchEngine(
            index_path=self.index_path,
            mapping_path=self.mapping_path
        )

        return engine.search(
            query,
            k=k
        )
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
                        "   Distance: "
                        f"{result['distance']}"
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