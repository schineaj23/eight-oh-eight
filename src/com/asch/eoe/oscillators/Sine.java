package com.asch.eoe.oscillators;

import com.asch.eoe.Configuration;
import com.asch.eoe.Oscillator;

public class Sine implements Oscillator {
    @Override
    public double sample(double freq, double t) {
        return 0.5 * Math.sin(2 * Math.PI * freq * t / Configuration.SAMPLE_RATE);
    }
}
