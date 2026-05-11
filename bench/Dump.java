import opendota.Parse;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class Dump {
    public static void main(String[] args) throws Exception {
        Path replay = Path.of(args[0]);
        Path out    = Path.of(args[1]);
        byte[] bytes = Files.readAllBytes(replay);
        try (OutputStream os = new FileOutputStream(out.toFile())) {
            long t = System.nanoTime();
            new Parse(new ByteArrayInputStream(bytes), os, false);
            System.out.printf("parsed in %.3fs, wrote %s%n",
                    (System.nanoTime()-t)/1e9, out);
        }
    }
}
