package com.asch.eoe;

public abstract class Oscillator {
    protected double freq;
    protected double gain = 1;

    // Given frequency and discrete point 't', sample the wave at that point
    public abstract double sample(double t);

    public void setFrequency(double freq) {
        this.freq = freq;
    }

    public double getFrequency() {
        return freq;
    }

    public void setGain(double gain) {
        this.gain = gain;
    }
}
