package com.aliyun.igraph.client.core.model;

import java.io.Serializable;

/**
 * @author alibaba
 */
public class ControlInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    private int multicallWeight = 100;
    private boolean containHotKey = false;

    public int getMulticallWeight() { return multicallWeight; }

    public void setMulticallWeight(int multicallWeight) {
        this.multicallWeight = multicallWeight;
    }

    public boolean getContainHotKey() { return containHotKey; }

    public void setContainHotKey(boolean containHotKey) {
        this.containHotKey = containHotKey;
    }
}
