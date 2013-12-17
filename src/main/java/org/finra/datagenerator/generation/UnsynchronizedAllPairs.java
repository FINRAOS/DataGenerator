/*
 * (C) Copyright 2013 DataGenerator Contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.finra.datagenerator.generation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;
import org.apache.log4j.Logger;

/**
 * This class can take in a specification of the number of possible choices at each point in an array. It then produces
 * a selection of integer arrays, such that for each choice of two points in in the first array, some array in the
 * selection has any possible pair of values at those two points. In other words, we cover every pair of combinations.
 */
public class UnsynchronizedAllPairs {

    private static final Logger log = Logger.getLogger(UnsynchronizedAllPairs.class);
    /**
     * prime power to use for prime-based construction
     */
    private int primePower;
    /**
     * primePower = basePrime ^ power
     */
    private int basePrime;
    private int power;
    /**
     * The prime-based construction constructs its solution using blocks with p+1 columns and p*p rows. p+1 may be
     * smaller than the number of columns we have to fill up. In this case, we number each column base p+1. This is the
     * number of digits required for this numbering: we will end up with at most primeDigits * p * p rows.
     */
    private int primeDigits;
    /**
     * maximum number of columns with this prime and repeats
     */
    private int maxColumns;
    /**
     * block from prime-based solution
     */
    private int[][] primeBlock;
    /**
     * Copy of input giving number of choices at each stage
     */
    private int[] numChoices;
    /**
     * used to make loads of decisions at random: this stuff mostly works by doing its best following random guesses so
     * that we can make loads of attempts and pick the best
     */
    private Random r;
    /**
     * Keeps track of the number of combinations accounted for so far
     */
    private Used used;
    /**
     * Used to keep track of the number of times each combination has been used, as a check, and in the prime-based
     * strategy. Indexed by a, a value, b, bvalue (yes this is redundant but I want to keep this bit simple).
     */
    private int[][][][] counts;
    /**
     * Seed value. Reseeded in generate to make it easier to reproduce any bugs
     */
    private long mySeed;
    /**
     * set this to control whether the prime-based method shuffles
     */
    private boolean shuffle;
    /**
     * List of small primes. We can detect prime powers of these
     */
    private static int smallPrimes[] = new int[]{2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37};

