import datetime


def toString():
    curr_time = datetime.datetime.now()
    time_str = datetime.datetime.strftime(curr_time, '%Y-%m-%d %H:%M:%S')
    return time_str


def getHour():
    curr_time = datetime.datetime.now()
    return curr_time.hour


def getMinute():
    curr_time = datetime.datetime.now()
    return curr_time.minute
