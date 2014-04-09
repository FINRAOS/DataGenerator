/*
 * Copyright 2014 mosama.
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
package org.finra.datagenerator.consumer.defaults;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;
import org.finra.datagenerator.consumer.DataConsumer;

public class DefaultOutput implements DataConsumer {

    private final OutputStream os;

    public DefaultOutput(OutputStream os) {
        this.os = os;
    }

    @Override
    public void consume(HashMap<String, String> row, AtomicBoolean exitFlag) {
        // Concatenate all data
        StringBuilder b = new StringBuilder(1024);
        for (Entry<String, String> entry : row.entrySet()) {
            if (b.length() > 0) {
                b.append('|');
            }
            b.append(entry.getValue());
        }
        b.append("\n");
        try {
            os.write(b.toString().getBytes());
        } catch (IOException ex) {
            throw new RuntimeException("Error while writing to the output stream", ex);
        }
    }
}
