import random
import datetime
import linecache
from PIL import Image, ImageDraw, ImageFont
import requests
from bs4 import BeautifulSoup

from plugins import dataManage
from plugins import logManage
from plugins import getNow
from plugins import Clash

# ==========================================================
# 硬币
def coin():
    ran = random.randint(1, 6)
    if ran % 2 == 0:
        return '你抛出的硬币是：正面'
    else:
        return '你抛出的硬币是：反面'


# 骰子
def dice():
    return '你丢出的点数是：' + str(random.randint(1, 6))


# ==========================================================
# 微博热搜
def getHot():
    url = 'https://s.weibo.com/top/summary/'
    timeout = random.choice(range(80, 180))
    header = {
      'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9',
      'Accept-Encoding': 'gzip, deflate, br',
      'Accept-Language': 'zh-CN,zh;q=0.9',
      'Cache-Control': 'max-age=0',
      'Connection': 'keep-alive',
      'Cookie': 'SUB=_2AkMWDOpbf8NxqwJRmPETzWzraIVzzg3EieKgUBuAJRMxHRl-yT9jqmAutRB6PYzEtE_p5IYGJyYeNqcLVtTfJ0SCqJhV; SUBP=0033WrSXqPxfM72-Ws9jqgMF55529P9D9WFEZnhsbjpNJ4BDcsuh_zEe; SINAGLOBAL=2647896765706.4746.1632658796711; _s_tentry=-; Apache=2912749181224.8384.1632727196301; ULV=1632727196310:2:2:2:2912749181224.8384.1632727196301:1632658796723; WBStorage=6ff1c79b|undefined',
      'Host': 's.weibo.com',
      'sec-ch-ua': '"Chromium";v="94", "Google Chrome";v="94", ";Not A Brand";v="99"',
      'sec-ch-ua-mobile': '?0',
      'sec-ch-ua-platform': '"Windows"',
      'Sec-Fetch-Dest': 'document',
      'Sec-Fetch-Mode': 'navigate',
      'Sec-Fetch-Site': 'none',
      'Sec-Fetch-User': '?1',
      'Upgrade-Insecure-Requests': '1',
      'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/94.0.4606.54 Safari/537.36'
    }
    strhtml = requests.get(url, headers=header, timeout=timeout)
    soup = BeautifulSoup(strhtml.text,'html.parser')

    data = soup.select('#pl_top_realtimehot > table > tbody > tr > td.td-02 > a')

    result = ''
    for index in range(0, 7):
        i = data[index]
        link = i.get('href')
        contain = i.get_text()
        if index != 0:
            result +=  '\n' + str(index) + ':' + contain
        else:
            result += '微博置顶:' + contain

    return result

# ==========================================================
# 运势
class luck:
    def __init__(self):
        self.luck = {}
        self.luck_file = 'data/luck'
        self.load_file()

    def load_file(self):
        self.luck = dataManage.load_obj(self.luck_file)

        clockDate = self.luck["luckDate"]
        today = str(datetime.date.today())
        if clockDate != today:
            self.luck["luck"].clear()
            self.luck["luckDate"] = today

    def write_file(self):
        dataManage.save_obj(self.luck, self.luck_file)

    def refresh_luck(self, qq):
        self.load_file()
        if self.luck["luck"].__contains__(qq):
            del self.luck["luck"][qq]
            self.write_file()


    def get_luck_number(self, qq):
        self.load_file()
        if self.luck["luck"].__contains__(qq):
            return self.luck["luck"][qq]

        number = random.normalvariate(50, 16)
        if number < 0:
            number = 0
        elif number > 100:
            number = 100
        self.luck["luck"][qq] = int(number)
        self.write_file()
        return number

    def get_luck(self, qq):
        self.load_file()
        if self.luck["luck"].__contains__(qq):
            return '你今天的运势是：' + str(self.luck["luck"][qq])

        number = random.normalvariate(50, 16)
        if number < 0:
            number = 0
        elif number > 100:
            number = 100
        self.luck["luck"][qq] = int(number)
        self.write_file()
        return '你今天的运势是：' + str(int(number))


# ==========================================================
# 单词
def loop_step(index, total):
    index += 1
    if index > total:
        index = 1
    return index


def get_vocabulary4(number):
    if number > 20:
        return '贪心可不是好事哦~请输入一个小于等于20的数字'
    lineNumber = 1
    with open('data/Function/Vocabulary/vocabulary-4-index.txt', 'r+', encoding='utf-8') as f:
        lineNumber = int(f.readline())

    totalNumber = int(linecache.getline(r'data/Function/Vocabulary/vocabulary-4.txt', 1))
    reply = ''
    for i in range(0, number):
        reply += linecache.getline(r'data/Function/Vocabulary/vocabulary-4.txt', lineNumber + 1)
        lineNumber = loop_step(lineNumber, totalNumber)

    print('lineNumber：', lineNumber)

    with open('data/Function/Vocabulary/vocabulary-4-index.txt', 'w+', encoding='utf-8') as f:
        f.write(str(lineNumber))

    return reply[:-1]


def get_vocabulary6(number):
    if number > 20:
        return '贪心可不是好事哦~请输入一个小于等于20的数字'
    lineNumber = 1
    with open('data/Function/Vocabulary/vocabulary-6-index.txt', 'r+', encoding='utf-8') as f:
        lineNumber = int(f.readline())

    totalNumber = int(linecache.getline(r'data/Function/Vocabulary/vocabulary-6.txt', 1))
    reply = ''
    for i in range(0, number):
        reply += linecache.getline(r'data/Function/Vocabulary/vocabulary-6.txt', lineNumber + 1)
        lineNumber = loop_step(lineNumber, totalNumber)

    with open('data/Function/Vocabulary/vocabulary-6-index.txt', 'w+', encoding='utf-8') as f:
        f.write(str(lineNumber))
    return reply[:-1]


