# 查看所有数据
import pickle

# ==========================================================
# 新的存储方式
def save_obj(obj, name):
    with open(''+ name + '.pkl', 'wb') as f:
        pickle.dump(obj, f, pickle.HIGHEST_PROTOCOL)
 
def load_obj(name):
    with open('' + name + '.pkl', 'rb') as f:
        return pickle.load(f)

    
if __name__ == '__main__':
    keyReply = load_obj('keyReply')
    with open('KeyReply.txt', 'w', encoding='utf-8') as f:
        for key, value in keyReply.items():
            if len(value) == 0:
                continue
            f.write('问题：' + key + '\n')
            f.write('回答：\n')

            for i in value:
                f.write('\t' + i + '\n')
            f.write('--------------\n')
    print('完成！')