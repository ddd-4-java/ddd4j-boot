package io.ddd4j.boot.sample.order.domain.model.vo;

import lombok.Value;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

/**
 * 地址值对象
 */
@Value
@Accessors(fluent = true)
public class Address {

    String province;
    String city;
    String district;
    String detail;
    String zipCode;

    public Address(String province, String city, String district, String detail, String zipCode) {
        if (StringUtils.isBlank(province)) {
            throw new IllegalArgumentException("省份不能为空");
        }
        if (StringUtils.isBlank(city)) {
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
