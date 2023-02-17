package com.aliyun.igraph.client.gremlin.structure;

import com.aliyun.igraph.client.proto.gremlin_fb.MatchRecords;

import java.util.*;

/**
 * @author alibaba
 */
public class ElementMeta {
    public Long metaId;
    public String allocatorName;
    public String label;
    public ElementMetaType elementMetaType;
    public Set<String> propertyNames = new HashSet<String>();
    public List<String> fieldNames = new ArrayList<String>();
    public List<FieldType> fieldTypes = new ArrayList<FieldType>();
    public Map<String, Integer> fieldName2index = new HashMap<String, Integer>();
    public Map<String, Byte> fieldName2Type = new HashMap<String, Byte>();
    public MatchRecords matchRecords;
}
