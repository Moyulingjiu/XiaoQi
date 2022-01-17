"""此文件定义了所有的配置信息"""
from typing import List
from entity import blacklist


class Config:
    """配置类"""
    def __init__(
            self,
            master: int,
            authority_group: List[int],
            blacklist_member: List[blacklist.Blacklist],
            blacklist_group: List[blacklist.Blacklist]
    ):
        self.master = master
        """主人的QQ"""
        self.authority_group = authority_group
        """官方群群号"""
        self.blacklist_member = blacklist_member
        """黑名单人"""
        self.blacklist_group = blacklist_group
        """黑名单群"""


class GroupConfig:
    """群配置类"""
    def __init__(
            self
    ):
        pass


class UserConfig:
    """用户配置类"""
    def __init__(
            self
    ):
        pass
