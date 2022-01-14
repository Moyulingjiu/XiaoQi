from mirai import Mirai, WebSocketAdapter
from mirai import FriendMessage, GroupMessage, TempMessage
from mirai import Plain, At, AtAll, Face
from mirai.models.events import MemberJoinEvent, NewFriendRequestEvent, BotLeaveEventKick, \
    BotInvitedJoinGroupRequestEvent
from mirai.models.events import NudgeEvent

# =============================================================
# 需求类
import asyncio
import datetime
import base64
import os
import time

# =============================================================
# 附加功能类
from plugins import getNow
from plugins import logManage
from plugins import dataManage
from plugins import Clock
from plugins import RPG


# =============================================================
# 主程序
async def new_day(bot):
    today = str(datetime.date.today())
    reply = '已经重置打卡日期到' + today

    logManage.log(getNow.toString(), '已重置打卡日期到：' + today)

    print('开始重置统计信息')
    statistics = dataManage.read_statistics()  # 读取统计信息
    reply += '\n统计信息如下：'
    reply += '\n被踢出群数：' + str(statistics['kick'])
    reply += '\n退群数：' + str(statistics['quit'])
    reply += '\n禁言次数：' + str(statistics['mute'])
    reply += '\n解除禁言次数：' + str(statistics['unmute'])
    reply += '\n唤醒次数：' + str(statistics['awaken'])
    reply += '\n帮助文档获取次数：' + str(statistics['help'])
    reply += '\n基础功能调用次数：' + str(statistics['base_function'])
    reply += '\ntalk模块调用次数：' + str(statistics['talk'])
    reply += '\nclock_activity模块调用次数：' + str(statistics['clock_activity'])
    reply += '\nimage_search模块调用次数：' + str(statistics['image_search'])
    reply += '\ncommand模块调用次数：' + str(statistics['command'])
    reply += '\noperate模块调用次数：' + str(statistics['operate'])
    reply += '\ngame模块调用次数：' + str(statistics['game'])
    reply += '\n自动加一次数：' + str(statistics['auto_repeat'])
    reply += '\n自主回复次数：' + str(statistics['auto_reply'])
    reply += '\n部落冲突调用次数：' + str(statistics['clash'])
    reply += '\n新好友：' + str(statistics['new_friend'])
    reply += '\n新群：' + str(statistics['new_group'])
    reply += '\n戳一戳：' + str(statistics['nudge'])
    reply += '\n发送消息：' + str(statistics['message'])

    statistics['kick'] = 0
    statistics['quit'] = 0
    statistics['mute'] = 0
    statistics['unmute'] = 0
    statistics['new_friend'] = 0
    statistics['new_group'] = 0

    statistics['awaken'] = 0
    statistics['help'] = 0
    statistics['base_function'] = 0
    statistics['talk'] = 0
    statistics['clock_activity'] = 0
    statistics['image_search'] = 0
    statistics['command'] = 0
    statistics['operate'] = 0
    statistics['game'] = 0
    statistics['auto_repeat'] = 0
    statistics['auto_reply'] = 0
    statistics['clash'] = 0
    statistics['nudge'] = 0
    statistics['message'] = 0
    dataManage.save_statistics(statistics)

    config = dataManage.read_config()
    for group_id in config['test_group']:
        await bot.send_group_message(group_id, reply)

    return


async def reset_clock(bot):
    today = str(datetime.date.today())
    clock = dataManage.read_clock()
    del_key = []
    for group_id, clock_dict in clock.items():
        if 'int' not in str(type(group_id)):
            del_key.append(group_id)
            continue
        member_list_origin = await bot.member_list(group_id)
        member_list = {}
        for member in member_list_origin.data:
            if not member_list.__contains__(member.id):
                member_list[member.id] = member.member_name

        for name, members in clock_dict.items():
            del_member = []
            at_member = []
            for member in members['member']:
                if not member_list.__contains__(member['qq']):
                    del_member.append(member)
                elif not Clock.is_yesterday(member['last']):
                    at_member.append(member['qq'])
            message = []
            if len(at_member) != 0:
                message.append(Plain('打卡<' + name + '>昨日未打卡的人公示：'))
                for qq in at_member:
                    message.append(At(qq))
                message.append(Plain('\n'))
                await bot.send_group_message(group_id, message)
            else:
                message.append(Plain('打卡<' + name + '>昨日所有人都完成了打卡'))
                await bot.send_group_message(group_id, message)

            for member in del_member:
                clock[group_id][name]['member'].remove(member)
            dataManage.save_clock(clock)

    for key in del_key:
        if key != 923691404:
            del clock[key]
    dataManage.save_clock(clock)


