from typing import Optional
from fastapi import FastAPI
from pydantic import BaseModel

from plugins import rpg

app = FastAPI()
my_rpg = rpg.RPG()


class Message(BaseModel):
    text: str
    qq: int
    member_name: str
    bot_name: str
    be_at: bool
    limit: bool


class Reply(BaseModel):
    need_reply: bool
    reply_text: str


@app.get("/")
async def root():
    return {"message": "Hello World"}


@app.post("/rpg")
async def rpg(message: Message):
    need_reply, reply_text, reply_image = my_rpg.handle(message.text, message.qq, message.member_name, message.bot_name,
                                                        message.be_at, message.limit)
    reply: Reply = Reply(need_reply=need_reply, reply_text=reply_text)
    return {"code": 0, "message": "成功", "data": reply}
