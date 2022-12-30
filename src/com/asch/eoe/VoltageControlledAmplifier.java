package com.asch.eoe;

// Amplifies signal based off an envelope and a gain
public class VoltageControlledAmplifier {
    private final Envelope envelope;
    private final double gain;

    public VoltageControlledAmplifier(Envelope envelope, double gain) {
        this.envelope = envelope;
        this.gain = gain;
    }

    public VoltageControlledAmplifier(Envelope envelope) {
        this(envelope, 1);
    }

    public double sample(double value, double t) {
        return value * envelope.sample(value, t) * gain;
    }

}
