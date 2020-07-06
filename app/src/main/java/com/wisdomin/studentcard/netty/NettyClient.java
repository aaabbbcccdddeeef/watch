package com.wisdomin.studentcard.netty;

import android.content.Context;

import com.wisdomin.studentcard.api.OnReceiveListener;
import com.wisdomin.studentcard.util.LogUtil;
import com.wisdomin.studentcard.util.PreferencesUtils;
import com.wisdomin.studentcard.util.PropertiesUtil;

import java.net.SocketAddress;
import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.channel.unix.DomainSocketAddress;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * Netty客户端
 * Created by dgl on 11/23/2019.
 */
public class NettyClient {

    private static NettyClient nettyClient = new NettyClient();
    private EventLoopGroup group;
    private ChannelListener listener;
    private SendBack sendBack;
    private Channel mChannel;
    private boolean isConnect = false;
    private int reconnectNum = Integer.MAX_VALUE;
    private long reconnectIntervalTime = 20000;
    private static Context mContext;
    private NettyClientInitializer nettyClientInitializer;

    public static NettyClient getInstance(Context context) {
        mContext = context;
        return nettyClient;
    }

    public synchronized void connect() {
        PreferencesUtils.getInstance(mContext).setBoolean("stopTcp",false);
        if (!isConnect) {
            if (mChannel != null && mChannel.isActive()) {
                return;
            }
            try {
                group = new NioEventLoopGroup();
                nettyClientInitializer =  new NettyClientInitializer(listener, sendBack);
                Bootstrap bootstrap = new Bootstrap().group(group)
                        .option(ChannelOption.SO_KEEPALIVE, true)
                        .channel(NioSocketChannel.class)
                        .handler(nettyClientInitializer);
                if( PropertiesUtil.getInstance().getTcp_port(mContext) == 0){//域名
                    LogUtil.e(" ------/域名="+PropertiesUtil.getInstance().getHost(mContext));
                    SocketAddress socketAddress = new DomainSocketAddress(PropertiesUtil.getInstance().getHost(mContext));
                    bootstrap.connect(socketAddress)
                            .addListener(new ChannelFutureListener() {
                                @Override
                                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                                    if (channelFuture.isSuccess()) {
                                        LogUtil.d("tcp connect:" + PropertiesUtil.getInstance().getHost(mContext) + ":" +
                                                PropertiesUtil.getInstance().getTcp_port(mContext));
                                        LogUtil.d(" --------------------------------- connect success ---------------------------------");
                                        isConnect = true;
                                        mChannel = channelFuture.channel();
                                    } else {
                                        isConnect = false;
                                        LogUtil.d(" --------------------------------- connect failed,reconnect ---------------------------------");
//                            reconnect();
                                    }
                                }
                            }).sync();
                }else {//ip+端口
                    LogUtil.e(" ------/ip+端口="+PropertiesUtil.getInstance().getHost(mContext)+":"+PropertiesUtil.getInstance().getTcp_port(mContext));
                    bootstrap.connect(PropertiesUtil.getInstance().getHost(mContext),
                            PropertiesUtil.getInstance().getTcp_port(mContext))
                            .addListener(new ChannelFutureListener() {
                                @Override
                                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                                    if (channelFuture.isSuccess()) {
                                        LogUtil.d("tcp connect:" + PropertiesUtil.getInstance().getHost(mContext) + ":" +
                                                PropertiesUtil.getInstance().getTcp_port(mContext));
                                        LogUtil.d(" --------------------------------- connect success ---------------------------------");
                                        isConnect = true;
                                        mChannel = channelFuture.channel();
                                    } else {
                                        isConnect = false;
                                        LogUtil.d(" --------------------------------- connect failed,reconnect ---------------------------------");
//                            reconnect();
                                    }
                                }
                            }).sync();
                }
            } catch (Exception e) {
                LogUtil.e(e.getMessage());
                listener.onServiceStatusConnectChanged(ChannelListener.STATUS_CONNECT_ERROR);
            }
        }
    }

    public void disconnect() {
        if (mChannel != null) {
            mChannel.disconnect();
            mChannel.close();
            mChannel.eventLoop().shutdownGracefully();
            group.shutdownGracefully();
        }
    }

    public void reconnect() {
        if (reconnectNum > 0 && !isConnect) {
            reconnectNum--;
            try {
                Thread.sleep(reconnectIntervalTime);
            } catch (InterruptedException e) {
            }
            LogUtil.e("重新连接");
            disconnect();
            connect();
        } else {
            disconnect();
        }
    }

    public boolean userAuth(String data, ChannelFutureListener channelFutureListener) {
        boolean flag = mChannel != null && isConnect;
        if (flag) {
            mChannel.writeAndFlush(data);
        }
        return flag;
    }

    public boolean sendMsgToServer(String data, OnReceiveListener listener) {
        boolean flag = mChannel != null && isConnect;
        if (flag) {
            mChannel.writeAndFlush(data);
        }
        return flag;
    }

    public boolean getConnectStatus() {
        return isConnect;
    }

    public void setConnectStatus(boolean status) {
        this.isConnect = status;
    }

    public void setListener(ChannelListener listener) {
        this.listener = listener;
    }

    public void setsendBackListener(SendBack sendBack) {
        this.sendBack = sendBack;
    }

    //断开tcp链接
    public void stopTcp() {
        if(mChannel!=null){
            mChannel.disconnect();
            mChannel.close();
            mChannel = null;
            isConnect = false;
        }
        if(group!=null){
            group.shutdownGracefully();
        }
    }
    //设置心跳
    public void setHeart(int allIdleTime) {
       if(nettyClientInitializer!=null){
           nettyClientInitializer.getPipeline().remove("heart");
           nettyClientInitializer.getPipeline().addAfter("buff","heart",new IdleStateHandler(0, 0,  allIdleTime * 60, TimeUnit.SECONDS));
       }

    }
}
