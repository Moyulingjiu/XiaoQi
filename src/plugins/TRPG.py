import random
import time
import re

from plugins import dataManage


def dick_sys(max_range):
    if max_range <= 1:
        max_range = 100
    return random.randint(1, max_range)


def attribute_dick1():
    return (dick_sys(6) + dick_sys(6) + dick_sys(6)) * 5


def attribute_dick2():
    return (dick_sys(6) + dick_sys(6) + 6) * 5


# 随机姓名
def random_name(number):
    if number < 1:
        return '数量都小于1啦！'
    elif number > 100:
        return '太多了吧！'

    result = '的随机名称：'
    first = False
    for i in range(number):
        if first:
            result += '、'
        else:
            first = True

        ran = random.randint(1, 3)
        if ran == 1:
            result += random_chinese_name()
        elif ran == 2:
            result += random_japanese_name()
        elif ran == 3:
            result += random_american_name()
    return result


def random_chinese_name():   
    # 删减部分，比较大众化姓氏
    firstName = "赵钱孙李周吴郑王冯陈褚卫蒋沈韩杨朱秦尤许何吕施张孔曹严华金魏陶姜戚谢邹喻水云苏潘葛奚范彭郎鲁韦昌马苗凤花方俞任袁柳鲍史唐费岑薛雷贺倪汤滕殷罗毕郝邬安常乐于时傅卞齐康伍余元卜顾孟平" \
                "黄和穆萧尹姚邵湛汪祁毛禹狄米贝明臧计成戴宋茅庞熊纪舒屈项祝董粱杜阮席季麻强贾路娄危江童颜郭梅盛林刁钟徐邱骆高夏蔡田胡凌霍万柯卢莫房缪干解应宗丁宣邓郁单杭洪包诸左石崔吉" \
                "龚程邢滑裴陆荣翁荀羊甄家封芮储靳邴松井富乌焦巴弓牧隗山谷车侯伊宁仇祖武符刘景詹束龙叶幸司韶黎乔苍双闻莘劳逄姬冉宰桂牛寿通边燕冀尚农温庄晏瞿茹习鱼容向古戈终居衡步都耿满弘国文东殴沃曾关红游盖益桓公晋楚闫"
    # 百家姓全部姓氏
    # firstName = "赵钱孙李周吴郑王冯陈褚卫蒋沈韩杨朱秦尤许何吕施张孔曹严华金魏陶姜戚谢邹喻柏水窦章云苏潘葛奚范彭郎鲁韦昌马苗凤花方俞任袁柳酆鲍史唐费廉岑薛雷贺倪汤滕殷罗毕郝邬安常乐于时傅皮卞齐康伍余元卜顾孟平" \
    #             "黄和穆萧尹姚邵湛汪祁毛禹狄米贝明臧计伏成戴谈宋茅庞熊纪舒屈项祝董粱杜阮蓝闵席季麻强贾路娄危江童颜郭梅盛林刁钟徐邱骆高夏蔡田樊胡凌霍虞万支柯昝管卢莫经房裘缪干解应宗丁宣贲邓郁单杭洪包诸左石崔吉钮" \
    #             "龚程嵇邢滑裴陆荣翁荀羊於惠甄麴家封芮羿储靳汲邴糜松井段富巫乌焦巴弓牧隗山谷车侯宓蓬全郗班仰秋仲伊宫宁仇栾暴甘钭厉戎祖武符刘景詹束龙叶幸司韶郜黎蓟薄印宿白怀蒲邰从鄂索咸籍赖卓蔺屠蒙池乔阴欎胥能苍" \
    #             "双闻莘党翟谭贡劳逄姬申扶堵冉宰郦雍舄璩桑桂濮牛寿通边扈燕冀郏浦尚农温别庄晏柴瞿阎充慕连茹习宦艾鱼容向古易慎戈廖庾终暨居衡步都耿满弘匡国文寇广禄阙东殴殳沃利蔚越夔隆师巩厍聂晁勾敖融冷訾辛阚那简饶空" \
    #             "曾毋沙乜养鞠须丰巢关蒯相查後荆红游竺权逯盖益桓公晋楚闫法汝鄢涂钦归海帅缑亢况后有琴梁丘左丘商牟佘佴伯赏南宫墨哈谯笪年爱阳佟言福百家姓终"
    # 百家姓中双姓氏
    firstName2="万俟司马上官欧阳夏侯诸葛闻人东方赫连皇甫尉迟公羊澹台公冶宗政濮阳淳于单于太叔申屠公孙仲孙轩辕令狐钟离宇文长孙慕容鲜于闾丘司徒司空亓官司寇仉督子颛孙端木巫马公西漆雕乐正壤驷公良拓跋夹谷宰父谷梁段干百里东郭南门呼延羊舌微生梁丘左丘东门西门南宫南宫"
    # 女孩名字
    girl = '秀娟英华慧巧美娜静淑惠珠翠雅芝玉萍红娥玲芬芳燕彩春菊兰凤洁梅琳素云莲真环雪荣爱妹霞香月莺媛艳瑞凡佳嘉琼勤珍贞莉桂娣叶璧璐娅琦晶妍茜秋珊莎锦黛青倩婷姣婉娴瑾颖露瑶怡婵雁蓓纨仪荷丹蓉眉君琴蕊薇菁梦岚苑婕馨瑗琰韵融园艺咏卿聪澜纯毓悦昭冰爽琬茗羽希宁欣飘育滢馥筠柔竹霭凝晓欢霄枫芸菲寒伊亚宜可姬舒影荔枝思丽'
    # 男孩名字
    boy = '伟刚勇毅俊峰强军平保东文辉力明永健世广志义兴良海山仁波宁贵福生龙元全国胜学祥才发武新利清飞彬富顺信子杰涛昌成康星光天达安岩中茂进林有坚和彪博诚先敬震振壮会思群豪心邦承乐绍功松善厚庆磊民友裕河哲江超浩亮政谦亨奇固之轮翰朗伯宏言若鸣朋斌梁栋维启克伦翔旭鹏泽晨辰士以建家致树炎德行时泰盛雄琛钧冠策腾楠榕风航弘'
    # 名
    name = '中笑贝凯歌易仁器义礼智信友上都卡被好无九加电金马钰玉忠孝爱'
    # 10%的机遇生成双数姓氏
    if random.choice(range(100))>10:
        firstName_name =firstName[random.choice(range(len(firstName)))]
    else:
        i = random.choice(range(len(firstName2)))
        firstName_name =firstName2[i:i+2]
 
    sex = random.choice(range(2))
    name_1 = ""
    # 生成并返回一个名字
    if sex > 0:
        girl_name = girl[random.choice(range(len(girl)))]
        if random.choice(range(2)) > 0:
            name_1 = name[random.choice(range(len(name)))]
        return firstName_name + name_1 + girl_name
    else:
        boy_name = boy[random.choice(range(len(boy)))]
        if random.choice(range(2)) > 0:
            name_1 = name[random.choice(range(len(name)))]
        return firstName_name + name_1 + boy_name


