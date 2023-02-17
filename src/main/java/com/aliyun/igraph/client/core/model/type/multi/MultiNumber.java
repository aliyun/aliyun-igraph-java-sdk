package com.aliyun.igraph.client.core.model.type.multi;

import java.util.List;

/**
 * @author alibaba
 */
public class MultiNumber<V> extends MultiValue<V> {
    protected List<V> value;

    public MultiNumber(List<V> value) {
        this.value = value;
    }

    @Override
    public List<V> getValue() {
        return value;
    }
}
