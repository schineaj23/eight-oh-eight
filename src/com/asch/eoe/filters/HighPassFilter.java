package com.asch.eoe.filters;

import com.asch.eoe.Configuration;
import com.asch.eoe.Filter;

public class HighPassFilter implements Filter {
    // The high pass filter passes signals only higher than the cutoff frequency
    // The HPF removes those below the cutoff frequency
    private double previousSample = 0;
    private double previousInput = 0;
    private double alpha;

    public HighPassFilter(double freq) {
        double dt = 1f / Configuration.SAMPLE_RATE;
        this.alpha = 1f / ((2 * Math.PI * freq * dt) + 1);
        this.previousSample = 1;

        System.out.println("HighPassFilter");
        System.out.println("dt " + dt);
        System.out.println("alpha " + alpha);
    }

    @Override
    public double sample(double input) {
        double sample = alpha * previousSample + alpha * (input - previousInput);
        previousSample = sample;
        previousInput = input;
        return sample;
    }
}