def random_japanese_name():
    # 姓
    # 常用姓氏
    last_name_1 = [
        "佐藤","铃木","高桥","田中","渡边","伊藤","山本","中村","小林","斋藤","加藤","吉田","山田","佐佐木","山口","松本","井上","木村","林","清水","山崎","中岛","池田","阿部","桥本","山下","森","石川","前田","小川","藤田","冈田","后藤","长谷川","石井","村上","近藤","坂本","远藤","青木","藤井","西村","福田","太田","三浦","藤原","冈本","松田","中川","中野","原田","小野","田村","竹内","金子","和田","中山","石田","上田","森田","小岛","柴田","原","宫崎","酒井","工藤","藤峰","横山","宫本","内田","高木","安藤","岛田","谷口","大野","高田","丸山","今井","河野","藤本","村田","武田","上野","杉山","增田","小山","大冢","平野","菅原","久保","松井","千叶","岩崎","樱井","木下","野口","松尾","菊地","野村","新井"
    ]
    # 非常用姓氏
    last_name_2 = [
        "渡部","佐野","杉本","大西","古川","滨田","市川","小松","高野","水野","吉川","山内","西田","西川","菊池","北村","五十岚","福岛","安田","中田","平田","川口","川崎","饭田","东","本田","泽田","久保田","吉村","中西","岩田","服部","辻","关","富田","川上","樋口","永井","松冈","山中","田口","森本","矢野","秋山","大岛","小泽","广濑","土屋","石原","松下","马场","大桥","松浦","吉冈","荒木","小池","久保","浅野","熊谷","野田","川村","田边","星野","大谷","黑田","尾崎","永田","松村","望月","内藤","菅野","西山","堀","岩本","平井","片山","川岛","本间","冈崎","横田","早川","荒井","镰田","小田","成田","宫田","大石","石桥","筱原","高山","须藤","萩原","大泽","小西","栗原","松原","伊东","三宅","大森","福井","南","奥村","松永","片冈","桑原","内山","关口","古贺","奥田","冈","北川","八木","上原","吉野","白石","今村","中泽","田岛","涩谷","小泉","上村","中尾","平山","青山","牧野","冈村","寺田","坂口","儿玉","大山","河合","多田","竹田","宫下","小仓","小野寺","小笠原","足立","村山","天野","坂井","杉浦","西","坂田","小原","丰田","角田","武藤","河村","根本","关根","水谷","中井","森下","神田","冢本","佐久间","植田","饭冢","安部","前川","山根","浅井","白井","宫川","冈部","大川","长田","堀内","松崎","饭岛","榎本","稻垣","若林","森山","金泽","江口","神谷","中谷","畠山","谷","细川","及川","安达","今野","三上","西尾","田代","石冢","岸本","津田","荒川","中原","长尾","户田","本多","高岛","森川","泷泽","土井","三好","金井","松山","米田","冈野","稻叶","村松","甲斐","西冈","佐伯","岩井","星","金田","黑木","野崎","藤泽","堤","落合","泉","堀田","广田","西野","町田","吉泽","古田","宫泽","德永","新田","长岛","山岸","富永","柳泽","黑川","山川","川田","松岛","杉田","奥山","土田","三木","村井","黑泽","笠原","须田","梅田","大竹","野中","堀江","川端","大村","日高","梶原","岸","西本","井口","大木","长泽","向井","大场","竹中","藤川","安井","榊原","川原","吉本","大内","深泽","竹下","西泽","吉原","藤冈","庄司","福本","冢田","宫内","小谷","绪方","谷川","下田","竹本","相泽","藤村","奥野","宇野","洼田","北野","栗田","石黑","野泽","龟井","平川","长野","宫原","山村","藤野","茂木","岛崎","川本","下村","丹羽","青柳","竹村","古谷","三轮","出口","高井","荻野","大城","田原","高濑","小森","稻田","宫城","筒井","福冈","矢岛","大原","福永","林田","横井",".大平","金城","筱崎","长冈","沟口","平松","山冈","浅田","越智","北原","永野","武井","鹤田","柳田","北岛","入江","大田","滨口","汤浅","相马","园田","高松","二宫","石山","堀川","手冢","川野","沼田","石崎","比嘉","臼井","宫岛","平冈","滨崎","池上","花田","谷本","濑户","西原","小出","筱田","杉原","志村","根岸","田畑","滨野","笠井","寺岛","松泽","三岛","大槻","岛村","仓田","福原","片桐","日野","小坂","菅","堀口","加纳","河原","新谷","千田","松野","德田","田上","吉井","森冈","柏木","村濑","内海","白川","畑中","秋元","大崎","中本","小柳","岩濑","原口","秋田","三谷","木原","岛","大井","川畑","福山","米山","米泽","植木","岩下","难波","古泽","今泉","三井","辻本","芳贺","坪井","井手","吉永","河内","畑","矶部","长井","荻原","大坪","盐田","真锅","岸田","皆川","细田","大友","细谷","植村","佐竹","河田","古屋","相原","若松","三村","远山","村冈","桥口","松川","平林","草野","川濑","栗山","橘","有马","井出","保坂","早坂","濑川","泷本","志贺","高冈","奈良","半田","龟山","高村","堀井","清野","玉井","神山","朝仓","富樫","水口","河本","进藤","富冈","中里","立石","岩渊","葛西","丰岛","水上","平泽","藤森","柳","川合","北泽","龟田","织田","武内","有田","三原","尾形","高见","狩野","宫地","大津","寺本","藤崎","植松","市村","坂下","平","大江","板垣","毛利","西岛","江藤","曾根","那须","北山","高柳","下山","高原","大沼","三桥","立花","浅见","久野","松木","木内","滨本","野上","田渊","门胁","手岛","泷口","坂上","土居","中根","平尾","三泽","牧","岩佐","田崎","深谷","石丸","阪本","市原","小宫","长谷","今田","守屋","前原","高尾","内野","柴崎","岩永","大仓","富泽","川井","高崎","关谷","金山","长濑","正木","饭野","矢部","外山","和泉","山野","金谷","秦","冢原","海野","鸟居","西谷","田川","成濑","木田","石垣","大泷","石本","小寺","坂元","门田","末永","长谷部","富山","井田","上山","金森","小畑","菅谷","永岛","奥","乾","宫胁","江崎","赤松","相川","梅泽","江川","板仓","野本","三田","永山","岛津","山上","藤木","杉村","小杉","小室","肉户","香川","泽井","水岛","黑岩","梅原","小关","松元","平冢","川边","喜多","诹访","今西","井川","生田","森谷","并木","宇佐美","秋叶","盐谷","水田","佐川","下川","细井","石渡","浦田","布施","金丸","真田","森胁","染谷","宫野","北田","风间","新垣","梅本","山城","中林","大矢","小椋","西口","玉城","土桥","高梨","细野","大庭","船桥","山元","滨","都筑","长崎","木本","仲田","阿久津","小幡","杉野","寺泽","麻生","山形","堀越","依田","小野田","梅津","羽田","木户","野原","赤木","浅川","椎名","岩间","日下","寺尾","仲村","宇都宫","矢田","矢口","大畑","大和田","波多野","大熊","前岛","儿岛","目黑","立川","菅沼","大贯","高仓","深田","泷川","池内","石泽","柳原","岩城","海老原","仓持","神崎","雨宫","影山","冈岛","神野","栗林","桥爪","石坂","上杉","结城","青野","川西","小沼","白鸟","濑尾","赤坂","板桥","小玉","首藤","清田","合田","玉置","丹野","尾上","角","泷","须贺","竹原","国分","横尾","井原","坂野","久米","森口","芦田","宫坂","泽","藤岛","柏原","三枝","楠本","妹尾","胁田","池本","明石","西胁","大下","北","大和","樱田","胜又","大林","川越","本村","小久保","小堀","本桥","野岛","有贺","田端","志田","横川","浦野","柳川","谷村","春日","今","坂东","飞田","江原","梅村","内村","会田","熊泽","冈山","桑田","楠","角谷","日比野","砂川","益田","泽村","安斋","花冈","笹川","中冢","胜田","吉崎","井泽","岛袋","森永","曾我","浅沼","白木","平良","小栗","石仓","田泽","前野","小菅","高畑","井本","岛本","玉木","高谷","冲田","仓本","长滨","冲","鹤冈","高泽","大浦","宇田川","柴山","本山","黑崎","城户","盐见","岩谷","北条","畠中","池谷","大高","广川","熊田","重松","阪口","野吕","八田","户冢","寺冈","菅井","末吉","井村","鹿岛","田尻","石野","川添","盐泽","田所","古山","伊泽","南部","岩泽","矢泽","神户","丸田","福地","笹原","恩田","平贺","米仓","森岛","增井","村木","福泽","中","五味","峰","日下部","菊田","森井","凑","秋本","矶野","寺西","若山","安永","重田","小峰","新村","上条","长友","小口","今川","泷田","猪股","深井","百濑","绀野","熊仓","鬼头","中冈","唐泽","玉田","氏家","小高","有村","岩村","小平","赤羽","境","寺井","河西","土谷","神原","西浦","照井","西井","牧田","宫里","相田","松谷","小田岛","藤山","福斯","品川","村中","对马","八乙女"
    ]
    # 名
    # 男
    first_name_boy = [
        '忠','孝','仁','义','礼','智','信','良','吉','喜','嘉','龟','鹤','松','千代','君','广','博','浩','洋','俊雄','高广','英松','秀赖','武夫','俊男','健人','翔太','涼介','将暉','凌','健太郎','勇輝','和也','孝之','大翔','剛','悠一','智也','翔','翼','慶太','健','廉','慎吾','航生','隆夫','雄大','優','涼平','拓哉','太一','亮','聪','翔平','昌平','昴','卓也','拓矢','一郎','次郎','三郎','四郎','五郎'
    ]
    # 女
    first_name_girl = [
        '小百合','赤子','花子','佐和子','和子','上乙子','纪子','洋子','行江','干代','由美','富枝','里奈', '翔子','真纪','千春','七恵','丽','工美', '琴美','贵子','裕子','峰子','真子','顺子','淳子','芽衣','麻衣','丽子','加奈子','亜美','恭子','晴美','有美子','麻矢','未来','久美子','桃子','菜穂子','美佳','朱美','绫乃','恭香'
    ]

    if random.randint(0, 100) > 30:
        last_name = random.choice(last_name_1)
    else:
        last_name = random.choice(last_name_2)


    sex = random.choice(range(2))
    if sex > 0:
        girl_name = random.choice(first_name_girl)
        return last_name + girl_name + '(女)'
    else:
        boy_name = random.choice(first_name_boy)
        return last_name + boy_name + '(男)'


