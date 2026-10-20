import torch
from transformers import RobertaTokenizer, RobertaModel


class CodeBERTEncoder:
    """
    CodeBERT-based document representation module.

    Generates a 768-dimensional embedding
    from the final CLS hidden state.
    """

    def __init__(self):

        self.model_name = "microsoft/codebert-base"

        self.tokenizer = RobertaTokenizer.from_pretrained(
            self.model_name
        )

        self.model = RobertaModel.from_pretrained(
            self.model_name
        )

        self.model.eval()


    def encode(self, text):

        inputs = self.tokenizer(
            text,
            return_tensors="pt",
            truncation=True,
            padding=True,
            max_length=512
        )

        with torch.no_grad():

            outputs = self.model(
                **inputs
            )

        embedding = outputs.last_hidden_state[:, 0, :]

        return embedding.squeeze().numpy()