import random
import requests  # 用来抓取网页的html源代码
from bs4 import BeautifulSoup  # 用来代替正则表达式取源码中相应标签的内容
import time
import socket  # 用做异常处理
import re


class pixiv:
    p_id = {}

    def __init__(self):
        pass

    def search(self, keyword):
        # https://pix.ipv4.host/illustrations?illustType=illust&searchType=original&maxSanityLevel=3&page=1&keyword=jk&pageSize=30
        print(keyword)
        url = "https://pix.ipv4.host/illustrations"
        if self.p_id.__contains__(keyword):
            self.p_id[keyword] += 1
            page = self.p_id[keyword]
        else:
            self.p_id[keyword] = 1
            page = 1
        param = {
            "illustType": "illust",
            "searchType": "original",
            "maxSanityLevel": 3,
            "page": page,
            "keyword": keyword,
            "pageSize": 1
        }
        headers = {
            "accept": "application/json, text/plain, */*",
            "accept-encoding": "gzip, deflate, br",
            "accept-language": "zh-CN,zh;q=0.9",
            "authorization": "eyJhbGciOiJIUzUxMiJ9.eyJwZXJtaXNzaW9uTGV2ZWwiOjEsInJlZnJlc2hDb3VudCI6MiwiaXNDaGVja1Bob25lIjowLCJ1c2VySWQiOjY1NDkwMCwiaWF0IjoxNjI0Mjc3MjE3LCJleHAiOjE2MjQ3OTU2MTd9.NtI-pAxNF1x5YSiz0Fh724dqh_RbglUPwG10ie6fd4FEgNsumftWEcvD00RhYlvAymKdxSGaeIooZ7u42fkfiw",
            "origin": "https://pixivic.com",
            "referer": "https://pixivic.com/",
            "sec-ch-ua": "\" Not;A Brand\";v=\"99\", \"Google Chrome\";v=\"91\", \"Chromium\";v=\"91\"",
            "sec-ch-ua-mobile": "?0",
            "sec-fetch-dest": "empty",
            "sec-fetch-mode": "cors",
            "sec-fetch-site": "cross-site",
            "user-agent": "LogStatistic"
        }
        datajson = requests.get(url, params=param, headers=headers)
        data = datajson.json()
        i = 0
        image = []
        print(data)
        print(len(data['data']))
        if data['message'] != '搜索结果获取成功':
            return image
        while i < 1:
            data_url = data['data'][i]['imageUrls'][0]["original"]
            url = re.sub(r'net', "cat", data_url)
            url = re.sub(r'pximg', "pixiv", url)
            image.append(url)
            i += 1
        print(image)
        return image
