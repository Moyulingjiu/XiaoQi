
import random
import linecache

# ==========================================================
# 塔罗牌


def GetTarot():
    card = random.randint(0, 21)
    side = random.randint(1, 2)
    title = linecache.getline(r'data/Function/Talk/tarot.txt', card * 10 + 1)
    contain = linecache.getline(r'data/Function/Talk/tarot.txt', card * 10 + side * 4 + 1)
    if side == 1:
        title += '正位'
    else:
        title += '逆位'
    return title + '\n' + contain[:-1]


def GetTarot2():
    card = random.randint(0, 3)
    side = random.randint(1, 14)
    title = linecache.getline(r'data/Function/Talk/tarot2.txt', card * 16 + 1)
    contain = linecache.getline(r'data/Function/Talk/tarot2.txt', card * 16 + side + 1)
    return title + contain[:-1]


def tarot():
    result = '大阿卡那牌（一般预示走势）：\n'
    result += GetTarot()
    result += '\n--------------------------\n'
    result += '小阿卡那牌（一般预示地点）：\n'
    result += GetTarot2()
    return result


def tarotTime():
    result = '过去：\n'
    result += GetTarot()
    result += '\n--------------------------\n'
    result += '现在：\n'
    result += GetTarot()
    result += '\n--------------------------\n'
    result += '未来：\n'
    result += GetTarot()
    return result


def tarotIs():
    ans = 0
    newline = '\n--------------------------\n'

    tarot_1 = GetTarot()
    if tarot_1.split('\n')[1] == '正位':
        ans += 1
    tarot_2 = GetTarot()
    if tarot_2.split('\n')[1] == '正位':
        ans += 1
    tarot_3 = GetTarot()
    if tarot_3.split('\n')[1] == '正位':
        ans += 1

    result = ''
    if ans == 3:
        result = '可以，会，能，行，爱。'
    elif ans == 2:
        result = '可能性高，但需要你将逆位所表示的问题改正以提高可能性。'
    elif ans == 1:
        result = '可能性低，需要你将逆位所表示的问题改正以提高可能性。'
    elif ans == 0:
        result = '不可以，不会，不能，不行，不爱。'

    return tarot_1 + newline + tarot_2 + newline + tarot_3 + newline + result


def tarotAns():
    newline = '\n--------------------------\n'
    tarot_1 = GetTarot()
    tarot_2 = GetTarot()
    tarot_3 = GetTarot()

    result = '原因：\n' + tarot_1 + newline + '现况：\n' + \
        tarot_2 + newline + '结果：\n' + tarot_3
    return result


def tarotBussiness():
    newline = '\n--------------------------\n'
    tarot_1 = GetTarot()
    tarot_2 = GetTarot()
    tarot_3 = GetTarot()
    tarot_4 = GetTarot()
    result = '现在：\n' + tarot_1 + newline + '结果：\n' + tarot_4 + \
        newline + '即将遇见的问题：\n' + tarot_3 + '\n' + tarot_2
    return result


def tarotLove():
    newline = '\n--------------------------\n'
    tarot_1 = GetTarot()
    tarot_2 = GetTarot()
    tarot_3 = GetTarot()
    tarot_4 = GetTarot()
    result = '你的期望：\n' + tarot_1 + newline + '恋人的期望：\n' + tarot_4 + \
        newline + '目前彼此的关系：\n' + tarot_3 + newline + '未来彼此的关系：\n' + tarot_2
    return result


def tarotSelf():
    newline = '\n--------------------------\n'
    tarot_1 = GetTarot()
    tarot_2 = GetTarot()
    tarot_3 = GetTarot()
    tarot_4 = GetTarot()
    result = '你所处的状态：\n' + tarot_1 + newline + '你的外在表现：\n' + tarot_4 + \
        newline + '你的内在想法：\n' + tarot_3 + newline + '你的潜意识：\n' + tarot_2
    return result


def tarotCross():
    newline = '\n--------------------------\n'
    tarot_1 = GetTarot()
    tarot_2 = GetTarot()
    tarot_3 = GetTarot()
    tarot_4 = GetTarot()
    tarot_5 = GetTarot()
    result = '对方的想法：\n' + tarot_1 + newline + '你的想法：\n' + tarot_2 + \
        newline + '相处中存在的问题：\n' + tarot_3 + newline + '二人目前的人文环境：\n' + \
        tarot_4 + newline + '二人关系发展的结果：\n' + tarot_5
    return result


