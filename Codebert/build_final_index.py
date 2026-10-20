from src.hnsw_index import HNSWIndex


index = HNSWIndex()

index.build(
    metadata_dir="metadata"
)

index.save(
    index_path="storage/dostabase_hnsw.bin",
    mapping_path="storage/dostabase_hnsw_mapping.json"
)

print("Final HNSW index built successfully")
print(
    "Documents indexed:",
    index.index.get_current_count()
)

print(
    "Index file:",
    "storage/dostabase_hnsw.bin"
)

print(
    "Mapping file:",
    "storage/dostabase_hnsw_mapping.json"
)