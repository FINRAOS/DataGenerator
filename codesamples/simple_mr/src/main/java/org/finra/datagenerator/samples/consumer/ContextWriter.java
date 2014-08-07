package org.finra.datagenerator.samples.consumer;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.finra.datagenerator.consumer.DataPipe;
import org.finra.datagenerator.writer.DataWriter;
import org.apache.hadoop.io.Text;

public class ContextWriter implements DataWriter {

    private final Context context;
    private String[] outTemplate;

    public ContextWriter(Context context, String[] template) {
        this.context = context;
        this.outTemplate = template;
    }

    public void writeOutput(DataPipe cr) {
        try {
            context.write(NullWritable.get(), new Text(cr.getPipeDelimited(outTemplate)));
        } catch (Exception e) {

        }
    }
}