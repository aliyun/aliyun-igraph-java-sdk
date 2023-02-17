package com.aliyun.igraph.client.gremlin.gremlin_api;

import org.apache.tinkerpop.gremlin.process.traversal.Pop;
import org.apache.tinkerpop.gremlin.process.traversal.Traversal;

/**
 * @author alibaba
 */
public class __ {
    protected __() {
    }

    public static GraphTraversal start() {
        return new GraphTraversal();
    }

    public static GraphTraversal indexQuery(final String queryFeature) {
        return __.start().indexQuery(queryFeature);
    }

    public static GraphTraversal map(final String expression) {
        return __.start().map(expression);
    }

    public static GraphTraversal map(final Traversal mapTraveral) {
        return __.start().map(mapTraveral);
    }

    public static GraphTraversal flatMap(final Traversal flatMapTraversal) {
        return __.start().flatMap(flatMapTraversal);
    }

    public static GraphTraversal label() {
        return __.start().label();
    }

    public static GraphTraversal identity() {
        return __.start().identity();
    }

    public static <T> GraphTraversal constant(final T t) {
        return __.start().constant(t);
    }

    public static GraphTraversal to(final org.apache.tinkerpop.gremlin.structure.Direction direction, final String... edgeLabels) {
        return __.start().to(direction, edgeLabels);
    }

    public static GraphTraversal out(final String... edgeLabels) {
        return __.start().out(edgeLabels);
    }

    public static GraphTraversal in(final String... edgeLabels) {
        return __.start().in(edgeLabels);
    }

    public static GraphTraversal both(final String... edgeLabels) {
        return __.start().both(edgeLabels);
    }

    public static GraphTraversal toE(final org.apache.tinkerpop.gremlin.structure.Direction direction, final  String... edgeLabels) {
        return __.start().toE(direction, edgeLabels);
    }

    public static GraphTraversal outE(final String... edgeLabels) {
        return __.start().outE(edgeLabels);
    }

    public static GraphTraversal inE(final String... edgeLabels) {
        return __.start().inE(edgeLabels);
    }

    public static GraphTraversal bothE(final String... edgeLabels) {
        return __.start().bothE(edgeLabels);
    }

    public static GraphTraversal toV(final org.apache.tinkerpop.gremlin.structure.Direction direction) {
        return __.start().toV(direction);
    }

    public static GraphTraversal inV() {
        return __.start().inV();
    }

    public static GraphTraversal outV() {
        return __.start().outV();
    }

    public static GraphTraversal bothV() {
        return __.start().bothV();
    }

    public static GraphTraversal order() {
        return __.start().order();
    }

    public static GraphTraversal properties(final String... propertyKeys) {
        return __.start().properties(propertyKeys);
    }

    public static GraphTraversal values(final String... propertyKeys) {
        return __.start().values(propertyKeys);
    }

    public static GraphTraversal propertyMap(final String... propertyKeys) {
        return __.start().propertyMap(propertyKeys);
    }

    public static GraphTraversal valueMap(final String... propertyKeys) {
        return __.start().valueMap(propertyKeys);
    }

    public static GraphTraversal select(final Column column) {
        return __.start().select(column);
    }

    public static GraphTraversal key() {
        return __.start().key();
    }

    public static GraphTraversal value() {
        return __.start().value();
    }

    public static GraphTraversal path() {
        return __.start().path();
    }

    public static GraphTraversal sack() {
        return __.start().sack();
    }

    public static GraphTraversal loops() {
        return __.start().loops();
    }

    public static GraphTraversal select(final Pop pop, final String selectKey) {
        return __.start().select(pop, selectKey);
    }
    public static GraphTraversal select(final String selectKey) {
        return __.start().select(selectKey);
    }

    public static GraphTraversal select(final Pop pop, final String selectKey1, final String selectKey2, String... otherSelectKeys) {
        return __.start().select(pop, selectKey1, selectKey2, otherSelectKeys);
    }

    public static GraphTraversal select(final String selectKey1, final String selectKey2, String... otherSelectKeys) {
        return __.start().select(selectKey1, selectKey2, otherSelectKeys);
    }

    public static GraphTraversal fold() {
        return __.start().fold();
    }

    public static GraphTraversal unfold() {
        return __.start().unfold();
    }

    public static GraphTraversal count() {
        return __.start().count();
    }

    public static GraphTraversal count(final org.apache.tinkerpop.gremlin.process.traversal.Scope scope) {
        return __.start().count(scope);
    }