async def clock_check(bot, hour, minute):
    today = str(datetime.date.today())
    clock = dataManage.read_clock()
    del_key = []
    for group_id, clock_dict in clock.items():
        if 'int' not in str(type(group_id)):
            del_key.append(group_id)
            continue
        group = await bot.get_group(group_id)
        if group is None:
            del_key.append(group_id)
            continue
        member_list_origin = await bot.member_list(group_id)
        if member_list_origin is None:
            del_key.append(group_id)
            continue

        member_list = {}
        for member in member_list_origin.data:
            if not member_list.__contains__(member.id):
                member_list[member.id] = member.member_name

        for name, members in clock_dict.items():
            # 如果需要提醒才进行提醒
            if members['remind']['hour'] and hour == members['remind']['hour'] and minute == members['remind'][
                'minute']:
                del_member = []
                at_member = []
                for member in members['member']:
                    if not member_list.__contains__(member['qq']):
                        del_member.append(member)
                    elif member['last'] != today:
                        at_member.append(member['qq'])
                message = []
                if len(at_member) != 0:
                    message.append(Plain('打卡<' + name + '>还未打卡的人：'))
                    for qq in at_member:
                        message.append(At(qq))
                    message.append(Plain('\n记得打卡呀~'))
                    await bot.send_group_message(group_id, message)

                for member in del_member:
                    clock[group_id][name]['member'].remove(member)
                dataManage.save_clock(clock)

    for key in del_key:
        if key != 923691404:
            del clock[key]
    dataManage.save_clock(clock)


async def static_message(bot):
    config = dataManage.read_config()
    master = config['master']
    text = '当前版本：' + config['version']
    text += '\n' + config['name'] + '仍然在稳定运行'
    await bot.send_friend_message(master, text)
    return


