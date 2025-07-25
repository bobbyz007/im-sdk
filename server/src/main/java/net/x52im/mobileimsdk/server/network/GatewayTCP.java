/*
 * Copyright (C) 2023  即时通讯网(52im.net) & Jack Jiang.
 * The MobileIMSDK v6.4 Project. 
 * All rights reserved.
 * 
 * > Github地址：https://github.com/JackJiang2011/MobileIMSDK
 * > 文档地址：  http://www.52im.net/forum-89-1.html
 * > 技术社区：  http://www.52im.net/
 * > 技术交流群：320837163 (http://www.52im.net/topic-qqgroup.html)
 * > 作者公众号：“【即时通讯技术圈】”，欢迎关注！
 * > 联系作者：  http://www.52im.net/thread-2792-1-1.html
 *  
 * "即时通讯网(52im.net) - 即时通讯开发者社区!" 推荐开源工程。
 * 
 * GatewayTCP.java at 2023-9-21 15:24:55, code by Jack Jiang.
 */
package net.x52im.mobileimsdk.server.network;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.MultiThreadIoEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.nio.NioIoHandler;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.timeout.ReadTimeoutHandler;
import net.x52im.mobileimsdk.server.ServerCoreHandler;
import net.x52im.mobileimsdk.server.network.tcp.MBTCPClientInboundHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GatewayTCP extends Gateway
{
	private static Logger logger = LoggerFactory.getLogger(GatewayTCP.class); 
	
    public static int PORT = 8901;
    public static int SESION_RECYCLER_EXPIRE = 20;//10;
    public static int TCP_FRAME_FIXED_HEADER_LENGTH = 4;     // 4 bytes
	public static int TCP_FRAME_MAX_BODY_LENGTH  = 6 * 1024; // 6K bytes
	
	public static SslContext sslContext = null;

	protected final EventLoopGroup __bossGroup4Netty = new MultiThreadIoEventLoopGroup(1, NioIoHandler.newFactory());
 	protected final EventLoopGroup __workerGroup4Netty = new MultiThreadIoEventLoopGroup(NioIoHandler.newFactory());
 	protected Channel __serverChannel4Netty = null;
 	
 	protected ServerBootstrap bootstrap = null;

	@Override
 	public void init(ServerCoreHandler serverCoreHandler)
    {
        bootstrap = new ServerBootstrap()
			.group(__bossGroup4Netty, __workerGroup4Netty)
			.channel(NioServerSocketChannel.class)
			.childHandler(initChildChannelHandler(serverCoreHandler));
        
        bootstrap.option(ChannelOption.SO_BACKLOG, 4096);
        bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.childOption(ChannelOption.TCP_NODELAY, true);
    }
    
	@Override
    public void bind() throws Exception
    {
        ChannelFuture cf = bootstrap.bind(PORT).sync();
        if (cf.isSuccess()) {
        	logger.info("[IMCORE-tcp] 基于MobileIMSDK的TCP服务绑定端口"+PORT+"成功 √ "+(isSsl()?"(已开启SSL/TLS加密传输)":""));
        }
        else{
        	logger.info("[IMCORE-tcp] 基于MobileIMSDK的TCP服务绑定端口"+PORT+"失败 ×");
        }
        
		__serverChannel4Netty = cf.channel();
		__serverChannel4Netty.closeFuture().addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				__bossGroup4Netty.shutdownGracefully();
				__workerGroup4Netty.shutdownGracefully();
			}
		});
		
		logger.info("[IMCORE-tcp] .... continue ...");
		logger.info("[IMCORE-tcp] 基于MobileIMSDK的TCP服务正在端口"+ PORT +"上监听中...");
    }
  
	@Override
	public void shutdown()
	{
    	if (__serverChannel4Netty != null) 
    		__serverChannel4Netty.close();
	}
	
    protected ChannelHandler initChildChannelHandler(final ServerCoreHandler serverCoreHandler)
	{
		return new ChannelInitializer<Channel>() {
			@Override
			protected void initChannel(Channel channel) throws Exception {
				
				ChannelPipeline pipeline = channel.pipeline(); 
				
				if(sslContext != null) {
					pipeline.addFirst(sslContext.newHandler(channel.alloc()));
				}
				
				pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(
							TCP_FRAME_FIXED_HEADER_LENGTH+TCP_FRAME_MAX_BODY_LENGTH
                        	, 0, TCP_FRAME_FIXED_HEADER_LENGTH, 0, TCP_FRAME_FIXED_HEADER_LENGTH));
                pipeline.addLast("frameEncoder", new LengthFieldPrepender(TCP_FRAME_FIXED_HEADER_LENGTH));
				pipeline.addLast(new ReadTimeoutHandler(SESION_RECYCLER_EXPIRE));
				pipeline.addLast(new MBTCPClientInboundHandler(serverCoreHandler));
			}
		};
	}
    
    public static boolean isSsl() 
    {
    	return sslContext != null;
    }
}
