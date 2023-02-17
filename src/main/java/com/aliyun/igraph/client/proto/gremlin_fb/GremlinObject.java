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
public final class GremlinObject extends Table {
  public static GremlinObject getRootAsGremlinObject(ByteBuffer _bb) { return getRootAsGremlinObject(_bb, new GremlinObject()); }
  public static GremlinObject getRootAsGremlinObject(ByteBuffer _bb, GremlinObject obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__init(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public GremlinObject __init(int _i, ByteBuffer _bb) { bb_pos = _i; bb = _bb; return this; }

  public byte valueType() { int o = __offset(4); return o != 0 ? bb.get(o + bb_pos) : 0; }
  public Table value(Table obj) { int o = __offset(6); return o != 0 ? __union(obj, o) : null; }

  public static int createGremlinObject(FlatBufferBuilder builder,
      byte value_type,
      int valueOffset) {
    builder.startObject(2);
    GremlinObject.addValue(builder, valueOffset);
    GremlinObject.addValueType(builder, value_type);
    return GremlinObject.endGremlinObject(builder);
  }

  public static void startGremlinObject(FlatBufferBuilder builder) { builder.startObject(2); }
  public static void addValueType(FlatBufferBuilder builder, byte valueType) { builder.addByte(0, valueType, 0); }
  public static void addValue(FlatBufferBuilder builder, int valueOffset) { builder.addOffset(1, valueOffset, 0); }
  public static int endGremlinObject(FlatBufferBuilder builder) {
    int o = builder.endObject();
    return o;
  }
}

