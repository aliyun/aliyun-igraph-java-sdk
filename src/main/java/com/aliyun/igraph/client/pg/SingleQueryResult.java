package com.aliyun.igraph.client.pg;

import java.util.List;
import java.util.Map;
import java.io.Serializable;

import lombok.NonNull;

/**
 * @author alibaba
 */
public class SingleQueryResult implements Serializable {
    private static final long serialVersionUID = 1L;
    protected List<MatchRecord> matchRecords;
    protected List<String> fieldNames;
    protected Map<String, Integer> fieldName2IndexMap;
    protected boolean hasError;
    protected String errorMsg;
    protected List<Map<String, String>> rows;

    /**
     * @return Result中包含错误信息时，返回true
     */
    public boolean hasError() {
        return hasError;
    }

    /** @return 错误信息 **/
    public String getErrorMsg() {
        return errorMsg;
    }

    /** @return 结果的所有字段名 **/
    public List<String> getFieldNames() {
        return fieldNames;
    }

    /**
     * 检索给定字段的在MatchRecord的位置
     *
     * @param fieldName 给定的检索字段名
     * @return 若字段存在，返回字段位置(0 ~ getFieldNames().size()-1)；否则，返回负值
     */
    public int getFieldIndex(@NonNull String fieldName) {
        if (fieldName2IndexMap == null) {
            return -1;
        }
        Integer index = fieldName2IndexMap.get(fieldName);
        if (index == null) {
            return -1;
        }
        return index;
    }

    /**
     * @return 结果集的大小，即MatchRecord的数量
     */
    public int size() {
        if (matchRecords == null) {
            return 0;
        }
        return matchRecords.size();
    }

    /** @return Size() == 0 **/
    public boolean empty() {
        return size() == 0;
    }

    /**
     * 获取结果集中其中一条记录
     *
     * @param index 所要获取记录的序号
     * @return 若记录存在，则返回该记录；否则返回null
     */
    public MatchRecord getMatchRecord(int index) {
        if (matchRecords == null || index < 0 || index >= matchRecords.size()) {
            return null;
        }
        return matchRecords.get(index);
    }

    /**
     * @return 获取结果集中的所有记录
     */
    public List<MatchRecord> getMatchRecords() {
        return matchRecords;
    }

    public void setHasError(boolean hasError) {
        this.hasError = hasError;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
