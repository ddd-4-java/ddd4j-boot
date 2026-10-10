package io.ddd4j.boot.sample.order.domain.model.vo;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Objects;

/**
 * 地址值对象
 *
 * 组件 province：省份
 * 组件 city：城市
 * 组件 district：区县
 * 组件 detail：详细地址
 * 组件 zipCode：邮编
 *
 */
public final class Address {

    private static final long serialVersionUID = 0L;

    private final String province;

    private final String city;

    private final String district;

    private final String detail;

    private final String zipCode;

    /**
     * 构造地址值对象时校验必填项。
     */
    @JsonCreator()
    public Address(@JsonProperty("province") String province, @JsonProperty("city") String city, @JsonProperty("district") String district, @JsonProperty("detail") String detail, @JsonProperty("zipCode") String zipCode) {
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

    /**
     * 获取FullAddress。
     *
     * @return FullAddress
     */
    public String getFullAddress() {
        return String.format("%s%s%s%s", province, city, district != null ? district : "", detail != null ? detail : "");
    }

    @JsonProperty("province")
    public String province() {
        return province;
    }

    @JsonProperty("city")
    public String city() {
        return city;
    }

    @JsonProperty("district")
    public String district() {
        return district;
    }

    @JsonProperty("detail")
    public String detail() {
        return detail;
    }

    @JsonProperty("zipCode")
    public String zipCode() {
        return zipCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (Objects.isNull(obj) || getClass() != obj.getClass()) {
            return false;
        }
        Address other = (Address) obj;
        return Objects.equals(this.province, other.province) && Objects.equals(this.city, other.city) && Objects.equals(this.district, other.district) && Objects.equals(this.detail, other.detail) && Objects.equals(this.zipCode, other.zipCode);
    }

    @Override
    public int hashCode() {
        int result = 0;
        result = 31 * result + Objects.hashCode(province);
        result = 31 * result + Objects.hashCode(city);
        result = 31 * result + Objects.hashCode(district);
        result = 31 * result + Objects.hashCode(detail);
        result = 31 * result + Objects.hashCode(zipCode);
        return result;
    }

    @Override
    public String toString() {
        return "Address[province=" + province + ", city=" + city + ", district=" + district + ", detail=" + detail + ", zipCode=" + zipCode + "]";
    }
}

