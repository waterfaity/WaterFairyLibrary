package com.waterfairy.library.regionselect;


/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2020/7/26 17:01
 * @info:
 */
public class RegionBean implements PickerView.DataInt {


    public RegionBean(String id, String regionName) {
        this.id = id;
        this.regionName = regionName;
    }

    /**
     * id : 110105
     * regionName : 朝阳区
     */

    private String id;
    private String regionName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    @Override
    public String getContent() {
        return regionName;
    }
}
