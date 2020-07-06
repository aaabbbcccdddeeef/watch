package com.wisdomin.studentcard.feature.step;


import com.smart.cc.stepcounter.PEDOMETERALG;

public class StepUtils {


    public static int getStep(){
        PEDOMETERALG.gsensorOpen();
        int step = PEDOMETERALG.getGsensorSteps();
        PEDOMETERALG.gsensorClose();

        return step;
    }


    public static void clearStep(){
        PEDOMETERALG.gsensorOpen();
        PEDOMETERALG.clearGsensorSteps();
        PEDOMETERALG.gsensorClose();

    }
}
