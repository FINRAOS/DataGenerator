package org.finra.datagenerator.engine.scxml;

import org.apache.commons.logging.Log;
import org.apache.commons.scxml.ErrorReporter;
import org.apache.commons.scxml.EventDispatcher;
import org.apache.commons.scxml.SCInstance;
import org.apache.commons.scxml.SCXMLExpressionException;
import org.apache.commons.scxml.model.Action;
import org.apache.commons.scxml.model.ModelException;

import java.util.Collection;

/**
 * Marshall Peters
 * Date: 9/15/14
 */
public class Transform extends Action {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Required implementation of an abstract method in Action
     *
     * @param eventDispatcher unused
     * @param errorReporter unused
     * @param scInstance unused
     * @param log unused
     * @param collection unused
     * @throws ModelException never
     * @throws SCXMLExpressionException never
     */
    public void execute(EventDispatcher eventDispatcher, ErrorReporter errorReporter, SCInstance scInstance, Log log,
                        Collection collection) throws ModelException, SCXMLExpressionException {
        //Handled manually
    }
}
