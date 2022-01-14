import requests
import json
import asyncio
import datetime

from bs4 import BeautifulSoup
import random
import time
import socket  # 用做异常处理
import base64  # 编码解析
import re
import uuid
from PIL import Image, ImageDraw, ImageFont

from plugins import dataManage

# 家庭测试api
# api_token = 'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiIsImtpZCI6IjI4YTMxOGY3LTAwMDAtYTFlYi03ZmExLTJjNzQzM2M2Y2NhNSJ9.eyJpc3MiOiJzdXBlcmNlbGwiLCJhdWQiOiJzdXBlcmNlbGw6Z2FtZWFwaSIsImp0aSI6ImIxNzM1Y2YxLTQxMDYtNGMzYS1hOWVjLTU1YjZlYTNkYTA0NCIsImlhdCI6MTYzNTM0MjI4Niwic3ViIjoiZGV2ZWxvcGVyLzUzNTVmZDI5LTc4NDEtYTVjNC0wN2M2LTE2MGNiYTBiN2MwNSIsInNjb3BlcyI6WyJjbGFzaCJdLCJsaW1pdHMiOlt7InRpZXIiOiJkZXZlbG9wZXIvc2lsdmVyIiwidHlwZSI6InRocm90dGxpbmcifSx7ImNpZHJzIjpbIjExNy4zMC4xODIuMTE2Il0sInR5cGUiOiJjbGllbnQifV19.BJ3T9iT-GYGZtIN_R7ANNwZ_QSXh5qUdlR55Cg7lqjHi3eWwLqA_it6lzaQVO4S3bVTaAWX8Z1BYmiW89DoCnQ'

# 服务器api
api_token = 'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiIsImtpZCI6IjI4YTMxOGY3LTAwMDAtYTFlYi03ZmExLTJjNzQzM2M2Y2NhNSJ9.eyJpc3MiOiJzdXBlcmNlbGwiLCJhdWQiOiJzdXBlcmNlbGw6Z2FtZWFwaSIsImp0aSI6IjdjMzEyYTE1LTcyN2EtNGQ3NC04Mzg5LTRhN2VhY2QwNDU5OSIsImlhdCI6MTYzMDY3Nzg4MCwic3ViIjoiZGV2ZWxvcGVyLzUzNTVmZDI5LTc4NDEtYTVjNC0wN2M2LTE2MGNiYTBiN2MwNSIsInNjb3BlcyI6WyJjbGFzaCJdLCJsaW1pdHMiOlt7InRpZXIiOiJkZXZlbG9wZXIvc2lsdmVyIiwidHlwZSI6InRocm90dGxpbmcifSx7ImNpZHJzIjpbIjE1MC4xNTguMTgwLjcxIl0sInR5cGUiOiJjbGllbnQifV19.8lcRUoWtpFQJaxzFFubCDlKul58eKK59F5Y7KP9xQ43p89BVhISYWqV2P8XZDAyTaWwjYPq2iPmGOa1zuBSBhA'


# 写数字
def get_text_place_color(d, fnt, left, top, value):
    space = 10
    fill_black = '#000000'
    gold_color = '#E77524'
    gray_color = '#646464'
    text = 'N'
    is_max = False
    if value != 0:
        text = str(value['level'])
        is_max = value['level'] == value['maxLevel']
        if value['level'] > 9:
            left -= space
    if text == 'N':
        d.text((left, top), text, font=fnt, fill=gray_color)
    elif is_max:
        d.text((left, top), text, font=fnt, fill=gold_color)
    else:
        d.text((left, top), text, font=fnt, fill=fill_black)


def get_text_place_color2(d, fnt, left, top, value, value2):
    space = 10
    fill_black = '#000000'
    gold_color = '#E77524'
    gray_color = '#646464'
    text = 'N'
    is_max = False
    if value != 0:
        if value.__contains__('superTroopIsActive'):
            text = str(value2['level'])
            is_max = value2['level'] == value2['maxLevel']
            if value2['level'] > 9:
                left -= space
    if text != 'N':
        if is_max:
            d.text((left, top), text, font=fnt, fill=gold_color)
        else:
            d.text((left, top), text, font=fnt, fill=fill_black)


# 解码base64格式的资源
def decode_image(src):
    """
    解码图片
    :param src: 图片编码
        eg:
            src="data:image/gif;base64,R0lGODlhMwAxAIAAAAAAAP///
                yH5BAAAAAAALAAAAAAzADEAAAK8jI+pBr0PowytzotTtbm/DTqQ6C3hGX
                ElcraA9jIr66ozVpM3nseUvYP1UEHF0FUUHkNJxhLZfEJNvol06tzwrgd
                LbXsFZYmSMPnHLB+zNJFbq15+SOf50+6rG7lKOjwV1ibGdhHYRVYVJ9Wn
                k2HWtLdIWMSH9lfyODZoZTb4xdnpxQSEF9oyOWIqp6gaI9pI1Qo7BijbF
                ZkoaAtEeiiLeKn72xM7vMZofJy8zJys2UxsCT3kO229LH1tXAAAOw=="

    :return: str 保存到本地的文件名
    """
    # 1、信息提取
    result = re.search("data:image/(?P<ext>.*?);base64,(?P<data>.*)", src, re.DOTALL)
    if result:
        ext = result.groupdict().get("ext")
        data = result.groupdict().get("data")

    else:
        raise Exception("Do not parse!")

    # 2、base64解码
    img = base64.urlsafe_b64decode(data)

    # 3、二进制文件保存
    filename = "data/Clash/鱼情.png"
    with open(filename, "wb") as f:
        f.write(img)

    return filename


# 编码base64格式的资源
def encode_image(filename):
    """
    编码图片
    :param filename: str 本地图片文件名
    :return: str 编码后的字符串
        eg:
        src="data:image/gif;base64,R0lGODlhMwAxAIAAAAAAAP///
            yH5BAAAAAAALAAAAAAzADEAAAK8jI+pBr0PowytzotTtbm/DTqQ6C3hGX
            ElcraA9jIr66ozVpM3nseUvYP1UEHF0FUUHkNJxhLZfEJNvol06tzwrgd
            LbXsFZYmSMPnHLB+zNJFbq15+SOf50+6rG7lKOjwV1ibGdhHYRVYVJ9Wn
            k2HWtLdIWMSH9lfyODZoZTb4xdnpxQSEF9oyOWIqp6gaI9pI1Qo7BijbF
            ZkoaAtEeiiLeKn72xM7vMZofJy8zJys2UxsCT3kO229LH1tXAAAOw=="

    """
    # 1、文件读取
    ext = filename.split(".")[-1]

    with open(filename, "rb") as f:
        img = f.read()

    # 2、base64编码
    data = base64.b64encode(img).decode()

    # 3、图片编码字符串拼接
    src = "data:image/{ext};base64,{data}".format(ext=ext, data=data)
    return src


