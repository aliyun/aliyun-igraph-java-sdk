package com.aliyun.igraph.client.core.model;

import com.aliyun.igraph.client.exception.IGraphQueryException;
import org.junit.Assert;
import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.Map;

public class UpdateQueryTest {
    @Test
    public void testConstructor() throws Exception {
        Map<String, String> valueMap = new LinkedHashMap<String, String>() {
            {
                put("value1", "v1");
                put("value2", "v2");
            }
        };
        {
            UpdateQuery updateQuery = UpdateQuery.builder()
                    .table("tb1")
                    .pkey("1")
                    .skey("2")
                    .valueMaps(valueMap)
                    .build();
            Assert.assertEquals("table=tb1&pkey=1&skey=2&value1=v1&value2=v2", updateQuery.toString());
        }
        {
            try {
                UpdateQuery updateQuery = UpdateQuery.builder()
                        .pkey("1")
                        .build();
                Assert.fail();
            } catch (NullPointerException expected) {
            }
            try {
                UpdateQuery updateQuery = UpdateQuery.builder()
                        .table("tb1")
                        .build();
                updateQuery.toString();
                Assert.fail();
            } catch (IGraphQueryException expected) {
            }
        }
    }
}
