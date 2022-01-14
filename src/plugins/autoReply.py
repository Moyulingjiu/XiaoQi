import random

from plugins import AIchat
from plugins import dataManage

# 自动回复部分

screenWords = []
unknown_reply = ['诶？', '你说的话太深奥了', '我也不是很清楚呢', '不知道哦~', '你猜', '这是什么意思呢？', '嘤嘤嘤~我听不懂']


def reply(message, be_at, config, statistics, nickname, group_id, qq, mode):
    global screenWords
    screenWords = dataManage.read_screen_word()

    bot_qq = config['qq']
    bot_name = config['name']
    need_reply = False
    need_at = False
    reply_text = ''
    reply_image = ''
    
    if be_at:
        need_reply, need_at, reply_text, reply_image = forced_reply(config, bot_name, nickname, message)
    else:
        need_reply, need_at, reply_text, reply_image = self_reply(statistics, config, bot_name, nickname, message)
    
    if need_reply:
        for i in screenWords:
            if i in reply_text:
                if be_at:
                    reply_text = random.choice(unknown_reply)
                else:
                    need_reply = False
                    reply_text = ''
                    reply_image = ''
                break

    return need_reply, reply_text, reply_image, 0, need_at


def forced_reply(config, bot_name, nickname, message):
    need_reply = False
    need_at = False
    reply_text = ''
    reply_image = ''

    need_reply, need_at, reply_text, reply_image = reply_word(bot_name, nickname, message)

    if not need_reply:  # 调用ai
        reply_text = AIchat.getReply(config, message)
        need_reply = True

    return need_reply, need_at, reply_text, reply_image


def self_reply(statistics, config, bot_name, nickname, message):
    need_reply = False
    need_at = False
    reply_text = ''
    reply_image = ''

    # 强制触发词
    if message == 'yjy爬':
        reply_text = 'yjy快爬'
        need_reply = True
    elif message == '来一张涩图':
        reply_text = '能不能多读书，少看涩图'
        need_reply = True
    elif message == '骂我':
        reply_text = '咦惹？你是弱0吗'
        need_reply = True
    elif message == '晚安' or message == '安安' or message == '晚':
        reply_list = ['晚安', 'image晚安', '晚安哦' + nickname, '记得要梦见' + bot_name, '快睡吧']
        reply_text = reply_list[random.randrange(0, len(reply_list))]
        if reply_text == 'image晚安':
            reply_text = ''
            reply_image = 'data/AutoReply/晚安.png'
        need_reply = True
    elif message == '早安' or message == '早' or message == '早上好':
        reply_list = ['早安', '早鸭~', '早安哦' + nickname, '小懒猪，你比' + bot_name + '晚起了好多', '又是元气满满的一天呢']
        reply_text = reply_list[random.randrange(0, len(reply_list))]
        need_reply = True
    elif message == '午安' or message == '睡午觉了':
        reply_text = '午安呀！' + nickname
        need_reply = True
    elif message == '中午好':
        reply_text = '中午好鸭！' + nickname
        need_reply = True
    elif message == '晚上好':
        reply_text = '晚上好鸭！' + nickname
        need_reply = True
    if need_reply:
        return need_reply, need_at, reply_text, reply_image

    # 非强制触发词回复内容
    rand = random.randrange(0, 10)
    if rand > 4:
        return need_reply, need_at, reply_text, reply_image
    need_reply, need_at, reply_text, reply_image = reply_word(bot_name, nickname, message)

    if not need_reply:
        tmpNumber = random.randrange(0, 1000)
        if tmpNumber < 10:
            if statistics['last_minute'] <= 10:
                reply_text = AIchat.getReply(config, message)
                need_reply = True
                if need_reply:
                    statistics['last_minute'] += 1
                    dataManage.save_statistics(statistics)

    return need_reply, need_at, reply_text, reply_image


