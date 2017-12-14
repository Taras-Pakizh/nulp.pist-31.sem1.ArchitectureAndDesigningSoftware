package ua.feo.app.inf;

import javafx.beans.NamedArg;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.PickResult;
import javafx.scene.paint.Color;
import ua.feo.app.data.ProgramData;
import ua.feo.app.data.User;
import ua.feo.app.inf.dialog.Dialog;
import ua.feo.app.task.TargetInf;

import java.util.List;

public class MainWindow implements Window {

    private final ProgramData programData = ProgramData.get();

    @FXML private ListView<String> taskListView;
    @FXML private Label currentUserField;
    @FXML private Label disabledTaskField;
    @FXML private Label enabledTaskField;
    @FXML private Label completedTaskField;
    @FXML private Label failedTaskField;
    @FXML private Label targetDataField;

    private static final ObservableList<String> taskListViewData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        programData.getStage().setTitle("Система тестування ПЗ");
        taskListViewData.clear();
        List<TargetInf> targetList = programData.getTargetList();
        taskListView.setCellFactory((ListView<String> list) -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(item);
                TargetInf target = targetList.stream().filter(t -> t.getName().equals(item)).findFirst().orElse(null);
                if (target != null) {
                    if (programData.getCurrentUser().targetIsFailed(target)) setTextFill(Color.RED);
                    else if (programData.getCurrentUser().targetIsCompleted(target)) setTextFill(Color.GREEN);
                    else if (programData.getCurrentUser().targetIsDisabled(target)) setTextFill(Color.GRAY);
                }
            }
        });
        taskListView.setItems(taskListViewData);
        taskListView.setEditable(true);
        taskListView.getSelectionModel().selectFirst();
        taskListItemClick();
        taskListViewData.addAll(programData.getTargetNameList());
        User user = programData.getCurrentUser();
        currentUserField.setText(user.name);
        disabledTaskField.setText(targetList.stream().filter(user::targetIsDisabled).count() + "/" + targetList.size());
        enabledTaskField.setText(targetList.stream().filter(user::targetIsEnabled).count() + "/" + targetList.size());
        completedTaskField.setText(targetList.stream().filter(user::targetIsCompleted).count() + "/" + targetList.size());
        failedTaskField.setText(targetList.stream().filter(user::targetIsFailed).count() + "/" + targetList.size());
    }

    @FXML
    public void exitButtonClick() {
        programData.getStageRouter().goTo(StageRouter.LOGIN_WINDOW);
    }

    @FXML
    public void taskListItemClick(MouseEvent event) {
        switch (event.getClickCount()) {
            case 1: {
                taskListItemClick();
            }break;
            case 2: {
                taskListItemDoubleClick();
            }break;
        }
    }

    public void taskListItemClick() {
        int currentItemIndex = taskListView.getSelectionModel().getSelectedIndex();
        if (currentItemIndex == -1) currentItemIndex = 0;
        TargetInf target = programData.getTargetList().get(currentItemIndex);
        String targetName = target.getName();
        List<String> testCaseList = target.getTestCaseList();
        StringBuilder buffer = new StringBuilder();
        buffer.append(" --- ").append(targetName).append(" --- \n\n");
        testCaseList.forEach(tc -> buffer.append("- ").append(tc).append("\n"));
        targetDataField.setText(buffer.toString());
    }

    public void taskListItemDoubleClick() {
        int currentItemIndex = taskListView.getSelectionModel().getSelectedIndex();
        if (currentItemIndex == -1) currentItemIndex = 0;
        programData.setCurrentTarget(currentItemIndex);
        if (!programData.getCurrentUser().targetIsDisabled(programData.getCurrentTarget())) {
            programData.getStageRouter().goTo(StageRouter.TASK_WINDOW);
        } else {
            Dialog.showError("Помилка", "Не вдалося відкрити завдання", "Завдання поки ще не доступне");
        }
    }

}
