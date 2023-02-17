package com.aliyun.igraph.client.utils;

import com.aliyun.igraph.client.pg.QueryResult;
import com.aliyun.igraph.client.pg.SingleQueryResult;
import com.aliyun.igraph.client.pg.MatchRecord;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * @author alibaba
 */
public class ResultPrinter {
    static FileOutputStream Indent(FileOutputStream os, int cnt) {
        String indent_str = new String("    ");
        try {
            for (int i = 0; i < cnt; i++) {
                os.write(indent_str.getBytes());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return os;
    }

    static void PrintResult(SingleQueryResult result, int indent, FileOutputStream os) {
        Indent(os, indent);
        boolean need_dot = false;
        indent++;
        try {
            os.write(new String("{\n").getBytes());
            if (!result.empty()) {
                need_dot = true;
                Indent(os, indent).write("\"records\" :\n".getBytes());
                Indent(os, indent).write("{\n".getBytes());
                List<String> fields = result.getFieldNames();
                if (!fields.isEmpty()) {
                    indent++;
                    Indent(os, indent).write("\"fields\" :\n".getBytes());
                    indent++;
                    Indent(os, indent).write("[\n".getBytes());
                    Indent(os, indent).write(("\"" + fields.get(0) + "\"").getBytes());
                    for (int i=1; i<fields.size(); i++) {
                        os.write((",\"" + fields.get(i) + "\"").getBytes());
                    }
                    os.write('\n');
                    Indent(os, indent).write("],\n".getBytes());
                    indent--;
                    Indent(os, indent).write("\"values\" :\n".getBytes());
                    indent++;
                    Indent(os, indent).write('[');
                    for(int i=0; i<result.size(); i++) {
                        MatchRecord record = result.getMatchRecord(i);
                        if (i==0) {
                            os.write('\n');
                        } else {
                            os.write(',');
                            os.write('\n');
                        }
                        Indent(os, indent).write("{\n".getBytes());
                        indent++;
                        Indent(os, indent).write("\"value\" :\n".getBytes());
                        indent++;
                        Indent(os, indent).write('[');
                        os.write('\n');
                        for (int j=0; j<fields.size(); j++) {
                            if (j==0) {
                                Indent(os, indent);
                            } else {
                                os.write(',');
                            }
                            String field_value = record.getFieldValue(j);
                            if (field_value == null) {
                                field_value = "NULL";
                            }
                            os.write('"');
                            os.write(field_value.getBytes());
                            os.write('"');
                        }
                        os.write('\n');
                        Indent(os, indent).write(']');
                        os.write('\n');
                        indent--;
                        indent--;
                        Indent(os, indent).write('}');
                    }
                    os.write('\n');
                    Indent(os, indent).write(']');
                    indent--;
                }
                indent--;
                os.write('\n');
                Indent(os, indent).write('}');
            }
            if (result.hasError()) {
                if (need_dot) {
                    os.write(",\n".getBytes());
                } else {
                    os.write('\n');
                }
                need_dot = true;
                Indent(os, indent).write("\"error_info\" :\"".getBytes());
                os.write(result.getErrorMsg().getBytes());
                os.write('"');
            }
            os.write('\n');
            indent--;
            Indent(os, indent).write('}');
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void PrintMultiResult(QueryResult results, FileOutputStream os) throws IOException {
        int size = results.getAllQueryResult().size();
        if (0 == size) {
            os.write("{\n}\n".getBytes());
            return;
        }
        int indent = 0;
        Indent(os, indent).write("{\n".getBytes());
        indent++;
        Indent(os, indent).write("\"results\" :\n".getBytes());
        indent++;
        Indent(os, indent).write("[\n".getBytes());
        for (int i=0; i<size; i++) {
            SingleQueryResult result = results.getQueryResult(i);
            if (i != 0) {
                os.write(",\n".getBytes());
            }
            PrintResult(result, indent, os);
        }
        os.write('\n');
        Indent(os, indent).write(']');
        indent--;
        os.write('\n');
        indent--;
        Indent(os, indent).write("}\n".getBytes());
    }
}
