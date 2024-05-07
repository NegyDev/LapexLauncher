module lapexlauncher.lapexlauncher {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;

    opens lapexlauncher.lapexlauncher to javafx.fxml;
    exports lapex.LauncherXD;
    opens lapex.LauncherXD to javafx.fxml;
}