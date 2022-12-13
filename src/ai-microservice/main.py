from fastapi import FastAPI, File, UploadFile
import cv2
import numpy as np

app = FastAPI()


@app.get("/")
async def root():
    return {"message": "Hello World"}

@app.post("/avatar")
async def avatar(picture : UploadFile = File(...)):
    contents = await picture.read()
    nparr = np.fromstring(contents, np.uint8)
    img = cv2.imdecode(nparr, cv2.IMREAD_COLOR)
    face_cascade = cv2.CascadeClassifier('frontal_face.xml')
    face_rects = face_cascade.detectMultiScale(img, scaleFactor=1.2, minNeighbors=3)
    if not isinstance(face_rects,tuple):
        if face_rects.shape[0] == 1:
            return {"status":True}
    return {"status":False}