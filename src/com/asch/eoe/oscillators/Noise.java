package com.asch.eoe.oscillators;

import java.util.Random;

import com.asch.eoe.Oscillator;

// Although this isn't strictly an 'oscillator'
// Since it generates noise on its own rather than adding
// I implemented it as an oscillator rather than a filter
public class Noise extends Oscillator {
    private final Random rand;

    public Noise() {
        rand = new Random();
    }

    public Noise(double gain) {
        this();
        this.gain = gain;
    }

    @Override
    public double sample(double t) {
        return rand.nextDouble(-1, 1) * gain;
    }
}
