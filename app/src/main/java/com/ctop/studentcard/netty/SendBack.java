package com.ctop.studentcard.netty;

import com.ctop.studentcard.api.Iback;
import com.ctop.studentcard.api.InstructionCallBack;

public class SendBack {

    InstructionCallBack instructionCallBack;

    public void getSend(String data, Iback iback){
        instructionCallBack.operateTerminal(data,iback);
    }
//
    public SendBack(InstructionCallBack instructionCallBack) {
        this.instructionCallBack = instructionCallBack;
    }
//
}
