package com.ctop.studentcard.netty;

import java.util.concurrent.TimeUnit;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.HashedWheelTimer;

/**
 *
 * Created by dgl on 11/23/2019.
 */
public class NettyClientInitializer extends ChannelInitializer<SocketChannel> {

    private ChannelListener listener;
    private SendBack sendBack;
    private ChannelPipeline pipeline;

    public NettyClientInitializer(ChannelListener listener, SendBack sendBack) {
        this.listener = listener;
        this.sendBack = sendBack;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        pipeline = ch.pipeline();
        // 创建分隔符缓冲对象$_作为分割符
        ByteBuf byteBuf = Unpooled.copiedBuffer("\r\n".getBytes());
        // 第一个参数：单条消息的最大长度，当达到最大长度仍然找不到分隔符抛异常，防止由于异常码流缺失分隔符号导致的内存溢出
        // 第二个参数：分隔符缓冲对象
        pipeline.addLast("buff",new DelimiterBasedFrameDecoder(2048,byteBuf));
//        pipeline.addLast("heart",new IdleStateHandler(0, 0,  5*60, TimeUnit.SECONDS));
        //添加拦截器
        pipeline.addLast(new LoggingHandler(LogLevel.TRACE));    // 开启日志，可以设置日志等级
        pipeline.addLast(new NettyClientHandler(listener,sendBack));
        pipeline.addLast(new StringEncoder());
        pipeline.addLast(new StringDecoder());
        pipeline.addLast("out1", new OutboundHandlerA());
    }


    public ChannelPipeline getPipeline() {
        return pipeline;
    }

    public void setPipeline(ChannelPipeline pipeline) {
        this.pipeline = pipeline;
    }
}
