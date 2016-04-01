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
package org.finra.datagenerator.engine.scxml.tags.boundary.action;

/**
 * Action for Decimal types
 */
public class BoundaryActionDecimal extends BoundaryActionNumeric {

    private String maxLen;
    private String minLen;
    private String length;

    public String getLength() {
        return length;
    }

    public String getMaxLen() {
        return maxLen;
    }

    public String getMinLen() {
        return minLen;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public void setMaxLen(String maxLen) {
        this.maxLen = maxLen;
    }

    public void setMinLen(String minLen) {
        this.minLen = minLen;
    }
}

