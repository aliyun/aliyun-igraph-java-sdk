package com.aliyun.igraph.client.core.model;

import java.io.Serializable;

/**
 * @author alibaba
 */
public abstract class BaseResult implements Serializable {
    private static final long serialVersionUID = 1L;
    protected String traceInfo;
    protected ControlInfo controlInfo;

    public boolean empty() { return 0 == size(); }

    public void setTraceInfo(String traceInfo) { this.traceInfo = traceInfo; }

    @Deprecated
    public String getTraceInfo() { return traceInfo; }

    public ControlInfo getControlInfo() { return controlInfo; }

    public void setControlInfo(ControlInfo controlInfo) { this.controlInfo = controlInfo; }

    public int getMulticallWeight() {
        if (controlInfo == null) {
            return 100;
        }
        return controlInfo.getMulticallWeight();
    }

    public boolean containHotKey() {
        if (controlInfo == null) {
            return false;
        }
        return controlInfo.getContainHotKey();
    }

    /**
     * 获取结果集个数
     *
     * @return int
     */
    public abstract int size();
}
