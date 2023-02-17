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
public final class ObjectPath extends Table {
  public static ObjectPath getRootAsObjectPath(ByteBuffer _bb) { return getRootAsObjectPath(_bb, new ObjectPath()); }
  public static ObjectPath getRootAsObjectPath(ByteBuffer _bb, ObjectPath obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__init(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public ObjectPath __init(int _i, ByteBuffer _bb) { bb_pos = _i; bb = _bb; return this; }

  public ObjectVec objectList() { return objectList(new ObjectVec()); }
  public ObjectVec objectList(ObjectVec obj) { int o = __offset(4); return o != 0 ? obj.__init(__indirect(o + bb_pos), bb) : null; }
  public MultiStringValue lables(int j) { return lables(new MultiStringValue(), j); }
  public MultiStringValue lables(MultiStringValue obj, int j) { int o = __offset(6); return o != 0 ? obj.__init(__indirect(__vector(o) + j * 4), bb) : null; }
  public int lablesLength() { int o = __offset(6); return o != 0 ? __vector_len(o) : 0; }

  public static int createObjectPath(FlatBufferBuilder builder,
      int object_listOffset,
      int lablesOffset) {
    builder.startObject(2);
    ObjectPath.addLables(builder, lablesOffset);
    ObjectPath.addObjectList(builder, object_listOffset);
    return ObjectPath.endObjectPath(builder);
  }

  public static void startObjectPath(FlatBufferBuilder builder) { builder.startObject(2); }
  public static void addObjectList(FlatBufferBuilder builder, int objectListOffset) { builder.addOffset(0, objectListOffset, 0); }
  public static void addLables(FlatBufferBuilder builder, int lablesOffset) { builder.addOffset(1, lablesOffset, 0); }
  public static int createLablesVector(FlatBufferBuilder builder, int[] data) { builder.startVector(4, data.length, 4); for (int i = data.length - 1; i >= 0; i--) builder.addOffset(data[i]); return builder.endVector(); }
  public static void startLablesVector(FlatBufferBuilder builder, int numElems) { builder.startVector(4, numElems, 4); }
  public static int endObjectPath(FlatBufferBuilder builder) {
    int o = builder.endObject();
    return o;
  }
}

