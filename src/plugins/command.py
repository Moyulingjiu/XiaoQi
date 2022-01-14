from plugins import tarot
from plugins import dataManage
from plugins import TRPG

# ==========================================================

key_allow = [
    '#', '*',
    ',', '，',
    '.', '。',
    '!', '！',
    '?', '？',
    ':', '：',
    ';', '；',
    '+',
    '-',
    '/'
]
table = TRPG.TableRolePlayGame()


# =========================================================
# 触发词
def add_key(key, mode, config, group_id, qq):
    if key in config['config']['key']:
        return '已启用该触发词'
    if key not in key_allow:
        reply = '该触发词不允许使用，仅允许使用以下符号作为触发词：'
        for i in key_allow:
            reply += i
        return reply

    config['config']['key'].append(key)
    if mode == 1:
        dataManage.save_group(group_id, config)
    else:
        dataManage.save_user(qq, config)
    return '操作成功！启用触发词“' + key + '”，使用“*key list”命令可以查看当前启用的触发词'


def remove_key(key, mode, config, group_id, qq):
    if key not in config['config']['key']:
        return '未启用该触发词'

    config['config']['key'].remove(key)
    if mode == 1:
        dataManage.save_group(group_id, config)
    else:
        dataManage.save_user(qq, config)
    return '操作成功！已删除触发词' + key


def clear_key(mode, config, group_id, qq):
    config['config']['key'] = []
    if mode == 1:
        dataManage.save_group(group_id, config)
    else:
        dataManage.save_user(qq, config)
    return '已清除所有触发词'


def show_key(config):
    if len(config['config']['key']) == 0:
        return '未启用任何触发词'
    reply = '启用触发词如下：'
    for i in config['config']['key']:
        reply += i
    return reply


# ==========================================================

def help_function():
    return 'data/Help/帮助.png'


def help_modular():
    return 'data/Help/模块管理帮助.png'


def help_clash():
    return 'data/Help/部落冲突查询帮助.png'


def help_thrower():
    return 'data/Help/骰娘帮助.png'


def help_clock():
    return 'data/Help/打卡帮助.png'


def help_activity():
    return 'data/Help/活动帮助.png'


def help_contributor():
    return 'data/Help/贡献者帮助.png'


def help_administrator():
    return 'data/Help/管理员帮助.png'


def help_master():
    return 'data/Help/主人帮助.png'


def help_tarot():
    return 'data/Help/塔罗牌帮助.png'


def help_game():
    return 'data/Help/游戏帮助.png'
    
def help_game2():
    return 'data/Help/游戏帮助2.png'
    
def enchanting():
    return 'data/Help/附魔查询.jpg'
    
def buff():
    return 'data/Help/buff查询.jpg'

def help_game_novice():
    return 'data/Help/游戏新手指南.png'

