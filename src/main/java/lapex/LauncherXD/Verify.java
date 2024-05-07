package lapex.LauncherXD;

import javafx.application.Platform;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

public class Verify {
    public boolean KontrolDurumu = false;
    public static int b;

    public boolean checkLapexFiles() {
        String userHome = System.getProperty("user.home");
        String lapexFolderPath = userHome + File.separator + "AppData" + File.separator + "Roaming" + File.separator + ".lapex";
        Map<String, String> fileMap = ReceiveClientData(lapexFolderPath);
        if (fileMap.size() == 794) {
            KontrolDurumu = true;
        }
        if (fileMap == null) {
            return false;
        }
        if (fileMap.isEmpty()) {
            return false;
        }
        return dosyalariKontrolEt(lapexFolderPath, fileMap);
    }


    private boolean checkAssets(String folderPath) {
        boolean result = false;
        String assetsPath = folderPath + File.separator + "assets" + File.separator;
        try {
            String hashValue = HashCalculator.calculateFileHash(assetsPath);
            if (hashValue.equals("34c2f3db20ff7ee98a214531e77fb6d3d2266fe59b36a94a9082e126398d631c")) {
                result = true;
            }
        } catch (Exception exception) {

        }
        return result;
    }

    public boolean dosyalariKontrolEt(String anaKlasor, Map<String, String> dosyaBilgileri) {
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        ArrayList<Future<Boolean>> futureListesi = new ArrayList<>();

        for (String dosyaAdi : dosyaBilgileri.keySet()) {
            String dosyaYolu = anaKlasor + File.separator + dosyaAdi;
            double ilerlemeOrani = this.KontrolDurumu ? 0.0015 : 0.04;

            if (!this.KontrolDurumu && b != 0) {
                ilerlemeOrani = 0.04;
            }

            File dosya = new File(dosyaYolu);
            if (dosya.exists() &&new DosyaKontrol(dosya).getHash().equals(dosyaBilgileri.get(dosyaAdi)) && b == 0) {
                continue;
            }

            Platform.runLater(() -> YaziyiGuncelle(dosyaAdi));

            Callable<Boolean> callable = () -> {
                return DosyalarıKontrolEt(dosyaAdi, dosyaYolu);
            };

            Future<Boolean> future = executorService.submit(callable);
            futureListesi.add(future);

            if (b != 0) {
                continue;
            }
        }

        for (Future<Boolean> future : futureListesi) {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException exception) {
                exception.printStackTrace();
            }
        }

        executorService.shutdown();
        try {
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
        }

        if (KontrolDurumu && DegerDondur()) {
            DurumuYazdir();
            return true;
        }

        return false;
    }
    private static void YaziyiGuncelle(String string) {
        Launcher.launcherController.DosyalarYukleniyor.setText(string);
    }
    private boolean DegerDondur() {
        return true;
    }
    private void DurumuYazdir() {
        System.out.println("Client başlatılıyor...");
    }
    private Boolean DosyalarıKontrolEt(String string, String string2) throws Exception {
        return this.DosyaCheck(string, string2);
    }
    private boolean DosyaCheck(String urlString, String destinationPath) {
        try {
            urlString = urlString.replace("\\", "/");
            URL url = new URL("https://launcher.lapexnw.com.tr/lapexcheck/" + urlString);
            try (InputStream inputStream = url.openStream()) {
                Path path = Paths.get(destinationPath);
                Files.createDirectories(path.getParent());
                Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
                return true;
            }
        } catch (IOException e) {
            return false;
        }
    }
    private Map<String, String> ReceiveClientData(String directoryPath) {
        return ReceiveClientData(directoryPath, false);
    }
    private Map<String, String> ReceiveClientData(String directoryPath, boolean ekVeri) {
        Map<String, String> dataMap = new HashMap<>();
        String clientDataUrl = "https://launcher.lapexnw.com.tr/lapexcheck/client.txt";
        String assetsDataUrl = "https://launcher.lapexnw.com.tr/lapexcheck/assets.txt";

        try {
            URL clientUrl = new URL(clientDataUrl);
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientUrl.openStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length >= 2) {
                    String key = parts[0];
                    String value = parts[1];
                    dataMap.put(key, value);
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        if (ekVeri) {
            System.out.println("Ek veriler bekleniyor...");
            try {
                URL assetsUrl = new URL(assetsDataUrl);
                BufferedReader reader = new BufferedReader(new InputStreamReader(assetsUrl.openStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(":");
                    if (parts.length >= 2) {
                        String key = parts[0];
                        String value = parts[1];
                        dataMap.put(key, value);
                    }
                }
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        return dataMap;
    }



}
