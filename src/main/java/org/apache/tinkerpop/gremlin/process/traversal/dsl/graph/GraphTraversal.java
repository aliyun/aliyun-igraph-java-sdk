/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tinkerpop.gremlin.process.traversal.dsl.graph;

import org.apache.tinkerpop.gremlin.process.traversal.Path;
import org.apache.tinkerpop.gremlin.process.traversal.Pop;
import org.apache.tinkerpop.gremlin.process.traversal.Scope;
import org.apache.tinkerpop.gremlin.process.traversal.Traversal;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Element;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Property;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.VertexProperty;
import javax.ws.rs.NotSupportedException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 * @author Stephen Mallette (http://stephen.genoprime.com)
 */
public interface GraphTraversal<S, E> extends Traversal<S, E> {

    public interface Admin<S, E> extends Traversal.Admin<S, E>, GraphTraversal<S, E> {

        @Override
        public GraphTraversal<S, E> iterate();
        @Override
        public GraphTraversal.Admin<S, E> clone();
    }

    @Override
    public GraphTraversal.Admin<S, E> asAdmin();

    ///////////////////// MAP STEPS /////////////////////

    ///**
    // * Map a {@link Traverser} referencing an object of type <code>E</code> to an object of type <code>E2</code>.
    // *
    // * @param function the lambda expression that does the functional mapping
    // * @return the traversal with an appended {@link LambdaMapStep}.
    // * @see <a href="http://tinkerpop.apache.org/docs/${project.version}/reference/#general-steps" target="_blank">Reference Documentation - General Steps</a>
    // * @since 3.0.0-incubating
    // */
    //public <E2> GraphTraversal<S, E2> map(final Function<Traverser<E>, E2> function) {
    //    this.asAdmin().getBytecode().addStep(Symbols.map, function);
    //    return this.asAdmin().addStep(new LambdaMapStep<>(this.asAdmin(), function));
    //}

    /**
     * Map a Traverser referencing an object of type <code>E</code> to an object of type <code>E2</code>.
     *
     * @param mapTraversal the traversal expression that does the functional mapping
     * @param <E2>  the end type of the internal traversal
     * @return the traversal with an appended LambdaMapStep.
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - General Steps</a>
     * @since 3.0.0-incubating
     */
    public <E2> GraphTraversal<S, E2> map(final Traversal<?, E2> mapTraversal);

    ///**
    // * Map a {@link Traverser} referencing an object of type <code>E</code> to an iterator of objects of type <code>E2</code>.
    // * The resultant iterator is drained one-by-one before a new <code>E</code> object is pulled in for processing.
    // *
    // * @param function the lambda expression that does the functional mapping
    // * @param <E2>     the type of the returned iterator objects
    // * @return the traversal with an appended {@link LambdaFlatMapStep}.
    // * @see <a href="http://tinkerpop.apache.org/docs/${project.version}/reference/#general-steps" target="_blank">Reference Documentation - General Steps</a>
    // * @since 3.0.0-incubating
    // */
    //public <E2> GraphTraversal<S, E2> flatMap(final Function<Traverser<E>, Iterator<E2>> function) {
    //    this.asAdmin().getBytecode().addStep(Symbols.flatMap, function);
    //    return this.asAdmin().addStep(new LambdaFlatMapStep<>(this.asAdmin(), function));
    //}

    /**
     * Map a Traverser referencing an object of type <code>E</code> to an iterator of objects of type <code>E2</code>.
     * The internal traversal is drained one-by-one before a new <code>E</code> object is pulled in for processing.
     *
     * @param flatMapTraversal the traversal generating objects of type <code>E2</code>
     * @param <E2>             the end type of the internal traversal
     * @return the traversal with an appended TraversalFlatMapStep.
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - General Steps</a>
     * @since 3.0.0-incubating
     */
    public <E2> GraphTraversal<S, E2> flatMap(final Traversal<?, E2> flatMapTraversal);

    /**
     * Map the {@link Element} to its {@link Element#id}.
     *
     * @return the traversal with an appended IdStep.
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Id Step</a>
     * @since 3.0.0-incubating
     */
    public GraphTraversal<S, Object> id();

    /**
     * Map the {@link Element} to its {@link Element#label}.
     *
     * @return the traversal with an appended LabelStep.
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Label Step</a>
     * @since 3.0.0-incubating
     */
    public GraphTraversal<S, String> label();

    /**
     * Map the <code>E</code> object to itself. In other words, a "no op."
     *
     * @return the traversal with an appended IdentityStep.
     * @since 3.0.0-incubating
     */
    public GraphTraversal<S, E> identity();

    /**
     * Map any object to a fixed <code>E</code> value.
     *
     * @param <E2>  the end type of the internal traversal
     * @param e E2
     * @return the traversal with an appended ConstantStep.
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Constant Step</a>
     * @since 3.0.0-incubating
     */
    public <E2> GraphTraversal<S, E2> constant(final E2 e);

    /**
     * A {@code V} step is usually used to start a traversal but it may also be used mid-traversal.
     *
     * @param vertexIdsOrElements vertices to inject into the traversal
     * @return the traversal with an appended GraphStep
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Graph Step</a>
     * @since 3.1.0-incubating
     */
    public  GraphTraversal<S, Vertex> V(final Object... vertexIdsOrElements);

    /**
     * Map the {@link Vertex} to its adjacent vertices given a direction and edge labels.
     *
     * @param direction  the direction to traverse from the current vertex
     * @param edgeLabels the edge labels to traverse
     * @return the traversal with an appended VertexStep.
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Vertex Step</a>
     * @since 3.0.0-incubating
     */
    public GraphTraversal<S, Vertex> to(final Direction direction, final String... edgeLabels);

    /**
     * Map the {@link Vertex} to its outgoing adjacent vertices given the edge labels.
     *
     * @param edgeLabels the edge labels to traverse
     * @return the traversal with an appended VertexStep.
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Vertex Step</a>
     * @since 3.0.0-incubating
     */
    public GraphTraversal<S, Vertex> out(final String... edgeLabels);

    /**
     * Map the {@link Vertex} to its incoming adjacent vertices given the edge labels.
     *
     * @param edgeLabels the edge labels to traverse
     * @return the traversal with an appended VertexStep.
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Vertex Step</a>
     * @since 3.0.0-incubating
     */
    public GraphTraversal<S, Vertex> in(final String... edgeLabels);

    /**
     * Map the {@link Vertex} to its adjacent vertices given the edge labels.
     *
     * @param edgeLabels the edge labels to traverse
     * @return the traversal with an appended VertexStep.
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Vertex Step</a>
     * @since 3.0.0-incubating
     */
    public GraphTraversal<S, Vertex> both(final String... edgeLabels);

    /**
     * Map the {@link Vertex} to its incident edges given the direction and edge labels.
     *
     * @param direction  the direction to traverse from the current vertex
     * @param edgeLabels the edge labels to traverse
     * @return the traversal with an appended VertexStep.
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Vertex Step</a>
     * @since 3.0.0-incubating
     */
    public GraphTraversal<S, Edge> toE(final Direction direction, final String... edgeLabels);

    /**
     * Map the {@link Vertex} to its outgoing incident edges given the edge labels.
     *
     * @param edgeLabels the edge labels to traverse
     * @return the traversal with an appended VertexStep.
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Vertex Step</a>
     * @since 3.0.0-incubating
     */
    public GraphTraversal<S, Edge> outE(final String... edgeLabels);

    /**
     * Map the {@link Vertex} to its incoming incident edges given the edge labels.
     *
     * @param edgeLabels the edge labels to traverse
     * @return the traversal with an appended VertexStep.
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Vertex Step</a>
     * @since 3.0.0-incubating
     */
    public GraphTraversal<S, Edge> inE(final String... edgeLabels);

    /**
     * Map the {@link Vertex} to its incident edges given the edge labels.
     *
     * @param edgeLabels the edge labels to traverse
     * @return the traversal with an appended VertexStep.
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Vertex Step</a>
     * @since 3.0.0-incubating
     */
    public GraphTraversal<S, Edge> bothE(final String... edgeLabels);

    /**
     * Map the {@link Edge} to its incident vertices given the direction.
     *
     * @param direction the direction to traverser from the current edge
     * @return the traversal with an appended EdgeVertexStep.
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Vertex Step</a>
     * @since 3.0.0-incubating
     */
    public GraphTraversal<S, Vertex> toV(final Direction direction);

    /**
     * Map the {@link Edge} to its incoming/head incident {@link Vertex}.
     *
     * @return the traversal with an appended EdgeVertexStep.
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Vertex Step</a>
     * @since 3.0.0-incubating
     */
    public GraphTraversal<S, Vertex> inV();

    /**
     * Map the {@link Edge} to its outgoing/tail incident {@link Vertex}.
     *
     * @return the traversal with an appended EdgeVertexStep.
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Vertex Step</a>
     * @since 3.0.0-incubating
     */
    public GraphTraversal<S, Vertex> outV();

    /**
     * Map the {@link Edge} to its incident vertices.
     *
     * @return the traversal with an appended EdgeVertexStep.
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Vertex Step</a>
     * @since 3.0.0-incubating
     */
    public GraphTraversal<S, Vertex> bothV();

    /**
     * Map the {@link Edge} to the incident vertex that was not just traversed from in the path history.
     *
     * @return the traversal with an appended EdgeOtherVertexStep.
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Vertex Step</a>
     * @since 3.0.0-incubating
     */
    public GraphTraversal<S, Vertex> otherV();
    /**
     * Order all the objects in the traversal up to this point and then emit them one-by-one in their ordered sequence.
     *
     * @return the traversal with an appended OrderGlobalStep.
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Order Step</a>
     * @since 3.0.0-incubating
     */
    public GraphTraversal<S, E> order();
    /**
     * Order either the {@link Scope#local} object (e.g. a list, map, etc.) or the entire {@link Scope#global} traversal stream.
     *
     * @param scope whether the ordering is the current local object or the entire global stream.
     * @return the traversal with an appended OrderGlobalStep or OrderLocalStep depending on the {@code scope}.
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Order Step</a>
     * @since 3.0.0-incubating
     */
    public GraphTraversal<S, E> order(final Scope scope);

    /**
     * Map the {@link Element} to its associated properties given the provide property keys.
     * If no property keys are provided, then all properties are emitted.
     *
     * @param propertyKeys the properties to retrieve
     * @param <E2>         the value type of the returned properties
     * @return the traversal with an appended PropertiesStep.
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Properties Step</a>
     * @since 3.0.0-incubating
     */
    public <E2> GraphTraversal<S, ? extends Property<E2>> properties(final String... propertyKeys);

    /**
     * Map the {@link Element} to the values of the associated properties given the provide property keys.
     * If no property keys are provided, then all property values are emitted.
     *
     * @param propertyKeys the properties to retrieve their value from
     * @param <E2>         the value type of the properties
     * @return the traversal with an appended PropertiesStep.
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Values Step</a>
     * @since 3.0.0-incubating
     */
    public <E2> GraphTraversal<S, E2> values(final String... propertyKeys);

    /**
     * Map the {@link Element} to a {@link Map} of the properties key'd according to their {@link Property#key}.
     * If no property keys are provided, then all properties are retrieved.
     *
     * @param propertyKeys the properties to retrieve
     * @param <E2>         the value type of the returned properties
     * @return the traversal with an appended PropertyMapStep.
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - PropertyMap Step</a>
     * @since 3.0.0-incubating
     */
    public <E2> GraphTraversal<S, Map<String, E2>> propertyMap(final String... propertyKeys);

    /**
     * Map the {@link Element} to a {@code Map} of the property values key'd according to their {@link Property#key}.
     * If no property keys are provided, then all property values are retrieved. For vertices, the {@code Map} will
     * be returned with the assumption of single property values along with {@link T#id} and {@link T#label}. Prefer
     * {@link #valueMap(String...)} if multi-property processing is required. For  edges, keys will include additional
     * related edge structure of {@link Direction#IN} and {@link Direction#OUT} which themselves are {@code Map}
     * instances of the particular {@link Vertex} represented by {@link T#id} and {@link T#label}.
     *
     * @param propertyKeys the properties to retrieve
     * @param <E2>         the value type of the returned properties
     * @return the traversal with an appended ElementMapStep.
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - ElementMap Step</a>
     * @since 3.4.4
     */
    public <E2> GraphTraversal<S, Map<Object, E2>> elementMap(final String... propertyKeys);

    /**
     * Map the {@link Element} to a {@code Map} of the property values key'd according to their {@link Property#key}.
     * If no property keys are provided, then all property values are retrieved.
     *
     * @param propertyKeys the properties to retrieve
     * @param <E2>         the value type of the returned properties
     * @return the traversal with an appended PropertyMapStep.
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - ValueMap Step</a>
     * @since 3.0.0-incubating
     */
    public <E2> GraphTraversal<S, Map<Object, E2>> valueMap(final String... propertyKeys);

    /**
     * Map the {@link Element} to a {@code Map} of the property values key'd according to their {@link Property#key}.
     * If no property keys are provided, then all property values are retrieved.
     *
     * @param includeTokens whether to include {@link T} tokens in the emitted map.
     * @param propertyKeys  the properties to retrieve
     * @param <E2>          the value type of the returned properties
     * @return the traversal with an appended PropertyMapStep.
     * @see <a href="http://tinkerpop.apache.org/docs/p" target="_blank">Reference Documentation - ValueMap Step</a>
     * @since 3.0.0-incubating
     * @deprecated As of release 3.4.0, deprecated in favor of {@link GraphTraversal#valueMap(String...)} in conjunction with
     *             {@link GraphTraversal#with(String, Object)} or simple prefer {@link #elementMap(String...)}.
     */
    @Deprecated
    public <E2> GraphTraversal<S, Map<Object, E2>> valueMap(final boolean includeTokens, final String... propertyKeys);

    /**
     * Map the {@link Property} to its {@link Property#key}.
     *
     * @return the traversal with an appended PropertyKeyStep.
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Key Step</a>
     * @since 3.0.0-incubating
     */
    public GraphTraversal<S, String> key();

    /**
     * Map the {@link Property} to its {@link Property#value}.
     *
     * @param <E2>  the end type of the internal traversal
     * @return the traversal with an appended PropertyValueStep.
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Value Step</a>
     * @since 3.0.0-incubating
     */
    public <E2> GraphTraversal<S, E2> value();

    /**
     * Map the Traverser to its {@link Path} history via Traverser#path.
     *
     * @return the traversal with an appended PathStep.
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Path Step</a>
     * @since 3.0.0-incubating
     */
    public GraphTraversal<S, Path> path();

    /**
     * Map the Traverser to a {@link Map} of bindings as specified by the provided match traversals.
     *
     * @param matchTraversals the traversal that maintain variables which must hold for the life of the traverser
     * @param <E2>            the type of the objects bound in the variables
     * @return the traversal with an appended MatchStep.
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Match Step</a>
     * @since 3.0.0-incubating
     */
    public <E2> GraphTraversal<S, Map<String, E2>> match(final Traversal<?, ?>... matchTraversals);

    /**
     * Map the Traverser to its Traverser#sack value.
     *
     * @param <E2> the sack value type
     * @return the traversal with an appended ackStep.
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Sack Step</a>
     * @since 3.0.0-incubating
     */
    public <E2> GraphTraversal<S, E2> sack();

    /**
     * If the Traverser supports looping then calling this method will extract the number of loops for that
     * traverser.
     *
     * @return the traversal with an appended LoopsStep
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Loops Step</a>
     * @since 3.1.0-incubating
     */
    public GraphTraversal<S, Integer> loops();

    /**
     * If the Traverser supports looping then calling this method will extract the number of loops for that
     * traverser for the named loop.
     *
     * @param loopName String
     * @return the traversal with an appended LoopsStep
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Loops Step</a>
     * @since 3.4.0
     */
    public GraphTraversal<S, Integer> loops(final String loopName);

    /**
     * Projects the current object in the stream into a {@code Map} that is keyed by the provided labels.
     *
     * @param <E2> the type of the objects projected
     * @param otherProjectKeys Strings
     * @param projectKey String
     * @return the traversal with an appended ProjectStep
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Project Step</a>
     * @since 3.2.0-incubating
     */
    public <E2> GraphTraversal<S, Map<String, E2>> project(final String projectKey, final String... otherProjectKeys);

    /**
     * Map the Traverser to a {@link Map} projection of sideEffect values, map values, and/or path values.
     *
     * @param pop             if there are multiple objects referenced in the path, the {@link Pop} to use.
     * @param selectKey1      the first key to project
     * @param selectKey2      the second key to project
     * @param otherSelectKeys the third+ keys to project
     * @param <E2>            the type of the objects projected
     * @return the traversal with an appended SelectStep.
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Select Step</a>
     * @since 3.0.0-incubating
     */
    public <E2> GraphTraversal<S, Map<String, E2>> select(final Pop pop, final String selectKey1, final String selectKey2, String... otherSelectKeys);

    /**
     * Map the Traverser to a {@link Map} projection of sideEffect values, map values, and/or path values.
     *
     * @param selectKey1      the first key to project
     * @param selectKey2      the second key to project
     * @param otherSelectKeys the third+ keys to project
     * @param <E2>            the type of the objects projected
     * @return the traversal with an appended SelectStep.
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Select Step</a>
     * @since 3.0.0-incubating
     */
    public <E2> GraphTraversal<S, Map<String, E2>> select(final String selectKey1, final String selectKey2, String... otherSelectKeys);

    /**
     * Map the Traverser to the object specified by the {@code selectKey} and apply the {@link Pop} operation
     * to it.
     *
     * @param selectKey the key to project
     * @param pop Pop
     * @param <E2>  the type of the objects projected
     * @return the traversal with an appended SelectStep.
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Select Step</a>
     * @since 3.0.0-incubating
     */
    public <E2> GraphTraversal<S, E2> select(final Pop pop, final String selectKey);
    /**
     * Map the Traverser to the object specified by the {@code selectKey}. Note that unlike other uses of
     * {@code select} where there are multiple keys, this use of {@code select} with a single key does not produce a
     * {@code Map}.
     *
     * @param selectKey the key to project
     * @param <E2> the type of the objects projected
     * @return the traversal with an appended SelectStep.
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Select Step</a>
     * @since 3.0.0-incubating
     */
    public <E2> GraphTraversal<S, E2> select(final String selectKey);

    /**
     * Map the Traverser to the object specified by the key returned by the {@code keyTraversal} and apply the {@link Pop} operation
     * to it.
     *
     * @param keyTraversal the traversal expression that selects the key to project
     * @param pop Pop
     * @param <E2> the type of the objects projected
     * @return the traversal with an appended SelectStep.
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Select Step</a>
     * @since 3.3.3
     */
    public <E2> GraphTraversal<S, E2> select(final Pop pop, final Traversal<S, E2> keyTraversal);
    /**
     * Map the Traverser to the object specified by the key returned by the {@code keyTraversal}. Note that unlike other uses of
     * {@code select} where there are multiple keys, this use of {@code select} with a traversal does not produce a
     * {@code Map}.
     *
     * @param keyTraversal the traversal expression that selects the key to project
     * @param <E2> the type of the objects projected
     * @return the traversal with an appended TraversalSelectStep.
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Select Step</a>
     * @since 3.3.3
     */
    public <E2> GraphTraversal<S, E2> select(final Traversal<S, E2> keyTraversal);
    ///**
    // * A version of {@code select} that allows for the extraction of a {@link Column} from objects in the traversal.
    // *
    // * @param column the column to extract
    // * @return the traversal with an appended {@link TraversalMapStep}
    // * @see <a href="http://tinkerpop.apache.org/docs/${project.version}/reference/#select-step" target="_blank">Reference Documentation - Select Step</a>
    // * @since 3.1.0-incubating
    // */
    //public <E2> GraphTraversal<S, Collection<E2>> select(final Column column) {
    //    this.asAdmin().getBytecode().addStep(Symbols.select, column);
    //    return this.asAdmin().addStep(new TraversalMapStep<>(this.asAdmin(), new ColumnTraversal(column)));
    //}

    /**
     * Unrolls a {@code Iterator}, {@code Iterable} or {@code Map} into a linear form or simply emits the object if it
     * is not one of those types.
     *
     * @param <E2> the type of the objects projected
     * @return the traversal with an appended UnfoldStep
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Unfold Step</a>
     * @since 3.0.0-incubating
     */
    public <E2> GraphTraversal<S, E2> unfold();

    /**
     * Rolls up objects in the stream into an aggregate list.
     *
     * @return the traversal with an appended FoldStep
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Fold Step</a>
     * @since 3.0.0-incubating
     */
    public GraphTraversal<S, List<E>> fold();

    /**
     * Rolls up objects in the stream into an aggregate value as defined by a {@code seed} and {@code BiFunction}.
     *
     * @param seed         the value to provide as the first argument to the {@code foldFunction}
     * @param foldFunction the function to fold by where the first argument is the {@code seed} or the value returned from subsequent calss and
     *                     the second argument is the value from the stream
     *
     * @param <E2> the type of the objects projected
     * @return the traversal with an appended FoldStep
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Fold Step</a>
     * @since 3.0.0-incubating
     */
    public <E2> GraphTraversal<S, E2> fold(final E2 seed, final BiFunction<E2, E, E2> foldFunction);

    /**
     * Map the traversal stream to its reduction as a sum of the Traverser#bulk values (i.e. count the number
     * of traversers up to this point).
     *
     * @return the traversal with an appended CountGlobalStep.
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Count Step</a>
     * @since 3.0.0-incubating
     */
    public GraphTraversal<S, Long> count();

    /**
     * Map the traversal stream to its reduction as a sum of the Traverser#bulk values given the specified
     * {@link Scope} (i.e. count the number of traversers up to this point).
     *
     * @param scope Scope
     * @return the traversal with an appended CountGlobalStep or CountLocalStep depending on the {@link Scope}
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Count Step</a>
     * @since 3.0.0-incubating
     */
    public GraphTraversal<S, Long> count(final Scope scope);

    /**
     * Map the traversal stream to its reduction as a sum of the Traverser#get values multiplied by their
     * Traverser#bulk (i.e. sum the traverser values up to this point).
     *
     * @param <E2> the type of the objects projected
     * @return the traversal with an appended SumGlobalStep.
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Sum Step</a>
     * @since 3.0.0-incubating
     */
    public <E2 extends Number> GraphTraversal<S, E2> sum();

    /**
     * Map the traversal stream to its reduction as a sum of the Traverser#get values multiplied by their
     * Traverser#bulk given the specified {@link Scope} (i.e. sum the traverser values up to this point).
     *
     * @param scope Scope
     * @param <E2> the type of the objects projected
     * @return the traversal with an appended SumGlobalStep or SumLocalStep depending on the {@link Scope}.
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Sum Step</a>
     * @since 3.0.0-incubating
     */
    public <E2 extends Number> GraphTraversal<S, E2> sum(final Scope scope);

    /**
     * Determines the largest value in the stream.
     *
     * @param <E2> the type of the objects projected
     * @return the traversal with an appended MaxGlobalStep.
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Max Step</a>
     * @since 3.0.0-incubating
     */
    public <E2 extends Comparable> GraphTraversal<S, E2> max();

    /**
     * Determines the largest value in the stream given the {@link Scope}.
     *
     * @param <E2> the type of the objects projected
     * @param scope Scope
     * @return the traversal with an appended MaxGlobalStep or MaxLocalStep depending on the {@link Scope}
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Max Step</a>
     * @since 3.0.0-incubating
     */
    public <E2 extends Comparable> GraphTraversal<S, E2> max(final Scope scope);

    /**
     * Determines the smallest value in the stream.
     *
     * @param <E2> the type of the objects projected
     * @return the traversal with an appended MinGlobalStep.
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Min Step</a>
     * @since 3.0.0-incubating
     */
    public <E2 extends Comparable> GraphTraversal<S, E2> min();

    /**
     * Determines the smallest value in the stream given the {@link Scope}.
     *
     * @param <E2> the type of the objects projected
     * @param scope Scope
     * @return the traversal with an appended MinGlobalStep or MinLocalStep depending on the {@link Scope}
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Min Step</a>
     * @since 3.0.0-incubating
     */
    public <E2 extends Comparable> GraphTraversal<S, E2> min(final Scope scope);

    /**
     * Determines the mean value in the stream.
     *
     * @param <E2> the type of the objects projected
     * @return the traversal with an appended MeanGlobalStep.
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Mean Step</a>
     * @since 3.0.0-incubating
     */
    public <E2 extends Number> GraphTraversal<S, E2> mean();

    /**
     * Determines the mean value in the stream given the {@link Scope}.
     *
     * @param scope Scope
     * @param <E2> the type of the objects projected
     * @return the traversal with an appended MeanGlobalStep or MeanLocalStep depending on the {@link Scope}
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Mean Step</a>
     * @since 3.0.0-incubating
     */
    public <E2 extends Number> GraphTraversal<S, E2> mean(final Scope scope);

    /**
     * Organize objects in the stream into a {@code Map}. Calls to {@code group()} are typically accompanied with
     * {@link #by()} modulators which help specify how the grouping should occur.
     *
     * @param <V> type
     * @param <K> type
     * @return the traversal with an appended GroupStep.
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Group Step</a>
     * @since 3.1.0-incubating
     */
    public <K, V> GraphTraversal<S, Map<K, V>> group();

    /**
     * Counts the number of times a particular objects has been part of a traversal, returning a {@code Map} where the
     * object is the key and the value is the count.
     *
     * @param <K> type
     * @return the traversal with an appended GroupCountStep.
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - GroupCount Step</a>
     * @since 3.0.0-incubating
     */
    public <K> GraphTraversal<S, Map<K, Long>> groupCount();

    /**
     * Aggregates the emanating paths into a Tree data structure.
     *
     * @return the traversal with an appended TreeStep
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Tree Step</a>
     * @since 3.0.0-incubating
     */
    //public GraphTraversal<S, Tree> tree()
    public GraphTraversal<S, Object> tree();

    /**
     * Adds a {@link Vertex}.
     *
     * @param vertexLabel the label of the {@link Vertex} to add
     * @return the traversal with the AddVertexStep added
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - AddVertex Step</a>
     * @since 3.1.0-incubating
     */
    public GraphTraversal<S, Vertex> addV(final String vertexLabel);

    /**
     * Adds a {@link Vertex} with a vertex label determined by a {@link Traversal}.
     *
     * @param vertexLabelTraversal Traversal
     * @return the traversal with the AddVertexStep added
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - AddVertex Step</a>
     * @since 3.3.1
     */
    public GraphTraversal<S, Vertex> addV(final Traversal<?, String> vertexLabelTraversal);

    /**
     * Adds a {@link Vertex} with a default vertex label.
     *
     * @return the traversal with the AddVertexStep added
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - AddVertex Step</a>
     * @since 3.1.0-incubating
     */
    public GraphTraversal<S, Vertex> addV();

    /**
     * Adds an {@link Edge} with the specified edge label.
     *
     * @param edgeLabel the label of the newly added edge
     * @return the traversal with the AddEdgeStep added
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - AddEdge Step</a>
     * @since 3.1.0-incubating
     */
    public GraphTraversal<S, Edge> addE(final String edgeLabel);

    /**
     * Adds a {@link Edge} with an edge label determined by a {@link Traversal}.
     *
     * @param edgeLabelTraversal Traversal
     * @return the traversal with the AddEdgeStep added
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - AddEdge Step</a>
     * @since 3.3.1
     */
    public GraphTraversal<S, Edge> addE(final Traversal<?, String> edgeLabelTraversal);

    /**
     * Provide {@code to()}-modulation to respective steps.
     *
     * @param toStepLabel the step label to modulate to.
     * @return the traversal with the modified FromToModulating step.
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - To Step</a>
     * @since 3.1.0-incubating
     */
    public GraphTraversal<S, E> to(final String toStepLabel);

    /**
     * Provide {@code from()}-modulation to respective steps.
     *
     * @param fromStepLabel the step label to modulate to.
     * @return the traversal with the modified FromToModulating step.
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - From Step</a>
     * @since 3.1.0-incubating
     */
    public GraphTraversal<S, E> from(final String fromStepLabel);

    /**
     * When used as a modifier to {@link #addE(String)} this method specifies the traversal to use for selecting the
     * incoming vertex of the newly added {@link Edge}.
     *
     * @param toVertex the traversal for selecting the incoming vertex
     * @return the traversal with the modified {AddEdgeStep
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - From Step</a>
     * @since 3.1.0-incubating
     */
    public GraphTraversal<S, E> to(final Traversal<?, Vertex> toVertex);

    /**
     * When used as a modifier to {@link #addE(String)} this method specifies the traversal to use for selecting the
     * outgoing vertex of the newly added {@link Edge}.
     *
     * @param fromVertex the traversal for selecting the outgoing vertex
     * @return the traversal with the modified AddEdgeStep
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - From Step</a>
     * @since 3.1.0-incubating
     */
    public GraphTraversal<S, E> from(final Traversal<?, Vertex> fromVertex);

    /**
     * When used as a modifier to {@link #addE(String)} this method specifies the traversal to use for selecting the
     * incoming vertex of the newly added {@link Edge}.
     *
     * @param toVertex the vertex for selecting the incoming vertex
     * @return the traversal with the modified AddEdgeStep
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - From Step</a>
     * @since 3.3.0
     */
    public GraphTraversal<S, E> to(final Vertex toVertex);

    /**
     * When used as a modifier to {@link #addE(String)} this method specifies the traversal to use for selecting the
     * outgoing vertex of the newly added {@link Edge}.
     *
     * @param fromVertex the vertex for selecting the outgoing vertex
     * @return the traversal with the modified AddEdgeStep
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - From Step</a>
     * @since 3.3.0
     */
    public GraphTraversal<S, E> from(final Vertex fromVertex);

    /**
     * Map the Traverser to a {@link Double} according to the mathematical expression provided in the argument.
     *
     * @param expression the mathematical expression with variables refering to scope variables.
     * @return the traversal with the MathStep added.
     * @since 3.3.1
     */
    public GraphTraversal<S, Double> math(final String expression);

    ///////////////////// FILTER STEPS /////////////////////

    ///**
    // * Map the {@link Traverser} to either {@code true} or {@code false}, where {@code false} will not pass the
    // * traverser to the next step.
    // *
    // * @param predicate the filter function to apply
    // * @return the traversal with the {@link LambdaFilterStep} added
    // * @see <a href="http://tinkerpop.apache.org/docs/${project.version}/reference/#general-steps" target="_blank">Reference Documentation - General Steps</a>
    // * @since 3.0.0-incubating
    // */
    //public GraphTraversal<S, E> filter(final Predicate<Traverser<E>> predicate) {
    //    this.asAdmin().getBytecode().addStep(Symbols.filter, predicate);
    //    return this.asAdmin().addStep(new LambdaFilterStep<>(this.asAdmin(), predicate));
    //}

    /**
     * Map the Traverser to either {@code true} or {@code false}, where {@code false} will not pass the
     * traverser to the next step.
     *
     * @param filterTraversal the filter traversal to apply
     * @return the traversal with the TraversalFilterStep added
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - General Steps</a>
     * @since 3.0.0-incubating
     */
    public GraphTraversal<S, E> filter(final Traversal<?, ?> filterTraversal);

    /**
     * Ensures that at least one of the provided traversals yield a result.
     *
     * @param orTraversals filter traversals where at least one must be satisfied
     * @return the traversal with an appended OrStep
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Or Step</a>
     * @since 3.0.0-incubating
     */
    public GraphTraversal<S, E> or(final Traversal<?, ?>... orTraversals);

    /**
     * Ensures that all of the provided traversals yield a result.
     *
     * @param andTraversals filter traversals that must be satisfied
     * @return the traversal with an appended AndStep
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - And Step</a>
     * @since 3.0.0-incubating
     */
    public GraphTraversal<S, E> and(final Traversal<?, ?>... andTraversals);
    /**
     * Provides a way to add arbitrary objects to a traversal stream.
     *
     * @param injections the objects to add to the stream
     * @return the traversal with an appended InjectStep
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Inject Step</a>
     * @since 3.0.0-incubating
     */
    public GraphTraversal<S, E> inject(final E... injections);
    /**
     * Remove all duplicates in the traversal stream up to this point.
     *
     * @param scope       whether the deduplication is on the stream (global) or the current object (local).
     * @param dedupLabels if labels are provided, then the scope labels determine de-duplication. No labels implies current object.
     * @return the traversal with an appended DedupGlobalStep or DedupLocalStep depending on {@code scope}
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Dedup Step</a>
     * @since 3.0.0-incubating
     */
    public GraphTraversal<S, E> dedup(final Scope scope, final String... dedupLabels);

    /**
     * Remove all duplicates in the traversal stream up to this point.
     *
     * @param dedupLabels if labels are provided, then the scoped object's labels determine de-duplication. No labels implies current object.
     * @return the traversal with an appended DedupGlobalStep.
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Dedup Step</a>
     * @since 3.0.0-incubating
     */
    public GraphTraversal<S, E> dedup(final String... dedupLabels);

    ///**
    // * Filters the current object based on the object itself or the path history.
    // *
    // * @param startKey  the key containing the object to filter
    // * @param predicate the filter to apply
    // * @return the traversal with an appended {@link WherePredicateStep}
    // * @see <a href="http://tinkerpop.apache.org/docs/${project.version}/reference/#where-step" target="_blank">Reference Documentation - Where Step</a>
    // * @see <a href="http://tinkerpop.apache.org/docs/${project.version}/reference/#using-where-with-match" target="_blank">Reference Documentation - Where with Match</a>
    // * @see <a href="http://tinkerpop.apache.org/docs/${project.version}/reference/#using-where-with-select" target="_blank">Reference Documentation - Where with Select</a>
    // * @since 3.0.0-incubating
    // */
    //public GraphTraversal<S, E> where(final String startKey, final P<String> predicate) {
    //    this.asAdmin().getBytecode().addStep(Symbols.where, startKey, predicate);
    //    return this.asAdmin().addStep(new WherePredicateStep<>(this.asAdmin(), Optional.ofNullable(startKey), predicate));
    //}

    ///**
    // * Filters the current object based on the object itself or the path history.
    // *
    // * @param predicate the filter to apply
    // * @return the traversal with an appended {@link WherePredicateStep}
    // * @see <a href="http://tinkerpop.apache.org/docs/${project.version}/reference/#where-step" target="_blank">Reference Documentation - Where Step</a>
    // * @see <a href="http://tinkerpop.apache.org/docs/${project.version}/reference/#using-where-with-match" target="_blank">Reference Documentation - Where with Match</a>
    // * @see <a href="http://tinkerpop.apache.org/docs/${project.version}/reference/#using-where-with-select" target="_blank">Reference Documentation - Where with Select</a>
    // * @since 3.0.0-incubating
    // */
    //public GraphTraversal<S, E> where(final P<String> predicate) {
    //    this.asAdmin().getBytecode().addStep(Symbols.where, predicate);
    //    return this.asAdmin().addStep(new WherePredicateStep<>(this.asAdmin(), Optional.empty(), predicate));
    //}

    /**
     * Filters the current object based on the object itself or the path history.
     *
     * @param whereTraversal the filter to apply
     * @return the traversal with an appended WherePredicateStep
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Where Step</a>
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Where with Match</a>
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Where with Select</a>
     * @since 3.0.0-incubating
     */
    public GraphTraversal<S, E> where(final Traversal<?, ?> whereTraversal);

    ///**
    // * Filters vertices, edges and vertex properties based on their properties.
    // *
    // * @param propertyKey the key of the property to filter on
    // * @param predicate   the filter to apply to the key's value
    // * @return the traversal with an appended {@link HasStep}
    // * @see <a href="http://tinkerpop.apache.org/docs/${project.version}/reference/#has-step" target="_blank">Reference Documentation - Has Step</a>
    // * @since 3.0.0-incubating
    // */
    //public GraphTraversal<S, E> has(final String propertyKey, final P<?> predicate) {
    //    this.asAdmin().getBytecode().addStep(Symbols.has, propertyKey, predicate);
    //    return TraversalHelper.addHasContainer(this.asAdmin(), new HasContainer(propertyKey, predicate));
    //}

    ///**
    // * Filters vertices, edges and vertex properties based on their properties.
    // *
    // * @param accessor  the {@link T} accessor of the property to filter on
    // * @param predicate the filter to apply to the key's value
    // * @return the traversal with an appended {@link HasStep}
    // * @see <a href="http://tinkerpop.apache.org/docs/${project.version}/reference/#has-step" target="_blank">Reference Documentation - Has Step</a>
    // * @since 3.0.0-incubating
    // */
    //public GraphTraversal<S, E> has(final T accessor, final P<?> predicate) {
    //    this.asAdmin().getBytecode().addStep(Symbols.has, accessor, predicate);
    //    return TraversalHelper.addHasContainer(this.asAdmin(), new HasContainer(accessor.getAccessor(), predicate));
    //}

    /**
     * Filters vertices, edges and vertex properties based on their properties.
     *
     * @param propertyKey the key of the property to filter on
     * @param value       the value to compare the property value to for equality
     * @return the traversal with an appended HasStep
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Has Step</a>
     * @since 3.0.0-incubating
     */
    public GraphTraversal<S, E> has(final String propertyKey, final Object value);

    /**
     * Filters vertices, edges and vertex properties based on their properties.
     *
     * @param accessor the {@link T} accessor of the property to filter on
     * @param value    the value to compare the accessor value to for equality
     * @return the traversal with an appended HasStep
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Has Step</a>
     * @since 3.0.0-incubating
     */
    public GraphTraversal<S, E> has(final T accessor, final Object value);

    ///**
    // * Filters vertices, edges and vertex properties based on their properties.
    // *
    // * @param label       the label of the {@link Element}
    // * @param propertyKey the key of the property to filter on
    // * @param predicate   the filter to apply to the key's value
    // * @return the traversal with an appended {@link HasStep}
    // * @see <a href="http://tinkerpop.apache.org/docs/${project.version}/reference/#has-step" target="_blank">Reference Documentation - Has Step</a>
    // * @since 3.0.0-incubating
    // */
    //public GraphTraversal<S, E> has(final String label, final String propertyKey, final P<?> predicate) {
    //    this.asAdmin().getBytecode().addStep(Symbols.has, label, propertyKey, predicate);
    //    TraversalHelper.addHasContainer(this.asAdmin(), new HasContainer(T.label.getAccessor(), P.eq(label)));
    //    return TraversalHelper.addHasContainer(this.asAdmin(), new HasContainer(propertyKey, predicate));
    //}

    /**
     * Filters vertices, edges and vertex properties based on their properties.
     *
     * @param label       the label of the {@link Element}
     * @param propertyKey the key of the property to filter on
     * @param value       the value to compare the accessor value to for equality
     * @return the traversal with an appended HasStep
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Has Step</a>
     * @since 3.0.0-incubating
     */
    public GraphTraversal<S, E> has(final String label, final String propertyKey, final Object value);

    /**
     * Filters vertices, edges and vertex properties based on their value of {@link T} where only {@link T#id} and
     * {@link T#label} are supported.
     *
     * @param accessor          the {@link T} accessor of the property to filter on
     * @param propertyTraversal the traversal to filter the accessor value by
     * @return the traversal with an appended HasStep
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Has Step</a>
     * @since 3.1.0-incubating
     */
    public GraphTraversal<S, E> has(final T accessor, final Traversal<?, ?> propertyTraversal);

    /**
     * Filters vertices, edges and vertex properties based on the value of the specified property key.
     *
     * @param propertyKey       the key of the property to filter on
     * @param propertyTraversal the traversal to filter the property value by
     * @return the traversal with an appended HasStep
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Has Step</a>
     * @since 3.0.0-incubating
     */
    public GraphTraversal<S, E> has(final String propertyKey, final Traversal<?, ?> propertyTraversal);

    /**
     * Filters vertices, edges and vertex properties based on the existence of properties.
     *
     * @param propertyKey the key of the property to filter on for existence
     * @return the traversal with an appended HasStep
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Has Step</a>
     * @since 3.0.0-incubating
     */
    public GraphTraversal<S, E> has(final String propertyKey);

    /**
     * Filters vertices, edges and vertex properties based on the non-existence of properties.
     *
     * @param propertyKey the key of the property to filter on for existence
     * @return the traversal with an appended HasStep
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Has Step</a>
     * @since 3.0.0-incubating
     */
    public GraphTraversal<S, E> hasNot(final String propertyKey);

    /**
     * Filters vertices, edges and vertex properties based on their label.
     *
     * @param label       the label of the {@link Element}
     * @param otherLabels additional labels of the {@link Element}
     * @return the traversal with an appended HasStep
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Has Step</a>
     * @since 3.2.2
     */
    public GraphTraversal<S, E> hasLabel(final String label, final String... otherLabels);

    ///**
    // * Filters vertices, edges and vertex properties based on their label.
    // *
    // * @param predicate the filter to apply to the label of the {@link Element}
    // * @return the traversal with an appended {@link HasStep}
    // * @see <a href="http://tinkerpop.apache.org/docs/${project.version}/reference/#has-step" target="_blank">Reference Documentation - Has Step</a>
    // * @since 3.2.4
    // */
    //public GraphTraversal<S, E> hasLabel(final P<String> predicate) {
    //    this.asAdmin().getBytecode().addStep(Symbols.hasLabel, predicate);
    //    return TraversalHelper.addHasContainer(this.asAdmin(), new HasContainer(T.label.getAccessor(), predicate));
    //}

    /**
     * Filters vertices, edges and vertex properties based on their identifier.
     *
     * @param id       the identifier of the {@link Element}
     * @param otherIds additional identifiers of the {@link Element}
     * @return the traversal with an appended HasStep
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Has Step</a>
     * @since 3.2.2
     */
    public GraphTraversal<S, E> hasId(final Object id, final Object... otherIds);

    ///**
    // * Filters vertices, edges and vertex properties based on their identifier.
    // *
    // * @param predicate the filter to apply to the identifier of the {@link Element}
    // * @return the traversal with an appended {@link HasStep}
    // * @see <a href="http://tinkerpop.apache.org/docs/${project.version}/reference/#has-step" target="_blank">Reference Documentation - Has Step</a>
    // * @since 3.2.4
    // */
    //public GraphTraversal<S, E> hasId(final P<Object> predicate) {
    //    this.asAdmin().getBytecode().addStep(Symbols.hasId, predicate);
    //    return TraversalHelper.addHasContainer(this.asAdmin(), new HasContainer(T.id.getAccessor(), predicate));
    //}

    /**
     * Filters vertices, edges and vertex properties based on their key.
     *
     * @param label       the key of the {@link Element}
     * @param otherLabels additional key of the {@link Element}
     * @return the traversal with an appended HasStep
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Has Step</a>
     * @since 3.2.2
     */
    public GraphTraversal<S, E> hasKey(final String label, final String... otherLabels);

    ///**
    // * Filters vertices, edges and vertex properties based on their key.
    // *
    // * @param predicate the filter to apply to the key of the {@link Element}
    // * @return the traversal with an appended {@link HasStep}
    // * @see <a href="http://tinkerpop.apache.org/docs/${project.version}/reference/#has-step" target="_blank">Reference Documentation - Has Step</a>
    // * @since 3.2.4
    // */
    //public GraphTraversal<S, E> hasKey(final P<String> predicate) {
    //    this.asAdmin().getBytecode().addStep(Symbols.hasKey, predicate);
    //    return TraversalHelper.addHasContainer(this.asAdmin(), new HasContainer(T.key.getAccessor(), predicate));
    //}

    /**
     * Filters vertices, edges and vertex properties based on their value.
     *
     * @param value       the value of the {@link Element}
     * @param otherValues additional values of the {@link Element}
     * @return the traversal with an appended HasStep
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Has Step</a>
     */
    public GraphTraversal<S, E> hasValue(final Object value, final Object... otherValues);

    ///**
    // * Filters vertices, edges and vertex properties based on their value.
    // *
    // * @param predicate the filter to apply to the value of the {@link Element}
    // * @return the traversal with an appended {@link HasStep}
    // * @see <a href="http://tinkerpop.apache.org/docs/${project.version}/reference/#has-step" target="_blank">Reference Documentation - Has Step</a>
    // * @since 3.2.4
    // */
    //public GraphTraversal<S, E> hasValue(final P<Object> predicate) {
    //    this.asAdmin().getBytecode().addStep(Symbols.hasValue, predicate);
    //    return TraversalHelper.addHasContainer(this.asAdmin(), new HasContainer(T.value.getAccessor(), predicate));
    //}

    ///**
    // * Filters <code>E</code> object values given the provided {@code predicate}.
    // *
    // * @param predicate the filter to apply
    // * @return the traversal with an appended {@link IsStep}
    // * @see <a href="http://tinkerpop.apache.org/docs/${project.version}/reference/#is-step" target="_blank">Reference Documentation - Is Step</a>
    // * @since 3.0.0-incubating
    // */
    //public GraphTraversal<S, E> is(final P<E> predicate) {
    //    this.asAdmin().getBytecode().addStep(Symbols.is, predicate);
    //    return this.asAdmin().addStep(new IsStep<>(this.asAdmin(), predicate));
    //}

    /**
     * Filter the <code>E</code> object if it is not P#eq to the provided value.
     *
     * @param value the value that the object must equal.
     * @return the traversal with an appended IsStep.
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Is Step</a>
     * @since 3.0.0-incubating
     */
    public GraphTraversal<S, E> is(final Object value);

    /**
     * Removes objects from the traversal stream when the traversal provided as an argument does not return any objects.
     *
     * @param notTraversal the traversal to filter by.
     * @return the traversal with an appended NotStep.
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Not Step</a>
     * @since 3.0.0-incubating
     */
    public GraphTraversal<S, E> not(final Traversal<?, ?> notTraversal);

    /**
     * Filter the <code>E</code> object given a biased coin toss.
     *
     * @param probability the probability that the object will pass through
     * @return the traversal with an appended CoinStep.
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Coin Step</a>
     * @since 3.0.0-incubating
     */
    public GraphTraversal<S, E> coin(final double probability);

    /**
     * Filter the objects in the traversal by the number of them to pass through the stream. Those before the value
     * of {@code low} do not pass through and those that exceed the value of {@code high} will end the iteration.
     *
     * @param low  the number at which to start allowing objects through the stream
     * @param high the number at which to end the stream - use {@code -1} to emit all remaining objects
     * @return the traversal with an appended RangeGlobalStep
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Range Step</a>
     * @since 3.0.0-incubating
     */
    public GraphTraversal<S, E> range(final long low, final long high);

    /**
     * Filter the objects in the traversal by the number of them to pass through the stream as constrained by the
     * {@link Scope}. Those before the value of {@code low} do not pass through and those that exceed the value of
     * {@code high} will end the iteration.
     *
     * @param <E2>  the type of the objects projected
     * @param scope the scope of how to apply the {@code range}
     * @param low   the number at which to start allowing objects through the stream
     * @param high  the number at which to end the stream - use {@code -1} to emit all remaining objects
     * @return the traversal with an appended RangeGlobalStep or RangeLocalStep depending on {@code scope}
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Range Step</a>
     * @since 3.0.0-incubating
     */
    public <E2> GraphTraversal<S, E2> range(final Scope scope, final long low, final long high);

    /**
     * Filter the objects in the traversal by the number of them to pass through the stream, where only the first
     * {@code n} objects are allowed as defined by the {@code limit} argument.
     *
     * @param limit the number at which to end the stream
     * @return the traversal with an appended RangeGlobalStep
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Limit Step</a>
     * @since 3.0.0-incubating
     */
    public GraphTraversal<S, E> limit(final long limit);

    /**
     * Filter the objects in the traversal by the number of them to pass through the stream given the {@link Scope},
     * where only the first {@code n} objects are allowed as defined by the {@code limit} argument.
     *
     * @param <E2>  the type of the objects projected
     * @param scope the scope of how to apply the {@code limit}
     * @param limit the number at which to end the stream
     * @return the traversal with an appended RangeGlobalStep or RangeLocalStep depending on {@code scope}
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Limit Step</a>
     * @since 3.0.0-incubating
     */
    public <E2> GraphTraversal<S, E2> limit(final Scope scope, final long limit);

    /**
     * Filters the objects in the traversal emitted as being last objects in the stream. In this case, only the last
     * object will be returned.
     *
     * @return the traversal with an appended TailGlobalStep
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Tail Step</a>
     * @since 3.0.0-incubating
     */
    public GraphTraversal<S, E> tail();

    /**
     * Filters the objects in the traversal emitted as being last objects in the stream. In this case, only the last
     * {@code n} objects will be returned as defined by the {@code limit}.
     *
     * @param limit the number at which to end the stream
     * @return the traversal with an appended ailGlobalStep
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Tail Step</a>
     * @since 3.0.0-incubating
     */
    public GraphTraversal<S, E> tail(final long limit);

    /**
     * Filters the objects in the traversal emitted as being last objects in the stream given the {@link Scope}. In
     * this case, only the last object in the stream will be returned.
     *
     * @param <E2>  the type of the objects projected
     * @param scope the scope of how to apply the {@code tail}
     * @return the traversal with an appended TailGlobalStep or TailLocalStep depending on {@code scope}
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Tail Step</a>
     * @since 3.0.0-incubating
     */
    public <E2> GraphTraversal<S, E2> tail(final Scope scope);

    /**
     * Filters the objects in the traversal emitted as being last objects in the stream given the {@link Scope}. In
     * this case, only the last {@code n} objects will be returned as defined by the {@code limit}.
     *
     * @param <E2>  the type of the objects projected
     * @param scope the scope of how to apply the {@code tail}
     * @param limit the number at which to end the stream
     * @return the traversal with an appended TailGlobalStep or TailLocalStep epending on {@code scope}
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Tail Step</a>
     * @since 3.0.0-incubating
     */
    public <E2> GraphTraversal<S, E2> tail(final Scope scope, final long limit);

    /**
     * Filters out the first {@code n} objects in the traversal.
     *
     * @param skip the number of objects to skip
     * @return the traversal with an appended RangeGlobalStep
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Skip Step</a>
     * @since 3.3.0
     */
    public GraphTraversal<S, E> skip(final long skip);

    /**
     * Filters out the first {@code n} objects in the traversal.
     *
     * @param <E2>  the type of the objects projected
     * @param scope the scope of how to apply the {@code tail}
     * @param skip  the number of objects to skip
     * @return the traversal with an appended RangeGlobalStep or RangeLocalStep depending on {@code scope}
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Skip Step</a>
     * @since 3.3.0
     */
    public <E2> GraphTraversal<S, E2> skip(final Scope scope, final long skip);

    /**
     * Once the first Traverser hits this step, a count down is started. Once the time limit is up, all remaining traversers are filtered out.
     *
     * @param timeLimit the count down time
     * @return the traversal with an appended TimeLimitStep
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - TimeLimit Step</a>
     * @since 3.0.0-incubating
     */
    public GraphTraversal<S, E> timeLimit(final long timeLimit);

    /**
     * Filter the <code>E</code> object if its Traverser#path is not {@link Path#isSimple}.
     *
     * @return the traversal with an appended PathFilterStep.
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - SimplePath Step</a>
     * @since 3.0.0-incubating
     */
    public GraphTraversal<S, E> simplePath();

    /**
     * Filter the <code>E</code> object if its Traverser#path is {@link Path#isSimple}.
     *
     * @return the traversal with an appended PathFilterStep.
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - CyclicPath Step</a>
     * @since 3.0.0-incubating
     */
    public GraphTraversal<S, E> cyclicPath();

    /**
     * Allow some specified number of objects to pass through the stream.
     *
     * @param amountToSample the number of objects to allow
     * @return the traversal with an appended SampleGlobalStep
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Sample Step</a>
     * @since 3.0.0-incubating
     */
    public GraphTraversal<S, E> sample(final int amountToSample);

    /**
     * Allow some specified number of objects to pass through the stream.
     *
     * @param scope          the scope of how to apply the {@code sample}
     * @param amountToSample the number of objects to allow
     * @return the traversal with an appended SampleGlobalStep or SampleLocalStep depending on the {@code scope}
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Sample Step</a>
     * @since 3.0.0-incubating
     */
    public GraphTraversal<S, E> sample(final Scope scope, final int amountToSample);

    /**
     * Removes elements and properties from the graph. This step is not a terminating, in the sense that it does not
     * automatically iterate the traversal. It is therefore necessary to do some form of iteration for the removal
     * to actually take place. In most cases, iteration is best accomplished with {@code g.V().drop().iterate()}.
     *
     * @return the traversal with the DropStep added
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Drop Step</a>
     * @since 3.0.0-incubating
     */
    public GraphTraversal<S, E> drop();

    ///////////////////// SIDE-EFFECT STEPS /////////////////////

    ///**
    // * Perform some operation on the {@link Traverser} and pass it to the next step unmodified.
    // *
    // * @param consumer the operation to perform at this step in relation to the {@link Traverser}
    // * @return the traversal with an appended {@link LambdaSideEffectStep}
    // * @see <a href="http://tinkerpop.apache.org/docs/${project.version}/reference/#general-steps" target="_blank">Reference Documentation - General Steps</a>
    // * @since 3.0.0-incubating
    // */
    //public GraphTraversal<S, E> sideEffect(final Consumer<Traverser<E>> consumer) {
    //    this.asAdmin().getBytecode().addStep(Symbols.sideEffect, consumer);
    //    return this.asAdmin().addStep(new LambdaSideEffectStep<>(this.asAdmin(), consumer));
    //}

    /**
     * Perform some operation on the Traverser and pass it to the next step unmodified.
     *
     * @param sideEffectTraversal the operation to perform at this step in relation to the Traverser
     * @return the traversal with an appended TraversalSideEffectStep
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - General Steps</a>
     * @since 3.0.0-incubating
     */
    public GraphTraversal<S, E> sideEffect(final Traversal<?, ?> sideEffectTraversal);

    /**
     * Iterates the traversal up to the itself and emits the side-effect referenced by the key. If multiple keys are
     * supplied then the side-effects are emitted as a {@code Map}.
     *
     * @param <E2>  the type of the objects projected
     * @param sideEffectKey  the side-effect to emit
     * @param sideEffectKeys other side-effects to emit
     * @return the traversal with an appended SideEffectCapStep
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Cap Step</a>
     * @since 3.0.0-incubating
     */
    public <E2> GraphTraversal<S, E2> cap(final String sideEffectKey, final String... sideEffectKeys);

    /**
     * Extracts a portion of the graph being traversed into a {@link Graph} object held in the specified side-effect
     * key.
     *
     * @param sideEffectKey the name of the side-effect key that will hold the subgraph
     * @return the traversal with an appended SubgraphStep
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Subgraph Step</a>
     * @since 3.0.0-incubating
     */
    public GraphTraversal<S, Edge> subgraph(final String sideEffectKey);

    /**
     * Eagerly collects objects up to this step into a side-effect. Same as calling {@link #aggregate(Scope, String)}
     * with a {@link Scope#local}.
     *
     * @param sideEffectKey the name of the side-effect key that will hold the aggregated objects
     * @return the traversal with an appended AggregateGlobalStep
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Aggregate Step</a>
     * @since 3.0.0-incubating
     */
    public GraphTraversal<S, E> aggregate(final String sideEffectKey);

    /**
     * Collects objects in a list using the {@link Scope} argument to determine whether it should be lazy
     * {@link Scope#local} or eager ({@link Scope#global} while gathering those objects.
     *
     * @param sideEffectKey the name of the side-effect key that will hold the aggregated objects
     * @param scope Scope
     * @return the traversal with an appended AggregateGlobalStep
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Aggregate Step</a>
     * @since 3.4.3
     */
    public GraphTraversal<S, E> aggregate(final Scope scope, final String sideEffectKey);

    /**
     * Organize objects in the stream into a {@code Map}. Calls to {@code group()} are typically accompanied with
     * {@link #by()} modulators which help specify how the grouping should occur.
     *
     * @param sideEffectKey the name of the side-effect key that will hold the aggregated grouping
     * @return the traversal with an appended GroupStep.
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Group Step</a>
     * @since 3.0.0-incubating
     */
    public GraphTraversal<S, E> group(final String sideEffectKey);

    /**
     * Counts the number of times a particular objects has been part of a traversal, returning a {@code Map} where the
     * object is the key and the value is the count.
     *
     * @param sideEffectKey the name of the side-effect key that will hold the aggregated grouping
     * @return the traversal with an appended GroupCountStep.
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - GroupCount Step</a>
     * @since 3.0.0-incubating
     */
    public GraphTraversal<S, E> groupCount(final String sideEffectKey);

    /**
     * Aggregates the emanating paths into a Tree data structure.
     *
     * @param sideEffectKey the name of the side-effect key that will hold the tree
     * @return the traversal with an appended TreeStep
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Tree Step</a>
     * @since 3.0.0-incubating
     */
    public GraphTraversal<S, E> tree(final String sideEffectKey);

    /**
     * Map the Traverser to its Traverser#sack value.
     *
     * @param sackOperator the operator to apply to the sack value
     * @param <V> type
     * @param <U> type
     * @return the traversal with an appended SackStep.
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Sack Step</a>
     * @since 3.0.0-incubating
     */
    public <V, U> GraphTraversal<S, E> sack(final BiFunction<V, U, V> sackOperator);

    /**
     * Lazily aggregates objects in the stream into a side-effect collection.
     *
     * @param sideEffectKey the name of the side-effect key that will hold the aggregate
     * @return the traversal with an appended AggregateLocalStep
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Store Step</a>
     * @since 3.0.0-incubating
     * @deprecated As of release 3.4.3, replaced by {@link #aggregate(Scope, String)} using {@link Scope#local}.
     */
    @Deprecated
    public GraphTraversal<S, E> store(final String sideEffectKey);

    /**
     * Allows developers to examine statistical information about a traversal providing data like execution times,
     * counts, etc.
     *
     * @param sideEffectKey the name of the side-effect key within which to hold the profile object
     * @return the traversal with an appended ProfileSideEffectStep
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Profile Step</a>
     * @since 3.2.0-incubating
     */
    public GraphTraversal<S, E> profile(final String sideEffectKey);

    /**
     * Allows developers to examine statistical information about a traversal providing data like execution times,
     * counts, etc.
     *
     * @return the traversal with an appended ProfileSideEffectStep
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Profile Step</a>
     * @since 3.0.0-incubating
     */
    @Override
    public default GraphTraversal<S, Object> profile() {
        throw new NotSupportedException("method is not supported");
    }

    /**
     * Filter all traversers in the traversal. This step has narrow use cases and is primarily intended for use as a
     * signal to remote servers that {@link #iterate()} was called. While it may be directly used, it is often a sign
     * that a traversal should be re-written in another form.
     *
     * @return the updated traversal with respective NoneStep.
     */
    @Override
    default GraphTraversal<S, E> none() {
        throw new NotSupportedException("method is not supported");
    }

    /**
     * Sets a {@link Property} value and related meta properties if supplied, if supported by the {@link Graph}
     * and if the {@link Element} is a {@link VertexProperty}.  This method is the long-hand version of
     * {@link #property(Object, Object, Object...)} with the difference that the {@link VertexProperty.Cardinality}
     * can be supplied.
     * Generally speaking, this method will append an AddPropertyStep to the {@link Traversal} but when
     * possible, this method will attempt to fold key/value pairs into an AddVertexStep, AddEdgeStep or
     * AddVertexStartStep.  This potential optimization can only happen if cardinality is not supplied
     * and when meta-properties are not included.
     *
     * @param cardinality the specified cardinality of the property where {@code null} will allow the {@link Graph}
     *                    to use its default settings
     * @param key         the key for the property
     * @param value       the value for the property
     * @param keyValues   any meta properties to be assigned to this property
     * @return the traversal with the last step modified to add a property
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">AddProperty Step</a>
     * @since 3.0.0-incubating
     */
    public GraphTraversal<S, E> property(final VertexProperty.Cardinality cardinality, final Object key, final Object value, final Object... keyValues);

    /**
     * Sets the key and value of a {@link Property}. If the {@link Element} is a {@link VertexProperty} and the
     * {@link Graph} supports it, meta properties can be set.  Use of this method assumes that the
     * {@link VertexProperty.Cardinality} is defaulted to {@code null} which  means that the default cardinality for
     * the {@link Graph} will be used.
     * This method is effectively calls {@link #property(VertexProperty.Cardinality, Object, Object, Object...)}
     * as {@code property(null, key, value, keyValues}.
     *
     * @param key       the key for the property
     * @param value     the value for the property
     * @param keyValues any meta properties to be assigned to this property
     * @return the traversal with the last step modified to add a property
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">AddProperty Step</a>
     * @since 3.0.0-incubating
     */
    public GraphTraversal<S, E> property(final Object key, final Object value, final Object... keyValues);

    ///////////////////// BRANCH STEPS /////////////////////

    /**
     * Split the Traverser to all the specified traversals.
     *
     * @param <E2>  the type of the objects projected
     * @param <M> type
     * @param branchTraversal the traversal to branch the Traverser to
     * @return the {@link Traversal} with the BranchStep}added
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - General Steps</a>
     * @since 3.0.0-incubating
     */
    public <M, E2> GraphTraversal<S, E2> branch(final Traversal<?, M> branchTraversal);

    ///**
    // * Split the {@link Traverser} to all the specified functions.
    // *
    // * @param function the traversal to branch the {@link Traverser} to
    // * @return the {@link Traversal} with the {@link BranchStep} added
    // * @see <a href="http://tinkerpop.apache.org/docs/${project.version}/reference/#general-steps" target="_blank">Reference Documentation - General Steps</a>
    // * @since 3.0.0-incubating
    // */
    //public <M, E2> GraphTraversal<S, E2> branch(final Function<Traverser<E>, M> function) {
    //    this.asAdmin().getBytecode().addStep(Symbols.branch, function);
    //    final BranchStep<E, E2, M> branchStep = new BranchStep<>(this.asAdmin());
    //    branchStep.setBranchTraversal((Traversal.Admin<E, M>) __.map(function));
    //    return this.asAdmin().addStep(branchStep);
    //}

    /**
     * Routes the current traverser to a particular traversal branch option which allows the creation of if-then-else
     * like semantics within a traversal. A {@code choose} is modified by {@link #option} which provides the various
     * branch choices.
     *
     * @param <E2>  the type of the objects projected
     * @param <M> type
     * @param choiceTraversal the traversal used to determine the value for the branch
     * @return the traversal with the appended ChooseStep
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Choose Step</a>
     * @since 3.0.0-incubating
     */
    public <M, E2> GraphTraversal<S, E2> choose(final Traversal<?, M> choiceTraversal);

    /**
     * Routes the current traverser to a particular traversal branch option which allows the creation of if-then-else
     * like semantics within a traversal.
     *
     * @param <E2>  the type of the objects projected
     * @param traversalPredicate the traversal used to determine the "if" portion of the if-then-else
     * @param trueChoice         the traversal to execute in the event the {@code traversalPredicate} returns true
     * @param falseChoice        the traversal to execute in the event the {@code traversalPredicate} returns false
     * @return the traversal with the appended ChooseStep
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Choose Step</a>
     * @since 3.0.0-incubating
     */
    public <E2> GraphTraversal<S, E2> choose(final Traversal<?, ?> traversalPredicate,
                                                     final Traversal<?, E2> trueChoice, final Traversal<?, E2> falseChoice);

    /**
     * Routes the current traverser to a particular traversal branch option which allows the creation of if-then
     * like semantics within a traversal.
     *
     * @param <E2>  the type of the objects projected
     * @param traversalPredicate the traversal used to determine the "if" portion of the if-then-else
     * @param trueChoice         the traversal to execute in the event the {@code traversalPredicate} returns true
     * @return the traversal with the appended ChooseStep
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Choose Step</a>
     * @since 3.2.4
     */
    public <E2> GraphTraversal<S, E2> choose(final Traversal<?, ?> traversalPredicate,
                                                     final Traversal<?, E2> trueChoice);

    /**
     * Routes the current traverser to a particular traversal branch option which allows the creation of if-then-else
     * like semantics within a traversal. A {@code choose} is modified by {@link #option} which provides the various
     * branch choices.
     *
     * @param <E2>  the type of the objects projected
     * @param <M> type
     * @param choiceFunction the function used to determine the value for the branch
     * @return the traversal with the appended ChooseStep
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Choose Step</a>
     * @since 3.0.0-incubating
     */
    public <M, E2> GraphTraversal<S, E2> choose(final Function<E, M> choiceFunction);

    /**
     * Routes the current traverser to a particular traversal branch option which allows the creation of if-then-else
     * like semantics within a traversal.
     *
     * @param <E2>  the type of the objects projected
     * @param choosePredicate the function used to determine the "if" portion of the if-then-else
     * @param trueChoice      the traversal to execute in the event the {@code traversalPredicate} returns true
     * @param falseChoice     the traversal to execute in the event the {@code traversalPredicate} returns false
     * @return the traversal with the appended ChooseStep
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Choose Step</a>
     * @since 3.0.0-incubating
     */
    public <E2> GraphTraversal<S, E2> choose(final Predicate<E> choosePredicate,
                                                     final Traversal<?, E2> trueChoice, final Traversal<?, E2> falseChoice);

    /**
     * Routes the current traverser to a particular traversal branch option which allows the creation of if-then
     * like semantics within a traversal.
     *
     * @param <E2>  the type of the objects projected
     * @param choosePredicate the function used to determine the "if" portion of the if-then-else
     * @param trueChoice      the traversal to execute in the event the {@code traversalPredicate} returns true
     * @return the traversal with the appended ChooseStep
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Choose Step</a>
     * @since 3.2.4
     */
    public <E2> GraphTraversal<S, E2> choose(final Predicate<E> choosePredicate,
                                                     final Traversal<?, E2> trueChoice);

    /**
     * Returns the result of the specified traversal if it yields a result, otherwise it returns the calling element.
     *
     * @param <E2>  the type of the objects projected
     * @param optionalTraversal the traversal to execute for a potential result
     * @return the traversal with the appended  ChooseStep
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Optional Step</a>
     * @since 3.0.0-incubating
     */
    public <E2> GraphTraversal<S, E2> optional(final Traversal<?, E2> optionalTraversal);

    /**
     * Merges the results of an arbitrary number of traversals.
     *
     * @param <E2>  the type of the objects projected
     * @param unionTraversals the traversals to merge
     * @return the traversal with the appended UnionStep
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Union Step</a>
     * @since 3.0.0-incubating
     */
    public <E2> GraphTraversal<S, E2> union(final Traversal<?, E2>... unionTraversals);

    /**
     * Evaluates the provided traversals and returns the result of the first traversal to emit at least one object.
     *
     * @param <E2>  the type of the objects projected
     * @param coalesceTraversals the traversals to coalesce
     * @return the traversal with the appended CoalesceStep
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Coalesce Step</a>
     * @since 3.0.0-incubating
     */
    public <E2> GraphTraversal<S, E2> coalesce(final Traversal<?, E2>... coalesceTraversals);

    /**
     * This step is used for looping over a traversal given some break predicate.
     *
     * @param repeatTraversal the traversal to repeat over
     * @return the traversal with the appended RepeatStep
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Repeat Step</a>
     * @since 3.0.0-incubating
     */
    public GraphTraversal<S, E> repeat(final Traversal<?, E> repeatTraversal);

    /**
     * This step is used for looping over a traversal given some break predicate and with a specified loop name.
     *
     * @param repeatTraversal the traversal to repeat over
     * @param loopName The name given to the loop
     * @return the traversal with the appended RepeatStep
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Repeat Step</a>
     * @since 3.4.0
     */
    public GraphTraversal<S, E> repeat(final String loopName, final Traversal<?, E> repeatTraversal);


    /**
     * Emit is used in conjunction with {@link #repeat(Traversal)} to determine what objects get emit from the loop.
     *
     * @param emitTraversal the emit predicate defined as a traversal
     * @return the traversal with the appended RepeatStep
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Repeat Step</a>
     * @since 3.0.0-incubating
     */
    public GraphTraversal<S, E> emit(final Traversal<?, ?> emitTraversal);

    ///**
    // * Emit is used in conjunction with {@link #repeat(Traversal)} to determine what objects get emit from the loop.
    // *
    // * @param emitPredicate the emit predicate
    // * @return the traversal with the appended {@link RepeatStep}
    // * @see <a href="http://tinkerpop.apache.org/docs/${project.version}/reference/#repeat-step" target="_blank">Reference Documentation - Repeat Step</a>
    // * @since 3.0.0-incubating
    // */
    //public GraphTraversal<S, E> emit(final Predicate<Traverser<E>> emitPredicate) {
    //    this.asAdmin().getBytecode().addStep(Symbols.emit, emitPredicate);
    //    return RepeatStep.addEmitToTraversal(this, (Traversal.Admin<E, ?>) __.filter(emitPredicate));
    //}

    /**
     * Emit is used in conjunction with {@link #repeat(Traversal)} to emit all objects from the loop.
     *
     * @return the traversal with the appended RepeatStep
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Repeat Step</a>
     * @since 3.0.0-incubating
     */
    public GraphTraversal<S, E> emit();

    /**
     * Modifies a {@link #repeat(Traversal)} to determine when the loop should exit.
     *
     * @param untilTraversal the traversal that determines when the loop exits
     * @return the traversal with the appended RepeatStep
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Repeat Step</a>
     * @since 3.0.0-incubating
     */
    public GraphTraversal<S, E> until(final Traversal<?, ?> untilTraversal);

    ///**
    // * Modifies a {@link #repeat(Traversal)} to determine when the loop should exit.
    // *
    // * @param untilPredicate the predicate that determines when the loop exits
    // * @return the traversal with the appended {@link RepeatStep}
    // * @see <a href="http://tinkerpop.apache.org/docs/${project.version}/reference/#repeat-step" target="_blank">Reference Documentation - Repeat Step</a>
    // * @since 3.0.0-incubating
    // */
    //public GraphTraversal<S, E> until(final Predicate<Traverser<E>> untilPredicate) {
    //    this.asAdmin().getBytecode().addStep(Symbols.until, untilPredicate);
    //    return RepeatStep.addUntilToTraversal(this, (Traversal.Admin<E, ?>) __.filter(untilPredicate));
    //}

    /**
     * Modifies a {@link #repeat(Traversal)} to specify how many loops should occur before exiting.
     *
     * @param maxLoops the number of loops to execute prior to exiting
     * @return the traversal with the appended RepeatStep
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Repeat Step</a>
     * @since 3.0.0-incubating
     */
    public GraphTraversal<S, E> times(final int maxLoops);

    /**
     * Provides a execute a specified traversal on a single element within a stream.
     *
     * @param <E2>  the type of the objects projected
     * @param localTraversal the traversal to execute locally
     * @return the traversal with the appended LocalStep
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Local Step</a>
     * @since 3.0.0-incubating
     */
    public <E2> GraphTraversal<S, E2> local(final Traversal<?, E2> localTraversal);

    /////////////////// VERTEX PROGRAM STEPS ////////////////

    /**
     * Calculates a PageRank over the graph using a 0.85 for the {@code alpha} value.
     *
     * @return the traversal with the appended PageRankVertexProgramStep
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - PageRank Step</a>
     * @since 3.2.0-incubating
     */
    public GraphTraversal<S, E> pageRank();

    /**
     * Calculates a PageRank over the graph.
     *
     * @param alpha double
     * @return the traversal with the appended PageRankVertexProgramStep
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - PageRank Step</a>
     * @since 3.2.0-incubating
     */
    public GraphTraversal<S, E> pageRank(final double alpha);

    /**
     * Executes a Peer Pressure community detection algorithm over the graph.
     *
     * @return the traversal with the appended PeerPressureVertexProgramStep
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - PeerPressure Step</a>
     * @since 3.2.0-incubating
     */
    public GraphTraversal<S, E> peerPressure();

    /**
     * Executes a Connected Component algorithm over the graph.
     *
     * @return the traversal with the appended ConnectedComponentVertexProgram
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - ConnectedComponent Step</a>
     * @since 3.4.0
     */
    public GraphTraversal<S, E> connectedComponent();

    /**
     * Executes a Shortest Path algorithm over the graph.
     *
     * @return the traversal with the appended ShortestPathVertexProgramStep
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - ShortestPath Step</a>
     */
    public GraphTraversal<S, Path> shortestPath();

    ///**
    // * Executes an arbitrary {@link VertexProgram} over the graph.
    // *
    // * @return the traversal with the appended {@link ProgramVertexProgramStep}
    // * @see <a href="http://tinkerpop.apache.org/docs/${project.version}/reference/#program-step" target="_blank">Reference Documentation - Program Step</a>
    // * @since 3.2.0-incubating
    // */
    //public GraphTraversal<S, E> program(final VertexProgram<?> vertexProgram) {
    //    return this.asAdmin().addStep((Step<E, E>) new ProgramVertexProgramStep(this.asAdmin(), vertexProgram));
    //}

    ///////////////////// UTILITY STEPS /////////////////////

    /**
     * A step modulator that provides a label to the step that can be accessed later in the traversal by other steps.
     *
     * @param stepLabel  the name of the step
     * @param stepLabels additional names for the label
     * @return the traversal with the modified end step
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - As Step</a>
     * @since 3.0.0-incubating
     */
    public GraphTraversal<S, E> as(final String stepLabel, final String... stepLabels);

    /**
     * Turns the lazy traversal pipeline into a bulk-synchronous pipeline which basically iterates that traversal to
     * the size of the barrier. In this case, it iterates the entire thing as the default barrier size is set to
     * {@code Integer.MAX_VALUE}.
     *
     * @return the traversal with an appended NoOpBarrierStep
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Barrier Step</a>
     * @since 3.0.0-incubating
     */
    public GraphTraversal<S, E> barrier();
    /**
     * Turns the lazy traversal pipeline into a bulk-synchronous pipeline which basically iterates that traversal to
     * the size of the barrier.
     *
     * @param maxBarrierSize the size of the barrier
     * @return the traversal with an appended NoOpBarrierStep
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Barrier Step</a>
     * @since 3.0.0-incubating
     */
    public GraphTraversal<S, E> barrier(final int maxBarrierSize);

    /**
     * Indexes all items of the current collection. The indexing format can be configured using the {@link GraphTraversal#with(String, Object)}
     * and org.apache.tinkerpop.gremlin.process.traversal.step.util.WithOptions#indexer.
     *
     * Indexed as list: ["a","b","c"] =&gt; [["a",0],["b",1],["c",2]]
     * Indexed as map:  ["a","b","c"] =&gt; {0:"a",1:"b",2:"c"}
     *
     * If the current object is not a collection, this step will map the object to a single item collection/map:
     *
     * Indexed as list: "a" =&gt; ["a",0]
     * Indexed as map:  "a" =&gt; {0:"a"}
     *
     * @param <E2> the type of the objects projected
     * @return the traversal with an appended IndexStep
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Index Step</a>
     * @since 3.4.0
     */
    public <E2> GraphTraversal<S, E2> index();

    ///**
    // * Turns the lazy traversal pipeline into a bulk-synchronous pipeline which basically iterates that traversal to
    // * the size of the barrier. In this case, it iterates the entire thing as the default barrier size is set to
    // * {@code Integer.MAX_VALUE}.
    // *
    // * @param barrierConsumer a consumer function that is applied to the objects aggregated to the barrier
    // * @return the traversal with an appended {@link NoOpBarrierStep}
    // * @see <a href="http://tinkerpop.apache.org/docs/${project.version}/reference/#barrier-step" target="_blank">Reference Documentation - Barrier Step</a>
    // * @since 3.2.0-incubating
    // */
    //public GraphTraversal<S, E> barrier(final Consumer<TraverserSet<Object>> barrierConsumer) {
    //    this.asAdmin().getBytecode().addStep(Symbols.barrier, barrierConsumer);
    //    return this.asAdmin().addStep(new LambdaCollectingBarrierStep<>(this.asAdmin(), (Consumer) barrierConsumer, Integer.MAX_VALUE));
    //}

    //// WITH-MODULATOR

    /**
     * Provides a configuration to a step in the form of a key which is the same as {@code with(key, true)}. The key
     * of the configuration must be step specific and therefore a configuration could be supplied that is not known to
     * be valid until execution.
     *
     * @param key the key of the configuration to apply to a step
     * @return the traversal with a modulated step
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - With Step</a>
     * @since 3.4.0
     */
    public GraphTraversal<S,E> with(final String key);

    /**
     * Provides a configuration to a step in the form of a key and value pair. The key of the configuration must be
     * step specific and therefore a configuration could be supplied that is not known to be valid until execution.
     *
     * @param key the key of the configuration to apply to a step
     * @param value the value of the configuration to apply to a step
     * @return the traversal with a modulated step
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - With Step</a>
     * @since 3.4.0
     */
    public GraphTraversal<S,E> with(final String key, final Object value);

    //// BY-MODULATORS

    /**
     * The {@code by()} can be applied to a number of different step to alter their behaviors. This form is essentially
     * an {@link #identity()} modulation.
     *
     * @return the traversal with a modulated step.
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - By Step</a>
     * @since 3.0.0-incubating
     */
    public GraphTraversal<S, E> by();

    /**
     * The {@code by()} can be applied to a number of different step to alter their behaviors. Modifies the previous
     * step with the specified traversal.
     *
     * @param traversal the traversal to apply
     * @return the traversal with a modulated step.
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - By Step</a>
     * @since 3.0.0-incubating
     */
    public GraphTraversal<S, E> by(final Traversal<?, ?> traversal);

    /**
     * The {@code by()} can be applied to a number of different step to alter their behaviors. Modifies the previous
     * step with the specified token of {@link T}.
     *
     * @param token the token to apply
     * @return the traversal with a modulated step.
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - By Step</a>
     * @since 3.0.0-incubating
     */
    public GraphTraversal<S, E> by(final T token);

    /**
     * The {@code by()} can be applied to a number of different step to alter their behaviors. Modifies the previous
     * step with the specified key.
     *
     * @param key the key to apply
     * @return the traversal with a modulated step.
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - By Step</a>
     * @since 3.0.0-incubating
     */
    public GraphTraversal<S, E> by(final String key);

    /**
     * The {@code by()} can be applied to a number of different step to alter their behaviors. Modifies the previous
     * step with the specified function.
     *
     * @param <V>      type
     * @param function the function to apply
     * @return the traversal with a modulated step.
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - By Step</a>
     * @since 3.0.0-incubating
     */
    public <V> GraphTraversal<S, E> by(final Function<V, Object> function);

    //// COMPARATOR BY-MODULATORS

    /**
     * The {@code by()} can be applied to a number of different step to alter their behaviors. Modifies the previous
     * step with the specified function.
     *
     * @param <V>        type
     * @param traversal  the traversal to apply
     * @param comparator the comparator to apply typically for some {@link #order()}
     * @return the traversal with a modulated step.
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - By Step</a>
     * @since 3.0.0-incubating
     */
    public <V> GraphTraversal<S, E> by(final Traversal<?, ?> traversal, final Comparator<V> comparator);

    /**
     * The {@code by()} can be applied to a number of different step to alter their behaviors. Modifies the previous
     * step with the specified function.
     *
     * @param comparator the comparator to apply typically for some {@link #order()}
     * @return the traversal with a modulated step.
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - By Step</a>
     * @since 3.0.0-incubating
     */
    public GraphTraversal<S, E> by(final Comparator<E> comparator);

    ///**
    // * The {@code by()} can be applied to a number of different step to alter their behaviors. Modifies the previous
    // * step with the specified function.
    // *
    // * @param order the comparator to apply typically for some {@link #order()}
    // * @return the traversal with a modulated step.
    // * @see <a href="http://tinkerpop.apache.org/docs/${project.version}/reference/#by-step" target="_blank">Reference Documentation - By Step</a>
    // * @since 3.0.0-incubating
    // */
    //public GraphTraversal<S, E> by(final Order order) {
    //    this.asAdmin().getBytecode().addStep(Symbols.by, order);
    //    ((ByModulating) this.asAdmin().getEndStep()).modulateBy(order);
    //    return this;
    //}

    /**
     * The {@code by()} can be applied to a number of different step to alter their behaviors. Modifies the previous
     * step with the specified function.
     *
     * @param <V>        type
     * @param key        the key to apply                                                                                                     traversal
     * @param comparator the comparator to apply typically for some {@link #order()}
     * @return the traversal with a modulated step.
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - By Step</a>
     * @since 3.0.0-incubating
     */
    public <V> GraphTraversal<S, E> by(final String key, final Comparator<V> comparator);

    /**
     * The {@code by()} can be applied to a number of different step to alter their behaviors. Modifies the previous
     * step with the specified function.
     *
     * @param <U>        type
     * @param function   the function to apply
     * @param comparator the comparator to apply typically for some {@link #order()}
     * @return the traversal with a modulated step.
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - By Step</a>
     * @since 3.0.0-incubating
     */
    public <U> GraphTraversal<S, E> by(final Function<U, Object> function, final Comparator comparator);

    ////

    /**
     * This step modifies {@link #choose(Function)} to specifies the available choices that might be executed.
     *
     * @param <M>             type
     * @param <E2>            the type of the objects projected
     * @param pickToken       the token that would trigger this option
     * @param traversalOption the option as a traversal
     * @return the traversal with the modulated step
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Choose Step</a>
     * @since 3.0.0-incubating
     */
    public <M, E2> GraphTraversal<S, E> option(final M pickToken, final Traversal<?, E2> traversalOption);

    /**
     * This step modifies {@link #choose(Function)} to specifies the available choices that might be executed.
     *
     * @param <E2>            the type of the objects projected
     * @param traversalOption the option as a traversal
     * @return the traversal with the modulated step
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Choose Step</a>
     * @since 3.0.0-incubating
     */
    public <E2> GraphTraversal<S, E> option(final Traversal<?, E2> traversalOption);

    ////

    ///////////////////// IO STEPS /////////////////////

    /**
     * This step is technically a step modulator for the the GraphTraversalSource#io(String) step which
     * instructs the step to perform a read with its given configuration.
     *
     * @return the traversal with the IoStep modulated to read
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - IO Step</a>
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Read Step</a>
     * @since 3.4.0
     */
    public GraphTraversal<S,E> read();

    /**
     * This step is technically a step modulator for the the GraphTraversalSource#io(String) step which
     * instructs the step to perform a write with its given configuration.
     *
     * @return the traversal with the IoStep modulated to write
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - IO Step</a>
     * @see <a href="http://tinkerpop.apache.org/docs/" target="_blank">Reference Documentation - Write Step</a>
     * @since 3.4.0
     */
    public GraphTraversal<S,E> write();

    /**
     * Iterates the traversal presumably for the generation of side-effects.
     */
    @Override
    public GraphTraversal<S, E> iterate();

    ////

    public static final class Symbols {

        private Symbols() {
            // static fields only
        }

        public static final String map = "map";
        public static final String flatMap = "flatMap";
        public static final String id = "id";
        public static final String label = "label";
        public static final String identity = "identity";
        public static final String constant = "constant";
        public static final String V = "V";
        public static final String E = "E";
        public static final String to = "to";
        public static final String out = "out";
        public static final String in = "in";
        public static final String both = "both";
        public static final String toE = "toE";
        public static final String outE = "outE";
        public static final String inE = "inE";
        public static final String bothE = "bothE";
        public static final String toV = "toV";
        public static final String outV = "outV";
        public static final String inV = "inV";
        public static final String bothV = "bothV";
        public static final String otherV = "otherV";
        public static final String order = "order";
        public static final String properties = "properties";
        public static final String values = "values";
        public static final String propertyMap = "propertyMap";
        public static final String valueMap = "valueMap";
        public static final String elementMap = "elementMap";
        public static final String select = "select";
        public static final String key = "key";
        public static final String value = "value";
        public static final String path = "path";
        public static final String match = "match";
        public static final String math = "math";
        public static final String sack = "sack";
        public static final String loops = "loops";
        public static final String project = "project";
        public static final String unfold = "unfold";
        public static final String fold = "fold";
        public static final String count = "count";
        public static final String sum = "sum";
        public static final String max = "max";
        public static final String min = "min";
        public static final String mean = "mean";
        public static final String group = "group";
        public static final String groupCount = "groupCount";
        public static final String tree = "tree";
        public static final String addV = "addV";
        public static final String addE = "addE";
        public static final String from = "from";
        public static final String filter = "filter";
        public static final String or = "or";
        public static final String and = "and";
        public static final String inject = "inject";
        public static final String dedup = "dedup";
        public static final String where = "where";
        public static final String has = "has";
        public static final String hasNot = "hasNot";
        public static final String hasLabel = "hasLabel";
        public static final String hasId = "hasId";
        public static final String hasKey = "hasKey";
        public static final String hasValue = "hasValue";
        public static final String is = "is";
        public static final String not = "not";
        public static final String range = "range";
        public static final String limit = "limit";
        public static final String skip = "skip";
        public static final String tail = "tail";
        public static final String coin = "coin";
        public static final String io = "io";
        public static final String read = "read";
        public static final String write = "write";

        public static final String timeLimit = "timeLimit";
        public static final String simplePath = "simplePath";
        public static final String cyclicPath = "cyclicPath";
        public static final String sample = "sample";

        public static final String drop = "drop";

        public static final String sideEffect = "sideEffect";
        public static final String cap = "cap";
        public static final String property = "property";

        /**
         * @deprecated As of release 3.4.3, replaced by {@link Symbols#aggregate} with a {@link Scope#local}.
         */
        @Deprecated
        public static final String store = "store";
        public static final String aggregate = "aggregate";
        public static final String subgraph = "subgraph";
        public static final String barrier = "barrier";
        public static final String index = "index";
        public static final String local = "local";
        public static final String emit = "emit";
        public static final String repeat = "repeat";
        public static final String until = "until";
        public static final String branch = "branch";
        public static final String union = "union";
        public static final String coalesce = "coalesce";
        public static final String choose = "choose";
        public static final String optional = "optional";


        public static final String pageRank = "pageRank";
        public static final String peerPressure = "peerPressure";
        public static final String connectedComponent = "connectedComponent";
        public static final String shortestPath = "shortestPath";
        public static final String program = "program";

        public static final String by = "by";
        public static final String with = "with";
        public static final String times = "times";
        public static final String as = "as";
        public static final String option = "option";

    }
}
