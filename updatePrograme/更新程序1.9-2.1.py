# 将小柒版本1.9更新到2.1的程序
# 重点更新内容为data文件下的内容，只需同目录要执行该程序即可
# 该程序将会自动完成小柒的文件结构更新
# 原始数据默认不会删除，以留作备份，当然也可以选择手动删除原始数据
# 
# 本次更新的新生成文件如下（不可删除）:
#       data/baseInformation.pkl
#       data/clockIn.pkl
#       data/luck.pkl

import pickle
import datetime

# ==========================================================
# 基本信息
Bot_Name = '小柒'
Bot_Age = 14
Bot_Color = '天蓝色'
Bot_QQ = 1622057984
Master_QQ = 1597867839
version = '1.9'

groupClock = []
dictClockPeople = {
    0: [1]
}
clockDate = '2021-2-28'

administrator = []
contributors = []
blacklistGroup = []
blacklistMember = []
testGroup = []
cursePlanGroup = []
isInit = False

lastAutorepeat = ''
lastMessage = ''


# ==========================================================
# 文件操作
def loadFile():
    global isInit
    isInit = True
    with open('data/administrators.txt', 'r+', encoding='utf-8') as f:
        global administrator
        administrator.clear()
        tmpList = f.readlines()
        for i in tmpList:
            if len(i.strip()) != 0:
                administrator.append(int(i))
        print('添加管理员', administrator)

    with open('data/contributors.txt', 'r+', encoding='utf-8') as f:
        global contributors
        contributors.clear()
        tmpList = f.readlines()
        for i in tmpList:
            if len(i.strip()) != 0:
                contributors.append(int(i))
        print('添加贡献者', contributors)

    with open('data/groupClock.txt', 'r+', encoding='utf-8') as f:
        global groupClock
        groupClock.clear()
        tmpList = f.readlines()
        for i in tmpList:
            if len(i.strip()) != 0:
                groupClock.append(int(i))
        print('添加打卡群', groupClock)

    with open('data/testGroup.txt', 'r+', encoding='utf-8') as f:
        global testGroup
        testGroup.clear()
        tmpList = f.readlines()
        for i in tmpList:
            if len(i.strip()) != 0:
                testGroup.append(int(i))
        print('添加测试群', testGroup)

    with open('data/cursePlanGroup.txt', 'r+', encoding='utf-8') as f:
        global cursePlanGroup
        cursePlanGroup.clear()
        tmpList = f.readlines()
        for i in tmpList:
            if len(i.strip()) != 0:
                cursePlanGroup.append(int(i))
        print('添加骂人计划群', cursePlanGroup)

    with open('data/blacklistGroup.txt', 'r+', encoding='utf-8') as f:
        global blacklistGroup
        blacklistGroup.clear()
        tmpList = f.readlines()
        for i in tmpList:
            if len(i.strip()) != 0:
                blacklistGroup.append(int(i))
        print('添加黑名单群', blacklistGroup)

    with open('data/blacklistMember.txt', 'r+', encoding='utf-8') as f:
        global blacklistMember
        blacklistMember.clear()
        tmpList = f.readlines()
        for i in tmpList:
            if len(i.strip()) != 0:
                blacklistMember.append(int(i))
        print('添加黑名单人', blacklistMember)

    with open('data/clockInData/clockDate.txt', 'r+', encoding='utf-8') as f:
        global clockDate
        clockDate = f.readline()
        print('获取打卡日期', clockDate)

    global dictClockPeople
    dictClockPeople.clear()
    for groupNumber in groupClock:
        with open('data/clockInData/' + str(groupNumber) + '.txt', 'r+', encoding='utf-8') as f:
            dictClockPeople[groupNumber] = {}
            clockMember = f.readlines()
            for line in clockMember:
                if len(line.strip()) != 0:
                    lines = line.split(' ')
                    if lines[1][0] == 'T':
                        dictClockPeople[groupNumber][int(lines[0])] = True
                    else:
                        dictClockPeople[groupNumber][int(lines[0])] = False
    print('获取打卡人', dictClockPeople)

    isInit = False


def loadLuckFile():
    lucky = {}

    clockDate = ''
    with open('data/clockInData/luckyDate.txt', 'r+', encoding='utf-8') as f:
        clockDate = f.readline()
    today = str(datetime.date.today())
    if clockDate == today:
        with open('data/clockInData/luck.txt', 'r+', encoding='utf-8') as f:
            text = f.readlines()
            for i in text:
                i = i.strip()
                if len(i) == 0:
                    continue
                luckData = i.split(' ')
                lucky[int(luckData[0])] = int(luckData[1])
    else:
        with open('data/clockInData/luckyDate.txt', 'w', encoding='utf-8') as f:
            f.write(today)
    return {
        'luck': lucky,
        'luckDate': today
    }

# ==========================================================
# 新的存储方式
def save_obj(obj, name):
    with open('data/'+ name + '.pkl', 'wb') as f:
        pickle.dump(obj, f, pickle.HIGHEST_PROTOCOL)
 
def load_obj(name):
    with open('data/' + name + '.pkl', 'rb') as f:
        return pickle.load(f)

# ==========================================================
# 主程序
if __name__ == '__main__':
    loadFile()

    # 基本信息
    botBaseInformation = {
        'baseInformation': {
            'Bot_Name': Bot_Name,
            'Bot_Age': Bot_Age,
            'Bot_Color': Bot_Color,
            'Bot_QQ': Bot_QQ,
            'Master_QQ': Master_QQ,
            'version': version
        },
        'administrator': administrator,
        'contributors': contributors,
        'blacklistGroup': blacklistGroup,
        'blacklistMember': blacklistMember,
        'testGroup': testGroup,
        'cursePlanGroup': cursePlanGroup,
    }
    save_obj(botBaseInformation, 'baseInformation')

    # 打卡信息
    groupClockNew = {}
    for item in groupClock:
        groupClockNew[item] = {
            'remind': True,
            'summary': True,
            'administrator': []
        }

    clockIn = {
        'groupClock': groupClockNew,
        'dictClockPeople': dictClockPeople,
        'clockDate': clockDate
    }
    save_obj(clockIn, 'clockIn')

    luck = loadLuckFile()
    save_obj(luck, 'luck')
