package org.finra.datagenerator.scaffolding.transformer;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.finra.datagenerator.scaffolding.transformer.function.impl.DateTimeSequential;
import org.finra.datagenerator.scaffolding.transformer.function.impl.LongSequential;
import org.finra.datagenerator.scaffolding.transformer.support.FunctionTransformation;
import org.finra.datagenerator.scaffolding.transformer.support.Order;
import org.finra.datagenerator.scaffolding.transformer.support.Transformation;

import java.time.LocalDateTime;

/**
 * Created by dkopel on 9/27/16.
 */
public class SmallClass {
    @Order(4)
    @Transformation("#big.id+'SPICY'+#big.num")
    private String fruit;

    @Order(0)
    @Transformation("#great.time+100")
    private Long tomorrow;

    @Order(1)
    @Transformation(condition = "tomorrow > 200", value="{'SUNNY', 'HOT'}", order=0)
    @Transformation(condition = "tomorrow < 150 && tomorrow > 50", value="{'COLD', 'RAIN'}", order=1)
    @Transformation(value="{'CLOUDY'}", order=2)
    private Weather[] weather;

    @Order(2)
    @Transformation(value="'HAPPY'", condition="#great.time < 1000000 && (#great.time > 100 || #great.amt > 100.00)", order=0)
    @Transformation(value="'SAD'", condition="#great.time < 50 && #great.amt < 50.00", order=1)
    @Transformation(value="'NONE'", order=2)
    private Mood mood;

    @Order(3)
    @Transformation(value="false", condition="#asList(weather).contains('RAIN') || mood == Mood.SAD", order=0)
    @Transformation(value="true", order=1)
    private Boolean goOnTrip;

    @Transformation(isNull = true)
    private String thisWillBeNull;

    @Transformation(emptyString = true, condition = "#iteration % 2 == 0")
    private String thisWillBeEmpty;

    private Integer id;

    @Transformation(function=@FunctionTransformation(key="seq", clazz=LongSequential.class))
    private Long seqSimple;

    @Transformation(function=@FunctionTransformation(key="seq1", clazz=LongSequential.class))
    private Long seq;

    @Transformation(function=@FunctionTransformation(key="date1", clazz=DateTimeSequential.class))
    private LocalDateTime dateSeq;

    private String justForOverride;

    @Transformation(value = "'green'", condition = "#globals['name'] == 'dmytro'")
    @Transformation(value = "'blue'", condition = "#globals['name'] == 'dovid'")
    private String heyThere;

    @Transformation(value="")
    private String emptyValue;

    public String getFruit() {
        return fruit;
    }

    public Integer getId() {
        return id;
    }

    public Mood getMood() {
        return mood;
    }

    public Long getTomorrow() {
        return tomorrow;
    }

    public Weather[] getWeather() {
        return weather;
    }

    public Boolean getGoOnTrip() {
        return goOnTrip;
    }

    public String getThisWillBeNull() {
        return thisWillBeNull;
    }

    public Long getSeq() {
        return seq;
    }

    public String getJustForOverride() {
        return justForOverride;
    }

    public String getThisWillBeEmpty() {
        return thisWillBeEmpty;
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    public LocalDateTime getDateSeq() {
        return dateSeq;
    }

    public Long getSeqSimple() {
        return seqSimple;
    }

    public String getHeyThere() {
        return heyThere;
    }

    public String getEmptyValue() {
        return emptyValue;
    }
}
