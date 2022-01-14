
import requests

def getReply(config, AtMessage):
    Bot_Name = config['name']
    quest = AtMessage
    url = 'http://api.qingyunke.com/api.php?key=free&appid=0&msg=' + quest #请求接口

    req = requests.get(url)#发送请求
    reply = req.text
    reply = reply.replace('菲菲', Bot_Name)
    return reply[23:-2]