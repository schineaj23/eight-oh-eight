package com.asch.eoe;

import javax.sound.sampled.SourceDataLine;

public class Sound {
    private byte[] buffer = new byte[Configuration.SAMPLE_BUFFER_SIZE * Configuration.BYTES_PER_SAMPLE];
    private int bufferSize = 0;
    private SourceDataLine line;
    private Oscillator oscillator;
    private Envelope envelope;

    public Sound(SourceDataLine line) {
        this.line = line;
    }

    // Overridden by constants defined as Sine, Sawtooth, Triangle, Square
    public void setOscillator(Oscillator oscillator) {
        this.oscillator = oscillator;
    }

    public void setEnvelope(Envelope envelope) {
        this.envelope = envelope;
    }

    // This method generates the values of buffer for the signal
    public void generate(double freq, double duration) {
        // An envelope is optional
        if (envelope != null) {
            envelope.setDuration(duration);
        }

        for (double t = 0; t <= Configuration.SAMPLE_RATE * duration; t++) {
            /*
             * Design Choice: Have each filter and oscillator be an interface
             * This way we can iterate over the filters and oscillators in a generic
             * way without having specific logic in the 'Sound' class.
             * I did this to create a 'builder' API for creating each sound of the 808
             */
            double sample = oscillator.sample(freq, t);

            if (envelope != null) {
                sample = envelope.sample(sample, t);
            }

            // TODO: Put rest of mixing pipeline stack in here.
            play(sample);
        }
    }

    public void generate(double freq) {
        generate(freq, 1);
    }

    // Write the buffered data to the line, only when we have the full signal.
    // TODO: Refactor with Mixer API if needed, this may only work for one sound
    public void play(double sample) {
        if (sample < -1.0)
            sample = -1.0;
        if (sample > +1.0)
            sample = +1.0;

        short s = (short) (Configuration.MAX_16_BIT * sample);

        if (sample == 1.0)
            s = Short.MAX_VALUE;

        // Convert the sample (a double from -1.0 to 1.0)
        // To a short value (-32767 to 32767) which is the raw data sent to the line
        // Since we specified that our audio format is little endian, put first byte of
        // sample in first index, then bit shift right by 8 and grab second byte
        buffer[bufferSize++] = (byte) s;
        buffer[bufferSize++] = (byte) (s >> 8);

        if (bufferSize >= buffer.length) {
            line.write(buffer, 0, buffer.length);
            bufferSize = 0;
        }
    }
}
