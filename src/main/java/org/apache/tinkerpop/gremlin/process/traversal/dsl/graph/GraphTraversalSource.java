package org.apache.tinkerpop.gremlin.process.traversal.dsl.graph;



import org.apache.tinkerpop.gremlin.process.traversal.Traversal;
import org.apache.tinkerpop.gremlin.process.traversal.TraversalSource;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;

/**
 * @author alibaba
 */
public interface GraphTraversalSource  extends TraversalSource {


    public GraphTraversalSource withBulk(final boolean useBulk);

    public GraphTraversalSource withPath();



    /**
     * Spawns a {@link GraphTraversal} by adding a vertex with the specified label. If the {@code label} is
     * {@code null} then it will default to {@link Vertex#DEFAULT_LABEL}.
     *
     * @param label label of graph
     * @return GraphTraversal
     */
    public GraphTraversal<Vertex, Vertex> addV(final String label);
    /**
     * Spawns a {@link GraphTraversal} by adding a vertex with the label as determined by a {@link Traversal}. If the
     * {@code vertexLabelTraversal} is {@code null} then it will default to {@link Vertex#DEFAULT_LABEL}.
     *
     * @param vertexLabelTraversal Traversal
     * @return GraphTraversal
     */
    public GraphTraversal<Vertex, Vertex> addV(final Traversal<?, String> vertexLabelTraversal);

    /**
     * Spawns a {@link GraphTraversal} by adding a vertex with the default label.
     *
     * @return GraphTraversal
     */
    public GraphTraversal<Vertex, Vertex> addV();

    /**
     * Spawns a {@link GraphTraversal} by adding a edge with the specified label.
     *
     * @param label label of graph
     * @return GraphTraversal
     */
    public GraphTraversal<Edge, Edge> addE(final String label);

    /**
     * Spawns a {@link GraphTraversal} by adding a edge with a label as specified by the provided {@link Traversal}.
     *
     * @param edgeLabelTraversal Trabersal
     * @return GraphTraversal
     */
    public GraphTraversal<Edge, Edge> addE(final Traversal<?, String> edgeLabelTraversal);

    /**
     * Spawns a {@link GraphTraversal} starting it with arbitrary values.
     *
     * @param <S> type
     * @param starts S
     * @return GraphTraversal
     */
    public <S> GraphTraversal<S, S> inject(S... starts);

    /**
     * Spawns a {@link GraphTraversal} starting with all vertices or some subset of vertices as specified by their
     * unique identifier.
     *
     * @param vertexIds Objects
     * @return GraphTraversal
     */
    public GraphTraversal<Vertex, Vertex> V(final Object... vertexIds);

    /**
     * Spawns a {@link GraphTraversal} starting with all edges or some subset of edges as specified by their unique
     * identifier.
     *
     * @param edgesIds Objects
     * @return GraphTraversal
     */
    public GraphTraversal<Edge, Edge> E(final Object... edgesIds);



}
