# 读写数据

import pickle
import os

from plugins import getNow
from plugins import logManage


key_allow: list = [
    '#', '*',
    ',', '，',
    '.', '。',
    '!', '！',
    '?', '？',
    ':', '：',
    ';', '；',
    '+',
    '-',
    '/'
]


def save_obj(obj, name: str) -> None:
    filePath = name + '.data'
    with open(filePath, 'wb') as f:
        pickle.dump(obj, f, pickle.HIGHEST_PROTOCOL)


def load_obj(name: str) -> dict:
    filePath = name + '.data'
    if not os.path.exists(filePath):
        return {}
    with open(filePath, 'rb') as f:
        return pickle.load(f)


# 读取配置信息
def read_config():
    filePath = 'data/config.txt'
    config = {
        'version': '0.5.5',
        'qq': 0,
        'name': '小柒',
        'color': '天蓝色',
        'age': 18,
        'master': 0,

        'master_right': [],
        'administrator': [],
        'contributor': [],
        'RPG_administrator': [],

        'blacklist_group': {},
        'blacklist_member': {},
        'test_group': []
    }
    if not os.path.exists(filePath):
        with open(filePath, 'w', encoding='utf-8') as f:
            f.write('version=0.5.5\n')
            f.write('qq=0\n')
            f.write('name=小柒\n')
            f.write('color=天蓝色\n')
            f.write('age=18\n')
            f.write('master=1597867839\n')

            f.write('masterRight=\n')
            f.write('administrator=\n')
            f.write('contributor=\n')
            f.write('RPGadministrator=\n')

            f.write('blacklistGroup=\n')
            f.write('blacklistMember=\n')
            f.write('testGroup=\n')
        return config

    with open(filePath, 'r', encoding='utf-8') as f:
        lines = f.readlines()
        for line in lines:
            line = line.strip()
            if len(line) == 0:
                continue
            if line[0] == '#':
                continue
            datas = line.split('#')
            if len(datas) < 1:
                continue
            pair = datas[0].split('=')
            if len(pair) != 2:
                continue
            pair[0] = pair[0].strip()
            pair[1] = pair[1].strip()

            if pair[0] == 'version':
                config['version'] = pair[1]
            elif pair[0] == 'qq':
                if pair[1].isdigit():
                    config['qq'] = int(pair[1])
            elif pair[0] == 'name':
                config['name'] = pair[1]
            elif pair[0] == 'color':
                config['color'] = pair[1]
            elif pair[0] == 'age':
                if pair[1].isdigit():
                    config['age'] = int(pair[1])
            elif pair[0] == 'master':
                if pair[1].isdigit():
                    config['master'] = int(pair[1])

            elif pair[0] == 'masterRight':
                qq_list = pair[1].split(',')
                for i in qq_list:
                    if i.isdigit():
                        config['master_right'].append(int(i))
            elif pair[0] == 'administrator':
                qq_list = pair[1].split(',')
                for i in qq_list:
                    if i.isdigit():
                        config['administrator'].append(int(i))
            elif pair[0] == 'contributor':
                qq_list = pair[1].split(',')
                for i in qq_list:
                    if i.isdigit():
                        config['contributor'].append(int(i))
            elif pair[0] == 'RPGadministrator':
                qq_list = pair[1].split(',')
                for i in qq_list:
                    if i.isdigit():
                        config['RPG_administrator'].append(int(i))

            elif pair[0] == 'testGroup':
                qq_list = pair[1].split(',')
                for i in qq_list:
                    if i.isdigit():
                        config['test_group'].append(int(i))

            elif pair[0] == 'blacklistGroup':
                qq_list = pair[1].split(',')
                for i in qq_list:
                    data_list = i.split('(')
                    if data_list[0].isdigit():
                        config['blacklist_group'][int(data_list[0])] = data_list[1][:-1]
            elif pair[0] == 'blacklistMember':
                qq_list = pair[1].split(',')
                for i in qq_list:
                    data_list = i.split('(')
                    if data_list[0].isdigit():
                        config['blacklist_member'][int(data_list[0])] = data_list[1][:-1]
    return config


