# 日志书写
filePath = 'data/__LOG__/bot.log'


def log(time, data):
    with open(filePath, 'a+', encoding='utf-8') as f:
        f.write('[' + time + '] 执行操作：' + data + '\n')


def member_log(time, qq, data):
    with open(filePath, 'a+', encoding='utf-8') as f:
        f.write('[' + time + '](' + str(qq) + ') 执行操作：' + data + '\n')


def group_log(time, qq, group_id, group_name, data):
    with open(filePath, 'a+', encoding='utf-8') as f:
        if qq != 0 and group_id != 0:
            f.write('[' + time + '](' + str(qq) + ')<' + group_name + '/' + str(group_id) + '> 执行操作：' + data + '\n')
        elif qq != 0 and group_id == 0:
            f.write('[' + time + '](' + str(qq) + ')<> 执行操作：' + data + '\n')
        elif qq == 0 and group_id != 0:
            f.write('[' + time + ']()<' + group_name + '/' + str(group_id) + '> 执行操作：' + data + '\n')
        else:
            f.write('[' + time + ']()<> 执行操作：' + data + '\n')
