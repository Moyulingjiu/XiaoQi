# 从2.10版本更新到2.11
# 主要引入内容为锁定打卡计划，和打卡天数

import pickle

def save_obj(obj, name):
    with open('data/'+ name + '.pkl', 'wb') as f:
        pickle.dump(obj, f, pickle.HIGHEST_PROTOCOL)
 
def load_obj(name):
    with open('data/' + name + '.pkl', 'rb') as f:
        return pickle.load(f)


        
if __name__ == '__main__':
    botBaseInformation = load_obj('baseInformation')

    botBaseInformation['reply'] = {}
    botBaseInformation['reply']['lastMinute'] = 0
    save_obj(botBaseInformation, 'baseInformation')
    print(botBaseInformation)