# 使用说明

## 版权说明

可以免费使用或者捐赠后使用，但请注明出处。

请勿用于违法行为。

## mirai框架的部署



### 根据官方文档部署

参加文章：[mirai-console](https://github.com/mamoe/mirai-console)



### 自定义部署

1. 也可以直接下载：[mirai-console下载](https://github.com/iTXTech/mirai-console-loader/releases)，我是使用的MCL 1.0.5，你可以根据自己的需求下载
2. java的配置建议是[SE 16](https://www.oracle.com/java/technologies/javase-jdk16-downloads.html)。真的不建议使用官方推荐的JDK8会出现各种各样的报错。
3. python推荐使用[3.9](https://www.python.org/downloads/)。理论上python3应该都可。在安装完python之后win + r打开控制台（CMD或者powershell），输入以下命令

```dockerfile
python -m pip install graia-application-mirai
```

到此我们的前期准备工作也算是完成了。



接下来为了让程序跑起来我们需要运行，我们需要运行第一步下载的文件（解压后）”mcl.cmd“，等待提示控制台已经成功运行，然后关闭，此时就可以看见目录下已经多了许多的文件。

第四步，下载两个插件

```
链接：https://pan.baidu.com/s/1HaOI7YIAP_wGq936OxtWzA 
提取码：w8i9 
```

两个 jar包：http负责连接网络，solver-selenium负责QQ登陆的滑块验证。

将两个jar包放入plugins目录下，部署就完成了。

这时候再次启动mcl.cmd，输入以下命令：

```
help
```

根据命令提示登陆QQ。

然后运行本项目下的bot.py，恭喜你就成功地拥有了小柒。



## 二次开发

如果你并不想只是拥有现在的小柒，想要自己添加一些内容，不建议参照官方文档！

文档建议参照：[mirai+Graia编写聊天机器人](https://yooziki.github.io/2020/08/297095/)

源代码建议参照：[Graia](https://github.com/GraiaProject/Application)



为什么要参照源代码？因为很多类的定义和使用可以直接在源代码里面找到答案，而不用去看文档。因为文档具有时效性，因为时间的更新文档很可能会失效。并且mirai的官方文档很差，可以说几乎没有。所以此时参照源码就成了极佳的选择。



## 疑惑解答

如果参照这片文档仔细思考之后仍然不清楚如何部署小柒，或则被mirai的官方文档搞得晕头转向，请联系readme文档里的QQ群。



## 捐助小柒

非常感谢您的捐助，能够让小柒的服务器继续运行下去

捐助地址：[捐助小柒](https://github.com/Moyulingjiu/QQbot/blob/master/doc/donation.png)