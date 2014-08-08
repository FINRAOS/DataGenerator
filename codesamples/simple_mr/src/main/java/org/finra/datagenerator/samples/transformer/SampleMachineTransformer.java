package org.finra.datagenerator.samples.transformer;

import org.finra.datagenerator.consumer.DataPipe;
import org.finra.datagenerator.consumer.DataTransformer;

import java.util.Map;
import java.util.Random;

public class SampleMachineTransformer implements DataTransformer {

    private final Random rand = new Random(System.currentTimeMillis());

    public void transform(DataPipe cr) {
        for (Map.Entry<String, String> entry : cr.getDataMap().entrySet()) {
            String value = entry.getValue();

            if (value.equals("#{customplaceholder}")) {
                // Generate a random number
                int ran = rand.nextInt();
                entry.setValue(String.valueOf(ran));
            } else if (value.equals("#{divideBy2}")) {
                String result = String.valueOf(Integer.getInteger(cr.getDataMap().get("var_out_V3")) / 2);
                entry.setValue(result);
            }
        }
    }

}
