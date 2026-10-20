import argparse

from src.pipeline import DocumentPipeline
from src.search_engine import SearchEngine


def build_parser():
    parser = argparse.ArgumentParser(
        prog="dostabase",
        description=(
            "Dostabase: An AI-Driven Automated Document "
            "Management System"
        ),
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

    return parser


def run_cli():
    parser = build_parser()
    args = parser.parse_args()

    if args.command is None:
        parser.print_help()
        return

    if args.command == "ingest":

        try:

            print("Dostabase document ingestion")

            pipeline = DocumentPipeline()

            result = pipeline.process(
                args.path
            )

            print(
                f"File: {result['file']}"
            )

            print(
                f"Format: {result['format']}"
            )

            print(
                f"Extracted characters: "
                f"{result['text_length']}"
            )

            print(
                f"Embedding dimension: "
                f"{result['embedding_dimension']}"
            )

            print(
                f"Metadata file: "
                f"{result['metadata_file']}"
            )

            print(
                f"HNSW label: "
                f"{result['hnsw_label']}"
            )

            print(
                f"Indexing mode: "
                f"{result['indexing_mode']}"
            )

            print(
                f"Documents in index: "
                f"{result['indexed_documents']}"
            )

            print("Status: successful")


        except (
            FileNotFoundError,
            ValueError,
            Exception,
        ) as error:

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


            engine = SearchEngine()


            results = engine.search(
                args.query
            )


            print(
                "Dostabase semantic search"
            )

            print(
                f"Query: {args.query}"
            )

            print(
                ""
            )


            for index, result in enumerate(
                results,
                start=1
            ):

                print(
                    f"{index}. "
                    f"{result['filename']}"
                )

                print(
                    f"   Distance: "
                    f"{result['distance']}"
                )


        except Exception as error:

            print(
                f"Search error: {error}"
            )
    elif args.command == "status":
        print("Dostabase CLI is operational.")
        print(
            "Implementation stage: "
            "Stage 1 - native PDF extraction"
        )
if __name__ == "__main__":
    run_cli()