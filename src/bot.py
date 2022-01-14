from mirai import Mirai, WebSocketAdapter
from mirai import FriendMessage, GroupMessage, TempMessage
from mirai import Plain, At, AtAll, Face
from mirai.adapters import websocket
from mirai.models.message import FlashImage
from mirai.models.events import MemberJoinEvent, NewFriendRequestEvent, BotLeaveEventKick, \
    BotInvitedJoinGroupRequestEvent, BotJoinGroupEvent
from mirai.models.events import NudgeEvent, MemberJoinRequestEvent, MemberLeaveEventQuit, MemberLeaveEventKick, \
    MemberCardChangeEvent, GroupRecallEvent

# =============================================================
# 需求类
import asyncio
import datetime
import time
import threading

# =============================================================
# 附加功能类
from plugins import getNow
from plugins import logManage
from plugins import MessageProcessing
from plugins import dataManage
from plugins import watcher

# ==========================================================
# 基本信息

message_processing = MessageProcessing.MessageProcessing()
init = message_processing.loadfile()
watcher_lock = True


# ==========================================================
# 定时器
def watcher_bot():
    print('定时器已启动')
    time.sleep(30)
    global watcher_lock
    while watcher_lock:
        now = datetime.datetime.now()
        print('%02d:%02d' % (now.hour, now.minute))
        statistics = dataManage.read_statistics()
        print('\t*上一分钟回复为：', statistics['last_minute'])
        statistics['last_minute'] = 0
        dataManage.save_statistics(statistics)
        print('\t*已将上一分钟回复其置为：0')

        if now.hour == 12 and now.minute == 0:  # 每日中午提醒版本
            loops = asyncio.new_event_loop()
            asyncio.set_event_loop(loops)
            tasks = [watcher.static_message(bot)]
            loops.run_until_complete(asyncio.wait(tasks))

        if now.hour == 12 and now.minute == 5:  # 每日健康打卡
            loops = asyncio.new_event_loop()
            asyncio.set_event_loop(loops)
            tasks = [watcher.daily_health_report(bot)]
            loops.run_until_complete(asyncio.wait(tasks))

        if now.hour == 0 and now.minute == 0:  # 每日零点重置数据
            loops = asyncio.new_event_loop()
            asyncio.set_event_loop(loops)
            tasks = [watcher.new_day(bot), watcher.reset_clock(bot)]
            loops.run_until_complete(asyncio.wait(tasks))

        if now.hour == 3 and now.minute == 0:  # 每日凌晨3点刷新
            message_processing.clash.set_refresh()

        # 每分钟进行打卡检查
        loops = asyncio.new_event_loop()
        asyncio.set_event_loop(loops)
        tasks = [
            watcher.clock_check(bot, now.hour, now.minute),
            watcher.muteall_schedule(bot, now.hour, now.minute),
            watcher.RPG_rank(bot, now.hour, now.minute)
        ]
        loops.run_until_complete(asyncio.wait(tasks))
        time.sleep(60)


# 主程序类
if __name__ == '__main__':
    bot = Mirai(
        qq=1812322920,  # 改成你的机器人的 QQ 号
        adapter=WebSocketAdapter(
            verify_key='Xiao_Qi_Key', host='localhost', port=8080
        )
    )


    # 朋友消息
    @bot.on(FriendMessage)
    async def on_friend_message(event: FriendMessage):
        await message_processing.run(bot, event, 0, event.message_chain, At(bot.qq) in event.message_chain)


    # 群消息
    @bot.on(GroupMessage)
    async def on_group_message(event: GroupMessage):
        await message_processing.run(bot, event, 1, event.message_chain, At(bot.qq) in event.message_chain)


    # 临时消息
    @bot.on(TempMessage)
    async def on_temp_message(event: TempMessage):
        await message_processing.run(bot, event, 2, event.message_chain, At(bot.qq) in event.message_chain)


    # 新成员加入
    @bot.on(MemberJoinEvent)
    async def on_member_join(event: MemberJoinEvent):
        await message_processing.join(bot, event)


    # 新好友请求
    @bot.on(NewFriendRequestEvent)
    async def new_friend(event: NewFriendRequestEvent):
        await message_processing.new_friend(bot, event)


    # 新群请求
    @bot.on(BotInvitedJoinGroupRequestEvent)
    async def new_group(event: BotInvitedJoinGroupRequestEvent):
        await message_processing.new_group(bot, event)


    # 加入群
    @bot.on(BotJoinGroupEvent)
    async def join_group(event: BotJoinGroupEvent):
        await message_processing.join_group(bot, event)


    # 被踢出群
    @bot.on(BotLeaveEventKick)
    async def kick(event: BotLeaveEventKick):
        await message_processing.kick(bot, event)


    # 戳一戳
    @bot.on(NudgeEvent)
    async def nudge(event: NudgeEvent):
        await message_processing.nudge(bot, event)


    # 入群申请
    @bot.on(MemberJoinRequestEvent)
    async def request_group(event: MemberJoinRequestEvent):
        await message_processing.request_group(bot, event)


    # 退群
    @bot.on(MemberLeaveEventQuit)
    async def leave_group(event: MemberLeaveEventQuit):
        await message_processing.leave_group(bot, event)


    # 退群
    @bot.on(MemberLeaveEventKick)
    async def kick_group(event: MemberLeaveEventKick):
        await message_processing.kick_group(bot, event)


    # 群名片修改
    @bot.on(MemberCardChangeEvent)
    async def member_change(event: MemberCardChangeEvent):
        await message_processing.member_change(bot, event)


    # 群消息撤回
    @bot.on(GroupRecallEvent)
    async def member_change(event: GroupRecallEvent):
        await message_processing.group_recall_message(bot, event)


    # ==========================================================
    # 启动机器人
    if not init:
        logManage.log(getNow.toString(), '——————————————————————————\n启动失败！！！\n')
        print('文件缺失！')
        exit(0)

    thread = threading.Thread(target=watcher_bot)
    thread.start()
    qq = bot.qq
    name = message_processing.get_name()
    logManage.log(getNow.toString(), name + '(' + str(qq) + ')初始化成功，开始运行！')

    try:
        bot.run()  # 机器人启动
    except websocket.exceptions.NetworkError:
        print('网络错误')
    except SystemExit:
        print('通道意外关闭，自动重连')
    except:
        print('未知错误自动重连')
