# 读写数据

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
    botBaseInformation['pixiv'] = []  # 群聊命令关键词
    print(botBaseInformation)
    save_obj(botBaseInformation, 'baseInformation')
    print('done!')
