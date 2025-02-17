package space.guild.testing;

import de.flapdoodle.embed.process.io.directories.PersistentDir;
import de.flapdoodle.reverse.Transition;
import de.flapdoodle.reverse.transitions.Start;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class EmbeddedMongoHelper {

    public static final String FIXED_PATH = "C:\\Olivier\\Dev\\emmo";

    public static Transition<PersistentDir> provideBaseDir() {
        var exists = Path.of(FIXED_PATH, "fileSets").toFile().exists();
        if (!exists) {
            unzipWinMongoOnFixedPath();
        }
        return Start.to(PersistentDir.class)
                .providedBy(() -> PersistentDir.of(Path.of(FIXED_PATH)));
    }

    private static void unzipWinMongoOnFixedPath() {
        try (ZipInputStream zipIn = new ZipInputStream(EmbeddedMongoHelper.class.getClassLoader().getResourceAsStream("embedded-mongo/archives.zip"))) {
            ZipEntry entry;
            while ((entry = zipIn.getNextEntry()) != null) {
                File filePath = new File(Path.of(FIXED_PATH).toFile(), entry.getName());
                if (entry.isDirectory()) {
                    filePath.mkdirs();
                } else {
                    filePath.getParentFile().mkdirs();
                    try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath))) {
                        byte[] bytesIn = new byte[4096];
                        int read;
                        while ((read = zipIn.read(bytesIn)) != -1) {
                            bos.write(bytesIn, 0, read);
                        }
                    }
                }
                zipIn.closeEntry();
            }
        } catch (IOException ioe) {
            throw new IllegalStateException("Unzipping encountered a problem", ioe);
        }
    }

}
