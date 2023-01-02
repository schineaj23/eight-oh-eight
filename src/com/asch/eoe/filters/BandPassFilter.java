package com.asch.eoe.filters;

import com.asch.eoe.Filter;

public class BandPassFilter implements Filter {
    private final LowPassFilter lowPassFilter;
    private final HighPassFilter highPassFilter;

    public BandPassFilter(double cutoffFrequency) {
        lowPassFilter = new LowPassFilter(cutoffFrequency);
        highPassFilter = new HighPassFilter(cutoffFrequency);
    }

    @Override
    public double sample(double value) {
        double lowSample = lowPassFilter.sample(value);
        return highPassFilter.sample(lowSample);
    }
}
