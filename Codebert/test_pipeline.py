from src.pipeline import DocumentPipeline


pipeline = DocumentPipeline()


result = pipeline.process(
    "data/academic/academic_01_ai_research.pdf"
)


print("Pipeline completed")
print(result)