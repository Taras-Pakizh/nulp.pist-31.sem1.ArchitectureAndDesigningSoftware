package ua.feo.app;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import ua.feo.app.data.Serializator;
import ua.feo.app.inf.StageRouter;
import ua.feo.app.data.ProgramData;

public class Main extends Application {

    private final ProgramData programData;

    public Main() {
        this.programData = ProgramData.get();
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setOnCloseRequest(event -> Serializator.save(programData));
        programData.setStage(primaryStage);
        programData.getStageRouter().goTo(StageRouter.LOGIN_WINDOW);
    }
    public static void main(String[] args) {
        launch(args);
    }

}
