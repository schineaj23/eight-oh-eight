package com.asch.eoe.oscillators;

import com.asch.eoe.Configuration;
import com.asch.eoe.Oscillator;

public class Saw extends Oscillator {
    // This generates a sawtooth waveform
    public Saw() {
        this.freq = 0;
    }

    public Saw(double freq) {
        this.freq = freq;
    }

    @Override
    public double sample(double t) {
        double p = Configuration.SAMPLE_RATE / freq * 2;
        return 2 * (t / p - Math.floor(0.5 + t / p)) * gain;
    }
}
