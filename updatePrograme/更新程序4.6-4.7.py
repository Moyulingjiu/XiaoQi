# 读写数据

import pickle
import os


goods = {
    '破旧的木剑': {
        'attack': 1,
        'cost': 10,
        'sell': 5,
        'comments': '一把勉强可以使用的木剑，攻击力+1',
        'type': 1
    },
    '破旧的木斧': {
        'attack': 2,
        'cost': 30,
        'sell': 5,
        'comments': '一把勉强可以使用的木斧，攻击力+2',
        'type': 1
    },
    '破旧的布头盔': {
        'defense': 1,
        'cost': 20,
        'sell': 5,
        'comments': '一顶勉强可以使用的头盔，护甲+1',
        'type': 2
    },
    '破旧的布甲': {
        'defense': 1,
        'cost': 20,
        'sell': 5,
        'comments': '一件勉强可以使用的布甲，护甲+1',
        'type': 3
    },
    '破旧的布护腿': {
        'defense': 1,
        'cost': 20,
        'sell': 5,
        'comments': '一件勉强可以使用的布护腿，护甲+1',
        'type': 4
    },
    '破旧的布靴': {
        'defense': 1,
        'cost': 20,
        'sell': 5,
        'comments': '一双勉强可以使用的布靴，护甲+1',
        'type': 5
    },
    '攻击戒指': {
        'attack': 3,
        'cost': 100,
        'sell': 50,
        'comments': '一个充斥着神奇力量的戒指，攻击力+3',
        'type': 6
    },
    '守护戒指': {
        'defense': 1,
        'cost': 100,
        'sell': 50,
        'comments': '一个充斥着神奇力量的戒指，护甲+1',
        'type': 7
    },
    '贪婪戒指': {
        'gold': 1,
        'cost': 100,
        'sell': 50,
        'comments': '一个充斥着神奇力量的戒指，每次收益积分+1',
        'type': 7
    },

    '木剑': {
        'attack': 2,
        'cost': 50,
        'sell': 10,
        'comments': '一把可以使用的木剑，攻击力+2',
        'type': 1
    },
    '布甲': {
        'defense': 2,
        'cost': 60,
        'sell': 30,
        'comments': '一件可以使用的布甲，护甲+2',
        'type': 3
    },

    '制式长枪': {
        'attack': 3,
        'defense': -1,
        'cost': 100,
        'sell': 50,
        'comments': '人类军队的制式长枪，长武器带来极强的进攻性，但也削弱了防御，攻击力+3，护甲-1',
        'type': 1
    },
    '大鸡腿': {
        'attack': -1,
        'cost': -1,
        'sell': 2,
        'comments': '这东西真的有杀伤力吗，攻击力-1',
        'type': 1
    },

    '金粒': {
        'gold': 5,
        'cost': -1,
        'sell': 5,
        'comments': '看！那小小的金粒，积分+5',
        'type': 11
    },
    '金条': {
        'gold': 10,
        'cost': -1,
        'sell': 10,
        'comments': '闪闪发光！积分+10',
        'type': 11
    },
    '金块': {
        'gold': 20,
        'cost': -1,
        'sell': 20,
        'comments': '天啊！积分+20',
        'type': 11
    },
    '木板': {
        'cost': 10,
        'sell': 7,
        'comments': '一块不知道有什么用的木板',
        'type': 13
    },
    '石头': {
        'cost': 10,
        'sell': 7,
        'comments': '一块不知道有什么用的石头',
        'type': 13
    },

    '体力药水': {
        'strength': 5,
        'cost': 5,
        'sell': 4,
        'comments': '体力值+5',
        'type': 0
    },
    '生命药水': {
        'hp': 10,
        'cost': 5,
        'sell': 4,
        'comments': '生命值+10',
        'type': 0
    },
    '精神药水': {
        'san': 10,
        'cost': 5,
        'sell': 4,
        'comments': 'san值+10',
        'type': 0
    },
    '积分药水': {
        'gold': 5,
        'cost': -1,
        'sell': 5,
        'comments': '积分+5',
        'type': 0
    },

    '体力补偿礼包': {
        'strength': 5,
        'cost': -1,
        'sell': 0,
        'comments': '体力值+5',
        'type': 10
    },
    '积分补偿礼包': {
        'gold': 5,
        'cost': -1,
        'sell': 0,
        'comments': '积分+5',
        'type': 10
    },
    '生命补偿礼包': {
        'hp': 10,
        'cost': -1,
        'sell': 0,
        'comments': '生命值+5',
        'type': 10
    },
    'san值补偿礼包': {
        'san': 10,
        'cost': -1,
        'sell': 0,
        'comments': 'san值+10',
        'type': 10
    },

    '内测玩家纪念品': {
        'cost': -1,
        'sell': 30,
        'comments': '一个看起来没什么用的摆件',
        'type': 12
    }
}


