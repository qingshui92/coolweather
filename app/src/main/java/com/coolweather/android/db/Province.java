package com.coolweather.android.db;

import org.litepal.crud.DataSupport;

/**
 * Created by danshui on 2017/11/21.
 */

public class Province extends DataSupport {
    private int id;
    private String provinceCode;
    private String provinceName;

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
