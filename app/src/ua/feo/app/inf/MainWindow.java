package ua.feo.app.inf;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import ua.feo.app.data.ProgramData;
import ua.feo.app.inf.dialog.Dialog;
import ua.feo.app.task.TargetInf;

import java.util.List;

public class MainWindow implements Window {

    private final ProgramData programData = ProgramData.get();

    @FXML private ListView<String> taskListView;

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
        taskListViewData.addAll(programData.getTargetNameList());
    }

    @FXML
    public void exitButtonClick() {
        programData.getStageRouter().goTo(StageRouter.LOGIN_WINDOW);
    }

    @FXML
    public void taskListItemClick(MouseEvent event) {
        if (event.getClickCount() == 2) {
            int currentItemIndex = taskListView.getSelectionModel().getSelectedIndex();
            programData.setCurrentTarget(currentItemIndex);
            if (!programData.getCurrentUser().targetIsDisabled(programData.getCurrentTarget())) {
                programData.getStageRouter().goTo(StageRouter.TASK_WINDOW);
            } else {
                Dialog.showError("Помилка", "Не вдалося відкрити завдання", "Завдання поки ще не доступне");
            }
        }
    }

}
