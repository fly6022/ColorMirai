# ColorMirai
一个基于[Mirai](https://github.com/mamoe/mirai) 的机器人框架


**官方QQ群**：[571239090](https://qm.qq.com/cgi-bin/qm/qr?k=85m_MZMJ7BbyZ2vZW4wHVZGGvGnIL2As&jump_from=webapi)

**索引列表**


- [构建说明](#构建说明)
- [**启动**](#启动)
    - [无法登录](#无法登录临时解决)
- [**插件教程**](#插件教程)
    - [链接机器人](#链接机器人)
    - [Socket数据包](#数据包)
- [示例](#目前的示例代码)
- [使用ColorMirai的插件](#使用ColorMirai的插件)


## 构建说明
![Gradle构建](https://github.com/Coloryr/ColorMirai/workflows/Gradle%E6%9E%84%E5%BB%BA/badge.svg)

*需要JAVA8环境*

*运行环境看你的构建环境*

*构建不了可以去群里找人要*

请根据自己的系统先安装JDK11并且设置好环境变量  
- 安装git  
- 右键打开git bash  
输入下面的指令（如果慢的话, 可能需要梯子等工具辅助下载）
```bash
git clone https://github.com/Coloryr/ColorMirai.git
cd ColorMirai
./gradlew shadowJar
```
在`BUILD SUCCESSFUL`之后, 你会在以下路径找到一个jar文件

`build/libs/ColorMirai-2.4.0-SNAPSHOT-all.jar`

## 启动
> 1. 启动ColorMirai，必须使用JAVA8及以上，推荐使用JAVA14启动
> ```
> java -jar ColorMirai-2.X-SNAPSHOT-all.jar
> ```
> 首次启动后, 会生成`info.json`和`MainConfig.json`文件  
> `info.json`是设备名称，不需要特别设置  
> `MainConfig.json`是配置文件

> 2. 默认配置

> `MainConfig.json`
> ```Json
> {
>     "MaxList": 100000,
>     "Pack": true,
>     "Port": 23333,
>     "QQs": [
>         {
>             "QQ": 1234567890,
>             "Password": "qwe123456789"
>         }
>     ],
>     "Type": 0,
>     "escapeSelf": true
> }
> ```
> - `MaxList`：最大消息列表
> - `Pack`：是否发送心跳包
> - `Port`：启动的端口
> - `QQs` : QQ账号列表 (可以添加多个)
>     - `QQ`：登录的QQ号
>     - `Password`：QQ号密码
> - `Type`：登录的方式
> - 目前支持两种协议: 
>     - 安卓 [0]
>     - 手表 [1]
> - `escapeSelf`：是否跳过自己机器人的信息

设置完成后再次启动ColorMirai，出现`[INFO] Socket已启动: 23333` 说明机器人已成功启动

### 无法登录临时解决
1. 将协议切换成`手表 [1]`
2. 登录成功后, 下次登录可以选择`安卓 [0]`


## 插件教程

### 链接机器人
ColorMirai使用socket方式让机器人和插件互相链接  
Socket数据包的接受和封装在Demo已经写完了，只需要引用一下就好了  
下面的教程使用C#作为示例

首先下载Demo的代码并且导入到你的IDE  
[C#](/demo/C%23demo.cs) [JAVA](/demo/JAVAdemo.java)

编译后开始链接机器人

1.插件第一次连接需要发送一个数据包来注册所监听事件的包
```Json
{
  "Name": "test",
  "Reg": [49,50,51],
  "Groups" : [],
  "QQs" : [],
  "RunQQ": 0
}
```
- `Name`：插件名字
- `Reg`：监听的包
- `Groups`：只监听的群号，可以为null
- `QQs`：只监听的QQ号，可以为null
- `RunQQ`：插件运行的机器人QQ号，可以为0

注意：所有包必须带ID，否则无法识别，只有标注`（事件）`的包才会被监听，数据包ID的代表含义请看[数据包](#数据包)

2.C# Demo说明  
使用C#的类需要先实例化`RobotConfig`类
```C#
public class RobotConfig
{
    /// <summary>
    /// 机器人IP
    /// </summary>
    public string ip { get; init; }
    /// <summary>
    /// 机器人端口
    /// </summary>
    public int port { get; init; }
    /// <summary>
    /// 监听的包
    /// </summary>
    public List<byte> pack { get; init; }
    /// <summary>
    /// 插件名字
    /// </summary>
    public string name { get; init; }
    /// <summary>
    /// 监听的群，可以为null
    /// </summary>
    public List<long> groups { get; init; }
    /// <summary>
    /// 监听的qq号，可以为null
    /// </summary>
    public List<long> qqs { get; init; }
    /// <summary>
    /// 运行的qq，可以不设置
    /// </summary>
    public long runqq { get; init; }
    /// <summary>
    /// 重连时间
    /// </summary>
    public int time { get; init; }
    /// <summary>
    /// 检测是否断开
    /// </summary>
    public bool check { get; init; }
    /// <summary>
    /// 机器人事件回调函数
    /// </summary>
    public Action<byte, string> action { get; init; }
}
```
机器人回调，回调函数需要有byte, string两个参数  
一个示例回调方法
```C#
private static void Call(byte packid, string data)
{
    Console.WriteLine($"收到消息{data}");
    switch (packid)
    {
        case 49:
            var pack = JsonConvert.DeserializeObject<GroupMessageEventPack>(data);
            Robot.SendGroupMessage(Robot.QQs[0], pack.id, new()
            { $"{pack.fid} {pack.name} 你发送了消息：{pack.message[1]}" });
            break;
        case 50:
            break;
        case 51:
            break;
    }
}
```
根据需求填好参数后，实例化一个`Robot`类
```C#
var config = new RobotConfig
{
    name = "Demo",
    groups = null,
    qqs = null,
    runqq = 0,
    pack = new() { 49, 50, 51 },
    ip = "127.0.0.1",
    port = 23333,
    time = 10000,
    check = true,
    action = Call
};
Robot = new Robot(config);
```
启动机器人
```C#
Robot.Start();
```

### 数据包
大部分数据包是一串JSON字符串+数据包ID构成  
但是发送图片和发送语音用的是FormData格式

**注**：经测试Server 2008存在数据包断包和丢包的现象，请切换到Server 2019使用

```
以字符串的方式看
{...}[ID]
以byte[]的方式看
123,...,125,[ID]
```
`[ID]`对应数据包的ID  

目前重要的ID：
```
0 插件开始连接
49 [机器人]收到群消息（事件）
50 [机器人]收到群临时会话消息（事件）
51 [机器人]收到朋友消息（事件）

52 [插件]发送群消息
53 [插件]发送私聊消息
54 [插件]发送好友消息

60 心跳包

61 [插件]发送图片到群
62 [插件]发送图片到私聊
63 [插件]发送图片到朋友
```
只有正确的发包，才能处理  
更多包请看[PackDo.java](src/main/java/Color_yr/ColorMirai/Pack/PackDo.java)  
每个包需要发送的数据都在`/Pack`路径下的java  
标注为`[机器人]`是框架发给插件的  
标注为`[插件]`是插件发送给框架的

## 目前的示例代码
[C#](demo/C%23demo.cs)  
[JAVA](demo/JAVAdemo.java)  
示例代码不包含Main函数，可以作为库用

## 依赖ColorMirai的插件
[Minecraft_QQ_Gui/Cmd](https://github.com/HeartAge/Minecraft_QQ-C-Server-)  
[GitHubPush](https://github.com/Coloryr/GitHubPush)

## 使用ColorMirai的项目
[CmdControl](https://github.com/Coloryr/CmdControl)

<!--有人帮你写2333-->
懒得写了（jiushi）
