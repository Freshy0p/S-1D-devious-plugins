package net.unethicalite.tempoross;

import java.util.Random;

public class S1dUtils
{
    // Calculate click delay based on min, max, target delay, and deviation.
    public static int calculateClickDelay(int min, int max, int target, int deviation)
    {
        Random rand = new Random();
        // Generate a Gaussian-distributed value centered around 0 with a standard deviation of 1
        double gaussianValue = rand.nextGaussian();

        // Scale and shift the Gaussian value to have a mean of `target` and a standard deviation of `deviation / 3`
        // The division by 3 makes it more likely that the value falls within one standard deviation from the mean
        int delay = (int) (target + gaussianValue * (deviation / 3));

        // Ensure the delay is within the specified bounds [min, max]
        if (delay < min) delay = min;
        if (delay > max) delay = max;

        return delay;
    }
}
