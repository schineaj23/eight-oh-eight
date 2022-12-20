package com.asch.eoe.oscillators;

import com.asch.eoe.Configuration;
import com.asch.eoe.Oscillator;

public class Triangle implements Oscillator {
    @Override
    public double sample(double freq, double t) {
        double p = Configuration.SAMPLE_RATE / freq * 2;
        double saw = 2 * (t / p - Math.floor(0.5 + t / p));
        return 2 * Math.abs(saw) - 1;
    }
}
