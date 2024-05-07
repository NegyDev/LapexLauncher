package lapex.LauncherXD;
import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

public class LauncherController implements Initializable{
    @FXML
    public Slider RamMiktariSlider;
    @FXML
    public TextField KullaniciAdi;
    @FXML
    public RadioButton BeniHatirla;
    @FXML
    public Button Oyna;
    @FXML
    public Button LauncherKapat;
    @FXML
    public Text KullaniciAdiGirmelisin;
    @FXML
    public Text HataOlustu;
    @FXML
    public Pane LauncherPanel;
    @FXML
    public AnchorPane LauncherMenu;
    public ImageView Resim;
    public ProgressBar ilerleme;
    @FXML
    private Text RamMiktarı;
    @FXML
    public Text DosyalarYukleniyor;
    boolean ClientFilesChecked = false;
    public static boolean ClientStarted;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.LauncherPanel.setVisible(false);
        this.HataOlustu.setVisible(false);
        this.KullaniciAdiGirmelisin.setVisible(false);
        this.Resim.setVisible(false);
        this.RamMiktariSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                SliderDegerDegistir(observable,oldValue,newValue);
            }
        });
        this.LauncherKapat.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                LauncherKapat(event);
            }
        });
        this.Oyna.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                OyunuBaslat(event);
            }
        });
    }
    private void OyunuBaslat(ActionEvent actionEvent) {
        new Thread(this::StartGame).start();
    }
    private String[] getClientArguments() {
        String javaPath = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java.exe";
        int memorySizeGB = Double.valueOf(this.RamMiktariSlider.getValue()).intValue();
        String maxMemory = "-Xmx" + memorySizeGB + "G";
        String initialMemory = "-Xms" + memorySizeGB + "G";
        String lapexPath = System.getProperty("user.home") + File.separator + "AppData" + File.separator + "Roaming" + File.separator + ".lapex";
        String librariesPath = lapexPath + File.separator + "libraries" + File.separator + "natives";
        String minecraftJarPath = lapexPath + File.separator + "versions" + File.separator + "Lapex" + File.separator + "Lapex.jar";
        String[] launchCommand = new String[26];
        launchCommand[0] = javaPath;
        launchCommand[1] = maxMemory;
        launchCommand[2] = initialMemory;
        launchCommand[3] = "-noverify";
        launchCommand[4] = "-Djava.library.path=" + librariesPath;
        launchCommand[5] = "-cp";
        launchCommand[6] = librariesPath + "/*;" + minecraftJarPath;
        launchCommand[7] = "net.minecraft.client.main.Main";
        launchCommand[8] = "--username";
        launchCommand[9] = this.KullaniciAdi.getText();
        launchCommand[10] = "--version";
        launchCommand[11] = "Lapex";
        launchCommand[12] = "--gameDir";
        launchCommand[13] = lapexPath + File.separator + "game-directories" + File.separator + "public";
        launchCommand[14] = "--assetsDir";
        launchCommand[15] = lapexPath + File.separator + "assets";
        launchCommand[16] = "--assetIndex";
        launchCommand[17] = "1.8";
        launchCommand[18] = "--uuid";
        launchCommand[19] = "129edd96-a8bb-37e5-a99d-02d8c893bcdd";
        launchCommand[20] = "--accessToken";
        launchCommand[21] = "00000000000000054210000000000000";
        launchCommand[22] = "--userProperties";
        launchCommand[23] = "{}";
        launchCommand[24] = "--userType";
        launchCommand[25] = "mojang";

        return launchCommand;
    }

    private void StartGame() {
        this.KullaniciAdi.setEditable(false);
        boolean Started = ClientStarted;
        if (this.KullaniciAdi.getText().length() < 3) {
            this.LauncherPanel.setVisible(true);
            this.HataOlustu.setVisible(true);
            this.KullaniciAdiGirmelisin.setVisible(true);
            new Thread(()->KullaniciHatasi());
            this.KullaniciAdi.setEditable(true);
            return;
        }
        if (this.ClientFilesChecked) {
            return;
        }
        this.ClientFilesChecked = true;
        new CacheUtils(this.BeniHatirla.isSelected(), this.KullaniciAdi.getText(), Double.valueOf(this.RamMiktariSlider.getValue()).intValue());
        this.Resim.setVisible(true);
        this.Oyna.setVisible(false);
        System.out.println("Verify başlatılıyor...");
        Verify verifier = new Verify();
        boolean checkedLapexFiles = verifier.checkLapexFiles();
        if (!checkedLapexFiles) {
            System.out.println("Verify Hata aldı!");
        }
        System.out.println("Verify sona geldi!");
        this.ClientFilesChecked = false;
        if (!checkedLapexFiles) {
            this.LauncherPanel.setVisible(true);
            this.HataOlustu.setVisible(true);
            this.KullaniciAdiGirmelisin.setText("Sunucuya Bağlanılamadı!");
            this.KullaniciAdiGirmelisin.setVisible(true);
            Platform.runLater(new Thread(LauncherController::SunucuBaglantiBasarisiz));
            return;
        }
        String[] stringArray = this.getClientArguments();
        Platform.runLater(new Thread(LauncherController::StageTamamlandi));
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(stringArray);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            InputStream inputStream = process.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            boolean eofExceptionOccurred = false;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.contains("java.io.EOFException") && !eofExceptionOccurred) {
                    eofExceptionOccurred = true;
                    if (checkedLapexFiles) {
                        new Thread(() -> {KütüphaneKontrol(process); }, "Library Controller Thread").start();
                    }
                }
            }
            bufferedReader.close();
            inputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private static void KütüphaneKontrol(Process process) {
        FileChecker c2 = new FileChecker();
        if (c2.Farklı) {
            process.destroy();
        }
    }
    private static void SunucuBaglantiBasarisiz() {
        try {
            Thread.sleep(2000L);
            System.exit(1);
        }
        catch (Exception exception) {
           exception.printStackTrace();
        }
    }
    public static void StageTamamlandi() {
        Launcher.stage.close();
    }
    private static void LauncherKapat(ActionEvent actionEvent) {
        Launcher.stage.close();
        System.exit(1);
    }
    private void KullaniciHatasi() {
        try {
            Thread.sleep(3000L);
            this.LauncherPanel.setVisible(false);
            this.HataOlustu.setVisible(false);
            this.KullaniciAdiGirmelisin.setVisible(false);
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void SliderDegerDegistir(ObservableValue observableValue, Number number, Number number2) {
        this.RamMiktarı.setText("." + number2.intValue() + "GB.");
    }

}