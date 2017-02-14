package org.finra.datagenerator.scaffolding.random;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.finra.datagenerator.scaffolding.config.ConfigName;
import org.finra.datagenerator.scaffolding.random.core.JavaRubberRandom;
import org.finra.datagenerator.scaffolding.random.core.RegexGenerator;
import org.finra.datagenerator.scaffolding.random.randomizers.EmailRandomizer;
import org.finra.datagenerator.scaffolding.random.support.annotations.CustomRandomizer;
import org.finra.datagenerator.scaffolding.random.types.CollectionTypes;
import org.finra.datagenerator.scaffolding.random.types.ParameterizedTypeReference;
import org.finra.datagenerator.scaffolding.random.userTypes.Email;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Created by dkopel on 9/30/16.
 */
public class RandomTest {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    public void paramertizedType() {
        JavaRubberRandom random = JavaRubberRandom.apply();
        Map<String, Buffet<Integer>> output = random.generate(
            new ParameterizedTypeReference<Map<String, Buffet<Integer>>>(){}
        );
        logger.debug("Out: {}", output);
    }

    public static class Foo {
        private Integer number;

        private String label;

        public void setNumber(Integer num) {
            this.number = num;
        }

        public void setLabel(String lbl) {
            this.label = lbl;
        }

        public Integer getNumber() {
            return number;
        }

        public String getLabel() {
            return label;
        }
    }

    @Test
    public void fooTest() throws Exception {
        JavaRubberRandom random = JavaRubberRandom.apply();
        random.jpr().random.setSeed(196542L);
        Long l = random.generate(Long.class);
        logger.info("l {}", l);
        for(int x=0; x < 5; x++) {
            Foo f = random.generate(Foo.class);
            logger.info("Foo {}", new ObjectMapper().writeValueAsString(f));
        }
    }

    public static class OtherEmail {
        private Email email;

        @CustomRandomizer(value=EmailRandomizer.class)
        private String emailString;

        public Email getEmail() {
            return email;
        }

        public OtherEmail setEmail(Email email) {
            this.email = email;
            return this;
        }

        public String getEmailString() {
            return emailString;
        }

        public OtherEmail setEmailString(String emailString) {
            this.emailString = emailString;
            return this;
        }
    }

    @Test
    public void emailTest() {
        JavaRubberRandom random = JavaRubberRandom.apply();
        Email e1 = random.generate(Email.class);
        logger.info("Email: {}", e1);
        OtherEmail e2 = random.generate(OtherEmail.class);
        logger.info("Email: {}", e2.getEmail());
        logger.info("String Email: {}", e2.getEmailString());
    }

    @Test
    public void regexTest() {
        JavaRubberRandom random = JavaRubberRandom.apply();
        RegexGenerator gen = random.generateRegex("[a-zA-Z0-9]{5,10}\\@[a-zA-Z0-9]{5,10}\\.[a-z]{3}");
        String r1 = gen.next();
        String r2 = gen.next();
        logger.info("Out: {}", r1);
        logger.info("Out: {}", r2);
    }

    @Test
    public void adjustCollectionOutput() {
        JavaRubberRandom random = JavaRubberRandom.apply();
        ConfigName cn = CollectionTypes.CollectionRangeName$.MODULE$;
        scala.collection.immutable.Range range = scala.collection.immutable.Range$.MODULE$.inclusive(5, 5);
        random.setOverride(cn, range);
        List<Integer> out = random.generate(new ParameterizedTypeReference<List<Integer>>() {});
        Assert.assertEquals(5, out.size());
    }
}
