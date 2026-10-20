from src.search_engine import SearchEngine


engine = SearchEngine()

engine.build()


results = engine.search(
    "artificial intelligence research"
)


print("Search completed")

for item in results:
    print(item)