package com.aliyun.igraph.client.gremlin.structure;

import lombok.Data;
import org.apache.tinkerpop.gremlin.process.traversal.Path;
import org.apache.tinkerpop.gremlin.process.traversal.Pop;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.NotSupportedException;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

/**
 * @author alibaba
 */
@Data
public class IGraphPath implements Path {
    private static final Logger log = LoggerFactory.getLogger(IGraphPath.class);
    protected List<Object> resultList;
    protected List<IGraphResult> iGraphResultList;
    protected List<Set<String>> labels;

    public IGraphPath() {
        resultList = new ArrayList<>();
        labels = new ArrayList<>();
        iGraphResultList = new ArrayList<>();
    }
    public IGraphPath(List<Object> resultList, List<Set<String>> labels) {
        this.resultList = resultList;
        this.labels = labels;
    }
    public IGraphPath(List<Object> resultList, List<Set<String>> labels, List<IGraphResult> iGraphResultList) {
        this.resultList = resultList;
        this.labels = labels;
        this.iGraphResultList = iGraphResultList;
    }

    public void setResultList(List<Object> resultList) {
        this.resultList = resultList;
    }

    @Override
    public String toString() {
        if (iGraphResultList.size() != labels.size()) {
            log.warn("the size of objects and labels is not equal in Path");
            return null;
        }
        StringBuilder ss = new StringBuilder(128);
        ss.append("[");
        for (int i = 0; i < iGraphResultList.size(); ++i) {
            ss.append("{");
            IGraphResult iGraphResult = (IGraphResult) iGraphResultList.get(i);
            ss.append("\"object\":").append(iGraphResult.toJson()).append(",");
            ss.append("\"label\":").append(labels.get(i));
            ss.append("},");
            if (i == iGraphResultList.size() -1) {
                ss.setLength(ss.length() - 1);
            }
        }
        ss.append("]");
        return ss.toString();
    }

    @Override
    public int size() {
        return Path.super.size();
    }

    @Override
    public boolean isEmpty() {
        return Path.super.isEmpty();
    }

    @Override
    public <A> A head() {
        return (A) objects().get(0);
    }

    @Override
    public Path extend(Object object, Set<String> labels) {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public Path extend(Set<String> labels) {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public Path retract(Set<String> labels) {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public <A> A get(String label) throws IllegalArgumentException {
        Object object = null;
        for (int i = 0; i < labels.size(); i++) {
            if (labels.get(i).contains(label)) {
                if (null == object) {
                    object = resultList.get(i);
                } else if (object instanceof List) {
                    ((List) object).add(resultList.get(i));
                } else {
                    final List list = new ArrayList(2);
                    list.add(object);
                    list.add(resultList.get(i));
                    object = list;
                }
            }
        }
        if (null == object) {
            throw Exceptions.stepWithProvidedLabelDoesNotExist(label);
        }
        return (A) object;
    }

    @Override
    public <A> A get(Pop pop, String label) throws IllegalArgumentException {
        return Path.super.get(pop, label);
    }

    @Override
    public <A> A get(int index) {
        return Path.super.get(index);
    }

    @Override
    public boolean hasLabel(String label) {
        return Path.super.hasLabel(label);
    }

    @Override
    public List<Object> objects() {
        return resultList;
    }

    @Override
    public List<Set<String>> labels() {
        return labels;
    }

    @Override
    public Path clone() {
        return new IGraphPath(resultList, labels, iGraphResultList);
    }

    @Override
    public boolean isSimple() {
        return Path.super.isSimple();
    }

    @Override
    public Iterator<Object> iterator() {
        return Path.super.iterator();
    }

    @Override
    public void forEach(BiConsumer<Object, Set<String>> consumer) {
        Path.super.forEach(consumer);
    }

    @Override
    public Stream<Pair<Object, Set<String>>> stream() {
        return Path.super.stream();
    }

    @Override
    public boolean popEquals(Pop pop, Object other) {
        return Path.super.popEquals(pop, other);
    }

    @Override
    public Path subPath(String fromLabel, String toLabel) {
        if (null != fromLabel || null != toLabel) {
            IGraphPath subPath = new IGraphPath();
            final int size = this.size();
            int fromIndex = -1;
            int toIndex = -1;
            for (int i = size - 1; i >= 0; i--) {
                final Set<String> labels = this.labels().get(i);
                if (-1 == fromIndex && labels.contains(fromLabel)) {
                    fromIndex = i;
                }
                if (-1 == toIndex && labels.contains(toLabel)) {
                    toIndex = i;
                }
            }
            if (null != fromLabel && -1 == fromIndex) {
                throw Exceptions.couldNotLocatePathFromLabel(fromLabel);
            }
            if (null != toLabel && -1 == toIndex) {
                throw Exceptions.couldNotLocatePathToLabel(toLabel);
            }

            if (fromIndex == -1) {
                fromIndex = 0;
            }
            if (toIndex == -1) {
                toIndex = size-1;
            }
            if (fromIndex > toIndex) {
                throw Exceptions.couldNotIsolatedSubPath(fromLabel, toLabel);
            }
            for (int i = fromIndex; i <= toIndex; i++) {
                final Set<String> labels = this.labels().get(i);
                subPath.objects().add(this.get(i));
                subPath.labels.add(labels);
                subPath.iGraphResultList.add(this.iGraphResultList.get(i));
            }
            return subPath;
        }
        return this;
    }
}
