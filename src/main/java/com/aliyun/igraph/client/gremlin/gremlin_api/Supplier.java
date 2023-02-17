package com.aliyun.igraph.client.gremlin.gremlin_api;


/**
 * @author alibaba
 */
public class Supplier {
    private String supplierString;

    public Supplier(String supplierString) {
        this.supplierString = supplierString;
    }

    public static Supplier supplier(SupplierType supplierType, SackType sackType, String sackInitial) {
        StringBuilder ss = new StringBuilder(1024);
        ss.append("supplier(");
        StringBuilderHelper.appendParameter(ss, false, supplierType);
        StringBuilderHelper.appendParameter(ss, true, sackType);
        StringBuilderHelper.appendParameter(ss, true, sackInitial);
        ss.append(')');
        return new Supplier(ss.toString());
    }

    public static Supplier supplier(SupplierType supplierType, String sackInitial) {
        StringBuilder ss = new StringBuilder(1024);
        ss.append("supplier(");
        StringBuilderHelper.appendParameter(ss, false, supplierType);
        StringBuilderHelper.appendParameter(ss, true, sackInitial);
        ss.append(')');
        return new Supplier(ss.toString());
    }

    @Override
    public String toString() {
        return supplierString;
    }

    public enum SupplierType {
        NORMAL("normal"),
        KV("kv"),
        KKV("kkv");

        String value;
        SupplierType(String value) {
            this.value = value;
        }
        @Override
        public String toString() {
            return this.value;
        }
    }

    public enum SackType {
        FLOAT("float"),
        INTEGER("integer"),
        STRING("string");

        String value;
        SackType(String value) {
            this.value = value;
        }
        @Override
        public String toString() {
            return this.value;
        }
    }
}
