package com.aliyun.igraph.client.pg;

import java.io.Serializable;
import java.util.List;

import com.aliyun.igraph.client.core.model.ControlInfo;

/**
 * @author alibaba
 */
public class QueryResult implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<SingleQueryResult> results;
    protected String traceInfo;
    private ControlInfo controlInfo;

    /**
     * 查询只有一个query时，调用此方法
     * @return 对应的唯一一个SingleQueryResult，个数不匹配返回null
     */
    public SingleQueryResult getSingleQueryResult() {
        if (results == null || results.size() != 1) {
            return null;
        }
        return results.get(0);
    }

    /**
     * 查询时有多个query时，调用此方法。
     * 
     * @return 多个query，要么所有的query结果按query顺序都返回，要么不返回。
     */
    public List<SingleQueryResult> getAllQueryResult() {
        return results;
    }

    /**
     * 查询特定query对应请求结果
     * 
     * @param index query的index
     * @return query对应的结果
     */
    public SingleQueryResult getQueryResult(int index) {
        if (results == null || index < 0 || index >= results.size()) {
            return null;
        }
        return results.get(index);
    }

    public void setResults(List<SingleQueryResult> results) {
        this.results = results;
    }

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
}
