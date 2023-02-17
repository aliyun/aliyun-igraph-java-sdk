// automatically generated, do not modify

package com.aliyun.igraph.client.proto.pg_fb;

import java.nio.*;
import java.lang.*;

import com.google.flatbuffers.*;

/**
 * @author alibaba
 */
@SuppressWarnings("unused")
public final class MultiFloatValueColumn extends Table {
  public static MultiFloatValueColumn getRootAsMultiFloatValueColumn(ByteBuffer _bb) { return getRootAsMultiFloatValueColumn(_bb, new MultiFloatValueColumn()); }
  public static MultiFloatValueColumn getRootAsMultiFloatValueColumn(ByteBuffer _bb, MultiFloatValueColumn obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__init(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public MultiFloatValueColumn __init(int _i, ByteBuffer _bb) { bb_pos = _i; bb = _bb; return this; }

  public MultiFloatValue value(int j) { return value(new MultiFloatValue(), j); }
  public MultiFloatValue value(MultiFloatValue obj, int j) { int o = __offset(4); return o != 0 ? obj.__init(__indirect(__vector(o) + j * 4), bb) : null; }
  public int valueLength() { int o = __offset(4); return o != 0 ? __vector_len(o) : 0; }

  public static int createMultiFloatValueColumn(FlatBufferBuilder builder,
      int valueOffset) {
    builder.startObject(1);
    MultiFloatValueColumn.addValue(builder, valueOffset);
    return MultiFloatValueColumn.endMultiFloatValueColumn(builder);
  }

  public static void startMultiFloatValueColumn(FlatBufferBuilder builder) { builder.startObject(1); }
  public static void addValue(FlatBufferBuilder builder, int valueOffset) { builder.addOffset(0, valueOffset, 0); }
  public static int createValueVector(FlatBufferBuilder builder, int[] data) { builder.startVector(4, data.length, 4); for (int i = data.length - 1; i >= 0; i--) builder.addOffset(data[i]); return builder.endVector(); }
  public static void startValueVector(FlatBufferBuilder builder, int numElems) { builder.startVector(4, numElems, 4); }
  public static int endMultiFloatValueColumn(FlatBufferBuilder builder) {
    int o = builder.endObject();
    return o;
  }
};

