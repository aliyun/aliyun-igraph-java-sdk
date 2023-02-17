package com.aliyun.igraph.client.gremlin.gremlin_api;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by wl on 2018/12/11.
 */
public class PTest {
    @Test
    public void singlePTest() {
        {
            P p = P.eq("123");
            Assert.assertEquals("P.eq(\"123\")", p.toString());
        }
        {
            P p = P.eq(123);
            Assert.assertEquals("P.eq(123)", p.toString());
        }
        {
            P p = P.neq("123");
            Assert.assertEquals("P.neq(\"123\")", p.toString());
        }
        {
            P p = P.neq(123);
            Assert.assertEquals("P.neq(123)", p.toString());
        }
        {
            P p = P.lt("123");
            Assert.assertEquals("P.lt(\"123\")", p.toString());
        }
        {
            P p = P.lt(123);
            Assert.assertEquals("P.lt(123)", p.toString());
        }
        {
            P p = P.lte("123");
            Assert.assertEquals("P.lte(\"123\")", p.toString());
        }
        {
            P p = P.lte(123);
            Assert.assertEquals("P.lte(123)", p.toString());
        }
        {
            P p = P.gt("123");
            Assert.assertEquals("P.gt(\"123\")", p.toString());
        }
        {
            P p = P.gt(123);
            Assert.assertEquals("P.gt(123)", p.toString());
        }
        {
            P p = P.gte("123");
            Assert.assertEquals("P.gte(\"123\")", p.toString());
        }
        {
            P p = P.gte(123);
            Assert.assertEquals("P.gte(123)", p.toString());
        }

    }

    @Test
    public void doubleAndMultiPTest() {
        {
            P p = P.inside(2, 3);
            Assert.assertEquals("P.inside(2,3)", p.toString());
        }
        {
            P p = P.inside("2", "3");
            Assert.assertEquals("P.inside(\"2\",\"3\")", p.toString());
        }
        {
            P p = P.outside(2, 3);
            Assert.assertEquals("P.outside(2,3)", p.toString());
        }
        {
            P p = P.outside("2", "3");
            Assert.assertEquals("P.outside(\"2\",\"3\")", p.toString());
        }
        {
            P p = P.between(2, 3);
            Assert.assertEquals("P.between(2,3)", p.toString());
        }
        {
            P p = P.between("2", "3");
            Assert.assertEquals("P.between(\"2\",\"3\")", p.toString());
        }
        {
            P p = P.within(1,2,3,4);
            Assert.assertEquals("P.within(1,2,3,4)", p.toString());
        }
        {
            P p = P.within("1","2");
            Assert.assertEquals("P.within(\"1\",\"2\")", p.toString());
        }
        {
            P p = P.without(1,2,3,4);
            Assert.assertEquals("P.without(1,2,3,4)", p.toString());
        }
        {
            P p = P.without("1","2");
            Assert.assertEquals("P.without(\"1\",\"2\")", p.toString());
        }
    }

    @Test
    public void otherTest() {
        {
            P p = P.lt(123).negate();
            Assert.assertEquals("P.lt(123).negate()", p.toString());
        }
        {
            P p = P.lt(123).and(P.gt(456).negate());
            Assert.assertEquals("P.lt(123).and(P.gt(456).negate())", p.toString());
        }
        {
            P p = P.lt(123).or(P.gt(456).negate());
            Assert.assertEquals("P.lt(123).or(P.gt(456).negate())", p.toString());
        }
    }
}
