package org.company.transformer;

import org.apache.log4j.Logger;
import org.finra.datagenerator.consumer.DataPipe;
import org.finra.datagenerator.consumer.DataTransformer;

import java.util.Map;
import java.util.Random;

public class SampleMachineTransformer implements DataTransformer {

    protected static final Logger log = Logger.getLogger(SampleMachineTransformer.class);
    private final Random rand = new Random(System.currentTimeMillis());

    public void transform(DataPipe cr) {
        for (Map.Entry<String, String> entry : cr.getDataMap().entrySet()) {
            String value = entry.getValue();

            if (value.equals("#{customplaceholder}")) {
                // Generate a random number
                int ran = rand.nextInt();
                entry.setValue(String.valueOf(ran));
            }
        }
    }

}
