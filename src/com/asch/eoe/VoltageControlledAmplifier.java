package com.asch.eoe;

public class VoltageControlledAmplifier {
    private Envelope envelope;
    private double gain;

    public VoltageControlledAmplifier(Envelope envelope, double gain) {
        this.envelope = envelope;
        this.gain = gain;
    }

    public VoltageControlledAmplifier(Envelope envelope) {
        this(envelope, 1);
    }

    public double sample(double value, double t) {
        return value + envelope.sample(value, t) * gain;
    }

}