    /**
     * Construct given number of choices at each point and the starting random number seed. This does some one-time work
     */
    public UnsynchronizedAllPairs(int[] choices, long seed, boolean shouldShuffle) {
        // Be paranoid about user fiddling with array after calling
        // constructor
        numChoices = choices.clone();
        mySeed = seed;
        shuffle = shouldShuffle;
        if (numChoices.length<=1) { // silly number of columns, which might cause us to blow up
            // later
            return;
        }
        used = new Used();
        counts = new int[numChoices.length][][][];
        for(int i = 0; i<counts.length; i++){
            counts[i] = new int[numChoices[i]][][];
            for(int iVal = 0; iVal<numChoices[i]; iVal++){
                counts[i][iVal] = new int[numChoices.length][];
                for(int j = 0; j<numChoices.length; j++){
                    counts[i][iVal][j] = new int[numChoices[j]];
                }
            }
        }
		// Now work out a prime or prime power to use for prime-number based
        // construction. Given a prime, or prime power, p, the construction
        // produces p^2 rows and p+1 columns. The first column is p 0s,
        // then p 1s, then p 2s..
        // and so on. The second column is 0,1,2,3..p-1,0,1,2,...
        // The ith column is the first column plus (i-1) times the second,
        // (mod p, or using Galois arithmetic if p is a prime power). If we
        // specify the values in the same row in any two different columns
        // we can work out the values in the same row for the first two
        // columns by solving a simultaneous linear equation in two
        // unknowns. This set of equations has a non-zero determinant and
        // we are working in a field so it has precisely one solution,
        // which means that each pair of values occurs in precisely one
        // row for each pair of columns.

        // We can extend the number of columns of this by using more
        // than one copy of each column, below each other. Write the
        // index of each column down base (p+1) and choose copies of
        // the original prime-based column according to the digits of
        // that column's index in this base. Any two columns have
        // different numbers and so will differ in at least one digit
        // of this representation, and therefore will have a set of
        // pairs produced by one of the columns from the basic
        // construction
        // First of all find a lower bound on p
        primePower = 2;
        for(int i = 0; i<numChoices.length; i++){
            if (numChoices[i]>primePower) {
                primePower = numChoices[i];
            }
        }
        // search here for a prime, or a power of a small prime
        ploop:
        for(;;){
            for(int p : smallPrimes){
                if (primePower==p) { // is a prime
                    basePrime = p;
                    power = 1;
                    break ploop;
                }
                // see if primePower is a power of p
                power = 1;
                for(int reduced = primePower;;){
                    if ((reduced%p)!=0) {
                        if (power>1) { // divisible by a small prime but not a power
                            // what's more, we know it is not a power of two,
                            // since we test for that first
                            if ((primePower&1)==0) {
                                primePower++;
                            } else {
                                primePower += 2;
                            }
                            continue ploop;
                        }
                        // here if not divisible by p
                        break;
                    }
                    reduced = reduced/p;
                    // now reduced = primePower / p^power
                    if (reduced==1) { // primePower = p ^ power
                        basePrime = p;
                        break ploop;
                    }
                    power++;
                }
            }
            for(int i = smallPrimes[smallPrimes.length-1]+2;; i += 2){
                if (i*i>primePower) { // primePower is a large prime
                    basePrime = primePower;
                    power = 1;
                    break ploop;
                }
                if ((primePower%i)==0) { // primePower is not a prime
                    // note that even if primePower = i * i, that does us no
                    // good, as i probably isn't prime
                    break;
                }
            }
            if ((primePower&1)==0) {
                primePower++;
            } else {
                primePower += 2;
            }
        }
        maxColumns = primePower+1;
        // now build basic block of prime-based solution
        primeBlock = new int[primePower*primePower][];
        if (power==1) {
            for(int i = 0; i<primeBlock.length; i++){
                int[] row = new int[maxColumns];
                primeBlock[i] = row;
                int a = i/primePower;
                int b = i%primePower;
                row[0] = a;
                row[1] = b;
                for(int j = 2; j<maxColumns; j++){
                    row[j] = (a+b*(j-1))%primePower;
                }
            }
        } else {
            // Need to do arithmetic over a Galois field. For the most
            // part we can think of this as working with polynomials mod
            // some polynomial, where the other polynomial happens to
            // be chosen such that this is a field, not a ring (that is,
            // so that every non-zero element has an inverse). Galois theory
            // is only necessary to guarantee that such a polynomial exists,
            // and that one exists such that 1, x, x^2, x^3... produces
            // all non-zero polynomials. We search for one such here, with
            // the highest non-zero power of x having coefficient 1.
            int[] magicPoly = new int[power];
            int[] trial = new int[power];
            int forcePositive = basePrime*basePrime;
            int exhaust = 1;
            exhaustLoop:
            for(; exhaust<primePower; exhaust++){
                int val = exhaust;
                for(int i = 0; i<power; i++){
                    magicPoly[i] = val%basePrime;
                    val = val/basePrime;
                }
                // start off with 1
                for(int i = 0; i<trial.length; i++){
                    trial[i] = 0;
                }
                trial[0] = 1;
                iLoop:
                for(int i = 0;; i++){
                    // multiply by x.
                    int top = trial[trial.length-1];
                    for(int j = trial.length-1; j>0; j--){
                        trial[j] = trial[j-1];
                    }
                    trial[0] = 0;
                    // top.x^power =  - top * magicPoly
                    for(int j = 0; j<trial.length; j++){
                        int coeff = trial[j]-top*magicPoly[j]+forcePositive;
                        trial[j] = coeff%basePrime;
                    }
                    if (i==(primePower-2)) { // should have cycled round to 1 again
                        for(int j = 1; j<trial.length; j++){
                            if (trial[j]!=0) {
                                continue exhaustLoop;
                            }
                        }
                        if (trial[0]==1) { // success!
                            break exhaustLoop;
                        }
                        // failure
                        continue exhaustLoop;
                    }
                    // Check for early failure cases: 1 or 0
                    for(int j = 1; j<trial.length; j++){
                        if (trial[j]!=0) {
                            continue iLoop;
                        }
                    }
                    if ((trial[0]==1)||(trial[0]==0)) { // early failure
                        continue exhaustLoop;
                    }
                }
            }
            if (exhaust==primePower) {
                throw new IllegalStateException(
                        "Could not find good polynomial");
            }
            /*
             System.err.println("Exhaust is " + exhaust);
             for (int i = 0; i < magicPoly.length; i++)
             {
             System.err.println("Magic " + magicPoly[i]);
             }
             */
            primeBlock = new int[primePower*primePower][];
            for(int i = 0; i<primeBlock.length; i++){
                primeBlock[i] = new int[maxColumns];
            }
            int[] zeroColumn = new int[power];
            int[] oneColumn = new int[power];
            // System.err.println("basePrime = " + basePrime);
            for(int i = 0; i<primeBlock.length; i++){
                // fill in the first two columns with polynomials written
                // down as numbers
                int z = i/primePower;
                primeBlock[i][0] = z;
                int o = i%primePower;
                primeBlock[i][1] = o;
                // System.err.println("i = " + i + " z = " + z + " o = " + o);
                // Split first two columns out as polynomials
                for(int j = 0; j<power; j++){
                    zeroColumn[j] = z%basePrime;
                    z = z/basePrime;
                    oneColumn[j] = o%basePrime;
                    o = o/basePrime;
                }
                // To work out the rest of the row, we continually
                // add oneColumn to zeroColumn to produce the new entry,
                // then multiply oneColumn by x. So each column is
                // z + x^k * o, for some value of k, and we already know
                // that x^k cycles round
                int last = maxColumns-1;
                for(int j = 2;; j++){
                    int v = 0;
                    int intPower = 1;
                    for(int k = 0; k<power; k++){
                        int x = (zeroColumn[k]+oneColumn[k])%basePrime;
                        v = x*intPower+v;
                        intPower = intPower*basePrime;
                    }
                    primeBlock[i][j] = v;
                    if (j==last) {
                        break;
                    }
                    int top = oneColumn[oneColumn.length-1];
                    for(int k = oneColumn.length-1; k>0; k--){
                        oneColumn[k] = oneColumn[k-1];
                    }
                    oneColumn[0] = 0;
                    // top.x^power =  - top * magicPoly
                    for(int k = 0; k<oneColumn.length; k++){
                        int coeff = oneColumn[k]-top*magicPoly[k]
                                +forcePositive;
                        oneColumn[k] = coeff%basePrime;
                    }
                }
            }
        }
        primeDigits = 1;
        while (maxColumns<numChoices.length){
            primeDigits++;
            maxColumns *= (primePower+1);
        }
        sumOfNumChoices = 0;
        for(int i = 0; i<numChoices.length; i++){
            sumOfNumChoices += numChoices[i];
        }
        // back to setting up stuff for greedy algorithm
        // Will keep track of combination of position and value
        // to occur, together with the number of other position-value
        // combinations it has yet to meet, ordered by that number
        combinationsByLeft = new ArrayList[sumOfNumChoices-1];
        for(int i = 0; i<combinationsByLeft.length; i++){
            combinationsByLeft[i] = new ArrayList<Combination>();
        }
        // Combination indexed by position and value
        combinationByPositionValue = new Combination[numChoices.length][];
        for(int i = 0; i<numChoices.length; i++){
            Combination[] arr = new Combination[numChoices[i]];
            combinationByPositionValue[i] = arr;
            for(int j = 0; j<arr.length; j++){
                Combination c = new Combination();
                c.position = i;
                c.value = j;
                c.numLeft = sumOfNumChoices-numChoices[i];
                arr[j] = c;
                c.positionInArray = combinationsByLeft[0].size();
                combinationsByLeft[0].add(c);
            }
        }
    }