# 保存配置信息
def save_config(config):
    filePath = 'data/config.txt'
    with open(filePath, 'w', encoding='utf-8') as f:
        f.write('version=' + config['version'] + '\n')
        f.write('qq=' + str(config['qq']) + '\n')
        f.write('name=' + config['name'] + '\n')
        f.write('color=' + config['color'] + '\n')
        f.write('age=' + str(config['age']) + '\n')
        f.write('master=' + str(config['master']) + '\n')

        f.write('masterRight=' + list_string(config['master_right']) + '\n')

        f.write('administrator=' + list_string(config['administrator']) + '\n')
        f.write('contributor=' + list_string(config['contributor']) + '\n')
        f.write('RPGadministrator=' + list_string(config['RPG_administrator']) + '\n')

        f.write('blacklistGroup=' + dict_string(config['blacklist_group']) + '\n')
        f.write('blacklistMember=' + dict_string(config['blacklist_member']) + '\n')
        f.write('testGroup=' + list_string(config['test_group']) + '\n')


# 读取群
def read_group(group_id):
    global key_allow
    filePath = 'data/__GROUP__/' + str(group_id)
    group = {
        'config': {
            'mute': False,  # 是否禁言
            'limit': False,  # 是否限制
            'nudge': True,  # 是否开启戳一戳
            'RPG': True,  # 是否开启RPG
            'limit_RPG': False,  # 是否开启RPG限制
            'curse': True,  # 是否开启脏话
            'image': False,  # 是否开启图片搜索
            'ai': False,  # 是否开启ai
            'autonomous_reply': True,  # 是否开启自动回复(群内自定义的)
            'repeat': True,  # 是否开启自动加一
            'TRPG': True,  # 是否开启头骰娘
            'clash': False,  # 是否开启部落冲突查询
            'clash_tag': '',  # 部落标签
            'key': ['.', '。', '*'],  # 触发词
            'reply_limit': 0,  # 回复限制次数
            'welcome': False,  # 是否开启欢迎
            'right_train': [],  # 谁可以训练小柒
            'right_activity': [],  # 谁可以发起活动
            'right_mute': [],  # 谁可以禁言
            'right_RPG': [],  # 谁可以开关游戏
            # =============================
            'flash': False,  # 解除闪照
            'member_wather': False,  # 群成员监控
            'revoke': False,  # 防止撤回
            'automatic': False,  # 自动审核
            'pass': ''  # 加群暗号
        },
        'key_reply': {
            'key_at': {},
            'key': {},
            'question': {},
            'question_at': {}
        },  # 关键词回复
        'welcome': None,  # 欢迎语
        'prohibited_word': [],
        'statistics': {
            'reply': [],
            'RPG': [],
            'card': []
        },
        'group': {},  # 分组信息
        'date': getNow.toString()
    }
    if not os.path.exists(filePath + '.config'):
        with open(filePath + '.config', 'w', encoding='utf-8') as f:
            f.write('date=' + group['date'] + '\n')
            f.write('mute=' + str(group['config']['mute']) + '\n')
            f.write('limit=' + str(group['config']['limit']) + '\n')
            f.write('nudge=' + str(group['config']['nudge']) + '\n')
            f.write('TRPG=' + str(group['config']['TRPG']) + '\n')
            f.write('RPG=' + str(group['config']['RPG']) + '\n')
            f.write('RPGlimit=' + str(group['config']['limit_RPG']) + '\n')
            f.write('curse=' + str(group['config']['curse']) + '\n')
            f.write('image=' + str(group['config']['image']) + '\n')
            f.write('ai=' + str(group['config']['ai']) + '\n')
            f.write('autoReply=' + str(group['config']['autonomous_reply']) + '\n')
            f.write('repeat=' + str(group['config']['repeat']) + '\n')
            f.write('clash=' + str(group['config']['clash']) + '\n')
            f.write('clashTag=' + str(group['config']['clash_tag']) + '\n')
            f.write('welcome=' + str(group['config']['welcome']) + '\n')
            f.write('key')
            for i in group['config']['key']:
                f.write('=' + i)
            f.write('\n')
            f.write('replyTimes=' + str(group['config']['reply_limit']) + '\n')
            f.write('trainRight=' + list_string(group['config']['right_train']) + '\n')
            f.write('activityRight=' + list_string(group['config']['right_activity']) + '\n')
            f.write('muteRight=' + list_string(group['config']['right_mute']) + '\n')
            f.write('gameRight=' + list_string(group['config']['right_RPG']) + '\n')
            f.write('flash=' + str(group['config']['flash']) + '\n')
            f.write('memberWather=' + str(group['config']['member_wather']) + '\n')
            f.write('revoke=' + str(group['config']['revoke']) + '\n')
            f.write('automatic=' + str(group['config']['automatic']) + '\n')
            f.write('pass=' + str(group['config']['pass']) + '\n')
        data = {
            'key_reply': group['key_reply'],
            'welcome': group['welcome'],
            'prohibited_word': group['prohibited_word'],
            'statistics': group['statistics'],
            'group': group['group']
        }
        if not os.path.exists(filePath + '.data'):
            save_obj(group, filePath)
        else:
            group = load_obj(filePath)
        return group
    if not os.path.exists(filePath + '.data'):
        data = {
            'key_reply': group['key_reply'],
            'welcome': group['welcome'],
            'prohibited_word': group['prohibited_word'],
            'statistics': group['statistics'],
            'group': group['group']
        }
        save_obj(group, filePath)

    data = load_obj(filePath)
    group['key_reply'] = data['key_reply']
    group['welcome'] = data['welcome']
    group['prohibited_word'] = data['prohibited_word']
    group['statistics'] = data['statistics']
    group['group'] = data['group']

    with open(filePath + '.config', 'r', encoding='utf-8') as f:
        lines = f.readlines()
        group['config']['key'] = []
        for line in lines:
            line = line.strip()
            if len(line) == 0:
                continue
            if line[0] == '#':
                continue
            datas = line.split('#')
            if len(datas) < 1:
                continue
            pair = datas[0].split('=')
            if len(pair) < 2:
                continue
            pair[0] = pair[0].strip()
            pair[1] = pair[1].strip().lower()

            if pair[0] == 'date':
                group['date'] = pair[1]
            elif pair[0] == 'mute':
                if pair[1] == 'true':
                    group['config']['mute'] = True
                else:
                    group['config']['mute'] = False
            elif pair[0] == 'limit':
                if pair[1] == 'true':
                    group['config']['limit'] = True
                else:
                    group['config']['limit'] = False
            elif pair[0] == 'nudge':
                if pair[1] == 'true':
                    group['config']['nudge'] = True
                else:
                    group['config']['nudge'] = False
            elif pair[0] == 'TRPG':
                if pair[1] == 'true':
                    group['config']['TRPG'] = True
                else:
                    group['config']['TRPG'] = False
            elif pair[0] == 'RPG':
                if pair[1] == 'true':
                    group['config']['RPG'] = True
                else:
                    group['config']['RPG'] = False
            elif pair[0] == 'RPGlimit':
                if pair[1] == 'true':
                    group['config']['limit_RPG'] = True
                else:
                    group['config']['limit_RPG'] = False
            elif pair[0] == 'curse':
                if pair[1] == 'true':
                    group['config']['curse'] = True
                else:
                    group['config']['curse'] = False
            elif pair[0] == 'image':
                if pair[1] == 'true':
                    group['config']['image'] = True
                else:
                    group['config']['image'] = False
            elif pair[0] == 'ai':
                if pair[1] == 'true':
                    group['config']['ai'] = True
                else:
                    group['config']['ai'] = False
            elif pair[0] == 'autoReply':
                if pair[1] == 'true':
                    group['config']['autonomous_reply'] = True
                else:
                    group['config']['autonomous_reply'] = False
            elif pair[0] == 'repeat':
                if pair[1] == 'true':
                    group['config']['repeat'] = True
                else:
                    group['config']['repeat'] = False
            elif pair[0] == 'clash':
                if pair[1] == 'true':
                    group['config']['clash'] = True
                else:
                    group['config']['clash'] = False
            elif pair[0] == 'clashTag':
                group['config']['clash_tag'] = pair[1].upper()
            elif pair[0] == 'welcome':
                if pair[1] == 'true':
                    group['config']['welcome'] = True
                else:
                    group['config']['welcome'] = False

            elif pair[0] == 'replyTimes':
                if pair[1].isdigit():
                    group['config']['reply_limit'] = int(pair[1])
            elif pair[0] == 'key':
                for i in pair:
                    if i in key_allow and i not in group['config']['key']:
                        group['config']['key'].append(i)

            elif pair[0] == 'trainRight':
                qq_list = pair[1].split(',')
                for i in qq_list:
                    if i.isdigit():
                        group['config']['right_train'].append(int(i))
            elif pair[0] == 'activityRight':
                qq_list = pair[1].split(',')
                for i in qq_list:
                    if i.isdigit():
                        group['config']['right_activity'].append(int(i))
            elif pair[0] == 'muteRight':
                qq_list = pair[1].split(',')
                for i in qq_list:
                    if i.isdigit():
                        group['config']['right_mute'].append(int(i))
            elif pair[0] == 'gameRight':
                qq_list = pair[1].split(',')
                for i in qq_list:
                    if i.isdigit():
                        group['config']['right_RPG'].append(int(i))

            elif pair[0] == 'flash':
                if pair[1] == 'true':
                    group['config']['flash'] = True
                else:
                    group['config']['flash'] = False
            elif pair[0] == 'memberWather':
                if pair[1] == 'true':
                    group['config']['member_wather'] = True
                else:
                    group['config']['member_wather'] = False
            elif pair[0] == 'revoke':
                if pair[1] == 'true':
                    group['config']['revoke'] = True
                else:
                    group['config']['revoke'] = False
            elif pair[0] == 'automatic':
                if pair[1] == 'true':
                    group['config']['automatic'] = True
                else:
                    group['config']['automatic'] = False
            elif pair[0] == 'pass':
                pair = datas[0].split('=')
                pair[1] = pair[1].strip()
                group['config']['pass'] = pair[1]
    return group


