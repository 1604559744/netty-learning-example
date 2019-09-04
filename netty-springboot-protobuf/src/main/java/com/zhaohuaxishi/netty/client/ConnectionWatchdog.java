package com.zhaohuaxishi.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.util.Timeout;
import io.netty.util.Timer;
import io.netty.util.TimerTask;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.concurrent.TimeUnit;


/**
 * @Author: zhaohuaxishi丶
 * @Description:
 * @Date: Creaded in 10:15 2019/9/3 0003
 */


@ChannelHandler.Sharable
@Slf4j
    public abstract class ConnectionWatchdog extends ChannelInboundHandlerAdapter implements TimerTask,ChannelHandlerHolder{



        private final Bootstrap bootstrap;
        private final Timer timer;
        private final int port;

        private final String host;

        private volatile boolean reconnect = true;
        private int attempts;


        public ConnectionWatchdog(Bootstrap bootstrap, Timer timer, int port,String host, boolean reconnect) {
            this.bootstrap = bootstrap;
            this.timer = timer;
            this.port = port;
            this.host = host;
            this.reconnect = reconnect;
        }

        /**
         * channel链路每次active的时候，将其连接的次数重新☞ 0
         */
        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            log.info("当前链路已经激活了，重连尝试次数重新置为0"+new Date());
            attempts = 0;
            ctx.fireChannelActive();
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            log.info("连接关闭"+new Date());
            if(reconnect){
                log.info("链接关闭，将进行重连"+new Date());
                attempts++;
                //重连的间隔时间会越来越长
                int timeout = 2 << attempts;
                timer.newTimeout(this, timeout, TimeUnit.MILLISECONDS);
            }
            ctx.fireChannelInactive();
        }

        public void run(Timeout timeout) throws Exception {
            ChannelFuture future;
            //bootstrap已经初始化好了，只需要将handler填入就可以了
            synchronized (bootstrap) {
                bootstrap.handler(new ChannelInitializer<Channel>(){
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ch.pipeline().addLast(handlers());
                    }
                });
                future = bootstrap.connect(host,port);
            }
            //future对象
            future.addListener(new ChannelFutureListener() {
                public void operationComplete(ChannelFuture f) throws Exception {
                    boolean succeed = f.isSuccess();
                    //如果重连失败，则调用ChannelInactive方法，再次出发重连事件，一直尝试12次，如果失败则不再重连
                    if (!succeed) {
                        log.info("重连失败"+new Date());
                        f.channel().pipeline().fireChannelInactive();
                    }else{
                        log.info("重连成功"+new Date());
                    }
                }
            });
        }
    }

