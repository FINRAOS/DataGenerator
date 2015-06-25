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
package org.finra.datagenerator.writer;

import org.apache.log4j.Logger;
import org.finra.datagenerator.consumer.DataPipe;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

/**
 * Created by mibrahim on 6/25/15.
 */
public class AllFieldsWriter implements DataWriter {
    /**
     * Logger
     */
    protected static final Logger log = Logger.getLogger(AllFieldsWriter.class);
    private final OutputStream os;
    private String[] outTemplate;

    /**
     * Constructor
     *
     * @param os          the output stream to use in writing
     */
    public AllFieldsWriter(final OutputStream os) {
        this.os = os;
    }

    @Override
    public void writeOutput(DataPipe cr) {
        try {
            if (outTemplate == null) {
                outTemplate = cr.getDataMap().keySet().toArray(new String[cr.getDataMap().size()]);
                Arrays.sort(outTemplate);
            }

            os.write(cr.getPipeDelimited(outTemplate).getBytes());
            os.write("\n".getBytes());
        } catch (IOException e) {
            log.error("IOException in DefaultConsumer", e);
        }
    }
}
