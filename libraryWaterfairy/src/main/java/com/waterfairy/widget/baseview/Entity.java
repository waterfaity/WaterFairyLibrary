package com.waterfairy.widget.baseview;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2018/6/15 09:27
 * @info:
 */
public class Entity {
    public int value;
    public String name;
    public String valueStr;
    public Object object;

    public Entity setTag(Object object) {
        this.object = object;
        return this;
    }

    public Object getTag() {
        return object;
    }

    public Entity() {
    }

    public Entity(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public int getABSValue() {
        return Math.abs(value);
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValueStr() {
        return valueStr;
    }

    public void setValueStr(String valueStr) {
        this.valueStr = valueStr;
    }
}
