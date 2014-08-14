/*
 * Copyright 2014 DataGenerator Contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.finra.datagenerator.samples.consumer;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.finra.datagenerator.consumer.DataPipe;
import org.finra.datagenerator.writer.DataWriter;

/**
 * A DataWriter for Hadoop MapReduce Mapper Contexts
 */
public class ContextWriter implements DataWriter {

    private final Context context;
    private String[] outTemplate;

    /**
     * Create a new ContextWriter given a Mapper Context and ordered array of fields to be included in output
     *
     * @param context  A Mapper context to which records should be written
     * @param template An array of fields to be written by this Writer (in order they should be written)
     */
    public ContextWriter(final Context context, final String[] template) {
        this.context = context;
        this.outTemplate = template;
    }

    /**
     * Write to a context. Uses NullWritable for key so that only value of output string is ultimately written
     *
     * @param cr the DataPipe to write to
     */
    public void writeOutput(DataPipe cr) {
        try {
            context.write(NullWritable.get(), new Text(cr.getPipeDelimited(outTemplate)));
        } catch (Exception e) {
            throw new RuntimeException("Exception occurred while writing to Context", e);
        }
    }
}