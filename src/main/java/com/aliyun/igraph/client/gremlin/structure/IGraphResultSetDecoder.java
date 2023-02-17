package com.aliyun.igraph.client.gremlin.structure;

import com.aliyun.igraph.client.proto.gremlin_fb.*;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author alibaba
 */
public class IGraphResultSetDecoder extends IGraphResultSet {
    private static final Logger log = LoggerFactory.getLogger(IGraphResultSetDecoder.class);

    private Set<String> noRecordsAllocator = new HashSet<String>();

    public IGraphResultSetDecoder() {
        super();
    }

    public void decodeFBByColumn(@NonNull Result result) {
        docDataMap = new HashMap<String, MatchRecords>();
        elementMetaMap = new HashMap<Long, ElementMeta>();
        errorCodes = new ArrayList<>();

        if (null != result.errorInfos()) {
            int errorInfoLength = result.errorInfos().errorInfoLength();
            if (errorInfoLength > 0) {
                hasError = true;
                StringBuilder errorString = new StringBuilder();
                errorString.append("error_size:").append(errorInfoLength).append('{');
                for (int i = 0; i < errorInfoLength; ++i) {
                    ErrorInfo errorInfo = result.errorInfos().errorInfo(i);
                    errorString.append("code:").append(errorInfo.errorCode());
                    errorCodes.add(errorInfo.errorCode());
                    errorString.append(", msg:").append(errorInfo.errorMessage()).append(';');
                }
                errorString.append('}');
                errorMsg = errorString.toString();
            }
        }
        traceInfo = result.traceInfo();
        // prepare doc data
        int docDataLength = result.docDataLength();
        for (int i = 0; i < docDataLength; ++i) {
            DocData docData = result.docData(i);
            if (null != docData.records()) {
                docDataMap.put(docData.allocatorName(), docData.records());
            } else {
                noRecordsAllocator.add(docData.allocatorName());
            }
        }
        // prepare meta
        int elementMetaLength = result.elementMetaLength();
        for (int i = 0; i < elementMetaLength; ++i) {
            com.aliyun.igraph.client.proto.gremlin_fb.ElementMeta fbElementMeta = result.elementMeta(i);
            ElementMeta elementMeta = new ElementMeta();
            elementMeta.metaId = fbElementMeta.metaId();
            elementMeta.allocatorName = fbElementMeta.allocatorName();
            elementMeta.label = fbElementMeta.label();
            Long elementMetaType = fbElementMeta.type();

            if (elementMetaType.equals(ElementMetaType.ENTITY.toLong())) {
                elementMeta.elementMetaType = ElementMetaType.ENTITY;
            } else if (elementMetaType.equals(ElementMetaType.PROPERTY.toLong())) {
                elementMeta.elementMetaType = ElementMetaType.PROPERTY;
            }
            // matchRecords
            MatchRecords matchRecords = docDataMap.get(elementMeta.allocatorName);
            if (null == matchRecords) {
                if (noRecordsAllocator.contains(elementMeta.allocatorName)) {
                    continue;
                } else {
                    return;
                }
            }
            elementMeta.matchRecords = matchRecords;
            // propertyNames
            int propertyNamesLength = fbElementMeta.propertyNamesLength();
            for (int j = 0; j < propertyNamesLength; ++j) {
                elementMeta.propertyNames.add(fbElementMeta.propertyNames(j));
            }
            // prepare fieldName, 从HashMap中取出来的elementMeta.matchRecords肯定不为空
            int fieldNameLength = elementMeta.matchRecords.fieldNameLength();
            for (int j = 0; j < fieldNameLength; ++j) {
                String fieldName = elementMeta.matchRecords.fieldName(j);
                elementMeta.fieldNames.add(fieldName);
                elementMeta.fieldName2index.put(fieldName, j);
                FieldValueColumnTable column = elementMeta.matchRecords.recordColumns(j);
                byte type = column.fieldValueColumnType();
                FieldType fieldType = FieldType.convertFieldType(type);
                elementMeta.fieldTypes.add(fieldType);
                elementMeta.fieldName2Type.put(fieldName, type);
            }
            elementMetaMap.put(elementMeta.metaId, elementMeta);
        }
        // prepare resultObject
        GremlinObject gremlinObject = result.objectResult();
        if (null == gremlinObject) {
            return;
        }
        IGraphResult rootObject = new IGraphResult();
        rootObject.resource.setGremlinObject(gremlinObject);
        rootObject.resource.setGremlinSingleResult(this);
        if (IGraphResultObjectType.LIST != rootObject.getObjectType()) {
            log.warn("root object type [" + rootObject.getObjectType().toString() + "] is invalid");
            return;
        }
        resultObjects = rootObject.get(List.class);
        completeFuture.complete(true);
    }
}
