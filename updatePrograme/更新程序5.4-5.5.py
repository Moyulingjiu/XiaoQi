import pickle
import os

def save_obj(obj, name):
    filePath = 'data/' + name + '.pkl'
    with open(filePath, 'wb') as f:
        pickle.dump(obj, f, pickle.HIGHEST_PROTOCOL)
 
def load_obj(name):
    filePath = 'data/' + name + '.pkl'
    if not os.path.exists(filePath):
        return {}
    with open(filePath, 'rb') as f:
        return pickle.load(f)

if __name__ == '__main__':
    botBaseInformation = load_obj('baseInformation')

    botBaseInformation['gamelimit'] = [] # 游戏专注模式
    
    print(botBaseInformation)
    save_obj(botBaseInformation, 'baseInformation')

    
    user = load_obj('user/information')

    for key, value in user.items():
        user[key]['occupation'] = {
                'work': '',  # 生活职业
                'battle': ''  # 战斗职业
        }

    print(user)
    save_obj(user, 'user/information')
    print('done!')