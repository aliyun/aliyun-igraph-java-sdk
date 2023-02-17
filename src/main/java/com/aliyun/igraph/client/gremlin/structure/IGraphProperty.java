package com.aliyun.igraph.client.gremlin.structure;

import lombok.NonNull;
import org.apache.tinkerpop.gremlin.structure.Element;
import org.apache.tinkerpop.gremlin.structure.Property;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.NotSupportedException;
import java.nio.charset.Charset;
import java.util.NoSuchElementException;

/**
 * @author alibaba
 */
public class IGraphProperty<V> extends IGraphElement implements Property {
    public static final Charset utf_8 = Charset.forName("utf-8");
    private static final Logger log = LoggerFactory.getLogger(IGraphElement.class);

    private String key = null;
    private Integer fieldIndex = null;
    private FieldType fieldType = null;
    protected V value;
    protected final IGraphElement IGraphElement;

    public IGraphProperty(@NonNull ElementMeta elementMeta, int cursor) {
        super(elementMeta, cursor);
        preparePropertyMeta();
        this.value = (V)getObject();
        this.IGraphElement = null;
    }

    public IGraphProperty(IGraphElement IGraphElement, String key, V value) {
        this.IGraphElement = IGraphElement;
        this.key = key;
        this.value = value;
    }

    private Object getObject() { return (fieldIndex == null ? null : super.getObejct(fieldIndex)); }

    @Override
    public String toString() {
        StringBuilder ss = new StringBuilder(128);
        if (null == key) {
            log.warn("get property's key failed when generate json");
            return null;
        }
        if (null == fieldType) {
            log.warn("get property's type failed when generate json");
            return null;
        }
        ss.append("{");
        addToStringObject(ss, key, fieldType);
        ss.setLength(ss.length() - 1);
        ss.append("}");
        return ss.toString();
    }

    private void preparePropertyMeta() {
        if (ElementMetaType.PROPERTY != elementMeta.elementMetaType) {
            log.warn("element's type is not 'Property' type, is {}", elementMeta.elementMetaType);
            return;
        }
        if (1 != elementMeta.propertyNames.size()) {
            log.warn(String.format("The size of propertyName in elementMeta is not equal to 1, is [%d]", elementMeta.propertyNames.size()));
            return;
        }
        key = elementMeta.propertyNames.iterator().next();

        Byte type = elementMeta.fieldName2Type.get(key);
        if (null == type) {
            log.warn(String.format("can't find key[%s]'s type in elementMeta", key));
            return;
        }
        fieldType = FieldType.convertFieldType(type);

        fieldIndex = elementMeta.fieldName2index.get(key);
        if (null == fieldIndex) {
            log.warn(String.format("can't find key[%s]'s index in elementMeta", key));
            return;
        }
    }

    @Override
    public String key() {
        return key;
    }

    @Override
    public <V> Property<V> property(String key) {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public V value() throws NoSuchElementException {
        return value;
    }

    @Override
    public boolean isPresent() {
        return value != null;
    }

    @Override
    public Element element() {
        if (IGraphElement == null) {
            throw new NotSupportedException("method is not supported");
        }
        return IGraphElement;
    }

    @Override
    public void remove() {
        throw new NotSupportedException("method is not supported");
    }
}
