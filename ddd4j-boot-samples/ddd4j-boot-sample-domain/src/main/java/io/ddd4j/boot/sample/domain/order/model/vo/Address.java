package io.ddd4j.boot.sample.domain.order.model.vo;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Objects;

/**
 * 地址值对象
 *
 * <p>领域语义：以不可变 record 表达收货地址，省份与城市必填、
 * 区县与详细地址可缺省；地址只作为值参与比较，不承载身份标识。</p>
 *
 * 组件 province：省份，不能为空
 * 组件 city：城市，不能为空
 * 组件 district：区/县，可为 {@code null}
 * 组件 detail：详细地址（街道、门牌号等），可为 {@code null}
 * 组件 zipCode：邮政编码，可为 {@code null}
 */
public final class Address {

    private static final long serialVersionUID = 0L;

    private final String province;

    private final String city;

    private final String district;

    private final String detail;

    private final String zipCode;

    /**
     * 紧凑构造器，校验地址必填项。
     *
     * @throws IllegalArgumentException 省份或城市为空、纯空白时抛出
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
     * 拼接省市区与详细地址，得到完整地址文本。
     *
     * <p>区县与详细地址为 {@code null} 时按空串处理，缺失层级不会产生「null」字样。</p>
     *
     * @return 省 + 市 + 区 + 详细地址拼接而成的完整地址
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
