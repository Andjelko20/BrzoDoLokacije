from fastapi import FastAPI, File, UploadFile
from typing import List
import cv2
import numpy as np
from face_recognition import face_encodings, compare_faces

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

@app.post("/compare")
async def compare(pictures : List[UploadFile] = File(...)):
    contentsPost = await pictures[0].read()
    contentsAvatar = await pictures[1].read()
    nparrPost = np.fromstring(contentsPost, np.uint8)
    nparrAvatar = np.fromstring(contentsAvatar, np.uint8)
    imgPost = cv2.imdecode(nparrPost, cv2.IMREAD_COLOR)
    imgAvatar = cv2.imdecode(nparrAvatar, cv2.IMREAD_COLOR)
    encodingPostArr = face_encodings(imgPost)
    if encodingPostArr == []:
        return {"status":False}
    encodingPost = encodingPostArr[0]
    encodingAvatar = face_encodings(imgAvatar)[0]
    sameFace=compare_faces([encodingAvatar],encodingPost)
    if True in sameFace:
        return {"status":True}
    else:
        return {"status":False}