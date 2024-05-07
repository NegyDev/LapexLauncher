package lapex.LauncherXD;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashCalculator {
    public static String calculateFileHash(String filePath) throws IOException, NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        updateDigestSets(filePath, messageDigest);
        byte[] hashBytes = messageDigest.digest();
        StringBuilder hashBuilder = new StringBuilder();
        for (byte hashByte : hashBytes) {
            String hexString = Integer.toHexString(0xFF & hashByte);
            if (hexString.length() == 1) {
                hashBuilder.append('0');
            }
            hashBuilder.append(hexString);
        }
        return hashBuilder.toString();
    }
    private static void updateDigestSets(String directoryPath, MessageDigest messageDigest) throws IOException {
        DirectoryStream<Path> directoryStream = null;
        try {
            directoryStream = Files.newDirectoryStream(Paths.get(directoryPath));
            for (Path path : directoryStream) {
                if (Files.isRegularFile(path)) {
                    updateFileDigest(path, messageDigest);
                } else if (Files.isDirectory(path)) {
                    updateDigestSets(path.toString(), messageDigest);
                }
            }
        } finally {
            if (directoryStream != null) {
                directoryStream.close();
            }
        }
    }

    private static void updateFileDigest(Path filePath, MessageDigest messageDigest) throws IOException {
        try (InputStream inputStream = Files.newInputStream(filePath)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                messageDigest.update(buffer, 0, bytesRead);
            }
        }
    }

}
