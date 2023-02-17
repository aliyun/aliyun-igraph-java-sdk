// automatically generated, do not modify

package com.aliyun.igraph.client.proto.pg_fb;

import java.nio.*;
import java.lang.*;

import com.google.flatbuffers.*;

/**
 * @author alibaba
 */
@SuppressWarnings("unused")
public final class MultiInt8Value extends Table {
  public static MultiInt8Value getRootAsMultiInt8Value(ByteBuffer _bb) { return getRootAsMultiInt8Value(_bb, new MultiInt8Value()); }
  public static MultiInt8Value getRootAsMultiInt8Value(ByteBuffer _bb, MultiInt8Value obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__init(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public MultiInt8Value __init(int _i, ByteBuffer _bb) { bb_pos = _i; bb = _bb; return this; }

  public byte value(int j) { int o = __offset(4); return o != 0 ? bb.get(__vector(o) + j * 1) : 0; }
  public int valueLength() { int o = __offset(4); return o != 0 ? __vector_len(o) : 0; }
  public ByteBuffer valueAsByteBuffer() { return __vector_as_bytebuffer(4, 1); }

  public static int createMultiInt8Value(FlatBufferBuilder builder,
      int valueOffset) {
    builder.startObject(1);
    MultiInt8Value.addValue(builder, valueOffset);
    return MultiInt8Value.endMultiInt8Value(builder);
  }

  public static void startMultiInt8Value(FlatBufferBuilder builder) { builder.startObject(1); }
  public static void addValue(FlatBufferBuilder builder, int valueOffset) { builder.addOffset(0, valueOffset, 0); }
  public static int createValueVector(FlatBufferBuilder builder, byte[] data) { builder.startVector(1, data.length, 1); for (int i = data.length - 1; i >= 0; i--) builder.addByte(data[i]); return builder.endVector(); }
  public static void startValueVector(FlatBufferBuilder builder, int numElems) { builder.startVector(1, numElems, 1); }
  public static int endMultiInt8Value(FlatBufferBuilder builder) {
    int o = builder.endObject();
    return o;
  }
};

