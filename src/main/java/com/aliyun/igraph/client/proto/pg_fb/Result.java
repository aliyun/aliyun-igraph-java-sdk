// automatically generated, do not modify

package com.aliyun.igraph.client.proto.pg_fb;

import java.nio.*;
import java.lang.*;

import com.google.flatbuffers.*;

/**
 * @author alibaba
 */
@SuppressWarnings("unused")
public final class Result extends Table {
  public static Result getRootAsResult(ByteBuffer _bb) { return getRootAsResult(_bb, new Result()); }
  public static Result getRootAsResult(ByteBuffer _bb, Result obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__init(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public Result __init(int _i, ByteBuffer _bb) { bb_pos = _i; bb = _bb; return this; }

  public ErrorResult error(int j) { return error(new ErrorResult(), j); }
  public ErrorResult error(ErrorResult obj, int j) { int o = __offset(4); return o != 0 ? obj.__init(__indirect(__vector(o) + j * 4), bb) : null; }
  public int errorLength() { int o = __offset(4); return o != 0 ? __vector_len(o) : 0; }
  public MatchRecords records() { return records(new MatchRecords()); }
  public MatchRecords records(MatchRecords obj) { int o = __offset(6); return o != 0 ? obj.__init(__indirect(o + bb_pos), bb) : null; }
  public String traceInfo() { int o = __offset(8); return o != 0 ? __string(o + bb_pos) : null; }
  public ByteBuffer traceInfoAsByteBuffer() { return __vector_as_bytebuffer(8, 1); }

  public static int createResult(FlatBufferBuilder builder,
      int errorOffset,
      int recordsOffset,
      int trace_infoOffset) {
    builder.startObject(3);
    Result.addTraceInfo(builder, trace_infoOffset);
    Result.addRecords(builder, recordsOffset);
    Result.addError(builder, errorOffset);
    return Result.endResult(builder);
  }

  public static void startResult(FlatBufferBuilder builder) { builder.startObject(3); }
  public static void addError(FlatBufferBuilder builder, int errorOffset) { builder.addOffset(0, errorOffset, 0); }
  public static int createErrorVector(FlatBufferBuilder builder, int[] data) { builder.startVector(4, data.length, 4); for (int i = data.length - 1; i >= 0; i--) builder.addOffset(data[i]); return builder.endVector(); }
  public static void startErrorVector(FlatBufferBuilder builder, int numElems) { builder.startVector(4, numElems, 4); }
  public static void addRecords(FlatBufferBuilder builder, int recordsOffset) { builder.addOffset(1, recordsOffset, 0); }
  public static void addTraceInfo(FlatBufferBuilder builder, int traceInfoOffset) { builder.addOffset(2, traceInfoOffset, 0); }
  public static int endResult(FlatBufferBuilder builder) {
    int o = builder.endObject();
    return o;
  }
};

