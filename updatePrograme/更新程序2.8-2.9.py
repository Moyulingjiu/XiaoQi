# 从2.8版本更新到2.9
# 主要引入内容为锁定打卡计划，和打卡天数

import pickle

def save_obj(obj, name):
    with open('data/'+ name + '.pkl', 'wb') as f:
        pickle.dump(obj, f, pickle.HIGHEST_PROTOCOL)
 
def load_obj(name):
    with open('data/' + name + '.pkl', 'rb') as f:
        return pickle.load(f)
        

if __name__ == '__main__':
    screenWords = ['老公', '草', '为获取到相关信息', '上课', '{', '暂时没这功能', '佢應', '這樣', '不要啊皇上', '有錢會好D囉', '太监', '聽']
    keyReply = {}
    botBaseInformation = load_obj('baseInformation')

    print(botBaseInformation)
    save_obj(screenWords, 'AIScreenWords')
    save_obj(keyReply, 'keyReply')
    print(screenWords)
    pass