# 保存群
def save_group(group_id, config):
    filePath = 'data/__GROUP__/' + str(group_id)
    data = {
        'key_reply': config['key_reply'],
        'welcome': config['welcome'],
        'prohibited_word': config['prohibited_word'],
        'statistics': config['statistics'],
        'group': config['group']
    }
    save_obj(data, filePath)

    with open(filePath + '.config', 'w', encoding='utf-8') as f:
        f.write('date=' + config['date'] + '\n')
        f.write('mute=' + str(config['config']['mute']) + '\n')
        f.write('limit=' + str(config['config']['limit']) + '\n')
        f.write('nudge=' + str(config['config']['nudge']) + '\n')
        f.write('TRPG=' + str(config['config']['TRPG']) + '\n')
        f.write('RPG=' + str(config['config']['RPG']) + '\n')
        f.write('RPGlimit=' + str(config['config']['limit_RPG']) + '\n')
        f.write('curse=' + str(config['config']['curse']) + '\n')
        f.write('image=' + str(config['config']['image']) + '\n')
        f.write('ai=' + str(config['config']['ai']) + '\n')
        f.write('autoReply=' + str(config['config']['autonomous_reply']) + '\n')
        f.write('repeat=' + str(config['config']['repeat']) + '\n')
        f.write('clash=' + str(config['config']['clash']) + '\n')
        f.write('clashTag=' + str(config['config']['clash_tag']) + '\n')
        f.write('welcome=' + str(config['config']['welcome']) + '\n')
        f.write('key')
        for i in config['config']['key']:
            f.write('=' + i)
        f.write('\n')
        f.write('replyTimes=' + str(config['config']['reply_limit']) + '\n')
        f.write('trainRight=' + list_string(config['config']['right_train']) + '\n')
        f.write('activityRight=' + list_string(config['config']['right_activity']) + '\n')
        f.write('muteRight=' + list_string(config['config']['right_mute']) + '\n')
        f.write('gameRight=' + list_string(config['config']['right_RPG']) + '\n')
        f.write('flash=' + str(config['config']['flash']) + '\n')
        f.write('memberWather=' + str(config['config']['member_wather']) + '\n')
        f.write('revoke=' + str(config['config']['revoke']) + '\n')
        f.write('automatic=' + str(config['config']['automatic']) + '\n')
        f.write('pass=' + str(config['config']['pass']) + '\n')


