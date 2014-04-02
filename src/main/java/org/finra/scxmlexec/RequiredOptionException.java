package org.finra.scxmlexec;

/**
 * Created by robbinbr on 3/24/14.
 */
public class RequiredOptionException extends Exception {

    public RequiredOptionException(String option){
        super("Option " + option + " is required but not provided");
    }
}