    /**
     * This class keeps track of whether particular pairs of position-value combinations have been used yet
     */
    private class Used {

        /**
         * total number of pairs required
         */
        private int numPossible = 0;

        /**
         * construct from array giving number of choices at each point
         */
        Used() {
            // Build up array and count number of distinct pairs. Each
            // pair of positions appears once as i,j and once as j,i.
            // Here we make use of that by storing it only in the order
            // in which i < j, sizing the arrays to suit.
            int n1 = numChoices.length-1;
            isUsed = new boolean[n1][][];
            for(int i = 0; i<n1; i++){
                int lenHere = numChoices.length-1-i;
                isUsed[i] = new boolean[lenHere][];
                for(int j = 0; j<lenHere; j++){
                    isUsed[i][j] = new boolean[numChoices[i]
                            *numChoices[i+j+1]];
                    numPossible += isUsed[i][j].length;
                }
            }
        }

        // test used only during debugging
        boolean isAllUsed() {
            // System.err.println("Possible " + numPossible +
            //   " used " + numUsed);
            for(int i = 0; i<isUsed.length; i++){
                boolean[][] ui = isUsed[i];
                for(int j = 0; j<ui.length; j++){
                    boolean[] uj = ui[j];
                    for(int k = 0; k<uj.length; k++){
                        if (!uj[k]) {
                            return false;
                        }
                    }
                }
            }
            return true;
        }

