
import requests        #导入requests包
from bs4 import BeautifulSoup
import random
import time
import socket  # 用做异常处理


def get_html(url, data=None):
    """
    模拟浏览器来获取网页的html代码
    """
    header = {
        'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8',
        'Accept-Encoding': 'gzip, deflate, sdch',
        'Accept-Language': 'zh-CN,zh;q=0.8',
        'Connection': 'keep-alive',
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
        except http.client.BadStatusLine as e:
            print("5:", e)
            time.sleep(random.choice(range(30, 80)))

        except http.client.IncompleteRead as e:
            print("6:", e)
            time.sleep(random.choice(range(5, 15)))

    return rep.text

def getHot():
    url = "https://www.baidu.com/"

    response = get_html(url)
    soup = BeautifulSoup(response,'html.parser')

    data = soup.select('li.hotsearch-item > a:nth-child(1) > span:nth-child(2)')

    result = ''
    for index in range(0, 6):
        i = data[index]
        contain = i.get_text()
        result += str(index + 1) + ':' + contain + '\n'

    return result[:-1]