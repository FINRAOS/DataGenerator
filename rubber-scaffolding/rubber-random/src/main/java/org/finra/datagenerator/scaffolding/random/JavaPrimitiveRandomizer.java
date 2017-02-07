package org.finra.datagenerator.scaffolding.random;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.util.Assert;

import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Created by dkopel on 11/24/16.
 */
public final class JavaPrimitiveRandomizer {
    final public Random random;

    public IntegerRandomizer ints = new IntegerRandomizer();
    public FloatRandomizer floats = new FloatRandomizer();
    public DoubleRandomizer doubles = new DoubleRandomizer();
    public LongRandomizer longs = new LongRandomizer();
    public CharacterRandomizer chars = new CharacterRandomizer();
    public ByteRandomizer bytes = new ByteRandomizer();
    public ShortRandomizer shorts = new ShortRandomizer();
    public BooleanRandomizer booleans = new BooleanRandomizer();
    public StringRandomizer strings = new StringRandomizer();

    public JavaPrimitiveRandomizer(long seed) {
        random = new Random(seed);
    }

    public JavaPrimitiveRandomizer() {
        random = new Random();
    }

    private float convert(float n) {
        return (n < 1) ? n : convert(n * .1F);
    }

    private float convert (double n) {
        return convert(((Number) n).floatValue());
    }

    public class IntegerRandomizer {
        public Integer nextInt() {
            return random.nextInt();
        }

        public Integer nextInt(Integer start, Integer bound) {
            return intStream(1, start, bound).findFirst().getAsInt();
        }

        public IntStream intStream(Integer size, Integer start, Integer bound) {
            return random.ints(size, start, bound);
        }
    }

    public class LongRandomizer {
        public Long nextLong() {
            return random.nextLong();
        }

        public Long nextLong(Long start, Long bound) {
            return longStream(1L, start, bound).findFirst().get();
        }

        public Stream<Long> longStream(Long size, Long start, Long bound) {
            return random.longs(size, start, bound).boxed();
        }
    }

    public class DoubleRandomizer {
        public Double nextDouble() {
            return random.nextDouble();
        }

        public Double nextDouble(Double start, Double bound) {
            return doubleStream(1L, start, bound).findFirst().get();
        }

        public Stream<Double> doubleStream(Long size, Double start, Double bound) {
            return random.doubles(size, start, bound).boxed();
        }
    }

    public class FloatRandomizer {
        public Float nextFloat() {
            return random.nextFloat();
        }

        public Float nextFloat(Float start, Float bound) {
            return floatStream(1L, start, bound).findFirst().get();
        }

        public Stream<Float> floatStream(Long size, Float start, Float bound) {
            return random.doubles(size, start, bound)
                    .mapToObj(i -> convert(i));
        }
    }

    public class ByteRandomizer {
        public byte[] nextBytes(byte[] bytes) {
            for(int i=0; i<0; i++) {
                bytes[i] = nextByte();
            }
            return bytes;
        }

        public Byte nextByte() {
            return ints.nextInt((int)Byte.MIN_VALUE, (int)Byte.MAX_VALUE).byteValue();
        }

        public Byte nextByte(Byte start, Byte bound) {
            return ints.nextInt((int) start, (int) bound).byteValue();
        }

        public Stream<Byte> bytesStream(Long size, Byte start, Byte bound) {
            return random.ints(size, start, bound)
                .mapToObj(i -> (byte)i);
        }
    }

    public class CharacterRandomizer {
        final private int[] DIGITS = IntStream.range(48, 57).toArray();
        final private int[] ALPHA_LOWER = IntStream.range(97, 122).toArray();
        final private int[] ALPHA_UPPER = IntStream.range(65, 90).toArray();
        final private int[] ALPHA = ArrayUtils.addAll(ALPHA_LOWER, ALPHA_UPPER);
        final private int[] ALPHA_NUMERIC = ArrayUtils.addAll(DIGITS, ALPHA);

        public Character nextChar() {
            return (char) ints.nextInt((int) Character.MIN_VALUE, (int) Character.MAX_VALUE).intValue();
        }

        public Character nextChar(Integer start, Integer bound) {
            return charStream(1, start, bound).findFirst().get();
        }

        public char nextDigit() {
            return (char) DIGITS[ints.nextInt(0, DIGITS.length)];
        }

        public char nextLowerAlpha() {
            return (char) ALPHA_LOWER[ints.nextInt(0, ALPHA_LOWER.length)];
        }

        public char nextUpperAlpha() {
            return (char) ALPHA_UPPER[ints.nextInt(0, ALPHA_UPPER.length)];
        }

        public char nextAlpha() { return (char) ALPHA[ints.nextInt(0, ALPHA.length)];}

        public char nextAlphaNumeric() {
            return (char) ALPHA_NUMERIC[ints.nextInt(0, ALPHA_NUMERIC.length)];
        }

        public Stream<Character> charStream(Integer size, Integer start, Integer bound) {
            Assert.isTrue(start >= Character.MIN_VALUE);
            Assert.isTrue(bound < Character.MAX_VALUE);
            return ints.intStream(size, start, bound)
                .mapToObj(i -> (char) i);
        }
    }

    public class ShortRandomizer {
        public Short nextShort() {
            return ints.nextInt((int)Short.MIN_VALUE, (int)Short.MAX_VALUE).shortValue();
        }

        public Short nextShort(Short start, Short bound) {
            return ints.nextInt((int)start, (int)bound).shortValue();
        }

        public Stream<Short> shortStream(Long size, Short start, Short bound) {
            return random.ints(size, start, bound)
                .mapToObj(i -> (short)i);
        }
    }

    public class BooleanRandomizer {
        public Boolean nextBoolean() {
            return booleanStream(1).findFirst().get();
        }

        public Stream<Boolean> booleanStream(Integer size) {
            return ints.intStream(size, Integer.MIN_VALUE, Integer.MAX_VALUE)
                .mapToObj(i -> i % 2 == 0);
        }
    }

    public class StringRandomizer {
        final int MIN_DEFAULT = 0;
        final int MAX_DEFAULT = 100;

        public String next(int min, int max, Supplier<Character> action) {
            StringBuffer b = new StringBuffer();
            for(int i=0; i<=ints.nextInt(min, max);i++) {
                b.append(action.get());
            }
            return b.toString();
        }

        public String nextString() {
            return next(MIN_DEFAULT, MAX_DEFAULT, ()->chars.nextAlphaNumeric());
        }

        public String nextString(int max) {
            return next(MIN_DEFAULT, max, ()->chars.nextAlphaNumeric());
        }

        public String nextString(int min, int max) {
            return next(min, max, ()->chars.nextAlphaNumeric());
        }

        public String nextAlphaString() {
            return next(MIN_DEFAULT, MAX_DEFAULT, ()->chars.nextAlpha());
        }

        public String nextAlphString(int max) {
            return next(MIN_DEFAULT, max, ()->chars.nextAlpha());
        }

        public String nextAlphaString(int min, int max) {
            return next(min, max, ()->chars.nextAlpha());
        }
    }
}