# 爬取html
def get_html(url, data=None):
    """
    模拟浏览器来获取网页的html代码
    """
    header = {
        'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8',
        'Accept-Encoding': 'gzip, deflate, sdch',
        'Accept-Language': 'zh-CN,zh;q=0.8',
        'Connection': 'keep-alive',
        # 'Connection': 'close',
        'User-Agent': 'Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.235'
    }
    # 设定超时时间，取随机数是因为防止被网站认为是爬虫
    timeout = random.choice(range(80, 180))
    while True:
        try:
            rep = requests.get(url, headers=header, timeout=timeout)
            rep.encoding = "utf-8"
            break
        except socket.timeout as e:
            print("3:", e)
            time.sleep(random.choice(range(8, 15)))

        except socket.error as e:
            print("4:", e)
            time.sleep(random.choice(range(20, 60)))

    return rep.text


# 解析罗马数字
def get_roman_number(text: str) -> str:
    if text.endswith('III'):
        return '3'
    elif text.endswith('II'):
        return '2'
    elif text.endswith('I'):
        return '1'
    return ''


# 将透明图片转变为白色底
def transparence2white(img: Image) -> Image:
    sp = img.size
    width = sp[0]
    height = sp[1]
    for yh in range(height):
        for xw in range(width):
            dot = (xw, yh)
            color_d = img.getpixel(dot)  # 与cv2不同的是，这里需要用getpixel方法来获取维度数据
            if (color_d[3] == 0):
                color_d = (255, 255, 255, 255)
                img.putpixel(dot, color_d)  # 赋值的方法是通过putpixel
    return img


