# 从2.10版本更新到2.11
# 主要引入内容为自主退群和禁言

import pickle

def save_obj(obj, name):
    with open('data/'+ name + '.pkl', 'wb') as f:
        pickle.dump(obj, f, pickle.HIGHEST_PROTOCOL)
 
def load_obj(name):
    with open('data/' + name + '.pkl', 'rb') as f:
        return pickle.load(f)

botBaseInformation = load_obj('baseInformation')
botBaseInformation['mute'] = []
save_obj(botBaseInformation, 'baseInformation')

print(botBaseInformation)