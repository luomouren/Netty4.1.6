package com.yao;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import com.yao.module.AskMsg;
import com.yao.module.AskParams;
import com.yao.module.Constants;
import com.yao.module.LoginMsg;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * Created by yaozb on 15-4-11.
 */
public class NettyClientBootstrap {
    private int port;
    private String host;
    private SocketChannel socketChannel;
    private volatile boolean closed = false;//是否是客户端自己断开连接
	private volatile EventLoopGroup workerGroup;
	private volatile Bootstrap bootstrap;
    
    public void close() {
		closed = true;
		workerGroup.shutdownGracefully();
		System.out.println("Stopped Tcp Client: " + getServerInfo());
	}
    
    public NettyClientBootstrap(int port, String host) throws InterruptedException {
        this.port = port;
        this.host = host;
        start();
    }
    
    private void start() throws InterruptedException {
    	closed = false;
    	workerGroup = new NioEventLoopGroup();
		bootstrap = new Bootstrap();
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.group(workerGroup);
        
        bootstrap.option(ChannelOption.SO_KEEPALIVE,true);
        bootstrap.remoteAddress(host,port);
        
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
			public void initChannel(SocketChannel ch) throws Exception {
				ChannelPipeline pipeline = ch.pipeline();
				pipeline.addLast(new IdleStateHandler(20,10,0));
				pipeline.addLast(new ObjectEncoder());
				pipeline.addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(null)));
				pipeline.addLast(new NettyClientHandler());
				
				pipeline.addFirst(new ChannelInboundHandlerAdapter() {
					@Override
					public void channelInactive(ChannelHandlerContext ctx) throws Exception {
						super.channelInactive(ctx);
						ctx.channel().eventLoop().schedule(() -> doConnect(), 1, TimeUnit.SECONDS);
					}
				});
				//todo: add more handler
				
			}
        });
        doConnect();
    }
    
	/**
	 * 连接Netty服务器
	 */
	private void doConnect() {
		if (closed) {
			return;
		}
		ChannelFuture future = bootstrap.connect(new InetSocketAddress(host, port));
		future.addListener(new ChannelFutureListener() {
			public void operationComplete(ChannelFuture f) throws Exception {
				if (f.isSuccess()) {
					//连接成功
					socketChannel = (SocketChannel)f.channel();
					System.out.println("Started Tcp Client: " + getServerInfo());
				} else {
					System.out.println("Started Tcp Client Failed: " + getServerInfo());
					//重新连接
					f.channel().eventLoop().schedule(() -> doConnect(), 1, TimeUnit.SECONDS);
				}
			}
		});
	}
    
    
    /**
     * @return 返回Netty服务器host、port
     */
    private String getServerInfo() {
		return String.format("host=%s port=%d",host,port);
	}
    
    public static void main(String[]args) throws InterruptedException {
        Constants.setClientId("001");
        NettyClientBootstrap bootstrap=new NettyClientBootstrap(9999,"localhost");

        LoginMsg loginMsg=new LoginMsg();
        loginMsg.setPassword("yao");
        loginMsg.setUserName("robin");
        bootstrap.socketChannel.writeAndFlush(loginMsg);
        while (true){
            TimeUnit.SECONDS.sleep(3);
            AskMsg askMsg=new AskMsg();
            AskParams askParams=new AskParams();
            askParams.setAuth("authToken");
            askMsg.setParams(askParams);
            bootstrap.socketChannel.writeAndFlush(askMsg);
        }
    }
}