def random_american_name():
    # 姓氏
    last_name = [
        '金','波普','斯图尔物','普尔','亨利','乔治', '法莫','卡特','史密斯','特纳','库克','贝克','泰勒','伦敦','华盛顿','约翰逊','汤姆森','鲁宾逊','詹姆林','斯特朗','利特尔','朗','肖特','杰克','弗兰克','马克','查尔斯','布莱克','怀特','布朗','格林','福克斯','科克','活尔夫','希尔','沃特斯','莱克','布什','伍德','弗劳尔'
    ]
    # 名
    first_name_girl = [
        '阿比盖尔','艾比','艾达','阿德莱德','艾德琳','亚历桑德拉','艾丽莎','艾米','亚历克西斯','爱丽丝','艾丽西娅','艾琳娜','艾莉森','爱丽丝娅','阿曼达','艾美','安伯','阿纳斯塔西娅','安德莉亚','安琪','安吉拉','安吉莉亚','安吉莉娜','安','安娜','安妮','安尼塔','艾莉尔','阿普里尔','艾希礼','欧蕊','阿维娃','笆笆拉','芭比','贝亚特','比阿特丽斯','贝基','贝拉','贝斯','贝蒂','布兰奇','邦妮','布莱安娜','布兰妮','布列塔尼','卡米尔','莰蒂丝','坎蒂','卡瑞娜','卡门','凯罗尔','卡罗琳','凯丽','卡桑德拉','凯西','凯瑟琳','凯茜','切尔西','沙琳','切莉','雪莉尔','克洛伊','克莉丝','克里斯蒂娜','克里斯汀','克里斯蒂','辛迪','克莱尔','克劳迪娅','克莱门特','克劳瑞丝','康妮','康斯坦斯','科拉','科瑞恩','科瑞斯特尔','戴茜','达芙妮','达茜','黛比','黛布拉','黛米','黛安娜','德洛丽丝','堂娜','多拉','桃瑞丝','伊迪丝','伊迪萨','伊莱恩','埃莉诺','伊丽莎白','埃拉','爱伦','艾莉','艾米瑞达','艾米丽','艾玛','伊妮德','埃尔莎','埃莉卡','爱斯特尔','爱丝特','尤杜拉','伊娃','伊芙','伊芙琳','芬妮','费怡','菲奥纳','福罗拉','弗罗伦丝','弗郎西丝','弗雷德里卡','弗里达','吉莉安','格拉蒂丝','格罗瑞娅','格瑞丝','格温多琳','格温','汉娜','海莉','赫柏','海伦娜','海伦','汉纳','海蒂','希拉里','英格丽德','伊莎贝拉','爱沙拉','艾琳','艾丽丝','艾维','杰奎琳','小玉','詹米','简','珍妮特','贾斯敏','姬恩','珍娜','詹妮弗','詹妮','杰西卡','杰西','琼','乔安娜','乔斯林','乔莉埃特','约瑟芬','乔茜','乔伊','乔伊斯','朱迪丝','朱蒂','朱莉娅','朱莉安娜','朱莉','朱恩','凯琳','卡瑞达','凯瑟琳','凯特','凯西','卡蒂','卡特里娜','凯','凯拉','凯莉','凯尔西','特里娜','基蒂','莱瑞拉','蕾西','劳拉','劳伦','莉娜','莉迪娅','莉莲','莉莉','琳达','琳赛','丽莎','洛拉','罗琳','路易莎','路易丝','露西娅','露茜','露西妮','露露','莉迪娅','马德琳','曼达','曼迪','玛格丽特','玛丽亚','玛丽莲','玛莎','梅维丝','玛丽','玛蒂尔达','莫琳','梅','梅琳达','梅利莎','美洛蒂','梅瑞狄斯','米娅','米歇尔','米莉','米兰达','米里亚姆','米娅','茉莉','莫尼卡','南茜','娜塔莉','妮可','尼基塔','尼娜','诺玛','尼迪亚','奥克塔维亚','奥琳娜','奥利维亚','奥菲莉娅','帕梅拉','帕特丽夏','芭迪','保拉','波琳','帕姬','菲洛米娜','菲比','菲丽丝','波莉','普里西拉','昆蒂娜','雷切尔','丽贝卡','瑞加娜','丽塔','罗丝','洛克萨妮','露丝','萨布丽娜','萨莉','桑德拉','萨曼莎','萨米','桑德拉','桑迪','莎拉','萨瓦娜','斯嘉丽','塞尔玛','塞琳娜','莎伦','希拉','雪莉','斯莱瑞','西尔维亚','索尼亚','索菲娅','苏菲亚','丝塔茜','丝特拉','斯蒂芬妮','苏','萨妮','苏珊','塔玛拉','苔米','塔莎','特莉萨','蒂凡妮','蒂娜','东妮亚','特蕾西','厄休拉','温妮莎','维纳斯','维拉','维多利亚','维尔莉特','维吉妮亚','维达','薇薇安','旺达','温蒂','惠特尼','韦恩','温妮','尤兰达','伊薇特','伊温妮','莎拉','塞尔达','佐伊','卓拉'
    ]
    first_name_boy = [
        '亚伦','亚伯','亚伯拉罕','亚当','艾德里安','艾登','阿尔瓦','亚历克斯','亚历山大','艾伦','艾伯特','阿尔弗雷德','安德鲁','安迪','安格斯','安东尼','阿波罗','阿诺德','亚瑟','奥古斯特','奥斯汀','本','本杰明','伯特','本森','比尔','比利','鲍伯','布拉德','布兰登','布鲁斯','迦勒','卡梅伦','卡尔','卡洛斯','凯里','克里斯','柯利弗','科迪','科尔','科林','丹尼','大卫','唐纳德','道格拉斯','杜克','迪伦','埃迪','埃德加','爱迪生','艾德蒙','爱德华','艾德文','埃尔维斯','伊桑','柳真','埃文','福特','弗兰克思','富兰克林','弗瑞德','加百利','加比','加菲尔德','加文','格林顿','汉克','哈利','海顿','希尔顿','雨果','伊格纳缇伍兹','伊凡','艾塞亚','贾斯汀','凯斯','肯','凯尔','兰斯','劳伦特','劳伦斯','利安德尔','李','雷欧','劳伦','路易斯','卢克','马库斯','马西','马修','米奇','麦克','尼尔','尼尔森','尼古拉斯','尼克','诺亚','诺曼','奥利弗','奥斯卡','欧文','彼得','菲利普','菲比','昆廷','雷','列得','理查德','里奇','罗伯特','罗宾','洛克','罗杰','罗纳德','赖安','山姆','萨米','斯考特','肖恩','西蒙','所罗门','斯帕克','斯宾塞','斯派克','斯坦利','史蒂夫','史蒂文','斯图尔特','斯图亚特','特伦斯','特里','泰德','托马斯','提姆','蒂莫西','托德','汤米','汤姆','托马斯','托尼','尤利塞斯','弗恩','弗农','维克多','文森特','华纳','沃伦','韦恩','卫斯理','威廉','维利','扎克','圣扎迦利'
    ]

    sex = random.choice(range(2))
    if sex > 0:
        girl_name = random.choice(first_name_girl)
        return girl_name + '·' + random.choice(last_name) + '(女)'
    else:
        boy_name = random.choice(first_name_boy)
        return boy_name + '·' + random.choice(last_name) + '(男)'



