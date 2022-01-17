"""基础工具类"""
import time


def output_time(date) -> str:
    return time.strftime("%Y-%m-%d %H:%M:%S", date)