# 读取用户
def read_user(qq):
    global key_allow
    filePath = 'data/__USER__/' + str(qq)
    user = {
        'config': {
            'ai': True,
            'reputation': 5,
            'clash_user_tag': [],  # 玩家标签
            'main_clash_user_tag': 0,  # 默认玩家标签
            'clash_tag': [],  # 部落标签
            'main_clash_tag': 0,  # 默认部落标签
            'key': []
        },
        'buffer': {
            'id': 0,
            'buffer': None,
            'time': 'xx-xx-xx'
        },
        'statistics': {
            'reply': [],
            'RPG': [],
            'card': []
        },
        'date': getNow.toString()
    }

    if not os.path.exists(filePath + '.config'):
        with open(filePath + '.config', 'w', encoding='utf-8') as f:
            f.write('ai=false\n')
            f.write('reputation=5\n')
            f.write('clashUserTag=\n')
            f.write('mainClashUserTag=0\n')
            f.write('clashTag=\n')
            f.write('mainClashTag=0\n')
            f.write('key=*=.=。\n')
            f.write('date=' + user['date'] + '\n')
        if not os.path.exists(filePath + '.data'):
            config = {
                'buffer': user['buffer'],
                'statistics': user['statistics']
            }
            save_obj(config, filePath)
        return user
    if not os.path.exists(filePath + '.data'):
        config = {
            'buffer': user['buffer'],
            'statistics': user['statistics']
        }
        save_obj(config, filePath)

    config = load_obj(filePath)
    user['buffer'] = config['buffer']
    user['statistics'] = config['statistics']

    with open(filePath + '.config', 'r', encoding='utf-8') as f:
        lines = f.readlines()
        for line in lines:
            line = line.strip()
            if len(line) == 0:
                continue
            if line[0] == '#':
                continue
            datas = line.split('#')
            if len(datas) < 1:
                continue
            pair = datas[0].split('=')
            if len(pair) < 2:
                continue
            pair[0] = pair[0].strip()
            pair[1] = pair[1].strip().lower()

            if pair[0] == 'date':
                user['date'] = pair[1]
            elif pair[0] == 'clashUserTag':
                for i in range(1, len(pair)):
                    if len(pair[i]) > 0:
                        user['config']['clash_user_tag'].append(pair[i].upper())
            elif pair[0] == 'mainClashUserTag' and pair[1].isdigit():
                user['config']['main_clash_user_tag'] = int(pair[1])
            elif pair[0] == 'clashTag':
                for i in range(1, len(pair)):
                    if len(pair[i]) > 0:
                        user['config']['clash_tag'].append(pair[i].upper())
            elif pair[0] == 'mainClashTag' and pair[1].isdigit():
                user['config']['main_clash_tag'] = int(pair[1])
            elif pair[0] == 'ai':
                if pair[1] == 'true':
                    user['config']['ai'] = True
                else:
                    user['config']['ai'] = False
            elif pair[0] == 'key':
                for i in pair:
                    if i in key_allow and i not in user['config']['key']:
                        user['config']['key'].append(i)
    return user


