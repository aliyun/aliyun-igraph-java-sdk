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
package org.apache.tinkerpop.gremlin.process.traversal;

import java.util.Optional;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import org.apache.tinkerpop.gremlin.structure.Graph;

/**
 * A {@code TraversalSource} is used to create {@link Traversal} instances.
 * A traversal source can generate any number of {@link Traversal} instances.
 * A traversal source is primarily composed of a {@link Graph} and a {@link TraversalStrategies}.
 * Various {@code withXXX}-based methods are used to configure the traversal strategies (called "configurations").
 * Various other methods (dependent on the traversal source type) will then generate a traversal given the graph and configured strategies (called "spawns").
 * A traversal source is immutable in that fluent chaining of configurations create new traversal sources.
 * This is unlike {@link Traversal} and GraphComputer, where chained methods configure the same instance.
 * Every traversal source implementation must maintain two constructors to enable proper reflection-based construction.
 * {@code TraversalSource(Graph)} and {@code TraversalSource(Graph,TraversalStrategies)}
 *
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 * @author Stephen Mallette (http://stephen.genoprime.com)
 */
public interface TraversalSource extends Cloneable, AutoCloseable {

    /**
     * Get the {@link TraversalStrategies} associated with this traversal source.
     *
     * @return the traversal strategies of the traversal source
     */
    public TraversalStrategies getStrategies();

    /**
     * Get the {@link Graph} associated with this traversal source.
     *
     * @return the graph of the traversal source
     */
    public Graph getGraph();

    /**
     * Get the {@link Bytecode} associated with the current state of this traversal source.
     *
     * @return the traversal source byte code
     */
    public Bytecode getBytecode();

    /////////////////////////////

    public static class Symbols {

        private Symbols() {
            // static fields only
        }

        public static final String with = "with";
        public static final String withSack = "withSack";
        public static final String withStrategies = "withStrategies";
        public static final String withoutStrategies = "withoutStrategies";
        public static final String withComputer = "withComputer";
        public static final String withSideEffect = "withSideEffect";
        public static final String withRemote = "withRemote";
    }

    /////////////////////////////

    /**
     * Provides a configuration to a traversal in the form of a key which is the same as {@code with(key, true)}. The
     * key of the configuration must be graph provider specific and therefore a configuration could be supplied that
     * is not known to be valid until execution.
     *
     * @param key the key of the configuration to apply to a traversal
     * @return a new traversal source with the included configuration
     * @since 3.4.0
     */
    public default TraversalSource with(final String key) {
        return with(key, true);
    }

    /**
     * Provides a configuration to a traversal in the form of a key value pair. The  key of the configuration must be
     * graph provider specific and therefore a configuration could be supplied that is not known to be valid until
     * execution. This is a handy shortcut for building an OptionsStrategy manually and then add with
     * {@link #withStrategies(TraversalStrategy[])}.
     *
     * @param key the key of the configuration to apply to a traversal
     * @param value the value of the configuration to apply to a traversal
     * @return a new traversal source with the included configuration
     * @since 3.4.0
     */
    public TraversalSource with(final String key, final Object value);

    /**
     * Add an arbitrary collection of {@link TraversalStrategy} instances to the traversal source.
     *
     * @param traversalStrategies a collection of traversal strategies to add
     * @return a new traversal source with updated strategies
     */
    public TraversalSource withStrategies(final TraversalStrategy... traversalStrategies);

    /**
     * Remove an arbitrary collection of {@link TraversalStrategy} classes from the traversal source.
     *
     * @param traversalStrategyClasses a collection of traversal strategy classes to remove
     * @return a new traversal source with updated strategies
     */
    @SuppressWarnings({"unchecked", "varargs"})
    public TraversalSource withoutStrategies(
        final Class<? extends TraversalStrategy>... traversalStrategyClasses);

    /**
     * Add a {@link Computer} that will generate a {@link GraphComputer} from the {@link Graph} that will be used to execute the traversal.
     * This adds a {@link VertexProgramStrategy} to the strategies.
     *
     * @param computer a builder to generate a graph computer from the graph
     * @return a new traversal source with updated strategies
     */
    //public default TraversalSource withComputer(final Computer computer) {
    //    return this.withStrategies(new VertexProgramStrategy(computer));
    //}

    /**
     * Add a {@link GraphComputer} class used to execute the traversal.
     * This adds a {@link VertexProgramStrategy} to the strategies.
     *
     * @param graphComputerClass the graph computer class
     * @return a new traversal source with updated strategies
     */
    //public default TraversalSource withComputer(final Class<? extends GraphComputer> graphComputerClass) {
    //    return this.withStrategies(new VertexProgramStrategy(Computer.compute(graphComputerClass)));
    //}

    /**
     * Add the standard  GraphComputer of the graph that will be used to execute the traversal.
     * This adds a VertexProgramStrategy to the strategies.
     *
     * @return a new traversal source with updated strategies
     */
    public TraversalSource withComputer();

    /**
     * Add a sideEffect to be used throughout the life of a spawned {@link Traversal}.
     * This adds a SideEffectStrategy to the strategies.
     *
     * @param key          the key of the sideEffect
     * @param initialValue a supplier that produces the initial value of the sideEffect
     * @param reducer      a reducer to merge sideEffect mutations into a single result
     * @param <A> type
     * @return a new traversal source with updated strategies
     */
    public <A> TraversalSource withSideEffect(final String key, final Supplier<A> initialValue,
                                                      final BinaryOperator<A> reducer);

