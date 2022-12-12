from fastapi import FastAPI, File, UploadFile
import cv2

app = FastAPI()


@app.get("/")
async def root():
    return {"message": "Hello World"}

@app.post("/avatar")
async def avatar(picture : UploadFile = File(...)):
    face_cascade = cv2.CascadeClassifier('frontal_face.xml')
    face_rects = face_cascade.detectMultiScale((picture,scaleFactor=1.2, minNeighbors=10)
    return {"numOfFaces":face_rects.shape[0]}
    #return {"status":False} # kad nije dobar format slike