// automatically generated, do not modify

package com.aliyun.igraph.client.proto.pg_fb;

import java.nio.*;
import java.lang.*;

import com.google.flatbuffers.*;

/**
 * @author alibaba
 */
@SuppressWarnings("unused")
public final class MultiStringValue extends Table {
  public static MultiStringValue getRootAsMultiStringValue(ByteBuffer _bb) { return getRootAsMultiStringValue(_bb, new MultiStringValue()); }
  public static MultiStringValue getRootAsMultiStringValue(ByteBuffer _bb, MultiStringValue obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__init(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public MultiStringValue __init(int _i, ByteBuffer _bb) { bb_pos = _i; bb = _bb; return this; }

  public String value(int j) { int o = __offset(4); return o != 0 ? __string(__vector(o) + j * 4) : null; }
  public int valueLength() { int o = __offset(4); return o != 0 ? __vector_len(o) : 0; }

  public static int createMultiStringValue(FlatBufferBuilder builder,
      int valueOffset) {
    builder.startObject(1);
    MultiStringValue.addValue(builder, valueOffset);
    return MultiStringValue.endMultiStringValue(builder);
  }

  public static void startMultiStringValue(FlatBufferBuilder builder) { builder.startObject(1); }
  public static void addValue(FlatBufferBuilder builder, int valueOffset) { builder.addOffset(0, valueOffset, 0); }
  public static int createValueVector(FlatBufferBuilder builder, int[] data) { builder.startVector(4, data.length, 4); for (int i = data.length - 1; i >= 0; i--) builder.addOffset(data[i]); return builder.endVector(); }
  public static void startValueVector(FlatBufferBuilder builder, int numElems) { builder.startVector(4, numElems, 4); }
  public static int endMultiStringValue(FlatBufferBuilder builder) {
    int o = builder.endObject();
    return o;
  }
};