# ==========================================================
# 漂流瓶

class DriftingBottle:
    def __init__(self):
        self.bottle = dataManage.load_obj('data/Function/Bottle/bottle')
        if not self.bottle.__contains__('message'):
            self.bottle['message'] = []

    def throw(self, qq, text):
        date = getNow.toString()
        self.bottle['message'].append({
            'text': text,
            'qq': qq,
            'date': date
        })
        dataManage.save_obj(self.bottle, 'data/Function/Bottle/bottle')
        logManage.member_log(getNow.toString(), qq, '扔出漂流瓶：' + text)
        return '成功扔出一个漂流瓶，当前有' + str(len(self.bottle['message'])) + '个漂流瓶' + '\n漂流瓶不是法外之地，每条漂流瓶都有日志记录'

    def pick(self):
        if len(self.bottle['message']) == 0:
            return '没有漂流瓶了呢~不妨扔一个'

        data = random.choice(self.bottle['message'])
        self.bottle['message'].remove(data)
        if data['qq'] != 1394144014:
            dataManage.save_obj(self.bottle, 'data/Function/Bottle/bottle')
        return data['text'] + '——' + data['date']


# ==========================================================
# 随机字符
def random_char(length):
    origin = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789'

    salt = ''
    for i in range(length):
        salt += random.choice(origin)

    return salt


# 
# 生成模块列表
def paste_switch(switch: bool, back: Image, image_on: Image, image_off: Image, left: int, top: int) -> Image:
    if switch:
        back.paste(image_on, (left, top))
    else:
        back.paste(image_off, (left, top))
    return back

def generate_module_list(group_id: int, group_config: dict) -> str:
    base = Image.open('data/__IMAGE__/模块列表.png').convert('RGBA')
    out_file = 'data/__TEMP__/' + str(group_id) + '.png'
    txt = Image.new('RGBA', base.size, (255,255,255,0))
    d = ImageDraw.Draw(txt)

    font_size = 20
    fnt = ImageFont.truetype('data/Font/FZHTJT.TTF', font_size)
    fill_black = '#000000'

    image_on = Image.open('data/__IMAGE__/on.png')
    image_on = Clash.transparence2white(image_on)
    image_on = image_on.resize((50, 50),Image.ANTIALIAS)
    image_off = Image.open('data/__IMAGE__/off.png')
    image_off = Clash.transparence2white(image_off)
    image_off = image_off.resize((50, 50),Image.ANTIALIAS)

    index = 0
    space = 89
    space2 = 84
    first_line = 100
    first_left = 450
    second_left = 950
    third_left = 1490

    base = paste_switch(group_config['config']['mute'], base, image_on, image_off, first_left, first_line + space * index)
    index += 1
    base = paste_switch(group_config['config']['limit'], base, image_on, image_off, first_left, first_line + space * index)
    index += 1
    base = paste_switch(group_config['config']['welcome'], base, image_on, image_off, first_left, first_line + space * index)
    index += 1
    base = paste_switch(group_config['config']['flash'], base, image_on, image_off, first_left, first_line + space * index)
    index += 1
    base = paste_switch(group_config['config']['member_wather'], base, image_on, image_off, first_left, first_line + space * index)
    index += 1
    key_allow = group_config['config']['key']
    text = ''
    if len(key_allow) == 0:
        text = '（没有任何触发词）'
    for key in key_allow:
        text += key + ' '
    d.text((155,554), text, font=fnt, fill=fill_black)
    index += 1
    base = paste_switch(group_config['config']['automatic'], base, image_on, image_off, first_left, 610)
    text = group_config['config']['pass']
    d.text((130,653), text, font=fnt, fill=fill_black)

    index = 0
    base = paste_switch(group_config['config']['nudge'], base, image_on, image_off, second_left, first_line + space2 * index)
    index += 1
    base = paste_switch(group_config['config']['curse'], base, image_on, image_off, second_left, first_line + space2 * index)
    index += 1
    base = paste_switch(group_config['config']['image'], base, image_on, image_off, second_left, first_line + space2 * index)
    index += 1
    base = paste_switch(group_config['config']['ai'], base, image_on, image_off, second_left, first_line + space2 * index)
    index += 1
    base = paste_switch(group_config['config']['TRPG'], base, image_on, image_off, second_left, first_line + space2 * index)
    index += 1
    base = paste_switch(group_config['config']['clash'], base, image_on, image_off, second_left, first_line + space2 * index)
    index += 1
    base = paste_switch(group_config['config']['repeat'], base, image_on, image_off, second_left, first_line + space2 * index)
    index += 1
    base = paste_switch(group_config['config']['autonomous_reply'], base, image_on, image_off, second_left, first_line + space2 * index)

    index = 0
    base = paste_switch(group_config['config']['RPG'], base, image_on, image_off, third_left, 115)
    index += 1
    base = paste_switch(group_config['config']['limit_RPG'], base, image_on, image_off, third_left, 220)

    service_left = 1030
    service_top = 360
    service_space = 30
    service_index = 0

    muteall_schedule = dataManage.load_obj('data/Function/muteall')  # 禁言计划
    if muteall_schedule.__contains__(group_id):
        text = '定时全体禁言服务（%2d:%2d——%2d:%2d）' % (
                    muteall_schedule[group_id]['hour1'],
                    muteall_schedule[group_id]['minute1'],
                    muteall_schedule[group_id]['hour2'],
                    muteall_schedule[group_id]['minute2']
                )
        d.text((service_left,service_top + service_space * service_index), text, font=fnt, fill=fill_black)
        service_index += 1


    out = Image.alpha_composite(base, txt)
    # out.show()
    out.save(out_file)
    return out_file