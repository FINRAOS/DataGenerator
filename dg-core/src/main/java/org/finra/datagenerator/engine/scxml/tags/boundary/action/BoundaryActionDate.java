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
 * Action for Date type
 */
public class BoundaryActionDate extends BoundaryAction {

    private String earliest;
    private String latest;
    private String onlyBusinessDays;

    public String getEarliest() {
        return earliest;
    }

    public String getLatest() {
        return latest;
    }

    public String getOnlyBusinessDays() {
        return onlyBusinessDays;
    }

    public void setEarliest(String earliest) {
        this.earliest = earliest;
    }

    public void setLatest(String latest) {
        this.latest = latest;
    }

    public void setOnlyBusinessDays(String onlyBusinessDays) {
        this.onlyBusinessDays = onlyBusinessDays;
    }
}
