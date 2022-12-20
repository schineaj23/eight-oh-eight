package com.asch.eoe.oscillators;

import com.asch.eoe.Configuration;
import com.asch.eoe.Oscillator;

public class Square implements Oscillator{
    @Override
    public double sample(double freq, double t) {
        double val = Math.sin(Math.PI * freq * t / Configuration.SAMPLE_RATE);
        if (val < 0)
            val = -1;
        else if (val > 0)
            val = 1;
        return 0.5 * val;
    }
}
