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
package org.finra.scxmlexec;

import org.apache.commons.scxml.Context;
import org.apache.log4j.Logger;

/**
 *
 * @author mosama
 */
public class PipeDelimitedOutputFormatter implements OutputFormatter {

    private static final Logger log = Logger.getLogger(PipeDelimitedOutputFormatter.class);

    private final String[] variablesList;

    public PipeDelimitedOutputFormatter(ChartExec chartExec) {
        String outputVariables = chartExec.getOutputVariables();

        if (outputVariables == null) {
            variablesList = null;
            throw new RuntimeException("outputVariables is null, cannot proceed");
        }
        variablesList = outputVariables.split(",");
    }

    @Override
    public byte[][] generateOutput(ChartExec chartExec, Context context) {
        StringBuilder b = new StringBuilder();
        for (String field : variablesList) {
            if (b.length() > 0) {
                b.append("|");
            }
            Object value = context.get(field);
            String stringValue = "";
            if (value != null) {
                stringValue = value.toString();
            }

            b.append(stringValue);
        }
        return new byte[][]{b.toString().getBytes()};
    }

}
