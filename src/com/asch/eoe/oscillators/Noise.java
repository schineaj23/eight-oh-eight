package com.asch.eoe.oscillators;

import java.util.Random;

import com.asch.eoe.Oscillator;

public class Noise extends Oscillator {
    // This is not a typical 'oscillator'
    // Since it produces random sound, I implemented it as an oscillator
    // So that I can easily sample it in Sound
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
