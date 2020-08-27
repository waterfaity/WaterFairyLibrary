package com.waterfairy.library.regionselect;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.HashMap;
import java.util.List;

import androidx.annotation.Nullable;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2020/7/26 17:26
 * @info:
 */
public class RegionSelectView extends LinearLayout {
    //省
    private PickerView<RegionBean> provincePickerView;
    //市/区
    private PickerView<RegionBean> cityPickerView;
    //县
    private PickerView<RegionBean> countyPickerView;

    private HashMap<String, List<RegionBean>> hashMap = new HashMap<>();

    private OnDataQueryListener onDataQueryListener;

    public OnDataQueryListener getOnDataQueryListener() {
        return onDataQueryListener;
    }

    public void setOnDataQueryListener(OnDataQueryListener onDataQueryListener) {
        this.onDataQueryListener = onDataQueryListener;
    }

    public RegionSelectView(Context context) {
        this(context, null);
    }

    public RegionSelectView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public PickerView<RegionBean> getProvincePickerView() {
        return provincePickerView;
    }

    public PickerView<RegionBean> getCityPickerView() {
        return cityPickerView;
    }

    public PickerView<RegionBean> getCountyPickerView() {
        return countyPickerView;
    }

    private void initView(Context context, AttributeSet attrs) {
        provincePickerView = new PickerView<>(context, attrs);
        provincePickerView.setOnSelectListener(new PickerView.onSelectListener<RegionBean>() {
            @Override
            public void onSelect(RegionBean regionBean) {
                String provinceId = regionBean.getId();
                List<RegionBean> regionBeans = hashMap.get(provinceId);
                if (regionBeans == null) {
                    if (onDataQueryListener != null) {
                        onDataQueryListener.onQueryCity(provinceId);
                    }
                } else {
                    cityPickerView.setData(regionBeans);
                    executeAnimator(cityPickerView);
                }
            }
        });
        cityPickerView = new PickerView<>(context, attrs);
        cityPickerView.setOnSelectListener(new PickerView.onSelectListener<RegionBean>() {
            @Override
            public void onSelect(RegionBean regionBean) {
                String cityId = regionBean.getId();
                List<RegionBean> regionBeans = hashMap.get(cityId);
                if (regionBeans == null) {
                    if (onDataQueryListener != null) {
                        onDataQueryListener.onQueryCounty(cityId);
                    }
                } else {
                    countyPickerView.setData(regionBeans);
                    executeAnimator(countyPickerView);

                }
            }
        });
        countyPickerView = new PickerView<>(context, attrs);
        addView(provincePickerView, newLayoutParams(2));
        addView(cityPickerView, newLayoutParams(3));
        addView(countyPickerView, newLayoutParams(3));
    }

    private LayoutParams newLayoutParams(int weight) {
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        layoutParams.width = 0;
        layoutParams.weight = weight;
        return layoutParams;
    }

    /**
     * 省
     *
     * @param regionBeans
     */
    public void setProvinceDataList(List<RegionBean> regionBeans) {
        provincePickerView.setData(regionBeans);
        executeAnimator(provincePickerView);
    }

    /**
     * 市
     *
     * @param provinceId
     * @param regionBeans
     */
    public void setCityDataList(String provinceId, List<RegionBean> regionBeans) {
        hashMap.put(provinceId, regionBeans);
        cityPickerView.setData(regionBeans);
        executeAnimator(cityPickerView);
    }

    /**
     * 县
     *
     * @param cityId
     * @param regionBeans
     */
    public void setCountyDataList(String cityId, List<RegionBean> regionBeans) {
        hashMap.put(cityId, regionBeans);
        countyPickerView.setData(regionBeans);
        executeAnimator(countyPickerView);
    }


    private void executeAnimator(View view) {
        PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat("alpha", 1f, 0f, 1f);
        PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat("scaleX", 1f, 1.3f, 1f);
        PropertyValuesHolder pvhZ = PropertyValuesHolder.ofFloat("scaleY", 1f, 1.3f, 1f);
        ObjectAnimator.ofPropertyValuesHolder(view, pvhX, pvhY, pvhZ).setDuration(200).start();
    }


    public interface OnDataQueryListener {
        /**
         * 查询市
         */
        void onQueryCity(String provinceId);

        /**
         * 查询县
         *
         * @param cityId
         */
        void onQueryCounty(String cityId);
    }
}
