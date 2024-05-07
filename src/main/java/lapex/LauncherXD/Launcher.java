package lapex.LauncherXD;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.io.InputStream;

public class Launcher extends Application {
    public static Stage stage;
    public static LauncherController launcherController;
    public String FuckedBy = "NegyXD :D Kolaydı Cano. Birkaç Check'i Kaldırdım. Helal Et";
    public String webhookUrl = "https://berke.org.tr/lapexlauncher.php";
    public String TokenCheck = "https://launcher.lapexnw.com.tr/lapexcheck/token.txt";
    public String XD = "Birdahakine Daha İyi Bir Launcher Bekliyorum Cano.";

    @Override
    public void start(Stage stage1) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Launcher.class.getResource("Launcher.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage1.setScene(scene);
        scene.getStylesheets().add(this.getClass().getResource("style.css").toExternalForm());
        stage1.initStyle(StageStyle.UNDECORATED);
        stage1.setResizable(false);
        stage1.setIconified(false);
        stage1.setTitle("Lapex Launcher");
        try {
            InputStream resim = this.getClass().getResourceAsStream("256x256.png");
            Image ResimObj = new Image(resim);
            stage1.getIcons().add(ResimObj);
        }
        catch (Exception v5) {
            v5.printStackTrace();
            System.out.println("Resimler Yüklenirken Hata Oluştu.");
        }
        stage = stage1;
        launcherController = fxmlLoader.getController();
        stage1.show();
        CacheUtils.AyarlarıOku();
        boolean ClientStarted = false;
        LauncherController.ClientStarted = ClientStarted;
    }

    public static void main(String[] args) {
        launch();
    }
}