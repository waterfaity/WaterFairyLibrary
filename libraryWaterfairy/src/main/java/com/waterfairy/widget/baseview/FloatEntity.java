package com.waterfairy.widget.baseview;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2018/6/15 09:27
 * @info:
 */
public class FloatEntity {
    public float value;
    public String valueStr;
    public String name;

    public FloatEntity(float value) {
        this.value = value;
    }

    public FloatEntity() {
    }

    public FloatEntity(float value, String name) {
        this.value = value;
        this.name = name;
    }

    public FloatEntity(float value, String valueStr, String name) {
        this.valueStr = valueStr;
        this.value = value;
        this.name = name;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
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

    public float getABSValue() {
        return Math.abs(value);
    }
}
