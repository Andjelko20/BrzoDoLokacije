from fastapi import FastAPI, File, UploadFile

app = FastAPI()


@app.get("/")
async def root():
    return {"message": "Hello World"}

@app.post("/avatar")
async def avatar(picture : UploadFile = File(...)):
    return {"status":False} # kad nije dobar format slike