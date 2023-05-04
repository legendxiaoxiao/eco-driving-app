# eco-driving-app
An ecological driving app

App	

本部分是app的前端开发code，利用androidstuid进行开发

本APP UI界面共四个模块：dashboard,home,notification,suggestion

Httputils是负责接口的文件夹，主要负责前后端的数据传输

在主页界面，利用百度开放API，根据手机的定位系统，收集手机的位置信息（cityselect文件夹）

UI文件夹下则是APP四个界面的前端code

主页界面通过百度API收集到的数据，通过接口传输到后端数据库（阿里云），然后当行车记录和驾驶建议界面激活的时候，后端处理后的数据接到前端请求传输至UI界面上。

考虑到安卓app的生命周期性质，当一个界面激活，另一个界面会死去，为了保存一些界面的数据，在mainactivity中缓存一些数据。

登录界面的CODE在use和loginactivity中。

关于整个app的xml文件，在res文件夹下的layout文件夹中。

App2

该部分为app的后端部分，我们租用了阿里云服务器，进行后端布设。

该部分的code为处理前端返回的地理位置数据以及驾驶员的速度，时间，加速度等信息。

利用我们构建的能耗模型进行生态驾驶评分
