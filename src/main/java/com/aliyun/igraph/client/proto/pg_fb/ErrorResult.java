// automatically generated, do not modify

package com.aliyun.igraph.client.proto.pg_fb;

import java.nio.*;
import java.lang.*;

import com.google.flatbuffers.*;

/**
 * @author alibaba
 */
@SuppressWarnings("unused")
public final class ErrorResult extends Table {
  public static ErrorResult getRootAsErrorResult(ByteBuffer _bb) { return getRootAsErrorResult(_bb, new ErrorResult()); }
  public static ErrorResult getRootAsErrorResult(ByteBuffer _bb, ErrorResult obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__init(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public ErrorResult __init(int _i, ByteBuffer _bb) { bb_pos = _i; bb = _bb; return this; }

  public long errorCode() { int o = __offset(4); return o != 0 ? (long)bb.getInt(o + bb_pos) & 0xFFFFFFFFL : 0; }
  public String errorMessage() { int o = __offset(6); return o != 0 ? __string(o + bb_pos) : null; }
  public ByteBuffer errorMessageAsByteBuffer() { return __vector_as_bytebuffer(6, 1); }

  public static int createErrorResult(FlatBufferBuilder builder,
      long error_code,
      int error_messageOffset) {
    builder.startObject(2);
    ErrorResult.addErrorMessage(builder, error_messageOffset);
    ErrorResult.addErrorCode(builder, error_code);
    return ErrorResult.endErrorResult(builder);
  }

  public static void startErrorResult(FlatBufferBuilder builder) { builder.startObject(2); }
  public static void addErrorCode(FlatBufferBuilder builder, long errorCode) { builder.addInt(0, (int)errorCode, 0); }
  public static void addErrorMessage(FlatBufferBuilder builder, int errorMessageOffset) { builder.addOffset(1, errorMessageOffset, 0); }
  public static int endErrorResult(FlatBufferBuilder builder) {
    int o = builder.endObject();
    return o;
  }
};

