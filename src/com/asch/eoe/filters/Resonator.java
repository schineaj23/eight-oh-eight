package com.asch.eoe.filters;

import com.asch.eoe.Filter;

public class Resonator implements Filter {
    private double Q;
    private double previousValue;

    public Resonator(double Q) {
        this.Q = Q;
    }

    @Override
    public double sample(double value) {
        double sample = Q * (value + previousValue);
        previousValue = value;
        return sample;
    }
}
