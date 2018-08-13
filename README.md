# wenda配置总结
---
## MySQL配置：
绑定 MySQL 地址到127.0.0.1：打开 MySQL 安装路径，在 my.ini 文件最后空白行键入`bind-address=127.0.0.1`，然后重启 MySQL 以使修改生效。
## Redis配置：
1. 通常情况下我们可以通过 redis-server.exe 和 redsi.windows.conf 配置文件启动 redis 服务`redis-server.exe redsi.windows.conf`，测试的时候用另启一个命令行窗口启动 redis-cli.exe 即可做一些简单的操作命令行。但是如果关闭了启动 redis-server.exe 的控制台时，那么就关闭了 redis 服务。

    在 Windows 中有个本地服务的概念，我们的目标就是将 Redis 注册成为本地服务，然后就可以不受控制台退出的影响。

        注册服务
        redis-server.exe --service-install redis.windows.conf
        删除服务
        redis-server --service-uninstall
        开启服务
        redis-server --service-start
        停止服务
        redis-server --service-stop

    还可以创建多个 Redis 实例：<br>
    **step1：** 复制一份 redis.windows.conf 配置文件，改名为 redis.windows10001.conf；<br>
    **step2：** 更改 redis.windows10001.conf 配置文件的信息，端口号 port 为 10001；<br>
    **step3：** 注册为 windows 服务。<br>
    `redis-server.exe --service-install redis.windows10001.conf --service-name Redis10001 --port 10001`
  <br><br>
2. 绑定 Redis 地址：打开 redis 安装路径，修改 redis.windows.conf 配置文件，查找`bind 127.0.0.1`，在后面追加需要绑定的IP，最后重新启动windows服务中的redis服务。
  <br><br>
3. Redis 设置密码登录：修改 redis.windows.conf 配置文件，查找`# requirepass foobared`，在下一空白行键入：<br>
  `requirepass <密码>`

## Solr配置：
1. 绑定 Solr 地址到127.0.0.1：打开 Solr 安装路径\server\etc 下的 jetty-http.xml 配置文件，修改：<br>
  `<Set name="host"><Property name="jetty.host" /></Set>`<br>
  为：<br>
  `<Set name="host">127.0.0.1</Set>`<br>
  同理，使用 https 连接时，修改 jetty-https.xml 配置文件。
  <br><br>
2. 配置中文分词器 ik-analyzer-solr7：<br>
  先下载 solr7 版本的ik分词器（ik-analyzer-7.4.0.jar），下载地址：https://search.maven.org/search?q=ik-analyzer<br>
  分词器 GitHub 源码地址：https://github.com/magese/ik-analyzer-solr7 ，参照 README.md 中的使用说明，使 ik-analyzer 适配最新版 solr7，并添加动态加载字典表功能（在不需要重启solr服务的情况下加载新增的字典）。
  <br><br>
3. 将 solr 注册成 windows 服务：使用 NSSM 这个工具，下载地址：http://www.nssm.cc/download 。下载 nssm 2.24-101-g897c7ad.zip，然后将 NSSM.exe 解压到 solr 的 bin 目录下，然后在 bin 文件夹空白处按住 Shift 键+右键，选择菜单“从此处打开命令窗口”启动 cmd 命令窗口，输入：<br>
  `nssm install solr`<br>
  在弹出的对话框中选好 solr 的启动文件 solr.cmd，启动参数 Arguments 里面填写：<br>
  `start -f`<br>
  要删除该服务可以用windows自带的命令：<br>
  `sc delete <服务名>`

## Tomcat配置：
1. 绑定 Tomcat 地址到127.0.0.1：打开 Tomcat 安装路径\conf 下的 server.xml 配置文件，在<br>
    `<Connector port="8080" protocol="HTTP/1.1" `**`address="127.0.0.1"`**` connectionTimeout="20000" redirectPort="8443" />`<br>
  中添加address的配置即可。
  <br><br>
2. 部署项目：将项目打成 war 包放入到 webapps 目录中（启动 bin 目录下的 startup.bat，tomcat 会自动将 war 包解压），然后打开 tomcat 安装目录\conf 下的 server.xml 文件，在`<Host> </Host>`标签之间输入项目配置信息：<br>
  `<Context docBase="..\webapps\<项目war包的名字>\" path="/" reloadable="true" debug="0"/>`

## Nginx配置：
修改在 nginx 的安装路径\conf 下的 nginx.conf 配置文件，如下：<br>

    server {
        listen       80;
        server_name  localhost;
        location / {
            proxy_pass	http://127.0.0.1:8080;
        }
    }
