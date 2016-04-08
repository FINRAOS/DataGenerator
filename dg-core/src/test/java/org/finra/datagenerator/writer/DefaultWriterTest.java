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

import org.finra.datagenerator.consumer.DataPipe;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.util.Map;

/**
 * Created by mibrahim on 9/11/15.
 */
public class DefaultWriterTest {
    /**
     * Test the delimiter
     */
    @Test
    public void testDelimiter() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        DefaultWriter writer = new DefaultWriter(bos, new String[]{"a", "b", "c"});

        writer.setSeparator("^");
        DataPipe pipe = new DataPipe();
        Map<String, String> data = pipe.getDataMap();
        data.put("a", "av");
        data.put("b", "bv");
        data.put("c", "cv");

        writer.writeOutput(pipe);

        String result = new String(bos.toByteArray());
        Assert.assertTrue("'" + result + "'", result.equals("av^bv^cv\n"));
    }
}