# 保存用户
def save_user(qq, user):
    filePath = 'data/__USER__/' + str(qq)

    config = {
        'buffer': user['buffer'],
        'statistics': user['statistics']
    }
    save_obj(config, filePath)

    with open(filePath + '.config', 'w', encoding='utf-8') as f:
        f.write('ai=' + str(user['config']['ai']) + '\n')
        f.write('clashUserTag')
        for i in range(0, len(user['config']['clash_user_tag'])):
            f.write('=' + user['config']['clash_user_tag'][i])
        f.write('\n')
        f.write('mainClashUserTag=' + str(user['config']['main_clash_user_tag']) + '\n')
        f.write('clashTag')
        for i in range(0, len(user['config']['clash_tag'])):
            f.write('=' + user['config']['clash_tag'][i])
        f.write('\n')
        f.write('mainClashTag=' + str(user['config']['main_clash_tag']) + '\n')
        f.write('key')
        for i in user['config']['key']:
            f.write('=' + i)
        f.write('\n')
        f.write('date=' + user['date'] + '\n')



def read_statistics():
    filePath = 'data/__LOG__/statistics.log'
    statistics = {
        'kick': 0,  # 被踢出的次数
        'quit': 0,  # 退群次数
        'mute': 0,  # 禁言次数
        'unmute': 0,  # 解除禁言次数
        'awaken': 0,  # 唤醒次数
        'help': 0,  # 帮助调用次数
        'base_function': 0,  # 基础功能调动次数
        'talk': 0,  # talk模块调用次数
        'clock_activity': 0,  # 打卡、活动模块调用次数
        'image_search': 0,  # 图片搜索模块调用次数
        'command': 0,  # 命令模块调用次数
        'operate': 0,  # 管理员操作调用次数
        'game': 0,  # RPG游戏调用次数
        'key_reply': 0,  # 群内自定义关键词调用次数
        'auto_repeat': 0,  # 自动加一调用次数
        'auto_reply': 0,  # 自动回复调用次数
        'clash': 0,  # 部落冲突调用次数
        'new_friend': 0,  # 新的朋友
        'new_group': 0,  # 新加入的群
        'message': 0,  # 发送消息量
        'last_minute': 0,  # 上一分钟回复量
        'nudge': 0  # 戳一戳
    }
    if not os.path.exists(filePath):
        with open (filePath, 'w', encoding='utf-8') as f:
            f.write('kick=0\n')
            f.write('quit=0\n')
            f.write('mute=0\n')
            f.write('unmute=0\n')
            f.write('awaken=0\n')
            f.write('help=0\n')
            f.write('base_function=0\n')
            f.write('talk=0\n')
            f.write('clock_activity=0\n')
            f.write('image_search=0\n')
            f.write('command=0\n')
            f.write('operate=0\n')
            f.write('game=0\n')
            f.write('auto_repeat=0\n')
            f.write('auto_reply=0\n')
            f.write('clash=0\n')
            f.write('new_friend=0\n')
            f.write('new_group=0\n')
            f.write('message=0\n')
            f.write('last_minute=0\n')
            f.write('nudge=0\n')
        return statistics

    with open(filePath, 'r', encoding='utf-8') as f:
        lines = f.readlines()
        for line in lines:
            line = line.strip()
            if len(line) == 0:
                continue
            if line[0] == '#':
                continue
            datas = line.split('#')
            if len(datas) < 1:
                continue
            pair = datas[0].split('=')
            if len(pair) < 2:
                continue
            pair[0] = pair[0].strip()
            pair[1] = pair[1].strip()

            if statistics.__contains__(pair[0]) and pair[1].isdigit():
                statistics[pair[0]] = int(pair[1])
    return statistics


