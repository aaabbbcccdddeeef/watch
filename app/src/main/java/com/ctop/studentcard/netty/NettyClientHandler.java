package com.ctop.studentcard.netty;

import com.ctop.studentcard.base.BaseSDK;
import com.ctop.studentcard.util.Const;
import com.ctop.studentcard.util.DeviceUtil;
import com.ctop.studentcard.util.LogUtil;
import com.ctop.studentcard.util.PackDataUtil;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * Created by dgl on 11/23/2019.
 */
public class NettyClientHandler extends SimpleChannelInboundHandler<ByteBuf> {


    private ChannelListener listener;
    private SendBack sendBack;

    public NettyClientHandler(ChannelListener listener, SendBack sendBack) {
        this.listener = listener;
        this.sendBack = sendBack;
    }

    public NettyClientHandler() {
    }



    //在socket通道建立时被触发
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        LogUtil.e("socket通道建立 成功");
        NettyClient.getInstance(BaseSDK.getBaseContext()).setConnectStatus(true);
        listener.onServiceStatusConnectChanged(ChannelListener.STATUS_CONNECT_SUCCESS);
    }

    //断开连接触发channelInactive
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        LogUtil.e("channelInactiveHandler:断开连接触发");
        NettyClient.getInstance(BaseSDK.getBaseContext()).setConnectStatus(false);
        listener.onServiceStatusConnectChanged(ChannelListener.STATUS_CONNECT_CLOSED);
//        NettyClient.getInstance().reconnect();
    }

    //客户端收到消息
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf string) {
        try{
            String msg = PackDataUtil.convertByteBufToString(string);
            LogUtil.e("channelRead msg: "+msg);
            listener.onMessageResponse(msg);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //利用写空闲发送心跳检测消息
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (evt instanceof IdleStateEvent) {
			IdleStateEvent event = (IdleStateEvent) evt;
			if (event.state() == IdleState.ALL_IDLE){
				try{
                    if (Const.LOGIN_SUCCESS) {
                        LogUtil.e("send heart");
                        String waterNumber = PackDataUtil.createWaterNumber();
                        String data = PackDataUtil.packRequestStr(BaseSDK.getBaseContext(),waterNumber, Const.REPORT_HEARTBEAT, Const.REPORT_THE_REQUEST, DeviceUtil.getBattery());
                        ctx.channel().writeAndFlush(data).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
                    }
				} catch (Exception e){
					LogUtil.e(e.getMessage());
				}
			}
		}
		super.userEventTriggered(ctx, evt);
	}

    //异常回调,默认的exceptionCaught只会打出日志，不会关掉channel
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
//        listener.onServiceStatusConnectChanged(ChannelListener.STATUS_CONNECT_ERROR);
        cause.printStackTrace();
    }
}
