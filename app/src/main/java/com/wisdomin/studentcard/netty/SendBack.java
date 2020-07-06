package com.wisdomin.studentcard.netty;

import com.wisdomin.studentcard.api.Iback;
import com.wisdomin.studentcard.api.InstructionCallBack;

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
