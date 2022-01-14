# 这部分是独立成立的词集

from plugins import dataManage
import random


def reply(strMessage, group_id, config, statistics):
    questionReply = {}
    KeyReply = {}
    questionReplyAt = {}
    KeyReplyAt = {}
    complexReply = {}

    if config['key_reply'].__contains__('question'):
        questionReply = config['key_reply']['question']
    if config['key_reply'].__contains__('key'):
        KeyReply = config['key_reply']['key']
    if config['key_reply'].__contains__('question_at'):
        questionReplyAt = config['key_reply']['question_at']
    if config['key_reply'].__contains__('key_at'):
        KeyReplyAt = config['key_reply']['key_at']
    if config['key_reply'].__contains__('complex'):
        complexReply = config['key_reply']['complex']

    need_reply = False
    needAt = False
    reply = ''
    AtId = 0
    need_complex_reply = False  # 是否是复杂回复
    complex_at = {
        'at_type': -1,  # -1：不艾特；0：艾特；1：艾特分组
        'at': 0
    }  # 复杂艾特
    complex_reply = None  # 复杂回复

    p = random.randrange(0, 100)
    if not KeyReply.__contains__('RecoveryProbability'):
        KeyReply['RecoveryProbability'] = 100
        config['key_reply']['key'] = KeyReply
        dataManage.save_group(group_id, config)

    # if statistics['last_minute'] <= 10:
    # 全字段匹配
    if questionReply.__contains__(strMessage):
        replyList = questionReply[strMessage]
        if len(replyList) > 0:
            tmpNumber = random.randrange(0, len(replyList))
            reply = replyList[tmpNumber]
            need_reply = True
            statistics['last_minute'] += 1
            dataManage.save_statistics(statistics)

    # 全字段匹配带at
    if not need_reply:
        if questionReplyAt.__contains__(strMessage):
            replyList = questionReplyAt[strMessage]
            if len(replyList) > 0:
                tmpNumber = random.randrange(0, len(replyList))
                tmp = replyList[tmpNumber].split('~$~')
                reply = tmp[0]
                AtId = int(tmp[1])
                need_reply = True
                needAt = True
                statistics['last_minute'] += 1
                dataManage.save_statistics(statistics)

    # 关键词匹配
    if not need_reply:
        if p < KeyReply['RecoveryProbability']:
            for i in KeyReply:
                if i in strMessage:
                    replyList = KeyReply[i]
                    if len(replyList) > 0:
                        tmpNumber = random.randrange(0, len(replyList))
                        reply = replyList[tmpNumber]
                        need_reply = True
                        statistics['last_minute'] += 1
                        dataManage.save_statistics(statistics)
                        break

    # 关键词匹配（带艾特）
    if not need_reply:
        if p < KeyReply['RecoveryProbability']:
            for i in KeyReplyAt:
                if i in strMessage:
                    replyList = KeyReplyAt[i]
                    if len(replyList) > 0:
                        tmpNumber = random.randrange(0, len(replyList))
                        tmp = replyList[tmpNumber].split('~$~')
                        reply = tmp[0]
                        AtId = int(tmp[1])
                        need_reply = True
                        needAt = True
                        statistics['last_minute'] += 1
                        dataManage.save_statistics(statistics)
                        break

    if not need_reply:
        if complexReply.__contains__(strMessage):
            need_reply = True
            need_complex_reply = True
            complex_reply = complexReply[strMessage]['reply']
            complex_at = {
                'at_type': complexReply[strMessage]['at_type'],
                'at': complexReply[strMessage]['at']
            }
            statistics['last_minute'] += 1
            dataManage.save_statistics(statistics)


    # print('\tKeyReply AtId:', AtId)
    # print('\tKeyReply Reply:', reply)
    return need_reply, reply, '', AtId, needAt, need_complex_reply, complex_reply, complex_at
