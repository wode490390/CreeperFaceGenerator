package cn.wode490390.nukkit.brandgen.noise;

import cn.nukkit.level.generator.noise.vanilla.d.NoiseGeneratorSimplexD;
import cn.nukkit.math.NukkitRandom;

public class PerlinSimplexNoise {

    protected final NoiseGeneratorSimplexD[] noiseLevels;
    protected final double highestFreqValueFactor;

    public PerlinSimplexNoise(NukkitRandom random, int total) {
        this.noiseLevels = new NoiseGeneratorSimplexD[total];

        for (int i = 0; i < total; ++i) {
            this.noiseLevels[i] = new NoiseGeneratorSimplexD(random);
        }

        this.highestFreqValueFactor = 1 / (Math.pow(2, total) - 1);
    }

    public double getValue(double x, double y) {
        double result = 0;
        double frequency = 1;
        double amplitude = this.highestFreqValueFactor;

        for (NoiseGeneratorSimplexD simplex : this.noiseLevels) {
            result += simplex.getValue(x * frequency, y * frequency) * amplitude;
            frequency /= 2;
            amplitude *= 2;
        }

        return result;
    }
}
