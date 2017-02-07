package org.finra.datagenerator.scaffolding.transformer;

import org.finra.datagenerator.scaffolding.transformer.support.Join;
import org.finra.datagenerator.scaffolding.transformer.support.JoinField;
import org.finra.datagenerator.scaffolding.transformer.support.Transformation;

import java.util.Date;

/**
 * A.id != A.date XXXX
 * A.id   A.date
 * B.id2  B.date4
 * C.id3  C.tax
 *
 * A.id == B.id2 == C.id3 --> key="id"
 * A.date == B.date4 == C.tax --> key="date"
 *
 */

@Join(value=InputA.class, alias="ia", fields={
    @JoinField(key="id", field="id"),
    @JoinField(key="date", field="date")
})
@Join(value=InputB.class, alias="ib", fields={
//                @JoinField(key="id", field="bar")
    @JoinField(key="date", field="date1")
})
@Join(value=InputC.class, alias="ic", fields={
    @JoinField(key="id", field="foo")
//                @JoinField(key="date", field="date2")
})
public class OutputC {


    @Transformation("#joinKey_id")
    public Long idx;

    @Transformation(value="#joinKey_date")
    public Date dt;

    public String data;

    public String other;

    public String movie;

    @Transformation(value="#flttaa.app")
    public Float fll;

    @Transformation(value="#ib.date1")
    public Date blah;

    @Transformation(value="#ib.karthik")
    public Object zoom;

    public Long reallyLong;
}
