package com.weicoder.socket.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import com.weicoder.socket.params.SocketParams;
import com.weicoder.common.util.CloseUtil;
import com.weicoder.socket.Client;
import com.weicoder.socket.Session;

/**
 * netty客户端
 * @author WD
 */
public final class NettyClient implements Client {
	// 保存Netty客户端 Bootstrap
	private Bootstrap		bootstrap;
	// 保存Netty服务器 ChannelFuture
	private ChannelFuture	future;
	// NettyHandler
	private NettyHandler		handler;
	// Session
	private Session			session;
	// 名称
	private String			name;

	/**
	 * 构造方法
	 * @param name 名称
	 */
	public NettyClient(String name) {
		// 名称
		this.name = name;
		// 实例化ClientBootstrap
		bootstrap = new Bootstrap();
		// NettyHandler
		handler = new NettyHandler(name);
		// 设置group
		bootstrap.group(new NioEventLoopGroup(1));
		// 设置属性
		bootstrap.option(ChannelOption.TCP_NODELAY, true);
		bootstrap.option(ChannelOption.SO_KEEPALIVE, false);
		bootstrap.option(ChannelOption.SO_LINGER, 0);
		bootstrap.option(ChannelOption.SO_SNDBUF, 1024 * 32);
		bootstrap.option(ChannelOption.SO_RCVBUF, 1024 * 8);
		// 设置channel
		bootstrap.channel(NioSocketChannel.class);
		// 设置初始化 handler
		bootstrap.handler(handler);
		// 设置监听端口
		bootstrap.remoteAddress(SocketParams.getHost(name), SocketParams.getPort(name));
	}

	@Override
	public void connect() {
		future = bootstrap.connect().awaitUninterruptibly();
		session = new NettySession(name, future.channel());
	}

	@Override
	public Session session() {
		// 如果session为空 或 未连接
		if (session == null) {
			// 连接
			connect();
		}
		// 返回session
		return session;
	}

	@Override
	public void close() throws Exception {
		CloseUtil.close(session);
		future.channel().close();
	}
}
