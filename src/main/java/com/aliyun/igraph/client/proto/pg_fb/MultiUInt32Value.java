// automatically generated, do not modify

package com.aliyun.igraph.client.proto.pg_fb;

import java.nio.*;
import java.lang.*;

import com.google.flatbuffers.*;

/**
 * @author alibaba
 */
@SuppressWarnings("unused")
public final class MultiUInt32Value extends Table {
  public static MultiUInt32Value getRootAsMultiUInt32Value(ByteBuffer _bb) { return getRootAsMultiUInt32Value(_bb, new MultiUInt32Value()); }
  public static MultiUInt32Value getRootAsMultiUInt32Value(ByteBuffer _bb, MultiUInt32Value obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__init(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public MultiUInt32Value __init(int _i, ByteBuffer _bb) { bb_pos = _i; bb = _bb; return this; }

  public long value(int j) { int o = __offset(4); return o != 0 ? (long)bb.getInt(__vector(o) + j * 4) & 0xFFFFFFFFL : 0; }
  public int valueLength() { int o = __offset(4); return o != 0 ? __vector_len(o) : 0; }
  public ByteBuffer valueAsByteBuffer() { return __vector_as_bytebuffer(4, 4); }

  public static int createMultiUInt32Value(FlatBufferBuilder builder,
      int valueOffset) {
    builder.startObject(1);
    MultiUInt32Value.addValue(builder, valueOffset);
    return MultiUInt32Value.endMultiUInt32Value(builder);
  }

  public static void startMultiUInt32Value(FlatBufferBuilder builder) { builder.startObject(1); }
  public static void addValue(FlatBufferBuilder builder, int valueOffset) { builder.addOffset(0, valueOffset, 0); }
  public static int createValueVector(FlatBufferBuilder builder, int[] data) { builder.startVector(4, data.length, 4); for (int i = data.length - 1; i >= 0; i--) builder.addInt(data[i]); return builder.endVector(); }
  public static void startValueVector(FlatBufferBuilder builder, int numElems) { builder.startVector(4, numElems, 4); }
  public static int endMultiUInt32Value(FlatBufferBuilder builder) {
    int o = builder.endObject();
    return o;
  }
};

