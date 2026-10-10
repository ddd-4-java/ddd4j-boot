package io.ddd4j.boot.sample.order.domain.model.vo;

/**
 * 地址值对象
 *
 * @param province 省份
 * @param city 城市
 * @param district 区县
 * @param detail 详细地址
 * @param zipCode 邮编
 *
 */
public record Address(String province, String city, String district, String detail, String zipCode) {

    /**
     * 构造地址值对象时校验必填项。
     */
    public Address {
        if (province == null || province.trim().isEmpty()) {
            throw new IllegalArgumentException("省份不能为空");
        }
        if (city == null || city.trim().isEmpty()) {
            throw new IllegalArgumentException("城市不能为空");
        }
    }

    /**
     * 获取FullAddress。
     *
     * @return FullAddress
     */
    public String getFullAddress() {
        return String.format("%s%s%s%s", province, city, district != null ? district : "", detail != null ? detail : "");
    }

}

