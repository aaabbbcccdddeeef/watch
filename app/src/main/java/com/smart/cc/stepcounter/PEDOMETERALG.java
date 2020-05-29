package com.smart.cc.stepcounter;

public class PEDOMETERALG {

    public native static int gsensorOpen();
    public native static int getGsensorSteps();
    public native static int clearGsensorSteps();
    public native static int gsensorClose();

    static{
        System.loadLibrary("pedometer");
    }

}