# 将字符串转变为属性字典
# 0:追加属性
# 1:修改属性
def str_to_attribute(attribute, string, mode):
    print('attribute:' + str(attribute))
    print('string:' + string)
    print('mode:' + str(mode))
    name = ''
    number = 0
    length = len(string)
    i = 0
    edit_number = 0
    while i < length:
        if string[i].isdigit():
            while string[i].isdigit():
                number = number * 10 + int(string[i])
                i += 1
                if i >= length:
                    break
            if number > 100:
                number = 100
            name = name.strip()
            if name[-1:] == '：' or name[-1:] == ':':
                name = name[:-1]
                
            if len(name) > 0:
                if mode == 0 or (mode == 1 and attribute.__contains__(name)):
                    attribute[name] = number
                    edit_number += 1
            name = ''
            number = 0

        if i >= length:
            break
        name += string[i]
        i += 1

    return attribute, edit_number


# 栈
class Stack:
    def __init__(self):
        self.items = []
        self.length = 0

    # 判断栈是否为空，返回布尔值
    def is_empty(self):
        return self.length == 0

    # 返回栈顶元素
    def top(self):
        return self.items[self.length - 1]

    # 返回栈的大小
    def size(self):
        return self.length

    # 入栈
    def push(self, item):
        self.items.append(item)
        self.length += 1

    # 出栈
    def pop(self):
        if self.length == 0:
            return None
        self.length -= 1
        return self.items.pop()