        /**
         * clear all used flags
         */
        void reset() {
            for(int i = 0; i<isUsed.length; i++){
                for(int j = 0; j<isUsed[i].length; j++){
                    Arrays.fill(isUsed[i][j], false);
                }
            }
            numUsed = 0;
        }

        /**
         * return the number of possible slots
         */
        int getNumPossible() {
            return numPossible;
        }

        /**
         * return the number of used slots
         */
        int getNumUsed() {
            return numUsed;
        }
        /**
         * Triangular array mapping to boolean array of used or not. We demand that the first index is less than the
         * second, which means that we don't need to allocate n^2 values. For 3 choices we actually store just 0,1 0,2
         * 1,2
         */
        private boolean[][][] isUsed;
        /**
         * number of combinations used
         */
        private int numUsed = 0;

        /**
         * mark the combination of aVal at a, bVal at b as used, returning the previous value.
         */
        boolean setUsed(int a, int aVal, int b, int bVal) {
            if (a==b) {
                System.err.println("Seed "+mySeed);
                throw new IllegalArgumentException("a == b");
            }
            if (a>b) {
                int t = a;
                a = b;
                b = t;
                t = aVal;
                aVal = bVal;
                bVal = t;
            }
            int index = aVal*numChoices[b]+bVal;
            int ba = b-a-1;
            boolean previous = isUsed[a][ba][index];
            if (!previous) {
                numUsed++;
                isUsed[a][ba][index] = true;
            }
            return previous;
        }

        /**
         * return whether the combination of aVal at a, bVal at b is used
         */
        boolean isCombinationUsed(int a, int aVal, int b, int bVal) {
            if (a==b) {
                System.err.println("Seed "+mySeed);
                throw new IllegalArgumentException("a == b");
            }
            if (a>b) {
                int t = a;
                a = b;
                b = t;
                t = aVal;
                aVal = bVal;
                bVal = t;
            }
            int index = aVal*numChoices[b]+bVal;
            int ba = b-a-1;
            return isUsed[a][ba][index];
        }
    }

    /**
     * Fill in the counts array and return the minimum value of any count, given a design. This is used both for
     * checking designs and for removing redundant rows from prime-based designs.
     */
    private int minCount(int[][] design) {
        for(int i = 0; i<numChoices.length; i++){
            int[][][] c1 = counts[i];
            for(int iVal = 0; iVal<c1.length; iVal++){
                int[][] c2 = c1[iVal];
                for(int j = 0; j<c2.length; j++){
                    Arrays.fill(c2[j], 0);
                }
            }
        }
        for(int d = 0; d<design.length; d++){
            int[] row = design[d];
            for(int i = 0; i<row.length; i++){
                for(int j = 0; j<row.length; j++){
                    if (i==j) {
                        continue;
                    }
                    counts[i][row[i]][j][row[j]]++;
                }
            }
        }
        int min = Integer.MAX_VALUE;
        for(int i = 0; i<numChoices.length; i++){
            int[][][] c1 = counts[i];
            for(int iVal = 0; iVal<c1.length; iVal++){
                int[][] c2 = c1[iVal];
                for(int j = 0; j<c2.length; j++){
                    if (i==j) {
                        continue;
                    }
                    int[] c3 = c2[j];
                    for(int k = 0; k<c3.length; k++){
                        int x = c3[k];
                        if (min>x) {
                            min = x;
                        }
                    }
                }
            }
        }
        return min;
    }

    /**
     * get String rep
     */
    public static String[][] getStringResult(int[][] result, Translator tran) {
        String[][] ret = new String[result.length][result[0].length];

        for(int i = 0; i<result.length; i++){
            for(int j = 0; j<result[i].length; j++){
                String toPrint;
                if (tran==null) {
                    toPrint = Integer.toString(result[i][j]);
                } else {
                    toPrint = tran.translate(j, result[i][j]);
                }
                ret[i][j] = toPrint;
            }
        }
        return ret;

    }

