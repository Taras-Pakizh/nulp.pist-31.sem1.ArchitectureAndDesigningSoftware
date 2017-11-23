package ua.feo.app;

import javafx.application.Application;
import javafx.stage.Stage;
import ua.feo.app.inf.StageRouter;
import ua.feo.app.inf.data.ProgramData;

public class Main extends Application {

    private final ProgramData programData;

    public Main() {
        this.programData = ProgramData.get();
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        programData.setStage(primaryStage);
        programData.getStage().setTitle("Авторизація");
        programData.getStageRouter().goTo(StageRouter.LOGIN_WINDOW);
    }
    public static void main(String[] args) {
        launch(args);
    }

}
