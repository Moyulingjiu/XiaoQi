import datetime
import os
import random
import base64

from mirai import Plain, At, AtAll, Image
from mirai.models.message import FlashImage

from plugins import BaseFunction
from plugins import Clash
from plugins import Clock
from plugins import RPG
from plugins import autoReply
from plugins import baidu
from plugins import command
from plugins import dataManage
from plugins import getNow
from plugins import keyReply
from plugins import logManage
from plugins import operator
from plugins import talk
from plugins import weather


async def send_message(bot, event, mode, merge_reply, reply_text, reply_image, need_at, at_qq):
    if reply_text is None:
        reply_text = '【突发未知bug，请使用“*send 信息”指令，将如何触发的以及状态尽可能简略地告诉主人】'

    if mode == 0 or mode == 2 or (mode == 1 and not need_at):
        if not merge_reply or reply_image == '' or reply_text == '':
            if reply_image != '':
                await bot.send(event, await Image.from_local(filename=reply_image))
            if reply_text != '':
                await bot.send(event, reply_text)
        else:
            await bot.send(event, [reply_text, await Image.from_local(filename=reply_image)])
    else:
        if at_qq > 0:
            member = await bot.get_group_member(event.sender.group.id, at_qq)
            if member is not None:
                if reply_image != '':
                    await bot.send(event, [At(at_qq), await Image.from_local(filename=reply_image)])
                if reply_text != '':
                    await bot.send(event, [At(at_qq), reply_text])
            else:
                if reply_image != '':
                    await bot.send(event, await Image.from_local(filename=reply_image))
                if reply_text != '':
                    await bot.send(event, reply_text)
        elif at_qq == 0:
            if reply_image != '':
                await bot.send(event, [At(event.sender.id), await Image.from_local(filename=reply_image)])
            if reply_text != '':
                await bot.send(event, [At(event.sender.id), reply_text])
        elif at_qq == -1:
            if reply_image != '':
                await bot.send(event, [AtAll(), await Image.from_local(filename=reply_image)])
            if reply_text != '':
                await bot.send(event, [AtAll(), reply_text])


async def send_complex_message(bot, event, mode, complex_reply, complex_at):
    if mode == 0 or mode == 2:
        await bot.send(event, complex_reply)
    elif mode == 1:
        if complex_at['at_type'] == -1:
            await bot.send(event, complex_reply)
        elif complex_at['at_type'] == 0:
            if complex_at['at'] > 0:
                member = await bot.get_group_member(event.sender.group.id, complex_at['at'])
                if member is not None:
                    complex_reply.insert(0, At(complex_at['at']))
                    await bot.send(event, complex_reply)
            elif complex_at['at'] == 0:
                complex_reply.insert(0, At(event.sender.id))
                await bot.send(event, complex_reply)
            elif complex_at['at'] == -1:
                complex_reply.insert(0, AtAll())
                await bot.send(event, complex_reply)
        elif complex_at['at_type'] == 1:
            group = dataManage.read_group(event.sender.group.id)
            init = False
            if not group['group'].__contains__(complex_at['at']):
                complex_reply.insert(0, Plain('@' + str(complex_at['at']) + ' '))
            else:
                complex_reply.insert(0, Plain('\n---------------\n'))
                for qq in group['group'][complex_at['at']]:
                    member = await bot.get_group_member(event.sender.group.id, qq)
                    if member is not None:
                        complex_reply.insert(0, At(qq))

            await bot.send(event, complex_reply)


# 布尔开关类型文案
def bool_string(switch):
    if switch:
        return '已开启'
    else:
        return '已关闭'


# 时间预处理
def time_pretreatment(time: str) -> str:
    time = time.replace('\\', '').strip()
    if len(time) == 2:
        if time == '00':
            return '0'
        if time[0] == '0' and time[1] != '0':
            return time[1]
    return time


# 合法的时间
def valid_time(hour: int, minute: int) -> bool:
    if not (0 <= hour < 24):
        return False
    if not (0 <= minute < 60):
        return False
    return True


