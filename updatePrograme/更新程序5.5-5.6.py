import pickle
import os

from plugins import dataManage

def load_obj(name):
    filePath = name + '.pkl'
    if not os.path.exists(filePath):
        return {}
    with open(filePath, 'rb') as f:
        return pickle.load(f)

if __name__ == '__main__':
    bot_information = load_obj('data2/baseInformation')

    config = {
        'version': bot_information['baseInformation']['version'],
        'qq': bot_information['baseInformation']['Bot_QQ'],
        'name': bot_information['baseInformation']['Bot_Name'],
        'color': bot_information['baseInformation']['Bot_Color'],
        'age': bot_information['baseInformation']['Bot_Age'],
        'master': bot_information['baseInformation']['Master_QQ'],

        'master_right': [],
        'administrator': bot_information['administrator'],
        'contributor': bot_information['contributors'],
        'RPG_administrator': [],

        'blacklist_group': {},
        'blacklist_member': {},
        'test_group': bot_information['testGroup']
    }

    for i in bot_information['blacklistGroup']:
        config['blacklist_group'][i] = '未知原因，诞生于增加原因之前'
    for i in bot_information['blacklistMember']:
        config['blacklist_member'][i] = '未知原因，诞生于增加原因之前'

    dataManage.save_config(config)

    groups = {}
    users = {}

    for id in bot_information['cursePlanGroup']:
        if not groups.__contains__(id):
            groups[id] = dataManage.read_group(id)
        groups[id]['config']['curse'] = False
        dataManage.save_group(id, groups[id])
    
    for id in bot_information['mute']:
        if not groups.__contains__(id):
            groups[id] = dataManage.read_group(id)
        groups[id]['config']['mute'] = True
        dataManage.save_group(id, groups[id])
    
    for id in bot_information['gameOff']:
        if not groups.__contains__(id):
            groups[id] = dataManage.read_group(id)
        groups[id]['config']['RPG'] = True
        dataManage.save_group(id, groups[id])
    
    for id in bot_information['noAI']['group']:
        if not groups.__contains__(id):
            groups[id] = dataManage.read_group(id)
        groups[id]['config']['ai'] = True
        dataManage.save_group(id, groups[id])
    
    for id in bot_information['noAI']['friend']:
        if not users.__contains__(id):
            users[id] = dataManage.read_user(id)
        users[id]['config']['ai'] = True
        dataManage.save_user(id, users[id])
    
    for key, value in bot_information['keyToken']['group'].items():
        if not groups.__contains__(key):
            groups[key] = dataManage.read_group(key)
        groups[key]['config']['key'] = value
        groups[key]['config']['key'].append('*')
        dataManage.save_group(key, groups[key])
    
    for key, value in bot_information['keyToken']['friend'].items():
        if not users.__contains__(key):
            users[key] = dataManage.read_user(key)
        users[key]['config']['key'] = value
        users[key]['config']['key'].append('*')
        dataManage.save_user(key, users[key])
    
    for id in bot_information['pixiv']:
        if not groups.__contains__(id):
            groups[id] = dataManage.read_group(id)
        groups[id]['config']['image'] = False
        dataManage.save_group(id, groups[id])
    
    for id in bot_information['limit']:
        if not groups.__contains__(id):
            groups[id] = dataManage.read_group(id)
        groups[id]['config']['limit'] = True
        dataManage.save_group(id, groups[id])
    
    for id in bot_information['gamelimit']:
        if not groups.__contains__(id):
            groups[id] = dataManage.read_group(id)
        groups[id]['config']['limit_RPG'] = True
        dataManage.save_group(id, groups[id])
    
    print('基本信息转移完成···')

    weather = load_obj('data2/weather')
    with open('data/Function/Weather/weather.txt', 'w', encoding='utf-8') as f:
        for key, value in weather.items():
            f.write(key + '=' + str(value) + '\n')
            
    print('天气信息转移完成···')

    activity = load_obj('data2/activity')
    dataManage.save_obj(activity, 'data/ClockActivity/activity')
            
    print('活动数据转移完成···')
    
    clock = load_obj('data2/clockIn')
    dataManage.save_obj(activity, 'data/ClockActivity/clockIn')
            
    print('打卡数据转移完成···')
    
    screen = load_obj('data2/AIScreenWords')
    with open('data/screenWords.txt', 'w', encoding='utf-8') as f:
        for i in screen:
            f.write(i + '\n')
            
    print('屏蔽词转移完成···')

    luck = load_obj('data2/luck')
    dataManage.save_obj(luck, 'data/luck')
            
    print('运势转移完成···')

    user = load_obj('data2/user/information')
    system = load_obj('data2/user/system')
    dataManage.save_obj(user, 'data/RPG/user/information')
    dataManage.save_obj(user, 'data/RPG/system/system')
            
    print('RPG数据转移完成···')

    filenames=os.listdir(r'data2/keyReply')
    for name in filenames:
        name = name.replace('.pkl', '')
        if name[-5:] == 'keyAt' and name[:-5].isdigit():
            id = int(name[:-5])
            if not groups.__contains__(id):
                groups[id] = dataManage.read_group(id)

            groups[id]['key_reply']['key_at'] = load_obj('data2/keyReply/' + name)

            dataManage.save_group(id, groups[id])
        elif name[-3:] == 'key' and name[:-3].isdigit():
            id = int(name[:-3])
            if not groups.__contains__(id):
                groups[id] = dataManage.read_group(id)

            groups[id]['key_reply']['key'] = load_obj('data2/keyReply/' + name)

            dataManage.save_group(id, groups[id])
        elif name[-2:] == 'at' and name[:-2].isdigit():
            id = int(name[:-2])
            if not groups.__contains__(id):
                groups[id] = dataManage.read_group(id)

            groups[id]['key_reply']['question_at'] = load_obj('data2/keyReply/' + name)

            dataManage.save_group(id, groups[id])
        elif name.isdigit():
            id = int(name)
            if not groups.__contains__(id):
                groups[id] = dataManage.read_group(id)

            groups[id]['key_reply']['question'] = load_obj('data2/keyReply/' + name)

            dataManage.save_group(id, groups[id])

    print('关键词转移完成···')

    print('全部完成！')