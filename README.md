<p align="center">
	<a href="https://scmtww.gnway.cc:9123"><img src="http://223.87.166.240:8014/logo.png"></a>
</p>
<p align="center">
	<strong>适合互联网企业使用的开源职业体检管理系统</strong>
</p>
<p align="center">
	👉 <a href="https://scmtww.gnway.cc:9123">https://scmtww.gnway.cc</a> 👈
</p>

<p align="center">
	<a target="_blank" href="https://spring.io/projects/spring-boot">
		<img src="https://img.shields.io/badge/spring%20boot-2.4.5-yellowgreen" />
	</a>
    <a target="_blank" href="https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html">
		<img src="https://img.shields.io/badge/JDK-8-green.svg" />
	</a>
	<a target="_blank" href="http://www.gnu.org/licenses/lgpl.html">
		<img src="https://img.shields.io/badge/license-LGPL--3.0-blue" />
	</a>
</p>


-------------------------------------------------------------------------------

## 📚 项目介绍

backend是一套适合互联网企业使用的开源职业体检管理系统，可以对接医院各个设备便于数据录入以及出具体检报告。

backend使用`Spring Boot`和`iView Vue`开发，集成`Spring Security`实现权限管理功能，是一套非常实用的web开发框架。


## 🍎 项目特点

* 对接了省上职业病体检管理系统，方便网报
* 可以对接各个医疗设备，便于数据录入以及报告整理
* 使用`spring boot`框架作为后端框架，便于维护
* 接口请求和响应数据采用签名机制，保证交易安全可靠
* 管理平台操作界面简洁、易用
* 使用`spring security`实现权限管理
* 前后端分离架构，方便二次开发

## 🥞 系统架构

> 职业病体检系统架构图

![系统架构设计](vx_images/179203314242438.png)
> 职业病体检流程图

 ![体检流程图](vx_images/343693211239179.png)


> 核心技术栈

| 软件名称  | 描述 | 版本
|---|---|---
|Jdk | Java环境 | 1.8
|Spring Boot | 开发框架 | 2.4.5
|Redis | 分布式缓存 | 3.2.8 或 高版本
|MySQL | 数据库 | 5.7.X 或 8.0 高版本
|[Iview Ui](http://iview.talkingdata.com/) | iview Vue框架，前端开发使用 | 4.7.0
|[MyBatis-Plus](https://mp.baomidou.com/) | MyBatis增强工具 | 3.4.2
|[Hutool](https://www.hutool.cn/) | Java工具类库 | 5.6.6

> 项目结构

```lua
backend-ui  -- https://gitee.com/scmt1/backend-ui.git

backend
├── scmt-admin -- 公共配置 启动项目包
└── scmt-core -- 公共工具包
├── scmt-modules -- 核心依赖包
     ├── scmt-activiti -- activiti组件
     └── scmt-base -- 父组件
     ├── scmt-generatori -- 代码生成组件
     └── scmt-license -- 授权组件     
     ├── scmt-yw -- 业务代码组件
└── vx_images -- 项目截图
```



## 🍿 功能模块

> 职业病系统平台功能
![系统功能模块图](vx_images/332934814235483.png)

## 🍯 系统截图

`以下截图是从实际已完成功能界面截取,截图时间为：2022-07-06 08:59`
![001](vx_images/322900612227046.jpg)![002](vx_images/429370612247212.jpg)
![002](vx_images/228890712239881.png)![003](vx_images/301140712236436.jpg)![004](vx_images/400260712232190.jpg)![005](vx_images/499770712250070.jpg)![006](vx_images/3290812247674.jpg)![007](vx_images/91860812245176.jpg)![008](vx_images/176260812226417.jpg)![009](vx_images/271210812248857.jpg)![010](vx_images/343710812243993.jpg)![012](vx_images/498320812230673.jpg)![013](vx_images/570080812221203.jpg)![014](vx_images/42010912223707.jpg)![015](vx_images/122230912232654.jpg)![016](vx_images/206350912225539.jpg)![017](vx_images/330830912226148.jpg)![0171](vx_images/463290912253103.png)![0172](vx_images/587130912235316.jpg)![018](vx_images/65781012224614.jpg)![019](vx_images/188261012220865.png)![020](vx_images/541951012233094.png)![021](vx_images/50411112238133.png)![022](vx_images/146161112239428.png)![023](vx_images/246971112240430.png)![024](vx_images/330101112240607.png)![025](vx_images/409861112226159.jpg)
## 🥪 关于我们
***
微信扫描下面二维码，关注官方公众号：民图科技，获取更多精彩内容。

![计全科技公众号](http://jeequan.oss-cn-beijing.aliyuncs.com/jeepay/img/jee-qrcode.jpg "计全科技公众号")

微信扫描下方二维码，邀请进官方微信交流群（加好友备注：邀请进群或jeepay咨询），开源不易，进群前请先点Star给与支持。

![Jeepay微信交流群](http://jeequan.oss-cn-beijing.aliyuncs.com/jeepay/img/wx_my.png "Jeepay微信交流群")