def save_statistics(statistics):
    filePath = 'data/__LOG__/statistics.log'
    with open(filePath, 'w', encoding='utf-8') as f:
        f.write('kick=' + str(statistics['kick']) + '\n')
        f.write('quit=' + str(statistics['quit']) + '\n')
        f.write('mute=' + str(statistics['mute']) + '\n')
        f.write('unmute=' + str(statistics['unmute']) + '\n')
        f.write('awaken=' + str(statistics['awaken']) + '\n')
        f.write('help=' + str(statistics['help']) + '\n')
        f.write('base_function=' + str(statistics['base_function']) + '\n')
        f.write('talk=' + str(statistics['talk']) + '\n')
        f.write('clock_activity=' + str(statistics['clock_activity']) + '\n')
        f.write('image_search=' + str(statistics['image_search']) + '\n')
        f.write('command=' + str(statistics['command']) + '\n')
        f.write('operate=' + str(statistics['operate']) + '\n')
        f.write('game=' + str(statistics['game']) + '\n')
        f.write('auto_repeat=' + str(statistics['auto_repeat']) + '\n')
        f.write('auto_reply=' + str(statistics['auto_reply']) + '\n')
        f.write('clash=' + str(statistics['clash']) + '\n')
        f.write('new_friend=' + str(statistics['new_friend']) + '\n')
        f.write('new_group=' + str(statistics['new_group']) + '\n')
        f.write('message=' + str(statistics['message']) + '\n')
        f.write('last_minute=' + str(statistics['last_minute']) + '\n')
        f.write('nudge=' + str(statistics['nudge']) + '\n')


