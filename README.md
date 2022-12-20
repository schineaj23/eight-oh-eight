# 808 Simulator

REQUIREMENTS
=====
Generate the same sounds as it was on the 808
 - Create tones that are similar to the 808
 - Create a UI that looks and feels like the 808

Have sequencing/programming and playback like the 808
 - Tempo
 - 16 steps total
 - Save/load sequences like the 808

Basic mixing
 - Solo/Master
 - Volume
 - Tap button

RESOURCES
=====
- https://en.wikipedia.org/wiki/Roland_TR-808 <- Wikipedia article
- http://cdn.roland.com/assets/media/pdf/TR-808_OM.pdf <- 808 User Manual
- https://en.wikipedia.org/wiki/Subtractive_synthesis (the analog subtractive synthesis method used by it)
- https://en.wikipedia.org/wiki/Additive_synthesis#Explanation (you need additive synthesis to create subtractive synthesis)
- https://musicskanner.com/attack-sustain-decay-release-envelope-explained/ (ASDR envelopes explained 1000ft view)
- https://maplelab.net/overview/amplitude-envelope/ (Amplitude envelope explained at 1000ft view)
- https://unison.audio/808-drum/ Faking an 808 1000ft view
- https://gearspace.com/board/electronic-music-instruments-and-electronic-music-production/460283-how-do-you-synthesize-808-ish-drums.html Good Forum thread


PROGRAMMING RESOURCES
====
- https://docs.oracle.com/javase/tutorial/sound/index.html <- For sound


IDEAL ERGONOMICS
====
I want the way of creating each sound similar to a factory/builder interface
For example:
```java
SoundBuilder builder = new SoundBuilder();
builder.oscillator(Sine.class).add(Square.class).lowPassFilter(500).delay(10);
Sound cowbell = builder.sound();
```
Not sure if this is the best method of doing things...