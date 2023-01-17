package com.asch.eoe.oscillators;

import com.asch.eoe.Configuration;
import com.asch.eoe.Oscillator;

public class Square extends Oscillator {
    // This generates a square waveform
    public Square() {
        this.freq = 0;
    }

    public Square(double freq) {
        this.freq = freq;
    }

    @Override
    public double sample(double t) {
        double val = Math.signum(Math.sin(Math.PI * freq * t / (double)Configuration.SAMPLE_RATE));
        return 0.5 * val * gain;
    }
}
