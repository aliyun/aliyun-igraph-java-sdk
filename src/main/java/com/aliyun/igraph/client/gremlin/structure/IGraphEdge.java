package com.aliyun.igraph.client.gremlin.structure;

import com.google.gson.JsonObject;
import lombok.NonNull;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Property;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import javax.ws.rs.NotSupportedException;
import javax.xml.bind.util.JAXBSource;

import java.util.*;

/**
 * @author alibaba
 */
public class IGraphEdge extends IGraphElement implements Edge {
    public IGraphEdge(@NonNull ElementMeta elementMeta, int cursor) {
        super(elementMeta, cursor);
    }

    @Override
    public Iterator<Vertex> vertices(Direction direction) {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public Vertex outVertex() {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public Vertex inVertex() {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public Iterator<Vertex> bothVertices() {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public <V> Property<V> property(String key) {
        V value = (V)getObject(key);
        return new IGraphProperty<>(this, key, value);
    }

    @Override
    public void remove() {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public <V> Iterator<Property<V>> properties(String... propertyKeys) {
        List<Property<V>> propertyList = new ArrayList<>();
        for (String propertyKey : propertyKeys) {
            propertyList.add(property(propertyKey));
        }
        return propertyList.iterator();
    }

    @Override
    public Object toJson () {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("label", label());
        List<String> fieldNames = getFieldNames();
        List<FieldType> fieldTypes = getFieldTypes();
        for (int i = 0; i < fieldNames.size(); ++i) {
            String fieldName = fieldNames.get(i);
            FieldType type = fieldTypes.get(i);
            addToJsonObject(jsonObject, fieldName, type);
        }
        return jsonObject;
    }
}
