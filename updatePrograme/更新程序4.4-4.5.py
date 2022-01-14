# 查看所有数据
import pickle

# ==========================================================
# 新的存储方式
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
    user = load_obj('user/information')
    for key, value in user.items():
        user[key]['warehouse'] = {}
    save_obj(user, 'user/information')

    systemData = load_obj('user/system')
    systemData['rampage-4'] = {} # 暴走模式（4级）
    systemData['defense-4'] = {} # 防御模式（4级）
    systemData['rampage-5'] = {} # 暴走模式（5级）
    systemData['defense-5'] = {} # 防御模式（5级）
    save_obj(systemData, 'user/system')
    print(systemData)
    print('done!')
