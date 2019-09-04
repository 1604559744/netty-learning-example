package com.zhaohuaxishi.netty.client;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoop;
import org.springframework.beans.factory.annotation.Value;

import java.util.concurrent.TimeUnit;

/**
 * @Author: zhaohuaxishi丶
 * @Description:
 * @Date: Creaded in 9:20 2019/9/3 0003
 */
public class ConnectionListener implements ChannelFutureListener{
    @Value("${server.bind_address}")
    private String host;

    @Value("${server.bind_port}")
    private Integer port;

    private NettyClient Connection = new NettyClient();
    @Override
    public void operationComplete(ChannelFuture channelFuture) throws Exception {
        if (!channelFuture.isSuccess()) {
            final EventLoop loop = channelFuture.channel().eventLoop();
            loop.schedule(new Runnable() {
                @Override
                public void run() {
                    System.err.println("与服务端断开连接!在10s之后准备尝试重连!");
                    Connection.doConnect(host, port);
                }
            }, 10, TimeUnit.SECONDS);
        } else {
            System.err.println("服务端链接成功...");
        }
    }
}