def function(code, qq, name, group_id, mode, bot_config, config, statistics):
    global key_allow

    trpg = True
    if mode == 1:
        trpg = config['config']['TRPG']

    need_at = False
    reply_text = ''
    reply_image = ''

    if code == 'help':
        reply_image = help_function()
    elif code == 'help tarot':
        reply_image = help_tarot()

    elif code[:7] == 'key add':
        tmp = code[7:].strip()
        if len(tmp) == 1:
            if tmp in key_allow:
                reply_text = add_key(tmp, mode, config, group_id, qq)
            else:
                reply_text = '不允许这个符号，仅允许扩展以下符号：'
                for i in key_allow:
                    reply_text += i
        else:
            reply_text = '格式错误！'
    elif code[:10] == 'key remove':
        tmp = code[10:].strip()
        if len(tmp) == 1:
            reply_text = remove_key(tmp, mode, config, group_id, qq)
        else:
            reply_text = '格式错误！'
    elif code == 'key list':
        reply_text = show_key(config)
    elif code == 'key clear':
        reply_text = clear_key(mode, config, group_id, qq)

    elif code == 'tarotb':
        reply_text = tarot.GetTarot()
    elif code == 'tarotl':
        reply_text = tarot.GetTarot2()
    elif code == 'tarot':
        reply_text = tarot.tarot()
    elif code == 'tarot 时间':
        reply_text = tarot.tarotTime()
    elif code == 'tarot 是非':
        reply_text = tarot.tarotIs()
    elif code == 'tarot 圣三角':
        reply_text = tarot.tarotIs()
    elif code == 'tarot 钻石展开法':
        reply_text = tarot.tarotBussiness()
    elif code == 'tarot 恋人金字塔':
        reply_text = tarot.tarotLove()
    elif code == 'tarot 自我探索':
        reply_text = tarot.tarotSelf()
    elif code == 'tarot 吉普赛十字':
        reply_text = tarot.tarotCross()
    elif code == 'tarot 二选一':
        reply_text = tarot.tarotChoose()
    elif code == 'tarot 关系发展':
        reply_text = tarot.tarotForward()
    elif code == 'tarot 六芒星':
        reply_text = tarot.tarotHexagram()
    elif code == 'tarot 凯尔特十字':
        reply_text = tarot.tarotCelticCross()

    elif code == 'jrrp' and trpg:
        reply_text = '*运势*'

    elif code[:8] == 'role add' and trpg:  # 添加人物
        tmp = code[8:].strip().split(' ')
        if len(tmp) == 2:
            reply_text = table.add_role(tmp[1], group_id, tmp[0])
    elif code[:11] == 'role remove' and trpg:  # 删除人物
        reply_text = table.remove_role(group_id, code[11:].strip())
    elif code == 'role list' and trpg:
        reply_text = table.show_role_list(group_id)
    elif code[:9] == 'role show' and trpg:
        reply_text = table.show_role(group_id, code[9:].strip())
    elif code[:9] == 'role copy' and trpg:
        if group_id != 0:
            reply_text = table.copy_role(code[9:].strip(), group_id, qq)
            need_at = True
        else:
            reply_text = '这是群聊命令，请在骰娘群里复制属性，因为每个群之间的属性是不共通的哦~'


    elif code[:7] == 'roleadd' and trpg:  # 添加人物
        tmp = code[7:].strip().split(' ')
        if len(tmp) == 2:
            reply_text = table.add_role(tmp[1], group_id, tmp[0])
    elif code[:10] == 'roleremove' and trpg:  # 删除人物
        reply_text = table.remove_role(group_id, code[10:].strip())
    elif code == 'rolelist' and trpg:
        reply_text = table.show_role_list(group_id)
    elif code[:8] == 'roleshow' and trpg:
        reply_text = table.show_role(group_id, code[8:].strip())
    elif code[:8] == 'rolecopy' and trpg:
        if group_id != 0:
            reply_text = table.copy_role(code[8:].strip(), group_id, qq)
            need_at = True
        else:
            reply_text = '这是群聊命令，请在骰娘群里复制属性，因为每个群之间的属性是不共通的哦~'


    elif code[:3] == 'coc' and trpg:
        tmp = code[3:].strip()
        if tmp.isdigit():
            reply_text = table.coc7(int(tmp))
            need_at = True
        elif len(tmp) == 0:
            reply_text = table.coc7(1)
            need_at = True
    elif code[:2] == 'sa' and trpg:
        if group_id != 0:
            tmp = code[2:].strip()
            if tmp.isdigit():
                reply_text = table.sa(int(tmp), group_id, qq)
                need_at = True
        else:
            reply_text = '这是群聊命令'
    elif code == 'sc' and trpg:
        if group_id != 0:
            reply_text = table.rasan(group_id, qq)
            need_at = True
        else:
            reply_text = '这是群聊命令'
    elif code[:2] == 'sc' and trpg:
        if group_id != 0:
            tmp1 = code[2:].strip()
            tmp2 = tmp1.split('/')

            success = 0
            fail_dick_number = 0
            fail_dick_size = 1
            fail_dick_base = 0
            if len(tmp2) == 2 and tmp2[0].isdigit():
                success = int(tmp2[0])
                if tmp2[1][0] == 'd':
                    fail_dick_number = 1  # 默认投一个骰子
                    tmp3 = tmp2[1][1:].split('+')
                    if len(tmp3) == 1 and tmp3[0].isdigit():
                        fail_dick_size = int(tmp3[0])
                    elif len(tmp3) == 2 and tmp3[0].isdigit() and tmp3[1].isdigit():
                        fail_dick_size = int(tmp3[0])
                        fail_dick_base = int(tmp3[1])
                else:
                    tmp3 = tmp2[1].split('d')
                    if len(tmp3) == 2 and tmp3[0].isdigit():
                        fail_dick_number = int(tmp3[0])
                        tmp4 = tmp3[1].split('+')
                        if len(tmp4) == 1 and tmp4[0].isdigit():
                            fail_dick_size = int(tmp4[0])
                        elif len(tmp4) == 2 and tmp4[0].isdigit() and tmp4[1].isdigit():
                            fail_dick_size = int(tmp4[0])
                            fail_dick_base = int(tmp4[1])
            if fail_dick_number > 0 and fail_dick_size > 0:
                reply_text = table.sc(success, fail_dick_number, fail_dick_size, fail_dick_base, group_id,
                                  qq)
                need_at = True
        else:
            reply_text = '这是群聊命令'


    elif (code == 'rd' or code == 'r') and trpg:
        reply_text = table.rd(1, 100, 1)
        need_at = True
    elif code == 'rp' and trpg:
        reply_text = table.rd(1, 20, 1)
        need_at = True
    elif code[:2] == 'rd' and trpg:
        size = 1
        times = 1
        if code[2:].isdigit():
            size = int(code[2:])
        else:
            tmp = code[2:].split('*')
            if len(tmp) == 2 and tmp[0].isdigit() and tmp[1].isdigit():
                size = int(tmp[0])
                times = int(tmp[1])
        if size > 0:
            reply_text = table.rd(1, size, times)
    elif code[0] == 'r' and 'd' in code and code[len(code) - 1] != 'd' and trpg:
        num = 0
        size = 0
        times = 1
        index = code.find('d')
        if code[1: index].isdigit():
            num = int(code[1: index])
        if code[code.find('d') + 1:].isdigit():
            size = int(code[code.find('d') + 1:])
        tmp = code[code.find('d') + 1:].split('*')
        if len(tmp) == 2 and tmp[0].isdigit() and tmp[1].isdigit():
            size = int(tmp[0])
            times = int(tmp[1])
        if size > 0 and num > 0:
            reply_text = table.rd(num, size, times)
            if reply_text != '啊嘞？':
                need_at = True

    elif code[:3] == 'sta' and trpg:  # 追加属性
        if group_id != 0:
            if len(code) > 4:
                attribute = code[3:].strip()
                reply_text = table.sta(attribute, group_id, qq)
                need_at = True
        else:
            reply_text = '这是群聊命令'
    elif code[:3] == 'stc' and trpg:  # 修改属性
        if group_id != 0:
            if len(code) > 4:
                attribute = code[3:].strip()
                reply_text = table.stc(attribute, group_id, qq)
                need_at = True
        else:
            reply_text = '这是群聊命令'
    elif code[:3] == 'std' and trpg:  # 删除属性
        if group_id != 0:
            if len(code) > 4:
                attribute = code[4:]
                reply_text = table.std(attribute, group_id, qq)
                need_at = True
        else:
            reply_text = '这是群聊命令'

    elif code[:7] == 'st from' and trpg:  # 设置属性
        if group_id != 0:
            role_name = code[7:].strip()
            reply_text = table.copy_role(role_name, group_id, qq)
            need_at = True
        else:
            reply_text = '这是群聊命令'
    elif code[:6] == 'stfrom' and trpg:  # 设置属性
        if group_id != 0:
            role_name = code[6:].strip()
            reply_text = table.copy_role(role_name, group_id, qq)
            need_at = True
        else:
            reply_text = '这是群聊命令'

    elif code[:5] == 'st to' and trpg:  # 把属性设置到人物卡
        if group_id != 0:
            role_name = code[5:].strip()
            reply_text = table.copy_to_role(role_name, group_id, qq)
            need_at = True
        else:
            reply_text = '这是群聊命令'
    elif code[:4] == 'stto' and trpg:  # 把属性设置到人物卡
        if group_id != 0:
            role_name = code[4:].strip()
            reply_text = table.copy_to_role(role_name, group_id, qq)
            need_at = True
        else:
            reply_text = '这是群聊命令'

    elif code[:2] == 'st' and trpg:  # 设置属性
        if group_id != 0:
            if len(code) > 3:
                attribute = code[2:].strip()
                reply_text = table.st(attribute, group_id, qq)
                need_at = True
        else:
            reply_text = '这是群聊命令'

    elif code == 'show' and trpg:  # 展示属性
        if group_id != 0:
            reply_text = table.show(group_id, qq)
            need_at = True
        else:
            reply_text = '这是群聊命令'
    elif (code == 'show all' or code == 'showall') and trpg:  # 展示属性
        if group_id != 0:
            reply_text = table.show_all(group_id, qq)
            need_at = True
        else:
            reply_text = '这是群聊命令'
    elif code[:4] == 'show' and trpg:
        if group_id != 0:
            reply_text = table.show_single(code[4:].strip(), group_id, qq)
        else:
            reply_text = '这是群聊命令'
    elif code[:2] == 'ra' and trpg:  # 鉴定属性
        if group_id != 0:
            if len(code) > 3:
                attribute = code[2:].strip()
                reply_text = table.ra(attribute, group_id, qq)
                need_at = True
        else:
            reply_text = '这是群聊命令'
    elif code == 'clear all' and trpg:  # 清空属性
        if group_id != 0:
            reply_text = table.clear_all(group_id)
            need_at = True
        else:
            reply_text = '这是群聊命令'
    elif code == 'clear' and trpg:  # 清空属性
        if group_id != 0:
            reply_text = table.clear_single(group_id, qq)
            need_at = True
        else:
            reply_text = '这是群聊命令'
    elif code == 'ex' and trpg:  # 清空属性
        if group_id != 0:
            reply_text = table.export(group_id, qq)
        else:
            reply_text = '这是群聊命令'
    elif code == 'name' and trpg:  # 随机名字
        reply_text = name + TRPG.random_name(1)
    elif code[:4] == 'name' and trpg:  # 随机名字
        tmp = code[4:].strip()
        if tmp.isdigit():
            reply_text = name + TRPG.random_name(int(tmp))

    if reply_text == '':
        express = TRPG.Expression(code.replace(' ', ''))
        try:
            reply_text = code + '=' + str(express.show())
        except OverflowError as e:
            reply_text = '运算超时'
        except ArithmeticError as e:
            err = str(e)
            if err == 'Non-expression':
                pass
            elif err == 'wrong format':
                reply_text = '表达式无法解析'

    if reply_text == '' and reply_image == '' and code.isalnum():
        reply_text = '未知指令：' + code + '\n请输入\"帮助\"查看帮助\n请输入\"骰娘\"查看骰娘帮助\n请输入\"游戏帮助\"查看游戏帮助'

    return reply_text, need_at, reply_image
