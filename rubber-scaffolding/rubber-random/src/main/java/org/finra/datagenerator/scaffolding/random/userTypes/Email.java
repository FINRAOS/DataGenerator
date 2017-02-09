package org.finra.datagenerator.scaffolding.random.userTypes;

/**
 * Created by dkopel on 1/26/17.
 */
public class Email {
    private String user;

    private String domain;

    private String tld;

    public Email(String user, String domain, String tld) {
        this.user = user;
        this.domain = domain;
        this.tld = tld;
    }

    public String getUser() {
        return user;
    }

    public String getDomain() {
        return domain;
    }

    public String getTld() {
        return tld;
    }

    @Override
    public String toString() {
        return user+"@"+domain+"."+tld;
    }
}
