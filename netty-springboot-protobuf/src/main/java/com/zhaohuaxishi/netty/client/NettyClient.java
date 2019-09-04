package com.zhaohuaxishi.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.HashedWheelTimer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.TimeUnit;

@Service("nettyClient")
@Slf4j
public class NettyClient {


    @Value("${server.bind_address}")
    private String host;

    @Value("${server.bind_port}")
    private Integer port;

    /**唯一标记 */
    private boolean initFalg=true;

    private EventLoopGroup group;
    private ChannelFuture f;

    protected final HashedWheelTimer timer = new HashedWheelTimer();

    /**
     * Netty创建全部都是实现自AbstractBootstrap。 客户端的是Bootstrap，服务端的则是 ServerBootstrap。
     **/
    @PostConstruct
    public void init() {
        group = new NioEventLoopGroup();
        doConnect(host, port);
        /*try {
            new NettyClient().connect(port, host);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }
    @PreDestroy
    public void shutdown() throws InterruptedException {
        log.info("正在停止客户端");
        try {
            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
        log.info("客户端已停止!");
    }
    /**
     * 重连
     */
    public void doConnect(String host, int port) {
        Bootstrap bootstrap = new Bootstrap();
        try {
            EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
            if (bootstrap != null) {
                bootstrap.group(eventLoopGroup);
                bootstrap.channel(NioSocketChannel.class);
                bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
                final ConnectionWatchdog watchdog = new ConnectionWatchdog(bootstrap, timer, port,host, true) {
                    public ChannelHandler[] handlers() {
                        return new ChannelHandler[] {
                                this,
                                new IdleStateHandler(0, 4, 0, TimeUnit.SECONDS),
                                new StringDecoder(),
                                new StringEncoder(),
                                new NettyClientHandler()
                        };
                    }
                };
                bootstrap.handler(new ChannelInitializer<Channel>() {
                    //初始化channel
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ch.pipeline().addLast(watchdog.handlers());
                    }
                });
                bootstrap.remoteAddress(host, port);
                f = bootstrap.connect().addListener((ChannelFuture futureListener) -> {
                    final EventLoop eventLoop = futureListener.channel().eventLoop();
                    if (!futureListener.isSuccess()) {
                        log.info("与服务端断开连接!在10s之后准备尝试重连!");
                        eventLoop.schedule(() -> doConnect(host, port), 10, TimeUnit.SECONDS);
                    }
                });
                /*f = bootstrap.connect(host, port);
                f.addListener(new ConnectionListener());
                channel = f.channel();*/
                if(initFalg){
                    log.info("Netty客户端启动成功!");
                    initFalg=false;
                }
            }
        } catch (Exception e) {
            log.info("客户端连接失败!"+e.getMessage());
        }
    }
}
