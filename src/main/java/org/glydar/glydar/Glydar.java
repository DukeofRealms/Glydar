package org.glydar.glydar;

import io.netty.bootstrap.ChannelFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.glydar.glydar.netty.CubeWorldServerInitializer;
import org.glydar.glydar.plugin.PluginLoader;

import java.net.InetSocketAddress;
import java.nio.channels.Channel;

public class Glydar {

    private static Server s = new Server(false); //TODO command line arg for debug
    private static Thread serverThread = new Thread(s);
	private static final PluginLoader loader = new PluginLoader();
    private static ChannelFuture chan;
    private static ServerBootstrap serverBootstrap;

    public static void main(String[] args) {
		serverBootstrap = new ServerBootstrap();
		serverBootstrap.childHandler(new CubeWorldServerInitializer());
		//serverBootstrap.option("child.tcpNoDelay", true);
		//serverBootstrap.setOption("child.keepAlive", true);
		serverBootstrap.group(new NioEventLoopGroup());
		serverBootstrap.channelFactory(new ChannelFactory<ServerChannel>() {
			@Override
			public ServerChannel newChannel() {
				return new NioServerSocketChannel();
			}
		});

		try {
			loader.loadPlugins();
		} catch (Exception e) {
			e.printStackTrace();
		}

		final int port = 12345;

		chan = serverBootstrap.bind(new InetSocketAddress(port));

		s.getLogger().info("Server ready on port " + port);
        serverThread.start();
	}

    public static Server getServer() {
        return s;
    }

    public static void shutdown() {
        getServer().shutdown();
        serverThread.interrupt();
        chan.channel().close();
        serverBootstrap.childGroup().shutdownGracefully();
        serverBootstrap.group().shutdownGracefully();
    }

}
