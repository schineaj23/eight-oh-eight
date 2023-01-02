package com.asch.eoe;

public abstract class Filter {
    protected Envelope envelope;

    public abstract double sample(double value, double t);

    // Some filters, such as cymbal synthesis, sound better
    // If the filter itself has its own envelope of effect
    // Such that the amplitudes of other frequencies vary by time
    public void setEnvelope(Envelope envelope) {
        this.envelope = envelope;
    }

    protected double sampleEnvelope(double input, double t) {
        return (envelope == null) ? 1 : envelope.sample(input, t);
    }
}
