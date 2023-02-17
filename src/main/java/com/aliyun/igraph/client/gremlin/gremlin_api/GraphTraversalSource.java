package com.aliyun.igraph.client.gremlin.gremlin_api;

import com.aliyun.igraph.client.exception.IGraphServerException;
import com.aliyun.igraph.client.pg.KeyList;
import lombok.NonNull;
import org.apache.tinkerpop.gremlin.process.traversal.Bytecode;
import org.apache.tinkerpop.gremlin.process.traversal.Traversal;
import org.apache.tinkerpop.gremlin.process.traversal.TraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.TraversalStrategies;
import org.apache.tinkerpop.gremlin.process.traversal.TraversalStrategy;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

import javax.ws.rs.NotSupportedException;

/**
 * @author alibaba
 */
public class GraphTraversalSource implements
    org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource {
    private StringBuilder ss = new StringBuilder(2048);
    private int keyStart = 0;
    private int keyEnd = 0;
    private String graphName;
    private List<String> keys;
    private List<String> labels;

    public GraphTraversalSource() {
        ss.append("g");
    }

    protected GraphTraversalSource(String graph) {
        this.graphName = graph;
        ss.append("g(\"").append(graph).append("\")");
    }

    private GraphTraversalSource(GraphTraversalSource graphTraversalSource) {
        this.ss.append(graphTraversalSource.ss.toString());
        this.keyStart = graphTraversalSource.keyStart;
        this.keyEnd = graphTraversalSource.keyEnd;
        this.graphName = graphTraversalSource.graphName;
        this.keys = graphTraversalSource.keys;
        this.labels = graphTraversalSource.labels;
    }

    public static GraphTraversalSource g(String graph) {
        return new GraphTraversalSource(graph);
    }

    @Override
    public String toString() {
        return ss.toString();
    }

    @Override
    public TraversalStrategies getStrategies() {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public Graph getGraph() {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public Bytecode getBytecode() {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public TraversalSource with(String key, Object value) {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public TraversalSource withComputer() {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public <A> TraversalSource withSideEffect(String key, java.util.function.Supplier<A> initialValue,
                                              BinaryOperator<A> reducer) {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public <A> TraversalSource withSideEffect(String key, A initialValue, BinaryOperator<A> reducer) {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public <A> TraversalSource withSideEffect(String key, java.util.function.Supplier<A> initialValue) {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public <A> TraversalSource withSideEffect(String key, A initialValue) {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public GraphTraversalSource clone() {
        return new GraphTraversalSource(this);
    }

    private void stepBegin(final String stepName) {
        ss.append('.').append(stepName).append('(');
    }

    private GraphTraversalSource stepEnd() {
        ss.append(')');
        return this;
    }

    /////////////////  STEP  ////////////
    /**
     * 暂时只支持String
     */
    @Override
    public GraphTraversal V(Object... vertexIds) {
        if (vertexIds.length != 0  && !(vertexIds[0] instanceof String)) {
            throw new IGraphServerException("type [" + vertexIds[0].getClass() + "]in V step is not supported");
        }
        String[] keys = new String[vertexIds.length];
        for (int i = 0; i < vertexIds.length; ++i) {
            keys[i] = (String) vertexIds[i];
        }
        this.keys = Arrays.asList(keys);
        final GraphTraversalSource clone = this.clone();
        clone.stepBegin("V");
        clone.keyStart = clone.ss.length();
        StringBuilderHelper.appendMultiString(clone.ss, false, keys);
        clone.keyEnd = clone.ss.length();
        return new GraphTraversal(clone.stepEnd(), clone.keyStart, clone.keyEnd, clone.graphName, clone.keys, clone.labels);

    }

    public GraphTraversal V(final String... keys) {
        this.keys = Arrays.asList(keys);
        final GraphTraversalSource clone = this.clone();
        clone.stepBegin("V");
        clone.keyStart = clone.ss.length();
        StringBuilderHelper.appendMultiString(clone.ss, false, keys);
        clone.keyEnd = clone.ss.length();
        return new GraphTraversal(clone.stepEnd(), clone.keyStart, clone.keyEnd, clone.graphName, clone.keys, clone.labels);
    }


    
    public GraphTraversal V(@NonNull List<KeyList> keyLists) {
        final GraphTraversalSource clone = this.clone();
        clone.stepBegin("V");
        clone.keyStart = clone.ss.length();
        StringBuilderHelper.appendParameter(clone.ss, false, keyLists);
        clone.keyEnd = clone.ss.length();
        return new GraphTraversal(clone.stepEnd(), clone.keyStart, clone.keyEnd);
    }

    
    public GraphTraversal V(@NonNull KeyList keyList) {
        List<KeyList> keyLists = new ArrayList<KeyList>();
        keyLists.add(keyList);
        return V(keyLists);
    }

    /**
     * 暂时只支持String
     */
    @Override
    public GraphTraversal E(Object... edgesIds) {
        if (edgesIds.length != 0  && !(edgesIds[0] instanceof String)) {
            throw new IGraphServerException("type [" + edgesIds[0].getClass() + "]in V step is not supported");
        }
        String[] keys = new String[edgesIds.length];
        for (int i = 0; i < edgesIds.length; ++i) {
            keys[i] = (String) edgesIds[i];
        }
        this.keys = Arrays.asList(keys);
        final GraphTraversalSource clone = this.clone();
        clone.stepBegin("E");
        clone.keyStart = clone.ss.length();
        StringBuilderHelper.appendMultiString(clone.ss, false, keys);
        clone.keyEnd = clone.ss.length();
        return new GraphTraversal(clone.stepEnd(), clone.keyStart, clone.keyEnd, clone.graphName, clone.keys, clone.labels);

    }

    public GraphTraversal E(final String... keys) {
        this.keys = Arrays.asList(keys);
        final GraphTraversalSource clone = this.clone();
        clone.stepBegin("E");
        clone.keyStart = clone.ss.length();
        StringBuilderHelper.appendMultiString(clone.ss, false, keys);
        clone.keyEnd = clone.ss.length();
        return new GraphTraversal(clone.stepEnd(), clone.keyStart, clone.keyEnd, clone.graphName, clone.keys, clone.labels);

    }

    
    public GraphTraversal E(@NonNull List<KeyList> keyLists) {
        final GraphTraversalSource clone = this.clone();
        clone.stepBegin("E");
        clone.keyStart = clone.ss.length();
        StringBuilderHelper.appendParameter(clone.ss, false, keyLists);
        clone.keyEnd = clone.ss.length();
        return new GraphTraversal(clone.stepEnd(), clone.keyStart, clone.keyEnd);
    }

    
    public GraphTraversal E(@NonNull KeyList keyList) {
        List<KeyList> keyLists = new ArrayList<KeyList>();
        keyLists.add(keyList);
        return E(keyLists);
    }

    @Override
    public org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource withBulk(
        boolean useBulk) {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource withPath() {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public GraphTraversal addV(@NonNull String label) {
        String tableName = graphName + "_" + label;
        return new GraphTraversal(tableName);
    }

    @Override
    public org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal<Vertex, Vertex> addV(
        Traversal<?, String> vertexLabelTraversal) {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal<Vertex, Vertex> addV() {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public GraphTraversal addE(@NonNull String label) {
        String tableName = graphName + "_" + label;
        return new GraphTraversal(tableName);
    }

    @Override
    public org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal<Edge, Edge> addE(
        Traversal<?, String> edgeLabelTraversal) {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public <S> org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal<S, S> inject(S... starts) {
        throw new NotSupportedException("method is not supported");
    }

    public GraphTraversalSource indexQuery(final String queryFeature) {
        final GraphTraversalSource clone = this.clone();
        clone.stepBegin("indexQuery");
        StringBuilderHelper.appendParameter(clone.ss, false, queryFeature);
        return clone.stepEnd();
    }

    @Override
    public TraversalSource withStrategies(TraversalStrategy... traversalStrategies) {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public TraversalSource withoutStrategies(Class<? extends TraversalStrategy>... traversalStrategyClasses) {
        throw new NotSupportedException("method is not supported");
    }

    public GraphTraversalSource withStrategies(final StrategyBase... strategies) {
        final GraphTraversalSource clone = this.clone();
        clone.stepBegin("withStrategies");
        boolean needComma = false;
        for (StrategyBase strategy : strategies) {
            StringBuilderHelper.appendParameter(clone.ss, needComma, strategy);
            needComma = true;
        }
        return clone.stepEnd();
    }
    @Override
    public <A> TraversalSource withSack(java.util.function.Supplier<A> initialValue,
                                        UnaryOperator<A> splitOperator, BinaryOperator<A> mergeOperator) {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public <A> TraversalSource withSack(A initialValue, UnaryOperator<A> splitOperator,
                                        BinaryOperator<A> mergeOperator) {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public <A> TraversalSource withSack(A initialValue) {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public <A> TraversalSource withSack(java.util.function.Supplier<A> initialValue) {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public <A> TraversalSource withSack(java.util.function.Supplier<A> initialValue,
                                        UnaryOperator<A> splitOperator) {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public <A> TraversalSource withSack(A initialValue, UnaryOperator<A> splitOperator) {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public <A> TraversalSource withSack(java.util.function.Supplier<A> initialValue,
                                        BinaryOperator<A> mergeOperator) {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public <A> TraversalSource withSack(A initialValue, BinaryOperator<A> mergeOperator) {
        throw new NotSupportedException("method is not supported");
    }

    public GraphTraversalSource withSack(final Supplier supplier) {
        final GraphTraversalSource clone = this.clone();
        clone.stepBegin("withSack");
        StringBuilderHelper.appendParameter(clone.ss, false, supplier);
        return clone.stepEnd();
    }

    public GraphTraversalSource withSack(final Supplier supplier, final Splitter sackSpliter, final Operator sackMerger) {
        final GraphTraversalSource clone = this.clone();
        clone.stepBegin("withSack");
        StringBuilderHelper.appendParameter(clone.ss, false, supplier);
        StringBuilderHelper.appendParameter(clone.ss, true, sackSpliter);
        StringBuilderHelper.appendParameter(clone.ss, true, sackMerger);
        return clone.stepEnd();
    }

    public GraphTraversalSource withSack(final Supplier supplier, final Splitter sackSpliter, final String sackMerger) {
        final GraphTraversalSource clone = this.clone();
        clone.stepBegin("withSack");
        StringBuilderHelper.appendParameter(clone.ss, false, supplier);
        StringBuilderHelper.appendParameter(clone.ss, true, sackSpliter);
        StringBuilderHelper.appendParameter(clone.ss, true, sackMerger);
        return clone.stepEnd();
    }

    public GraphTraversalSource noSupportedStep(final String stepName, final Object... parameters) {
        final GraphTraversalSource clone = this.clone();
        clone.stepBegin(stepName);
        boolean needComma = false;
        for (Object parameter : parameters) {
            if (parameter instanceof String) {
                StringBuilderHelper.appendParameter(clone.ss, needComma, (String)parameter);
            } else {
                StringBuilderHelper.appendParameter(clone.ss, needComma, parameter);
            }
            needComma = true;
        }
        return clone.stepEnd();
    }

}
