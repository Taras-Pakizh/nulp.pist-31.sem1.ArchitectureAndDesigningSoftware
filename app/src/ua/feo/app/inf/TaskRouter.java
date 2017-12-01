package ua.feo.app.inf;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ua.feo.app.task.TargetInf;

import java.awt.*;

public class TaskRouter {

    public static final int HIDE = Integer.MIN_VALUE;

    private final Stage stage;

    public TaskRouter(Stage stage) {
        this.stage = stage;
    }

    public void goTo(int resource) {
        if (resource == HIDE) {
            stage.hide();
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getClassLoader().getResource("fxml/task/Target" + (resource + 1) + ".fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage.setResizable(false);
            stage.setScene(scene);
            Dimension screenDimension = Toolkit.getDefaultToolkit().getScreenSize();
            stage.setX(screenDimension.getWidth() / 2 - scene.getWidth() / 2);
            stage.setY(screenDimension.getHeight() / 2 - scene.getHeight() / 2);
            stage.show();
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public TargetInf getController(int resource) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getClassLoader().getResource("fxml/task/TaskWindow" + resource + ".fxml"));
            return loader.getController();
        } catch (Exception ex){
            ex.printStackTrace();
            return null;
        }
    }
}