async def daily_health_report(bot):
    xmu = dataManage.load_obj('lib/account')
    for key, value in xmu.items():
        member = await bot.get_friend(key)
        with open('report_setting.json', 'w', encoding='utf8') as f:
            contains = '''{
    "form_data": "{\\"formData\\":[{\\"name\\":\\"select_1582538796361\\",\\"title\\":\\"\\\\u4ECA\\\\u65E5\\\\u4F53\\\\u6E29 Body temperature today \\\\uFF08\\\\u2103\\\\uFF09\\",\\"value\\":{\\"stringValue\\":\\"37.3\\\\u4EE5\\\\u4E0B Below 37.3 degree celsius\\"},\\"hide\\":false},{\\"name\\":\\"select_1582538846920\\",\\"title\\":\\"\\\\u662F\\\\u5426\\\\u51FA\\\\u73B0\\\\u53D1\\\\u70ED\\\\u6216\\\\u54B3\\\\u55FD\\\\u6216\\\\u80F8\\\\u95F7\\\\u6216\\\\u547C\\\\u5438\\\\u56F0\\\\u96BE\\\\u7B49\\\\u75C7\\\\u72B6\\\\uFF1FDo you have sypmtoms such as fever, coughing, chest tightness or breath difficulties?\\",\\"value\\":{\\"stringValue\\":\\"\\\\u5426 No\\"},\\"hide\\":false},{\\"name\\":\\"select_1584240106785\\",\\"title\\":\\"\\\\u5B66\\\\u751F\\\\u672C\\\\u4EBA\\\\u662F\\\\u5426\\\\u586B\\\\u5199\\",\\"value\\":{\\"stringValue\\":\\"\\\\u662F\\"},\\"hide\\":false},{\\"name\\":\\"select_1582538939790\\",\\"title\\":\\"Can you hereby declare that all the information provided is all true and accurate and there is no concealment, false information or omission. \\\\u672C\\\\u4EBA\\\\u662F\\\\u5426\\\\u627F\\\\u8BFA\\\\u6240\\\\u586B\\\\u62A5\\\\u7684\\\\u5168\\\\u90E8\\\\u5185\\\\u5BB9\\\\u5747\\\\u5C5E\\\\u5B9E\\\\u3001\\\\u51C6\\\\u786E\\\\uFF0C\\\\u4E0D\\\\u5B58\\\\u5728\\\\u4EFB\\\\u4F55\\\\u9690\\\\u7792\\\\u548C\\\\u4E0D\\\\u5B9E\\\\u7684\\\\u60C5\\\\u51B5\\\\uFF0C\\\\u66F4\\\\u65E0\\\\u9057\\\\u6F0F\\\\u4E4B\\\\u5904\\\\u3002\\",\\"value\\":{\\"stringValue\\":\\"\\\\u662F Yes\\"},\\"hide\\":false},{\\"name\\":\\"input_1582538924486\\",\\"title\\":\\"\\\\u5907\\\\u6CE8 Notes\\",\\"value\\":{\\"stringValue\\":\\"\\"},\\"hide\\":false},{\\"name\\":\\"datetime_1611146487222\\",\\"title\\":\\"\\\\u6253\\\\u5361\\\\u65F6\\\\u95F4\\\\uFF08\\\\u65E0\\\\u9700\\\\u586B\\\\u5199\\\\uFF0C\\\\u4FDD\\\\u5B58\\\\u540E\\\\u4F1A\\\\u81EA\\\\u52A8\\\\u66F4\\\\u65B0\\\\uFF09\\",\\"value\\":{\\"dateValue\\":\\"\\"},\\"hide\\":false}],\\"playerId\\":\\"owner\\"}",
    "mail": {
        "address": "",
        "reporter_name": "HAL-9000",
        "smtp_password": ""
    },
    "report_retry": {
        "counts": 3,
        "duration": 10
    },
    "user_agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36 Edg/92.0.902.84",
    "xmu": {
        "ID": "{account}",
        "password": "{password}"
    }
}'''
            contains = contains.replace('''{account}''', value['account'])
            password = value['password']
            plaintext = base64.b64decode(password)
            password_decode = str(plaintext, encoding="utf8")
            contains = contains.replace('''{password}''', password_decode)
            f.write(contains)

        try_times = 0
        result = ''
        while try_times < 3:
            result = os.popen(".\\lib\\daily_reporter.exe").read()
            print(result)
            if result.startswith('Please edit'):
                try_times += 1
            else:
                break
        result_list = result.split('\n')
        reply = '“厦大自动健康打卡”服务报告：'
        init = False
        for line in result_list:
            line = line.strip()
            if len(line) > 0:
                if line == 'login failed! please check your username and password.':
                    reply += '\n登陆失败！账号密码错误'
                    init = True
                elif line == 'can\'t open the report url!':
                    reply += '\n登陆失败！无法打开健康打卡的网页'
                    init = True
                elif line == 'failed to get businees_ID':
                    reply += '\n打卡失败！无法获取business_ID'
                    init = True
                elif line == 'can\'t open the report form!':
                    reply += '\n打卡失败！无法打开表单页面'
                    init = True
                elif line == 'failed to get form_ID':
                    reply += '\n打卡失败！无法获取您的默认打卡表单'
                    init = True
                elif line == 'post form-data failed':
                    reply += '\n打卡失败！无法连接打卡服务器'
                    init = True
                elif line == 'Today\'s report is over. You can\'t report now.':
                    reply += '\n打卡失败！您已经完成打卡'
                    init = True
                elif line == 'Warning: It is not allowed to report at this time.':
                    reply += '\n打卡失败！超出允许打卡的时间'
                    init = True
                elif line == 'report succeeed!':
                    reply += '\n打卡成功！请登录打卡系统或配合辅导员提醒手动检查！'
                    init = True
        if not init:
            reply += '\n因为未知原因打卡失败。\nDEBUG日志：' + result
        if member is not None:
            await bot.send_friend_message(key, reply)


async def muteall_schedule(bot, hour, minute):
    muteall_schedule = dataManage.load_obj('data/Function/muteall')  # 禁言计划
    del_list = []

    for group_id, value in muteall_schedule.items():
        group = await bot.get_group(group_id)
        if group is None:
            del_list.append(group_id)
            continue

        if value['hour1'] == hour and value['minute1'] == minute:
            if await bot.is_admin(group):
                await bot.send(group, '按照计划开启全体禁言——由群成员' + str(value['id']) + '编辑')
                await bot.mute_all(group_id)
            else:
                await bot.send(group, '按照计划开启全体禁言，但小柒权限不足——由群成员' + str(value['id'] + '编辑'))
        if value['hour2'] == hour and value['minute2'] == minute:
            if await bot.is_admin(group):
                await bot.send(group, '按照计划关闭全体禁言——由群成员' + str(value['id']) + '编辑')
                await bot.unmute_all(group_id)
            else:
                await bot.send(group, '按照计划关闭全体禁言，但小柒权限不足——由群成员' + str(value['id'] + '编辑'))


