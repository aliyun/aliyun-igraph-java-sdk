package com.aliyun.igraph.client.gremlin.gremlin_api;

import com.aliyun.igraph.client.core.model.SetUpdateQueryHelper;
import com.aliyun.igraph.client.exception.IGraphQueryException;
import com.aliyun.igraph.client.exception.IGraphServerException;
import com.aliyun.igraph.client.pg.KeyList;
import com.aliyun.igraph.client.exception.IGraphClientException;
import com.aliyun.igraph.client.core.model.UpdateQuery;
import lombok.NonNull;
import org.apache.tinkerpop.gremlin.process.traversal.Traversal;
import org.apache.tinkerpop.gremlin.structure.*;
import org.apache.tinkerpop.gremlin.structure.VertexProperty.Cardinality;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import javax.ws.rs.NotSupportedException;

/**
 * @author alibaba
 */
public class GraphTraversal implements org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal {
    private StringBuilder ss = new StringBuilder(2048);
    private boolean isBegin = true;
    private boolean isSearch = false;
    private int keyStart = 0;
    private int keyEnd = 0;
    private List<String> keys;
    private List<String> labels;
    private String tableName;
    private GremlinQueryType gremlinQueryType = GremlinQueryType.SEARCH;
    private UpdateQuery updateQuery;

    // only used for __
    public GraphTraversal() {
        ss.append("__.");
    }

    public GraphTraversal(@NonNull String tableName) {
        this.gremlinQueryType = GremlinQueryType.UPDATE;
        this.tableName = tableName;
    }

    public GraphTraversal(GraphTraversalSource graphTraversalSource, int keyStart, int keyEnd) {
        ss.append(graphTraversalSource);
        isBegin = false;
        this.keyStart = keyStart;
        this.keyEnd = keyEnd;
    }
    public GraphTraversal(GraphTraversalSource graphTraversalSource, int keyStart, int keyEnd, String tableName, List<String> keys, List<String> labels) {
        ss.append(graphTraversalSource);
        isBegin = false;
        this.keyStart = keyStart;
        this.keyEnd = keyEnd;
        this.tableName = tableName;
        this.keys = keys;
        this.labels = labels;
    }

    public GremlinQueryType getGremlinQueryType() { return gremlinQueryType; }

    public UpdateQuery getUpdateQuery() { return updateQuery; }

    private void stepBegin(final String stepName) {
        if (gremlinQueryType != GremlinQueryType.SEARCH) {
            throw new IGraphQueryException("complex step is not supported in update query!");
        }
        if (isBegin) {
            isBegin = false;
        } else {
            ss.append('.');
        }
        isSearch = true;
        ss.append(stepName).append('(');
    }

    private GraphTraversal stepEnd() {
        ss.append(')');
        return this;
    }

    public void setKeys(String... newKeys) {
        if (keyStart == 0 || keyEnd == 0) {
            throw new IGraphClientException("no V or E step");
        }
        StringBuilder keyBuilder = new StringBuilder(1024);
        StringBuilderHelper.appendMultiString(keyBuilder, false, newKeys);
        ss.replace(keyStart, keyEnd, keyBuilder.toString());
        keyEnd = keyStart + keyBuilder.length();
    }

    public void setKeys(@NonNull List<KeyList> keyLists) {
        if (keyStart == 0 || keyEnd == 0) {
            throw new IGraphClientException("no V or E step");
        }
        StringBuilder keyBuilder = new StringBuilder(1024);
        StringBuilderHelper.appendParameter(keyBuilder, false, keyLists);
        ss.replace(keyStart, keyEnd, keyBuilder.toString());
        keyEnd = keyStart + keyBuilder.length();
    }

    @Deprecated
    public String getKeys() {
        return ss.substring(keyStart, keyEnd);
    }

    @Override
    public String toString() {
        return ss.toString();
    }

