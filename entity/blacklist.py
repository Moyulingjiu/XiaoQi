"""黑名单实体"""
import time
from util import common_kit


class Blacklist:
    """黑名单抽象类"""
    def __init__(
            self,
            tag: int,
            reason: str,
            creation_time=time.time(),
            creation_id=0
    ):
        self.tag = tag
        """QQ号或者群号"""
        self.reason = reason
        self.creation_time = creation_time
        self.creation_id = creation_id

    def __str__(self):
        return "{0} | {1} | reason:{2} | tag:{3}".format(
            common_kit.output_time(self.creation_time),
            self.creation_id,
            self.reason,
            self.tag
        )

