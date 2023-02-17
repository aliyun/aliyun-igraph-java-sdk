// automatically generated, do not modify

package com.aliyun.igraph.client.proto.pg_fb;

import java.nio.*;
import java.lang.*;

import com.google.flatbuffers.*;

/**
 * @author alibaba
 */
@SuppressWarnings("unused")
public final class MultiInt64ValueColumn extends Table {
  public static MultiInt64ValueColumn getRootAsMultiInt64ValueColumn(ByteBuffer _bb) { return getRootAsMultiInt64ValueColumn(_bb, new MultiInt64ValueColumn()); }
  public static MultiInt64ValueColumn getRootAsMultiInt64ValueColumn(ByteBuffer _bb, MultiInt64ValueColumn obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__init(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public MultiInt64ValueColumn __init(int _i, ByteBuffer _bb) { bb_pos = _i; bb = _bb; return this; }

  public MultiInt64Value value(int j) { return value(new MultiInt64Value(), j); }
  public MultiInt64Value value(MultiInt64Value obj, int j) { int o = __offset(4); return o != 0 ? obj.__init(__indirect(__vector(o) + j * 4), bb) : null; }
  public int valueLength() { int o = __offset(4); return o != 0 ? __vector_len(o) : 0; }

  public static int createMultiInt64ValueColumn(FlatBufferBuilder builder,
      int valueOffset) {
    builder.startObject(1);
    MultiInt64ValueColumn.addValue(builder, valueOffset);
    return MultiInt64ValueColumn.endMultiInt64ValueColumn(builder);
  }

  public static void startMultiInt64ValueColumn(FlatBufferBuilder builder) { builder.startObject(1); }
  public static void addValue(FlatBufferBuilder builder, int valueOffset) { builder.addOffset(0, valueOffset, 0); }
  public static int createValueVector(FlatBufferBuilder builder, int[] data) { builder.startVector(4, data.length, 4); for (int i = data.length - 1; i >= 0; i--) builder.addOffset(data[i]); return builder.endVector(); }
  public static void startValueVector(FlatBufferBuilder builder, int numElems) { builder.startVector(4, numElems, 4); }
  public static int endMultiInt64ValueColumn(FlatBufferBuilder builder) {
    int o = builder.endObject();
    return o;
  }
};

