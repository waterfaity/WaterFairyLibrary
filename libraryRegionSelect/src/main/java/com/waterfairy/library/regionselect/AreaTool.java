package com.waterfairy.library.regionselect;


import java.util.ArrayList;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2020/7/26 17:01
 * @info:
 */
public class AreaTool {
    public static ArrayList<RegionBean> provinceList = new ArrayList<>();

    static {
        if (provinceList.size() == 0) {
            provinceList.add(new RegionBean("110000", "北京"));
            provinceList.add(new RegionBean("120000", "天津"));
            provinceList.add(new RegionBean("130000", "河北省"));
            provinceList.add(new RegionBean("140000", "山西省"));
            provinceList.add(new RegionBean("150000", "内蒙古"));
            provinceList.add(new RegionBean("210000", "辽宁省"));
            provinceList.add(new RegionBean("220000", "吉林省"));
            provinceList.add(new RegionBean("230000", "黑龙江"));
            provinceList.add(new RegionBean("310000", "上海"));
            provinceList.add(new RegionBean("320000", "江苏省"));
            provinceList.add(new RegionBean("330000", "浙江省"));
            provinceList.add(new RegionBean("340000", "安徽省"));
            provinceList.add(new RegionBean("350000", "福建省"));
            provinceList.add(new RegionBean("360000", "江西省"));
            provinceList.add(new RegionBean("370000", "山东省"));
            provinceList.add(new RegionBean("410000", "河南省"));
            provinceList.add(new RegionBean("420000", "湖北省"));
            provinceList.add(new RegionBean("430000", "湖南省"));
            provinceList.add(new RegionBean("440000", "广东省"));
            provinceList.add(new RegionBean("450000", "广西"));
            provinceList.add(new RegionBean("460000", "海南省"));
            provinceList.add(new RegionBean("500000", "重庆"));
            provinceList.add(new RegionBean("510000", "四川省"));
            provinceList.add(new RegionBean("520000", "贵州省"));
            provinceList.add(new RegionBean("530000", "云南省"));
            provinceList.add(new RegionBean("540000", "西藏"));
            provinceList.add(new RegionBean("610000", "陕西省"));
            provinceList.add(new RegionBean("620000", "甘肃省"));
            provinceList.add(new RegionBean("630000", "青海省"));
            provinceList.add(new RegionBean("640000", "宁夏"));
            provinceList.add(new RegionBean("650000", "新疆"));
            provinceList.add(new RegionBean("710000", "台湾省"));
            provinceList.add(new RegionBean("810000", "香港"));
            provinceList.add(new RegionBean("820000", "澳门"));
        }
    }
}
