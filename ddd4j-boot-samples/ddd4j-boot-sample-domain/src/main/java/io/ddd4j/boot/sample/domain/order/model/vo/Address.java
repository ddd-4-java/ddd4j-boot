package io.ddd4j.boot.sample.domain.order.model.vo;

/**
 * 地址值对象
 *
 * <p>领域语义：以不可变 record 表达收货地址，省份与城市必填、
 * 区县与详细地址可缺省；地址只作为值参与比较，不承载身份标识。</p>
 *
 * @param province 省份，不能为空
 * @param city     城市，不能为空
 * @param district 区/县，可为 {@code null}
 * @param detail   详细地址（街道、门牌号等），可为 {@code null}
 * @param zipCode  邮政编码，可为 {@code null}
 */
public record Address(String province, String city, String district, String detail, String zipCode) {

    /**
     * 紧凑构造器，校验地址必填项。
     *
     * @throws IllegalArgumentException 省份或城市为空、纯空白时抛出
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
     * 拼接省市区与详细地址，得到完整地址文本。
     *
     * <p>区县与详细地址为 {@code null} 时按空串处理，缺失层级不会产生「null」字样。</p>
     *
     * @return 省 + 市 + 区 + 详细地址拼接而成的完整地址
     */
    public String getFullAddress() {
        return String.format("%s%s%s%s", province, city, district != null ? district : "", detail != null ? detail : "");
    }

}