    public static GraphTraversal sum() {
        return __.start().sum();
    }

    public static GraphTraversal sum(final org.apache.tinkerpop.gremlin.process.traversal.Scope scope) {
        return __.start().sum(scope);
    }

    public static GraphTraversal max() {
        return __.start().max();
    }

    public static GraphTraversal max(final org.apache.tinkerpop.gremlin.process.traversal.Scope scope) {
        return __.start().max(scope);
    }

    public static GraphTraversal min() {
        return __.start().min();
    }

    public static GraphTraversal mean() {
        return __.start().mean();
    }

    public static GraphTraversal group() {
        return __.start().group();
    }

    public static GraphTraversal groupCount() {
        return __.start().groupCount();
    }

    public static GraphTraversal to(final String toStepLabel) {
        return __.start().to(toStepLabel);
    }

    public static GraphTraversal from(final String toStepLabel) {
        return __.start().from(toStepLabel);
    }

    public static GraphTraversal filter(final Traversal filterTraveral) {
        return __.start().filter(filterTraveral);
    }

    public static GraphTraversal filter(final String expression) {
        return __.start().filter(expression);
    }

    public static GraphTraversal or(final Traversal... orTraversals) {
        return __.start().or(orTraversals);
    }

    public static GraphTraversal and(final Traversal... orTraversals) {
        return __.start().and(orTraversals);
    }

    public static GraphTraversal dedup(final String... dedupLabels) {
        return __.start().dedup(dedupLabels);
    }

    public static GraphTraversal where(final P predicate) {
        return __.start().where(predicate);
    }

    public static GraphTraversal where(final String startKey, final P predicate) {
        return __.start().where(startKey, predicate);
    }

    public static GraphTraversal where(final Traversal whereTraversal) {
        return __.start().where(whereTraversal);
    }

    public static GraphTraversal has(final String propertyKey, final P predicate) {
        return __.start().has(propertyKey, predicate);
    }

    public static GraphTraversal has(final T accessor, final P predicate) {
        return __.start().has(accessor, predicate);
    }

    public static GraphTraversal has(final String propertyKey, final Traversal propertyTraversal) {
        return __.start().has(propertyKey, propertyTraversal);
    }

    public static GraphTraversal has(final String propertyKey, final Object value) {
        return __.start().has(propertyKey, value);
    }

    public static GraphTraversal has(final String propertyKey) {
        return __.start().has(propertyKey);
    }

    public static GraphTraversal hasLabel(final String label, final String... otherLabels) {
        return __.start().hasLabel(label, otherLabels);
    }

    public static GraphTraversal hasKey(final String label, final String... otherLabels) {
        return __.start().hasKey(label, otherLabels);
    }

    public static GraphTraversal hasKey(final P predicate) {
        return __.start().hasKey(predicate);
    }

    public static GraphTraversal hasValue(final Object value, final Object...otherValues) {
        return __.start().hasValue(value, otherValues);
    }

    public static GraphTraversal hasValue(final P predicate) {
        return __.start().hasValue(predicate);
    }

    public static GraphTraversal is(final P predicate) {
        return __.start().is(predicate);
    }

    public static GraphTraversal is(final Object value) {
        return __.start().is(value);
    }

    public static GraphTraversal not(final Traversal notTraversal) {
        return __.start().not(notTraversal);
    }

    public static GraphTraversal range(final long low, final long high) {
        return __.start().range(low, high);
    }

    public static GraphTraversal limit(final long limit) {
        return __.start().limit(limit);
    }

    public static GraphTraversal tail() {
        return __.start().tail();
    }

    public static GraphTraversal tail(final long limit) {
        return __.start().tail(limit);
    }

    public static GraphTraversal tail(final org.apache.tinkerpop.gremlin.process.traversal.Scope scope) {
        return __.start().tail(scope);
    }

    public static GraphTraversal tail(final  org.apache.tinkerpop.gremlin.process.traversal.Scope scope, final long limit) {
        return __.start().tail(scope, limit);
    }

    public static GraphTraversal simplePath() {
        return __.start().simplePath();
    }

    public static GraphTraversal cyclicPath() {
        return __.start().cyclicPath();
    }

    public static GraphTraversal sample(final int amountToSample) {
        return __.start().sample(amountToSample);
    }

    public static GraphTraversal sample(final Sample sampleOption, final int amountToSample) {
        return __.start().sample(sampleOption, amountToSample);
    }

