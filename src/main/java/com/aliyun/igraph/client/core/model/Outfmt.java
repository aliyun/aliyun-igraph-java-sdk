package com.aliyun.igraph.client.core.model;

/**
 * @author alibaba
 */
public enum Outfmt {
    /**
     * out format type
     * fb2: flatbuffer with compress
     * fb2nz: flatbuffer without compress
     */
    FBBYCOLUMN("fb2"), FBBYCOLUMNNZ("fb2nz"), UNKNOWN(null);
    String value;

    Outfmt(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
