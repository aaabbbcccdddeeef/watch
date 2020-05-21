package com.ctop.studentcard.feature.step;

import com.ctop.studentcard.jni.NDKTools;

public class StepUtils {


    public static int getStep(){

        NDKTools.gsensorOpen();
        int step = NDKTools.getGsensorSteps();
        NDKTools.gsensorClose();

        return step;
    }
}