# 骰子
class Dick:
    def __int__(self):
        self.number = 0
        self.size = 1
        self.sum = 0
        self.dick = []

    def __init__(self, number, size):
        self.number = number
        self.size = size
        if self.number < 0:
            self.number = 0
        if self.size <= 0:
            self.size = 6
        self.sum = 0
        self.dick = []
        self.calculate()

    def calculate(self):
        self.sum = 0
        self.dick = []
        for i in range(self.number):
            tmp = dick_sys(self.size)
            self.dick.append(tmp)
            self.sum += tmp

    def show(self):
        if self.number <= 0:
            return '0'
        if self.number == 1:
            return str(self.dick[0])

        result = '('
        plus_flag = False
        for i in self.dick:
            if plus_flag:
                result += '+'
            else:
                plus_flag = True

            result += str(i)

        result += ')'
        return result

    def show_without_brackets(self):
        if self.number <= 0:
            return '0'
        result = ''
        plus_flag = False
        for i in self.dick:
            if plus_flag:
                result += '+'
            else:
                plus_flag = True

            result += str(i)
        return result


# =====================================
# 计算表达式分析
def filters(string):
    reg = r"[0-9\+\-\*\/\^rd()]+"
    temp = re.fullmatch(reg, string)
    return temp


def power(base, exponent):
    res = 1
    while exponent:
        if exponent & 1:  # 判断当前的最后一位是否为1，如果为1的话，就需要把之前的幂乘到结果中。
            res *= base
        base *= base  # 一直累乘，如果最后一位不是1的话，就不用了把这个值乘到结果中，但是还是要乘。
        exponent = exponent >> 1
    return res


def get_prior(ch):
    if ch == '(':
        return 1
    elif ch == '+' or ch == '-':
        return 2
    elif ch == '*' or ch == '/':
        return 3
    elif ch == '^':
        return 4


