// automatically generated, do not modify

package com.aliyun.igraph.client.proto.pg_fb;

import java.nio.*;
import java.lang.*;

import com.google.flatbuffers.*;

/**
 * @author alibaba
 */
@SuppressWarnings("unused")
public final class Int32ValueColumn extends Table {
  public static Int32ValueColumn getRootAsInt32ValueColumn(ByteBuffer _bb) { return getRootAsInt32ValueColumn(_bb, new Int32ValueColumn()); }
  public static Int32ValueColumn getRootAsInt32ValueColumn(ByteBuffer _bb, Int32ValueColumn obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__init(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public Int32ValueColumn __init(int _i, ByteBuffer _bb) { bb_pos = _i; bb = _bb; return this; }

  public int value(int j) { int o = __offset(4); return o != 0 ? bb.getInt(__vector(o) + j * 4) : 0; }
  public int valueLength() { int o = __offset(4); return o != 0 ? __vector_len(o) : 0; }
  public ByteBuffer valueAsByteBuffer() { return __vector_as_bytebuffer(4, 4); }

  public static int createInt32ValueColumn(FlatBufferBuilder builder,
      int valueOffset) {
    builder.startObject(1);
    Int32ValueColumn.addValue(builder, valueOffset);
    return Int32ValueColumn.endInt32ValueColumn(builder);
  }

  public static void startInt32ValueColumn(FlatBufferBuilder builder) { builder.startObject(1); }
  public static void addValue(FlatBufferBuilder builder, int valueOffset) { builder.addOffset(0, valueOffset, 0); }
  public static int createValueVector(FlatBufferBuilder builder, int[] data) { builder.startVector(4, data.length, 4); for (int i = data.length - 1; i >= 0; i--) builder.addInt(data[i]); return builder.endVector(); }
  public static void startValueVector(FlatBufferBuilder builder, int numElems) { builder.startVector(4, numElems, 4); }
  public static int endInt32ValueColumn(FlatBufferBuilder builder) {
    int o = builder.endObject();
    return o;
  }
};

