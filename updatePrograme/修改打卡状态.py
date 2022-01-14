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

# 该程序是给管理员用的，不是给用户用的

if __name__ == '__main__':
    clockIn = load_obj('clockIn')

    print(clockIn)
    id = int(input('请输入要修改的QQ号：'))
    groupId = int(input('请输入群号：'))
    state = input('请问要修改为什么（1表示打卡，0表示未打卡）：')

    if state == '1':
        if clockIn['dictClockPeople'].__contains__(groupId):
            if clockIn['dictClockPeople'][groupId].__contains__(id):
                clockIn['dictClockPeople'][groupId][id]['clockIn'] = True
    elif state == '0':
        if clockIn['dictClockPeople'].__contains__(groupId):
            if clockIn['dictClockPeople'][groupId].__contains__(id):
                clockIn['dictClockPeople'][groupId][id]['clockIn'] = False
                print('修改成功！')

    save_obj(clockIn, 'clockIn')
    print(clockIn)