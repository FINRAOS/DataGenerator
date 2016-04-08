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

import org.apache.commons.logging.Log;
import org.apache.commons.scxml.ErrorReporter;
import org.apache.commons.scxml.EventDispatcher;
import org.apache.commons.scxml.SCInstance;
import org.apache.commons.scxml.SCXMLExpressionException;
import org.apache.commons.scxml.model.Action;
import org.apache.commons.scxml.model.ModelException;

import java.util.Collection;

/**
 *  Action for all types
 */
public class BoundaryAction extends Action {

    private String name;
    private String nullable = "true";

    public String getName() {
        return name;
    }

    public String getNullable() {

        return nullable;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNullable(String nullable) {
        this.nullable = nullable;
    }

    @Override
    public void execute(EventDispatcher eventDispatcher, ErrorReporter errorReporter,
                        SCInstance scInstance, Log log, Collection collection)
        throws ModelException, SCXMLExpressionException {

    }
}


