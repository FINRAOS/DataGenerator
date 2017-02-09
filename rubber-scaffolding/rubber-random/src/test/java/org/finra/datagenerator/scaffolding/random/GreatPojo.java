package org.finra.datagenerator.scaffolding.random;

import java.util.List;
import java.util.Map;

/**
 * Created by dkopel on 11/25/16.
 */
public class GreatPojo {
    private List<Map<String, Integer>> ms;

    private Map<String, Long> ls;

    private String ss;

    private Double[] dd;

    private Color color;

    private long[] lll;

    private float f;

    private Blah<String> blah;

    public List<Map<String, Integer>> getMs() {
        return ms;
    }

    public Map<String, Long> getLs() {
        return ls;
    }

    public String getSs() {
        return ss;
    }

    public Double[] getDd() {
        return dd;
    }

    public long[] getLll() {
        return lll;
    }

    public float getF() {
        return f;
    }

    public Blah<String> getBlah() {
        return blah;
    }

    public GreatPojo setMs(List<Map<String, Integer>> ms) {
        this.ms = ms;
        return this;
    }

    public GreatPojo setLs(Map<String, Long> ls) {
        this.ls = ls;
        return this;
    }

    public GreatPojo setSs(String ss) {
        this.ss = ss;
        return this;
    }

    public GreatPojo setDd(Double[] dd) {
        this.dd = dd;
        return this;
    }

    public GreatPojo setLll(long[] lll) {
        this.lll = lll;
        return this;
    }

    public GreatPojo setF(float f) {
        this.f = f;
        return this;
    }

    public GreatPojo setBlah(Blah<String> blah) {
        this.blah = blah;
        return this;
    }

    public Color getColor() {
        return color;
    }

    public GreatPojo setColor(Color color) {
        this.color = color;
        return this;
    }
}