    /**
     * print out the design
     */
    public static void showResult(int[][] result, Translator tran, String dir) {
        StringBuffer sb = new StringBuffer("\n");
        for(int i = 0; i<result.length; i++){
            for(int j = 0; j<result[i].length; j++){
                //if (j != 0)
                //{
                sb.append('|');
                //}
                String toPrint;
                if (tran==null) {
                    toPrint = Integer.toString(result[i][j]);
                } else {
                    toPrint = tran.translate(j, result[i][j]);
                }
                sb.append(toPrint);
            }
            sb.append("|\n");
        }
        sb.append("SIZE:"+result.length);

        log.debug(sb.toString());
        //FileWriterUsingLog4jLogger.write("PDLogging", dir, "pw_combos.txt", sb.toString(), false);

    }

    /**
     * Generate and print out stuff for ever, or until max goes
     */
    public static int[][] indefiniteGenerate(
            int[] choices, long seed, int max, boolean shuffle,
            Translator tran, String dir) {
        int bestSofar = Integer.MAX_VALUE;
        UnsynchronizedAllPairs ap = new UnsynchronizedAllPairs(choices, seed, shuffle);
        for(int go = 0;; go++){
            int[][] result;
            switch (go%2) {
                // Want to do the prime-based generation first as it may
                // be pretty if shuffling is turned off
                case 0:
                    result = ap.generateViaPrime(dir);
                    break;
                case 1:
                    result = ap.generateGreedy();
                    break;
                default:
                    throw new IllegalStateException("Bad case");
            }
            /*
             if (!ap.used.isAllUsed())
             {
             System.err.println("Trouble seen by used:");
             showResult(result);
             System.err.println("Seed " + ap.mySeed);
             throw new IllegalStateException("Generated bad result");
             }
             */
            if (ap.minCount(result)<1) {
                System.err.println("Trouble:");
                showResult(result, null, dir);
                System.err.println("Seed "+ap.mySeed);
                throw new IllegalStateException("Generated bad result");
            }
            if (result.length<bestSofar) {
                bestSofar = result.length;
                log.debug("New best of "+bestSofar+" rows at go "
                        +go);
                log.debug("Data Varaitions:"+bestSofar);
                showResult(result, tran, dir);
                log.debug("New best was "
                        +bestSofar+" rows at go "+go);
            }
            //if ((max > 0) && (go >= max))
            if ((go+1)>=max) {
                return result;
            }
        }
    }

    /**
     * Work out the value in a cell for a prime-based table of size primePower*primePower rows and primePower+1 columns,
     * stacked on top of each other primeDigits times.
     */
    private int getCell(int row, int col) {
        int p2 = primePower*primePower;
        if ((row<0)||(row>=p2*primeDigits)
                ||(col<0)||(col>maxColumns)) {
            throw new IllegalArgumentException("Outside table");
        }
        int digit = row/p2;
        row = row%p2;
        int base = primePower+1;
        for(int i = 0; i<digit; i++){
            col = col/base;
        }
        col = col%base;
        return primeBlock[row][col];
    }

    /**
     * return an array of numbers 0..num-1 in random order, assuming that shuffle is true
     */
    private int[] randomOrder(int num) {
        int[] result = new int[num];
        for(int i = 0; i<num; i++){
            result[i] = i;
        }
        if (!shuffle) {
            return result;
        }
        for(int i = 0; i<num; i++){
            // Choose at random from amongst integers not yet chosen
            int from = r.nextInt(num-i)+i;
            int t = result[i];
            result[i] = result[from];
            result[from] = t;
        }
        return result;
    }

