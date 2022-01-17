"""事件分配器"""
from typing import List


class Register:
    """注册器，所有插件都会封装为注册器"""


class EventBus:
    """事件中心通过调度注册器来分发事件"""
    event: List[Register]

    def __init__(self):
        pass
