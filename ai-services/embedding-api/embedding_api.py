from fastapi import FastAPI
from pydantic import BaseModel
import numpy as np
import base64
import cv2
from insightface.app import FaceAnalysis
from io import BytesIO

app = FastAPI()

# Initialize InsightFace model
app_model = FaceAnalysis(name='buffalo_l')
app_model.prepare(ctx_id=-1, det_size=(640, 640))  # CPU

class EmbeddingRequest(BaseModel):
    imageBase64: str

@app.post("/embedding")
def generate_embedding(req: EmbeddingRequest):
    # Remove prefix if present
    image_base64 = req.imageBase64
    if "," in image_base64:
        image_base64 = image_base64.split(",")[1]

    image_data = base64.b64decode(image_base64)
    np_arr = np.frombuffer(image_data, np.uint8)
    img = cv2.imdecode(np_arr, cv2.IMREAD_COLOR)

    if img is None:
        return {"error": "Invalid image data"}

    faces = app_model.get(img)
    if len(faces) == 0:
        return {"error": "No face detected"}

    embedding = faces[0].embedding
    return {"embedding": embedding.tolist()}
