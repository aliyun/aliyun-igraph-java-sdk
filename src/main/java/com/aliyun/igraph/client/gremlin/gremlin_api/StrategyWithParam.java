package com.aliyun.igraph.client.gremlin.gremlin_api;

import static com.aliyun.igraph.client.gremlin.gremlin_api.StringBuilderHelper.appendMultiString;
import static com.aliyun.igraph.client.gremlin.gremlin_api.StringBuilderHelper.appendParameter;

/**
 * @author alibaba
 */
public class StrategyWithParam implements StrategyBase {
    private String val;

    private StrategyWithParam(String val) {
        this.val = val;
    }

    public String toString() {
        return val;
    }

    public static StrategyWithParam PushDownStrategy(String... params) {
        StringBuilder ss = new StringBuilder();
        ss.append("PushDownStrategy(");
        appendMultiString(ss, false, params);
        ss.append(")");
        return new StrategyWithParam(ss.toString());
    }

    public static StrategyWithParam PathRecordStrategy(PathRecord... params) {
        StringBuilder ss = new StringBuilder();
        ss.append("PathRecordStrategy(");
        boolean needComma = false;
        for (PathRecord param : params) {
            appendParameter(ss, needComma, param);
            needComma = true;
        }
        ss.append(")");
        return new StrategyWithParam(ss.toString());
    }
}
