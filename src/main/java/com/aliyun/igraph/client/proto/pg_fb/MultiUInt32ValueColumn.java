// automatically generated, do not modify

package com.aliyun.igraph.client.proto.pg_fb;

import java.nio.*;
import java.lang.*;

import com.google.flatbuffers.*;

/**
 * @author alibaba
 */
@SuppressWarnings("unused")
public final class MultiUInt32ValueColumn extends Table {
  public static MultiUInt32ValueColumn getRootAsMultiUInt32ValueColumn(ByteBuffer _bb) { return getRootAsMultiUInt32ValueColumn(_bb, new MultiUInt32ValueColumn()); }
  public static MultiUInt32ValueColumn getRootAsMultiUInt32ValueColumn(ByteBuffer _bb, MultiUInt32ValueColumn obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__init(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public MultiUInt32ValueColumn __init(int _i, ByteBuffer _bb) { bb_pos = _i; bb = _bb; return this; }

  public MultiUInt32Value value(int j) { return value(new MultiUInt32Value(), j); }
  public MultiUInt32Value value(MultiUInt32Value obj, int j) { int o = __offset(4); return o != 0 ? obj.__init(__indirect(__vector(o) + j * 4), bb) : null; }
  public int valueLength() { int o = __offset(4); return o != 0 ? __vector_len(o) : 0; }

  public static int createMultiUInt32ValueColumn(FlatBufferBuilder builder,
      int valueOffset) {
    builder.startObject(1);
    MultiUInt32ValueColumn.addValue(builder, valueOffset);
    return MultiUInt32ValueColumn.endMultiUInt32ValueColumn(builder);
  }

  public static void startMultiUInt32ValueColumn(FlatBufferBuilder builder) { builder.startObject(1); }
  public static void addValue(FlatBufferBuilder builder, int valueOffset) { builder.addOffset(0, valueOffset, 0); }
  public static int createValueVector(FlatBufferBuilder builder, int[] data) { builder.startVector(4, data.length, 4); for (int i = data.length - 1; i >= 0; i--) builder.addOffset(data[i]); return builder.endVector(); }
  public static void startValueVector(FlatBufferBuilder builder, int numElems) { builder.startVector(4, numElems, 4); }
  public static int endMultiUInt32ValueColumn(FlatBufferBuilder builder) {
    int o = builder.endObject();
    return o;
  }
};

