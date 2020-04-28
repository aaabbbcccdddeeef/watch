package com.ctop.studentcard.bean;

/**
 * 情景模式
 */
public class ContextualModel {
    /**
     * silence :
     * ring :
     * inBound :
     * outBound :
     */

    private String silence;
    private String ring;
    private String inBound;
    private String outBound;

    public String getSilence() {
        return silence;
    }

    public void setSilence(String silence) {
        this.silence = silence;
    }

    public String getRing() {
        return ring;
    }

    public void setRing(String ring) {
        this.ring = ring;
    }

    public String getInBound() {
        return inBound;
    }

    public void setInBound(String inBound) {
        this.inBound = inBound;
    }

    public String getOutBound() {
        return outBound;
    }

    public void setOutBound(String outBound) {
        this.outBound = outBound;
    }


    /**
     * {"silence":"","ring":"","inBound":"","outBound":""}
     *
     *   String silence = strings[0];
     *                    String ring = strings[0];
     *                    String inBound = strings[0];
     *                    String outBound = strings[0];
     */


    public static ContextualModel parseJson(String json){
        String[] bean = json.split("@");
        ContextualModel contextualModel = new ContextualModel();
        contextualModel.setSilence(bean[0]);
        contextualModel.setRing(bean[1]);
        contextualModel.setInBound(bean[2]);
        contextualModel.setOutBound(bean[3]);
        return contextualModel;
    }


}
