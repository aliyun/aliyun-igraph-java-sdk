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
public final class ElementMeta extends Table {
  public static ElementMeta getRootAsElementMeta(ByteBuffer _bb) { return getRootAsElementMeta(_bb, new ElementMeta()); }
  public static ElementMeta getRootAsElementMeta(ByteBuffer _bb, ElementMeta obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__init(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public ElementMeta __init(int _i, ByteBuffer _bb) { bb_pos = _i; bb = _bb; return this; }

  public long metaId() { int o = __offset(4); return o != 0 ? bb.getLong(o + bb_pos) : 0; }
  public String allocatorName() { int o = __offset(6); return o != 0 ? __string(o + bb_pos) : null; }
  public ByteBuffer allocatorNameAsByteBuffer() { return __vector_as_bytebuffer(6, 1); }
  public String label() { int o = __offset(8); return o != 0 ? __string(o + bb_pos) : null; }
  public ByteBuffer labelAsByteBuffer() { return __vector_as_bytebuffer(8, 1); }
  public String propertyNames(int j) { int o = __offset(10); return o != 0 ? __string(__vector(o) + j * 4) : null; }
  public int propertyNamesLength() { int o = __offset(10); return o != 0 ? __vector_len(o) : 0; }
  public long type() { int o = __offset(12); return o != 0 ? (long)bb.getInt(o + bb_pos) & 0xFFFFFFFFL : 0; }

  public static int createElementMeta(FlatBufferBuilder builder,
      long meta_id,
      int allocator_nameOffset,
      int labelOffset,
      int property_namesOffset,
      long type) {
    builder.startObject(5);
    ElementMeta.addMetaId(builder, meta_id);
    ElementMeta.addType(builder, type);
    ElementMeta.addPropertyNames(builder, property_namesOffset);
    ElementMeta.addLabel(builder, labelOffset);
    ElementMeta.addAllocatorName(builder, allocator_nameOffset);
    return ElementMeta.endElementMeta(builder);
  }

  public static void startElementMeta(FlatBufferBuilder builder) { builder.startObject(5); }
  public static void addMetaId(FlatBufferBuilder builder, long metaId) { builder.addLong(0, metaId, 0); }
  public static void addAllocatorName(FlatBufferBuilder builder, int allocatorNameOffset) { builder.addOffset(1, allocatorNameOffset, 0); }
  public static void addLabel(FlatBufferBuilder builder, int labelOffset) { builder.addOffset(2, labelOffset, 0); }
  public static void addPropertyNames(FlatBufferBuilder builder, int propertyNamesOffset) { builder.addOffset(3, propertyNamesOffset, 0); }
  public static int createPropertyNamesVector(FlatBufferBuilder builder, int[] data) { builder.startVector(4, data.length, 4); for (int i = data.length - 1; i >= 0; i--) builder.addOffset(data[i]); return builder.endVector(); }
  public static void startPropertyNamesVector(FlatBufferBuilder builder, int numElems) { builder.startVector(4, numElems, 4); }
  public static void addType(FlatBufferBuilder builder, long type) { builder.addInt(4, (int)type, 0); }
  public static int endElementMeta(FlatBufferBuilder builder) {
    int o = builder.endObject();
    return o;
  }
}

