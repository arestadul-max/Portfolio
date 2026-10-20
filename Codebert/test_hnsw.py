from src.hnsw_index import HNSWIndex


index = HNSWIndex()

index.build()

index.save()

print("HNSW index created successfully")