package lapex.LauncherXD;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class CacheUtils {
    private final boolean DosyalarMevcutmu;
    private final String kullaniciAdi;
    private final int BellekMiktari;
    public CacheUtils(boolean bl, String string, int n) {
        this.DosyalarMevcutmu = bl;
        this.kullaniciAdi = string;
        this.BellekMiktari = n;
    }
    public void AyarlariKaydet() {
        String kullaniciKlasoru = System.getProperty("user.home");
        String dosyaYolu = kullaniciKlasoru + File.separator + "AppData" + File.separator + "Roaming" + File.separator + ".lapex";
        File dosya = new File(dosyaYolu + File.separator + "cache.txt");

        if (!this.DosyalarMevcutmu || !dosya.exists()) {
            return;
        }

        try (FileWriter dosyaYazici = new FileWriter(dosya)) {
            dosyaYazici.append("username:").append(this.kullaniciAdi).append("\n");
            dosyaYazici.append("memory:").append(String.valueOf(this.BellekMiktari));
        } catch (IOException exception) {
            System.out.println(exception.getMessage());
        }
    }
    public static void AyarlarıOku() {
        String kullaniciKlasoru = System.getProperty("user.home");
        String dosyaYolu = kullaniciKlasoru + File.separator + "AppData" + File.separator + "Roaming" + File.separator + ".lapex";
        File dosya = new File(dosyaYolu + File.separator + "cache.txt");

        if (!dosya.exists()) {
            return;
        }

        try {
            String kullaniciAdi = null;
            int bellekKullanimi = 0;
            Scanner scanner = new Scanner(dosya);

            if (scanner.hasNextLine()) {
                String satir = scanner.nextLine();
                if (satir.startsWith("username:")) {
                    kullaniciAdi = satir.replace("username:", "");
                }
                if (satir.startsWith("memory:")) {
                    bellekKullanimi = Integer.parseInt(satir.replace("memory:", ""));
                }
            }
        } catch (FileNotFoundException exception) {
            System.err.println("Dosya bulunamadı: " + exception.getMessage());
        } catch (NumberFormatException exception) {
            System.err.println("Bellek kullanımı bilgisi geçersiz: " + exception.getMessage());
        }
    }
}
