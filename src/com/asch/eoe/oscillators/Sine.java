package com.asch.eoe.oscillators;

import com.asch.eoe.Configuration;
import com.asch.eoe.Oscillator;

public class Sine extends Oscillator {
    public Sine() {
        this.freq = 0;
    }

    public Sine(double freq) {
        this.freq = freq;
    }

    @Override
    public double sample(double t) {
        return 0.5 * Math.sin(Math.PI * freq * t / Configuration.SAMPLE_RATE);
    }
}
