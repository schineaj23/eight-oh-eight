package com.asch.eoe.oscillators;

import com.asch.eoe.Configuration;
import com.asch.eoe.Oscillator;

public class FrequencyModulatedOscillator extends Oscillator {
    private Oscillator carrier;
    private Oscillator modulator;

    private double termA = 1f;
    private double termB = 1f;

    private final double carrierFreq;
    private final double modulatorFreq;

    public FrequencyModulatedOscillator(Oscillator carrier, Oscillator modulator) {
        this.carrier = carrier;
        this.modulator = modulator;

        this.carrierFreq = Math.PI * carrier.getFrequency() / (double) Configuration.SAMPLE_RATE;
        this.modulatorFreq = Math.PI * modulator.getFrequency() / (double)Configuration.SAMPLE_RATE;
    }

    public void setTermA(double termA) {
        this.termA = termA;
    }

    public void setTermB(double termB) {
        this.termB = termB;
    }

    @Override
    public double sample(double t) {
        double in = (termB / modulatorFreq) * (Math.cos(modulatorFreq * t) - 1);
        return termA * Math.sin(modulatorFreq * t - in);
    }
}
