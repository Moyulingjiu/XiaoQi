import requests
from datetime import datetime


# 运势
class Luck:
    """
        获取远程服务器上的运势信息。运势信息会缓存600秒的时间。
    """
    def __init__(self):
        self.cache = {}

    def get_luck_number(self, qq):
        if self.cache.__contains__(qq):
            if (datetime.now() - self.cache[qq]['time']).seconds < 600:
                return self.cache[qq]['data']
        url: str = "http://localhost:8080/user/luck/" + str(qq) + "?botId=1812322920"
        res = requests.get(url)
        if res.status_code != 200:
            return 50
        data = res.json()
        self.cache[qq] = {
            'time': datetime.now(),
            'data': data['data']['luck']
        }
        return data['data']['luck']


if __name__ == '__main__':
    my_luck = Luck()
    print('第一次获取，从远程服务器拉取')
    print(my_luck.get_luck_number(1597867839))
    print('后两次都是从缓存中拉取出来的')
    print(my_luck.get_luck_number(1597867839))
    print(my_luck.get_luck_number(1597867839))