def save_obj(obj, name):
    filePath = 'data/' + name + '.pkl'
    with open(filePath, 'wb') as f:
        pickle.dump(obj, f, pickle.HIGHEST_PROTOCOL)

def load_obj(name):
    filePath = 'data/' + name + '.pkl'
    if not os.path.exists(filePath):
        return {}
    with open(filePath, 'rb') as f:
        return pickle.load(f)

# 取消应用属性值
def cancelAttribute(key, name, user):
    global goods
    if not goods.__contains__(name):
        return user

    if goods[name].__contains__('attack'):
        user[key]['attribute']['attack'] -= goods[name]['attack']
    if goods[name].__contains__('defense'):
        user[key]['attribute']['defense'] -= goods[name]['defense']
    return user

# 获取数据
def getMyData(key, user):
    result = '昵称：' + user[key]['name']
    if not user[key]['initName']:
        result += '（自动获取）'
    result += '\n'
    result += '积分：' + str(user[key]['gold']) + '\n'

    maxStrength = user[key]['attribute']['strength-max'] + user[key]['attribute']['strength-up']
    result += '体力：' + str(user[key]['attribute']['strength']) + '/' + str(maxStrength) + '\n'

    maxHp = user[key]['attribute']['hp-max'] + user[key]['attribute']['hp-up']
    result += '生命值：' + str(user[key]['attribute']['hp']) + '（' + str(maxHp) + '）\n'

    attack = user[key]['attribute']['attack'] + user[key]['attribute']['attack-up']
    result += '攻击力：' + str(attack) + '（' + str(user[key]['attribute']['attack-up']) + '）\n'

    defense = user[key]['attribute']['defense'] + user[key]['attribute']['defense-up']
    result += '护甲：' + str(defense) + '（' + str(user[key]['attribute']['defense-up']) + '）\n'

    maxSan = user[key]['attribute']['san-max'] + user[key]['attribute']['san-up']
    result += 'San值：' + str(user[key]['attribute']['san']) + '/' + str(maxSan) + '\n'

    result += '总计场次：' + str(user[key]['match']['win'] + user[key]['match']['lose'])
    if user[key]['match']['win'] + user[key]['match']['lose'] != 0:
        rate = float(user[key]['match']['win']) / float(user[key]['match']['win'] + user[key]['match']['lose'])
        rate = round(rate, 2) * 100
        result += '（' + str(int(rate)) + '%）'

    maxKnapsack = user[key]['attribute']['knapsack-max'] + user[key]['attribute']['knapsack-up']
    result += '\n背包物品数：' + str(len(user[key]['warehouse'])) + '/' + str(maxKnapsack)

    result += '\n强化次数：' + str(user[key]['attribute']['strengthen']) + '次'
    return result


if __name__ == '__main__':
    user = load_obj('user/information')

    for key, value in user.items():
        user[key]['equipment']['ring'] = []

        user[key]['attribute']['strengthen'] = 0
        user[key]['attribute']['attack-up'] = 0
        user[key]['attribute']['hp-max'] = 100
        user[key]['attribute']['hp-up'] = 0
        user[key]['attribute']['san-max'] = 100
        user[key]['attribute']['san-up'] = 0
        user[key]['attribute']['defense-up'] = 0

        user[key]['attribute']['strength-max'] = 120
        user[key]['attribute']['strength-sign'] = 20
        user[key]['attribute']['strength-up'] = 0
        user[key]['attribute']['strength-sign-up'] = 0

        user[key]['attribute']['knapsack-max'] = 10
        user[key]['attribute']['knapsack-up'] = 0

        user[key]['attribute']['gold-income'] = 0
        user[key]['attribute']['gold-expenditure'] = 0
        user[key]['attribute']['gold-income-shop'] = 0
        user[key]['attribute']['gold-expenditure-shop'] = 0

        if user[key]['equipment']['arms'] != '':
            user = cancelAttribute(key, user[key]['equipment']['arms'], user)
        if user[key]['equipment']['jacket'] != '':
            user = cancelAttribute(key, user[key]['equipment']['jacket'], user)
        if user[key]['equipment']['trousers'] != '':
            user = cancelAttribute(key, user[key]['equipment']['trousers'], user)
        if user[key]['equipment']['shoes'] != '':
            user = cancelAttribute(key, user[key]['equipment']['shoes'], user)
        if user[key]['equipment']['ring-left'] != '':
            user = cancelAttribute(key, user[key]['equipment']['ring-left'], user)
        if user[key]['equipment']['ring-right'] != '':
            user = cancelAttribute(key, user[key]['equipment']['ring-right'], user)
        if user[key]['equipment']['knapsack'] != '':
            user = cancelAttribute(key, user[key]['equipment']['knapsack'], user)

        user[key]['attribute']['strengthen'] = user[key]['attribute']['attack'] + user[key]['attribute']['defense'] - 5

        result = getMyData(key, user)
        print(result)
        print('--------------------------------')
        print('\n\n\n')

    save_obj(user, 'user/information')
    print('done!')