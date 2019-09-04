package com.zhaohuaxishi.netty.client;

import com.alibaba.fastjson.JSONObject;
import com.zhaohuaxishi.netty.bean.ParamQuery;
import com.zhaohuaxishi.netty.bean.param;
import com.zhaohuaxishi.netty.dao.SaveData;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.EventLoop;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Value;


@ChannelHandler.Sharable
@Slf4j
public class NettyClientHandler extends ChannelInboundHandlerAdapter {
    @Value("${server.bind_address}")
    private String host;

    @Value("${server.bind_port}")
    private Integer port;
    @Autowired
    private NettyClient nettyClient;


    /** 循环次数 */
    private AtomicInteger fcount = new AtomicInteger(1);

    /**
     * 建立连接时
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("激活时间是："+new Date());
        log.info("NettyClientHandler channelActive");
        ctx.fireChannelActive();
    }

    /**
     * 关闭连接时
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("停止时间是："+new Date());
        log.info("NettyClientHandler channelInactive");
        /*final EventLoop eventLoop = ctx.channel().eventLoop();
        //nettyClient.doConnect(new Bootstrap(), eventLoop);
        eventLoop.schedule(new Runnable() {
            @Override
            public void run() {
                log.info("服务端连接不上，开始重连操作");
                nettyClient.doConnect(host, port);
            }
        }, 10, TimeUnit.SECONDS);
        super.channelInactive(ctx);*/
        //super.channelInactive(ctx);
        /*try {
            new NettyClient().connect(port, host);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    /**
     * 心跳请求处理 每4秒发送一次心跳请求;
     *
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object obj) throws Exception {
        log.info("循环请求的时间：" + new Date() + "，次数" + fcount.get());
        if (obj instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) obj;
            // 如果写通道处于空闲状态,就发送心跳命令
            if (IdleState.WRITER_IDLE.equals(event.state())) {
                //UserMsg.User.Builder userState = UserMsg.User.newBuilder().setState(2);
                String msg = "{\"command\":\"send_head\",\"code\":\"qzj0001\"}\n";
                ctx.channel().writeAndFlush(msg);
                fcount.getAndIncrement();
            }
        }
    }

    /**
     * 业务逻辑处理
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {

            /*// 得到protobuf的数据
            UserMsg.User userMsg = null;
            // 进行相应的业务处理。。。
            // 这里就从简了，只是打印而已
            log.info(
                    "客户端接受到的用户信息。编号:" + userMsg.getId() + ",姓名:" + userMsg.getName() + ",年龄:" + userMsg.getAge());*/
            //TODO 这里处理数据（发送应答、存到数据库）
            // 报文解析处理
            String ServerMsg=msg.toString();
            Map<String, Object> result = JSON.parseObject(ServerMsg);
            /*List<String> list= result.entrySet().stream().sorted(Comparator.comparing(e->e.getKey())).map(e -> e.getValue().toString()).collect(Collectors.toList());*/
            ParamQuery bean=new ParamQuery();
            bean.setSeq(result.get("seq").toString());
            bean.setCode(result.get("code").toString());
            bean.setType(result.get("type").toString());
            bean.setPortId(result.get("portId").toString());
            bean.setGroup(result.get("group").toString());
            Map<String, Object> paramStr  = JSON.parseObject(result.get("param").toString());
            param paramBean=new param();
            paramBean.setFactory(paramStr.get("factory").toString());
            paramBean.setCommand(paramStr.get("command").toString());
            paramBean.setTime(paramStr.get("time").toString());
            paramBean.setPool(paramStr.get("pool").toString());
            paramBean.setCode(paramStr.get("code").toString());
            paramBean.setTempoId(paramStr.get("tempoId").toString());
            paramBean.setPac(paramStr.get("pac").toString());
            paramBean.setRetCode(0);
            paramBean.setRetVal("保存成功");
            bean.setParamBean(paramBean);
            SaveData.saveControllableSocketInfo(bean);
            log.info("接收到的服务端的信息!" + msg);
            /*// 这里返回一个已经接受到数据的状态
            UserMsg.User.Builder userState = UserMsg.User.newBuilder().setState(1);*/
            Map<String, String> returnMsg = new HashMap<>();
            returnMsg.put("seq",bean.getSeq());
            returnMsg.put("code",bean.getCode());
            returnMsg.put("type",bean.getType());
            returnMsg.put("portId",bean.getPortId());
            returnMsg.put("group", bean.getGroup());
            returnMsg.put("factory",paramBean.getFactory());
            returnMsg.put("command","set_param_ret");
            returnMsg.put("retCode", String.valueOf(paramBean.getRetCode()));
            returnMsg.put("retVal",paramBean.getRetVal());
            String infoMsg = JSON.toJSONString(returnMsg);
            ctx.writeAndFlush(infoMsg);
            log.info("成功发送给服务端!"+infoMsg);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

}