    public static GraphTraversal sample(final Sample sampleOption, final Sample sampleOption2, final int amountToSample) {
        return __.start().sample(sampleOption, sampleOption2, amountToSample);
    }

    public static GraphTraversal aggregate(final String sideEffectKey) {
        return __.start().aggregate(sideEffectKey);
    }

    public static GraphTraversal sack(final Operator sackOperator) {
        return __.start().sack(sackOperator);
    }

    public static GraphTraversal sack(final String mapKey, final Operator sackOperator) {
        return __.start().sack(mapKey, sackOperator);
    }

    public static GraphTraversal bulk() {
        return __.start().bulk();
    }

    public static GraphTraversal toList() {
        return __.start().toList();
    }

    public static GraphTraversal withBulk() {
        return __.start().withBulk();
    }

    public static GraphTraversal withSack() {
        return __.start().withSack();
    }

    public static GraphTraversal needFold() {
        return __.start().needFold();
    }

    public static GraphTraversal fields(final String... fieldNames) {
        return __.start().fields(fieldNames);
    }

    public static GraphTraversal branch(final Traversal branchTraversal) {
        return __.start().branch(branchTraversal);
    }

    public static GraphTraversal choose(final Traversal choiceTraversal) {
        return __.start().choose(choiceTraversal);
    }

    public static GraphTraversal choose(final Traversal traversalPredicate, final Traversal trueChoice, final Traversal falseChoice) {
        return __.start().choose(traversalPredicate, trueChoice, falseChoice);
    }

    public static GraphTraversal choose(final Traversal traversalPredicate, final Traversal trueChoice) {
        return __.start().choose(traversalPredicate, trueChoice);
    }

    public static GraphTraversal optional(final Traversal optionalTraversal) {
        return __.start().optional(optionalTraversal);
    }

    public static GraphTraversal union(final Traversal... unionTraversals) {
        return __.start().union(unionTraversals);
    }

    public static GraphTraversal coalesce(final Traversal... traversals) {
        return __.start().coalesce(traversals);
    }

    public static GraphTraversal repeat(final Traversal repeatTraversal) {
        return __.start().repeat(repeatTraversal);
    }

    public static GraphTraversal emit(final Traversal emitTraversal) {
        return __.start().emit(emitTraversal);
    }

    public static GraphTraversal emit() {
        return __.start().emit();
    }

    public static GraphTraversal until(final Traversal untilTraversal) {
        return __.start().until(untilTraversal);
    }

    public static GraphTraversal times(final int maxLoops) {
        return __.start().times(maxLoops);
    }

    public static GraphTraversal local(final Traversal localTraversal) {
        return __.start().local(localTraversal);
    }

    public static GraphTraversal as(final String stepLabel, final String... stepLabels) {
        return __.start().as(stepLabel, stepLabels);
    }

    public static GraphTraversal barrier() {
        return __.start().barrier();
    }

    public static GraphTraversal barrier(final int maxBarrierSize) {
        return __.start().barrier(maxBarrierSize);
    }

    public static GraphTraversal barrier(final Barrier barrierOption) {
        return __.start().barrier(barrierOption);
    }

    public static GraphTraversal by() {
        return __.start().by();
    }

    public static GraphTraversal by(final Traversal traversal) {
        return __.start().by(traversal);
    }

    public static GraphTraversal by(final T token) {
        return __.start().by(token);
    }

    public static GraphTraversal by(final String key) {
        return __.start().by(key);
    }

    public static GraphTraversal by(final Traversal traversal, final Order order) {
        return __.start().by(traversal, order);
    }

    public static GraphTraversal by(final Order order) {
        return __.start().by(order);
    }

    public static GraphTraversal by(final T accessor, final Order order) {
        return __.start().by(accessor, order);
    }

    public static GraphTraversal by(final String key, final Order order) {
        return __.start().by(key, order);
    }

    public static GraphTraversal by(final Column column, final Order order) {
        return __.start().by(column, order);
    }

    public static GraphTraversal option(final int pick, final Traversal optionTraversal) {
        return __.start().option(pick, optionTraversal);
    }

    public static GraphTraversal option(final String pick, final Traversal optionTraversal) {
        return __.start().option(pick, optionTraversal);
    }

    public static GraphTraversal option(final Pick pick, final Traversal optionTraversal) {
        return __.start().option(pick, optionTraversal);
    }

    public static GraphTraversal noSupportedStep(final String stepName, final Object... parameters) {
        return __.start().noSupportedStep(stepName, parameters);
    }
}
