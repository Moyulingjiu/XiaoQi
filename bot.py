"""
入口文件，直接运行该文件即可

author：墨羽翎玖
version：1.0.0
"""
import asyncio

from graia.ariadne.event.message import FriendMessage, GroupMessage
from graia.broadcast import Broadcast

from graia.ariadne.app import Ariadne
from graia.ariadne.message.chain import MessageChain
from graia.ariadne.message.element import Plain, Source
from graia.ariadne.model import Friend, MiraiSession, Member, Group

loop = asyncio.new_event_loop()

bcc = Broadcast(loop=loop)
app = Ariadne(
    broadcast=bcc,
    connect_info=MiraiSession(
        host="http://localhost:8080",  # 填入 HTTP API 服务运行的地址
        verify_key="Xiao_Qi_Key",  # 填入 verifyKey
        account=1812322920,  # 你的机器人的 qq 号
    )
)


@bcc.receiver(FriendMessage)
async def friend_message_listener(app: Ariadne, friend: Friend, source: Source):
    message = await app.getMessageFromId(source.id)
    print(message)
    await app.sendMessage(friend, MessageChain.create(Plain("Hello, World!")))


@bcc.receiver(GroupMessage)
async def group_message_listener(app: Ariadne, member: Member, group: Group, source: Source):
    message = await app.getMessageFromId(source.id)
    if message.messageChain == [Plain("小玖")]:
        await app.sendMessage(group, MessageChain.create(Plain("我在~")))


if __name__ == '__main__':
    loop.run_until_complete(app.lifecycle())
