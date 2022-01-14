import datetime

from plugins import dataManage


def is_yesterday(date):
    today = str(datetime.date.today())
    list1 = today.split('-')
    for i in range(len(list1)):
        if list1[i].isdigit():
            list1[i] = int(list1[i])
        else:
            return False

    list2 = date.split('-')
    for i in range(len(list2)):
        if list2[i].isdigit():
            list2[i] = int(list2[i])
        else:
            return False
    if len(list2) != 3:
        return False

    leap = [31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31]
    no_leap = [31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31]

    is_leap = False
    if (list1[0] % 100 != 0 and list1[0] % 4 == 0) or list1[0] % 400 == 0:
        is_leap = True

    if list1[0] == list2[0]:
        if list1[1] == list2[1]:
            if list1[2] - list2[2] == 1:
                return True
        elif list1[1] - list2[1] == 1:
            if is_leap:
                if list2[2] == leap[list2[1] - 1] and list1[2] == 1:
                    return True
            else:
                if list2[2] == no_leap[list2[1] - 1] and list1[2] == 1:
                    return True
    elif list1[0] - list2[0] == 1:
        if list1[1] == 1 and list1[2] == 1 and list2[1] == 12 and list2[2] == 31:
            return True

    return False


class Clock:
    clock_data = dataManage.read_clock()

    def __init__(self):
        pass

    def save(self):
        dataManage.save_clock(self.clock_data)

    def existence(self, group_id, clock_name):
        if not self.clock_data.__contains__(group_id):
            return False
        elif not self.clock_data[group_id].__contains__(clock_name):
            return False
        return True

    # 获取所有打卡信息
    def get_clock(self, group_id):
        if not self.clock_data.__contains__(group_id):
            return None
        return self.clock_data[group_id]

    # 获取单个打卡信息
    def get_clock_single(self, group_id, clock_name):
        if not self.clock_data.__contains__(group_id):
            return None
        if not self.clock_data[group_id].__contains__(clock_name):
            return None
        return self.clock_data[group_id][clock_name]

    # 添加打卡计划
    def insert_clock(self, group_id, clock_name):
        if not self.clock_data.__contains__(group_id):
            self.clock_data[group_id] = {}
        elif self.clock_data[group_id].__contains__(clock_name):
            return False
        elif len(self.clock_data[group_id]) > 10:
            return False
        self.clock_data[group_id][clock_name] = {
            'member': [],
            'remind': {
                'switch': True,
                'hour': 22,
                'minute': 0
            },
            'summary': {
                'switch': True,
                'hour': 0,
                'minute': 0
            }
        }
        self.save()
        return True

    # 删除打卡计划
    def remove_clock(self, group_id, clock_name):
        if not self.clock_data.__contains__(group_id):
            return False
        elif not self.clock_data[group_id].__contains__(clock_name):
            return False
        del self.clock_data[group_id][clock_name]
        if len(self.clock_data[group_id]) == 0:
            del self.clock_data[group_id]
        self.save()
        return True

    def join_clock(self, group_id, qq, clock_name):
        if not self.existence(group_id, clock_name):
            return 1
        del_member = None
        for member in self.clock_data[group_id][clock_name]['member']:
            if member['qq'] == qq:
                del_member = member
                break
        if del_member is None:
            if len(self.clock_data[group_id][clock_name]['member']) > 30:
                return 3
            self.clock_data[group_id][clock_name]['member'].append({
                'qq': qq,
                'last': '',
                'continuity': 0
            })
            self.save()
            return 0
        return 2

    def quit_clock(self, group_id, qq, clock_name):
        if not self.existence(group_id, clock_name):
            return 1
        del_member = None
        for member in self.clock_data[group_id][clock_name]['member']:
            if member['qq'] == qq:
                del_member = member
                break
        if del_member is not None:
            self.clock_data[group_id][clock_name]['member'].remove(del_member)
            self.save()
            return 0
        return 2

    def sign(self, group_id, qq, clock_name):
        if not self.existence(group_id, clock_name):
            return -1
        today = str(datetime.date.today())
        for index in range(len(self.clock_data[group_id][clock_name]['member'])):
            if self.clock_data[group_id][clock_name]['member'][index]['qq'] == qq:
                if self.clock_data[group_id][clock_name]['member'][index]['last'] == today:
                    return -3
                if is_yesterday(self.clock_data[group_id][clock_name]['member'][index]['last']):  # 连续打卡判断
                    self.clock_data[group_id][clock_name]['member'][index]['continuity'] += 1
                else:
                    self.clock_data[group_id][clock_name]['member'][index]['continuity'] = 1
                self.clock_data[group_id][clock_name]['member'][index]['last'] = today
                self.save()
                return self.clock_data[group_id][clock_name]['member'][index]['continuity']
        return -2

    def edit_remind(self, group_id, clock_name, remind, hour, minute):
        if not self.existence(group_id, clock_name):
            return False
        if remind is not None:
            self.clock_data[group_id][clock_name]['remind']['switch'] = remind
        if hour is not None:
            self.clock_data[group_id][clock_name]['remind']['hour'] = hour
        if minute is not None:
            self.clock_data[group_id][clock_name]['remind']['minute'] = minute
        self.save()
        return True
