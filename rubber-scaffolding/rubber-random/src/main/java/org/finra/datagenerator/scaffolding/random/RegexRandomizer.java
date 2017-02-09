package org.finra.datagenerator.scaffolding.random;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by dkopel on 12/12/16.
 */
public class RegexRandomizer {
    private final Random random;

    public RegexRandomizer(Random random) {
        this.random = random;
    }

    public RegexRandomizer() {
        this.random = new Random();
    }

    public String generateFromRegex(String regex) {
        StringBuilder b = new StringBuilder();

        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher("");

        int[] cypher = new int[95];
        boolean done = false;

        //start from an empty string and grow a solution
        while (!done) {
            //make a cypher to jumble the order letters are tried
            for (int i = 0; i < 95; i++) {
                cypher[i] = i;
            }

            for (int i = 0; i < 95; i++) {
                int n = random.nextInt(95 - i) + i;

                int t = cypher[n];
                cypher[n] = cypher[i];
                cypher[i] = t;
            }

            //try and grow partial solution using an extra letter on the end
            for (int i = 0; i < 95; i++) {
                int n = cypher[i] + 32;
                b.append((char) n);

                String result = b.toString();
                m.reset(result);
                if (m.matches()) { //complete solution found
                    //don't try to expand to a larger solution
                    if (!random.nextBoolean()) {
                        done = true;
                    }
                    break;
                } else if (m.hitEnd()) { //prefix to a solution found, keep this letter
                    break;
                } else { //dead end found, try a new character at the end
                    b.deleteCharAt(b.length() - 1);

                    //no more possible characters to try and expand with - stop
                    if (i == 94) {
                        done = true;
                    }
                }
            }
        }

        return b.toString();
    }
}
