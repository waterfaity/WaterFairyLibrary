package com.waterfairy.widget.baseview;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2018/6/8 13:48
 * @info:
 */
public class Coordinate {
    public int x;
    public int y;
    public int value;
    public String text;

    public Coordinate() {
    }

    public Coordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Coordinate(int x, int y, int value, String text) {
        this.x = x;
        this.value = value;
        this.y = y;
        this.text = text;
    }

}
