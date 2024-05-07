package lapex.LauncherXD;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;

public class FileChecker {
    public boolean Farklı = false;
    private static final String[] URLS_CLIENT = {"https://launcher.lapexnw.com.tr/lapexcheck/client.txt"};
    private static final String USER_HOME = System.getProperty("user.home");
    public String UfakBirStr = "Oruspu Evladı Adam Gibi Check Yap Birdahakine Sikmiyeyim Seni";

    public FileChecker() {
        HashMap<String, String> fileDataMap = new HashMap<>();
        try {
            readDataFromURL(URLS_CLIENT[0], fileDataMap);
        } catch (IOException e) {
        }

        String lapexFolder = USER_HOME + File.separator + "AppData" + File.separator + "Roaming" + File.separator + ".lapex";
        fileDataMap.forEach((fileName, expectedHash) -> checkFile(lapexFolder, fileName, expectedHash));
    }

    private void readDataFromURL(String urlString, HashMap<String, String> dataMap) throws IOException {
        URL url = new URL(urlString);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length >= 2) {
                    String fileName = parts[0];
                    String hash = parts[1];
                    dataMap.put(fileName, hash);
                }
            }
        }
    }

    private void checkFile(String folderPath, String fileName, String expectedHash) {
        String filePath = folderPath + File.separator + fileName;
        File file = new File(filePath);
        String fileHash = new DosyaKontrol(file).getHash();

        if (!fileHash.equals(expectedHash)) {
            Farklı = true;
        }
    }
}