def tarotChoose():
    newline = '\n--------------------------\n'
    tarot_1 = GetTarot()
    tarot_2 = GetTarot()
    tarot_3 = GetTarot()
    tarot_4 = GetTarot()
    tarot_5 = GetTarot()
    result = '现况：\n' + tarot_1 + newline + '选择A的近未来：\n' + tarot_2 + \
        newline + '选择B的近未来：\n' + tarot_3 + newline + '选择A的结果：\n' + \
        tarot_4 + newline + '选择B的结果：\n' + tarot_5
    return result


def tarotForward():
    newline = '\n--------------------------\n'
    tarot_1 = GetTarot()
    tarot_2 = GetTarot()
    tarot_3 = GetTarot()
    tarot_4 = GetTarot()
    tarot_5 = GetTarot()
    tarot_6 = GetTarot()
    result = '你对对方的看法：\n' + tarot_1 + newline + '对方对你的看法：\n' + tarot_2 + \
        newline + '你认为目前的关系：\n' + tarot_3 + newline + '对方认为目前的关系：\n' + \
        tarot_4 + newline + '你期望双方关系的发展结果：\n' + tarot_5 + \
        newline + '对方期望双方关系的发展结果：\n' + tarot_6
    return result


def tarotHexagram():
    newline = '\n--------------------------\n'
    tarot_1 = GetTarot()
    tarot_2 = GetTarot()
    tarot_3 = GetTarot()
    tarot_4 = GetTarot()
    tarot_5 = GetTarot()
    tarot_6 = GetTarot()
    tarot_7 = GetTarot()
    result = '过去：\n' + tarot_1 + newline + '现况：\n' + tarot_2 + \
        newline + '未来：\n' + tarot_3 + newline + '指引：\n' + \
        tarot_4 + newline + '环境：\n' + tarot_5 + \
        newline + '期望：\n' + tarot_6 + \
        newline + '结果：\n' + tarot_7
    return result


def tarotCelticCross():
    newline = '\n--------------------------\n'
    tarot_1 = GetTarot()
    tarot_2 = GetTarot()
    tarot_3 = GetTarot()
    tarot_4 = GetTarot()
    tarot_5 = GetTarot()
    tarot_6 = GetTarot()
    tarot_7 = GetTarot()
    tarot_8 = GetTarot()
    tarot_9 = GetTarot()
    tarot_10 = GetTarot()
    result = '1号卡牌（表示现在）：\n' + tarot_1 + newline + '2号卡牌（表示挑战）：\n' + tarot_2 + newline + '3号卡牌（表示过去）：\n' + \
        tarot_3 + newline + '4号卡牌（表示未来）：\n' + tarot_4 + newline + '5号卡牌（表示前文）：\n' + tarot_5 + \
        newline + '6号卡牌（表示后文）：\n' + tarot_6 + newline + '7号卡牌（表示建议）：\n' + tarot_7 + newline + \
        '8号卡牌（表示外部影响）：\n' + tarot_8 + newline + '9号卡牌（表示希望/恐惧）：\n' + \
        tarot_9 + newline + '10号卡牌（表示结果）：\n' + tarot_10 + newline + \
        '对比五号与六号塔罗牌，正逆位是否一致，如果一致，则表示解决问题很容易，如果不一致，则要结合事实找到这么做的真正原因。\n' + \
        '对比五号与十号塔罗牌，如果期望结果与最终结果一致，那么可以继续执行当前自己心中的想法与思路。如果不一致，则要重点查看七号牌所表示的建议。\n' + \
        '对比四号与十号塔罗牌，看四号塔罗牌即将发生的事对十号塔罗牌结果的影响，如果四号未来塔罗牌对十号结果塔罗牌没有促进作用，请查看七号建议塔罗牌。\n' + \
        '如果发现九号希望/恐惧塔罗牌难以解释，请结合六号塔罗牌。看六号塔罗牌表示的潜意识中是恐惧还是希望。\n' + \
        '如果十号结果塔罗牌并不是问卜者所想要的结果，请结合四号未来塔罗牌与七号建议塔罗牌寻找改变的方法。'
    return result
