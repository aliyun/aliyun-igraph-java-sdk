package com.aliyun.igraph.client.gremlin.gremlin_api;

/**
 * @author alibaba
 */
public class P {
    private String pString;

    public P(String pString) {
        this.pString = pString;
    }

    public P negate() {
        return new P(pString + ".negate()");
    }

    public P and(final P p) {
        return new P(pString + ".and(" + p.toString() + ')');
    }

    public P or(final P p) {
        return new P(pString + ".or(" + p.toString() + ')');
    }

    // public static P not(final P p);

    public static <V> P eq(final V value) {
        return new P(singlePGenerator("P.eq", value));
    }
    public static <V> P neq(final V value) {
        return new P(singlePGenerator("P.neq", value));
    }
    public static <V> P lt(final V value) {
        return new P(singlePGenerator("P.lt", value));
    }
    public static <V> P lte(final V value) {
        return new P(singlePGenerator("P.lte", value));
    }
    public static <V> P gt(final V value) {
        return new P(singlePGenerator("P.gt", value));
    }
    public static <V> P gte(final V value) {
        return new P(singlePGenerator("P.gte", value));
    }
    public static <V> P inside(final V first, final V second) {
        return new P(doublePGenerator("P.inside", first, second));
    }
    public static <V> P outside(final V first, final V second) {
        return new P(doublePGenerator("P.outside", first, second));
    }
    public static <V> P between(final V first, final V second) {
        return new P(doublePGenerator("P.between", first, second));
    }
    public static <V> P within(final V... values) {
        return new P(multiPGenerator("P.within", values));
    }
    public static <V> P without(final V... values) {
        return new P(multiPGenerator("P.without", values));
    }

    @Override
    public String toString() {
        return pString;
    }

    private static <V> String singlePGenerator(String pPrefix, V value) {
        StringBuilder ss = new StringBuilder(1024);
        if (value instanceof String) {
            ss.append(pPrefix).append("(\"").append(value).append("\")");
        } else {
            ss.append(pPrefix).append('(').append(value).append(')');
        }
        return ss.toString();
    }

    private static <V> String doublePGenerator(String pPrefix, V first, V second) {
        StringBuilder ss = new StringBuilder(1024);
        if (first instanceof String) {
            ss.append(pPrefix).append("(\"").append(first).append("\",")
                    .append('\"').append(second).append("\")");
        } else {
            ss.append(pPrefix).append('(').append(first).append(',').append(second).append(')');
        }
        return ss.toString();
    }

    private static <V> String multiPGenerator(String pPrefix, V... values) {
        StringBuilder ss = new StringBuilder(1024);
        ss.append(pPrefix).append('(');
        boolean isFirst = true;
        boolean isString = false;
        for (V value : values) {
            if (!isFirst) {
                ss.append(',');
            } else {
                isFirst = false;
                isString = (value instanceof String);
            }
            if (isString) {
                ss.append('\"').append(value).append('\"');
            } else {
                ss.append(value);
            }
        }
        ss.append(')');
        return ss.toString();
    }
}
