from src.codebert_encoder import CodeBERTEncoder
from src.hnsw_index import HNSWIndex


class SearchEngine:

    def __init__(
        self,
        index_path="storage/dostabase_hnsw.bin",
        mapping_path="storage/dostabase_hnsw_mapping.json"
    ):
        self.encoder = CodeBERTEncoder()

        self.index = HNSWIndex()

        self.index.load(
            index_path=index_path,
            mapping_path=mapping_path
        )


    def search(self, query, k=5):

        if not query.strip():
            raise ValueError("Search query cannot be empty.")

        query_vector = self.encoder.encode(query)

        results = self.index.search(
            query_vector,
            k=k
        )

        return results