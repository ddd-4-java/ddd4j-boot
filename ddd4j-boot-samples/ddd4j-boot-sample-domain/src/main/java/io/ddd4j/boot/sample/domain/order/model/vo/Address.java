package io.ddd4j.boot.sample.domain.order.model.vo;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * 地址值对象
 */
@Getter
@EqualsAndHashCode
public class Address {
    
    private final String province;
    private final String city;
    private final String district;
    private final String detail;
    private final String zipCode;
    
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

