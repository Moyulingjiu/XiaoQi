from fastapi import FastAPI
from pydantic import BaseModel

from plugins import rpg
from plugins import tarot
import base64

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


@app.get("/tarot")
async def tar(code: str):
    reply_text = '指令错误'
    if code == 'tarotb':
        reply_text = tarot.GetTarot()
    elif code == 'tarotl':
        reply_text = tarot.GetTarot2()
    elif code == 'tarot':
        reply_text = tarot.tarot()
    elif code == 'tarot时间':
        reply_text = tarot.tarotTime()
    elif code == 'tarot是非':
        reply_text = tarot.tarotIs()
    elif code == 'tarot圣三角':
        reply_text = tarot.tarotIs()
    elif code == 'tarot钻石展开法':
        reply_text = tarot.tarotBussiness()
    elif code == 'tarot恋人金字塔':
        reply_text = tarot.tarotLove()
    elif code == 'tarot自我探索':
        reply_text = tarot.tarotSelf()
    elif code == 'tarot吉普赛十字':
        reply_text = tarot.tarotCross()
    elif code == 'tarot二选一':
        reply_text = tarot.tarotChoose()
    elif code == 'tarot关系发展':
        reply_text = tarot.tarotForward()
    elif code == 'tarot六芒星':
        reply_text = tarot.tarotHexagram()
    elif code == 'tarot凯尔特十字':
        reply_text = tarot.tarotCelticCross()
    return {"code": 0, "message": "成功", "data": reply_text}