# -----------------------
# @author Troiy
# @Date 2021/6/29
# 实现表达式计算
class Expression:
    expression = ''

    def __init__(self, expression):
        self.expression = expression
        self.number = Stack()
        self.operator = Stack()

    def show(self):
        return int(self.handle())

    def calculate(self, operation):
        num3 = 0
        num2 = self.number.pop()
        num1 = self.number.pop()
        if operation == '+':
            num3 = num1 + num2
        elif operation == '-':
            num3 = num1 - num2
        elif operation == '*':
            num3 = num1 * num2
        elif operation == '/':
            num3 = num1 / num2
        elif operation == '^':
            if num2 > 1000:
                raise OverflowError('too large')
            num3 = power(num1, num2)

        self.number.push(num3)

    def handle(self):
        i = 0
        operator_flag = 0
        negate = 1
        bracket_flag = 0
        if filters(self.expression) is None:
            raise ArithmeticError('Non-expression')
        if not self.expression[len(self.expression) - 1] == ')' and not '0' <= self.expression[
            len(self.expression) - 1] <= '9':
            raise ArithmeticError('wrong format')
        while i < len(self.expression):
            current = time.time()
            if '0' <= self.expression[i] <= '9':
                j = i + 1
                while j < len(self.expression) and '0' <= self.expression[j] <= '9':
                    j = j + 1
                tmp = int(self.expression[i:j])
                self.number.push(tmp * negate)
                negate = 1
                operator_flag = 0
                i = j
            elif self.expression[i] == '+' or self.expression[i] == '-' or self.expression[i] == '*' or \
                    self.expression[i] == '/' or self.expression[i] == '^':
                if operator_flag < 1 or operator_flag == 1 and self.expression[i] == '-':
                    operator_flag = operator_flag + 1
                    if operator_flag == 1:
                        if self.operator.is_empty():
                            self.operator.push(self.expression[i])
                        else:
                            while not self.operator.is_empty():
                                tmp = self.operator.top()
                                if get_prior(tmp) >= get_prior(self.expression[i]):
                                    self.calculate(tmp)
                                    try:
                                        end = time.time()
                                        if end - current > 1:
                                            raise ArithmeticError("time out")
                                    except ArithmeticError:
                                        print("time out")
                                        raise ArithmeticError("time out")
                                    self.operator.pop()
                                else:
                                    break
                            self.operator.push(self.expression[i])
                    else:
                        if operator_flag == 2:
                            negate = -1
                else:
                    raise ArithmeticError('wrong format')
                i = i + 1
            elif self.expression[i] == 'r':
                try:
                    j = self.expression.index('d', i, len(self.expression))
                    if j != -1:
                        r_str = self.expression[i + 1:j]
                    else:
                        raise ArithmeticError('wrong format')
                except ArithmeticError:
                    print("error")
                    raise ArithmeticError('wrong format')

                d_str = ''
                i = j
                if '0' <= self.expression[i + 1] <= '9':
                    j = i + 1
                    d_str = self.expression[j]
                elif self.expression[i + 1] == '(':
                    bracket_flag = 1
                    j = j + 1
                    while bracket_flag > 0:
                        if self.expression[j + 1] == '(':
                            bracket_flag = bracket_flag + 1
                        elif self.expression[j + 1] == ')':
                            bracket_flag = bracket_flag - 1
                        j = j + 1

                    d_str = self.expression[i + 1:j + 1]
                dick_expression = Expression(r_str)
                dick_number = dick_expression.handle()
                dick_expression = Expression(d_str)
                dick_size = dick_expression.handle()
                dice = Dick(dick_number, dick_size)
                self.number.push(dice.sum)
                i = j + 1
                operator_flag = 0
            elif self.expression[i] == '(':
                self.operator.push(self.expression[i])
                i = i + 1
            elif self.expression[i] == ')':
                while self.operator.top() != '(':
                    tmp = self.operator.top()
                    self.calculate(tmp)
                    try:
                        end = time.time()
                        if end - current > 1:
                            raise ArithmeticError("time out")
                    except ArithmeticError:
                        print("time out")
                        raise ArithmeticError("time out")
                    self.operator.pop()
                self.operator.pop()
                i = i + 1
            else:
                raise ArithmeticError('wrong format')

        while not self.operator.is_empty():
            tmp = self.operator.top()
            self.calculate(tmp)
            try:
                end = time.time()
                if end - current > 1:
                    raise ArithmeticError("time out")
            except ArithmeticError:
                print("time out")
                raise ArithmeticError("time out")
            self.operator.pop()
        return self.number.top()


