import json
import shutil
from pathlib import Path

import numpy as np

from src.hnsw_index import HNSWIndex


SOURCE_METADATA = Path("metadata")
TEMP_METADATA = Path("storage/incremental_test_metadata")

TEST_INDEX = "storage/incremental_test_hnsw.bin"
TEST_MAPPING = "storage/incremental_test_hnsw_mapping.json"

HELD_OUT_FILE = "technical_03_database_systems.json"


# ---------------------------------------------------------
# 1. Prepare temporary metadata containing only 14 documents
# ---------------------------------------------------------

if TEMP_METADATA.exists():
    shutil.rmtree(TEMP_METADATA)

TEMP_METADATA.mkdir(
    parents=True,
    exist_ok=True
)

metadata_files = sorted(
    SOURCE_METADATA.glob("*.json")
)

if len(metadata_files) != 15:
    raise ValueError(
        f"Expected 15 metadata files, found {len(metadata_files)}"
    )


for metadata_file in metadata_files:

    if metadata_file.name != HELD_OUT_FILE:

        shutil.copy2(
            metadata_file,
            TEMP_METADATA / metadata_file.name
        )


temporary_files = sorted(
    TEMP_METADATA.glob("*.json")
)

print("Initial metadata documents:", len(temporary_files))


# ---------------------------------------------------------
# 2. Build the initial HNSW graph from 14 documents
# ---------------------------------------------------------

initial_index = HNSWIndex()

initial_index.build(
    metadata_dir=TEMP_METADATA
)

initial_index.save(
    index_path=TEST_INDEX,
    mapping_path=TEST_MAPPING
)

print(
    "Initial HNSW count:",
    initial_index.index.get_current_count()
)


# ---------------------------------------------------------
# 3. Reload the persisted 14-document index
# ---------------------------------------------------------

reloaded_index = HNSWIndex()

reloaded_index.load(
    index_path=TEST_INDEX,
    mapping_path=TEST_MAPPING
)

print(
    "Count after reload:",
    reloaded_index.index.get_current_count()
)


# ---------------------------------------------------------
# 4. Load ONLY the held-out 15th document embedding
# ---------------------------------------------------------

held_out_path = (
    SOURCE_METADATA / HELD_OUT_FILE
)

with open(
    held_out_path,
    "r",
    encoding="utf-8"
) as file:

    held_out_metadata = json.load(file)


held_out_embedding = np.asarray(
    held_out_metadata["embedding"],
    dtype=np.float32
)


# ---------------------------------------------------------
# 5. Incrementally insert the 15th vector
# ---------------------------------------------------------

new_label = reloaded_index.add_document(
    filename=held_out_metadata["filename"],
    embedding=held_out_embedding
)

print(
    "Incrementally added:",
    held_out_metadata["filename"]
)

print(
    "Assigned HNSW label:",
    new_label
)

print(
    "Count after incremental insertion:",
    reloaded_index.index.get_current_count()
)


# ---------------------------------------------------------
# 6. Persist the updated graph
# ---------------------------------------------------------

reloaded_index.save(
    index_path=TEST_INDEX,
    mapping_path=TEST_MAPPING
)


# ---------------------------------------------------------
# 7. Reload again to verify persistence
# ---------------------------------------------------------

verification_index = HNSWIndex()

verification_index.load(
    index_path=TEST_INDEX,
    mapping_path=TEST_MAPPING
)

print(
    "Final count after save and reload:",
    verification_index.index.get_current_count()
)

print(
    "Final mapping entries:",
    len(verification_index.documents)
)


last_document = max(
    verification_index.documents,
    key=lambda document: int(document["label"])
)

print(
    "Last indexed document:",
    last_document["filename"]
)

print("Incremental HNSW test completed successfully")