package org.finra.datagenerator.scaffolding.transformer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.RandomStringUtils;
import org.finra.datagenerator.scaffolding.config.ConfigName;
import org.finra.datagenerator.scaffolding.config.ConfigurationUtils$;
import org.finra.datagenerator.scaffolding.random.core.JavaRubberRandom;
import org.finra.datagenerator.scaffolding.transformer.service.*;
import org.finra.datagenerator.scaffolding.transformer.function.Direction;
import org.finra.datagenerator.scaffolding.transformer.function.impl.DateTimeSequential;
import org.finra.datagenerator.scaffolding.transformer.function.impl.LongSequential;
import org.finra.datagenerator.scaffolding.transformer.join.PartialJoin$;
import org.finra.datagenerator.scaffolding.transformer.service.adapter.TransformerAdapter;
import org.finra.datagenerator.scaffolding.transformer.service.adapter.XSDAdapter;
import org.finra.datagenerator.scaffolding.transformer.service.transformations.TransformationsImpl;
import org.finra.datagenerator.scaffolding.transformer.support.Transformation;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

/**
 * Created by dkopel on 9/27/16.
 */

@DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
@RunWith(SpringRunner.class)
@SpringBootTest
public class TransformTest {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Transformer t = Transformer.apply();
    private final JavaRubberRandom random = JavaRubberRandom.apply();