async def RPG_rank(bot, hour, minute):
    dayOfWeek = datetime.datetime.now().weekday()
    if dayOfWeek == 6 and hour == 21 and minute == 10:
        RPG.lock = True
        core = RPG.Core()
        rank = core.get_rank()

        if rank['gold']['1'] != 0:
            items = {
                'name': '魔法石',
                'number': 8,
                'enchanting': {
                    'sharp': 0,
                    'strong': 0,
                    'rapid': 0
                }
            }
            give_items(rank['gold']['1'], core, items, '排行榜结算获得积分第一')
        if rank['gold']['2'] != 0:
            items = {
                'name': '魔法石',
                'number': 4,
                'enchanting': {
                    'sharp': 0,
                    'strong': 0,
                    'rapid': 0
                }
            }
            give_items(rank['gold']['2'], core, items, '排行榜结算获得积分第二')
        if rank['gold']['3'] != 0:
            items = {
                'name': '魔法石',
                'number': 2,
                'enchanting': {
                    'sharp': 0,
                    'strong': 0,
                    'rapid': 0
                }
            }
            give_items(rank['gold']['3'], core, items, '排行榜结算获得积分第三')

        if rank['fencing_master'] != 0:
            items = {
                'name': '魔法石',
                'number': 3,
                'enchanting': {
                    'sharp': 0,
                    'strong': 0,
                    'rapid': 0
                }
            }
            give_items(rank['fencing_master'], core, items, '排行榜结算获得击剑达人')
        if rank['be_fenced'] != 0:
            items = {
                'name': '魔法石',
                'number': 3,
                'enchanting': {
                    'sharp': 0,
                    'strong': 0,
                    'rapid': 0
                }
            }
            give_items(rank['be_fenced'], core, items, '排行榜结算获得被击剑次数最多')
        if rank['monster'] != 0:
            items = {
                'name': '魔法石',
                'number': 8,
                'enchanting': {
                    'sharp': 0,
                    'strong': 0,
                    'rapid': 0
                }
            }
            give_items(rank['monster'], core, items, '排行榜结算获得怪物猎人')
        if rank['die'] != 0:
            items = {
                'name': '魔法石',
                'number': 3,
                'enchanting': {
                    'sharp': 0,
                    'strong': 0,
                    'rapid': 0
                }
            }
            give_items(rank['die'], core, items, '排行榜结算获得反复作死')
        if rank['travel'] != 0:
            items = {
                'name': '魔法石',
                'number': 5,
                'enchanting': {
                    'sharp': 0,
                    'strong': 0,
                    'rapid': 0
                }
            }
            give_items(rank['travel'], core, items, '排行榜结算获得流浪者')
        if rank['mining_max'] != 0:
            items = {
                'name': '魔法石',
                'number': 8,
                'enchanting': {
                    'sharp': 0,
                    'strong': 0,
                    'rapid': 0
                }
            }
            give_items(rank['mining_max'], core, items, '排行榜结算获得黄金矿工')
        if rank['sign_max'] != 0:
            items = {
                'name': '魔法石',
                'number': 3,
                'enchanting': {
                    'sharp': 0,
                    'strong': 0,
                    'rapid': 0
                }
            }
            give_items(rank['sign_max'], core, items, '排行榜结算获得签到达人')

        config = dataManage.read_config()
        for group_id in config['test_group']:
            await bot.send_group_message(group_id, '完成排行榜结算！')
        time.sleep(15)
        RPG.force_reload = True
        RPG.lock = False


def give_items(value: int, core: RPG.Core, items: dict, reason: str = ''):
    user = core.get_user(value)
    reply, user = core.get_items(user, items)
    if reply:
        reason += '获得：%sx%d' % (items['name'], items['number'])
    else:
        reason += '获得：%sx%d，但是背包已满。' % (items['name'], items['number'])
    user['config']['report'] += '\n' + reason
    core.update(value, user, RPG.Result(), force=True)  # 强制更新
