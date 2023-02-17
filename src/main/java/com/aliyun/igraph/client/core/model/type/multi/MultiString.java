package com.aliyun.igraph.client.core.model.type.multi;

import java.util.List;

/**
 * @author alibaba
 */
public class MultiString extends MultiValue<String>{
    List<String> value;

    public MultiString(List<String> value) {
        this.value = value;
    }

    @Override
    public List<String> getValue() {
        return value;
    }
}
