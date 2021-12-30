### 这是一个springboot程序模版
> 用来开发新项目的时候不需要再去搭建，直接用模版

#### 整合的功能
* 独立socket程序
* 后台管理shiro权限控制
* 移动端api jwt+token
* 腾讯云短信模版
* Redis整合
* logback日志
* queue延迟任务队列实现
* 移动端api完成程序常见接口、登录注册、用户个人信息修改、发送验证码、意见反馈、帮助信息
* 后台管理常见接口、管理员管理、意见反馈管理、帮助信息管理、用户管理
* 待整合RabbitMQ。。。

#### 结构说明
* **doc** : 项目文件目录
  * **desc**: 项目说明文件
  * **sql** : sql文件
* **jishi-admin** : 后台管理
* **jishi-api** : 移动端api
* **jishi-core** : 核心
* **jishi-db** : 数据库相关
* **jishi-start** : 程序启动
* **netty-demo** : socket程序
* **logs** : 主程序日志目录
* **netty-logs** : socket程序日志目录