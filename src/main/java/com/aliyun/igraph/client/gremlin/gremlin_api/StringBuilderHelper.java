package com.aliyun.igraph.client.gremlin.gremlin_api;

import com.aliyun.igraph.client.exception.IGraphQueryException;
import com.aliyun.igraph.client.pg.KeyList;

import javax.ws.rs.NotSupportedException;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;

/**
 * @author alibaba
 */
public class StringBuilderHelper {
    public static void stepWithAtLeastSingleParameter(StringBuilder ss, final Object parameter, final String... otherStrings) {
        if (parameter instanceof String) {
            appendParameter(ss, false, (String)parameter);
        } else {
            appendParameter(ss, false, parameter);
        }
        appendMultiString(ss, true, otherStrings);
    }

    public static void appendMultiString(StringBuilder ss, final boolean needComma, final String... strings) {
        boolean needCommatmp = needComma;
        for (String string : strings) {
            appendParameter(ss, needCommatmp, string);
            needCommatmp = true;
        }
    }

    public static <E> void appendParameter(StringBuilder ss, final boolean needComma, final E e) {
        if (needComma) {
            ss.append(',');
        }
        ss.append(e);
    }

    public static void appendParameter(StringBuilder ss, final boolean needComma, final String string) {
        if(needComma) {
            ss.append(',');
        }
        ss.append('\"').append(string).append('\"');
    }

    public static void appendParameter(StringBuilder ss, final boolean needComma, final List<KeyList> keyLists) {
        if(needComma) {
            ss.append(',');
        }
        ss.append('\"');
        boolean first = true;
        for (KeyList keyList : keyLists) {
            if (!first) {
                ss.append(";");
            }
            first = false;
            ss.append(keyList.toString());
        }
        ss.append('\"');
    }

    public static Map.Entry<String, String> decodeKeyString(String keyString) {
        String pkey;
        String skey = null;
        int hasColon = keyString.indexOf(':');
        if (hasColon != -1) {
            pkey = keyString.substring(0,hasColon);
            skey = keyString.substring(hasColon+1);
            if (skey.contains("|")) {
                throw new IGraphQueryException("batch delete is not supported!");
            }
        } else {
            pkey = keyString;
        }
        return new AbstractMap.SimpleEntry<String, String>(pkey, skey);
    }

    public static Direction convertDirection(org.apache.tinkerpop.gremlin.structure.Direction direction) {
        switch (direction) {
            case IN:
                return Direction.IN;
            case OUT:
                return Direction.OUT;
            case BOTH:
                return Direction.BOTH;
            default:
        }
        return null;
    }

    public static Pop convertPop(org.apache.tinkerpop.gremlin.process.traversal.Pop pop) {
        switch (pop) {
            case first:
                return Pop.first;
            case last:
                return Pop.last;
            case all:
                return Pop.all;
            default:
                throw new NotSupportedException("Pop.mixed is not supported!");
        }
    }

    public static Scope convertScope(org.apache.tinkerpop.gremlin.process.traversal.Scope scope) {
        switch (scope) {
            case local:
                return Scope.local;
            case global:
                return Scope.global;
            default:
        }
        return null;
    }
}
