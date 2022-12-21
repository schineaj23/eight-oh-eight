package com.asch.eoe;
public abstract class Oscillator {
    protected double freq;

    // Each function in this class is a type of oscillator corresponding to the wave
    // Given frequency and discrete point 't', sample the wave at that point
    public abstract double sample(double t);

    public void setFrequency(double freq) {
        this.freq = freq;
    }

    public Oscillator(double freq) {
        this.freq = freq;
    }
}
