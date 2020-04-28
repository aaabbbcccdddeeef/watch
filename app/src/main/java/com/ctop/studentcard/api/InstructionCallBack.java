package com.ctop.studentcard.api;

/**
 * 指令接口
 */
public interface InstructionCallBack {

    //终端设备服务调用
    void operateTerminal(String downData, Iback iback);
}
