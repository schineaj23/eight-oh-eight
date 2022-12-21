package com.asch.eoe.oscillators;

import com.asch.eoe.Configuration;
import com.asch.eoe.Oscillator;

public class Saw extends Oscillator {
    public Saw() {
        super(0);
    }

    public Saw(double freq) {
        super(freq);
    }

    @Override
    public double sample(double t) {
        double p = Configuration.SAMPLE_RATE / freq * 2;
        return 2 * (t / p - Math.floor(0.5 + t / p));
    }
}
