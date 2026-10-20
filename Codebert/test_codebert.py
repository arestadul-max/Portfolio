from src.codebert_encoder import CodeBERTEncoder


encoder = CodeBERTEncoder()

sample_text = """
Artificial intelligence is a branch of computer science
that develops intelligent systems.
"""

vector = encoder.encode(sample_text)

print("CodeBERT embedding generated")
print("Vector dimension:", len(vector))
print("First 10 values:")
print(vector[:10])