    {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    @SpringBootConfiguration
    @ComponentScan({
        "org.finra.datagenerator.scaffolding.random.*",
        "org.finra.datagenerator.scaffolding.transformer.*"
    })
    static class Conf {
        @Bean
        public ConfigurationUtils$ getConfigUtil() {
            return ConfigurationUtils$.MODULE$;
        }
    }

    @Before
    public void before() { System.gc(); }

    @Test
    public void simpleTransformation() throws JsonProcessingException {
        final BigClass bg = random.nextObject(BigClass.class);
        final GreatClass gc = random.nextObject(GreatClass.class);

        logger.info("(INPUT) BigClass: {}", objectMapper.writeValueAsString(bg));
        logger.info("(INPUT) GreatClass: {}", objectMapper.writeValueAsString(gc));

        List<TransformationContainer> outputs = t.transform(
            Lists.<TransformationContainer>newArrayList(
                new InputTransformationContainer("big", bg),
                new InputTransformationContainer("great", gc),
                new OutputTransformationContainer("sc", SmallClass.class),
                new OutputTransformationContainer("ac", AnotherClass.class)
            )
        );

        outputs.forEach(o -> {
            if (o.clazz.equals(SmallClass.class)) {
                SmallClass sc = (SmallClass) o.value;
                Assert.assertTrue(gc.getTime() + 100 == sc.getTomorrow());
                Assert.assertTrue((bg.getId() + "SPICY" + bg.getNum()).equals(sc.getFruit()));
                Assert.assertNull(sc.getThisWillBeNull());
                if ((gc.getTime() > 100 || gc.getAmt() > 100) && gc.getTime() < 1000000) {
                    Assert.assertEquals(Mood.HAPPY, sc.getMood());
                }
            } else if (o.clazz.equals(AnotherClass.class)) {
                AnotherClass ac = (AnotherClass) o.value;
                Assert.assertTrue(bg.getName().equals(ac.getName()));
                Assert.assertTrue((bg.getName() + bg.getNum()).equals(ac.getSecretName()));
            }
        });
    }

    public static String removeWhiteSpaces(String str) {
        return str.replaceAll("\\n|\\t|\\r|\\s", "");
    }

    public static String removeNumbers(String str) {
        return str.replaceAll("[^A-Za-z]", "");
    }

    public static class StringWithSpaces {
        @Transformation(value="#removeNumbers(#removeWhiteSpaces('t h i s  45454   has a lot \n o 666f white space\r\r \t'))")
        private String lotsOfSpaces;

        public String getLotsOfSpaces() {
            return lotsOfSpaces;
        }

        public StringWithSpaces setLotsOfSpaces(String lotsOfSpaces) {
            this.lotsOfSpaces = lotsOfSpaces;
            return this;
        }
    }

    @Test
    @Ignore
    public void testJoin() {
        Transformer tt = t;

        List<InputA> inputs = new ArrayList();
        List<InputB> inputsb = new ArrayList();
        List<InputC> inputsc = new ArrayList();
        DateTimeSequential dts = new DateTimeSequential();

        random.setOverride(ConfigName.apply("longRandomizerMin"), 100L);
        random.setOverride(ConfigName.apply("longRandomizerMax"), 150L);
        for(long i=0; i < 20; i++) {
            Date d = new Date(dts.next(null).toEpochSecond(ZoneOffset.UTC));
            InputA a = random.generate(InputA.class);
            InputB b = random.generate(InputB.class);
            InputC c = random.generate(InputC.class);

            if(i != 9) {
                a.id = i;
                b.bar = i;
                c.foo = i;
                a.date = d;
                b.date1 = d;
            } else {
                a.id = i;
                b.bar = i;
                c.foo = i;

                a.date = d;
                b.date1 = d;
            }

//            if(i < 10) {
                c.date2 = d;
//            }

            inputs.add(a);
            inputsb.add(b);
            inputsc.add(c);
        }

        TransformationContext context = new TransformationContext(
            0L,
            Maps.newHashMap(ImmutableMap.<Long, Collection<TransformationContainer>>builder()
                .put(0L, Lists.newArrayList(
                    new InputTransformationContainer("ia", inputs),
                    new InputTransformationContainer("ib", inputsb),
                    new InputTransformationContainer("ic", inputsc),
                    new OutputTransformationContainer("output", OutputC.class, true)
                )).build()),
            new HashSet()
        );
        //tt.setOverride(ConfigName.apply("joinType"), PartialJoin$.MODULE$);
        tt.join(context, tt);

//        logger.debug("OUTPUTS: {}", tt.toString(out));

        Collection<TransformationContainer> out = context.containers.get(0L);

        out.forEach(o -> {
            try {
                logger.debug("Out: {}", objectMapper.writeValueAsString(o));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

            if(o instanceof OutputTransformationContainer) {
                Assert.assertEquals(10, ((Collection) o.value).size());
            }
        });

        tt.setOverride(ConfigName.apply("joinType"), PartialJoin$.MODULE$);

        tt.join(context, tt);

        out = context.containers.get(0L);

        out.forEach(o -> {
            if (o instanceof OutputTransformationContainer) {
                Assert.assertEquals(20, ((Collection) o.value).size());
            }
        });
    }

    @Test
    public void testSimpleContextFunction() throws Exception {
        Transformer t = Transformer.apply();
        t.registerContextFunction("removeWhiteSpaces", TransformTest.class.getDeclaredMethod("removeWhiteSpaces", new Class[] { String.class}));
        t.registerContextFunction("removeNumbers", TransformTest.class.getDeclaredMethod("removeNumbers", new Class[] { String.class}));

        List<TransformationContainer> outputs = t.transform(Lists.newArrayList(
            new OutputTransformationContainer<>("white", StringWithSpaces.class)
        ));

        outputs.stream()
            .filter(o -> o.clazz.equals(StringWithSpaces.class))
            .map(o -> (StringWithSpaces) o.value)
            .forEach(o -> {
                Assert.assertEquals("thishasalotofwhitespace", o.getLotsOfSpaces());
                try {
                    logger.debug("Outputs: {}", objectMapper.writeValueAsString(o));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            });
    }

    @Test
    public void testRegisterTransformation() throws Exception {
        final BigClass bg = random.nextObject(BigClass.class);
        final GreatClass gc = random.nextObject(GreatClass.class);
        logger.debug("BigClass: {}", t.toString(bg));

        Transformer t = Transformer.apply();
        t.registerTransformations(
            TransformationsImpl.multiBuilder()
                .withField(SmallClass.class.getDeclaredField("fruit"))
                    .withTransformations()
                        .withTransformation()
                            .withValue("#big.name")
                        .build()
                    .build()
                .build()
                .withField(SmallClass.class.getDeclaredField("tomorrow"))
                    .withTransformations()
                        .withTransformation()
                            .withValue("#big.num")
                        .build()
                .build()
            .build()
        .build());

        List<TransformationContainer> outputs = t.transform(Lists.<TransformationContainer>newArrayList(
            new InputTransformationContainer("great", gc),
            new InputTransformationContainer("big", bg),
            new OutputTransformationContainer("sc", SmallClass.class)
        ), TransformationSessionType.REGISTERED_ONLY);

        SmallClass sc = outputs.stream()
            .filter(c -> c.alias.equals("sc"))
            .map(c -> (TransformationContainer<SmallClass>) c)
            .findFirst()
            .get()
            .value;
        Assert.assertEquals(bg.getName(), sc.getFruit());
        Assert.assertEquals(bg.getNum(), sc.getTomorrow());
    }

    @Test
    public void testRegisterTransformationMerge() throws Exception {
        final BigClass bg = random.nextObject(BigClass.class);
        final GreatClass gc = random.nextObject(GreatClass.class);
        logger.debug("BigClass: {}", t.toString(bg));

        Transformer t = Transformer.apply();
        t.registerTransformations(
            TransformationsImpl.multiBuilder()
                .withField(SmallClass.class.getDeclaredField("justForOverride"))
                    .withTransformations()
                        .withTransformation()
                            .withCondition("#big.id < 100")
                            .withValue("'test'")
                        .build()
                        .withTransformation()
                            .withCondition("#big.id > 400")
                            .withValue("'big test'")
                        .build()
                    .build()
                .build()
            .build());

        bg.setId(50);
        List<TransformationContainer> outputs = t.transform(Lists.<TransformationContainer>newArrayList(
            new InputTransformationContainer("big", bg),
            new InputTransformationContainer("great", gc),
            new OutputTransformationContainer("sc", SmallClass.class)
        ), TransformationSessionType.MERGE);

        SmallClass sc = outputs.stream()
            .filter(c -> c.alias.equals("sc"))
            .map(c -> (TransformationContainer<SmallClass>) c)
            .findFirst()
            .get()
            .value;
        Assert.assertEquals(bg.getId()+"SPICY"+bg.getNum(), sc.getFruit());
        Assert.assertEquals(gc.getTime()+100L, (long)sc.getTomorrow());
        Assert.assertEquals("test", sc.getJustForOverride());

        bg.setId(500);
        outputs = t.transform(Lists.<TransformationContainer>newArrayList(
            new InputTransformationContainer("big", bg),
            new InputTransformationContainer("great", gc),
            new OutputTransformationContainer("sc", SmallClass.class)
        ), TransformationSessionType.MERGE);

        sc = outputs.stream()
            .filter(c -> c.alias.equals("sc"))
            .map(c -> (TransformationContainer<SmallClass>) c)
            .findFirst()
            .get()
            .value;
        Assert.assertEquals(sc.getJustForOverride(), "big test");
    }

    @Test
    public void testGlobals() {
        final BigClass bg = random.nextObject(BigClass.class);

        // HAPPY - great.time > 100 || great.amt > 100
        GreatClass gc1 = new GreatClass(400L, 200D);
        t.setGlobal("name", "dmytro");
        List<TransformationContainer> outputs = t.transform(
            Lists.newArrayList(
                new InputTransformationContainer("big", bg),
                new InputTransformationContainer("great", gc1),
                new OutputTransformationContainer("sc", SmallClass.class)
            )
        );
        outputs.stream()
            .filter(o -> o.clazz.equals(SmallClass.class))
            .map(o -> (TransformationContainer<SmallClass>) o)
            .forEach(sc -> Assert.assertTrue(sc.value.getHeyThere().equals("green")));

        t.setGlobal("name", "dovid");
        outputs = t.transform(
            Lists.newArrayList(
                new InputTransformationContainer("big", bg),
                new InputTransformationContainer("great", gc1),
                new OutputTransformationContainer("sc", SmallClass.class)
            )
        );
        outputs.stream()
            .filter(o -> o.clazz.equals(SmallClass.class))
            .map(o -> (TransformationContainer<SmallClass>) o)
            .forEach(sc -> Assert.assertTrue(sc.value.getHeyThere().equals("blue")));
    }

    @Test
    public void conditionalTransformation() {
        final BigClass bg = random.nextObject(BigClass.class);

        // HAPPY - great.time > 100 || great.amt > 100
        GreatClass gc1 = new GreatClass(400L, 200D);
        List<TransformationContainer> outputs = t.transform(
            Lists.newArrayList(
                new InputTransformationContainer("big", bg),
                new InputTransformationContainer("great", gc1),
                new OutputTransformationContainer("sc", SmallClass.class)
            )
        );
        outputs.stream()
            .filter(o -> o.clazz.equals(SmallClass.class))
            .map(o -> (TransformationContainer<SmallClass>) o)
            .forEach(sc -> Assert.assertTrue(sc.value.getMood().equals(Mood.HAPPY)));


        // Complex condition yield NONE
        gc1 = new GreatClass(400_000_000L, 200D);
        outputs = t.transform(
            Lists.newArrayList(
                new InputTransformationContainer("big", bg),
                new InputTransformationContainer("great", gc1),
                new OutputTransformationContainer("sc", SmallClass.class)
            )
        );
        outputs.stream()
            .filter(o -> o.clazz.equals(SmallClass.class))
            .map(o -> (TransformationContainer<SmallClass>) o)
            .forEach(sc -> Assert.assertTrue(sc.value.getMood().equals(Mood.NONE)));

        // Neither HAPPY or SAD conditions are true
        gc1 = new GreatClass(50L, 100D);
        outputs = t.transform(
            Lists.newArrayList(
                new InputTransformationContainer("big", bg),
                new InputTransformationContainer("great", gc1),
                new OutputTransformationContainer("sc", SmallClass.class)
            )
        );
        outputs.stream()
            .filter(o -> o.clazz.equals(SmallClass.class))
            .map(o -> (TransformationContainer<SmallClass>) o)
            .forEach(sc -> Assert.assertTrue(sc.value.getMood().equals(Mood.NONE)));

        // Sad is true - time < 50 and amt < 50
        gc1 = new GreatClass(5L, 10D);
        outputs = t.transform(
            Lists.newArrayList(
                new InputTransformationContainer("big", bg),
                new InputTransformationContainer("great", gc1),
                new OutputTransformationContainer("sc", SmallClass.class)
            )
        );
        outputs.stream()
            .filter(o -> o.clazz.equals(SmallClass.class))
            .map(o -> (TransformationContainer<SmallClass>) o)
            .forEach(sc -> Assert.assertTrue(sc.value.getMood().equals(Mood.SAD)));

        MultiTransformer mt = new MultiTransformer(random, objectMapper, t);
        Map m = new HashMap();




        try {

        } catch(IllegalArgumentException e) {
            logger.info("Invalid was invalid!");
        }


    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidation() {
        MultiTransformer mt = new MultiTransformer(random, objectMapper, t);
        Map m = new HashMap();
        final BigClass bg = random.nextObject(BigClass.class);

        LongStream.range(0, 10L).forEach(i -> {
            m.put(i,
                Lists.newArrayList(
                    new InputTransformationContainer("big", bg),
                    new OutputTransformationContainer("invalid", InvalidClass.class)
                ));
        });

        Map<Long, ? extends Collection<TransformationContainer>> oos = mt.orderedTransform(
            m,
            2L,
            ImmutableMap.<Predicate<TransformationContext>, Set<OutputOverride<?>>>builder()
                .put(
                    t -> true,
                    Sets.newHashSet(
                        new OutputOverride<>(InvalidClass.class, "invalid", tt -> true, () -> RandomStringUtils.randomAscii(100))
                    )
                ).build()
        );
    }

    @Test
    public void testValidation2() {
        MultiTransformer mt = new MultiTransformer(random, objectMapper, t);
        Map m = new HashMap();
        final BigClass bg = random.nextObject(BigClass.class);

        LongStream.range(0, 10L).forEach(i -> {
            m.put(i,
                Lists.newArrayList(
                    new InputTransformationContainer("big", bg),
                    new OutputTransformationContainer("invalid", InvalidClass.class)
                ));
        });

        Map<Long, ? extends Collection<TransformationContainer>> oos = mt.orderedTransform(
            m,
            10L,
            ImmutableMap.<Predicate<TransformationContext>, Set<OutputOverride<?>>>builder()
                .put(
                    t -> true,
                    Sets.newHashSet(
                        new OutputOverride<>(InvalidClass.class, "invalid", tt -> true, () -> RandomStringUtils.randomAscii(10))
                    )
                ).build()
        );
    }

    @Test
    public void transformationOrder() {
        final BigClass bg = random.nextObject(BigClass.class);

        // tomorrow=great.time+100 (-100)
        // great.time is negative: weather -> CLOUDY
        // great.time is negative and great.amt < 50: mood -> SAD
        final GreatClass gc1 = new GreatClass(-200L, 10D);
        List<TransformationContainer> outputs = t.transform(
            Lists.newArrayList(
                new InputTransformationContainer("big", bg),
                new InputTransformationContainer("great", gc1),
                new OutputTransformationContainer("sc", SmallClass.class)
            )
        );
        outputs.stream()
            .filter(o -> o.clazz.equals(SmallClass.class))
            .map(o -> (TransformationContainer<SmallClass>) o)
            .forEach(scc -> {
                SmallClass sc = scc.value;
                Assert.assertTrue(sc.getTomorrow() == gc1.getTime()+100);
                Assert.assertTrue(Sets.newHashSet(sc.getWeather()).contains(Weather.CLOUDY));
                Assert.assertEquals(false, sc.getGoOnTrip());
            });


        final GreatClass gc2 = new GreatClass(200L, 10D);
        outputs = t.transform(
            Lists.newArrayList(
                new InputTransformationContainer("big", bg),
                new InputTransformationContainer("great", gc2),
                new OutputTransformationContainer("sc", SmallClass.class)
            )
        );
        outputs.stream()
            .filter(o -> o.clazz.equals(SmallClass.class))
            .map(o -> (TransformationContainer<SmallClass>) o)
            .forEach(scc -> {
                SmallClass sc = scc.value;
                Assert.assertTrue(sc.getTomorrow() == gc2.getTime()+100);
                Assert.assertTrue(Sets.newHashSet(sc.getWeather()).contains(Weather.SUNNY));
                Assert.assertEquals(true, sc.getGoOnTrip());
            });
    }

    @Test
    public void dateTimeSequentialTest() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeSequential dt = new DateTimeSequential(now);
        Assert.assertEquals(now.plus(Duration.ofDays(0)), dt.next(null));
        Assert.assertEquals(now.plus(Duration.ofDays(1)), dt.next(null));
        Assert.assertEquals(now.plus(Duration.ofDays(2)), dt.next(null));
        Assert.assertEquals(now.plus(Duration.ofDays(3)), dt.next(null));

        LocalDateTime july4th = LocalDateTime.of(1776, Month.JULY, 4, 0, 0);
        dt = new DateTimeSequential(july4th, Duration.ofHours(2));
        Assert.assertEquals(july4th, dt.next(null));
        Assert.assertEquals(july4th.plus(Duration.ofHours(2)), dt.next(null));
        Assert.assertEquals(july4th.plus(Duration.ofHours(4)), dt.next(null));
        Assert.assertEquals(july4th.plus(Duration.ofHours(6)), dt.next(null));
        Assert.assertEquals(july4th.plus(Duration.ofHours(8)), dt.next(null));

        dt = new DateTimeSequential(july4th, Duration.ofHours(2), Direction.DESCENDING);
        Assert.assertEquals(july4th, dt.next(null));
        Assert.assertEquals(july4th.minus(Duration.ofHours(2)), dt.next(null));
        Assert.assertEquals(july4th.minus(Duration.ofHours(4)), dt.next(null));
        Assert.assertEquals(july4th.minus(Duration.ofHours(6)), dt.next(null));
        Assert.assertEquals(july4th.minus(Duration.ofHours(8)), dt.next(null));
    }

    @Test
    public void functionTransformationTest() throws Exception {
        MultiTransformerService mt = new MultiTransformer(random, objectMapper, Transformer.apply());
        List<FunctionTransformationContainer<LongSequential>> nss = mt.getFunctionTransformation(LongSequential.class);
        nss.stream().filter(i -> i.clazz.equals(LongSequential.class))
            .forEach(o -> {
                LongSequential ns = o.inst;
                Assert.assertTrue(0 == ns.next(null));
                Assert.assertTrue(1 == ns.next(null));
                Assert.assertTrue(2 == ns.next(null));
                Assert.assertTrue(3 == ns.next(null));
            });

        mt.getFunctionTransformation(DateTimeSequential.class).stream()
            .filter(i -> i.clazz.equals(DateTimeSequential.class))
            .forEach(o -> {
                DateTimeSequential dt = o.inst;
                LocalDateTime start = dt.getStart();
                Assert.assertEquals(start.plus(Duration.ofDays(0)), dt.next(null));
                Assert.assertEquals(start.plus(Duration.ofDays(1)), dt.next(null));
                Assert.assertEquals(start.plus(Duration.ofDays(2)), dt.next(null));
                Assert.assertEquals(start.plus(Duration.ofDays(3)), dt.next(null));
                Assert.assertEquals(start.plus(Duration.ofDays(4)), dt.next(null));
            });


        mt = new MultiTransformer(random, objectMapper, Transformer.apply());
        mt.setFunctionTransformation("test", LongSequential.class, new Object[]{10.00 , 5}, new Class[]{Long.class, long.class});
        nss = mt.getFunctionTransformation(LongSequential.class);
        LongSequential ns = nss.stream().filter(c -> c.key.equals("test")).iterator().next().inst;
        Assert.assertEquals((Object) 10L, ns.next(null));
        Assert.assertEquals((Object) 15L, ns.next(null));
        Assert.assertEquals((Object) 20L, ns.next(null));
        Assert.assertEquals((Object) 25L, ns.next(null));

        mt.setFunctionTransformation("test1", LongSequential.class, new Object[]{100L, 5L, Direction.DESCENDING }, new Class[]{Long.class, Long.class, Direction.class});
        FunctionTransformationContainer<LongSequential> ns1 = mt.getFunctionTransformation("test1", LongSequential.class);
        Assert.assertNotNull(ns1);
        ns = ns1.inst;
        Assert.assertNotNull(ns);
        Assert.assertEquals((Object) 100L, ns.next(null));
        Assert.assertEquals((Object) 95L, ns.next(null));
        Assert.assertEquals((Object) 90L, ns.next(null));
        Assert.assertEquals((Object) 85L, ns.next(null));

        Assert.assertEquals(3, mt.getFunctionTransformation(LongSequential.class).size());
    }

    @Test
    public void multiTransformerTest() throws Exception {
//        MultiTransformer mt = new MultiTransformer(random, objectMapper, new Transformer(random, objectMapper));
        MultiTransformer mt = new MultiTransformer(random, objectMapper, Transformer.apply());
        mt.setFunctionTransformation("seq", LongSequential.class);
        mt.setFunctionTransformation("seq1", LongSequential.class, new Object[]{10.00 , 5}, new Class[]{Long.class, long.class});
        mt.setFunctionTransformation("date1", DateTimeSequential.class, new Object[]{LocalDateTime.of(1776, Month.JULY, 4, 0, 0)}, new Class[]{LocalDateTime.class});

        Map m = new HashMap();

        LongStream.range(0, 10L).forEach(i -> {
            BigClass bg = random.nextObject(BigClass.class);
            GreatClass gc1 = new GreatClass(-200L, 10D);

            m.put(i, Lists.newArrayList(
                new InputTransformationContainer("big", bg),
                new InputTransformationContainer("great", gc1),
                new OutputTransformationContainer("sc", SmallClass.class),
                new OutputTransformationContainer("ac", AnotherClass.class)
            ));
        });


        // tomorrow=great.time+100 (-100)
        // great.time is negative: weather -> CLOUDY
        // great.time is negative and great.amt < 50: mood -> SAD
        Map<Predicate<TransformationContext>, Set<OutputOverride<?>>> overrides = new HashMap();
        overrides.put(
            ct -> ct.iteration % 2 == 0,
            Sets.newHashSet(
                new OutputOverride<>(
                    SmallClass.class,
                    "fruit",
                    (sc) -> true,
                    () -> "flounder"
                )
            )
        );

        Map<Long, ? extends Collection<TransformationContainer>> os = mt.orderedTransform(
            m,
            10L,
            overrides
        );

        AtomicLong expected = new AtomicLong(10L);

        logger.debug("Complete output: {}", objectMapper.writeValueAsString(os));

        os.entrySet().forEach(r -> {
            SmallClass sc = (SmallClass) r.getValue().stream().filter(e -> e.clazz.equals(SmallClass.class)).findFirst().get().value;
            AnotherClass ac = (AnotherClass) r.getValue().stream().filter(e -> e.clazz.equals(AnotherClass.class)).findFirst().get().value;

            Assert.assertNull(sc.getThisWillBeNull());

            if(r.getKey() % 2 == 0) {
                Assert.assertTrue(sc.getThisWillBeEmpty().isEmpty());
            } else {
                Assert.assertFalse(sc.getThisWillBeEmpty().isEmpty());
            }

            Long ex = expected.getAndAdd(5L);
            Assert.assertEquals(ex, sc.getSeq());
            if(r.getKey() % 2 == 0) {
                Assert.assertEquals("flounder", sc.getFruit());
            }

            if(r.getKey() % 2 == 0) {
                Assert.assertEquals((Object)  ac.getSeq(), sc.getSeq()+10L);
            } else {
                Assert.assertEquals((Object)  ac.getSeq(), sc.getSeq()-10L);
            }

            try {
                logger.debug("Entry #{} class {}, output {}", r.getKey(), objectMapper.writeValueAsString(r));
            } catch (JsonProcessingException e1) {
                e1.printStackTrace();
            }


        });
    }

    class RandomDate {
        public List<Date> dates;


        public List<String> getDates() {
            return dates.stream().map(d -> LocalDateTime.ofInstant(d.toInstant(), ZoneId.systemDefault()).format(DateTimeFormatter.ISO_LOCAL_DATE)
            ).collect(Collectors.toList());
        }

        public List<Date> manyDates;
    }


//    @Test
//    public void datesNumber() throws JsonProcessingException {
//        config.configRandomizers()
//            .randomize(XMLGregorianCalendar.class, new Randomizer() {
//                public XMLGregorianCalendar getRandomValue() {
//                    Date d = new DateRandomizer().getRandomValue();
//                    GregorianCalendar g = new GregorianCalendar();
//                    g.setTime(d);
//                    return new XMLGregorianCalendarImpl(g);
//                }
//            });
//
//        RubberRandom random = new RubberRandom(config);
//
//        ObjectMapper m = new ObjectMapper();
//        class DateFormatModule extends SimpleModule {
//            public DateFormatModule() {
//                addSerializer(XMLGregorianCalendar.class, new JsonSerializer<XMLGregorianCalendar>() {
//                    @Override
//                    public void serialize(XMLGregorianCalendar xmlGregorianCalendar, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
//                        LocalDateTime dt = LocalDateTime.ofInstant(xmlGregorianCalendar.toGregorianCalendar().toInstant(), ZoneId.systemDefault());
//                        String ft = dt.format(DateTimeFormatter.ISO_LOCAL_DATE);
//                        logger.debug("LocalDateTime {}, yields formatted string: {}", dt.toEpochSecond(ZoneOffset.UTC), ft);
//                        jsonGenerator.writeString(ft);
//                    }
//                });
//            }
//        }
//        m.registerModule(new DateFormatModule());
//        XMLGregorianCalendar gc = random.nextObject(XMLGregorianCalendar.class);
//        // Don't use `writeValueAsString()` this adds " " quotes!
//        String gcs = m.convertValue(gc, String.class);
//        String expected = String.format("%4d-%02d-%02d", gc.getYear(), gc.getMonth(), gc.getDay());
//        logger.debug("Real: {}, Expected: {}", gcs, expected);
//        Assert.assertTrue(gcs.equals(expected));
//    }


    @Ignore
    @Test
    public void xsdTest() throws Exception {
        System.gc();
//        RandomProvider rndm = new RubberRandomImpl();

        Path input = Paths.get((ClassLoader.getSystemResource("test.xsd").toURI()));

        TransformerAdapter xsdAdapter = new XSDAdapter();
        Collection<Class<?>> classes = xsdAdapter.convert(input);
        System.gc();
        classes.forEach(c -> System.out.println(c.getName()));

        classes.forEach(c -> {
            System.out.println("Class: "+c.getName());
            Object inst = random.nextObject(c);
            JAXBContext context = null;
            try {
                context = JAXBContext.newInstance(c);
                Marshaller marshaller = context.createMarshaller();
                marshaller.marshal(inst, Paths.get(c.getSimpleName()+".xml").toFile());
            } catch (JAXBException e) {


            }
            System.gc();
        });


    }

}
