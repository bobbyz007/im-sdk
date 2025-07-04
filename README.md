# im-sdk
An IM(instant message) SDK

Based on https://github.com/JackJiang2011/MobileIMSDK

为了突出核心交互逻辑， 简化界面实现。
1. 简化java原生界面实现，仅仅依赖javax swing，不依赖任何第三方包。
2. 优化代码逻辑。

## How to run?
1. 启动server：net.x52im.mobileimsdk.server.demo.ServerLauncherImpl
2. 启动多个tcp或udp客户端： net.x52im.mobileimsdk.java.demo
3. 随便输入用户名或密码登录， 即可在各个用户之间发送消息。

## FAQ
如果调试启动时报错 "Caused by: java.lang.ClassNotFoundException: kotlin.Result",
打开设置界面 Settings -> Build,Execution,Deployment -> Debugger, 在kotlin设置下面不要勾选"Attach coroutine agent"