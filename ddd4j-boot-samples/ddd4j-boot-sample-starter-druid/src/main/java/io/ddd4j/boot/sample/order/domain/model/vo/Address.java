package io.ddd4j.boot.sample.order.domain.model.vo;

import lombok.Data;

/**
 * 地址值对象
 */
@Data
public class Address {

    String province;
    String city;
    String district;
    String detail;
    String zipCode;
    /**
     * 构造函数
     *
     * @param province 省份
     * @param city 城市
     * @param district 区县
     * @param detail 详细地址
     * @param zipCode 邮编
     */
    public Address(String province, String city, String district, String detail, String zipCode) {
        if (province == null || province.trim().isEmpty()) {
            throw new IllegalArgumentException("省份不能为空");
        }
        if (city == null || city.trim().isEmpty()) {
            throw new IllegalArgumentException("城市不能为空");
        }
        this.province = province;
        this.city = city;
        this.district = district;
        this.detail = detail;
        this.zipCode = zipCode;
    }

    public String getFullAddress() {
        return String.format("%s%s%s%s", province, city, district != null ? district : "", detail != null ? detail : "");
    }

}

