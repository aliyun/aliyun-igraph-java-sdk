// automatically generated by the FlatBuffers compiler, do not modify

package com.aliyun.igraph.client.proto.gremlin_fb;

import com.google.flatbuffers.FlatBufferBuilder;
import com.google.flatbuffers.Table;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * @author alibaba
 */
@SuppressWarnings("unused")
public final class FloatValue extends Table {
  public static FloatValue getRootAsFloatValue(ByteBuffer _bb) { return getRootAsFloatValue(_bb, new FloatValue()); }
  public static FloatValue getRootAsFloatValue(ByteBuffer _bb, FloatValue obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__init(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public FloatValue __init(int _i, ByteBuffer _bb) { bb_pos = _i; bb = _bb; return this; }

  public float value() { int o = __offset(4); return o != 0 ? bb.getFloat(o + bb_pos) : 0.0f; }

  public static int createFloatValue(FlatBufferBuilder builder,
      float value) {
    builder.startObject(1);
    FloatValue.addValue(builder, value);
    return FloatValue.endFloatValue(builder);
  }

  public static void startFloatValue(FlatBufferBuilder builder) { builder.startObject(1); }
  public static void addValue(FlatBufferBuilder builder, float value) { builder.addFloat(0, value, 0.0f); }
  public static int endFloatValue(FlatBufferBuilder builder) {
    int o = builder.endObject();
    return o;
  }
}

