# 查看所有数据
import pickle

# ==========================================================
# 新的存储方式
def save_obj(obj, name):
    with open('data/'+ name + '.pkl', 'wb') as f:
        pickle.dump(obj, f, pickle.HIGHEST_PROTOCOL)
 
def load_obj(name):
    with open('data/' + name + '.pkl', 'rb') as f:
        return pickle.load(f)

if __name__ == '__main__':
    botBaseInformation = load_obj('baseInformation')
    clockIn = load_obj('clockIn')
    luck = load_obj('luck')

    print(botBaseInformation)
    print(clockIn)
    print(luck)
    print(botBaseInformation['administrator'])