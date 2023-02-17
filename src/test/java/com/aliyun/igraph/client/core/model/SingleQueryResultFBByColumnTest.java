package com.aliyun.igraph.client.core.model;

import com.aliyun.igraph.client.pg.SingleQueryResultFBByColumn;
import com.aliyun.igraph.client.proto.pg_fb.*;
import com.google.flatbuffers.FlatBufferBuilder;
import com.aliyun.igraph.client.pg.MatchRecord;
import org.junit.Assert;
import org.junit.Test;

import java.nio.ByteBuffer;

/**
 * Created by bingyu.zby on 2017.8.17
 */
public class SingleQueryResultFBByColumnTest {
    private int kMaxColumnSize = 10;

    private ByteBuffer createTestData() {
        FlatBufferBuilder fbb = new FlatBufferBuilder(1);

        int[] fieldNames = {
                fbb.createString("int32"),
        };
        int fieldNamesOffset = MatchRecords.createFieldNameVector(fbb, fieldNames);

        int index = 0;
        int[] fieldColumnOffsets = new int[fieldNames.length];

        // int32
        int[] int32 = new int[kMaxColumnSize];
        for (int i = 0; i < kMaxColumnSize; i++) {
            if (0 == i%2) {
                int32[i] = i;
            } else {
                int32[i] = -i;
            }
        }
        int int32ValueOffset = Int32ValueColumn.createValueVector(fbb, int32);
        int int32ColumnOffset = Int32ValueColumn.createInt32ValueColumn(fbb, int32ValueOffset);
        fieldColumnOffsets[index++] = FieldValueColumnTable.createFieldValueColumnTable(fbb,
                FieldValueColumn.Int32ValueColumn, int32ColumnOffset);

        int matchRecordValueOffset = MatchRecords.createRecordColumnsVector(fbb, fieldColumnOffsets);
        int mrOffset = MatchRecords.createMatchRecords(fbb, fieldNamesOffset, matchRecordValueOffset);

        int error_messageOffset = fbb.createString("test error mesage");
        int error_resultOffset = ErrorResult.createErrorResult(fbb, 123, error_messageOffset);
        int[] error_resultOffsets = new int[] {error_resultOffset};
        int errorOffset = Result.createErrorVector(fbb, error_resultOffsets);
        int trace_infoOffset = fbb.createString("query result from cache");
        int resultOffset = Result.createResult(fbb, errorOffset, mrOffset, trace_infoOffset);
        int control_infosOffset = ControlInfos.createControlInfos(fbb, 10086, true);

        int pgResultValueOffset = PGResult.createResultVector(fbb, new int[] {resultOffset});
        int pgResultOffset = PGResult.createPGResult(fbb, Status.FINISHED, pgResultValueOffset, control_infosOffset);

        fbb.finish(pgResultOffset);
        return fbb.dataBuffer();
    }

    @Test
    public void NormalTest() {
        PGResult pgResult = PGResult.getRootAsPGResult(createTestData());
        Assert.assertNotNull(pgResult);
        Assert.assertEquals(1, pgResult.resultLength());
        Assert.assertEquals(Status.FINISHED, pgResult.status());
        Result result = pgResult.result(0);
        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.errorLength());
        Assert.assertEquals(123, result.error(0).errorCode());
        Assert.assertEquals("test error mesage", result.error(0).errorMessage());

        MatchRecords matchRecords = result.records();
        Assert.assertNotNull(matchRecords);

        SingleQueryResultFBByColumn sqr = new SingleQueryResultFBByColumn();
        sqr.setResult(matchRecords);


        Assert.assertFalse(sqr.hasError());
        Assert.assertNull(sqr.getErrorMsg());
        Assert.assertFalse(sqr.empty());
        Assert.assertEquals(kMaxColumnSize, sqr.size());
        Assert.assertEquals(1, sqr.getFieldNames().size());
        Assert.assertEquals("int32", sqr.getFieldNames().get(0));
        for (int i = 0; i < sqr.size(); i++) {
            MatchRecord mr = sqr.getMatchRecord(i);
            if (0 == i%2) {
                Assert.assertEquals(i, mr.getInt("int32").intValue());
            } else {
                Assert.assertEquals(-i, mr.getInt("int32").intValue());
            }
            Assert.assertNull(mr.getFieldValue("error"));
        }
        sqr.setErrorMsg("error message");
        sqr.setHasError(true);
        Assert.assertTrue(sqr.hasError());
        Assert.assertEquals("error message", sqr.getErrorMsg());
    }
}

