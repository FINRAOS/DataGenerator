package org.finra.datagenerator.scaffolding.operators;

/**
 * Created by dkopel on 06/06/16.
 */
public class NumericComparisonOperatorTest {

    /*
    @Test
    public void greaterThanTest() {

        // Entity is anything greater than 3
        // Looking for anything greater than 2
        // 3 > 2 = TRUE
        Assert.assertTrue(
        NumericComparisonOperator.match(
                new IntegerNumberOperatorContainer(3, NumericComparisonOperator.GREATER_THAN),
                new IntegerNumberOperatorContainer(2, NumericComparisonOperator.GREATER_THAN)
        ).equals(Matches.SOMETIMES));

        Assert.assertTrue(
                NumericComparisonOperator.match(
                        new IntegerNumberOperatorContainer(3, NumericComparisonOperator.GREATER_THAN),
                        new IntegerNumberOperatorContainer(3, NumericComparisonOperator.GREATER_THAN)
                ).equals(Matches.ALWAYS));

        Assert.assertTrue(
                NumericComparisonOperator.match(
                        new IntegerNumberOperatorContainer(3, NumericComparisonOperator.GREATER_THAN),
                        new IntegerNumberOperatorContainer(5, NumericComparisonOperator.GREATER_THAN)
                ).equals(Matches.ALWAYS));

        Assert.assertEquals(Matches.ALWAYS,
                NumericComparisonOperator.match(
                        new IntegerNumberOperatorContainer(3, NumericComparisonOperator.GREATER_THAN),
                        new IntegerNumberOperatorContainer(5, NumericComparisonOperator.GREATER_THAN_OR_EQUAL)
                ));

        Assert.assertEquals(Matches.NEVER, NumericComparisonOperator.match(
                new IntegerNumberOperatorContainer(3, NumericComparisonOperator.GREATER_THAN),
                new IntegerNumberOperatorContainer(3, NumericComparisonOperator.GREATER_THAN_OR_EQUAL)
        ));

        Assert.assertTrue(
                NumericComparisonOperator.match(
                        new IntegerNumberOperatorContainer(3, NumericComparisonOperator.GREATER_THAN),
                        new IntegerNumberOperatorContainer(2, NumericComparisonOperator.GREATER_THAN_OR_EQUAL)
                ).equals(Matches.SOMETIMES));

        // Entity is anything greater than 3
        // Looking for anything less than 2
        // 2 > 3 = FALSE
        Assert.assertEquals(
                Matches.NEVER,
                NumericComparisonOperator.match(
                        new IntegerNumberOperatorContainer(3, NumericComparisonOperator.GREATER_THAN),
                        new IntegerNumberOperatorContainer(2, NumericComparisonOperator.LESS_THAN)
                )
        );

        Assert.assertEquals(
                Matches.SOMETIMES,
                NumericComparisonOperator.match(
                        new IntegerNumberOperatorContainer(3, NumericComparisonOperator.GREATER_THAN),
                        new IntegerNumberOperatorContainer(5, NumericComparisonOperator.LESS_THAN)
                )
        );

        Assert.assertEquals(
                Matches.NEVER,
                NumericComparisonOperator.match(
                        new IntegerNumberOperatorContainer(3, NumericComparisonOperator.GREATER_THAN),
                        new IntegerNumberOperatorContainer(3, NumericComparisonOperator.LESS_THAN_OR_EQUAL)
                )
        );

        Assert.assertEquals(
                Matches.SOMETIMES,
                NumericComparisonOperator.match(
                        new IntegerNumberOperatorContainer(3, NumericComparisonOperator.GREATER_THAN),
                        new IntegerNumberOperatorContainer(10, NumericComparisonOperator.LESS_THAN_OR_EQUAL)
                )
        );

        Assert.assertEquals(
                Matches.NEVER,
                NumericComparisonOperator.match(
                        new IntegerNumberOperatorContainer(3, NumericComparisonOperator.GREATER_THAN),
                        new IntegerNumberOperatorContainer(1, NumericComparisonOperator.EQUAL)
                )
        );

        Assert.assertEquals(
                Matches.ALWAYS,
                NumericComparisonOperator.match(
                        new IntegerNumberOperatorContainer(3, NumericComparisonOperator.GREATER_THAN),
                        new IntegerNumberOperatorContainer(4, NumericComparisonOperator.EQUAL)
                )
        );

        Assert.assertEquals(
                Matches.NEVER,
                NumericComparisonOperator.match(
                        new IntegerNumberOperatorContainer(3, NumericComparisonOperator.GREATER_THAN),
                        new IntegerNumberOperatorContainer(4, NumericComparisonOperator.NOT_EQUAL)
                )
        );

        Assert.assertEquals(
                Matches.ALWAYS,
                NumericComparisonOperator.match(
                        new IntegerNumberOperatorContainer(3, NumericComparisonOperator.GREATER_THAN),
                        new IntegerNumberOperatorContainer(3, NumericComparisonOperator.NOT_EQUAL)
                )
        );

        Assert.assertEquals(
                Matches.ALWAYS,
                NumericComparisonOperator.match(
                        new IntegerNumberOperatorContainer(3, NumericComparisonOperator.GREATER_THAN),
                        new IntegerNumberOperatorContainer(2, NumericComparisonOperator.NOT_EQUAL)
                )
        );
    }

    @Test
    public void lessThanTest() {
        Assert.assertEquals(
                Matches.ALWAYS,
                NumericComparisonOperator.match(
                        new IntegerNumberOperatorContainer(10, NumericComparisonOperator.LESS_THAN),
                        new IntegerNumberOperatorContainer(2, NumericComparisonOperator.EQUAL)
                )
        );

        Assert.assertEquals(
                Matches.NEVER,
                NumericComparisonOperator.match(
                        new IntegerNumberOperatorContainer(10, NumericComparisonOperator.LESS_THAN),
                        new IntegerNumberOperatorContainer(20, NumericComparisonOperator.EQUAL)
                )
        );

        Assert.assertEquals(
                Matches.NEVER,
                NumericComparisonOperator.match(
                        new IntegerNumberOperatorContainer(10, NumericComparisonOperator.LESS_THAN),
                        new IntegerNumberOperatorContainer(2, NumericComparisonOperator.NOT_EQUAL)
                )
        );

        Assert.assertEquals(
                Matches.ALWAYS,
                NumericComparisonOperator.match(
                        new IntegerNumberOperatorContainer(10, NumericComparisonOperator.LESS_THAN),
                        new IntegerNumberOperatorContainer(20, NumericComparisonOperator.NOT_EQUAL)
                )
        );

        Assert.assertEquals(
                Matches.ALWAYS,
                NumericComparisonOperator.match(
                        new IntegerNumberOperatorContainer(10, NumericComparisonOperator.LESS_THAN),
                        new IntegerNumberOperatorContainer(10, NumericComparisonOperator.NOT_EQUAL)
                )
        );

        Assert.assertEquals(
                Matches.SOMETIMES,
                NumericComparisonOperator.match(
                        new IntegerNumberOperatorContainer(10, NumericComparisonOperator.LESS_THAN),
                        new IntegerNumberOperatorContainer(2, NumericComparisonOperator.GREATER_THAN)
                )
        );

        Assert.assertEquals(
                Matches.NEVER,
                NumericComparisonOperator.match(
                        new IntegerNumberOperatorContainer(10, NumericComparisonOperator.LESS_THAN),
                        new IntegerNumberOperatorContainer(10, NumericComparisonOperator.GREATER_THAN)
                )
        );

        Assert.assertEquals(
                Matches.NEVER,
                NumericComparisonOperator.match(
                        new IntegerNumberOperatorContainer(10, NumericComparisonOperator.LESS_THAN),
                        new IntegerNumberOperatorContainer(20, NumericComparisonOperator.GREATER_THAN)
                )
        );

        Assert.assertEquals(
                Matches.SOMETIMES,
                NumericComparisonOperator.match(
                        new IntegerNumberOperatorContainer(10, NumericComparisonOperator.LESS_THAN),
                        new IntegerNumberOperatorContainer(2, NumericComparisonOperator.GREATER_THAN_OR_EQUAL)
                )
        );

        Assert.assertEquals(
                Matches.NEVER,
                NumericComparisonOperator.match(
                        new IntegerNumberOperatorContainer(10, NumericComparisonOperator.LESS_THAN),
                        new IntegerNumberOperatorContainer(20, NumericComparisonOperator.GREATER_THAN_OR_EQUAL)
                )
        );

        Assert.assertEquals(
                Matches.NEVER,
                NumericComparisonOperator.match(
                        new IntegerNumberOperatorContainer(10, NumericComparisonOperator.LESS_THAN),
                        new IntegerNumberOperatorContainer(10, NumericComparisonOperator.GREATER_THAN_OR_EQUAL)
                )
        );

        Assert.assertEquals(
                Matches.ALWAYS,
                NumericComparisonOperator.match(
                        new IntegerNumberOperatorContainer(10, NumericComparisonOperator.LESS_THAN),
                        new IntegerNumberOperatorContainer(2, NumericComparisonOperator.LESS_THAN)
                )
        );

        Assert.assertEquals(
                Matches.ALWAYS,
                NumericComparisonOperator.match(
                        new IntegerNumberOperatorContainer(10, NumericComparisonOperator.LESS_THAN),
                        new IntegerNumberOperatorContainer(10, NumericComparisonOperator.LESS_THAN)
                )
        );

        Assert.assertEquals(
                Matches.SOMETIMES,
                NumericComparisonOperator.match(
                        new IntegerNumberOperatorContainer(10, NumericComparisonOperator.LESS_THAN),
                        new IntegerNumberOperatorContainer(20, NumericComparisonOperator.LESS_THAN)
                )
        );

        Assert.assertEquals(
                Matches.ALWAYS,
                NumericComparisonOperator.match(
                        new IntegerNumberOperatorContainer(10, NumericComparisonOperator.LESS_THAN),
                        new IntegerNumberOperatorContainer(2, NumericComparisonOperator.LESS_THAN_OR_EQUAL)
                )
        );

        Assert.assertEquals(
                Matches.NEVER,
                NumericComparisonOperator.match(
                        new IntegerNumberOperatorContainer(10, NumericComparisonOperator.LESS_THAN),
                        new IntegerNumberOperatorContainer(10, NumericComparisonOperator.LESS_THAN_OR_EQUAL)
                )
        );

        Assert.assertEquals(
                Matches.SOMETIMES,
                NumericComparisonOperator.match(
                        new IntegerNumberOperatorContainer(10, NumericComparisonOperator.LESS_THAN),
                        new IntegerNumberOperatorContainer(20, NumericComparisonOperator.LESS_THAN_OR_EQUAL)
                )
        );
    }

    @Test
    public void equalTest() {
        Assert.assertEquals(
                Matches.ALWAYS,
                NumericComparisonOperator.match(
                        new IntegerNumberOperatorContainer(10, NumericComparisonOperator.EQUAL),
                        new IntegerNumberOperatorContainer(10, NumericComparisonOperator.EQUAL)
                )
        );

        Assert.assertEquals(
                Matches.NEVER,
                NumericComparisonOperator.match(
                        new IntegerNumberOperatorContainer(10, NumericComparisonOperator.EQUAL),
                        new IntegerNumberOperatorContainer(20, NumericComparisonOperator.EQUAL)
                )
        );

        Assert.assertEquals(
                Matches.ALWAYS,
                NumericComparisonOperator.match(
                        new IntegerNumberOperatorContainer(10, NumericComparisonOperator.EQUAL),
                        new IntegerNumberOperatorContainer(2, NumericComparisonOperator.NOT_EQUAL)
                )
        );

        Assert.assertEquals(
                Matches.NEVER,
                NumericComparisonOperator.match(
                        new IntegerNumberOperatorContainer(10, NumericComparisonOperator.EQUAL),
                        new IntegerNumberOperatorContainer(10, NumericComparisonOperator.NOT_EQUAL)
                )
        );

        Assert.assertEquals(
                Matches.ALWAYS,
                NumericComparisonOperator.match(
                        new IntegerNumberOperatorContainer(10, NumericComparisonOperator.EQUAL),
                        new IntegerNumberOperatorContainer(20, NumericComparisonOperator.NOT_EQUAL)
                )
        );

        Assert.assertEquals(
                Matches.NEVER,
                NumericComparisonOperator.match(
                        new IntegerNumberOperatorContainer(10, NumericComparisonOperator.EQUAL),
                        new IntegerNumberOperatorContainer(10, NumericComparisonOperator.GREATER_THAN)
                )
        );

        Assert.assertEquals(
                Matches.SOMETIMES,
                NumericComparisonOperator.match(
                        new IntegerNumberOperatorContainer(10, NumericComparisonOperator.EQUAL),
                        new IntegerNumberOperatorContainer(5, NumericComparisonOperator.GREATER_THAN)
                )
        );

        Assert.assertEquals(
                Matches.NEVER,
                NumericComparisonOperator.match(
                        new IntegerNumberOperatorContainer(10, NumericComparisonOperator.EQUAL),
                        new IntegerNumberOperatorContainer(30, NumericComparisonOperator.GREATER_THAN)
                )
        );

        Assert.assertEquals(
                Matches.NEVER,
                NumericComparisonOperator.match(
                        new IntegerNumberOperatorContainer(10, NumericComparisonOperator.EQUAL),
                        new IntegerNumberOperatorContainer(10, NumericComparisonOperator.GREATER_THAN)
                )
        );
    }

    @Test
    public void notEqualTest() {
        Assert.assertEquals(
                Matches.NEVER,
                NumericComparisonOperator.match(
                        new IntegerNumberOperatorContainer(10, NumericComparisonOperator.NOT_EQUAL),
                        new IntegerNumberOperatorContainer(10, NumericComparisonOperator.EQUAL)
                )
        );

        Assert.assertEquals(
                Matches.ALWAYS,
                NumericComparisonOperator.match(
                        new IntegerNumberOperatorContainer(10, NumericComparisonOperator.NOT_EQUAL),
                        new IntegerNumberOperatorContainer(20, NumericComparisonOperator.EQUAL)
                )
        );

//        Assert.assertEquals(
//                Matches.ALWAYS,
//                NumericComparisonOperator.match(
//                        new IntegerNumberOperatorContainer(10, NumericComparisonOperator.NOT_EQUAL),
//                        new IntegerNumberOperatorContainer(10, NumericComparisonOperator.NOT_EQUAL)
//                )
//        );
//
//        Assert.assertEquals(
//                Matches.NEVER,
//                NumericComparisonOperator.match(
//                        new IntegerNumberOperatorContainer(10, NumericComparisonOperator.NOT_EQUAL),
//                        new IntegerNumberOperatorContainer(20, NumericComparisonOperator.NOT_EQUAL)
//                )
//        );
        Assert.assertEquals(
                Matches.SOMETIMES,
                NumericComparisonOperator.match(
                        new IntegerNumberOperatorContainer(10, NumericComparisonOperator.NOT_EQUAL),
                        new IntegerNumberOperatorContainer(10, NumericComparisonOperator.GREATER_THAN)
                )
        );

        Assert.assertEquals(
                Matches.SOMETIMES,
                NumericComparisonOperator.match(
                        new IntegerNumberOperatorContainer(10, NumericComparisonOperator.NOT_EQUAL),
                        new IntegerNumberOperatorContainer(10, NumericComparisonOperator.GREATER_THAN)
                )
        );

        Assert.assertEquals(
                Matches.SOMETIMES,
                NumericComparisonOperator.match(
                        new IntegerNumberOperatorContainer(10, NumericComparisonOperator.NOT_EQUAL),
                        new IntegerNumberOperatorContainer(20, NumericComparisonOperator.GREATER_THAN)
                )
        );
    }
    */
}