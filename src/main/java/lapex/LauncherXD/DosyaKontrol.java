package lapex.LauncherXD;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DosyaKontrol {
    private final File Dosya;

    public DosyaKontrol(File file) {
        this.Dosya = file;
    }
    public String getHash() {
        try {
            return this.HashDegeriniHesapla(this.Dosya.getPath());
        }
        catch (Exception exception) {
            return null;
        }
    }
    private String HashDegeriniHesapla(String filePath) throws IOException, NoSuchAlgorithmException {
        Path path = Paths.get(filePath);
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = messageDigest.digest(Files.readAllBytes(path));
        StringBuilder stringBuilder = new StringBuilder();
        for (byte b : hashBytes) {
            String hexString = Integer.toHexString(0xFF & b);
            if (hexString.length() == 1) {
                stringBuilder.append('0');
            }
            stringBuilder.append(hexString);
        }
        return stringBuilder.toString();
    }

}
