from fastapi import FastAPI
from pydantic import BaseModel
import random

app = FastAPI()

class EmbeddingRequest(BaseModel):
    imageBase64: str

@app.post("/embedding")
def generate_embedding(req: EmbeddingRequest):
    # return fake 512-D vector for testing
    embedding = [random.random() for _ in range(512)]
    return {"embedding": embedding}
