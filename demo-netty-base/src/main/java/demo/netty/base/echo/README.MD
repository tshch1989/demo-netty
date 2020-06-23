1.安装telnet客户端
2.
cmd#:telnet 
cmd#telnet:open 127.0.0.1 8080   //建立连接
cmd#telnet:ctrl + ]              //跳回telnet控制台
cmd#telnet:st                    //st查看连接状态
cmd#telnet:send hello netty      //发送hello netty到服务端
enter                            //发送
enter                            //获取返回结果


##########################
Telnet 客户端命常用命令：
  　open : 使用 openhostname 可以建立到主机的 Telnet 连接。
　　close : 使用命令 close 命令可以关闭现有的 Telnet 连接。
　　display : 使用 display 命令可以查看 Telnet 客户端的当前设置。
　　send : 使用 send 命令可以向 Telnet 服务器发送命令。支持以下命令：
　　ao : 放弃输出命令。
　　ayt : “Are you there”命令。
　　esc : 发送当前的转义字符。
　　ip : 中断进程命令。
　　synch : 执行 Telnet 同步操作。
　　brk : 发送信号。