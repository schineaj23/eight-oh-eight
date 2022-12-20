package com.asch.eoe;
public interface Oscillator {
    // Each function in this class is a type of oscillator corresponding to the wave
    // Given frequency and discrete point 't', sample the wave at that point
    double sample(double freq, double t);
}