    /**
     * Add a sideEffect to be used throughout the life of a spawned {@link Traversal}.
     * This adds a SideEffectStrategy to the strategies.
     *
     * @param key          the key of the sideEffect
     * @param initialValue the initial value of the sideEffect
     * @param reducer      a reducer to merge sideEffect mutations into a single result
     * @param <A> type
     * @return a new traversal source with updated strategies
     */
    public <A> TraversalSource withSideEffect(final String key, final A initialValue,
                                                      final BinaryOperator<A> reducer);

    /**
     * Add a sideEffect to be used throughout the life of a spawned {@link Traversal}.
     * This adds a SideEffectStrategy to the strategies.
     *
     * @param key          the key of the sideEffect
     * @param initialValue a supplier that produces the initial value of the sideEffect
     * @param <A> type
     * @return a new traversal source with updated strategies
     */
    public <A> TraversalSource withSideEffect(final String key, final Supplier<A> initialValue);

    /**
     * Add a sideEffect to be used throughout the life of a spawned {@link Traversal}.
     * This adds a SideEffectStrategy to the strategies.
     *
     * @param key          the key of the sideEffect
     * @param initialValue the initial value of the sideEffect
     * @param <A> type
     * @return a new traversal source with updated strategies
     */
    public <A> TraversalSource withSideEffect(final String key, final A initialValue);

    /**
     * Add a sack to be used throughout the life of a spawned {@link Traversal}.
     * This adds a SackStrategy to the strategies.
     *
     * @param initialValue  a supplier that produces the initial value of the sideEffect
     * @param splitOperator the sack split operator
     * @param mergeOperator the sack merge operator
     * @param <A> type
     * @return a new traversal source with updated strategies
     */
    public <A> TraversalSource withSack(final Supplier<A> initialValue, final UnaryOperator<A> splitOperator,
                                                final BinaryOperator<A> mergeOperator);

    /**
     * Add a sack to be used throughout the life of a spawned {@link Traversal}.
     * This adds a SackStrategy to the strategies.
     *
     * @param initialValue  the initial value of the sideEffect
     * @param splitOperator the sack split operator
     * @param mergeOperator the sack merge operator
     * @param <A> type
     * @return a new traversal source with updated strategies
     */
    public <A> TraversalSource withSack(final A initialValue, final UnaryOperator<A> splitOperator,
                                                final BinaryOperator<A> mergeOperator);

    /**
     * Add a sack to be used throughout the life of a spawned {@link Traversal}.
     * This adds a SackStrategy to the strategies.
     *
     * @param initialValue the initial value of the sideEffect
     * @param <A> type
     * @return a new traversal source with updated strategies
     */
    public <A> TraversalSource withSack(final A initialValue);

    /**
     * Add a sack to be used throughout the life of a spawned {@link Traversal}.
     * This adds a SackStrategy to the strategies.
     *
     * @param initialValue a supplier that produces the initial value of the sideEffect
     * @param <A> type
     * @return a new traversal source with updated strategies
     */
    public <A> TraversalSource withSack(final Supplier<A> initialValue);

    /**
     * Add a sack to be used throughout the life of a spawned {@link Traversal}.
     * This adds a SackStrategy to the strategies.
     *
     * @param initialValue  a supplier that produces the initial value of the sideEffect
     * @param splitOperator the sack split operator
     * @param <A> type
     * @return a new traversal source with updated strategies
     */
    public <A> TraversalSource withSack(final Supplier<A> initialValue, final UnaryOperator<A> splitOperator);

    /**
     * Add a sack to be used throughout the life of a spawned {@link Traversal}.
     * This adds a SackStrategy to the strategies.
     *
     * @param initialValue  the initial value of the sideEffect
     * @param splitOperator the sack split operator
     * @param <A> type
     * @return a new traversal source with updated strategies
     */
    public <A> TraversalSource withSack(final A initialValue, final UnaryOperator<A> splitOperator);

    /**
     * Add a sack to be used throughout the life of a spawned {@link Traversal}.
     * This adds a SackStrategy to the strategies.
     *
     * @param initialValue  a supplier that produces the initial value of the sideEffect
     * @param mergeOperator the sack merge operator
     * @param <A> type
     * @return a new traversal source with updated strategies
     */
    public <A> TraversalSource withSack(final Supplier<A> initialValue, final BinaryOperator<A> mergeOperator);

    /**
     * Add a sack to be used throughout the life of a spawned {@link Traversal}.
     * This adds a SackStrategy to the strategies.
     *
     * @param initialValue  the initial value of the sideEffect
     * @param mergeOperator the sack merge operator
     * @param <A> type
     * @return a new traversal source with updated strategies
     */
    public <A> TraversalSource withSack(final A initialValue, final BinaryOperator<A> mergeOperator);

    public default Optional<Class<?>> getAnonymousTraversalClass() {
        return Optional.empty();
    }

    /**
     * The clone-method should be used to create immutable traversal sources with each call to a configuration "withXXX"-method.
     * The clone-method should clone the {@link Bytecode}, {@link TraversalStrategies}, mutate the cloned strategies accordingly,
     * and then return the cloned traversal source leaving the original unaltered.
     *
     * @return the cloned traversal source
     */
    @SuppressWarnings("CloneDoesntDeclareCloneNotSupportedException")
    public TraversalSource clone();

    @Override
    public default void close() throws Exception {
        // do nothing
    }

}
