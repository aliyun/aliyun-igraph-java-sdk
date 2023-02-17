// automatically generated, do not modify

package com.aliyun.igraph.client.proto.pg_fb;

import java.nio.*;
import java.lang.*;

import com.google.flatbuffers.*;

/**
 * @author alibaba
 */
@SuppressWarnings("unused")
public final class MultiDoubleValue extends Table {
  public static MultiDoubleValue getRootAsMultiDoubleValue(ByteBuffer _bb) { return getRootAsMultiDoubleValue(_bb, new MultiDoubleValue()); }
  public static MultiDoubleValue getRootAsMultiDoubleValue(ByteBuffer _bb, MultiDoubleValue obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__init(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public MultiDoubleValue __init(int _i, ByteBuffer _bb) { bb_pos = _i; bb = _bb; return this; }

  public double value(int j) { int o = __offset(4); return o != 0 ? bb.getDouble(__vector(o) + j * 8) : 0; }
  public int valueLength() { int o = __offset(4); return o != 0 ? __vector_len(o) : 0; }
  public ByteBuffer valueAsByteBuffer() { return __vector_as_bytebuffer(4, 8); }

  public static int createMultiDoubleValue(FlatBufferBuilder builder,
      int valueOffset) {
    builder.startObject(1);
    MultiDoubleValue.addValue(builder, valueOffset);
    return MultiDoubleValue.endMultiDoubleValue(builder);
  }

  public static void startMultiDoubleValue(FlatBufferBuilder builder) { builder.startObject(1); }
  public static void addValue(FlatBufferBuilder builder, int valueOffset) { builder.addOffset(0, valueOffset, 0); }
  public static int createValueVector(FlatBufferBuilder builder, double[] data) { builder.startVector(8, data.length, 8); for (int i = data.length - 1; i >= 0; i--) builder.addDouble(data[i]); return builder.endVector(); }
  public static void startValueVector(FlatBufferBuilder builder, int numElems) { builder.startVector(8, numElems, 8); }
  public static int endMultiDoubleValue(FlatBufferBuilder builder) {
    int o = builder.endObject();
    return o;
  }
};