    public int[][] generateViaPrime(String dir) {
        r = new Random(mySeed++);
        int[][] first = new int[primePower*primePower*primeDigits][];
        for(int i = 0; i<first.length; i++){
            first[i] = new int[numChoices.length];
        }
        int[] rowShuffle = randomOrder(first.length);
        int[] colShuffle = randomOrder(maxColumns);
        for(int i = 0; i<numChoices.length; i++){
            int past = numChoices[i];
            int col = colShuffle[i];
            for(int j = 0; j<first.length; j++){
                int x = getCell(rowShuffle[j], col);
                if (x>=past) { // outside range of choices at this position
                    x = r.nextInt(past);
                }
                first[j][i] = x;
            }
        }
        int min = minCount(first);
        if (min<1) {
            System.err.println("MySeed is "+mySeed);
            showResult(first, null, dir);
            throw new IllegalStateException("Prime construction failed");
        }
        // see if we can discard any rows, by looking to see what
        // removing them would do to the counts
        int wp = 0;
        for(int i = 0; i<first.length; i++){
            // look to see if we really need this row
            int[] thisRow = first[i];
            int j = 0;
            // For each pair of columns, see what the count would be
            // if we skipped this row
            checkCounts:
            for(; j<numChoices.length; j++){
                for(int k = 0; k<numChoices.length; k++){
                    if (j==k) {
                        continue;
                    }
                    int x = counts[j][thisRow[j]][k][thisRow[k]];
                    if (x<=1) { // must have the row to stop this count going non-zero
                        break checkCounts;
                    }
                }
            }
            if (j!=numChoices.length) { // needed this row as didn't scan right to the end to find
                // a reason to have it
                for(int k = 0; k<numChoices.length; k++){
                    first[wp][k] = first[i][k];
                }
                wp++;
            } else { // can delete the row so ammend counts
                for(j = 0; j<numChoices.length; j++){
                    for(int k = 0; k<numChoices.length; k++){
                        if (j==k) {
                            continue;
                        }
                        counts[j][thisRow[j]][k][thisRow[k]]--;
                    }
                }
            }
        }
        if (wp==first.length) {
            return first;
        }
        int[][] result = new int[wp][];
        System.arraycopy(first, 0, result, 0, wp);
        return result;
    }

    /**
     * Combination of position and value, together with number of matches left to make with other position-value pairs,
     * and an index of its position in an array of combinations with the same number of matches left
     */
    private static class Combination {

        int position;
        int value;
        int numLeft;
        int positionInArray;
    }
    /**
     * sum over numChoices[]
     */
    private int sumOfNumChoices;
    /**
     * combinations of position, value by number of pairings of each to make
     */
    private ArrayList<Combination>[] combinationsByLeft;
    /**
     * combinations by position and value
     */
    private Combination[][] combinationByPositionValue;

    /**
     * remove a combination from combinationsByLeft
     */
    private void removeFromByLeft(Combination c) {
        ArrayList<Combination> fromHere = combinationsByLeft[c.numLeft];
        int s1 = fromHere.size()-1;
        Combination replacement = fromHere.get(s1);
        replacement.positionInArray = c.positionInArray;
        fromHere.set(c.positionInArray, replacement);
        fromHere.remove(s1);
        c.positionInArray = -1;
    }

    /**
     * Add a combination to combinationsByLeft
     */
    private void addToByLeft(Combination c) {
        c.positionInArray = combinationsByLeft[c.numLeft].size();
        combinationsByLeft[c.numLeft].add(c);
        // System.err.println("Add to at " + c.numLeft);
    }

    /**
     * decrement the number of combinations required by a combination, moving it from list to list
     */
    private void decrementPosition(int position, int value) {
        // System.err.println("Decrement position " + position + " value " + value);
        Combination c = combinationByPositionValue[position][value];
        removeFromByLeft(c);
        // System.err.println("Current need is " + c.numLeft);
        c.numLeft--;
        addToByLeft(c);
    }