def list_string(data):
    init = False
    ans = ''
    for i in data:
        if init:
            ans += ','
        else:
            init = True
        ans += str(i)
    return ans


def dict_string(data):
    init = False
    ans = ''
    for key, value in data.items():
        if init:
            ans += ','
        else:
            init = True
        ans += str(key) + '(' + value + ')'
    return ans


def read_weather():
    filePath = 'data/Function/Weather/weather.txt'
    weather = {}
    if not os.path.exists(filePath):
        logManage.log(getNow.toString(), '天气文件缺失！')
        return weather

    with open(filePath, 'r', encoding='utf-8') as f:
        lines = f.readlines()
        for line in lines:
            line = line.strip()
            if len(line) == 0:
                continue
            if line[0] == '#':
                continue
            datas = line.split('#')
            if len(datas) < 1:
                continue
            pair = datas[0].split('=')
            if len(pair) != 2:
                continue
            pair[0] = pair[0].strip()
            pair[1] = pair[1].strip()
            weather[pair[0]] = pair[1]
    return weather


def read_clock():
    filePath = 'data/ClockActivity/clockIn'
    clock = {}
    if not os.path.exists(filePath + '.data'):
        save_obj(clock, filePath)
    else:
        clock = load_obj(filePath)
    return clock


def save_clock(clock):
    filePath = 'data/ClockActivity/clockIn'
    save_obj(clock, filePath)


def read_luck():
    filePath = 'data/luck'
    luck = {
        'luck': {},
        'luckDate': 'xx-xx-xx'
    }
    if not os.path.exists(filePath + '.data'):
        save_obj(luck, filePath)
    else:
        luck = load_obj(filePath)
    return luck


def save_luck(luck):
    filePath = 'data/luck'
    save_obj(luck, filePath)


def read_screen_word():
    filePath = 'data/screenWords.txt'
    screen = []
    if not os.path.exists(filePath):
        with open(filePath , 'w', encoding='utf-8') as f:
            f.write('\n')
        logManage.log(getNow.toString(), '屏蔽词文件缺失')
        return screen
    with open(filePath, 'r', encoding='utf-8') as f:
        lines = f.readlines()
        for line in lines:
            line = line.strip()
            if len(line) > 0:
                screen.append(line)
    return screen


def save_screen_word(screen):
    filePath = 'data/screenWords.txt'
    with open(filePath , 'w', encoding='utf-8') as f:
        for i in screen:
            f.write(i + '\n')


if __name__ == '__main__':
    print('读写数据')
