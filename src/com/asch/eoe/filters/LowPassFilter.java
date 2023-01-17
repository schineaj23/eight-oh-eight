package com.asch.eoe.filters;

import com.asch.eoe.Configuration;
import com.asch.eoe.Filter;

public class LowPassFilter implements Filter {
    // The LPF passes signals lower than the cutoff frequency
    private double previousSample = 0;
    private double alpha;

    public LowPassFilter(double freq) {
        double dt = 1f / Configuration.SAMPLE_RATE;
        double factor = 2 * Math.PI * freq * dt;
        this.alpha = factor / (factor + 1);

        System.out.println("LowPassFilter");
        System.out.println("dt " + dt);
        System.out.println("factor " + factor);
        System.out.println("alpha " + alpha);
    }

    @Override
    public double sample(double value) {
        double sample = alpha * value + (1 - alpha) * previousSample;
        // System.out.printf("IN: %f OUT: %f\n", value, sample);
        previousSample = sample;
        return sample;
    }
}
