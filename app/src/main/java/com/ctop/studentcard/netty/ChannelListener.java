package com.ctop.studentcard.netty;

/**
 *
 * Created by dgl on 11/23/2019.
 *  通道的监听
 */
public interface ChannelListener {

    byte STATUS_CONNECT_SUCCESS = 1;

    byte STATUS_CONNECT_CLOSED = 0;

    byte STATUS_CONNECT_ERROR = 0;

    /**
     * 当接收到系统消息
     */
    void onMessageResponse(String msg);

    /**
     * 当服务状态发生变化时触发
     */
    void onServiceStatusConnectChanged(int statusCode);
}
