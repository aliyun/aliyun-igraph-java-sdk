// automatically generated, do not modify

package com.aliyun.igraph.client.proto.pg_fb;

import java.nio.*;
import java.lang.*;

import com.google.flatbuffers.*;

/**
 * @author alibaba
 */
@SuppressWarnings("unused")
public final class MultiInt16Value extends Table {
  public static MultiInt16Value getRootAsMultiInt16Value(ByteBuffer _bb) { return getRootAsMultiInt16Value(_bb, new MultiInt16Value()); }
  public static MultiInt16Value getRootAsMultiInt16Value(ByteBuffer _bb, MultiInt16Value obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__init(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public MultiInt16Value __init(int _i, ByteBuffer _bb) { bb_pos = _i; bb = _bb; return this; }

  public short value(int j) { int o = __offset(4); return o != 0 ? bb.getShort(__vector(o) + j * 2) : 0; }
  public int valueLength() { int o = __offset(4); return o != 0 ? __vector_len(o) : 0; }
  public ByteBuffer valueAsByteBuffer() { return __vector_as_bytebuffer(4, 2); }

  public static int createMultiInt16Value(FlatBufferBuilder builder,
      int valueOffset) {
    builder.startObject(1);
    MultiInt16Value.addValue(builder, valueOffset);
    return MultiInt16Value.endMultiInt16Value(builder);
  }

  public static void startMultiInt16Value(FlatBufferBuilder builder) { builder.startObject(1); }
  public static void addValue(FlatBufferBuilder builder, int valueOffset) { builder.addOffset(0, valueOffset, 0); }
  public static int createValueVector(FlatBufferBuilder builder, short[] data) { builder.startVector(2, data.length, 2); for (int i = data.length - 1; i >= 0; i--) builder.addShort(data[i]); return builder.endVector(); }
  public static void startValueVector(FlatBufferBuilder builder, int numElems) { builder.startVector(2, numElems, 2); }
  public static int endMultiInt16Value(FlatBufferBuilder builder) {
    int o = builder.endObject();
    return o;
  }
};

