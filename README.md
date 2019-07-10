# springlearn
#### springlearn是基于springboot的学习项目，欢迎任何人下载运行<br>
#### master会随着学习不断更新，每个单独的功能可以参考不同的分支<br>
    分支：master_security
    这个分支仅集成了spring security，附有sql文件，登录地址为localhost:8080/login，使用了原生的登录界面
    表tbl_user是用户的基本信息
    表tbl_role是权限相关设置
    表tbl_user_role是用户关联权限
    用户名：admin 密码：123456 可以访问localhost：8080/getAllUsers以及localhost:8080/bookList
    用户名：user  密码：user   可以访问localhost:8080/bookList
    目前只完成了userController，bookController并未开发，只用作页面展示及权限功能调试