def reply_word(bot_name, nickname, message):
    need_reply = False
    need_at = False
    reply_text = ''
    reply_image = ''

    if message == '你好':
        reply_text = '你好呀，' + nickname + '。小柒很高兴遇见你！'
        need_at = True
        need_reply = True
    elif message == '抱抱':
        reply_list = ['抱抱呀！', bot_name + '才不要和你抱抱！', '抱抱', '抱抱' + nickname]
        reply_text = reply_list[random.randrange(0, len(reply_list))]
        need_reply = True
    elif message == '贴贴':
        reply_list = ['贴贴', 'image贴贴', '快来贴贴，嘿嘿！', '不贴不贴']
        reply_text = reply_list[random.randrange(0, len(reply_list))]
        if reply_text == 'image贴贴':
            reply_text = ''
            reply_image = 'data/AutoReply/贴贴.jpg'
        need_reply = True
    elif message == '晚安' or message == '安安':
        reply_list = ['晚安', 'image晚安', '晚安哦' + nickname, '记得要梦见' + bot_name, '快睡吧']
        reply_text = reply_list[random.randrange(0, len(reply_list))]
        if reply_text == 'image晚安':
            reply_text = ''
            reply_image = 'data/AutoReply/晚安.png'
        need_reply = True
    elif message == '早安' or message == '早':
        reply_text = '早哦，' + nickname
        need_reply = True
    elif message == '午安' or message == '睡午觉了':
        reply_text = '午安呀！' + nickname
        need_reply = True

    elif message == '谢谢':
        reply_list = ['嘿嘿', '不用谢啦', '要时刻想着' + bot_name, '没事啦']
        reply_text = reply_list[random.randrange(0, len(reply_list))]
    elif message == '快来' or message == '快来快来':
        reply_list = ['游戏启动', '来了来了', '不要着急嘛']
        reply_text = reply_list[random.randrange(0, len(reply_list))]
        need_reply = True

    elif message == '傻子':
        reply_text = '你才是傻子，' + bot_name + '才不傻'
        need_reply = True
    elif message == '笨蛋':
        reply_text = bot_name + '才不要理你了'
        need_reply = True
    elif message == '蠢货':
        reply_text = '哼'
        need_reply = True
    elif message == '你是猪吗' or message == '猪':
        reply_text = '你以为谁都像你一天天哼唧哼唧的'
        need_reply = True
    elif message == '人工智障':
        reply_text = '哎呀呀呀！我已经很努力了QAQ'
        need_reply = True

    elif message == '爱不爱我' or message == '爱我吗':
        reply_list = ['爱你鸭！', bot_name + '超级爱你的~', '不爱，略略略']
        reply_text = reply_list[random.randrange(0, len(reply_list))]
        need_reply = True
    elif message == '喜不喜欢我' or message == '喜欢我吗':
        reply_list = ['喜欢你鸭！', bot_name + '超级喜欢你的~', '不喜欢，略略略']
        reply_text = reply_list[random.randrange(0, len(reply_list))]
        need_reply = True

    elif message == '我是fw' or message == '我是废物':
        reply_text = '在' + bot_name + '心中，' + nickname + '一直都很厉害的哦~'
        need_reply = True
    elif message == '摸了' or message == '摸鱼' or message == '摸鱼了':
        reply_text = nickname + '桑怎么可以摸鱼呢'
        need_reply = True
    elif message == '也不是不行':
        reply_text = nickname + '那就快冲！'
        need_reply = True

    elif message == '？' or message == '?':
        tmpNumber = random.randrange(0, 10)
        if tmpNumber == 2:
            reply_list = ['怎么啦？', '嗯哼？']
            reply_text = reply_list[random.randrange(0, len(reply_list))]
            need_reply = True
        elif tmpNumber == 1:
            reply_image = 'data/AutoReply/问号.jpg'
            need_reply = True
    elif message == '好家伙':
        tmpNumber = random.randrange(0, 5)
        if tmpNumber == 3:
            reply_list = ['又发生什么辣', '又有什么大事情吗', '什么大事件']
            reply_text = reply_list[random.randrange(0, len(reply_list))]
            need_reply = True
        elif tmpNumber == 1:
            reply_image = 'data/AutoReply/问号.jpg'
            need_reply = True

    elif message == '有人ow吗':
        reply_text = bot_name + '也想来'
        need_reply = True
    elif message[-2:] == '快来':
        reply_text = bot_name + '来了来了'
        need_reply = True
    elif message[-3:] == '多好啊':
        reply_text = '是呀是呀'
        need_reply = True
    elif message == '上课':
        reply_text = bot_name + '陪你一起上课'
        need_reply = True
    elif message == '满课':
        reply_text = '好惨哦'
        need_reply = True
    elif message == '谢谢':
        reply_text = '嘿嘿'
        need_reply = True
    elif message == '你们早上都没课的嘛':
        reply_text = bot_name + '还没有开始上课呢'
        need_reply = True
    elif message == '早八' or message == '又是早八' or message == '明天早八' or message == '我明天早八':
        reply_list = ['好惨鸭', bot_name + '抱抱你，不要哭', '摸摸头', '不哭不哭，站起来撸']
        reply_text = reply_list[random.randrange(0, len(reply_list))]
        need_reply = True
    elif message == '我不配':
        reply_list = ['人贵有自知之明', bot_name + '抱抱你，不要哭']
        reply_text = reply_list[random.randrange(0, len(reply_list))]
        need_reply = True
    
    elif message == '你主人是谁':
        reply_text = '你猜我的主人是谁~'
        need_reply = True

    return need_reply, need_at, reply_text, reply_image
