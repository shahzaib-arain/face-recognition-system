from fastapi import FastAPI
from pydantic import BaseModel
import numpy as np
import base64
import cv2
from insightface.app import FaceAnalysis
from io import BytesIO

app = FastAPI()

# Initialize InsightFace model
# 'antelope' is fast; 'buffalo_l' or 'arcface_r100_v1' for higher accuracy
app_model = FaceAnalysis(name='buffalo_l')
app_model.prepare(ctx_id=-1, det_size=(640, 640))  # ctx_id=-1 uses CPU

class EmbeddingRequest(BaseModel):
    imageBase64: str

@app.post("/embedding")
def generate_embedding(req: EmbeddingRequest):
    # Decode Base64 image
    image_data = base64.b64decode(req.imageBase64)
    np_arr = np.frombuffer(image_data, np.uint8)
    img = cv2.imdecode(np_arr, cv2.IMREAD_COLOR)

    # Analyze face
    faces = app_model.get(img)
    if len(faces) == 0:
        return {"error": "No face detected"}

    # Use first detected face
    embedding = faces[0].embedding  # 512-D numpy array
    embedding_list = embedding.tolist()  # convert to Python list

    return {"embedding": embedding_list}
