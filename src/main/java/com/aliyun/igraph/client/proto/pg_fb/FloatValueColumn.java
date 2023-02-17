// automatically generated, do not modify

package com.aliyun.igraph.client.proto.pg_fb;

import java.nio.*;
import java.lang.*;

import com.google.flatbuffers.*;

/**
 * @author alibaba
 */
@SuppressWarnings("unused")
public final class FloatValueColumn extends Table {
  public static FloatValueColumn getRootAsFloatValueColumn(ByteBuffer _bb) { return getRootAsFloatValueColumn(_bb, new FloatValueColumn()); }
  public static FloatValueColumn getRootAsFloatValueColumn(ByteBuffer _bb, FloatValueColumn obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__init(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public FloatValueColumn __init(int _i, ByteBuffer _bb) { bb_pos = _i; bb = _bb; return this; }

  public float value(int j) { int o = __offset(4); return o != 0 ? bb.getFloat(__vector(o) + j * 4) : 0; }
  public int valueLength() { int o = __offset(4); return o != 0 ? __vector_len(o) : 0; }
  public ByteBuffer valueAsByteBuffer() { return __vector_as_bytebuffer(4, 4); }

  public static int createFloatValueColumn(FlatBufferBuilder builder,
      int valueOffset) {
    builder.startObject(1);
    FloatValueColumn.addValue(builder, valueOffset);
    return FloatValueColumn.endFloatValueColumn(builder);
  }

  public static void startFloatValueColumn(FlatBufferBuilder builder) { builder.startObject(1); }
  public static void addValue(FlatBufferBuilder builder, int valueOffset) { builder.addOffset(0, valueOffset, 0); }
  public static int createValueVector(FlatBufferBuilder builder, float[] data) { builder.startVector(4, data.length, 4); for (int i = data.length - 1; i >= 0; i--) builder.addFloat(data[i]); return builder.endVector(); }
  public static void startValueVector(FlatBufferBuilder builder, int numElems) { builder.startVector(4, numElems, 4); }
  public static int endFloatValueColumn(FlatBufferBuilder builder) {
    int o = builder.endObject();
    return o;
  }
};

