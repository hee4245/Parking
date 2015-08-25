package com.bug.parking.data;

/**
 * Created by 세희 on 2015-08-25.
 */
public class TimePeriodsData {
    private static String[] data = new String[]{"AM","PM"};

    public static String[] getData(){
        return data;
    }

    public static String getItem(int index){
        return data[index];
    }
}
