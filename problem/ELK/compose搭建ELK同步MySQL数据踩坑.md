[TOC]

# compose搭建ELK同步MySQL数据踩坑

### elasticsearch的端口配置成了es-heads的9100端口

![image-20191203204947106](assets/image-20191203204947106.png)

这种错误一般都是配置文件配置有误。

就是这里的端口写成了es-head的9100端口，所以一直启动失败。

![image-20191203205055405](assets/image-20191203205055405.png)

