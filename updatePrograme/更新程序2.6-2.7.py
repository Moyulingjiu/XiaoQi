# 从2.6版本更新到2.7
# 主要引入内容为锁定打卡计划，和打卡天数

import pickle

def save_obj(obj, name):
    with open('data/'+ name + '.pkl', 'wb') as f:
        pickle.dump(obj, f, pickle.HIGHEST_PROTOCOL)
 
def load_obj(name):
    with open('data/' + name + '.pkl', 'rb') as f:
        return pickle.load(f)


if __name__ == '__main__':
    clockIn = load_obj('clockIn')
    for key, value in clockIn['groupClock'].items():
        if clockIn['groupClock'][key].__contains__('lock'):
            del clockIn['groupClock'][key]['lock']
        clockIn['groupClock'][key]['exitPermission'] = False
        clockIn['groupClock'][key]['enterPermission'] = False
        clockIn['groupClock'][key]['administratorRemind'] = False
    for key, value in clockIn['dictClockPeople'].items():
        for key2, value2 in clockIn['dictClockPeople'][key].items():
            clockIn['dictClockPeople'][key][key2] = {
                'clockIn': value2,
                'consecutiveDays': 0
            }
    print(clockIn)
    save_obj(clockIn, 'clockIn')
    print('更新成功！')
