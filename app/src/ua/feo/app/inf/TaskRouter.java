package ua.feo.app.inf;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ua.feo.app.task.TargetInf;

import java.awt.*;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class TaskRouter implements RouterInf, IteratorInf<TargetInf> {

    public static final String HIDE = "";

    private final Stage stage;

    public TaskRouter(Stage stage) {
        this.stage = stage;
    }

    @Override
    public TargetInf goTo(String resource) {
        if (resource.equals(HIDE)) {
            stage.hide();
            return null;
        }
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getClassLoader().getResource("fxml/task/Target" + (Integer.parseInt(resource) + 1) + ".fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage.setResizable(false);
            stage.setScene(scene);
            Dimension screenDimension = Toolkit.getDefaultToolkit().getScreenSize();
            stage.setX(screenDimension.getWidth() / 2 - scene.getWidth() / 2);
            stage.setY(screenDimension.getHeight() / 2 - scene.getHeight() / 2);
            stage.show();
            return loader.getController();
        } catch (Exception ex){
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public TargetInf getController(String resource) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getClassLoader().getResource("fxml/task/TaskWindow" + resource + ".fxml"));
            return loader.getController();
        } catch (Exception ex){
            ex.printStackTrace();
            return null;
        }
    }

    private int currentTarget = 1;

    @Override
    public TargetInf reset() {
        currentTarget = 1;
        return goTo(String.valueOf(currentTarget));
    }

    @Override
    public TargetInf next() {
        currentTarget++;
        return goTo(String.valueOf(currentTarget));
    }

    @Override
    public boolean hasNext() {
        currentTarget++;
        return getController(String.valueOf(currentTarget)) != null;
    }

    @Override
    public TargetInf current() {
        return goTo(String.valueOf(currentTarget));
    }

}
