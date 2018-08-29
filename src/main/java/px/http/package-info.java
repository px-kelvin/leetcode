package px.http;
//由于netty天生是异步事件驱动的架构，因此基于NIO TCP协议开发的HTTP协议也是异步非阻塞的
//文件服务器使用Http协议对外提供服务