    @Override
    public Admin asAdmin() {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public Optional tryNext() {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public List next(int amount) {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public Set toSet() {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public Stream toStream() {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public CompletableFuture promise(Function traversalFunction) {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public Collection fill(Collection collection) {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public void forEachRemaining(Class endType, Consumer consumer) {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public void forEachRemaining(Consumer action) {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public void close() throws Exception {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public boolean hasNext() {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public Object next() {
        throw new NotSupportedException("method is not supported");
    }

    //////////////////////  QUERY STEPS  //////////////////////////
    public GraphTraversal indexQuery(final String queryFeature) {
        stepBegin("indexQuery");
        StringBuilderHelper.appendParameter(ss, false, queryFeature);
        return stepEnd();
    }

    //////////////////////  MAP STEPS  //////////////////////////

    // public GraphTraversal map(Function);
    @Override
    public GraphTraversal map(final Traversal mapTraversal) {
        stepBegin("map");
        StringBuilderHelper.appendParameter(ss, false, mapTraversal);
        return stepEnd();
    }

    public GraphTraversal map(final String expression) {
        stepBegin("map");
        StringBuilderHelper.appendParameter(ss, false, expression);
        return stepEnd();
    }

    // public GraphTraversal flatMap(Function);
    @Override
    public GraphTraversal flatMap(final Traversal flatMapTraversal) {
        stepBegin("flatMap");
        StringBuilderHelper.appendParameter(ss, false, flatMapTraversal);
        return stepEnd();
    }

    @Override
    public GraphTraversal id() {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public GraphTraversal label() {
        stepBegin("label");
        return stepEnd();
    }

    @Override
    public GraphTraversal identity() {
        stepBegin("identity");
        return stepEnd();
    }

    @Override
    public GraphTraversal constant(final Object e) {
        stepBegin("constant");
        if (e instanceof String) {
            StringBuilderHelper.appendParameter(ss, false, (String)e);
        } else {
            StringBuilderHelper.appendParameter(ss, false, e);
        }
        return stepEnd();
    }

    @Override
    public GraphTraversal V(Object... vertexIdsOrElements) {
        if (vertexIdsOrElements.length != 0  && !(vertexIdsOrElements[0] instanceof String)) {
            throw new IGraphServerException("type [" + vertexIdsOrElements[0].getClass() + "]in V step is not supported");
        }
        String[] keys = new String[vertexIdsOrElements.length];
        for (int i = 0; i < vertexIdsOrElements.length; ++i) {
            keys[i] = (String) vertexIdsOrElements[i];
        }
        stepBegin("V");
        StringBuilderHelper.appendMultiString(ss, false, keys);
        return stepEnd();
    }

    public GraphTraversal E(String... keys) {
        stepBegin("E");
        StringBuilderHelper.appendMultiString(ss, false, keys);
        return stepEnd();
    }

    public GraphTraversal join() {
        stepBegin("join");
        return stepEnd();
    }

    public GraphTraversal join(Join joinType, String joinTag) {
        stepBegin("join");
        StringBuilderHelper.appendParameter(ss, false, joinType);
        StringBuilderHelper.appendParameter(ss, true, joinTag);
        return stepEnd();
    }

    @Override
    public GraphTraversal to(org.apache.tinkerpop.gremlin.structure.Direction direction, String... edgeLabels) {
        Direction igraphDirection = StringBuilderHelper.convertDirection(direction);
        stepBegin("to");
        StringBuilderHelper.stepWithAtLeastSingleParameter(ss, igraphDirection, edgeLabels);
        return stepEnd();
    }

    // public GraphTraversal V(final vertexIdsOrElements);

    @Override
    public GraphTraversal out(final String... edgeLabels) {
        stepBegin("out");
        StringBuilderHelper.appendMultiString(ss, false, edgeLabels);
        return stepEnd();
    }

    @Override
    public GraphTraversal in(final String... edgeLabels) {
        stepBegin("in");
        StringBuilderHelper.appendMultiString(ss, false, edgeLabels);
        return stepEnd();
    }

    @Override
    public GraphTraversal both(final String... edgeLabels) {
        stepBegin("both");
        StringBuilderHelper.appendMultiString(ss, false, edgeLabels);
        return stepEnd();
    }

    @Override
    public GraphTraversal toE(org.apache.tinkerpop.gremlin.structure.Direction direction, String... edgeLabels) {
        Direction igraphDirection = StringBuilderHelper.convertDirection(direction);
        stepBegin("toE");
        StringBuilderHelper.stepWithAtLeastSingleParameter(ss, igraphDirection, edgeLabels);
        return stepEnd();
    }

    @Override
    public GraphTraversal outE(final String... edgeLabels) {
        stepBegin("outE");
        StringBuilderHelper.appendMultiString(ss, false, edgeLabels);
        return stepEnd();
    }

    @Override
    public GraphTraversal inE(final String... edgeLabels) {
        stepBegin("inE");
        StringBuilderHelper.appendMultiString(ss, false, edgeLabels);
        return stepEnd();
    }

    @Override
    public GraphTraversal bothE(final String... edgeLabels) {
        stepBegin("bothE");
        StringBuilderHelper.appendMultiString(ss, false, edgeLabels);
        return stepEnd();
    }

    @Override
    public GraphTraversal toV(org.apache.tinkerpop.gremlin.structure.Direction direction) {
        Direction igraphDirection = StringBuilderHelper.convertDirection(direction);
        stepBegin("toV");
        StringBuilderHelper.appendParameter(ss, false, igraphDirection);
        return stepEnd();
    }

    @Override
    public GraphTraversal inV() {
        stepBegin("inV");
        return stepEnd();
    }

    @Override
    public GraphTraversal outV() {
        stepBegin("outV");
        return stepEnd();
    }

    @Override
    public GraphTraversal bothV() {
        stepBegin("bothV");
        return stepEnd();
    }

    @Override
    public GraphTraversal otherV() {
        throw new NotSupportedException("method is not supported");
    }

    // public GraphTraversal otherV();

    @Override
    public GraphTraversal order() {
        stepBegin("order");
        return stepEnd();
    }

    @Override
    public GraphTraversal order(org.apache.tinkerpop.gremlin.process.traversal.Scope scope) {
        throw new NotSupportedException("method is not supported");
    }

    // public GraphTraversal order(final Scope scope);

    @Override
    public GraphTraversal properties(final String... propertyKeys) {
        stepBegin("properties");
        StringBuilderHelper.appendMultiString(ss, false, propertyKeys);
        return stepEnd();
    }

    @Override
    public GraphTraversal values(final String... propertyKeys) {
        stepBegin("values");
        StringBuilderHelper.appendMultiString(ss, false, propertyKeys);
        return stepEnd();
    }

    @Override
    public GraphTraversal propertyMap(final String... propertyKeys) {
        stepBegin("propertyMap");
        StringBuilderHelper.appendMultiString(ss, false, propertyKeys);
        return stepEnd();
    }

    @Override
    public GraphTraversal elementMap(
            String... propertyKeys) {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public GraphTraversal valueMap(final String... propertyKeys) {
        stepBegin("valueMap");
        StringBuilderHelper.appendMultiString(ss, false, propertyKeys);
        return stepEnd();
    }

    @Override
    public GraphTraversal valueMap(boolean includeTokens, String... propertyKeys) {
        throw new NotSupportedException("method is not supported");
    }

    public GraphTraversal select(final Column column) {
        stepBegin("select");
        StringBuilderHelper.appendParameter(ss, false, column);
        return stepEnd();
    }

    // public GraphTraversal mapValues();

    // public GraphTraversal mapKeys();

    @Override
    public GraphTraversal key() {
        stepBegin("key");
        return stepEnd();
    }

    @Override
    public GraphTraversal value() {
        stepBegin("value");
        return stepEnd();
    }

    @Override
    public GraphTraversal path() {
        stepBegin("path");
        return stepEnd();
    }

    @Override
    public GraphTraversal match(Traversal... matchTraversals) {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public GraphTraversal sack() {
        stepBegin("sack");
        return stepEnd();
    }

    @Override
    public GraphTraversal loops() {
        stepBegin("loops");
        return stepEnd();
    }

    @Override
    public GraphTraversal loops(String loopName) {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public GraphTraversal project(final String projectKey, final String... otherProjectKeys) {
        stepBegin("project");
        StringBuilderHelper.appendParameter(ss, false, projectKey);
        StringBuilderHelper.appendMultiString(ss, true, otherProjectKeys);
        return stepEnd();
    }

    @Override
    public GraphTraversal select(org.apache.tinkerpop.gremlin.process.traversal.Pop pop,
                                 String selectKey1,
                                 String selectKey2,
                                 String... otherSelectKeys) {
        Pop igraphPop = StringBuilderHelper.convertPop(pop);
        stepBegin("select");
        StringBuilderHelper.appendParameter(ss, false, igraphPop);
        StringBuilderHelper.appendParameter(ss, true, selectKey1);
        StringBuilderHelper.appendParameter(ss, true, selectKey2);
        StringBuilderHelper.appendMultiString(ss, true, otherSelectKeys);
        return stepEnd();
    }

    @Override
    public GraphTraversal select(String selectKey1, String selectKey2, String... otherSelectKeys) {
        stepBegin("select");
        StringBuilderHelper.appendParameter(ss, false, selectKey1);
        StringBuilderHelper.appendParameter(ss, true, selectKey2);
        StringBuilderHelper.appendMultiString(ss, true, otherSelectKeys);
        return stepEnd();
    }

    @Override
    public GraphTraversal select(org.apache.tinkerpop.gremlin.process.traversal.Pop pop, String selectKey) {
        Pop igraphPop = StringBuilderHelper.convertPop(pop);
        stepBegin("select");
        StringBuilderHelper.appendParameter(ss, false, igraphPop);
        StringBuilderHelper.appendParameter(ss, true, selectKey);
        return stepEnd();
    }

    @Override
    public GraphTraversal select(String selectKey) {
        stepBegin("select");
        StringBuilderHelper.appendParameter(ss, false, selectKey);
        return stepEnd();
    }

    @Override
    public GraphTraversal select(
            org.apache.tinkerpop.gremlin.process.traversal.Pop pop, Traversal keyTraversal) {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public GraphTraversal select(Traversal keyTraversal) {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public GraphTraversal unfold() {
        stepBegin("unfold");
        return stepEnd();
    }

    @Override
    public GraphTraversal fold() {
        stepBegin("fold");
        return stepEnd();
    }

    @Override
    public GraphTraversal fold(Object seed, BiFunction foldFunction) {
        throw new NotSupportedException("method is not supported");
    }

    // public GraphTraversal fold(final T seed, final BiFunction);

    @Override
    public GraphTraversal count() {
        stepBegin("count");
        return stepEnd();
    }

    @Override
    public GraphTraversal count(org.apache.tinkerpop.gremlin.process.traversal.Scope scope) {
        Scope igraphScope = StringBuilderHelper.convertScope(scope);
        stepBegin("count");
        StringBuilderHelper.appendParameter(ss, false, igraphScope);
        return stepEnd();
    }

    @Override
    public GraphTraversal sum() {
        stepBegin("sum");
        return stepEnd();
    }

    @Override
    public GraphTraversal sum(org.apache.tinkerpop.gremlin.process.traversal.Scope scope) {
        Scope igraphScope = StringBuilderHelper.convertScope(scope);
        stepBegin("sum");
        StringBuilderHelper.appendParameter(ss, false, igraphScope);
        return stepEnd();
    }

    @Override
    public GraphTraversal max() {
        stepBegin("max");
        return stepEnd();
    }

    @Override
    public GraphTraversal max(final org.apache.tinkerpop.gremlin.process.traversal.Scope scope) {
        Scope igraphScope = StringBuilderHelper.convertScope(scope);
        stepBegin("max");
        StringBuilderHelper.appendParameter(ss, false, igraphScope);
        return stepEnd();
    }

    @Override
    public GraphTraversal min() {
        stepBegin("min");
        return stepEnd();
    }

    @Override
     public GraphTraversal min(final org.apache.tinkerpop.gremlin.process.traversal.Scope scope) {
        Scope igraphScope = StringBuilderHelper.convertScope(scope);
        stepBegin("min");
        StringBuilderHelper.appendParameter(ss, false, igraphScope);
        return stepEnd();
    }


    @Override
    public GraphTraversal mean() {
        stepBegin("mean");
        return stepEnd();
    }

    @Override
     public GraphTraversal mean(final org.apache.tinkerpop.gremlin.process.traversal.Scope  scope) {
        throw new NotSupportedException("method is not supported");
    }


    @Override
    public GraphTraversal group() {
        stepBegin("group");
        return stepEnd();
    }

    // public GraphTraversal groupV3d0();

    @Override
    public GraphTraversal groupCount() {
        stepBegin("groupCount");
        return stepEnd();
    }

    @Override
    public GraphTraversal tree() {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public GraphTraversal addV(String vertexLabel) {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public GraphTraversal addV( Traversal vertexLabelTraversal) {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public GraphTraversal addV() {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public GraphTraversal addE(String edgeLabel) {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public GraphTraversal addE(Traversal edgeLabelTraversal) {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public GraphTraversal to(final String toStepLabel) {
        stepBegin("to");
        StringBuilderHelper.appendParameter(ss, false, toStepLabel);
        return stepEnd();
    }

    @Override
    public GraphTraversal from(final String toStepLabel) {
        stepBegin("from");
        StringBuilderHelper.appendParameter(ss, false, toStepLabel);
        return stepEnd();
    }

    @Override
    public GraphTraversal to(Traversal toVertex) {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public GraphTraversal from(Traversal fromVertex) {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public GraphTraversal to(Vertex toVertex) {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public GraphTraversal from(Vertex fromVertex) {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public GraphTraversal math(String expression) {
        stepBegin("math");
        StringBuilderHelper.appendParameter(ss, false, expression);
        return stepEnd();
    }

    //////////////////////  FILTER STEPS  //////////////////////////

    // public GraphTraversal filter(final Predicate<Traverser<E>> predicate);

    @Override
    public GraphTraversal filter(final Traversal filterTraversal) {
        stepBegin("filter");
        StringBuilderHelper.appendParameter(ss, false, filterTraversal);
        return stepEnd();
    }

    public GraphTraversal filter(final String expression) {
        stepBegin("filter");
        StringBuilderHelper.appendParameter(ss, false, expression);
        return stepEnd();
    }

    @Override
    public GraphTraversal or(final Traversal... orTraversals) {
        stepBegin("or");
        boolean needComma = false;
        for (Traversal traversal : orTraversals) {
            StringBuilderHelper.appendParameter(ss, needComma, traversal);
            needComma = true;
        }
        return stepEnd();
    }

    @Override
    public GraphTraversal and(final Traversal... orTraversals) {
        stepBegin("and");
        boolean needComma = false;
        for (Traversal traversal : orTraversals) {
            StringBuilderHelper.appendParameter(ss, needComma, traversal);
            needComma = true;
        }
        return stepEnd();
    }

    @Override
    public GraphTraversal inject(Object... injections) {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public GraphTraversal dedup(final org.apache.tinkerpop.gremlin.process.traversal.Scope scope, final String... dedupLabels) {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public GraphTraversal dedup(final String... dedupLabels) {
        stepBegin("dedup");
        StringBuilderHelper.appendMultiString(ss, false, dedupLabels);
        return stepEnd();
    }

    public GraphTraversal where(final String startKey, final P predicate) {
        stepBegin("where");
        StringBuilderHelper.appendParameter(ss, false, startKey);
        StringBuilderHelper.appendParameter(ss, true, predicate);
        return stepEnd();
    }

    public GraphTraversal where(final P predicate) {
        stepBegin("where");
        StringBuilderHelper.appendParameter(ss, false, predicate);
        return stepEnd();
    }

    @Override
    public GraphTraversal where(final Traversal whereTraversal) {
        stepBegin("where");
        StringBuilderHelper.appendParameter(ss, false, whereTraversal);
        return stepEnd();
    }

    public GraphTraversal has(final String propertyKey, final P predicate) {
        stepBegin("has");
        StringBuilderHelper.appendParameter(ss, false, propertyKey);
        StringBuilderHelper.appendParameter(ss, true, predicate);
        return stepEnd();
    }

    public GraphTraversal has(final T accessor, final P predicate) {
        stepBegin("has");
        StringBuilderHelper.appendParameter(ss, false, accessor);
        StringBuilderHelper.appendParameter(ss, true, predicate);
        return stepEnd();
    }

    @Override
    public GraphTraversal has(final String propertyKey, final Object value) {
        if (value instanceof P) {
            return this.has(propertyKey, (P) value);
        } else if (value instanceof Traversal) {
            return this.has(propertyKey, (Traversal) value);
        }
        stepBegin("has");
        StringBuilderHelper.appendParameter(ss, false, propertyKey);
        if (value instanceof String) {
            StringBuilderHelper.appendParameter(ss, true, (String)value);
        } else {
            StringBuilderHelper.appendParameter(ss, true, value);
        }
        return stepEnd();
    }

    @Override
    public GraphTraversal has(final org.apache.tinkerpop.gremlin.structure.T accessor, final Object value) {
        throw new NotSupportedException("method is not supported");
    }

    public GraphTraversal has(final String label, final String propertyKey, final P predicate) {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public GraphTraversal has(String label, String propertyKey, Object value) {
        stepBegin("has");
        StringBuilderHelper.appendParameter(ss, false, label);
        StringBuilderHelper.appendParameter(ss, true, propertyKey);
        StringBuilderHelper.appendParameter(ss, true, value);
        return stepEnd();
    }

    @Override
    public GraphTraversal has(org.apache.tinkerpop.gremlin.structure.T accessor, Traversal propertyTraversal) {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public GraphTraversal has(final String propertyKey, final Traversal propertyTraversal) {
        stepBegin("has");
        StringBuilderHelper.appendParameter(ss, false, propertyKey);
        StringBuilderHelper.appendParameter(ss, true, propertyTraversal);
        return stepEnd();
    }

    @Override
    public GraphTraversal has(final String propertyKey) {
        stepBegin("has");
        StringBuilderHelper.appendParameter(ss, false, propertyKey);
        return stepEnd();
    }

    @Override
    public GraphTraversal hasNot(String propertyKey) {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public GraphTraversal hasLabel(final String label, final String... otherLabels) {
        final String[] labels = new String[otherLabels.length + 1];
        labels[0] = label;
        System.arraycopy(otherLabels, 0, labels, 1, otherLabels.length);
        this.labels = Arrays.asList(labels);
        if(!isSearch) {
            stepBegin("hasLabel");
            isSearch = false;
        } else {
            stepBegin("hasLabel");
        }
        StringBuilderHelper.stepWithAtLeastSingleParameter(ss, label, otherLabels);
        return stepEnd();
    }

    public GraphTraversal hasLabel(final P predicate) {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public GraphTraversal hasId(Object id, Object... otherIds) {
        throw new NotSupportedException("method is not supported");
    }

    public GraphTraversal hasId(final P predicate) {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public GraphTraversal hasKey(final String label, final String... otherLabels) {
        stepBegin("hasKey");
        StringBuilderHelper.stepWithAtLeastSingleParameter(ss, label, otherLabels);
        return stepEnd();
    }

    public GraphTraversal hasKey(final P predicate) {
        stepBegin("hasKey");
        StringBuilderHelper.appendParameter(ss, false, predicate);
        return stepEnd();
    }

    @Override
    public GraphTraversal hasValue(final Object value, final Object...otherValues) {
        stepBegin("hasValue");
        boolean isString = (value instanceof String);
        if (isString) {
            StringBuilderHelper.appendParameter(ss, false, (String)value);
        } else {
            StringBuilderHelper.appendParameter(ss, false, value);
        }
        for (Object item : otherValues) {
            if (isString) {
                StringBuilderHelper.appendParameter(ss, true, (String)item);
            } else {
                StringBuilderHelper.appendParameter(ss, true, item);
            }
        }
        return stepEnd();
    }

    public GraphTraversal hasValue(final P predicate) {
        stepBegin("hasValue");
        StringBuilderHelper.appendParameter(ss, false, predicate);
        return stepEnd();
    }

    public GraphTraversal is(final P predicate) {
        stepBegin("is");
        StringBuilderHelper.appendParameter(ss, false, predicate);
        return stepEnd();
    }

    @Override
    public GraphTraversal is(final Object value) {
        stepBegin("is");
        if (value instanceof String) {
            StringBuilderHelper.appendParameter(ss, false, (String)value);
        } else {
            StringBuilderHelper.appendParameter(ss, false, value);
        }
        return stepEnd();
    }

    @Override
    public GraphTraversal not(final Traversal notTraversal) {
        stepBegin("not");
        StringBuilderHelper.appendParameter(ss, false, notTraversal);
        return stepEnd();
    }

    @Override
    public GraphTraversal coin(double probability) {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public GraphTraversal range(final long low, final long high) {
        stepBegin("range");
        StringBuilderHelper.appendParameter(ss, false, low);
        StringBuilderHelper.appendParameter(ss, true, high);
        return stepEnd();
    }

    @Override
    public GraphTraversal range(final org.apache.tinkerpop.gremlin.process.traversal.Scope scope, final long low, final long high) {
        Scope igraphScope = StringBuilderHelper.convertScope(scope);
        stepBegin("range");
        StringBuilderHelper.appendParameter(ss, false, igraphScope);
        StringBuilderHelper.appendParameter(ss, true, low);
        StringBuilderHelper.appendParameter(ss, true, high);
        return stepEnd();
    }

    @Override
    public GraphTraversal limit(final long limit) {
        stepBegin("limit");
        StringBuilderHelper.appendParameter(ss, false, limit);
        return stepEnd();
    }

    @Override
    public GraphTraversal limit(final org.apache.tinkerpop.gremlin.process.traversal.Scope scope, final long limit) {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public GraphTraversal tail() {
        stepBegin("tail");
        return stepEnd();
    }

    @Override
    public GraphTraversal tail(final long limit) {
        stepBegin("tail");
        StringBuilderHelper.appendParameter(ss, false, limit);
        return stepEnd();
    }

    @Override
    public GraphTraversal tail(final org.apache.tinkerpop.gremlin.process.traversal.Scope scope) {
        Scope igraphScope = StringBuilderHelper.convertScope(scope);
        stepBegin("tail");
        StringBuilderHelper.appendParameter(ss, false, igraphScope);
        return stepEnd();
    }

    @Override
    public GraphTraversal tail(final org.apache.tinkerpop.gremlin.process.traversal.Scope scope, final long limit) {
        Scope igraphScope = StringBuilderHelper.convertScope(scope);
        stepBegin("tail");
        StringBuilderHelper.appendParameter(ss, false, igraphScope);
        StringBuilderHelper.appendParameter(ss, true, limit);
        return stepEnd();
    }

    @Override
    public GraphTraversal skip(long skip) {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public GraphTraversal skip(final org.apache.tinkerpop.gremlin.process.traversal.Scope scope, final long skip) {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public GraphTraversal timeLimit(long timeLimit) {
        throw new NotSupportedException("method is not supported");
    }

    // public GraphTraversal timeLimit(final long timeLimit);

    @Override
    public GraphTraversal simplePath() {
        stepBegin("simplePath");
        return stepEnd();
    }

    @Override
    public GraphTraversal cyclicPath() {
        stepBegin("cyclicPath");
        return stepEnd();
    }

    @Override
    public GraphTraversal sample(final int amountToSample) {
        stepBegin("sample");
        StringBuilderHelper.appendParameter(ss, false, amountToSample);
        return stepEnd();
    }

    @Override
    public GraphTraversal sample(final org.apache.tinkerpop.gremlin.process.traversal.Scope scope, final int amountToSample) {
        Scope igraphScope = StringBuilderHelper.convertScope(scope);
        stepBegin("sample");
        StringBuilderHelper.appendParameter(ss, false, igraphScope);
        StringBuilderHelper.appendParameter(ss, true, amountToSample);
        return stepEnd();
    }

    public GraphTraversal sample(final Sample sampleOption, final int amountToSample) {
        stepBegin("sample");
        StringBuilderHelper.appendParameter(ss, false, sampleOption);
        StringBuilderHelper.appendParameter(ss, true, amountToSample);
        return stepEnd();
    }

    public GraphTraversal sample(final Sample sampleOption, final Sample sampleOption2, final int amountToSample) {
        stepBegin("sample");
        StringBuilderHelper.appendParameter(ss, false, sampleOption);
        StringBuilderHelper.appendParameter(ss, true, sampleOption2);
        StringBuilderHelper.appendParameter(ss, true, amountToSample);
        return stepEnd();
    }

    @Override
    public GraphTraversal drop() {
        if (isSearch) {
            throw new IGraphQueryException("complex step is not supported in delete query!");
        }
        if (keys.size() != 1 || labels.size() != 1) {
            throw new IGraphQueryException("batch delete is not supported!");
        }
        gremlinQueryType = GremlinQueryType.DELETE;
        if (updateQuery == null) {
            tableName = tableName + "_" + labels.get(0);
            Map.Entry<String, String> keyMap = StringBuilderHelper.decodeKeyString(keys.get(0));
            updateQuery = UpdateQuery.builder()
                    .table(tableName)
                    .pkey(keyMap.getKey())
                    .skey(keyMap.getValue())
                    .build();
        }
        return this;
    }

    public GraphTraversal distinct() {
        stepBegin("distinct");
        return stepEnd();
    }

    public GraphTraversal distinct(final Distinct distinct) {
        stepBegin("distinct");
        StringBuilderHelper.appendParameter(ss, false, distinct);
        return stepEnd();
    }

    public GraphTraversal distinct(final Distinct distinct, final int amount) {
        stepBegin("distinct");
        StringBuilderHelper.appendParameter(ss, false, distinct);
        StringBuilderHelper.appendParameter(ss, true, amount);
        return stepEnd();
    }

    public GraphTraversal distinct(final Distinct distinct1, final int amount, final Distinct distinct2) {
        stepBegin("distinct");
        StringBuilderHelper.appendParameter(ss, false, distinct1);
        StringBuilderHelper.appendParameter(ss, true, amount);
        StringBuilderHelper.appendParameter(ss, true, distinct2);
        return stepEnd();
    }

    public GraphTraversal distinct(final Distinct distinct1, final int amount1, final Distinct distinct2, final int amount2) {
        stepBegin("distinct");
        StringBuilderHelper.appendParameter(ss, false, distinct1);
        StringBuilderHelper.appendParameter(ss, true, amount1);
        StringBuilderHelper.appendParameter(ss, true, distinct2);
        StringBuilderHelper.appendParameter(ss, true, amount2);
        return stepEnd();
    }

    public GraphTraversal distinct(final Distinct distinct1, final int amount1, final Distinct distinct2, final int amount2, final Distinct distinct3) {
        stepBegin("distinct");
        StringBuilderHelper.appendParameter(ss, false, distinct1);
        StringBuilderHelper.appendParameter(ss, true, amount1);
        StringBuilderHelper.appendParameter(ss, true, distinct2);
        StringBuilderHelper.appendParameter(ss, true, amount2);
        StringBuilderHelper.appendParameter(ss, true, distinct3);
        return stepEnd();
    }

    ///////////////////// SIDE-EFFECT STEPS /////////////////////

    public GraphTraversal alias(String sideEffectKey) {
        stepBegin("alias");
        StringBuilderHelper.appendParameter(ss, false, sideEffectKey);
        return stepEnd();
    }
    // public GraphTraversal sideEffect(final Consumer);

    @Override
    public GraphTraversal sideEffect(Traversal sideEffectTraversal) {
        stepBegin("sideEffect");
        StringBuilderHelper.appendParameter(ss, false, sideEffectTraversal);
        return stepEnd();
    }

    public GraphTraversal get(final int idx) {
        stepBegin("get");
        StringBuilderHelper.appendParameter(ss, false, idx);
        return stepEnd();
    }

    public GraphTraversal get(final float idx) {
        stepBegin("get");
        StringBuilderHelper.appendParameter(ss, false, idx);
        return stepEnd();
    }

    public GraphTraversal get(final String idx) {
        stepBegin("get");
        StringBuilderHelper.appendParameter(ss, false, idx);
        return stepEnd();
    }

    @Override
    public GraphTraversal cap(String sideEffectKey, String... sideEffectKeys) {
        stepBegin("cap");
        StringBuilderHelper.stepWithAtLeastSingleParameter(ss, sideEffectKey, sideEffectKeys);
        return stepEnd();
    }

    @Override
    public GraphTraversal subgraph(String sideEffectKey) {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public GraphTraversal aggregate(final String sideEffectKey) {
        stepBegin("aggregate");
        StringBuilderHelper.appendParameter(ss, false, sideEffectKey);
        return stepEnd();
    }

    @Override
    public GraphTraversal aggregate(final org.apache.tinkerpop.gremlin.process.traversal.Scope scope,
                                    final String sideEffectKey) {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public GraphTraversal group(String sideEffectKey) {
        this.stepBegin("group");
        StringBuilderHelper.appendParameter(ss, false, sideEffectKey);
        return this.stepEnd();
    }

    @Override
    public GraphTraversal groupCount(String sideEffectKey) {
        this.stepBegin("groupCount");
        StringBuilderHelper.appendParameter(ss, false, sideEffectKey);
        return this.stepEnd();
    }

    @Override
    public GraphTraversal tree(String sideEffectKey) {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public GraphTraversal sack(BiFunction sackOperator) {
        throw new NotSupportedException("method is not supported");
    }

    public GraphTraversal sack(final Operator sackOperator) {
        stepBegin("sack");
        StringBuilderHelper.appendParameter(ss, false, sackOperator);
        return stepEnd();
    }

    public GraphTraversal sack(final String mapKey, final Operator sackOperator) {
        stepBegin("sack");
        StringBuilderHelper.appendParameter(ss, false, mapKey);
        StringBuilderHelper.appendParameter(ss, true, sackOperator);
        return stepEnd();
    }

    @Override
    public GraphTraversal store(String sideEffectKey) {
        stepBegin("store");
        StringBuilderHelper.appendParameter(ss, false, sideEffectKey);
        return stepEnd();
    }

    @Override
    public GraphTraversal profile(String sideEffectKey) {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public GraphTraversal property(Cardinality cardinality, Object key, Object value, Object... keyValues) {
        throw new NotSupportedException("method is not supported");
    }

    /**
     * only used for update
     */
    @Override
    public GraphTraversal property(final Object key, final Object value, final Object... keyValues) {
        if (keyValues.length > 0 || !(value instanceof String) || !(key instanceof String)) {
            throw new NotSupportedException("method is not supported");
        }
        if (updateQuery == null) {
            updateQuery = UpdateQuery.builder()
                    .table(tableName)
                    .build();
        }
        switch ((String) key) {
            case "pkey":
                SetUpdateQueryHelper.pkey(updateQuery, (String)value);
                break;
            case "skey":
                SetUpdateQueryHelper.skey(updateQuery, (String)value);
                break;
            default:
                SetUpdateQueryHelper.valueMap(updateQuery, (String)key, (String)value);
        }
        return this;
    }

    ///////////////////// BRANCH STEPS /////////////////////

    public GraphTraversal bulk() {
        stepBegin("bulk");
        return stepEnd();
    }

    public GraphTraversal toList() {
        stepBegin("toList");
        return stepEnd();
    }

    public GraphTraversal withBulk() {
        stepBegin("withBulk");
        return stepEnd();
    }

    public GraphTraversal withSack() {
        stepBegin("withSack");
        return stepEnd();
    }

    public GraphTraversal needFold() {
        stepBegin("needFold");
        return stepEnd();
    }

    public GraphTraversal fields(final String... fieldNames) {
        stepBegin("fields");
        StringBuilderHelper.appendMultiString(ss, false, fieldNames);
        return stepEnd();
    }

    @Override
    public GraphTraversal branch(final Traversal branchTraversal) {
        stepBegin("branch");
        StringBuilderHelper.appendParameter(ss, false, branchTraversal);
        return stepEnd();
    }

    // public GraphTraversal branch(final Function);

    @Override
    public GraphTraversal choose(final Traversal choiceTraversal) {
        stepBegin("choose");
        StringBuilderHelper.appendParameter(ss, false, choiceTraversal);
        return stepEnd();
    }

    @Override
    public GraphTraversal choose(final Traversal traversalPredicate, final Traversal trueChoice, final Traversal falseChoice) {
        stepBegin("choose");
        StringBuilderHelper.appendParameter(ss, false, traversalPredicate);
        StringBuilderHelper.appendParameter(ss, true, trueChoice);
        StringBuilderHelper.appendParameter(ss, true, falseChoice);
        return stepEnd();
    }

    @Override
    public GraphTraversal choose(final Traversal traversalPredicate, final Traversal trueChoice) {
        stepBegin("choose");
        StringBuilderHelper.appendParameter(ss, false, traversalPredicate);
        StringBuilderHelper.appendParameter(ss, true, trueChoice);
        return stepEnd();
    }

    @Override
    public GraphTraversal choose(Function choiceFunction) {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public GraphTraversal choose(Predicate choosePredicate, Traversal trueChoice, Traversal falseChoice) {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public GraphTraversal choose(Predicate choosePredicate, Traversal trueChoice) {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public GraphTraversal optional(final Traversal optionalTraversal) {
        stepBegin("optional");
        StringBuilderHelper.appendParameter(ss, false, optionalTraversal);
        return stepEnd();
    }

    @Override
    public GraphTraversal union(final Traversal... unionTraversals) {
        stepBegin("union");
        boolean needComma = false;
        for (Traversal unionTraversal : unionTraversals) {
            StringBuilderHelper.appendParameter(ss, needComma, unionTraversal);
            needComma = true;
        }
        return stepEnd();
    }

    @Override
    public GraphTraversal coalesce(final Traversal... traversals) {
        stepBegin("coalesce");
        boolean needComma = false;
        for (Traversal traversal : traversals) {
            StringBuilderHelper.appendParameter(ss, needComma, traversal);
            needComma = true;
        }
        return stepEnd();
    }

    @Override
    public GraphTraversal repeat(final Traversal repeatTraversal) {
        stepBegin("repeat");
        StringBuilderHelper.appendParameter(ss, false, repeatTraversal);
        return stepEnd();
    }

    @Override
    public GraphTraversal repeat(String loopName, Traversal repeatTraversal) {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public GraphTraversal emit(final Traversal emitTraversal) {
        stepBegin("emit");
        StringBuilderHelper.appendParameter(ss, false, emitTraversal);
        return stepEnd();
    }

    // public GraphTraversal emit(final Predicate);

    @Override
    public GraphTraversal emit() {
        stepBegin("emit");
        return stepEnd();
    }

    @Override
    public GraphTraversal until(final Traversal untilTraversal) {
        stepBegin("until");
        StringBuilderHelper.appendParameter(ss, false, untilTraversal);
        return stepEnd();
    }

    // public GraphTraversal until(final Predicate);

    @Override
    public GraphTraversal times(final int maxLoops) {
        stepBegin("times");
        StringBuilderHelper.appendParameter(ss, false, maxLoops);
        return stepEnd();
    }

    @Override
    public GraphTraversal local(final Traversal localTraversal) {
        stepBegin("local");
        StringBuilderHelper.appendParameter(ss, false, localTraversal);
        return stepEnd();
    }

    /////////////////// VERTEX PROGRAM STEPS ////////////////

    @Override
    public GraphTraversal pageRank() {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public GraphTraversal pageRank(double alpha) {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public GraphTraversal peerPressure() {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public GraphTraversal connectedComponent() {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public GraphTraversal shortestPath() {
        throw new NotSupportedException("method is not supported");
    }

    // public GraphTraversal program(final VertexProgram);

    ///////////////////// UTILITY STEPS /////////////////////

    @Override
    public GraphTraversal as(final String stepLabel, final String... stepLabels) {
        stepBegin("as");
        StringBuilderHelper.stepWithAtLeastSingleParameter(ss, stepLabel, stepLabels);
        return stepEnd();
    }

    @Override
    public GraphTraversal barrier() {
        stepBegin("barrier");
        return stepEnd();
    }

    @Override
    public GraphTraversal barrier(final int maxBarrierSize) {
        stepBegin("barrier");
        StringBuilderHelper.appendParameter(ss, false, maxBarrierSize);
        return stepEnd();
    }

    // public GraphTraversal barrier(finale Consumer);

    public GraphTraversal barrier(final Barrier barrierOption) {
        stepBegin("barrier");
        StringBuilderHelper.appendParameter(ss, false, barrierOption);
        return stepEnd();
    }

    @Override
    public GraphTraversal index() {
        throw new NotSupportedException("method is not supported");
    }

    //// WITH-MODULATOR

    @Override
    public GraphTraversal with(String key) {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public GraphTraversal with(String key, Object value) {
        throw new NotSupportedException("method is not supported");
    }

    //// BY-MODULATORS

    @Override
    public GraphTraversal by() {
        stepBegin("by");
        return stepEnd();
    }

    @Override
    public GraphTraversal by(final Traversal traversal) {
        stepBegin("by");
        StringBuilderHelper.appendParameter(ss, false, traversal);
        return stepEnd();
    }

    @Override
    public GraphTraversal by(org.apache.tinkerpop.gremlin.structure.T token) {
        throw new NotSupportedException("method is not supported");
    }

    public GraphTraversal by(final T token) {
        stepBegin("by");
        StringBuilderHelper.appendParameter(ss, false, token);
        return stepEnd();
    }

    @Override
    public GraphTraversal by(final String key) {
        stepBegin("by");
        StringBuilderHelper.appendParameter(ss, false, key);
        return stepEnd();
    }

    public GraphTraversal by(final String table, final String field) {
        stepBegin("by");
        StringBuilderHelper.appendMultiString(ss, false, table, field);
        return stepEnd();
    }

    public GraphTraversal by(final String field, final String table, final Traversal traversal) {
        stepBegin("by");
        StringBuilderHelper.appendMultiString(ss, false, field, table);
        StringBuilderHelper.appendParameter(ss, true, traversal);
        return stepEnd();
    }

    @Override
    public GraphTraversal by(Function function) {
        throw new NotSupportedException("method is not supported");
    }

    //// COMPARATOR BY-MODULATORS

    @Override
    public GraphTraversal by(Traversal traversal, Comparator comparator) {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public GraphTraversal by(Comparator comparator) {
        throw new NotSupportedException("method is not supported");
    }

    public GraphTraversal by(final Order order) {
        stepBegin("by");
        StringBuilderHelper.appendParameter(ss, false, order);
        return stepEnd();
    }

    public GraphTraversal by(final Traversal traversal, final Order order) {
        stepBegin("by");
        StringBuilderHelper.appendParameter(ss, false, traversal);
        StringBuilderHelper.appendParameter(ss, true, order);
        return stepEnd();
    }

    public GraphTraversal by(final T accessor, final Order order) {
        stepBegin("by");
        StringBuilderHelper.appendParameter(ss, false, accessor);
        StringBuilderHelper.appendParameter(ss, true, order);
        return stepEnd();
    }

    public GraphTraversal by(final String key, final Order order) {
        stepBegin("by");
        StringBuilderHelper.appendParameter(ss, false, key);
        StringBuilderHelper.appendParameter(ss, true, order);
        return stepEnd();
    }

    public GraphTraversal by(final Column column, final Order order) {
        stepBegin("by");
        StringBuilderHelper.appendParameter(ss, false, column);
        StringBuilderHelper.appendParameter(ss, true, order);
        return stepEnd();
    }

    @Override
    public GraphTraversal by(String key, Comparator comparator) {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public GraphTraversal by(Function function, Comparator comparator) {
        throw new NotSupportedException("method is not supported");
    }

    ////

    @Override
    public GraphTraversal option(Object pickToken, Traversal traversalOption) {
        throw new NotSupportedException("method is not supported");
    }

    public GraphTraversal option(final int pick, final Traversal optionTraversal) {
        stepBegin("option");
        StringBuilderHelper.appendParameter(ss, false, pick);
        StringBuilderHelper.appendParameter(ss, true, optionTraversal);
        return stepEnd();
    }

    public GraphTraversal option(final String pick, final Traversal optionTraversal) {
        stepBegin("option");
        StringBuilderHelper.appendParameter(ss, false, pick);
        StringBuilderHelper.appendParameter(ss, true, optionTraversal);
        return stepEnd();
    }

    @Override
    public GraphTraversal option(Traversal traversalOption) {
        throw new NotSupportedException("method is not supported");
    }

    public GraphTraversal option(final Pick pick, final Traversal optionTraversal) {
        stepBegin("option");
        StringBuilderHelper.appendParameter(ss, false, pick);
        StringBuilderHelper.appendParameter(ss, true, optionTraversal);
        return stepEnd();
    }

    ////

    ///////////////////// IO STEPS /////////////////////

    @Override
    public GraphTraversal read() {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public GraphTraversal write() {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public GraphTraversal iterate() {
        throw new NotSupportedException("method is not supported");
    }


    // public GraphTraversal by(final Function, final Order);



    /////////////////// NO SUPPORT STEPS ////////////////
    public GraphTraversal noSupportedStep(final String stepName, final Object... parameters) {
        stepBegin(stepName);
        boolean needComma = false;
        for (Object parameter : parameters) {
            if (parameter instanceof String) {
                StringBuilderHelper.appendParameter(ss, needComma, (String)parameter);
            } else {
                StringBuilderHelper.appendParameter(ss, needComma, parameter);
            }
            needComma = true;
        }
        return stepEnd();
    }

}