# 跑团
class TableRolePlayGame:
    attribute = {}
    role = {}

    def __init__(self):
        self.attribute = dataManage.load_obj('data/TRPG/coc')
        self.role = dataManage.load_obj('data/TRPG/cocRole')

    # ==========================================================
    # 丢色子

    def rasan(self, group_id, qq):
        if not self.attribute.__contains__(group_id):
            return '意志属性，请使用rd命令手动检验'
        if not self.attribute[group_id].__contains__(qq):
            return '意志属性，请使用rd命令手动检验'

        powe = -1
        if self.attribute[group_id][qq].__contains__('意志'):
            powe = self.attribute[group_id][qq]['意志']
        elif self.attribute[group_id][qq].__contains__('pow'):
            powe = self.attribute[group_id][qq]['pow']

        tmp = dick_sys(100)
        if tmp <= powe:
            return '你扔出来的点数为：' + str(tmp) + '（意志：' + str(powe) + '） 鉴定成功！小柒也在为你祈祷哦~'
        else:
            return '你扔出来的点数为：' + str(tmp) + '（意志：' + str(powe) + '） 鉴定失败！摸摸头，不要哭'

    def sc(self, success, fail_dick_number, fail_dick_size, fail_dick_base, group_id, qq):
        if not self.attribute.__contains__(group_id):
            return '未能找到san值、意志两个属性，请使用rd命令手动检验'
        if not self.attribute[group_id].__contains__(qq):
            return '未能找到san值、意志两个属性，请使用rd命令手动检验'

        san = -1
        powe = -1
        result = '鉴定结果如下：'

        if self.attribute[group_id][qq].__contains__('san'):
            san = self.attribute[group_id][qq]['san']
        elif self.attribute[group_id][qq].__contains__('san值'):
            san = self.attribute[group_id][qq]['san值']
        elif self.attribute[group_id][qq].__contains__('理智'):
            san = self.attribute[group_id][qq]['理智']
        elif self.attribute[group_id][qq].__contains__('理智值'):
            san = self.attribute[group_id][qq]['理智值']

        if self.attribute[group_id][qq].__contains__('意志'):
            powe = self.attribute[group_id][qq]['意志']
        elif self.attribute[group_id][qq].__contains__('pow'):
            powe = self.attribute[group_id][qq]['pow']

        if san == -1 or powe == -1:
            return '未能找到san值、意志两个属性，请使用rd命令手动检验'

        tmp = dick_sys(100)
        if tmp <= powe:
            result = '鉴定成功！'
            san -= success
            if san <= 0:
                san = 0
            result += '\nsan值减少：' + str(success)
            result += '\n目前san值：' + str(san) + '/' + str(powe)
            self.attribute[group_id][qq]['san'] = san
            self.attribute[group_id][qq]['san值'] = san
            self.attribute[group_id][qq]['理智'] = san
            self.attribute[group_id][qq]['理智值'] = san
        else:
            sum = fail_dick_base
            while fail_dick_number > 0:
                fail_dick_number -= 1
                sum += dick_sys(fail_dick_size)
            result = '鉴定失败！'
            san -= sum
            if san <= 0:
                san = 0
            result += '\nsan值减少：' + str(sum)
            result += '\n目前san值：' + str(san) + '/' + str(powe)
            self.attribute[group_id][qq]['san'] = san
            self.attribute[group_id][qq]['san值'] = san
            self.attribute[group_id][qq]['理智'] = san
            self.attribute[group_id][qq]['理智值'] = san
        dataManage.save_obj(self.attribute, 'data/TRPG/coc')
        return result

    def sa(self, num, group_id, qq):
        if not self.attribute.__contains__(group_id):
            return '未能找到san值、意志两个属性，请先使用sta指令为你自己添加这个两个属性'
        if not self.attribute[group_id].__contains__(qq):
            return '未能找到san值、意志两个属性，请先使用sta指令为你自己添加这个两个属性'

        san = -1
        powe = -1

        if self.attribute[group_id][qq].__contains__('san'):
            san = self.attribute[group_id][qq]['san']
        elif self.attribute[group_id][qq].__contains__('san值'):
            san = self.attribute[group_id][qq]['san值']
        elif self.attribute[group_id][qq].__contains__('理智'):
            san = self.attribute[group_id][qq]['理智']
        elif self.attribute[group_id][qq].__contains__('理智值'):
            san = self.attribute[group_id][qq]['理智值']

        if self.attribute[group_id][qq].__contains__('意志'):
            powe = self.attribute[group_id][qq]['意志']
        elif self.attribute[group_id][qq].__contains__('pow'):
            powe = self.attribute[group_id][qq]['pow']

        if san == -1 or powe == -1:
            return '未能找到san值、意志两个属性，请先使用sta指令为你自己添加这个两个属性'

        san += num
        if san > powe:
            san = powe
        self.attribute[group_id][qq]['san'] = san
        self.attribute[group_id][qq]['san值'] = san
        self.attribute[group_id][qq]['理智'] = san
        self.attribute[group_id][qq]['理智值'] = san
        return '恢复' + str(num) + 'san值，当前san值：' + str(san) + '/' + str(powe)

    def coc7(self, num):
        result = '你的人物制作：'

        if num <= 0:
            return result
        elif num > 20:
            return '咦？你确定要那么多板子吗？输入一个小于20的数字试试吧~'

        for i in range(num):
            strength = attribute_dick1()
            con = attribute_dick1()
            size = attribute_dick2()
            dex = attribute_dick1()
            appe = attribute_dick1()
            intt = attribute_dick2()
            powe = attribute_dick1()
            edu = attribute_dick2()
            lucky = attribute_dick1()

            result += '\n力量：' + str(strength)
            result += ' 体质：' + str(con)
            result += ' 体型：' + str(size)
            result += ' 敏捷：' + str(dex)
            result += ' 外貌：' + str(appe)
            result += ' 智力：' + str(intt)
            result += ' 意志：' + str(powe)
            result += ' 教育：' + str(edu)
            result += ' 幸运：' + str(lucky)

            result += ' 共计：'
            total = strength + con + size + dex + appe + intt + powe + edu + lucky
            result += str(total) + '/' + str(total + lucky)

        return result

    def rd(self, number, size, times):
        if size > 1000000:
            return '这么多面吗？输一个小点的数字试试吧~'
        if number > 200:
            return '这么多骰子吗？输一个小点的数字试试吧~'

        dick = Dick(number, size)
        if number > 1:
            if times != 1:
                return '你投出的点数为：' + dick.show() + '*' + str(times) + '=' + str(dick.sum * times)
            else:
                return '你投出的点数为：' + dick.show_without_brackets() + '=' + str(dick.sum)
        else:
            if times != 1:
                return '你投出的点数为：' + str(dick.sum * times)
            else:
                return '你投出的点数为：' + str(dick.sum)

    def st(self, attribute, group_id, qq):
        if not self.attribute.__contains__(group_id):
            self.attribute[group_id] = {}
        if self.attribute[group_id].__contains__(qq):
            del self.attribute[group_id][qq]

        self.sta(attribute, group_id, qq)
        dataManage.save_obj(self.attribute, 'data/TRPG/coc')
        return '覆盖成功！目前有属性个数：' + str(len(self.attribute[group_id][qq]))

    def sta(self, attribute, group_id, qq):
        if not self.attribute.__contains__(group_id):
            self.attribute[group_id] = {}
        if not self.attribute[group_id].__contains__(qq):
            self.attribute[group_id][qq] = {}

        self.attribute[group_id][qq], edit_number = str_to_attribute(self.attribute[group_id][qq], attribute, 0)

        dataManage.save_obj(self.attribute, 'data/TRPG/coc')
        return '追加成功！' + '\n追加属性个数：' + str(edit_number) + '\n目前有属性个数：' + str(len(self.attribute[group_id][qq]))

    def stc(self, attribute, group_id, qq):
        if not self.attribute.__contains__(group_id):
            return '不存在该属性'
        if not self.attribute[group_id].__contains__(qq):
            return '不存在该属性'

        self.attribute[group_id][qq], edit_number = str_to_attribute(self.attribute[group_id][qq], attribute, 1)

        dataManage.save_obj(self.attribute, 'data/TRPG/coc')
        return '成功修改' + str(edit_number) + '个属性'

    def std(self, attribute, group_id, qq):
        if not self.attribute.__contains__(group_id):
            return '不存在该属性'
        if not self.attribute[group_id].__contains__(qq):
            return '不存在该属性'

        attributeList = attribute.split(' ')
        edit_number = 0
        for i in attributeList:
            i = i.strip()
            if not self.attribute[group_id][qq].__contains__(i):
                continue
            edit_number += 1
            del self.attribute[group_id][qq][i]
        dataManage.save_obj(self.attribute, 'data/TRPG/coc')
        return '成功删除' + str(edit_number) + '个属性'

    def clear_all(self, group_id):
        if self.attribute.__contains__(group_id):
            del self.attribute[group_id]
        dataManage.save_obj(self.attribute, 'data/TRPG/coc')
        return '清空成功！'

    def clear_single(self, group_id, qq):
        if self.attribute.__contains__(group_id):
            if self.attribute[group_id].__contains__(qq):
                del self.attribute[group_id][qq]
        dataManage.save_obj(self.attribute, 'data/TRPG/coc')
        return '清空成功！'

    def show(self, group_id, qq):
        if not self.attribute.__contains__(group_id):
            return '暂无属性'
        if not self.attribute[group_id].__contains__(qq):
            return '暂无属性'
        if len(self.attribute[group_id][qq]) == 0:
            return '暂无属性'

        result = '你的20及以上的属性如下：'
        for key, value in self.attribute[group_id][qq].items():
            if value >= 20:
                result += '\n' + key + '：' + str(value)
        return result

    def show_single(self, name, group_id, qq):
        if not self.attribute.__contains__(group_id):
            return '你没有属性' + name
        if not self.attribute[group_id].__contains__(qq):
            return '你没有属性' + name
        if not self.attribute[group_id][qq].__contains__(name):
            return '你没有属性' + name
        return '属性：' + name + '值：' + str(self.attribute[group_id][qq][name])

    def show_all(self, group_id, qq):
        if not self.attribute.__contains__(group_id):
            return '暂无属性'
        if not self.attribute[group_id].__contains__(qq):
            return '暂无属性'
        if len(self.attribute[group_id][qq]) == 0:
            return '暂无属性'

        result = '你的属性如下：'
        for key, value in self.attribute[group_id][qq].items():
            result += '\n' + key + '：' + str(value)
        return result

    # 鉴定属性
    def ra(self, attribute, group_id, qq):
        if not self.attribute.__contains__(group_id):
            return '不存在该属性'
        if not self.attribute[group_id].__contains__(qq):
            return '不存在该属性'
        if attribute[len(attribute) - 1].isdigit():
            index = len(attribute) - 2
            while index >= 0:
                if not attribute[index].isdigit():
                    break
                index -= 1
            name = ''
            if index == -1:
                name = '[未知属性]'
            else:
                name = attribute[:index + 1]
            number = int(attribute[index + 1:])
            if number > 100:
                number = 100
            elif number < 0:
                number = 0
            dicks = dick_sys(100)
            if dicks < number:
                return '点数：' + str(dicks) + '\n' + name + ':' + str(number) + '\n鉴定成功！'
            else:
                return '点数：' + str(dicks) + '\n' + name + ':' + str(number) + '\n鉴定失败！'

        if not self.attribute[group_id][qq].__contains__(attribute):
            return '不存在该属性'
        dicks = dick_sys(100)
        if dicks <= self.attribute[group_id][qq][attribute]:
            return '点数：' + str(dicks) + '\n' + attribute + ':' + str(
                self.attribute[group_id][qq][attribute]) + '\n鉴定成功！'
        else:
            return '点数：' + str(dicks) + '\n' + attribute + ':' + str(
                self.attribute[group_id][qq][attribute]) + '\n鉴定失败！'

    def export(self, group_id, qq):
        if not self.attribute.__contains__(group_id):
            return '*st '
        if not self.attribute[group_id].__contains__(qq):
            return '*st '
        if len(self.attribute[group_id][qq]) == 0:
            return '*st '

        result = '*st '
        for key, value in self.attribute[group_id][qq].items():
            result += key + str(value)
        return result

    # ====================================
    # 人物模板
    def add_role(self, attribute, group_id, role_name):
        if role_name.isdigit():
            return '人物模板名不能全为数字！'
        if self.role.__contains__(role_name):
            del self.role[role_name]
        self.role[role_name], edit_number = str_to_attribute(self.role[role_name], attribute, 0)

        dataManage.save_obj(self.role, 'data/TRPG/cocRole')
        return '人物' + role_name + '已修改'

    def remove_role(self, group_id, role_name):
        if self.role.__contains__(role_name):
            del self.role[role_name]
            dataManage.save_obj(self.role, 'data/TRPG/cocRole')
            return '人物' + role_name + '已删除'
        else:
            return '人物' + role_name + '不存在'

    def show_role(self, group_id, role_name):
        if not self.role.__contains__(role_name):
            return '人物' + role_name + '不存在'
        result = '人物' + role_name + '的属性如下：'
        for key, value in self.role[role_name].items():
            result += '\n' + key + '：' + str(value)
        return result

    def show_role_list(self, group_id):
        if len(self.role) == 0:
            return '暂无人物'

        result = '人物列表如下：'
        for key, value in self.role.items():
            result += '\n' + key
        return result

    def copy_role(self, role_name, group_id, qq):
        if not self.attribute.__contains__(group_id):
            self.attribute[group_id] = {}
        if not self.attribute[group_id].__contains__(qq):
            self.attribute[group_id][qq] = {}
        if not self.role.__contains__(role_name):
            return '不存在该人物'

        self.attribute[group_id][qq] = self.role[role_name]
        dataManage.save_obj(self.attribute, 'data/TRPG/coc')
        return '已将人物模板' + role_name + '的属性复制给你~'

    def copy_to_role(self, role_name, group_id, qq):
        if not self.attribute.__contains__(group_id):
            return '你目前没有属性，不能复制到人物卡上哦~'
        if not self.attribute[group_id].__contains__(qq):
            return '你目前没有属性，不能复制到人物卡上哦~'

        self.role[role_name] = self.attribute[group_id][qq]
        dataManage.save_obj(self.role, 'data/TRPG/cocRole')
        return '已将你的属性复制到人物卡：' + role_name