class Clash:
    def __init__(self):
        super().__init__()
        self.lock_date: str = str(datetime.date.today())
        self.lock_hour: int = -1
        self.lock_minute: int = -1
        self.set_lock: bool = False
        self.clan_cache: dict = dataManage.load_obj('data/Clash/cache/clan')
        self.user_cache: dict = dataManage.load_obj('data/Clash/cache/user')
        self.today: str = str(datetime.date.today())
        self.refresh: bool = False

    def set_refresh(self) -> None:
        self.refresh = True

    def refresh_chache(self) -> None:
        today = str(datetime.date.today())
        if self.refresh:
            self.clan_cache = dataManage.load_obj('data/Clash/cache/clan')
            self.user_cache: dict = dataManage.load_obj('data/Clash/cache/user')
            self.today = today
            self.refresh = False

    def is_lock(self) -> bool:
        today = str(datetime.date.today())
        now = datetime.datetime.now()
        if self.set_lock:
            if self.lock_date == today:
                if self.lock_hour > now.hour or (self.lock_hour == now.hour and self.lock_minute > now.minute):
                    return True
        self.set_lock = False
        return False

    def fish(self):
        url = "https://www.cocservice.top/search/"

        response = get_html(url)
        soup = BeautifulSoup(response, 'html.parser')

        data = soup.select('#loot-index-score')
        image = soup.select('#loot-trend > img')

        if len(data) > 0 and len(image) > 0:
            decode_image(image[0]['src'])

            reply = '目前鱼情：' + data[0].text
            reply += '\n-------------'
            reply += '\n数据由ClashOfClansForecaster提供'

            return reply
        else:
            return '爬虫已失效，请前往官方群反馈给主人，群号可以通过添加小柒好友获得'

    def player(self, tag):
        url = "https://api.clashofclans.com/v1/players/%23" + tag

        params = {
            "Authorization": "Bearer " + api_token
        }

        res = requests.get(url=url, params=params)
        if res.status_code == 200:
            user_dict = json.loads(res.text)

            # 获取空白卡
            base = Image.open('data/Clash/玩家空白卡.jpg').convert('RGBA')
            # make a blank image for the text, initialized to transparent text color
            txt = Image.new('RGBA', base.size, (255, 255, 255, 0))
            # 设置字体
            margin = 40
            font_size = 32
            fnt = ImageFont.truetype('data/Font/FZHTJT.TTF', font_size)
            font_size2 = 26
            fnt2 = ImageFont.truetype('data/Font/FZHTJT.TTF', font_size2)
            font_size3 = 40
            fnt3 = ImageFont.truetype('data/Font/FZHTJT.TTF', font_size3)
            # get a drawing context
            d = ImageDraw.Draw(txt)

            fill_black = '#000000'
            gold_color = '#F8BC44'
            gray_color = '#E77524'

            text = '玩家昵称：' + user_dict['name']
            d.text((margin, 20), text, font=fnt, fill=fill_black)
            text = '等级：' + str(user_dict['expLevel'])
            d.text((450, 20), text, font=fnt, fill=fill_black)
            text = '职位：'
            if user_dict.__contains__('role'):
                if user_dict['role'] == 'leader':
                    text += '首领'
                elif user_dict['role'] == 'coLeader':
                    text += '副首领'
                elif user_dict['role'] == 'admin':
                    text += '长老'
                elif user_dict['role'] == 'member':
                    text += '成员'
            else:
                text += '无部落'
            d.text((base.size[0] - (font_size * len(text)) - margin, 20), text, font=fnt, fill=fill_black)

            text = '玩家部落：暂无'
            if user_dict.__contains__('clan'):
                text = '玩家部落：' + user_dict['clan']['name'] + '（LV.' + str(user_dict['clan']['clanLevel']) + '） - ' + \
                       user_dict['clan']['tag']
            d.text((40, 70), text, font=fnt, fill=fill_black)

            # ======================
            # 大本营数据
            text = '奖杯：' + str(user_dict['trophies']) + ' / ' + str(user_dict['bestTrophies'])
            d.text((200, 135), text, font=fnt2, fill=fill_black)
            text = '胜利之星：' + str(user_dict['warStars'])
            d.text((200, 175), text, font=fnt2, fill=fill_black)
            text = '捐收：' + str(user_dict['donations']) + ' / ' + str(user_dict['donationsReceived'])
            d.text((200, 215), text, font=fnt2, fill=fill_black)
            text = '攻防：' + str(user_dict['attackWins']) + ' / ' + str(user_dict['defenseWins'])
            d.text((200, 255), text, font=fnt2, fill=fill_black)

            text = '奖杯：' + str(user_dict['versusTrophies']) + ' / ' + str(user_dict['bestVersusTrophies'])
            d.text((650, 135), text, font=fnt2, fill=fill_black)
            text = '胜利次数：' + str(user_dict['versusBattleWins'])
            d.text((650, 175), text, font=fnt2, fill=fill_black)

            image_path = 'data/Clash/大本营/TownHall' + str(user_dict['townHallLevel'])
            if user_dict.__contains__('townHallWeaponLevel'):
                image_path += '_' + str(user_dict['townHallWeaponLevel'])
            image_path += '.jpg'
            image1 = Image.open(image_path)
            image1 = image1.resize((145, 145), Image.ANTIALIAS)
            base.paste(image1, (40, 135))

            if user_dict.__contains__('builderHallLevel'):
                image_path = 'data/Clash/大本营/BuilderHall' + str(user_dict['builderHallLevel']) + '.jpg'
            else:
                image_path = 'data/Clash/大本营/空白大本营.jpg'
            image1 = Image.open(image_path)
            image1 = image1.resize((145, 145), Image.ANTIALIAS)
            base.paste(image1, (485, 135))

            # ======================
            first_distance = 50
            space = 74
            line_high1 = 410
            line_high2 = 560
            line_high3 = 700
            line_high4 = 840
            line_high5 = 980
            line_high6 = 1120

            # ======================
            # 兵种等级
            Barbarian = 0  # 野蛮人
            Archer = 0  # 弓箭手
            Giant = 0  # 巨人
            Goblin = 0  # 哥布林
            Wall_Breaker = 0  # 炸弹人
            Balloon = 0  # 气球
            Wizard = 0  # 法师
            Healer = 0  # 天使
            Dragon = 0  # 飞龙
            Peeka = 0  # 皮卡
            Baby_Dragon = 0  # 飞龙宝宝
            Miner = 0  # 矿工
            Electro_Dragon = 0  # 雷电飞龙
            Yeti = 0  # 大雪怪
            Dragon_Rider = 0  # 龙骑士

            Minion = 0  # 亡灵
            Hog_Rider = 0  # 野猪骑士
            Valkyrie = 0  # 女武神
            Golem = 0  # 石头人
            Witch = 0  # 女巫
            Lava_Hound = 0  # 熔岩猎犬
            Bowler = 0  # 蓝胖
            Ice_Golem = 0  # 冰石头人
            Headhunter = 0  # 英雄猎手

            Super_Barbarian = 0  # 超级野蛮人
            Super_Archer = 0  # 超级弓箭手
            Super_Giant = 0  # 超级巨人
            Super_Goblin = 0  # 超级哥布林
            Super_Wall_Breaker = 0  # 超级炸弹人
            Rocket_Balloon = 0  # 火箭气球
            Super_Wizard = 0  # 超级法师
            Inferno_Dragon = 0  # 超级飞龙宝宝
            Super_Minion = 0  # 超级亡灵
            Super_Valkyrie = 0  # 超级女武神
            Super_Witch = 0  # 超级女巫
            Ice_Hound = 0  # 寒冰猎犬

            Raged_Barbarian = 0  # 狂暴野蛮人
            Sneaky_Archer = 0  # 隐秘弓箭手
            Boxer_Giant = 0  # 建筑师巨人
            Beta_Minion = 0  # 变异亡灵
            Bomber = 0  # 炸弹人
            Baby_Dragon2 = 0  # 飞龙宝宝（建筑师）
            Cannon_Cart = 0  # 炮车
            Night_Witch = 0  # 夜世界女巫
            Drop_Ship = 0  # 骷髅气球
            Super_Pekka = 0  # 超级皮卡
            Hog_Glider = 0  # 飞猪

            Wall_Wrecker = 0  # 攻城车
            Battle_Blimp = 0  # 攻城飞艇
            Stone_Slammer = 0  # 攻城气球
            Siege_Barracks = 0  # 攻城器
            Log_Launcher = 0  # 滚木攻城车

            LASSI = 0  # 莱希
            Electro_Owl = 0  # 闪枭
            Mighty_Yak = 0  # 大牦
            Unicorn = 0  # 独角

            for i in user_dict['troops']:
                if i['name'] == 'Barbarian' and i['village'] == 'home':
                    Barbarian = i
                elif i['name'] == 'Archer' and i['village'] == 'home':
                    Archer = i
                elif i['name'] == 'Giant' and i['village'] == 'home':
                    Giant = i
                elif i['name'] == 'Goblin' and i['village'] == 'home':
                    Goblin = i
                elif i['name'] == 'Wall Breaker' and i['village'] == 'home':
                    Wall_Breaker = i
                elif i['name'] == 'Balloon' and i['village'] == 'home':
                    Balloon = i
                elif i['name'] == 'Wizard' and i['village'] == 'home':
                    Wizard = i
                elif i['name'] == 'Healer' and i['village'] == 'home':
                    Healer = i
                elif i['name'] == 'Dragon' and i['village'] == 'home':
                    Dragon = i
                elif i['name'] == 'P.E.K.K.A' and i['village'] == 'home':
                    Peeka = i
                elif i['name'] == 'Baby Dragon' and i['village'] == 'home':
                    Baby_Dragon = i
                elif i['name'] == 'Miner' and i['village'] == 'home':
                    Miner = i
                elif i['name'] == 'Electro Dragon' and i['village'] == 'home':
                    Electro_Dragon = i
                elif i['name'] == 'Yeti' and i['village'] == 'home':
                    Yeti = i
                elif i['name'] == 'Dragon Rider' and i['village'] == 'home':
                    Dragon_Rider = i

                elif i['name'] == 'Minion' and i['village'] == 'home':
                    Minion = i
                elif i['name'] == 'Hog Rider' and i['village'] == 'home':
                    Hog_Rider = i
                elif i['name'] == 'Valkyrie' and i['village'] == 'home':
                    Valkyrie = i
                elif i['name'] == 'Golem' and i['village'] == 'home':
                    Golem = i
                elif i['name'] == 'Witch' and i['village'] == 'home':
                    Witch = i
                elif i['name'] == 'Lava Hound' and i['village'] == 'home':
                    Lava_Hound = i
                elif i['name'] == 'Bowler' and i['village'] == 'home':
                    Bowler = i
                elif i['name'] == 'Ice Golem' and i['village'] == 'home':
                    Ice_Golem = i
                elif i['name'] == 'Headhunter' and i['village'] == 'home':
                    Headhunter = i

                elif i['name'] == 'Super Barbarian' and i['village'] == 'home':
                    Super_Barbarian = i
                elif i['name'] == 'Super Archer' and i['village'] == 'home':
                    Super_Archer = i
                elif i['name'] == 'Super Giant' and i['village'] == 'home':
                    Super_Giant = i
                elif i['name'] == 'Sneaky Goblin' and i['village'] == 'home':
                    Super_Goblin = i
                elif i['name'] == 'Super Wall Breaker' and i['village'] == 'home':
                    Super_Wall_Breaker = i
                elif i['name'] == 'Rocket Balloon' and i['village'] == 'home':
                    Rocket_Balloon = i
                elif i['name'] == 'Super Wizard' and i['village'] == 'home':
                    Super_Wizard = i
                elif i['name'] == 'Inferno Dragon' and i['village'] == 'home':
                    Inferno_Dragon = i
                elif i['name'] == 'Super Minion' and i['village'] == 'home':
                    Super_Minion = i
                elif i['name'] == 'Super Valkyrie' and i['village'] == 'home':
                    Super_Valkyrie = i
                elif i['name'] == 'Super Witch' and i['village'] == 'home':
                    Super_Witch = i
                elif i['name'] == 'Ice Hound' and i['village'] == 'home':
                    Ice_Hound = i

                elif i['name'] == 'Raged Barbarian' and i['village'] == 'builderBase':
                    Raged_Barbarian = i
                elif i['name'] == 'Sneaky Archer' and i['village'] == 'builderBase':
                    Sneaky_Archer = i
                elif i['name'] == 'Boxer Giant' and i['village'] == 'builderBase':
                    Boxer_Giant = i
                elif i['name'] == 'Beta Minion' and i['village'] == 'builderBase':
                    Beta_Minion = i
                elif i['name'] == 'Bomber' and i['village'] == 'builderBase':
                    Bomber = i
                elif i['name'] == 'Baby Dragon' and i['village'] == 'builderBase':
                    Baby_Dragon2 = i
                elif i['name'] == 'Cannon Cart' and i['village'] == 'builderBase':
                    Cannon_Cart = i
                elif i['name'] == 'Night Witch' and i['village'] == 'builderBase':
                    Night_Witch = i
                elif i['name'] == 'Drop Ship' and i['village'] == 'builderBase':
                    Drop_Ship = i
                elif i['name'] == 'Super P.E.K.K.A' and i['village'] == 'builderBase':
                    Super_Pekka = i
                elif i['name'] == 'Hog Glider' and i['village'] == 'builderBase':
                    Hog_Glider = i

                elif i['name'] == 'Wall Wrecker' and i['village'] == 'home':
                    Wall_Wrecker = i
                elif i['name'] == 'Battle Blimp' and i['village'] == 'home':
                    Battle_Blimp = i
                elif i['name'] == 'Stone Slammer' and i['village'] == 'home':
                    Stone_Slammer = i
                elif i['name'] == 'Siege Barracks' and i['village'] == 'home':
                    Siege_Barracks = i
                elif i['name'] == 'Log Launcher' and i['village'] == 'home':
                    Log_Launcher = i

                elif i['name'] == 'L.A.S.S.I' and i['village'] == 'home':
                    LASSI = i
                elif i['name'] == 'Electro Owl' and i['village'] == 'home':
                    Electro_Owl = i
                elif i['name'] == 'Mighty Yak' and i['village'] == 'home':
                    Mighty_Yak = i
                elif i['name'] == 'Unicorn' and i['village'] == 'home':
                    Unicorn = i

            # =============================
            # 第一排
            get_text_place_color(d, fnt3, first_distance, line_high1, Barbarian)
            get_text_place_color(d, fnt3, first_distance + space, line_high1, Archer)
            get_text_place_color(d, fnt3, first_distance + space * 2, line_high1, Giant)
            get_text_place_color(d, fnt3, first_distance + space * 3, line_high1, Goblin)
            get_text_place_color(d, fnt3, first_distance + space * 4, line_high1, Wall_Breaker)
            get_text_place_color(d, fnt3, first_distance + space * 5, line_high1, Balloon)
            get_text_place_color(d, fnt3, first_distance + space * 6, line_high1, Wizard)
            get_text_place_color(d, fnt3, first_distance + space * 7, line_high1, Healer)
            get_text_place_color(d, fnt3, first_distance + space * 8, line_high1, Dragon)
            get_text_place_color(d, fnt3, first_distance + space * 9, line_high1, Peeka)
            get_text_place_color(d, fnt3, first_distance + space * 10, line_high1, Baby_Dragon)
            get_text_place_color(d, fnt3, first_distance + space * 11, line_high1, Miner)

            # =============================
            # 第二排
            get_text_place_color(d, fnt3, first_distance, line_high2, Electro_Dragon)
            get_text_place_color(d, fnt3, first_distance + space, line_high2, Yeti)
            get_text_place_color(d, fnt3, first_distance + space * 2, line_high2, Dragon_Rider)
            get_text_place_color(d, fnt3, first_distance + space * 3, line_high2, Minion)
            get_text_place_color(d, fnt3, first_distance + space * 4, line_high2, Hog_Rider)
            get_text_place_color(d, fnt3, first_distance + space * 5, line_high2, Valkyrie)
            get_text_place_color(d, fnt3, first_distance + space * 6, line_high2, Golem)
            get_text_place_color(d, fnt3, first_distance + space * 7, line_high2, Witch)
            get_text_place_color(d, fnt3, first_distance + space * 8, line_high2, Lava_Hound)
            get_text_place_color(d, fnt3, first_distance + space * 9, line_high2, Bowler)
            get_text_place_color(d, fnt3, first_distance + space * 10, line_high2, Ice_Golem)
            get_text_place_color(d, fnt3, first_distance + space * 11, line_high2, Headhunter)

            # =============================
            # 第三排
            # 英雄
            Barbarian_King = 0  # 蛮王
            Archer_Queen = 0  # 弓箭女王
            Grand_Warden = 0  # 守护者
            Royal_Champion = 0  # 闰土
            Battle_Machine = 0  # 战争机器
            for i in user_dict['heroes']:
                if i['name'] == 'Barbarian King':
                    Barbarian_King = i
                elif i['name'] == 'Archer Queen':
                    Archer_Queen = i
                elif i['name'] == 'Grand Warden':
                    Grand_Warden = i
                elif i['name'] == 'Royal Champion':
                    Royal_Champion = i
                elif i['name'] == 'Battle Machine':
                    Battle_Machine = i

            get_text_place_color(d, fnt3, first_distance, line_high3, Barbarian_King)
            get_text_place_color(d, fnt3, first_distance + space, line_high3, Archer_Queen)
            get_text_place_color(d, fnt3, first_distance + space * 2, line_high3, Grand_Warden)
            get_text_place_color(d, fnt3, first_distance + space * 3, line_high3, Royal_Champion)
            get_text_place_color(d, fnt3, first_distance + space * 4, line_high3, Battle_Machine)

            first_distance_pet = first_distance + 28
            space_pet = 55
            get_text_place_color(d, fnt3, first_distance_pet + space * 4 + space_pet, line_high3, LASSI)
            get_text_place_color(d, fnt3, first_distance_pet + space * 4 + space_pet * 2, line_high3, Electro_Owl)
            get_text_place_color(d, fnt3, first_distance_pet + space * 4 + space_pet * 3, line_high3, Mighty_Yak)
            get_text_place_color(d, fnt3, first_distance_pet + space * 4 + space_pet * 4, line_high3, Unicorn)
            get_text_place_color(d, fnt3, first_distance_pet + space * 4 + space_pet * 5, line_high3, Wall_Wrecker)
            get_text_place_color(d, fnt3, first_distance_pet + space * 4 + space_pet * 6, line_high3, Battle_Blimp)
            get_text_place_color(d, fnt3, first_distance_pet + space * 4 + space_pet * 7, line_high3, Stone_Slammer)
            get_text_place_color(d, fnt3, first_distance_pet + space * 4 + space_pet * 8, line_high3, Siege_Barracks)
            get_text_place_color(d, fnt3, first_distance_pet + space * 4 + space_pet * 9, line_high3, Log_Launcher)

            # =============================
            # 第四排
            first_distance2 = 112
            space2 = 68
            get_text_place_color2(d, fnt3, first_distance2, line_high4, Super_Barbarian, Barbarian)
            get_text_place_color2(d, fnt3, first_distance2 + space2, line_high4, Super_Archer, Archer)
            get_text_place_color2(d, fnt3, first_distance2 + space2 * 2, line_high4, Super_Giant, Giant)
            get_text_place_color2(d, fnt3, first_distance2 + space2 * 3, line_high4, Super_Goblin, Goblin)
            get_text_place_color2(d, fnt3, first_distance2 + space2 * 4, line_high4, Super_Wall_Breaker, Wall_Breaker)
            get_text_place_color2(d, fnt3, first_distance2 + space2 * 5, line_high4, Rocket_Balloon, Balloon)
            get_text_place_color2(d, fnt3, first_distance2 + space2 * 6, line_high4, Super_Wizard, Wizard)
            get_text_place_color2(d, fnt3, first_distance2 + space2 * 7, line_high4, Inferno_Dragon, Baby_Dragon)
            get_text_place_color2(d, fnt3, first_distance2 + space2 * 8, line_high4, Super_Minion, Minion)
            get_text_place_color2(d, fnt3, first_distance2 + space2 * 9, line_high4, Super_Valkyrie, Valkyrie)
            get_text_place_color2(d, fnt3, first_distance2 + space2 * 10, line_high4, Super_Witch, Witch)
            get_text_place_color2(d, fnt3, first_distance2 + space2 * 11, line_high4, Ice_Hound, Lava_Hound)

            # =============================
            # 第五排
            Lightning = 0
            Healing = 0
            Rage = 0
            Jump = 0
            Freeze = 0
            Clone = 0
            Invisibility = 0
            Poison = 0
            Earthquake = 0
            Haste = 0
            Skeleton = 0
            Bat = 0

            if user_dict.__contains__('spells'):
                for i in user_dict['spells']:
                    if i['name'] == 'Lightning Spell':
                        Lightning = i
                    elif i['name'] == 'Healing Spell':
                        Healing = i
                    elif i['name'] == 'Rage Spell':
                        Rage = i
                    elif i['name'] == 'Jump Spell':
                        Jump = i
                    elif i['name'] == 'Freeze Spell':
                        Freeze = i
                    elif i['name'] == 'Clone Spell':
                        Clone = i
                    elif i['name'] == 'Invisibility Spell':
                        Invisibility = i

                    elif i['name'] == 'Poison Spell':
                        Poison = i
                    elif i['name'] == 'Earthquake Spell':
                        Earthquake = i
                    elif i['name'] == 'Haste Spell':
                        Haste = i
                    elif i['name'] == 'Skeleton Spell':
                        Skeleton = i
                    elif i['name'] == 'Bat Spell':
                        Bat = i

            get_text_place_color(d, fnt3, first_distance, line_high5, Lightning)
            get_text_place_color(d, fnt3, first_distance + space, line_high5, Healing)
            get_text_place_color(d, fnt3, first_distance + space * 2, line_high5, Rage)
            get_text_place_color(d, fnt3, first_distance + space * 3, line_high5, Jump)
            get_text_place_color(d, fnt3, first_distance + space * 4, line_high5, Freeze)
            get_text_place_color(d, fnt3, first_distance + space * 5, line_high5, Clone)
            get_text_place_color(d, fnt3, first_distance + space * 6, line_high5, Invisibility)
            get_text_place_color(d, fnt3, first_distance + space * 7, line_high5, Poison)
            get_text_place_color(d, fnt3, first_distance + space * 8, line_high5, Earthquake)
            get_text_place_color(d, fnt3, first_distance + space * 9, line_high5, Haste)
            get_text_place_color(d, fnt3, first_distance + space * 10, line_high5, Skeleton)
            get_text_place_color(d, fnt3, first_distance + space * 11, line_high5, Bat)

            # =============================
            # 第六排
            get_text_place_color(d, fnt3, first_distance, line_high6, Raged_Barbarian)
            get_text_place_color(d, fnt3, first_distance + space, line_high6, Sneaky_Archer)
            get_text_place_color(d, fnt3, first_distance + space * 2, line_high6, Boxer_Giant)
            get_text_place_color(d, fnt3, first_distance + space * 3, line_high6, Beta_Minion)
            get_text_place_color(d, fnt3, first_distance + space * 4, line_high6, Bomber)
            get_text_place_color(d, fnt3, first_distance + space * 5, line_high6, Baby_Dragon2)
            get_text_place_color(d, fnt3, first_distance + space * 6, line_high6, Cannon_Cart)
            get_text_place_color(d, fnt3, first_distance + space * 7, line_high6, Night_Witch)
            get_text_place_color(d, fnt3, first_distance + space * 8, line_high6, Drop_Ship)
            get_text_place_color(d, fnt3, first_distance + space * 9, line_high6, Super_Pekka)
            get_text_place_color(d, fnt3, first_distance + space * 10, line_high6, Hog_Glider)

            out = Image.alpha_composite(base, txt)
            # out.show()
            file_path = 'data/clash/temp/' + tag + '.png'
            out.save(file_path)
            return '完成'
        elif res.status_code == 404:
            return '玩家#' + tag + '不存在'
        else:
            print('报错信息：' + str(res.text))
            return '爬虫已失效（code：' + str(res.status_code) + '），请前往官方群反馈给主人，群号可以通过添加小柒好友获得'

    async def clan(self, tag):
        if self.clan_cache.__contains__(tag):
            pass

        global api_token
        url = "https://api.clashofclans.com/v1/clans/%23" + tag

        params = {
            "Authorization": "Bearer " + api_token
        }

        res = requests.get(url=url, params=params)
        if res.status_code == 200:
            clan_dict = json.loads(res.text)
            base = Image.open('data/Clash/部落空白卡.jpg').convert('RGBA')
            # make a blank image for the text, initialized to transparent text color
            txt = Image.new('RGBA', base.size, (255, 255, 255, 0))
            # 设置字体
            font_size = 60
            fnt = ImageFont.truetype('data/Font/FZHTJT.TTF', font_size)
            font_size2 = 38
            fnt2 = ImageFont.truetype('data/Font/FZHTJT.TTF', font_size2)
            # get a drawing context
            d = ImageDraw.Draw(txt)

            fill_black = '#000000'
            gray_color = '#E77524'
            green_color = '#00EE00'
            red_color = '#FF4500'

            icon = requests.get(url=clan_dict['badgeUrls']['small'], params=params)
            if icon.status_code == 200:
                image_path = 'data/clash/temp/icon_' + tag + '.png'
                with open(image_path, 'wb') as f:
                    f.write(icon.content)
                image1 = Image.open(image_path)
                image1 = image1.resize((350, 350), Image.ANTIALIAS)
                base.paste(image1, (100, 150))
            else:
                text = '【图标获取失败】'
                d.text((200, 200), text, font=fnt, fill=gray_color)

            # ====================================
            # 基本信息
            text = str(clan_dict['name'])
            d.text((890, 110), text, font=fnt, fill=fill_black)
            text = str(clan_dict['tag'])
            d.text((890, 210), text, font=fnt, fill=fill_black)
            text = str(len(clan_dict['memberList']))
            d.text((890, 305), text, font=fnt, fill=fill_black)
            text = str(clan_dict['warLeague']['name'])
            if text == 'Unranked':
                text = '未排位'
            elif text.startswith('Bronze'):
                text = '青铜' + get_roman_number(text)
            elif text.startswith('Silver'):
                text = '白银' + get_roman_number(text)
            elif text.startswith('Gold'):
                text = '黄金' + get_roman_number(text)
            elif text.startswith('Crystal'):
                text = '水晶' + get_roman_number(text)
            elif text.startswith('Master'):
                text = '大师' + get_roman_number(text)
            elif text.startswith('Champion'):
                text = '冠军' + get_roman_number(text)
            elif text.startswith('Titan'):
                text = '泰坦' + get_roman_number(text)
            elif text.startswith('Legend'):
                text = '传奇'
            d.text((890, 400), text, font=fnt, fill=fill_black)

            text = '主' + str(clan_dict['clanPoints']) + '/夜' + str(clan_dict['clanVersusPoints'])
            d.text((1840, 110), text, font=fnt, fill=fill_black)
            text = str(clan_dict['type'])
            if text == 'inviteOnly':
                text = '只有被批准才能加入'
            elif text == 'closed':
                text = '不可加入'
            elif text == 'open':
                text = '任何人都可加入'
            d.text((1840, 210), text, font=fnt, fill=fill_black)
            text = '主' + str(clan_dict['requiredTrophies']) + '/夜' + str(clan_dict['requiredVersusTrophies'])
            d.text((1840, 305), text, font=fnt, fill=fill_black)
            text = '主' + str(clan_dict['requiredTownhallLevel'])
            d.text((1900, 400), text, font=fnt, fill=fill_black)

            # ====================================
            # 成员信息
            first_line = 820
            space = 52.9
            index = 0
            level_14 = 0
            level_13 = 0
            level_12 = 0
            level_11 = 0
            level_10 = 0
            level_9 = 0

            clan_member = []
            async def request_clan_member(tag):
                user_url = "https://api.clashofclans.com/v1/players/%23" + tag[1:]
                user_res = requests.get(url=user_url, params=params)
                if user_res.status_code == 200:
                    clan_member.append(json.loads(user_res.text))
            tasks = []
            for member in clan_dict['memberList']:
                tasks.append(request_clan_member(member['tag']))
            start = time.time()
            print('开始爬数据')
            await asyncio.gather(asyncio.wait(tasks))

            end = time.time()
            print('run seconds:', end - start)
            for user_dict in clan_member:
                user_name = user_dict['name']
                if len(user_name) > 6:
                    print(user_name)
                    user_name = user_name[:6] + '…'
                user_tag = user_dict['tag']
                user_level = '（获取失败）'
                user_donation = user_dict['donations']
                user_recieve = user_dict['donationsReceived']
                user_win = '（获取失败）'

                if user_dict['townHallLevel'] == 14:
                    level_14 += 1
                if user_dict['townHallLevel'] == 13:
                    level_13 += 1
                if user_dict['townHallLevel'] == 12:
                    level_12 += 1
                if user_dict['townHallLevel'] == 11:
                    level_11 += 1
                if user_dict['townHallLevel'] == 10:
                    level_10 += 1
                if user_dict['townHallLevel'] <= 9:
                    level_9 += 1
                user_level = str(user_dict['townHallLevel']) + '本'
                if user_dict.__contains__('townHallWeaponLevel'):
                    user_level += str(user_dict['townHallWeaponLevel']) + '星'
                user_win = str(user_dict['attackWins'])

                d.text((80, first_line + space * index), user_name, font=fnt2, fill=fill_black)
                d.text((350, first_line + space * index), user_tag, font=fnt2, fill=fill_black)
                d.text((700, first_line + space * index), user_level, font=fnt2, fill=fill_black)

                d.text((935, first_line + space * index), '%6d' % (user_donation), font=fnt2, fill=fill_black)
                d.text((1055, first_line + space * index), '/', font=fnt2, fill=fill_black)
                d.text((1080, first_line + space * index), '%-6d' % (user_recieve), font=fnt2, fill=fill_black)

                d.text((1300, first_line + space * index), user_win, font=fnt2, fill=fill_black)
                d.text((1500, first_line + space * index), '（未启用）', font=fnt2, fill=fill_black)
                index += 1

            d.text((700, 533), str(level_14), font=fnt, fill=fill_black)
            d.text((1000, 533), str(level_13), font=fnt, fill=fill_black)
            d.text((1300, 533), str(level_12), font=fnt, fill=fill_black)
            d.text((1600, 533), str(level_11), font=fnt, fill=fill_black)
            d.text((1900, 533), str(level_10), font=fnt, fill=fill_black)
            d.text((2350, 533), str(level_9), font=fnt, fill=fill_black)

            out = Image.alpha_composite(base, txt)
            out = transparence2white(out)
            file_path = 'data/clash/temp/clan_' + tag + '.png'
            out.save(file_path)
            return '完成'
        elif res.status_code == 404:
            return '部落#' + tag + '不存在'
        else:
            print(res.text)
            return '爬虫已失效（code：' + str(res.status_code) + '），请前往官方群反馈给主人，群号可以通过添加小柒好友获得'

    async def handle(self, bot, event, message, group_id, qq, group_config, user_config):
        need_reply = False
        reply_text = ''
        reply_image = ''

        message = message.replace(' ', '').lower()

        clan_wait_reply = '部落查询可能需要几分钟的时间，请耐心等待。\n请勿利用频繁部落查询卡死小柒，一旦查到如此情况直接拉黑。'
        clan_refuse = '目前访问量过高，自动拒绝查询'

        if message == 'coc鱼情' or message == 'coc鱼':
            need_reply = True
            reply_image = 'data/Clash/鱼情.png'
            reply_text = self.fish()

        elif message == 'coc清空标签':
            user_config['config']['clash_user_tag'].clear()
            user_config['config']['main_clash_user_tag'] = 0
            user_config['config']['clash_tag'].clear()
            user_config['config']['main_clash_tag'] = 0
            dataManage.save_user(qq, user_config)
            need_reply = True
            reply_text = '已经清空你的标签'
        elif message[:8] == 'coc绑定玩家#':
            if len(message) > 8:
                need_reply = True
                if message[8:] not in user_config['config']['clash_user_tag']:
                    user_config['config']['clash_user_tag'].append(message[8:])
                    print(user_config['config']['clash_user_tag'])
                    dataManage.save_user(qq, user_config)
                    reply_text = '成功绑定玩家标签#' + message[8:].upper()
                else:
                    reply_text = '已经绑定了该标签'
        elif message[:8] == 'coc绑定部落#':
            if len(message) > 8:
                need_reply = True
                if message[8:] not in user_config['config']['clash_tag']:
                    user_config['config']['clash_tag'].append(message[8:])
                    dataManage.save_user(qq, user_config)
                    reply_text = '成功绑定部落标签#' + message[8:].upper()
                else:
                    reply_text = '已经绑定了该标签'
        elif message[:8] == 'coc删除玩家#':
            reply_text = '标签不存在！'
            tag = message[8:]
            del_index = -1
            for index in range(0, len(user_config['config']['clash_user_tag'])):
                if tag == user_config['config']['clash_user_tag'][index]:
                    del_index = index
            if del_index >= 0:
                if del_index == user_config['config']['main_clash_user_tag']:
                    user_config['config']['main_clash_user_tag'] = 0
                elif del_index < user_config['config']['main_clash_user_tag']:
                    user_config['config']['main_clash_user_tag'] -= 1
                del user_config['config']['clash_user_tag'][del_index]
                dataManage.save_user(qq, user_config)
                reply_text = '删除成功！'
            need_reply = True
        elif message[:8] == 'coc删除部落#':
            reply_text = '标签不存在！'
            tag = message[8:]
            del_index = -1
            for index in range(0, len(user_config['config']['clash_tag'])):
                if tag == user_config['config']['clash_tag'][index]:
                    del_index = index
            if del_index >= 0:
                if del_index == user_config['config']['main_clash_tag']:
                    user_config['config']['main_clash_tag'] = 0
                elif del_index < user_config['config']['main_clash_tag']:
                    user_config['config']['main_clash_tag'] -= 1
                del user_config['config']['clash_tag'][del_index]
                dataManage.save_user(qq, user_config)
                reply_text = '删除成功！'
            need_reply = True
        elif message[:9] == 'coc设置默认玩家' and message[9:].isdigit():
            need_reply = True
            index = int(message[9:]) - 1
            if len(user_config['config']['clash_user_tag']) > index >= 0:
                user_config['config']['main_clash_user_tag'] = index
                dataManage.save_user(qq, user_config)
                reply_text = '修改成功！'
            else:
                reply_text = '没有序号对应的标签'
        elif message[:9] == 'coc设置默认部落' and message[9:].isdigit():
            need_reply = True
            index = int(message[9:]) - 1
            if len(user_config['config']['clash_tag']) > index >= 0:
                user_config['config']['main_clash_tag'] = index
                dataManage.save_user(qq, user_config)
                reply_text = '修改成功！'
            else:
                reply_text = '没有序号对应的标签'
        elif message[:7] == 'coc删除玩家' and message[7:].isdigit():
            need_reply = True
            index = int(message[7:]) - 1
            if len(user_config['config']['clash_user_tag']) > index >= 0:
                del user_config['config']['clash_user_tag'][index]
                if index == user_config['config']['main_clash_user_tag']:
                    user_config['config']['main_clash_user_tag'] = 0
                elif index < user_config['config']['main_clash_user_tag']:
                    user_config['config']['main_clash_user_tag'] -= 1
                dataManage.save_user(qq, user_config)

                reply_text = '删除成功'
            else:
                reply_text = '没有序号对应的标签'
        elif message[:7] == 'coc删除部落' and message[7:].isdigit():
            need_reply = True
            index = int(message[7:]) - 1
            if len(user_config['config']['clash_tag']) > index >= 0:
                del user_config['config']['clash_tag'][index]
                if index == user_config['config']['main_clash_tag']:
                    user_config['config']['main_clash_tag'] = 0
                elif index < user_config['config']['main_clash_tag']:
                    user_config['config']['main_clash_tag'] -= 1
                dataManage.save_user(qq, user_config)

                reply_text = '删除成功'
            else:
                reply_text = '没有序号对应的标签'

        elif message == 'coc部落':
            need_reply = True
            if len(user_config['config']['clash_tag']) > 0:
                await bot.send(event, clan_wait_reply)
                reply_text = await self.clan(user_config['config']['clash_tag'][user_config['config']['main_clash_tag']])
                if reply_text == '完成':
                    reply_image = 'data/clash/temp/clan_' + user_config['config']['clash_tag'][
                        user_config['config']['main_clash_tag']] + '.png'
                    reply_text = ''
            else:
                reply_text = '你目前没有绑定部落标签'
        elif message == 'coc玩家':
            need_reply = True
            if len(user_config['config']['clash_user_tag']) > 0:
                reply_text = self.player(
                    user_config['config']['clash_user_tag'][user_config['config']['main_clash_user_tag']])
                if reply_text == '完成':
                    reply_image = 'data/clash/temp/' + user_config['config']['clash_user_tag'][
                        user_config['config']['main_clash_user_tag']] + '.png'
                    reply_text = ''
            else:
                reply_text = '你目前没有绑定玩家标签'
        elif message[:6] == 'coc部落#':
            await bot.send(event, clan_wait_reply)
            need_reply = True
            tag = message[6:]
            reply_text = await self.clan(tag)
            if reply_text == '完成':
                reply_image = 'data/clash/temp/clan_' + tag + '.png'
                reply_text = ''
        elif message[:6] == 'coc玩家#':
            need_reply = True
            tag = message[6:]
            reply_text = self.player(tag)
            if reply_text == '完成':
                reply_image = 'data/clash/temp/' + tag + '.png'
                reply_text = ''
        elif message[:5] == 'coc部落' and message[5:].isdigit():
            index = int(message[5:]) - 1
            if len(user_config['config']['clash_tag']) > index >= 0:
                await bot.send(event, clan_wait_reply)
                reply_text = await self.clan(user_config['config']['clash_tag'][index])
                if reply_text == '完成':
                    reply_image = 'data/clash/temp/clan_' + user_config['config']['clash_tag'][index] + '.png'
                    reply_text = ''
            else:
                reply_text = '没有序号对应的标签'
            need_reply = True
        elif message[:5] == 'coc玩家' and message[5:].isdigit():
            index = int(message[5:]) - 1
            if len(user_config['config']['clash_user_tag']) > index >= 0:
                reply_text = self.player(user_config['config']['clash_user_tag'][index])
                if reply_text == '完成':
                    reply_image = 'data/clash/temp/' + user_config['config']['clash_user_tag'][index] + '.png'
                    reply_text = ''
            else:
                reply_text = '没有序号对应的标签'
            need_reply = True
        elif message[:6] == 'coc部落@' and message[6:].isdigit():
            new_qq = int(message[6:])
            new_user = dataManage.read_user(new_qq)
            need_reply = True
            if len(new_user['config']['clash_tag']) > 0:
                await bot.send(event, clan_wait_reply)
                reply_text = await self.clan(new_user['config']['clash_tag'][new_user['config']['main_clash_tag']])
                if reply_text == '完成':
                    reply_image = 'data/clash/temp/clan_' + user_config['config']['clash_tag'][
                        user_config['config']['main_clash_tag']] + '.png'
                    reply_text = ''
            else:
                reply_text = '你目前没有绑定部落标签'
        elif message[:6] == 'coc玩家@' and message[6:].isdigit():
            new_qq = int(message[6:])
            new_user = dataManage.read_user(new_qq)
            need_reply = True
            if len(new_user['config']['clash_user_tag']) > 0:
                reply_text = self.player(
                    new_user['config']['clash_user_tag'][new_user['config']['main_clash_user_tag']])
                if reply_text == '完成':
                    reply_image = 'data/clash/temp/' + new_user['config']['clash_user_tag'][
                        new_user['config']['main_clash_user_tag']] + '.png'
                    reply_text = ''
            else:
                reply_text = '对方目前没有绑定玩家标签'

        elif message == 'coc标签':
            reply_text = '玩家标签：'
            for i in range(0, len(user_config['config']['clash_user_tag'])):
                reply_text += '\n' + str(i + 1) + '.' + user_config['config']['clash_user_tag'][i]
                if i == user_config['config']['main_clash_user_tag']:
                    reply_text += '（默认标签）'
            if len(user_config['config']['clash_user_tag']) == 0:
                reply_text += '\n无'
            reply_text += '\n---------------'
            reply_text += '\n部落标签：'
            for i in range(0, len(user_config['config']['clash_tag'])):
                reply_text += '\n' + str(i + 1) + '.' + user_config['config']['clash_tag'][i]
                if i == user_config['config']['main_clash_tag']:
                    reply_text += '（默认标签）'
            if len(user_config['config']['clash_tag']) == 0:
                reply_text += '\n无'
            need_reply = True

        elif message == 'coc商人' or message == 'coc商人刷新表' or message == 'coc商店':
            reply_image = 'data/Clash/商人数据.jpg'
            need_reply = True
        elif message == 'coc联赛' or message == 'coc联赛奖励':
            reply_image = 'data/Clash/联赛数据.jpg'
            need_reply = True
        elif message == 'coc部落战奖励':
            reply_image = 'data/Clash/部落战奖励.png'
            need_reply = True
        elif message == 'coc闪电机制':
            reply_image = 'data/Clash/闪电机制.jpg'
            need_reply = True
        elif message == 'coc闪震计算表':
            reply_image = 'data/Clash/闪震计算表.png'
            need_reply = True
        elif message == 'coc夜世界奖励':
            reply_image = 'data/Clash/夜世界奖励.png'
            need_reply = True
        elif message == 'coc援军等级限制':
            reply_image = 'data/Clash/援军等级限制.png'
            need_reply = True

        return need_reply, reply_text, reply_image
