# 查看所有数据
import pickle

# ==========================================================
# 新的存储方式
def save_obj(obj, name):
    with open('data/'+ name + '.pkl', 'wb') as f:
        pickle.dump(obj, f, pickle.HIGHEST_PROTOCOL)
 
def load_obj(name):
    with open('data/' + name + '.pkl', 'rb') as f:
        return pickle.load(f)

if __name__ == '__main__':
    user = load_obj('user/information')
    for key, value in user.items():
        user[key]['initName'] = False
        user[key]['match']['topTimes'] = 0
        user[key]['match']['lostTopTimes'] = 0
        user[key]['equipment']['arms'] = ''

    goldId = 0
    goldId2 = 0
    goldId3 = 0

    rateId = 0
    rate = 0.0
    rateValueId = 0
    rateValue = 0

    timesId = 0

    for key, value in user.items():
        if goldId == 0:
            goldId = key
            rateId = key
            timesId = key
            if user[rateId]['match']['win'] + user[rateId]['match']['lose'] != 0:
                rate = float(user[rateId]['match']['win']) / float(user[rateId]['match']['win'] + user[rateId]['match']['lose'])
            else:
                rate = 0.0
        else:
            if value['gold'] > user[goldId]['gold']:
                goldId3 = goldId2
                goldId2 = goldId
                goldId = key
            elif goldId2 == 0 or value['gold'] > user[goldId2]['gold']:
                goldId3 = goldId2
                goldId2 = key 
            elif goldId3 == 0 or value['gold'] > user[goldId3]['gold']:
                goldId3 = key

            if user[key]['match']['win'] + user[key]['match']['lose'] > user[timesId]['match']['win'] + user[timesId]['match']['lose']:
                timesId = key

            if user[key]['match']['win'] + user[key]['match']['lose'] != 0:
                rate2 = float(user[key]['match']['win']) / float(user[key]['match']['win'] + user[key]['match']['lose'])
                if rate2 > rate:
                    rate = rate2
                    rateId = key
                if user[key]['match']['win'] + user[key]['match']['lose'] > 50:
                    if rateValueId == 0:
                        rateValue = rate2
                        rateValueId = key
                    elif rate2 > rateValue:
                        rateValue = rate2
                        rateValueId = key

        
    user[goldId]['match']['topTimes'] = 1
    systemData = {
        'god': [], # 无敌模式
        'tmpGod': {},  # 临时无敌模式
        'rampage-1': {}, # 暴走模式（1级）
        'defense-1': {}, # 防御模式（1级）
        'rampage-2': {}, # 暴走模式（2级）
        'defense-2': {}, # 防御模式（2级）
        'rampage-3': {}, # 暴走模式（3级）
        'defense-3': {}, # 防御模式（3级）
        'noLoss': {}, # 击剑不掉积分
        'halveGold': {}, # 减半积分
        'doubleGold': {}, # 双倍积分
        'tripleGold': {}, # 三倍积分
        'fixedGold': {}, # 固定增加或减少一定积分
        'rank': {
            'gold-1': { # 积分第一
                'id': goldId,
                'gold': user[goldId]['gold'] if goldId != 0 else 0
            },
            'gold-2': { # 积分第二
                'id': goldId2,
                'gold': user[goldId2]['gold'] if goldId2 != 0 else 0
            },
            'gold-3': { # 积分第三
                'id': goldId3,
                'gold': user[goldId3]['gold'] if goldId3 != 0 else 0
            },
            'rate': { # 胜率第一
                'id': rateId,
                'rate': rate
            },
            'rate50': { # 胜率第一（大于50场）
                'id': rateValueId,
                'rate': rateValue
            },
            'field': { # 场次第一
                'id': timesId,
                'number': (user[timesId]['match']['win'] + user[timesId]['match']['lose'])  if timesId != 0 else 0
            },
            'challenger': { # 登顶次数第一
                'id': goldId,
                'number': 1
            },
            'loser': { # 被挑战者次数第一
                'id': 0,
                'number': 0
            }
        }
    }
    save_obj(user, 'user/information')
    save_obj(systemData, 'user/system')


    result = '排行榜\n'
    result += '----------------\n'
    result += '积分第一：' + user[systemData['rank']['gold-1']['id']]['name'] + '（' + str(user[systemData['rank']['gold-1']['id']]['gold']) + '）\n'
    if systemData['rank']['gold-2']['id'] != 0:
        result += '积分第二：' + user[systemData['rank']['gold-2']['id']]['name'] + '（' + str(user[systemData['rank']['gold-2']['id']]['gold']) + '）\n'
    if systemData['rank']['gold-3']['id'] != 0:
        result += '积分第三：' + user[systemData['rank']['gold-3']['id']]['name'] + '（' + str(user[systemData['rank']['gold-3']['id']]['gold']) + '）\n'
    
    rate = round(systemData['rank']['rate']['rate'], 2) * 100
    result += '胜率第一：' + user[systemData['rank']['rate']['id']]['name'] + '（' + str(int(rate)) + '%）'
    rateValue = round(systemData['rank']['rate50']['rate'], 2) * 100
    if systemData['rank']['rate50']['id'] != 0:
        result += '\n胜率第一（大于50场）：' + user[systemData['rank']['rate50']['id']]['name'] + '（' + str(int(rateValue)) + '%）'

    if systemData['rank']['field']['id'] != 0:
        result += '\n击剑达人：' + user[systemData['rank']['field']['id']]['name'] + '（' + str(systemData['rank']['field']['number']) + '场）'
    if systemData['rank']['challenger']['id'] != 0:
        result += '\n登顶次数最多：' + user[systemData['rank']['challenger']['id']]['name'] + '（' + str(systemData['rank']['challenger']['number']) + '次）'
    if systemData['rank']['loser']['id'] != 0:
        result += '\n被击剑次数最多：' + user[systemData['rank']['loser']['id']]['name'] + '（' + str(systemData['rank']['loser']['number']) + '次）'
    print(result)

    print('done!')