class MessageProcessing:
    config = {}
    statistics = {}
    bot_qq = 0
    bot_name = '小柒'

    groups = {}
    users = {}

    message_tmp = {}
    last_reply = ''

    luck = BaseFunction.luck()
    bottle = BaseFunction.DriftingBottle()

    rpg = RPG.RPG()
    clash = Clash.Clash()

    clock = Clock.Clock()

    def __init__(self):
        pass

    def get_user(self, qq):
        self.users[qq] = dataManage.read_user(qq)

    def get_group(self, group_id):
        self.groups[group_id] = dataManage.read_group(group_id)

    def loadfile(self):
        # 基本信息重置
        self.config = dataManage.read_config()
        self.statistics = dataManage.read_statistics()
        luck = dataManage.read_luck()
        screen = dataManage.read_screen_word()

        if not os.path.exists('data/Function/Talk/lovetalk.txt'):
            with open('data/Function/Talk/lovetalk.txt', 'w', encoding='utf-8') as f:
                f.write('1\n1.我大约真的没有什么才华，只是因为有幸见着了你，于是这颗庸常的心中才凭空生出好些浪漫。')
        if not os.path.exists('data/Function/Talk/poem.txt'):
            with open('data/Function/Talk/poem.txt', 'w', encoding='utf-8') as f:
                f.write('1\n1.我们趋行在人生这个恒古的旅途，在坎坷中奔跑，在挫折里涅槃，忧愁缠满全身，痛苦飘洒一地。我们累，却无从止歇；我们苦，却无法回避。——《百年孤独》')
        if not os.path.exists('data/Function/Talk/swear.txt'):
            with open('data/Function/Talk/swear.txt', 'w', encoding='utf-8') as f:
                f.write('1\n1.我无外乎也就讨厌两种人，一种是你这样的，另一种是不管你以后变成什么样那样的。')

        if not os.path.exists('data/Function/Talk/tarot.txt'):
            return False
        if not os.path.exists('data/Function/Talk/tarot2.txt'):
            return False

        # 四六级词汇
        if not os.path.exists('data/Function/Vocabulary/vocabulary-4.txt'):
            return False
        if not os.path.exists('data/Function/Vocabulary/vocabulary-4-index.txt'):
            with open('data/vocabulary-4-index.txt', 'w', encoding='utf-8') as f:
                f.write('1')
        if not os.path.exists('data/Function/Vocabulary/vocabulary-6.txt'):
            return False
        if not os.path.exists('data/Function/Vocabulary/vocabulary-6-index.txt'):
            with open('data/vocabulary-6-index.txt', 'w', encoding='utf-8') as f:
                f.write('1')

        return True

    def get_right(self, qq):
        if qq == self.config['master']:
            return 0
        elif qq in self.config["administrator"]:
            return 1
        elif qq in self.config["contributor"]:
            return 2
        else:
            return 3

    def get_blacklist(self, qq, group_id):
        if qq in self.config['blacklist_member']:
            return 1
        elif group_id > 0 and group_id in self.config['blacklist_group']:
            return 2
        return 0

    def get_qq(self):
        return self.bot_qq

    def get_name(self):
        return self.bot_name

    async def switch(self, bot, event, mode, message, message_code, group_id, right, group_right, qq):
        merge_reply = False
        reply_image = ''
        need_at = False
        at_qq = 0
        muteall_schedule = dataManage.load_obj('data/Function/muteall')  # 禁言计划
        remind_schedule = dataManage.load_obj('data/Function/remind')  # 定时提醒

        if message_code == 'nudge on' or message == '开启戳一戳':
            if not self.groups[group_id]['config']['nudge']:
                if group_right < 2 or right < 3:
                    self.groups[group_id]['config']['nudge'] = True
                    dataManage.save_group(group_id, self.groups[group_id])

                    self.statistics['operate'] += 1
                    dataManage.save_statistics(self.statistics)
                    reply_text = '本群已开启戳一戳功能~'
                else:
                    reply_text = '权限不足，需要群管理或群主'
                await send_message(bot, event, mode, merge_reply, reply_text, reply_image, need_at, at_qq)
            return
        elif message_code == 'nudge off' or message == '关闭戳一戳':
            if self.groups[group_id]['config']['nudge']:
                if group_right < 2 or right < 3:
                    self.groups[group_id]['config']['nudge'] = False
                    dataManage.save_group(group_id, self.groups[group_id])

                    self.statistics['operate'] += 1
                    dataManage.save_statistics(self.statistics)
                    reply_text = '本群已关闭戳一戳功能~'
                else:
                    reply_text = '权限不足，需要群管理或群主'
                await send_message(bot, event, mode, merge_reply, reply_text, reply_image, need_at, at_qq)
            return
        elif message_code == 'curse on' or message == '开启脏话':
            if not self.groups[group_id]['config']['curse']:
                if group_right < 2 or right < 3:
                    self.groups[group_id]['config']['curse'] = True
                    dataManage.save_group(group_id, self.groups[group_id])

                    self.statistics['operate'] += 1
                    dataManage.save_statistics(self.statistics)
                    reply_text = '本群已开启脏话功能~'
                else:
                    reply_text = '权限不足，需要群管理或群主'
                await send_message(bot, event, mode, merge_reply, reply_text, reply_image, need_at, at_qq)
            return
        elif message_code == 'curse off' or message == '关闭脏话':
            if self.groups[group_id]['config']['curse']:
                if group_right < 2 or right < 3:
                    self.groups[group_id]['config']['curse'] = False
                    dataManage.save_group(group_id, self.groups[group_id])

                    self.statistics['operate'] += 1
                    dataManage.save_statistics(self.statistics)
                    reply_text = '本群已关闭脏话功能~'
                else:
                    reply_text = '权限不足，需要群管理或群主'
                await send_message(bot, event, mode, merge_reply, reply_text, reply_image, need_at, at_qq)
            return
        elif message_code == 'game on' or message == '开启游戏':
            if not self.groups[group_id]['config']['RPG']:
                if group_right < 2 or right < 3:
                    self.groups[group_id]['config']['RPG'] = True
                    dataManage.save_group(group_id, self.groups[group_id])

                    self.statistics['operate'] += 1
                    dataManage.save_statistics(self.statistics)
                    reply_text = '本群已开启游戏功能~'
                else:
                    reply_text = '权限不足，需要群管理或群主'
                await send_message(bot, event, mode, merge_reply, reply_text, reply_image, need_at, at_qq)
            return
        elif message_code == 'game off' or message == '关闭游戏':
            if self.groups[group_id]['config']['RPG']:
                if group_right < 2 or right < 3:
                    self.groups[group_id]['config']['RPG'] = False
                    dataManage.save_group(group_id, self.groups[group_id])

                    self.statistics['operate'] += 1
                    dataManage.save_statistics(self.statistics)
                    reply_text = '本群已关闭游戏功能~'
                else:
                    reply_text = '权限不足，需要群管理或群主'
                await send_message(bot, event, mode, merge_reply, reply_text, reply_image, need_at, at_qq)
            return
        elif message_code == 'game limit on' or message == '开启游戏限制模式':
            if not self.groups[group_id]['config']['limit_RPG']:
                if group_right < 2 or right < 3:
                    self.groups[group_id]['config']['limit_RPG'] = True
                    dataManage.save_group(group_id, self.groups[group_id])

                    self.statistics['operate'] += 1
                    dataManage.save_statistics(self.statistics)
                    reply_text = '本群已开启游戏限制~'
                else:
                    reply_text = '权限不足，需要群管理或群主'
                await send_message(bot, event, mode, merge_reply, reply_text, reply_image, need_at, at_qq)
            return
        elif message_code == 'game limit off' or message == '关闭游戏限制模式':
            if self.groups[group_id]['config']['limit_RPG']:
                if group_right < 2 or right < 3:
                    self.groups[group_id]['config']['limit_RPG'] = False
                    dataManage.save_group(group_id, self.groups[group_id])

                    self.statistics['operate'] += 1
                    dataManage.save_statistics(self.statistics)
                    reply_text = '本群已关闭游戏限制~'
                else:
                    reply_text = '权限不足，需要群管理或群主'
                await send_message(bot, event, mode, merge_reply, reply_text, reply_image, need_at, at_qq)
            return

        elif message_code == 'image on' or message == '开启图片搜索':
            if not self.groups[group_id]['config']['image']:
                if right == 0:
                    self.groups[group_id]['config']['image'] = True
                    dataManage.save_group(group_id, self.groups[group_id])

                    self.statistics['operate'] += 1
                    dataManage.save_statistics(self.statistics)
                    reply_text = '本群已开启p站图片搜索功能~'
                else:
                    reply_text = '权限不足，需要主人（发送图片及其占用资源所以只对部分开放）'
                await send_message(bot, event, mode, merge_reply, reply_text, reply_image, need_at, at_qq)
            return
        elif message_code == 'image off' or message == '关闭图片搜索':
            if self.groups[group_id]['config']['image']:
                if right == 0:
                    self.groups[group_id]['config']['image'] = False
                    dataManage.save_group(group_id, self.groups[group_id])

                    self.statistics['operate'] += 1
                    dataManage.save_statistics(self.statistics)
                    reply_text = '本群已关闭p站图片搜索功能~'
                else:
                    reply_text = '权限不足，需要主人（发送图片及其占用资源所以只对部分开放）'
                await send_message(bot, event, mode, merge_reply, reply_text, reply_image, need_at, at_qq)
            return

        elif message_code == 'reply on' or message == '开启自定义回复':
            if not self.groups[group_id]['config']['autonomous_reply']:
                if group_right < 2 or right < 3:
                    self.groups[group_id]['config']['autonomous_reply'] = True
                    dataManage.save_group(group_id, self.groups[group_id])

                    self.statistics['operate'] += 1
                    dataManage.save_statistics(self.statistics)
                    reply_text = '本群已开启自定义回复功能~'
                else:
                    reply_text = '权限不足，需要群管理或群主'
                await send_message(bot, event, mode, merge_reply, reply_text, reply_image, need_at, at_qq)
            return
        elif message_code == 'reply off' or message == '关闭自定义回复':
            if self.groups[group_id]['config']['autonomous_reply']:
                if group_right < 2 or right < 3:
                    self.groups[group_id]['config']['autonomous_reply'] = False
                    dataManage.save_group(group_id, self.groups[group_id])

                    self.statistics['operate'] += 1
                    dataManage.save_statistics(self.statistics)
                    reply_text = '本群已关闭自定义回复功能~'
                else:
                    reply_text = '权限不足，需要群管理或群主'
                await send_message(bot, event, mode, merge_reply, reply_text, reply_image, need_at, at_qq)
            return
        elif message_code == 'repeat on' or message == '开启自动加一':
            if not self.groups[group_id]['config']['repeat']:
                if group_right < 2 or right < 3:
                    self.groups[group_id]['config']['repeat'] = True
                    dataManage.save_group(group_id, self.groups[group_id])

                    self.statistics['operate'] += 1
                    dataManage.save_statistics(self.statistics)
                    reply_text = '本群已开启自动加一功能~'
                else:
                    reply_text = '权限不足，需要群管理或群主'
                await send_message(bot, event, mode, merge_reply, reply_text, reply_image, need_at, at_qq)
            return
        elif message_code == 'repeat off' or message == '关闭自动加一':
            if self.groups[group_id]['config']['repeat']:
                if group_right < 2 or right < 3:
                    self.groups[group_id]['config']['repeat'] = False
                    dataManage.save_group(group_id, self.groups[group_id])

                    self.statistics['operate'] += 1
                    dataManage.save_statistics(self.statistics)
                    reply_text = '本群已关闭自动加一功能~'
                else:
                    reply_text = '权限不足，需要群管理或群主'
                await send_message(bot, event, mode, merge_reply, reply_text, reply_image, need_at, at_qq)
            return
        elif message_code == 'trpg on' or message == '开启骰娘':
            if not self.groups[group_id]['config']['TRPG']:
                if group_right < 2 or right < 3:
                    self.groups[group_id]['config']['TRPG'] = True
                    dataManage.save_group(group_id, self.groups[group_id])

                    self.statistics['operate'] += 1
                    dataManage.save_statistics(self.statistics)
                    reply_text = '本群已开启骰娘功能~'
                else:
                    reply_text = '权限不足，需要群管理或群主'
                await send_message(bot, event, mode, merge_reply, reply_text, reply_image, need_at, at_qq)
            return
        elif message_code == 'trpg off' or message == '关闭骰娘':
            if self.groups[group_id]['config']['TRPG']:
                if group_right < 2 or right < 3:
                    self.groups[group_id]['config']['TRPG'] = False
                    dataManage.save_group(group_id, self.groups[group_id])

                    self.statistics['operate'] += 1
                    dataManage.save_statistics(self.statistics)
                    reply_text = '本群已关闭骰娘功能~'
                else:
                    reply_text = '权限不足，需要群管理或群主'
                await send_message(bot, event, mode, merge_reply, reply_text, reply_image, need_at, at_qq)
            return
        elif message_code == 'clash on' or message == '开启部落冲突查询':
            if not self.groups[group_id]['config']['clash']:
                if group_right < 2 or right < 3:
                    self.groups[group_id]['config']['clash'] = True
                    dataManage.save_group(group_id, self.groups[group_id])

                    self.statistics['operate'] += 1
                    dataManage.save_statistics(self.statistics)
                    reply_text = '本群已开启部落冲突查询功能~'
                else:
                    reply_text = '权限不足，需要群管理或群主'
                await send_message(bot, event, mode, merge_reply, reply_text, reply_image, need_at, at_qq)
            return
        elif message_code == 'clash off' or message == '关闭部落冲突查询':
            if self.groups[group_id]['config']['clash']:
                if group_right < 2 or right < 3:
                    self.groups[group_id]['config']['clash'] = False
                    dataManage.save_group(group_id, self.groups[group_id])

                    self.statistics['operate'] += 1
                    dataManage.save_statistics(self.statistics)
                    reply_text = '本群已关闭部落冲突查询功能~'
                else:
                    reply_text = '权限不足，需要群管理或群主'
                await send_message(bot, event, mode, merge_reply, reply_text, reply_image, need_at, at_qq)
            return

        elif message_code == 'flash on' or message == '开启解除闪照':
            if not self.groups[group_id]['config']['flash']:
                if group_right < 2 or right < 3:
                    self.groups[group_id]['config']['flash'] = True
                    dataManage.save_group(group_id, self.groups[group_id])

                    self.statistics['operate'] += 1
                    dataManage.save_statistics(self.statistics)
                    reply_text = '本群已开启自动解除闪照功能~'
                else:
                    reply_text = '权限不足，需要群管理或群主'
                await send_message(bot, event, mode, merge_reply, reply_text, reply_image, need_at, at_qq)
            return
        elif message_code == 'flash off' or message == '关闭解除闪照':
            if self.groups[group_id]['config']['flash']:
                if group_right < 2 or right < 3:
                    self.groups[group_id]['config']['flash'] = False
                    dataManage.save_group(group_id, self.groups[group_id])

                    self.statistics['operate'] += 1
                    dataManage.save_statistics(self.statistics)
                    reply_text = '本群已关闭自动解除闪照功能~'
                else:
                    reply_text = '权限不足，需要群管理或群主'
                await send_message(bot, event, mode, merge_reply, reply_text, reply_image, need_at, at_qq)
            return

        elif message_code == 'member wather on' or message == '开启成员监控':
            if not self.groups[group_id]['config']['member_wather']:
                if group_right < 2 or right < 3:
                    self.groups[group_id]['config']['member_wather'] = True
                    dataManage.save_group(group_id, self.groups[group_id])

                    self.statistics['operate'] += 1
                    dataManage.save_statistics(self.statistics)
                    reply_text = '本群已开启成员监控功能~'
                else:
                    reply_text = '权限不足，需要群管理或群主'
                await send_message(bot, event, mode, merge_reply, reply_text, reply_image, need_at, at_qq)
            return
        elif message_code == 'member wather off' or message == '关闭成员监控':
            if self.groups[group_id]['config']['member_wather']:
                if group_right < 2 or right < 3:
                    self.groups[group_id]['config']['member_wather'] = False
                    dataManage.save_group(group_id, self.groups[group_id])

                    self.statistics['operate'] += 1
                    dataManage.save_statistics(self.statistics)
                    reply_text = '本群已关闭成员监控功能~'
                else:
                    reply_text = '权限不足，需要群管理或群主'
                await send_message(bot, event, mode, merge_reply, reply_text, reply_image, need_at, at_qq)
            return

        elif message_code == 'welcome on' or message == '开启新人欢迎':
            if not self.groups[group_id]['config']['welcome']:
                if group_right < 2 or right < 3:
                    self.groups[group_id]['config']['welcome'] = True
                    dataManage.save_group(group_id, self.groups[group_id])

                    self.statistics['operate'] += 1
                    dataManage.save_statistics(self.statistics)
                    reply_text = '本群已开启入群欢迎功能~'

                    if self.groups[group_id]['welcome'] is None:
                        reply_text += '\n但是您还没有设置入群欢迎哦~请告诉我入群欢迎的内容吧~（下一条发送的消息将会被记录，请不要包含链接，违者黑名单！！！）'
                        self.users[qq]['buffer']['id'] = 1
                        self.users[qq]['buffer']['buffer'] = group_id
                        dataManage.save_user(qq, self.users[qq])
                else:
                    reply_text = '权限不足，需要群管理或群主'
                await send_message(bot, event, mode, merge_reply, reply_text, reply_image, need_at, at_qq)
            return
        elif message_code == 'welcome off' or message == '关闭新人欢迎':
            if self.groups[group_id]['config']['welcome']:
                if group_right < 2 or right < 3:
                    self.groups[group_id]['config']['welcome'] = False
                    dataManage.save_group(group_id, self.groups[group_id])

                    self.statistics['operate'] += 1
                    dataManage.save_statistics(self.statistics)
                    reply_text = '本群已关闭入群欢迎功能~'
                else:
                    reply_text = '权限不足，需要群管理或群主'
                await send_message(bot, event, mode, merge_reply, reply_text, reply_image, need_at, at_qq)
            return
        elif message_code == 'welcome set' or message == '设置新人欢迎':
            if group_right < 2 or right < 3:
                self.groups[group_id]['config']['welcome'] = True
                dataManage.save_group(group_id, self.groups[group_id])

                self.statistics['operate'] += 1
                dataManage.save_statistics(self.statistics)
                reply_text = '请告诉我入群欢迎的内容吧~（下一条发送的消息将会被记录，请不要包含链接，违者黑名单！！！）'
                self.users[qq]['buffer']['id'] = 1
                self.users[qq]['buffer']['buffer'] = group_id
                dataManage.save_user(qq, self.users[qq])
            else:
                reply_text = '权限不足，需要群管理或群主'
            await send_message(bot, event, mode, merge_reply, reply_text, reply_image, need_at, at_qq)
            return

        elif message_code == 'automatic on' or message == '开启自动审核':
            if not self.groups[group_id]['config']['automatic']:
                if group_right < 2 or right < 3:
                    self.groups[group_id]['config']['automatic'] = True
                    dataManage.save_group(group_id, self.groups[group_id])

                    self.statistics['operate'] += 1
                    dataManage.save_statistics(self.statistics)
                    reply_text = '本群已开启入群自动审核功能~'

                    if self.groups[group_id]['config']['pass'] == '':
                        reply_text += '\n但是您还没有设置入群暗号哦~请告诉我入群暗号的内容吧~（下一条发送的消息将会被记录）'
                        self.users[qq]['buffer']['id'] = 8
                        self.users[qq]['buffer']['buffer'] = group_id
                        dataManage.save_user(qq, self.users[qq])
                else:
                    reply_text = '权限不足，需要群管理或群主'
                await send_message(bot, event, mode, merge_reply, reply_text, reply_image, need_at, at_qq)
            return
        elif message_code == 'automatic off' or message == '关闭自动审核':
            if self.groups[group_id]['config']['automatic']:
                if group_right < 2 or right < 3:
                    self.groups[group_id]['config']['automatic'] = False
                    dataManage.save_group(group_id, self.groups[group_id])

                    self.statistics['operate'] += 1
                    dataManage.save_statistics(self.statistics)
                    reply_text = '本群已关闭入群自动审核功能~'
                else:
                    reply_text = '权限不足，需要群管理或群主'
                await send_message(bot, event, mode, merge_reply, reply_text, reply_image, need_at, at_qq)
            return
        elif message_code == 'automatic set' or message == '设置自动审核':
            if group_right < 2 or right < 3:
                self.groups[group_id]['config']['welcome'] = True
                dataManage.save_group(group_id, self.groups[group_id])

                self.statistics['operate'] += 1
                dataManage.save_statistics(self.statistics)
                reply_text = '请告诉我自动审批的暗号吧~（下一条发送的消息将会被记录）'
                self.users[qq]['buffer']['id'] = 8
                self.users[qq]['buffer']['buffer'] = group_id
                dataManage.save_user(qq, self.users[qq])
            else:
                reply_text = '权限不足，需要群管理或群主'
            await send_message(bot, event, mode, merge_reply, reply_text, reply_image, need_at, at_qq)
            return

        elif message_code == 'muteall schedule on' or message == '开启定时全员禁言' or message == '开启定时全体禁言':
            if not muteall_schedule.__contains__(group_id):
                if group_right < 2 or right < 2:
                    self.users[qq]['buffer']['id'] = 12
                    self.users[qq]['buffer']['buffer'] = group_id
                    dataManage.save_user(qq, self.users[qq])
                    await bot.send(event,
                                   '欢迎订阅“定时全体禁言”服务！请用以下格式告诉我您的开始和结束时间：\nxx:xx xx:xx（采用24小时制，不足两位补0）\n例如您想从凌晨两点半禁言到早上六点，可以输入：“02:30 06:00”')
                else:
                    await bot.send(event, '权限不足，需要群主或群管理')
            else:
                await bot.send(event, '现在已有了定时全员禁言的计划，不可重复添加。当前计划：%2d：%2d—%2d：%2d' % (
                    muteall_schedule[group_id]['hour1'],
                    muteall_schedule[group_id]['minute1'],
                    muteall_schedule[group_id]['hour2'],
                    muteall_schedule[group_id]['minute2']
                ))
            return
        elif message_code == 'muteall schedule off' or message == '关闭定时全员禁言' or message == '关闭定时全体禁言':
            if muteall_schedule.__contains__(group_id):
                if group_right < 2 or right < 2:
                    del muteall_schedule[group_id]
                    dataManage.save_obj(muteall_schedule, 'data/Function/muteall')
                    await bot.send(event, '已成功关闭')
                else:
                    await bot.send(event, '权限不足，需要群主或群管理')
            else:
                await bot.send(event, '现在没有定时全员禁言的计划')
            return

        elif message == '添加定时提醒':
            single = {
                'name': '',
                'is_consecutive': False,
                'from': {  # 开始时间
                    'year': 0,
                    'month': 0,
                    'day': 0,
                    'hour': 0,
                    'minute': 0,
                    'second': 0
                },
                'to': {  # 结束时间
                    'year': 0,
                    'month': 0,
                    'day': 0,
                    'hour': 0,
                    'minute': 0,
                    'second': 0
                },
                'repeat': '每小时、每天、每星期、每月、每年、每季度',
                'repeat-mode': ''
            }
        elif message == '查看定时提醒':
            if not remind_schedule.__contains__(group_id) or len(remind_schedule[group_id]) == 0:
                await bot.send(event, '本群没有任何定时提醒')
                return

            index = 1
            reply = '本群定时提醒如下：'
            for single in remind_schedule[group_id]:
                reply = '\n%d.' % index
            await bot.send(event, reply)
            return
        elif message == '删除定时提醒':
            pass

        elif message_code == 'revoke on' or message == '开启防撤回':
            if not self.groups[group_id]['config']['revoke']:
                if group_right < 2 or right < 3:
                    self.groups[group_id]['config']['revoke'] = True
                    dataManage.save_group(group_id, self.groups[group_id])

                    self.statistics['operate'] += 1
                    dataManage.save_statistics(self.statistics)
                    reply_text = '本群已开启防撤回功能~'
                else:
                    reply_text = '权限不足，需要群管理或群主'
                await send_message(bot, event, mode, merge_reply, reply_text, reply_image, need_at, at_qq)
            return
        elif message_code == 'revoke off' or message == '关闭防撤回':
            if self.groups[group_id]['config']['revoke']:
                if group_right < 2 or right < 3:
                    self.groups[group_id]['config']['revoke'] = False
                    dataManage.save_group(group_id, self.groups[group_id])

                    self.statistics['operate'] += 1
                    dataManage.save_statistics(self.statistics)
                    reply_text = '本群已关闭防撤回功能~'
                else:
                    reply_text = '权限不足，需要群管理或群主'
                await send_message(bot, event, mode, merge_reply, reply_text, reply_image, need_at, at_qq)
            return

        elif message[:6] == '添加指令隧穿':
            if not (group_right < 2 or right < 3):
                await bot.send(event, '权限不足')
                return
            information: list = message[6:].strip().split(' ')
            if len(information) == 2:
                if '删除指令隧穿' in information[0] or '查看指令隧穿' in information[0] or '添加指令隧穿' in information[0]:
                    await bot.send(event, "非法占用，该指令格式不可以占用删除隧穿，查看隧穿")
                else:
                    tunneling: dict = dataManage.load_obj('data/Function/tunneling')
                    if not tunneling.__contains__(group_id):
                        tunneling[group_id] = {}
                    information[0] = information[0].strip()
                    information[1] = information[1].strip()
                    if tunneling[group_id].__contains__(information[0]):
                        await bot.send(event, "该隧穿已被占用：%s->%s" % (information[0], tunneling[group_id][information[0]]))
                    else:
                        tunneling[group_id][information[0]] = information[1]
                        dataManage.save_obj(tunneling, 'data/Function/tunneling')
                        await bot.send(event, "成功添加隧穿指令：%s->%s" % (information[0], information[1]))
            else:
                await bot.send(event, "格式错误，该指令格式如下“添加指令隧穿 原始指令 隧穿到某个指令”")
            return
        elif message[:6] == '删除指令隧穿':
            if not (group_right < 2 or right < 3):
                await bot.send(event, '权限不足')
                return
            information: str = message[6:].strip()
            tunneling: dict = dataManage.load_obj('data/Function/tunneling')
            if not tunneling.__contains__(group_id) or not tunneling[group_id].__contains__(information):
                await bot.send(event, "原始隧穿指令不存在")
            else:
                await bot.send(event, "成功删除隧穿指令：%s->%s" % (information, tunneling[group_id][information]))
                del tunneling[group_id][information]
                dataManage.save_obj(tunneling, 'data/Function/tunneling')
            return
        elif message == '查看指令隧穿':
            tunneling: dict = dataManage.load_obj('data/Function/tunneling')
            if not tunneling.__contains__(group_id):
                await bot.send(event, "暂无任何隧穿命令")
            elif len(tunneling[group_id]) == 0:
                await bot.send(event, "暂无任何隧穿命令")
            else:
                reply = '隧穿指令如下：'
                for key, value in tunneling[group_id].items():
                    reply += '\n%s->%s' % (key, value)
                await bot.send(event, reply)
            return

        elif message == '开启群回复共享':
            if not (group_right < 2 or right < 3):
                await bot.send(event, '权限不足')
                return
            copy_allow = dataManage.load_obj('data/Function/reply_copy_right')
            if copy_allow.__contains__(group_id):
                await bot.send(event, '本群的回复共享本来就是开启的')
                return
            copy_allow[group_id] = True
            dataManage.save_obj(copy_allow, 'data/Function/reply_copy_right')
            await bot.send(event, '已开启群回复共享，其他群可以输入“复制群回复%d”来复制回复' % group_id)
            return
        elif message == '关闭群回复共享':
            if not (group_right < 2 or right < 3):
                await bot.send(event, '权限不足')
                return
            copy_allow = dataManage.load_obj('data/Function/reply_copy_right')
            if not copy_allow.__contains__(group_id):
                await bot.send(event, '本群的回复共享本来就是关闭的')
                return
            del copy_allow[group_id]
            dataManage.save_obj(copy_allow, 'data/Function/reply_copy_right')
            await bot.send(event, '已关闭群回复共享')
            return
        elif message[:5] == '复制群回复':
            if not (group_right < 2 or right < 3):
                await bot.send(event, '权限不足')
                return
            target = message[5:].strip()
            if target.isdigit():
                target = int(target)
                copy_allow = dataManage.load_obj('data/Function/reply_copy_right')
                if copy_allow.__contains__(target):
                    self.get_group(target)
                    target_group = self.groups[target]
                    group = self.groups[group_id]
                    group['key_reply'] = target_group['key_reply']
                    dataManage.save_group(group_id, group)
                    await bot.send(event, '复制成功~')
                else:
                    await bot.send(event, '目标群没有共享回复库~')
            else:
                await bot.send(event, '格式错误~')
        return 1

    # 0：朋友消息，1：群消息，2：临时消息
    async def run(self, bot, event, mode, message_chain, be_at):
        self.config = dataManage.read_config()
        self.statistics = dataManage.read_statistics()
        self.bot_qq = self.config['qq']
        self.bot_name = self.config['name']

        # ===================================================================================
        # ===================================================================================
        # 消息表获取
        message = ''
        plain_list = message_chain[Plain]
        for i in plain_list:
            message += str(i)
        at_list = message_chain[At]
        for i in at_list:
            if i != At(bot.qq):
                message += str(i)
        if len(message_chain[Image]) != 0:
            message += '[图片]'
        flash_image = message_chain[FlashImage]
        if len(flash_image) != 0:
            message += '[闪照]'

        # ===================================================================================
        # ===================================================================================
        # 基本信息获取
        # interceptable_need_reply = False  # 可被打断的回复
        need_reply = False  # 是否需要回复
        merge_reply = False  # 是否合并回复
        reply_text = ''  # 回复的文本内容
        reply_image = ''  # 回复的图片
        need_complex_reply = False  # 是否是复杂回复
        complex_at = {
            'at_type': -1,  # -1：不艾特；0：艾特；1：艾特分组
            'at': 0
        }  # 复杂艾特
        complex_reply = None  # 复杂回复
        need_at = False  # 是否需要at
        at_qq = 0  # at的qq是谁
        # 状态信息
        group_right = 2  # 在群里的权限（群主、管理员、成员）
        if mode == 0:
            group_id = 0  # 发消息的人的群号（如果是群聊消息）
            group_name = ''
        else:
            group_id = event.sender.group.id
            group_name = event.sender.group.name
            tmp = str(event.sender.permission)
            if tmp == 'Permission.Owner':
                group_right = 0
            elif tmp == 'Permission.Administrator':
                group_right = 1

        qq = event.sender.id  # (发消息人的qq)
        name = event.sender.get_name()

        right = self.get_right(qq)  # 对于小柒的权限（主人、管理员、贡献者）
        blacklist = self.get_blacklist(qq, group_id)

        if mode == 0 or mode == 2:
            be_at = True

        self.get_user(qq)
        if mode == 1:
            self.get_group(group_id)

        key_allow = []
        if mode == 1:
            key_allow = self.groups[group_id]['config']['key']
        elif mode == 0 or mode == 2:
            key_allow = self.users[qq]['config']['key']

        # 获取指令信息
        message = message.strip()
        tunneling: dict = dataManage.load_obj('data/Function/tunneling')
        if tunneling.__contains__(group_id):
            if tunneling[group_id].__contains__(message):
                print('隧穿指令：%s->%s' % (message, tunneling[group_id][message]))
                message = tunneling[group_id][message]
            else:
                for key, value in tunneling[group_id].items():
                    if message.startswith(key):
                        message = message.replace(key, value, 1)
                        break

        message_len = len(message)
        message_code = message.lower()
        if len(key_allow) == 0:
            message_code = message_code
        elif message_len > 0 and message_code[0] in key_allow:
            message_code = message_code[1:]
        else:
            message_code = ''
        message_code_len = len(message_code)

        be_mute = (mode == 1 and self.groups[group_id]['config']['mute'])

        master = await bot.get_friend(self.config['master'])

        # print('\tmessage:' + message)
        # print('\tmessage_code:' + message_code)
        # print('\tqq:' + str(qq) + '<' + name + '>')
        # if mode == 1:
        #     print('\tgroup:' + str(group_id) + '<' + event.sender.group.get_name() + '>')
        #     print('\tmute:' + str(be_mute))

        # ===================================================================================
        # ===================================================================================
        # 消息处理开始

        # 禁言消息的处理
        if mode == 1 and message[:5] != '删除屏蔽词' and message[
                                                    :5] != '添加屏蔽词' and message != '清空屏蔽词' and message != '查看屏蔽词':
            revoke = False
            for key in self.groups[group_id]['prohibited_word']:
                if key in message:
                    reply_text = '发现屏蔽词“' + key + '”'
                    revoke = True
                    break

            if revoke:
                need_reply = True
                need_at = True
                if group_right == 2:
                    if str(event.sender.group.permission) != 'Permission.Member':
                        await bot.recall(message_chain.message_id)
                        reply_text += '，予以撤回~'
                    else:
                        reply_text += '，但是' + self.bot_name + '没有办法撤回诶~'
                else:
                    reply_text += '，但是对方是管理员/群主，' + self.bot_name + '打不过，嘤嘤嘤~'

            if need_reply:
                await send_message(bot, event, mode, merge_reply, reply_text, reply_image, need_at, at_qq)
                return

        # 基本信息查看
        if message == '我的权限':
            need_at = True
            if blacklist == 1:
                reply_text = '你当前在黑名单中~'
            elif blacklist == 2:
                reply_text = '本群当前在黑名单中'
            elif right == 0:
                reply_text = '当前权限：主人\n可以输入“主人帮助”来获取指令帮助哦~'
            elif right == 1:
                reply_text = '当前权限：管理员\n可以输入“管理员帮助”来获取指令帮助哦~'
            elif right == 2:
                reply_text = '当前权限：贡献者\n可以输入“贡献者帮助”来获取指令帮助哦~'
            elif right == 3:
                reply_text = '当前权限：普通用户\n可以输入“*help”来获取指令帮助；输入“骰娘”来获取骰娘帮助；输入“游戏帮助”来获取游戏帮助'

            if be_mute:
                reply_text += '\n在本群中' + self.bot_name + '被禁言了'
            self.statistics['help'] += 1
            dataManage.save_statistics(self.statistics)
            await send_message(bot, event, mode, merge_reply, reply_text, reply_image, need_at, at_qq)
            return
        elif message.replace('查看', '').replace('查询', '') == '开关列表' or message.replace('查看', '').replace('查询',
                                                                                                        '') == '模块列表':
            if mode == 1:
                reply_image = BaseFunction.generate_module_list(group_id, self.groups[group_id])
            else:
                reply_text = '用户<' + name + '>模块开关情况如下：'
                reply_text += '\n输入“模块管理帮助”获取所有指令的详细说明'
                reply_text += '\n格式：”字段（操作指令）：状态“\n'
                reply_text += '\n是否开启ai（时不时自主回复）【开启/关闭智能回复】：' + bool_string(self.users[qq]['config']['ai'])
            self.statistics['help'] += 1
            dataManage.save_statistics(self.statistics)
            await send_message(bot, event, mode, merge_reply, reply_text, reply_image, need_at, at_qq)
            return

        # 如果是黑名单那么不会回复任何消息
        if blacklist != 0:
            return
        if message_len == 0:
            if be_at:
                reply_text = '我在'
                await send_message(bot, event, mode, merge_reply, reply_text, reply_image, need_at, at_qq)
            return

        # 如果被限制那么只回复at消息
        if mode == 1:
            if self.groups[group_id]['config']['limit']:
                if not be_at:
                    return

        # ===================================================================================
        # 处理上一次的消息
        if self.users[qq]['buffer']['id'] != 0:
            reset_buffer = True

            if self.users[qq]['buffer']['id'] == 1:  # 群欢迎语
                self.get_group(self.users[qq]['buffer']['buffer'])
                self.groups[self.users[qq]['buffer']['buffer']]['welcome'] = message_chain
                reply_text = self.bot_name + '已经记录下了~！'
                need_reply = True
                dataManage.save_group(self.users[qq]['buffer']['buffer'],
                                      self.groups[self.users[qq]['buffer']['buffer']])
            elif self.users[qq]['buffer']['id'] == 2:  # 清空屏蔽词
                if message == '是' or message == '确定' or message == '确认' or message == '可':
                    self.get_group(self.users[qq]['buffer']['buffer'])
                    self.groups[self.users[qq]['buffer']['buffer']]['prohibited_word'] = []
                    reply_text = self.bot_name + '已经帮您清空了'
                    need_reply = True
                    dataManage.save_group(self.users[qq]['buffer']['buffer'],
                                          self.groups[self.users[qq]['buffer']['buffer']])
                else:
                    reply_text = self.bot_name + '啊嘞？已为您取消清空。'
                    need_reply = True
            elif self.users[qq]['buffer']['id'] == 3:  # 覆盖分组
                if message == '是' or message == '确定' or message == '确认' or message == '可':
                    tmp_members = self.users[qq]['buffer']['buffer']['members']
                    tmp_name = self.users[qq]['buffer']['buffer']['name']
                    tmp_group_id = self.users[qq]['buffer']['buffer']['group_id']
                    self.get_group(tmp_group_id)
                    operator.del_group(tmp_group_id, self.groups[tmp_group_id], tmp_name)
                    operator.add_group(tmp_group_id, self.groups[tmp_group_id], tmp_name, tmp_members, qq)
                    reply_text = '已经覆盖~'
                    need_reply = True
                else:
                    reply_text = self.bot_name + '啊嘞？已为您取消覆盖分组。'
                    need_reply = True
            elif self.users[qq]['buffer']['id'] == 4:  # 清空分组
                if message == '是' or message == '确定' or message == '确认' or message == '可':
                    tmp_group_id = self.users[qq]['buffer']['buffer']['group_id']
                    self.get_group(tmp_group_id)
                    self.groups[tmp_group_id]['group'] = {}
                    dataManage.save_group(tmp_group_id, self.groups[tmp_group_id])
                    reply_text = '清空成功！'
                    need_reply = True
                else:
                    reply_text = self.bot_name + '啊嘞？已为您取消清空分组。'
                    need_reply = True
            elif self.users[qq]['buffer']['id'] == 5:  # 创建复杂回复的触发词
                if message != '*取消创建*':
                    self.users[qq]['buffer']['id'] = 6
                    self.users[qq]['buffer']['buffer'] = {
                        'group_id': self.users[qq]['buffer']['buffer'],
                        'key': message
                    }
                    dataManage.save_user(qq, self.users[qq])
                    reply_text = '触发词：' + message
                    reply_text += '\n小柒已为您记录下来了，请问你的回复内容是什么？（可以文字+图片，不可以包含艾特）'
                    reset_buffer = False
                else:
                    reply_text = '已为您取消创建'
                need_reply = True
            elif self.users[qq]['buffer']['id'] == 6:  # 创建复杂回复的回复内容
                if message != '*取消创建*':
                    self.users[qq]['buffer']['id'] = 7
                    self.users[qq]['buffer']['buffer']['reply'] = message_chain
                    dataManage.save_user(qq, self.users[qq])
                    reply_text += '小柒记录下来了，请问这条消息需要艾特谁吗（全体成员/分组/触发人/QQ号，这四种都是可以的哦~如果QQ号为0表示不艾特，如果不明白分组可以看“贡献者帮助”）？'
                    reset_buffer = False
                else:
                    reply_text = '已为您取消创建'
                need_reply = True
            elif self.users[qq]['buffer']['id'] == 7:  # 创建复杂回复的艾特对象
                message = message.replace('@', '').strip()
                if message != '*取消创建*':
                    if message == '全体成员':
                        self.users[qq]['buffer']['buffer']['at_type'] = 0  # 0表示艾特
                        self.users[qq]['buffer']['buffer']['at'] = -1
                    elif message == '触发人':
                        self.users[qq]['buffer']['buffer']['at_type'] = 0
                        self.users[qq]['buffer']['buffer']['at'] = 0
                    elif message.isdigit():
                        buffer_at = int(message)
                        if buffer_at > 0:
                            self.users[qq]['buffer']['buffer']['at_type'] = 0
                            self.users[qq]['buffer']['buffer']['at'] = buffer_at
                        else:
                            self.users[qq]['buffer']['buffer']['at_type'] = -1  # -1表示不艾特
                            self.users[qq]['buffer']['buffer']['at'] = 0
                    else:
                        self.users[qq]['buffer']['buffer']['at_type'] = 1  # 1表示艾特分组
                        self.users[qq]['buffer']['buffer']['at'] = message
                    self.get_group(self.users[qq]['buffer']['buffer']['group_id'])
                    group = self.groups[self.users[qq]['buffer']['buffer']['group_id']]
                    if not group['key_reply'].__contains__('complex'):
                        group['key_reply']['complex'] = {}
                    group['key_reply']['complex'][self.users[qq]['buffer']['buffer']['key']] = {
                        'reply': self.users[qq]['buffer']['buffer']['reply'],
                        'at': self.users[qq]['buffer']['buffer']['at'],
                        'at_type': self.users[qq]['buffer']['buffer']['at_type']
                    }
                    dataManage.save_group(self.users[qq]['buffer']['buffer']['group_id'], group)
                    reply_text = '创建成功~'
                else:
                    reply_text = '已为您取消创建'
                need_reply = True
            elif self.users[qq]['buffer']['id'] == 8:  # 自动审批暗号
                self.get_group(self.users[qq]['buffer']['buffer'])
                self.groups[self.users[qq]['buffer']['buffer']]['config']['pass'] = message
                reply_text = self.bot_name + '已经记录下了~！当前入群暗号：' + message
                need_reply = True
                dataManage.save_group(self.users[qq]['buffer']['buffer'],
                                      self.groups[self.users[qq]['buffer']['buffer']])
            elif self.users[qq]['buffer']['id'] == 9:  # XMU服务条款同意
                need_reply = True
                if message == '同意':
                    reply_text = '很高兴您订阅“厦大自动健康打卡”服务，请问您的厦大统一身份认证账号是什么？'
                    reset_buffer = False
                    self.users[qq]['buffer']['id'] = 10
                    self.users[qq]['buffer']['buffer'] = {
                        'account': '',
                        'password': ''
                    }
                    dataManage.save_user(qq, self.users[qq])
                else:
                    reply_text = '已取消为您取消订阅“厦大自动健康打卡”服务'
            elif self.users[qq]['buffer']['id'] == 10:  # XMU服务账号
                need_reply = True
                reply_text = '请问您的厦大统一身份认证密码是什么？（请再次确保您在私聊！）'
                reset_buffer = False
                self.users[qq]['buffer']['id'] = 11
                self.users[qq]['buffer']['buffer'] = {
                    'account': message,
                    'password': ''
                }
                dataManage.save_user(qq, self.users[qq])
            elif self.users[qq]['buffer']['id'] == 11:  # XMU服务密码
                need_reply = True
                reply_text = '好的~已为您记录下来了，将会在每天12:05自动打卡，并私聊告诉你打卡的结果，请确保有添加' + self.get_name() + '的好友'
                reply_text += '\n你可以通过输入“AsYNARTvgt”来退订此服务'

                password_byte = bytes(message, encoding="utf8")
                ciphertext = base64.b64encode(password_byte)

                xmu = dataManage.load_obj('lib/account')
                xmu[qq] = {
                    'account': self.users[qq]['buffer']['buffer']['account'],
                    'password': ciphertext
                }
                dataManage.save_obj(xmu, 'lib/account')
            elif self.users[qq]['buffer']['id'] == 12:  # 订阅定时全局禁言服务
                need_reply = True
                get_time = True
                value = {
                    'id': qq,
                    'hour1': 0,
                    'minute1': 0,
                    'hour2': 0,
                    'minute2': 0
                }
                list1 = message.replace('：', ':').split(' ')
                if len(list1) != 2:
                    get_time = False
                else:
                    list1_1 = list1[0].split(':')
                    list1_2 = list1[1].split(':')
                    if len(list1_1) != 2 or len(list1_2) != 2:
                        get_time = False
                    else:
                        list1_1[0] = time_pretreatment(list1_1[0])
                        list1_1[1] = time_pretreatment(list1_1[1])
                        list1_2[0] = time_pretreatment(list1_2[0])
                        list1_2[1] = time_pretreatment(list1_2[1])
                        if not list1_1[0].isdigit() or not list1_1[1].isdigit or not list1_2[0].isdigit() or not \
                        list1_2[1].isdigit:
                            get_time = False
                        else:
                            value['hour1'] = int(list1_1[0])
                            value['minute1'] = int(list1_1[1])
                            value['hour2'] = int(list1_2[0])
                            value['minute2'] = int(list1_2[1])
                            if not valid_time(value['hour1'], value['minute1']) or not valid_time(value['hour2'],
                                                                                                  value['minute2']):
                                get_time = False

                if not get_time:
                    if message != '取消':
                        reset_buffer = False
                        await bot.send(event, '这好像不是一个正确的格式，你可以输入“取消”来取消创建。请再次告诉我时间：')
                    else:
                        await bot.send(event, '已为您取消创建')
                else:
                    muteall_schedule = dataManage.load_obj('data/Function/muteall')  # 禁言计划
                    if value['hour1'] == value['hour2'] and value['minute1'] == value['minute2']:
                        reset_buffer = False
                        await bot.send(event, '这好像只有一分钟呢，你可以输入“取消”来取消创建。请再次告诉我时间：')
                    else:
                        muteall_schedule[group_id] = value
                        dataManage.save_obj(muteall_schedule, 'data/Function/muteall')
                        await bot.send(event, '创建成功！你可以输入“模块列表”来查看订阅的服务')

            if reset_buffer:
                self.users[qq]['buffer']['id'] = 0
                self.users[qq]['buffer']['buffer'] = None
                dataManage.save_user(qq, self.users[qq])
            if need_reply:
                await send_message(bot, event, mode, merge_reply, reply_text, reply_image, need_at, at_qq)
                return

        # ===================================================================================
        # 如果是群聊消息，并且具有小柒的操作权限，那么就可以进行退群和禁言的操作
        if mode == 1:
            if message_code == 'quit' or message_code == 'dismiss':
                if group_right < 2 or right < 3:
                    await bot.send(event, '再见啦~各位！我会想你们的!')

                    await bot.quit(group_id)
                    self.statistics['quit'] += 1
                    dataManage.save_statistics(self.statistics)
                    logManage.group_log(getNow.toString(), qq, group_id, event.sender.group.get_name(),
                                        message + '; 小柒退群！')

                    if master is not None:
                        await bot.send_friend_message(master.id, [
                            Plain('已退出群聊：' + str(group_id) + '！')
                        ])
                else:
                    reply_text = '权限不足，需要群管理或群主或者小柒的管理员'
                    await send_message(bot, event, mode, merge_reply, reply_text, reply_image, need_at, at_qq)
                return
            elif message_code == 'mute' or message_code == 'bot off':
                if not self.groups[group_id]['config']['mute']:
                    if group_right < 2 or right < 3:
                        self.groups[group_id]['config']['mute'] = True
                        dataManage.save_group(group_id, self.groups[group_id])
                        await bot.send(event, 'QAQ，那我闭嘴了')
                        self.statistics['mute'] += 1
                        dataManage.save_statistics(self.statistics)
                        logManage.group_log(getNow.toString(), qq, group_id, event.sender.group.get_name(),
                                            message + '; 小柒禁言！')

                        if master is not None:
                            await bot.send_friend_message(master.id, [
                                Plain('在群' + str(group_id) + '被禁言！')
                            ])
                    else:
                        reply_text = '权限不足，需要群管理或群主'
                        await send_message(bot, event, mode, merge_reply, reply_text, reply_image, need_at, at_qq)
                else:
                    reply_text = '小柒本来就被禁言了！'
                    await send_message(bot, event, mode, merge_reply, reply_text, reply_image, need_at, at_qq)
                return
            elif message_code == 'unmute' or message_code == 'bot on':
                if self.groups[group_id]['config']['mute']:
                    if group_right < 2 or right < 3:
                        self.groups[group_id]['config']['mute'] = False
                        dataManage.save_group(group_id, self.groups[group_id])
                        await bot.send(event, '呜呜呜，憋死我了，终于可以说话了')
                        self.statistics['unmute'] += 1
                        dataManage.save_statistics(self.statistics)
                        logManage.group_log(getNow.toString(), qq, group_id, event.sender.group.get_name(),
                                            message + '; 小柒解除禁言！')

                        if master is not None:
                            await bot.send_friend_message(master.id, [
                                Plain('在群' + str(group_id) + '解除禁言！')
                            ])
                    else:
                        reply_text = '权限不足，需要群管理或群主'
                        await send_message(bot, event, mode, merge_reply, reply_text, reply_image, need_at, at_qq)
                else:
                    reply_text = '本来就没有禁言哦~'
                    await send_message(bot, event, mode, merge_reply, reply_text, reply_image, need_at, at_qq)
                return
            elif message_code == 'limit on' or message == '开启限制模式':
                if group_right < 2 or right < 3:
                    if not self.groups[group_id]['config']['limit']:
                        self.groups[group_id]['config']['limit'] = True
                        dataManage.save_group(group_id, self.groups[group_id])
                        await bot.send(event, '限制模式已开启，指令需艾特才能回复。解禁指令也别忘记艾特哦~')
                    self.statistics['operate'] += 1
                    dataManage.save_statistics(self.statistics)
                    logManage.group_log(getNow.toString(), qq, group_id, event.sender.group.get_name(),
                                        message + '; 小柒开启限制！')
                else:
                    reply_text = '权限不足，需要群管理或群主或小柒的管理'
                    await send_message(bot, event, mode, merge_reply, reply_text, reply_image, need_at, at_qq)
                return
            elif message_code == 'limit off' or message == '关闭限制模式':
                if group_right < 2 or right < 3:
                    if self.groups[group_id]['config']['limit']:
                        self.groups[group_id]['config']['limit'] = False
                        dataManage.save_group(group_id, self.groups[group_id])
                        await bot.send(event, '从现在起，指令无需艾特也能回复~')
                    self.statistics['operate'] += 1
                    dataManage.save_statistics(self.statistics)
                    logManage.group_log(getNow.toString(), qq, group_id, event.sender.group.get_name(),
                                        message + '; 小柒解除限制！')
                else:
                    reply_text = '权限不足，需要群管理或群主或小柒的管理'
                    await send_message(bot, event, mode, merge_reply, reply_text, reply_image, need_at, at_qq)
                return

        # 如果被禁言那么直接返回
        if be_mute:
            return

        # 基本权限管理
        # if message_code[:9] == 'broadcast':
        #     if right == 0:
        #         temp = message_code[9:].strip() + '【全局广播内容无需回复】'
        #         group_list = await app.groupList()
        #         for i in group_list:
        #             print(i)
        #             await app.sendGroupMessage(i, MessageChain.create([
        #                 Plain(temp)
        #             ]))
        if message_code[:4] == 'send':
            if master is not None and len(message) > 5:
                await bot.send_friend_message(master.id, [
                    Plain(group_name + '<' + group_id + '>' + name + '(' + str(qq) + ')：' + message[5:].strip())
                ])
                reply_text = '已经报告给主人了~'
                await send_message(bot, event, mode, merge_reply, reply_text, reply_image, need_at, at_qq)

                self.statistics['operate'] += 1
                dataManage.save_statistics(self.statistics)
            return
        elif message_code == 'ai on' or message == '开启智能回复':
            if mode == 0 or mode == 2:
                if not self.users[qq]['config']['ai']:
                    self.users[qq]['config']['ai'] = True
                    dataManage.save_user(qq, self.users[qq])

                    self.statistics['operate'] += 1
                    dataManage.save_statistics(self.statistics)
                    reply_text = '已开启智能回复~'
                else:
                    reply_text = '智能回复本身就是开启的'
                await send_message(bot, event, mode, merge_reply, reply_text, reply_image, need_at, at_qq)
            elif mode == 1:  # 如果是群聊则需要有权限，才能够操作
                if group_right < 2 or right < 3:
                    if not self.groups[group_id]['config']['ai']:
                        self.groups[group_id]['config']['ai'] = True
                        dataManage.save_group(group_id, self.groups[group_id])

                        self.statistics['operate'] += 1
                        dataManage.save_statistics(self.statistics)
                        reply_text = '本群已开启艾特的智能回复~'
                    else:
                        reply_text = '本群本身就是开启艾特智能回复的'
                    await send_message(bot, event, mode, merge_reply, reply_text, reply_image, need_at, at_qq)
                else:
                    reply_text = '权限不足，需要群管理或群主'
                    await send_message(bot, event, mode, merge_reply, reply_text, reply_image, need_at, at_qq)
            return
        elif message_code == 'ai off' or message == '关闭智能回复':
            if mode == 0 or mode == 2:
                if self.users[qq]['config']['ai']:
                    self.users[qq]['config']['ai'] = False
                    dataManage.save_user(qq, self.users[qq])

                    self.statistics['operate'] += 1
                    dataManage.save_statistics(self.statistics)
                    reply_text = '已关闭智能回复~'
                else:
                    reply_text = '智能回复本身就是关闭的'
                await send_message(bot, event, mode, merge_reply, reply_text, reply_image, need_at, at_qq)
            elif mode == 1:
                if group_right < 2 or right < 3:
                    if self.groups[group_id]['config']['ai']:
                        self.groups[group_id]['config']['ai'] = False
                        dataManage.save_group(group_id, self.groups[group_id])

                        self.statistics['operate'] += 1
                        dataManage.save_statistics(self.statistics)
                        reply_text = '本群已关闭艾特的智能回复~'
                    else:
                        reply_text = '本群本身就是关闭艾特智能回复的'
                else:
                    reply_text = '权限不足，需要群管理或群主'
                await send_message(bot, event, mode, merge_reply, reply_text, reply_image, need_at, at_qq)
            return
        elif mode == 1:
            ans = await self.switch(bot, event, mode, message, message_code, group_id, right, group_right, qq)
            if ans is None:
                return

        # -----------------------------------------------------------------------------------
        # 闪照处理
        if mode == 1 and self.groups[group_id]['config']['flash']:
            for image in flash_image:
                await bot.send(event, image.as_image())

        # 禁言操作
        if mode == 1 and group_right != 2:
            if message[:2] == '禁言' and len(at_list) > 0:
                need_reply = True
                mute_seconds = 60 * 10

                message_plain = ''
                for i in plain_list:
                    message_plain += str(i)
                message_plain = message_plain.replace(' ', '').replace('个', '').strip()
                if len(message_plain) > 2:
                    mute_seconds = 0
                    sum_number = 0
                    temp_number = 0
                    valid = True

                    for index in range(len(message_plain[2:])):
                        char = message_plain[2:][index]
                        if char == '一':
                            temp_number = 1
                        elif char == '二' or char == '两':
                            temp_number = 2
                        elif char == '三':
                            temp_number = 3
                        elif char == '四':
                            temp_number = 4
                        elif char == '五':
                            temp_number = 5
                        elif char == '六':
                            temp_number = 6
                        elif char == '七':
                            temp_number = 7
                        elif char == '八':
                            temp_number = 8
                        elif char == '九':
                            temp_number = 9

                        elif char == '十':
                            if temp_number > 0:
                                sum_number += temp_number * 10
                                temp_number = 0
                            else:
                                sum_number += 10
                        elif char == '百':
                            sum_number += temp_number * 100
                            temp_number = 0
                        elif char == '千':
                            sum_number += temp_number * 1000
                            temp_number = 0
                        elif char == '万':
                            sum_number = sum_number * 10000 + temp_number * 10000
                            temp_number = 0

                        elif char == '天':
                            sum_number += temp_number
                            mute_seconds += sum_number * 24 * 3600
                            temp_number = 0
                            sum_number = 0
                        elif char == '小':
                            if message_plain[2:][index + 1] == '时':
                                sum_number += temp_number
                                mute_seconds += sum_number * 3600
                                temp_number = 0
                                sum_number = 0
                            else:
                                valid = False
                                break
                        elif char == '分':
                            if message_plain[2:][index + 1] == '钟':
                                sum_number += temp_number
                                mute_seconds += sum_number * 60
                                temp_number = 0
                                sum_number = 0
                            else:
                                valid = False
                                break
                        elif char == '秒':
                            sum_number += temp_number
                            mute_seconds += sum_number
                            temp_number = 0
                            sum_number = 0
                        elif char != '钟' and char != '时':
                            valid = False
                            break
                    if not valid and message_plain[2:].isdigit():
                        mute_seconds = int(message_plain[2:]) * 60
                    elif not valid:
                        mute_seconds = 60 * 10

                if mute_seconds < 60:
                    mute_seconds = 60
                if mute_seconds > 30 * 24 * 3600:
                    mute_seconds = 30 * 24 * 3600
                number = 0

                for qq in at_list:
                    if str(event.sender.group.permission) != 'Permission.Member':
                        if qq.target != bot.qq:
                            member = await bot.get_group_member(group_id, qq.target)
                            if member is not None:
                                if str(member.permission) == 'Permission.Member':
                                    await bot.mute(member_id=qq.target, target=group_id, time=mute_seconds)
                                    number += 1
                    else:
                        reply_text = '小柒无权禁言'
                        break
                if number > 0:
                    reply_text = '成功禁言' + str(number) + '人'
                elif reply_text == '':
                    reply_text = '啊嘞？好像全是管理员或群主呢'
            elif message[:4] == '解除禁言' and len(at_list) > 0:
                if str(event.sender.group.permission) != 'Permission.Member':
                    need_reply = True
                    number = 0
                    for qq in at_list:
                        member = await bot.get_group_member(group_id, qq.target)
                        if member is not None:
                            await bot.unmute(member_id=qq.target, target=group_id)
                            number += 1
                    reply_text = '成功解除' + str(number) + '人禁言'
            elif message == '开启全体禁言':
                if str(event.sender.group.permission) != 'Permission.Member':
                    await bot.mute_all(target=group_id)
                    need_reply = True
                    reply_text = '已开启全体禁言'
            elif message == '解除全体禁言' or message == '关闭全体禁言':
                if str(event.sender.group.permission) != 'Permission.Member':
                    await bot.unmute_all(target=group_id)
                    need_reply = True
                    reply_text = '已关闭全体禁言'

        # -----------------------------------------------------------------------------------
        # 定制功能
        if message == 'XKoTVtvG2P':
            need_reply = True
            reply_text = '欢迎订阅' + self.get_name() + '的“厦大自动健康打卡”服务，请确保你了解以下需知：'
            reply_text += '\n1.不得利用本软件进行瞒报，以此造成的责任应由使用者自行承担，如有前往其他城市，请及时手动登陆打卡系统更新相关信息'
            reply_text += '\n2.使用者的厦大账号密码，将会采用加密算法加密后存储到数据库，开发者可以使用特定的解密工具看到你的密码，但是我们保证不会如此做或者泄露你的密码，也不会向任何人透露加密密钥，信不信任由你自行决定。'
            reply_text += '\n3.请确保你目前在私聊告诉小柒密码，而不是在群聊之中，因此造成的损失应该由使用者自行承担'
            reply_text += '\n4.自动打卡不保证一直有效，或许接口更改，服务器忙等造成打卡失败，应配合辅导员的提醒自行检查。（由于学业原因不一定及时更新接口）'
            reply_text += '\n----------------------------'
            reply_text += '\n回复“同意”表示你同意以上服务条款，其余任何回复表示不同意'
            self.users[qq]['buffer']['id'] = 9
            dataManage.save_user(qq, self.users[qq])
        elif message == 'AsYNARTvgt':
            need_reply = True
            xmu = dataManage.load_obj('lib/account')
            if xmu.__contains__(qq):
                reply_text = '已为您取消订阅“厦大自动健康打卡”服务'
                del xmu[qq]
                dataManage.save_obj(xmu, 'lib/account')
            else:
                reply_text = '您没有订阅“厦大自动健康打卡”服务'

        # -----------------------------------------------------------------------------------
        # 通过名字唤醒
        if message == self.bot_name:
            reply_text = '我在！'
            need_reply = True
            self.statistics['awaken'] += 1
            dataManage.save_statistics(self.statistics)

        # 帮助内容
        if not need_reply:
            if message == '帮助' or message == '指令' or message == '菜单':
                reply_image = command.help_function()
                if mode == 1:
                    reply_text = '在群内输入“模块列表”查询各个模块开关状态'
                need_reply = True
            elif message == '打卡帮助':
                reply_image = command.help_clock()
                if mode == 0 or mode == 2:
                    reply_text = '这部分命令，只支持群聊哦~'
                need_reply = True
            elif message == '骰娘' or message == '骰娘帮助' or message == '骰娘指令':
                reply_image = command.help_thrower()
                if mode == 0 or mode == 2:
                    reply_text = '这部分命令，只支持群聊哦~'
                    reply_text += '\n目前因为框架更新骰娘未能及时迁移，如果你确保了解什么是骰娘可以使用小柒的妹妹399608601。' + \
                                  '小捌采用的是塔骰并且加入了全网共同黑名单。违规操作将会被所有塔骰拉黑。'
                else:
                    reply_text = '目前因为框架更新骰娘未能及时迁移，如果你确保了解什么是骰娘可以使用小柒的妹妹399608601。' + \
                                 '小捌采用的是塔骰并且加入了全网共同黑名单。违规操作将会被所有塔骰拉黑。'
                need_reply = True
            elif message == '塔罗牌帮助':
                reply_image = command.help_tarot()
                need_reply = True
            elif message == '游戏帮助' or message == '游戏指令':
                reply_image = command.help_game()
                reply_text = '游戏官方社区（906554784）'
                reply_text += '\n输入“游戏帮助2”查看下一页'
                if mode == 1:
                    if not self.groups[group_id]['config']['RPG']:
                        reply_text += '\n本群游戏模块为关闭状态，在群内输入“模块列表”查询各个模块开关状态'
                need_reply = True
            elif message == '游戏帮助2':
                reply_image = command.help_game2()
                need_reply = True
            elif message == '附魔查询' or message == '查询附魔':
                reply_image = command.enchanting()
                need_reply = True
            elif message == 'buff查询' or message == '查询buff':
                reply_image = command.buff()
                need_reply = True
            elif message == '游戏新手指南' or message == '新手指南':
                reply_image = command.help_game_novice()
                need_reply = True
            elif message == '模块管理帮助':
                reply_image = command.help_modular()
                reply_text = '您可以使用“模块列表”来查看开关状态'
                need_reply = True
            elif message == '部落冲突查询帮助' or message.lower() == 'coc帮助':
                reply_image = command.help_clash()
                need_reply = True
                if mode == 1:
                    if not self.groups[group_id]['config']['clash']:
                        reply_text = '本群部落冲突查询模块为关闭状态，在群内输入“模块列表”查询各个模块开关状态'

            if need_reply:
                self.statistics['help'] += 1
                dataManage.save_statistics(self.statistics)

        # 打卡&分组
        if not need_reply and mode == 1:
            if message[:4] == '加入分组':
                group_name = message[4:].strip()
                reply_text = operator.join_group(group_id, self.groups[group_id], group_name, qq)
                need_reply = True
            elif message[:4] == '退出分组':
                group_name = message[4:].strip()
                reply_text = operator.quit_group(group_id, self.groups[group_id], group_name, qq)
                need_reply = True

            elif message == '打卡列表':
                clock_data = self.clock.get_clock(group_id)
                need_reply = True
                if clock_data is None or len(clock_data) == 0:
                    reply_text = '暂无任何打卡~'
                else:
                    reply_text = '本群现有打卡如下：'
                    for key, value in clock_data.items():
                        reply_text += '\n' + key + '（' + str(len(value['member'])) + '人）'
            elif message[:4] == '添加打卡' and '@' not in message:
                name = message[4:].strip()
                need_reply = True
                if self.clock.insert_clock(group_id, name):
                    reply_text = '添加成功！群成员可以输入“加入打卡' + name + '”'
                else:
                    reply_text = '添加失败！已有同名的打卡计划或者打卡已满10个'
            elif message[:4] == '删除打卡':
                name = message[4:].strip()
                need_reply = True
                if self.clock.remove_clock(group_id, name):
                    reply_text = '删除成功！'
                else:
                    reply_text = '删除失败！没有该打卡'
            elif message[:4] == '查看打卡':
                name = message[4:].strip()
                clock_data = self.clock.get_clock_single(group_id, name)
                need_reply = True
                if clock_data is None:
                    reply_text = '不存在该打卡'
                else:
                    today = str(datetime.date.today())
                    reply_text = '打卡<' + name + '>情况如下：'
                    if clock_data['remind']['switch']:
                        reply_text += '\n提醒时间-%02d:%02d' % (
                        clock_data['remind']['hour'], clock_data['remind']['minute'])
                    if clock_data['summary']['switch']:
                        reply_text += '\n总结时间-%02d:%02d' % (
                        clock_data['summary']['hour'], clock_data['summary']['minute'])
                    reply_text += '\n参与打卡的成员：'
                    member_list_origin = await bot.member_list(group_id)
                    member_list = {}
                    for member in member_list_origin.data:
                        if not member_list.__contains__(member.id):
                            member_list[member.id] = member.member_name

                    for member in clock_data['member']:
                        if member_list.__contains__(member['qq']):
                            state = '已签' if today == member['last'] else '未签'
                            reply_text += '\n' + member_list[member['qq']] + '<' + str(
                                member['qq']) + '>：' + state + '（连续' + str(member['continuity']) + '天） '
            elif message[:4] == '加入打卡':
                name = message[4:].strip()
                ans = self.clock.join_clock(group_id, qq, name)
                need_reply = True
                if ans == 0:
                    reply_text = '加入打卡' + name + '成功\n你可以输入“打卡' + name + '”来进行打卡\n输入“退出打卡' + name + '”来退出'
                elif ans == 1:
                    reply_text = '不存在打卡' + name
                elif ans == 2:
                    reply_text = '你已在打卡' + name + '中'
                else:
                    reply_text = '达到人数上限（单个打卡30人）'
            elif message[:4] == '退出打卡':
                name = message[4:].strip()
                ans = self.clock.quit_clock(group_id, qq, name)
                need_reply = True
                if ans == 0:
                    reply_text = '退出打卡' + name + '成功'
                elif ans == 1:
                    reply_text = '不存在打卡' + name
                elif ans == 2:
                    reply_text = '你不在打卡' + name + '中'
            elif message[:2] == '打卡' and message_len > 2 and '@' not in message:
                name = message[2:].strip()
                ans = self.clock.sign(group_id, qq, name)
                need_at = True
                need_reply = True
                if ans >= 0:
                    reply_text = '打卡' + name + '成功！已经连续打卡' + str(ans) + '天'
                elif ans == -1:
                    need_reply = False
                    need_at = False
                    reply_text = '不存在打卡<' + name + '>'
                elif ans == -2:
                    reply_text = '你没有加入打卡<' + name + '>'
                elif ans == -3:
                    reply_text = '你今天已经打过卡了~'

            if need_reply:
                self.statistics['clock_activity'] += 1
                dataManage.save_statistics(self.statistics)
                logManage.group_log(getNow.toString(), qq, group_id, event.sender.group.get_name(),
                                    message + "; 执行结果：" + reply_text)

        # 基础功能
        if not need_reply:
            if message[:2] == '天气':  # 开始的天气
                tmp = message[2:].strip()
                if tmp[0] != '#':
                    reply_text = weather.getWeather(tmp)
                    need_at = False
                    need_reply = True
            elif message[-2:] == '天气':  # 结尾的天气
                tmp = message[:-2].strip()
                if '这鬼' not in tmp and tmp[0] != '#':  # 语言优化处理（避免“这鬼天气”的语气词）
                    reply_text = weather.getWeather(tmp)
                    need_at = False
                    need_reply = True
            elif message[-3:] == '的天气':  # 结尾的天气
                tmp = message[:-3].strip()
                if tmp[0] != '#':
                    reply_text = weather.getWeather(tmp)
                    need_at = False
                    need_reply = True

            elif message == '色子' or message == '骰子':
                reply_text = BaseFunction.dice()
                need_at = True
                need_reply = True
            elif message == '抛硬币' or message == '硬币':
                reply_text = BaseFunction.coin()
                need_at = True
                need_reply = True
            elif message == '运势':
                reply_text = self.luck.get_luck(qq)
                need_at = True
                need_reply = True

            elif message == '微博热搜':
                reply_text = BaseFunction.getHot()
                need_reply = True
            elif message == '百度热搜':
                reply_text = baidu.getHot()
                need_reply = True

            elif message == '四级词汇' or message == '四级单词' or message == '4级词汇' or message == '4级单词':
                vocabularyNumber = 1
                reply_text = BaseFunction.get_vocabulary4(vocabularyNumber)
                need_reply = True
            elif message[:5] == '四级词汇 ' or message[:5] == '四级单词 ' or message[:5] == '4级词汇 ' or message[:5] == '4级单词 ':
                vocabularyNumber = int(message[5:].strip())
                if vocabularyNumber <= 0:
                    vocabularyNumber = 1
                reply_text = BaseFunction.get_vocabulary4(vocabularyNumber)
                need_reply = True
            elif message == '六级词汇' or message == '六级单词' or message == '6级词汇' or message == '6级单词':
                vocabularyNumber = 1
                reply_text = BaseFunction.get_vocabulary6(vocabularyNumber)
                need_reply = True
            elif message[:5] == '六级词汇 ' or message[:5] == '六级单词 ' or message[:5] == '6级词汇 ' or message[:5] == '6级单词 ':
                vocabularyNumber = int(message[5:].strip())
                if vocabularyNumber <= 0:
                    vocabularyNumber = 1
                reply_text = BaseFunction.get_vocabulary6(vocabularyNumber)
                need_reply = True

            elif message == '拾取漂流瓶' or message == '捡漂流瓶' or message == '捞漂流瓶':
                reply_text = self.bottle.pick()
                need_reply = True
            elif message[:4] == '扔漂流瓶' and message_len > 4:
                text = message[4:].strip()
                if len(text) > 0:
                    reply_text = self.bottle.throw(qq, text)
                    need_reply = True

            elif message[:5] == '随机字符串' and message_len > 5:
                text = message[5:].strip()
                if text.isdigit():
                    need_reply = True
                    reply_text = BaseFunction.random_char(int(text))

            if need_reply:
                self.statistics['base_function'] += 1
                dataManage.save_statistics(self.statistics)

        # 文摘、脏话、情话
        if not need_reply:
            if message == '文摘':
                reply_text = talk.poem()
                need_reply = True
            elif message == '情话':
                reply_text = talk.loveTalk()
                need_reply = True
            elif message == '骂我一句' or message == '骂我' or message == '再骂' or message == '你再骂' or message == '脏话':
                if mode == 0 or mode == 2 or (mode == 1 and self.groups[group_id]['config']['curse']):
                    reply_text = talk.swear()
                    need_reply = True

            if need_reply:
                self.statistics['talk'] += 1
                dataManage.save_statistics(self.statistics)

        # 涩图
        if not need_reply and mode == 1 and self.groups[group_id]['config']['image']:
            if message == '涩图':
                await bot.send(event, '该功能并未优化暂时被锁定，不开放。具体开放日期待定，是开发情况而定。')
                need_reply = True

            if need_reply:
                self.statistics['image_search'] += 1
                dataManage.save_statistics(self.statistics)

        # 指令
        if not need_reply:
            if 0 < message_code_len < 1000 and message_code[0].isalnum():
                if mode == 1:
                    (reply_text, need_at, reply_image) = command.function(message_code,
                                                                          qq,
                                                                          name,
                                                                          group_id,
                                                                          mode,
                                                                          self.config,
                                                                          self.groups[group_id],
                                                                          self.statistics)
                else:
                    (reply_text, need_at, reply_image) = command.function(message_code,
                                                                          qq,
                                                                          name,
                                                                          group_id,
                                                                          mode,
                                                                          self.config,
                                                                          self.users[qq],
                                                                          self.statistics)
                if len(key_allow) == 0 and reply_text.startswith('未知指令'):
                    need_reply = False
                else:
                    need_reply = True

                if reply_text == '*运势*':
                    reply_text = self.luck.get_luck(qq)
                    need_at = True

            if need_reply:
                self.statistics['command'] += 1
                dataManage.save_statistics(self.statistics)

        # -----------------------------------------------------------------------------------
        # 管理员操作
        if not need_reply:
            if not need_reply:
                if mode == 1:
                    (need_reply, need_at, reply_text, reply_image) = await operator.administrator_operation(
                        bot,
                        event,
                        message,
                        qq,
                        name,
                        group_id,
                        mode,
                        self.config,
                        self.groups[group_id],
                        self.statistics,
                        right,
                        group_right)
                else:
                    (need_reply, need_at, reply_text, reply_image) = await operator.administrator_operation(
                        bot,
                        event,
                        message,
                        qq,
                        name,
                        group_id,
                        mode,
                        self.config,
                        self.users[qq],
                        self.statistics,
                        right,
                        group_right)

            if need_reply:
                self.statistics['operate'] += 1
                dataManage.save_statistics(self.statistics)

        # -----------------------------------------------------------------------------------
        # rpg游戏
        if not need_reply:
            if mode == 1:
                limit = self.groups[group_id]['config']['limit_RPG']
                RPG = self.groups[group_id]['config']['RPG']
            else:
                limit = False
                RPG = True

            if RPG:
                (need_reply, reply_text, reply_image) = self.rpg.handle(
                    message,
                    qq,
                    name,
                    self.get_user(qq),
                    self.config,
                    be_at,
                    limit
                )

                if need_reply:
                    self.statistics['game'] += 1
                    dataManage.save_statistics(self.statistics)

        # -----------------------------------------------------------------------------------
        # 部落冲突
        if not need_reply and mode == 1 and self.groups[group_id]['config']['clash']:
            need_reply, reply_text, reply_image = await self.clash.handle(bot, event, message, group_id, qq,
                                                                          self.groups[group_id],
                                                                          self.users[qq])
            if need_reply:
                merge_reply = True

            if need_reply:
                self.statistics['clash'] += 1
                dataManage.save_statistics(self.statistics)
        if not need_reply and mode == 0 and message.startswith('coc'):
            need_reply = True
            reply_text = '暂不支持私聊查询，请在群聊内查询。后续会慢慢支持私聊查询。'

        # -----------------------------------------------------------------------------------
        # 群自己设定的关键词回复
        if not need_reply and mode == 1 and self.groups[group_id]['config']['autonomous_reply']:
            (need_reply, reply_text, reply_image, at_qq, need_at, need_complex_reply, complex_reply,
             complex_at) = keyReply.reply(
                message,
                group_id,
                self.groups[group_id],
                self.statistics)

            if need_reply:
                self.statistics['key_reply'] += 1
                dataManage.save_statistics(self.statistics)

        # -----------------------------------------------------------------------------------
        # 自动加一
        if not need_reply and mode == 1 and self.groups[group_id]['config']['repeat']:
            if not self.message_tmp.__contains__(group_id):
                self.message_tmp[group_id] = message_chain
            else:
                reply_chain = self.message_tmp[group_id]
                tmp = str(reply_chain)
                self.message_tmp[group_id] = message_chain  # 将记录的上一次的消息更改为这次收到的消息
                if 'xml' not in tmp and tmp[0] != '[' and tmp[-1] != ']':
                    if tmp == message and tmp != self.last_reply:
                        await bot.send(event, message_chain)
                        need_reply = True
                        self.last_reply = tmp

            if need_reply:
                self.statistics['auto_repeat'] += 1
                dataManage.save_statistics(self.statistics)

        # 智能回复
        if not need_reply:
            if mode == 1:
                ai = self.groups[group_id]['config']['ai']
            else:
                ai = self.users[qq]['config']['ai']

            if ai:
                (need_reply, reply_text, reply_image, at_qq, need_at) = autoReply.reply(
                    message,
                    be_at,
                    self.config,
                    self.statistics,
                    name,
                    group_id,
                    qq,
                    mode)
                if need_reply:
                    self.statistics['auto_reply'] += 1
                    dataManage.save_statistics(self.statistics)

                    if mode == 1:
                        for key in self.groups[group_id]['prohibited_word']:
                            if key in reply_text:
                                reply_text = '【神经网络回复内容包含群内设置的屏蔽词，已自动和谐】'
                                break

        if need_reply:
            self.statistics['message'] += 1
            dataManage.save_statistics(self.statistics)
            if not need_complex_reply:  # 非复杂回复
                if reply_text != '':
                    self.last_reply = reply_text

                await send_message(bot, event, mode, merge_reply, reply_text, reply_image, need_at, at_qq)
            else:
                await send_complex_message(bot, event, mode, complex_reply, complex_at)

    async def new_friend(self, bot, event):
        self.config = dataManage.read_config()
        master = await bot.get_friend(self.config['master'])
        blacklist = self.get_blacklist(event.from_id, 0)

        if blacklist != 0:
            if master is not None:
                await bot.send_friend_message(self.config['master'],
                                              '有新的好友申请<' + event.nick + '>(' + str(event.from_id) + ')！已拒绝，原因：黑名单')

            await bot.decline(event)
            return

        if master is not None:
            await bot.send_friend_message(self.config['master'],
                                          '有新的好友申请<' + event.nick + '>(' + str(event.from_id) + ')！')

        await bot.allow(event)
        qq = event.from_id
        name = event.nick
        member = await bot.get_friend(qq)
        if member is not None:
            reply = '你好呀！' + name + '\n'
            reply += '小柒的快速上手指南：\n'
            reply += '可以通过输入“帮助”来获取所有的指令帮助，请仔细阅读其中的内容！！\n'
            reply += '可以通过输入“骰娘帮助”来获取所有的骰娘指令帮助\n\n'
            reply += '小柒的功能是分模块的，按需开启，可以在群内输入“模块列表”查询\n'
            reply += '如果有任何疑问可以加小柒的官方Q群：479504567，在群聊里可以告诉主人解除黑名单，以及获取到管理员权限解锁一些新功能\n\n'
            reply += '特别申明：\n'
            reply += '1.不要将小柒踢出任何群聊，或者在任何群聊禁言小柒，这些都有专门的指令代替！！！如果直接踢出，踢出人和群将会无理由黑名单，禁言视情况（频繁程度）而定\n'
            reply += '2.不要对机器人搞黄色，对机器人搞黄色你是有多饥渴？'
            await bot.send_friend_message(qq, reply)

            self.statistics['new_friend'] += 1
            dataManage.save_statistics(self.statistics)

    async def new_group(self, bot, event):
        self.config = dataManage.read_config()
        master = await bot.get_friend(self.config['master'])

        blacklist = self.get_blacklist(event.from_id, event.group_id)

        if blacklist != 0:
            await bot.send_friend_message(self.config['master'],
                                          '有新的群申请<' + event.group_name + '>(' + str(event.group_id) + ')！已拒绝，原因：黑名单')
            await bot.decline(event)
            return

        qq = event.from_id
        name = event.nick
        if master is not None:
            await bot.send_friend_message(self.config['master'], '有新的群申请<' + event.group_name + '>(' + str(
                event.group_id) + ')！\n邀请人：<' + name + '>(' + str(qq) + ')')

        # await bot.allow(event)
        member = await bot.get_friend(qq)
        if member is not None:
            await bot.send_friend_message(qq, '暂时不接受群邀请，请前往官方群（479504567）申请')

        self.statistics['new_group'] += 1
        dataManage.save_statistics(self.statistics)

    async def join_group(self, bot, event):
        if event.invitor is not None:
            name = event.invitor['memberName']
            qq = event.invitor['id']
            reply = '已加入群，邀请人：<' + name + '>(' + str(qq) + ')' + '\n'
            reply += '小柒的快速上手指南：\n'
            reply += '可以通过输入“帮助”来获取所有的指令帮助，请仔细阅读其中的内容！！\n'
            reply += '可以通过输入“骰娘帮助”来获取所有的骰娘指令帮助\n\n'
            reply += '小柒的功能是分模块的，按需开启，可以在群内输入“模块列表”查询\n'
            reply += '如果有任何疑问可以加小柒的官方Q群：479504567，在群聊里可以告诉主人解除黑名单，以及获取到管理员权限解锁一些新功能\n\n'
            reply += '特别申明：\n'
            reply += '1.不要将小柒踢出任何群聊，或者在任何群聊禁言小柒，这些都有专门的指令代替！！！如果直接踢出，踢出人和群将会无理由黑名单，禁言视情况（频繁程度）而定\n'
            reply += '2.不要对机器人搞黄色，对机器人搞黄色你是有多饥渴？'
            reply += '3.如果群主或管理员对该机器人有疑问请问邀请人，或者在群里发送“*quit”指令让机器人退群，星号不可以省略'
            await bot.send_group_message(event.group.id, reply)
        else:
            reply = '已加入群<' + event.group.name + '>'
            reply += '1.如果群主或管理员对该机器人有疑问请问邀请人，或者在群里发送“*quit”指令让机器人退群，星号不可以省略'
            await bot.send_group_message(event.group.id, reply)

    async def nudge(self, bot, event):
        self.get_group(event.subject.id)
        if self.groups[event.subject.id]['config']['mute']:
            return
        if not self.groups[event.subject.id]['config']['nudge']:
            return
        if self.get_blacklist(0, event.subject.id) != 0:
            return

        if event.target == bot.qq:
            statistics = dataManage.read_statistics()
            statistics['nudge'] += 1
            dataManage.save_statistics(statistics)

            if str(event.subject.kind) == 'Group':
                rand = random.randint(0, 26)
                if rand == 0:
                    reply_image = 'data/AutoReply/Nudge/打.gif'
                    await bot.send_group_message(event.subject.id,
                                                 [Plain('你再戳？你再戳？'), await Image.from_local(filename=reply_image)])
                elif rand == 1:
                    reply_image = 'data/AutoReply/Nudge/质疑.jpg'
                    await bot.send_group_message(event.subject.id, [await Image.from_local(filename=reply_image)])
                elif rand == 2:
                    reply_image = 'data/AutoReply/Nudge/过分.jpg'
                    await bot.send_group_message(event.subject.id,
                                                 [Plain('别戳了'), await Image.from_local(filename=reply_image)])
                elif rand == 3:
                    reply_image = 'data/AutoReply/Nudge/乖巧.jpg'
                    await bot.send_group_message(event.subject.id,
                                                 [Plain('放过我吧'), await Image.from_local(filename=reply_image)])
                elif rand == 4:
                    reply_image = 'data/AutoReply/Nudge/无语.jpg'
                    await bot.send_group_message(event.subject.id, [await Image.from_local(filename=reply_image)])
                elif rand == 5:
                    await bot.send_group_message(event.subject.id, '你再戳我就哭给你看，嘤嘤嘤~')
                elif rand == 6:
                    reply_image = 'data/AutoReply/Nudge/委屈2.jpg'
                    await bot.send_group_message(event.subject.id,
                                                 [Plain('别戳了呜呜'), await Image.from_local(filename=reply_image)])
                elif rand == 7:
                    reply_image = 'data/AutoReply/Nudge/上头.png'
                    await bot.send_group_message(event.subject.id,
                                                 [Plain('你是不是戳上头了'), await Image.from_local(filename=reply_image)])
                elif rand == 8:
                    reply_image = 'data/AutoReply/Nudge/质疑2.gif'
                    await bot.send_group_message(event.subject.id,
                                                 [Plain('为什么戳我'), await Image.from_local(filename=reply_image)])
                elif rand == 9:
                    reply_image = 'data/AutoReply/Nudge/委屈.jpg'
                    await bot.send_group_message(event.subject.id,
                                                 [Plain('别戳了呜呜'), await Image.from_local(filename=reply_image)])
                elif rand == 10:
                    reply_image = 'data/AutoReply/Nudge/不许戳.jpg'
                    await bot.send_group_message(event.subject.id,
                                                 [Plain('不许戳'), await Image.from_local(filename=reply_image)])
                elif rand == 11:
                    reply_image = 'data/AutoReply/Nudge/委屈3.jpg'
                    await bot.send_group_message(event.subject.id, [await Image.from_local(filename=reply_image)])
                elif rand == 12:
                    reply_image = 'data/AutoReply/Nudge/不开心.jpg'
                    await bot.send_group_message(event.subject.id, [await Image.from_local(filename=reply_image)])
                elif rand == 13:
                    reply_image = 'data/AutoReply/Nudge/不开心2.jpg'
                    await bot.send_group_message(event.subject.id,
                                                 [Plain('不可以再戳了'), await Image.from_local(filename=reply_image)])
                elif rand == 14:
                    reply_image = 'data/AutoReply/Nudge/无语2.jpg'
                    await bot.send_group_message(event.subject.id, [await Image.from_local(filename=reply_image)])
                elif rand == 15:
                    reply_image = 'data/AutoReply/Nudge/无语3.bmp'
                    await bot.send_group_message(event.subject.id, [await Image.from_local(filename=reply_image)])
                elif rand == 16:
                    reply_image = 'data/AutoReply/Nudge/哭.bmp'
                    await bot.send_group_message(event.subject.id,
                                                 [Plain('不可以做这种事情哦~'), await Image.from_local(filename=reply_image)])
                elif rand == 17:
                    reply_image = 'data/AutoReply/Nudge/别戳了.bmp'
                    await bot.send_group_message(event.subject.id,
                                                 [Plain('不可以再戳了~'), await Image.from_local(filename=reply_image)])
                elif rand == 18:
                    reply_image = 'data/AutoReply/Nudge/质疑3.bmp'
                    await bot.send_group_message(event.subject.id,
                                                 [Plain('你再戳你是笨蛋'), await Image.from_local(filename=reply_image)])
                elif rand == 19:
                    reply_image = 'data/AutoReply/Nudge/骂骂咧咧.png'
                    await bot.send_group_message(event.subject.id, [await Image.from_local(filename=reply_image)])
                elif rand == 20:
                    reply_image = 'data/AutoReply/Nudge/质疑4.bmp'
                    await bot.send_group_message(event.subject.id,
                                                 [Plain('真够无聊的呢'), await Image.from_local(filename=reply_image)])
                elif rand == 21:
                    reply_image = 'data/AutoReply/Nudge/打2.jpg'
                    await bot.send_group_message(event.subject.id,
                                                 [Plain('突死你'), await Image.from_local(filename=reply_image)])
                elif rand == 22:
                    reply_image = 'data/AutoReply/Nudge/无语4.gif'
                    await bot.send_group_message(event.subject.id, [await Image.from_local(filename=reply_image)])
                elif rand == 23:
                    reply_image = 'data/AutoReply/Nudge/乖巧2.jpg'
                    await bot.send_group_message(event.subject.id, [await Image.from_local(filename=reply_image)])
                elif rand == 24:
                    reply_image = 'data/AutoReply/Nudge/哭2.jpg'
                    await bot.send_group_message(event.subject.id, [await Image.from_local(filename=reply_image)])
                else:
                    await bot.send_group_message(event.subject.id, '别戳啦~')
            elif str(event.subject.kind) == 'Friend':
                await bot.send_friend_message(event.from_id, '别戳啦~')

    async def kick(self, bot, event):
        self.config = dataManage.read_config()
        master = await bot.get_friend(self.config['master'])

        if master is not None:
            await bot.send_friend_message(self.config['master'],
                                          '被踢出群<' + event.group.get_name() + '>(' + str(event.group.id) + ')！')

        self.statistics['kick'] += 1
        dataManage.save_statistics(self.statistics)

    async def join(self, bot, event):
        self.get_group(event.group.id)
        if self.groups[event.group.id]['config']['welcome']:
            welcome = self.groups[event.group.id]['welcome']
            if welcome is None:
                self.groups[event.group.id]['config']['welcome'] = False
                dataManage.save_group(event.group.id, self.groups[event.group.id])
                return

            welcome.insert(0, At(event.member.id))
            await bot.send_group_message(event.group.id, welcome)

            logManage.group_log(getNow.toString(), event.member.id, event.group.id, event.group.get_name(), '入群欢迎')

    async def request_group(self, bot, event):
        self.get_group(event.group_id)
        if self.groups[event.group_id]['config']['member_wather']:
            reply = '有新的群申请~'
            reply += '\n申请人：' + event.nick
            reply += '\n申请人QQ：' + str(event.from_id)
            reply += '\n申请信息：\n' + event.message
            await bot.send_group_message(event.group_id, reply)
        if self.groups[event.group_id]['config']['automatic']:
            if event.message.strip() == self.groups[event.group_id]['config']['pass']:
                await bot.allow(event)

    async def leave_group(self, bot, event):
        self.get_group(event.group.id)
        if self.groups[event.group.id]['config']['member_wather']:
            reply = '此刻我们失去了一位成员：' + event.member.member_name + '（' + str(event.member.id) + '）'
            await bot.send_group_message(event.group.id, reply)

    async def kick_group(self, bot, event):
        self.get_group(event.group.id)
        if self.groups[event.group.id]['config']['member_wather']:
            reply = '管理员<' + event.operator.member_name + '>踢出成员<' + event.member.member_name + '（' + str(
                event.member.id) + '）>'
            await bot.send_group_message(event.group.id, reply)

    async def member_change(self, bot, event):
        self.get_group(event.group.id)
        if self.groups[event.group.id]['config']['member_wather']:
            reply = '有一成员（' + str(event.member.id) + '）修改了群名片'
            reply += '\n原始名字：' + event.origin
            if event.origin == '':
                reply += event.member.member_name + '（QQ昵称）'
            reply += '\n新名字：' + event.current
            if event.current == '':
                reply += event.member.member_name + '（QQ昵称）'
            await bot.send_group_message(event.group.id, reply)

    async def group_recall_message(self, bot, event):
        self.get_group(event.group.id)
        if self.groups[event.group.id]['config']['revoke']:
            if event.author_id == event.operator.id:
                message = await bot.message_from_id(event.message_id)
                message_chain = message.message_chain
                message_chain.insert(0, Plain('成员<%d>试图撤回%s的消息：\n------------\n' % (event.author_id, event.time)))
                await bot.send_group_message(event.group.id, message_chain)
            else:
                print('非自己撤回')
