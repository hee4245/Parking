package com.bug.parking.data;

/**
 * Created by json on 15. 8. 16..
 */
public class FloorData {
    private static String[] floors = new String[]{"B5", "B4", "B3", "B2", "B1", "1F", "2F", "3F", "4F", "5F"};

    static public String[] getData() {
        return floors;
    }

    static public String getItem(int index) {
        if (index > -1 && index < floors.length)
            return floors[index];
        else
            return "";
    }
}
