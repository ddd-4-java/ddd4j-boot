package io.ddd4j.boot.sample.order.domain.model.vo;

/**
 * 地址值对象
 */
public record Address(String province, String city, String district, String detail, String zipCode) {

    public Address {
        if (province == null || province.trim().isEmpty()) {
            throw new IllegalArgumentException("省份不能为空");
        }
        if (city == null || city.trim().isEmpty()) {
            throw new IllegalArgumentException("城市不能为空");
        }
    }

    public String getFullAddress() {
        return String.format("%s%s%s%s", province, city, district != null ? district : "", detail != null ? detail : "");
    }

}

