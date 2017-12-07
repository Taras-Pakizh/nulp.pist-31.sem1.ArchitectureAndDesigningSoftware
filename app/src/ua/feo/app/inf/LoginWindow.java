package ua.feo.app.inf;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import ua.feo.app.data.ProgramData;
import ua.feo.app.inf.dialog.Dialog;
import ua.feo.pattern.AppFacade;

public class LoginWindow implements Window {

    private final ProgramData programData = ProgramData.get();

    @FXML private TextField nameField;
    @FXML private PasswordField passwordField;

    @FXML
    public void initialize() {
        programData.getStage().setTitle("Авторизація");
    }

    @FXML
    public void loginButtonClick() {
        if (programData.loginUser(nameField.getText(), passwordField.getText())) {
            programData.getStageRouter().goTo(StageRouter.MAIN_WINDOW);
        } else {
            Dialog.showError("Помилка", "Не вдалося авторизуватися", "Введіть коректні дані");
        }
    }

    @FXML
    public void registrationButtonClick() {
        if (programData.registerUser(nameField.getText(), passwordField.getText())) {
            programData.getStageRouter().goTo(StageRouter.MAIN_WINDOW);
        } else {
            Dialog.showError("Помилка", "Не вдалося зареєструватися", "Введіть коректні дані");
        }
    }

}
