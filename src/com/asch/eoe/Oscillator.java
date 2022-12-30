package com.asch.eoe;

public abstract class Oscillator {
    protected double freq;

    // Given frequency and discrete point 't', sample the wave at that point
    public abstract double sample(double t);

    public void setFrequency(double freq) {
        this.freq = freq;
    }
}
