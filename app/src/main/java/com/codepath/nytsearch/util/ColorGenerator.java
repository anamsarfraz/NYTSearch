package com.codepath.nytsearch.util;

import android.graphics.Color;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by usarfraz on 3/19/17.
 */

public class ColorGenerator {
    private static Map<String, Integer> categoryColorMap = new HashMap<>();

    private static int getRandomColor(){
        Random rnd = new Random();
        return Color.argb(255, rnd.nextInt(254), rnd.nextInt(254), rnd.nextInt(254));
    }

    public static Integer getColor(String category) {
        if (!categoryColorMap.containsKey(category)) {
            categoryColorMap.put(category, getRandomColor());
        }

        return categoryColorMap.get(category);
    }
}
