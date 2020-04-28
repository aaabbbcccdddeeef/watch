package com.ctop.studentcard.netty;

import com.ctop.studentcard.util.LogUtil;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

class OutboundHandlerA extends ChannelOutboundHandlerAdapter {

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        LogUtil.e("push msg : "+msg);
        super.write(ctx, msg, promise);
    }
}
