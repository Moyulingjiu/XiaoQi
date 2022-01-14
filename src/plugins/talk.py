
import random
import linecache

# ==========================================================
# 文摘

def poem():
    lineNumber = int(linecache.getline(r'data/Function/Talk/poem.txt', 1))
    a = random.randrange(2, lineNumber + 1)  # 1-9中生成随机数
    # 从文件poem.txt中对读取第a行的数据
    theline = linecache.getline(r'data/Function/Talk/poem.txt', a)
    theline = theline[theline.find('.')+1:-1]
    return theline


def loveTalk():
    lineNumber = int(linecache.getline(r'data/Function/Talk/lovetalk.txt', 1))
    a = random.randrange(2, lineNumber + 1)  # 1-9中生成随机数
    # 从文件lovetalk.txt中对读取第a行的数据
    theline = linecache.getline(r'data/Function/Talk/lovetalk.txt', a)
    theline = theline[theline.find('.')+1:-1]
    return theline


def swear():
    lineNumber = int(linecache.getline(r'data/Function/Talk/swear.txt', 1))
    a = random.randrange(2, lineNumber + 1)  # 1-9中生成随机数
    # 从文件swear.txt中对读取第a行的数据
    theline = linecache.getline(r'data/Function/Talk/swear.txt', a)
    theline = theline[theline.find('.')+1:-1]
    return theline


def numPoem():
    lineNumber = int(linecache.getline(r'data/Function/Talk/poem.txt', 1))
    return '当前文摘有：' + str(lineNumber) + '条'


def numLoveTalk():
    lineNumber = int(linecache.getline(r'data/Function/Talk/lovetalk.txt', 1))
    return '当前情话有：' + str(lineNumber) + '条'


def numSwear():
    lineNumber = int(linecache.getline(r'data/Function/Talk/swear.txt', 1))
    return '当前脏话有：' + str(lineNumber) + '条'


def addPoem(newPoem):
    with open('data/Function/Talk/poem.txt', 'r+', encoding='utf-8') as f:
        text = f.readlines()
    index = 1
    text[0] = str(int(text[0]) + len(newPoem))
    head = text[0]
    with open('data/Function/Talk/poem.txt', 'w', encoding='utf-8') as f:
        f.write(text[0] + '\n')
        del text[0]
        for line in text:
            lineTmp = line.split('.')
            f.write(str(index) + '.' + line[len(lineTmp[0]) + 1:])
            index += 1

        for line in newPoem:
            f.write(str(index) + '.' + str(line) + '\n')
            index += 1
    return '添加成功！当前文摘有：' + head + '条'


def addLoveTalk(newLoveTalk):
    with open('data/Function/Talk/lovetalk.txt', 'r+', encoding='utf-8') as f:
        text = f.readlines()
    index = 1
    print(text[0])
    text[0] = str(int(text[0]) + len(newLoveTalk))
    head = text[0]
    with open('data/Function/Talk/lovetalk.txt', 'w', encoding='utf-8') as f:
        f.write(text[0] + '\n')
        del text[0]
        for line in text:
            lineTmp = line.split('.')
            f.write(str(index) + '.' + line[len(lineTmp[0]) + 1:])
            index += 1

        for line in newLoveTalk:
            f.write(str(index) + '.' + str(line) + '\n')
            index += 1
    return '添加成功！当前情话有：' + head + '条'


def addSwear(newLoveTalk):
    with open('data/Function/Talk/swear.txt', 'r+', encoding='utf-8') as f:
        text = f.readlines()
    index = 1
    print(text[0])
    text[0] = str(int(text[0]) + len(newLoveTalk))
    head = text[0]
    with open('data/Function/Talk/swear.txt', 'w', encoding='utf-8') as f:
        f.write(text[0] + '\n')
        del text[0]
        for line in text:
            lineTmp = line.split('.')
            f.write(str(index) + '.' + line[len(lineTmp[0]) + 1:])
            index += 1

        for line in newLoveTalk:
            f.write(str(index) + '.' + str(line) + '\n')
            index += 1
    return '添加成功！当前脏话有：' + head + '条'


def delPoem(delIndex):
    with open('data/Function/Talk/poem.txt', 'r+', encoding='utf-8') as f:
        text = f.readlines()
    index = 1
    text[0] = str(int(text[0]) - 1)
    head = text[0]
    with open('data/Function/Talk/poem.txt', 'w', encoding='utf-8') as f:
        f.write(text[0] + '\n')
        del text[0]
        for line in text:
            if index != delIndex:
                lineTmp = line.split('.')
                f.write(str(index) + '.' + line[len(lineTmp[0]) + 1:])
            index += 1
    return '删除成功！当前文摘有：' + head + '条'


def delLoveTalk(delIndex):
    with open('data/Function/Talk/lovetalk.txt', 'r+', encoding='utf-8') as f:
        text = f.readlines()
    index = 1
    text[0] = str(int(text[0]) - 1)
    head = text[0]
    with open('data/Function/Talk/lovetalk.txt', 'w', encoding='utf-8') as f:
        f.write(text[0] + '\n')
        del text[0]
        for line in text:
            if index != delIndex:
                lineTmp = line.split('.')
                f.write(str(index) + '.' + line[len(lineTmp[0]) + 1:])
            index += 1
    return '删除成功！当前情话有：' + head + '条'


def delSwear(delIndex):
    with open('data/Function/Talk/swear.txt', 'r+', encoding='utf-8') as f:
        text = f.readlines()
    index = 1
    text[0] = str(int(text[0]) - 1)
    head = text[0]
    with open('data/Function/Talk/swear.txt', 'w', encoding='utf-8') as f:
        f.write(text[0] + '\n')
        del text[0]
        for line in text:
            if index != delIndex:
                lineTmp = line.split('.')
                f.write(str(index) + '.' + line[len(lineTmp[0]) + 1:])
            index += 1
    return '删除成功！当前脏话有：' + head + '条'
