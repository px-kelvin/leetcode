package px.netty.flash.channel;

/**
 * 在netty中，每一条连接就对应一个channel,这条Channel所有的处理逻辑都在一个叫做channelPipeline对象中，channelPipeline是一个双向链表，
 * 和channel是一对一的关系
 *
 * channelPipeline中每一个节点就是一个ChannelHandlerContext对象，可以拿到channel相关的所有上下文与channelHandler
 */