    /**
     * This strategy keeps track of the number of combinations left to particular position/value combinations
     */
    public int[][] generateGreedy() {
        r = new Random(mySeed++);
        if (numChoices.length<2) {
            return new int[0][];
        }
        used.reset();
        for(Combination c : combinationsByLeft[0]){
            // Work out number of pairings of this particular position-value
            // combination with others
            c.numLeft = sumOfNumChoices-numChoices[c.position];
            addToByLeft(c);
        }
        combinationsByLeft[0].clear();
        ArrayList<int[]> result = new ArrayList<int[]>();
        int highest = combinationsByLeft.length-1;
        // Will use these arrays to keep track of which positions
        // in a partial row have been filled in
        boolean[] posUsed = new boolean[numChoices.length];
        int[] positionsUsed = new int[numChoices.length];
        lineloop:
        for(;;){
            // Find a combination with most pairings to match
            // System.err.println("New line");
            while (combinationsByLeft[highest].size()==0){ // Note that we always decrease the number of other values a
                // combination has to pair with, so values always move down
                // this array, not up.
                highest--;
                // System.err.println("Highest is " + highest);
                if (highest<=0) { // all done
                    // System.err.println("All DONE");
                    break lineloop;
                }
            }
            ArrayList<Combination> fromHere = combinationsByLeft[highest];
            int size = fromHere.size();
            Combination c = fromHere.get(r.nextInt(size));

            // Build up a line starting with the chosen combinations
            int[] line = new int[numChoices.length];
            line[c.position] = c.value;
            // note down see how many matches are yet to do
            int before = used.getNumUsed();
            // will keep track of what positions in the new line are filled
            // in at each point
            Arrays.fill(posUsed, false);
            posUsed[c.position] = true;
            int inUse = 1;
            positionsUsed[0] = c.position;

            // Now look for best extension, considering all choices
            searchLoop:
            while (inUse<numChoices.length){
                int topScore = Integer.MIN_VALUE;
                int numTops = 0;
                Combination top = null;
                for(int searchHere = highest; searchHere>0; searchHere--){
                    if (searchHere<topScore) {
                        // Can't possibly score more that searchHere by including a combination
                        // from here so break
                        break;
                    }
                    for(Combination d : combinationsByLeft[searchHere]){
                        if (posUsed[d.position]) {
                            continue;
                        }
                        int score = 0;
                        for(int l = 0; l<inUse; l++){
                            int otherPos = positionsUsed[l];
                            if (!used.isCombinationUsed(otherPos, line[otherPos],
                                    d.position, d.value)) {
                                score++;
                            }
                        }
                        if (score>topScore) {
                            topScore = score;
                            top = d;
                            numTops = 1;
                        } else if (score==topScore) { // Have a tie. Choice the new value with probability
                            // 1 in number-of-contendors-so-far. This makes all
                            // contendors equally likely to be chosen, regardless of
                            // the order in which they appear.
                            numTops++;
                            if (r.nextInt(numTops)==0) {
                                top = d;
                            }
                        }
                    }
                }
                if (top==null) { // no matches left to make
                    for(int k = 0; k<numChoices.length; k++){
                        if (!posUsed[k]) {
                            line[k] = r.nextInt(numChoices[k]);
                        }
                    }
                    break;
                }
                // note down all the new matches
                for(int k = 0; k<inUse; k++){
                    int otherPos = positionsUsed[k];
                    int otherVal = line[otherPos];
                    if (!used.setUsed(otherPos, otherVal,
                            top.position, top.value)) {
                        decrementPosition(otherPos, otherVal);
                        decrementPosition(top.position, top.value);
                    }
                }
                positionsUsed[inUse++] = top.position;
                line[top.position] = top.value;
                posUsed[top.position] = true;
            }
            if (before==used.getNumUsed()) { // have not decreased number of matches to make
                throw new IllegalStateException("useless line");
            }
            result.add(line);
        }
        return result.toArray(new int[result.size()][]);
    }

    public static String[][] main(String[] s, String dir, File cfile) throws IOException {

        String[][] pwc = null;

        List<Integer> choiceList = new ArrayList<Integer>();
        String sp = "";
        boolean trouble = false;
        boolean noShuffle = false;
        int maxGoes = 100;
        long seed = 42;
        String choiceFile = null;
        int s1 = s.length-1;
        try {
            for(int i = 0; i<s.length; i++){
                sp = s[i].trim();
                if (sp.startsWith("-")) {
                    if ((sp.equals("-file"))&&(i<s1)) {
                        choiceFile = s[++i].trim();
                    } else if ((sp.equals("-goes"))&&(i<s1)) {
                        sp = s[++i].trim();
                        maxGoes = Integer.parseInt(sp);
                    } else if (sp.equals("-noShuffle")) {
                        noShuffle = true;
                    } else if ((sp.equals("-seed"))&&(i<s1)) {
                        sp = s[++i].trim();
                        seed = Long.parseLong(sp);
                    } else {
                        System.err.println("Could not handle flag "+sp);
                        trouble = true;
                    }
                } else {
                    int numChoices = Integer.parseInt(sp);
                    if (numChoices<1) {
                        System.err.println(
                                "Number of choices must be > 1 at every point");
                        trouble = false;
                    }
                    choiceList.add(numChoices);
                }
            }
        }
        catch (NumberFormatException nf) {
            System.err.println("Could not read number in "+sp);
            trouble = true;
        }

        int[] choices = new int[choiceList.size()];
        for(int i = 0; i<choices.length; i++){
            choices[i] = choiceList.get(i);
        }
        Translator tran = null;
        if (choiceFile!=null) {
            if (!choiceList.isEmpty()) {
                System.err.println(
                        "Cannot give both -file <name> and numeric choices");
                trouble = true;
            } else {
                tran = new Translator(choiceFile, cfile); // modified to just take the file
                choices = tran.getNumChoices();
            }
        }
        if (choices.length<2) {
            System.err.println(
                    "No point trying to work with less than two points");
            trouble = true;
        }
        if (trouble) {
            System.err.println(
                    "Arguments should be the number of choices at each point");
            System.err.println(
                    "E.g. to find a design for a system with three parameters,");
            System.err.println(
                    "with 2 choices for the first, 3 for the second, and 4 for the third,");
            System.err.println("Use arguments 2 3 4");
            System.err.println(
                    "Or use -file <name> with one line of strings per option");
            System.err.println("and # a comment");
            System.err.println(
                    "Can add flags [-goes #] [-noShuffle] [-seed #] [-file <name>]");
            System.err.println("-goes 0 => keep trying until halted");
            return null;
        }
        log.debug("Working on choices:");
        for(int i = 0; i<choices.length; i++){
            log.debug(" x ");
            log.debug(choices[i]);
        }
        log.debug("\n");
        log.debug("MaxGoes "+maxGoes+" seed "+seed
                +" noShuffle "+noShuffle);

        int[][] result = UnsynchronizedAllPairs.indefiniteGenerate(choices, seed, maxGoes, !noShuffle, tran, dir);

        pwc = getStringResult(result, tran);

        return pwc;
    }

