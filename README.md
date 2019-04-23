# *JAVA - API*
**JAVA API Framework**

环境：
>* OpenJDK 1.8
>* IDEA 2018
>* spring-boot 2.1.1
>* swagger 2.9.2

安装：
```
git clone https://github.com/wuguangnuo/java-api.git
```

配置：
>* IDEA java Setting, Project Setting
>* 框架 resources/application.yml
>* 填写 DB/rabbitMQ/Redis 等账号密码
>* 生成数据库实体 run appGenerator.java

运行：
>* run app.java
>* http://localhost:8081/

发布：
>* 调整 SwaggerConfig.java、application.yml
>* 打包 Maven clear, Maven package
>* 解压 target/xxx.zip start.bat
