package slancer.nettystack.host.http;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
/**
 * Created by ashley on 17-4-7.
 */
public class HttpSvr implements ISvr {

    private ServerBootstrap serverBootstrap;
    protected NettyConf nettyConfig;
    protected ChannelInitializer<? extends Channel> channelInitializer;

    public HttpSvr(ChannelInitializer<? extends Channel> channelInitializer, int port) {
        this.channelInitializer = channelInitializer;
        this.nettyConfig = new NettyConf();
        nettyConfig.setPortNumber(port);
    }

    public void stopSvr() {
    }

    public void startSvr() {
        serverBootstrap = new ServerBootstrap();

        serverBootstrap.group(nettyConfig.getBossGroup(), nettyConfig.getWorkerGroup())
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, nettyConfig.getChannelBackLog())
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(channelInitializer);

        System.out.println(String.format("HTTP Server start now, and bind to %s", nettyConfig
                .getSocketAddress().toString()));

        try {
            Channel serverChannel = serverBootstrap
                    .bind(nettyConfig.getSocketAddress()).sync().channel();

            // block and monitoring,wait until the server socket is closed.
            serverChannel.closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            nettyConfig.getBossGroup().shutdownGracefully()
                    .syncUninterruptibly();
            nettyConfig.getWorkerGroup().shutdownGracefully()
                    .syncUninterruptibly();
        }
    }
}
