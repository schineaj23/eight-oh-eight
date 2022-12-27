package com.asch.eoe;

import javax.sound.sampled.AudioFormat;

public class Configuration {
    // Constants of recording, depended on by the logic that generates waves.
    public static final int SAMPLE_RATE = 44100; // 44100Hz Sample Rate (CD Quality)
    public static final int BYTES_PER_SAMPLE = 2; // 16-bit audio
    public static final int BITS_PER_SAMPLE = 16; // 16-bit audio
    public static final double MAX_16_BIT = Short.MAX_VALUE; // 32,767
    public static final int SAMPLE_BUFFER_SIZE = 4096;
    public static final AudioFormat FORMAT = new AudioFormat((float) Configuration.SAMPLE_RATE,
            Configuration.BITS_PER_SAMPLE, 2, true, false);
}
