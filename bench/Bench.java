import opendota.Parse;

import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public class Bench {
    public static void main(String[] args) throws Exception {
        Path replay = Path.of(args[0]);
        int warmup = args.length > 1 ? Integer.parseInt(args[1]) : 1;
        int runs   = args.length > 2 ? Integer.parseInt(args[2]) : 5;

        byte[] bytes = Files.readAllBytes(replay);
        OutputStream sink = OutputStream.nullOutputStream();

        System.out.printf("replay=%s size=%.1fMB warmup=%d runs=%d%n",
                replay.getFileName(), bytes.length / 1048576.0, warmup, runs);

        for (int i = 0; i < warmup; i++) {
            long t = System.nanoTime();
            try { new Parse(new ByteArrayInputStream(bytes), sink, false); }
            catch (Throwable th) { System.out.printf("warmup %d THREW after %.3fs: %s%n",
                    i, (System.nanoTime()-t)/1e9, th); continue; }
            System.out.printf("warmup %d: %.3fs%n", i, (System.nanoTime()-t)/1e9);
        }

        double[] secs = new double[runs];
        for (int i = 0; i < runs; i++) {
            long t = System.nanoTime();
            new Parse(new ByteArrayInputStream(bytes), sink, false);
            secs[i] = (System.nanoTime()-t)/1e9;
            System.out.printf("run %d: %.3fs%n", i, secs[i]);
        }

        double[] sorted = secs.clone();
        Arrays.sort(sorted);
        double median = runs % 2 == 1 ? sorted[runs/2] : (sorted[runs/2-1]+sorted[runs/2])/2;
        double min = sorted[0], max = sorted[runs-1];
        double mean = 0; for (double s : secs) mean += s; mean /= runs;
        System.out.printf("median=%.3fs min=%.3fs max=%.3fs mean=%.3fs spread=%.1f%%%n",
                median, min, max, mean, (max-min)/median*100);
    }
}