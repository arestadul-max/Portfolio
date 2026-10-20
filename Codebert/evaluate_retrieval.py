import csv
import json
from pathlib import Path

from src.pipeline import DostabasePipeline


QUERIES = [
    {
        "id": "Q1",
        "query": "artificial intelligence history applications and methods",
        "relevant": "academic_01_ai_research.pdf",
    },
    {
        "id": "Q2",
        "query": "information retrieval indexing ranking and evaluation",
        "relevant": "academic_02_information_retrieval.pdf",
    },
    {
        "id": "Q3",
        "query": "natural language processing learning and search",
        "relevant": "academic_03_nlp_study.pdf",
    },
    {
        "id": "Q4",
        "query": "confidential information non-disclosure agreement",
        "relevant": "legal_03_non_disclosure_agreement.pdf",
    },
    {
        "id": "Q5",
        "query": "machine learning algorithms and learning theory",
        "relevant": "technical_01_machine_learning.pdf",
    },
    {
        "id": "Q6",
        "query": "software architecture components connectors and architectural styles",
        "relevant": "technical_02_software_architecture.pdf",
    },
    {
        "id": "Q7",
        "query": "database systems data modeling and SQL",
        "relevant": "technical_03_database_systems.pdf",
    },
    {
        "id": "Q8",
        "query": "patient case history document",
        "relevant": "scanned_01_printed_page.pdf",
    },
]


def main():
    output_dir = Path("evaluation")
    output_dir.mkdir(parents=True, exist_ok=True)

    pipeline = DostabasePipeline()

    evaluation_records = []

    print()
    print("DOSTABASE FINAL RETRIEVAL EVALUATION")
    print("=" * 78)
    print("Corpus size: 15 documents")
    print("Queries: 8")
    print("Embedding model: microsoft/codebert-base")
    print("Embedding dimension: 768")
    print("Vector index: HNSW")
    print()

    for item in QUERIES:
        results = pipeline.search(
            item["query"],
            k=15
        )

        ranked_filenames = [
            result["filename"]
            for result in results
        ]

        if item["relevant"] in ranked_filenames:
            target_rank = (
                ranked_filenames.index(
                    item["relevant"]
                ) + 1
            )
        else:
            target_rank = None

        precision_at_1 = (
            1.0
            if target_rank == 1
            else 0.0
        )

        relevant_in_top_5 = (
            1
            if (
                target_rank is not None
                and target_rank <= 5
            )
            else 0
        )

        precision_at_5 = (
            relevant_in_top_5 / 5.0
        )

        recall_at_5 = float(
            relevant_in_top_5
        )

        reciprocal_rank = (
            1.0 / target_rank
            if target_rank is not None
            else 0.0
        )

        top_5 = []

        for rank, result in enumerate(
            results[:5],
            start=1
        ):
            top_5.append(
                {
                    "rank": rank,
                    "filename": result["filename"],
                    "distance": result["distance"],
                    "similarity": result["similarity"],
                }
            )

        record = {
            "query_id": item["id"],
            "query": item["query"],
            "relevant_document": item["relevant"],
            "target_rank": target_rank,
            "precision_at_1": precision_at_1,
            "precision_at_5": precision_at_5,
            "recall_at_5": recall_at_5,
            "reciprocal_rank": reciprocal_rank,
            "top_5": top_5,
        }

        evaluation_records.append(record)

        print(
            f"{item['id']}: {item['query']}"
        )
        print(
            f"Relevant document: {item['relevant']}"
        )

        for result in top_5:
            marker = (
                " <-- RELEVANT"
                if result["filename"]
                == item["relevant"]
                else ""
            )

            print(
                f"  {result['rank']}. "
                f"{result['filename']}  "
                f"distance={result['distance']:.6f}  "
                f"similarity={result['similarity']:.6f}"
                f"{marker}"
            )

        print(
            "Target rank:",
            target_rank
        )
        print(
            "P@1={:.3f}  P@5={:.3f}  "
            "Recall@5={:.3f}  RR={:.3f}".format(
                precision_at_1,
                precision_at_5,
                recall_at_5,
                reciprocal_rank,
            )
        )
        print("-" * 78)

    mean_p1 = sum(
        record["precision_at_1"]
        for record in evaluation_records
    ) / len(evaluation_records)

    mean_p5 = sum(
        record["precision_at_5"]
        for record in evaluation_records
    ) / len(evaluation_records)

    mean_recall5 = sum(
        record["recall_at_5"]
        for record in evaluation_records
    ) / len(evaluation_records)

    mrr = sum(
        record["reciprocal_rank"]
        for record in evaluation_records
    ) / len(evaluation_records)

    summary = {
        "corpus_size": 15,
        "query_count": len(QUERIES),
        "precision_at_1": mean_p1,
        "precision_at_5": mean_p5,
        "recall_at_5": mean_recall5,
        "mrr": mrr,
    }

    output = {
        "evaluation_method": (
            "known-item retrieval with one "
            "ground-truth relevant document per query"
        ),
        "queries": evaluation_records,
        "summary": summary,
    }

    json_path = (
        output_dir
        / "final_retrieval_evaluation.json"
    )

    with open(
        json_path,
        "w",
        encoding="utf-8"
    ) as file:
        json.dump(
            output,
            file,
            indent=4
        )

    csv_path = (
        output_dir
        / "final_retrieval_metrics.csv"
    )

    with open(
        csv_path,
        "w",
        newline="",
        encoding="utf-8"
    ) as file:
        writer = csv.writer(file)

        writer.writerow(
            [
                "Query",
                "Relevant Document",
                "Target Rank",
                "P@1",
                "P@5",
                "Recall@5",
                "Reciprocal Rank",
            ]
        )

        for record in evaluation_records:
            writer.writerow(
                [
                    record["query_id"],
                    record["relevant_document"],
                    record["target_rank"],
                    record["precision_at_1"],
                    record["precision_at_5"],
                    record["recall_at_5"],
                    record["reciprocal_rank"],
                ]
            )

    print()
    print("=" * 78)
    print("FINAL AGGREGATE RESULTS")
    print("=" * 78)
    print(f"Precision@1 : {mean_p1:.4f}")
    print(f"Precision@5 : {mean_p5:.4f}")
    print(f"Recall@5    : {mean_recall5:.4f}")
    print(f"MRR         : {mrr:.4f}")
    print()
    print(
        "Detailed log:",
        json_path
    )
    print(
        "Metrics table:",
        csv_path
    )


if __name__ == "__main__":
    main()