    // Check function for Galois Field based constructions
    static void gfCheck(int maxPower, String dir) {
        for(int p = 1; p<=maxPower; p++){
            for(int i = 0; i<smallPrimes.length; i++){
                int prime = smallPrimes[i];
                int pp = 1;
                for(int j = 0; j<p; j++){
                    pp *= prime;
                }
                if (pp>121) {
                    // We will run out of memory very quickly, because there are
                    // (pp + 1) * (pp + 1) combinations of columns and pp * pp pairs to check
                    // for for each combination of columns, and we keep all of this
                    // in memory
                    continue;
                }
                System.err.println("Prime "+prime+" power "+pp);
                int[] choices = new int[pp+1];
                Arrays.fill(choices, pp);
                System.err.println("Before ap");
                UnsynchronizedAllPairs ap = new UnsynchronizedAllPairs(choices, 42, true);
                System.err.println("Before generate");
                int[][] gen = ap.generateViaPrime(dir);
                System.err.println("after generate");
                if (gen.length!=pp*pp) {
                    throw new IllegalArgumentException("Bad length");
                }
            }
        }
    }

    /**
     * Class to read input file, create choices, and translate
     */
    private static class Translator {

        Translator(String filename, File cfile) throws IOException {
            BufferedReader br = null;
            try {
                //br = new BufferedReader(new FileReader(filename));
                br = new BufferedReader(new FileReader(cfile));
                List<String[]> sofar = new ArrayList<String[]>();
                for(;;){
                    String line = br.readLine();
                    if (line==null) {
                        break;
                    }
                    List<String> sl = new ArrayList<String>();
                    StringTokenizer st = new StringTokenizer(line, "|"); // now tokenize on | instead of , because values may contain ,
                    while (st.hasMoreTokens()){
                        String token = st.nextToken();
                        /*int pos = token.indexOf('#'); // what is this? comments? we neeed to be able to use all chars for values
                         if (pos == 0)
                         {
                         break;
                         }
                         else if (pos > 0)
                         {
                         sl.add(token.substring(0, pos));
                         break;
                         }*/
                        sl.add(token);
                    }
                    int len = sl.size();

                    //if (len == 0)
                    //{
                    //	//System.err.println(
                    //	//  "Discarding line with only one option: " + sl.get(0));
                    //}
                    //else if (len >= 1)
                    if (len>=1) {
                        String[] fromLine = new String[len];
                        sofar.add(sl.toArray(fromLine));
                    }
                }
                int lines = sofar.size();
                choiceTranslator = new String[lines][];
                choiceTranslator = sofar.toArray(choiceTranslator);
                numChoices = new int[lines];
                for(int i = 0; i<numChoices.length; i++){
                    numChoices[i] = choiceTranslator[i].length;
                }
            }
            finally {
                if (br!=null) {
                    br.close();
                }
            }
        }
        /**
         * number of choices
         */
        private int[] numChoices;
        /**
         * used to convert choices back to numbers
         */
        private String[][] choiceTranslator;

        int[] getNumChoices() {
            return numChoices.clone();
        }

        String translate(int col, int choice) {
            return choiceTranslator[col][choice];
        }
    }
}
