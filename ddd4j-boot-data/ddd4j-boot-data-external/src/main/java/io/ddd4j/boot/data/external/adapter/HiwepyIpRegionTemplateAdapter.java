package io.ddd4j.boot.data.external.adapter;

import io.github.easy4j.ip2region.spring.boot.IP2regionTemplate;
import io.ddd4j.data.external.region.IpRegionTemplate;
import io.ddd4j.data.external.region.RegionAddress;
import io.ddd4j.data.external.region.RegionEnum;
import io.ddd4j.data.external.region.XdbSearcher;

import java.util.Objects;

/**
 * Adapts the Boot-only hiwepy ip2region starter to the ddd4j core abstraction.
 */
public class HiwepyIpRegionTemplateAdapter implements IpRegionTemplate {

    private final IP2regionTemplate delegate;

    public HiwepyIpRegionTemplateAdapter(IP2regionTemplate delegate) {
        this.delegate = Objects.requireNonNull(delegate, "delegate must not be null");
    }

    @Override
    public String getRegion(String ip) {
        return delegate.getRegion(ip);
    }

    @Override
    public RegionAddress getRegionAddress(String ip) {
        io.github.easy4j.ip2region.spring.boot.ext.RegionAddress address = delegate.getRegionAddress(ip);
        if (Objects.isNull(address)) {
            return XdbSearcher.NOT_MATCH_REGION_ADDRESS;
        }
        return new RegionAddress(address.getCountry(), address.getProvince(), address.getCity(),
                address.getArea(), address.getISP());
    }

    @Override
    public RegionEnum getRegionByIp(String ip) {
        io.github.easy4j.ip2region.spring.boot.ext.RegionEnum region = delegate.getRegionByIp(ip);
        if (Objects.isNull(region)) {
            return RegionEnum.UK;
        }
        return RegionEnum.getByCode2(region.getCode2());
    }
}
