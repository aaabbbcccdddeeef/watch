package com.ctop.studentcard.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * 情景模式
 */
public class TemFrequency {

    private List<Frequency> items;

    public List<Frequency> getItems() {
        return items;
    }

    public void setItems(List<Frequency> items) {
        this.items = items;
    }

    public static class Frequency{
        private String day;
        private ArrayList<String> times;

        public String getDay() {
            return day;
        }

        public void setDay(String day) {
            this.day = day;
        }

        public ArrayList<String> getTimes() {
            return times;
        }

        public void setTimes(ArrayList<String> times) {
            this.times = times;
        }
    }

    /**
     * 1=0900!1200#2=1200
     */
    public static TemFrequency parseJson(String json){
        TemFrequency temFrequency = new TemFrequency();
        if(json.equals("0")){
            temFrequency = null;
            return temFrequency;
        }
        String[] days = json.split("#");

        List<Frequency> items = new ArrayList<>();
        for(int i = 0;i<days.length;i++){
            Frequency frequency = new Frequency();
            frequency.setDay(days[i].split("=")[0]);
            String timesall = days[i].split("=")[1];
            String[] times = timesall.split("!");
            ArrayList<String> timesList = new ArrayList<>();
            for(int j = 0;j<times.length;j++){
                timesList.add(times[j]);
            }
            frequency.setTimes(timesList);

            items.add(frequency);
        }
        temFrequency.setItems(items);
        return temFrequency;
    }


}
