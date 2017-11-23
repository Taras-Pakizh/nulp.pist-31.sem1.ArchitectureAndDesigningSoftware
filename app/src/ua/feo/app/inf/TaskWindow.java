package ua.feo.app.inf;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.util.Callback;
import javafx.util.Pair;
import ua.feo.app.inf.data.ProgramData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

public class TaskWindow {

    private final ProgramData programData = ProgramData.get();

    @FXML private ListView<String> testCaseListView;

    private static final ObservableList<String> testCaseListViewData = FXCollections.observableArrayList();
    private Map<String, Boolean> testCaseListViewResult = new HashMap<>();

    @FXML
    public void initialize() {
        programData.getStage().setTitle(programData.getCurrentTarget().getName());
        testCaseListViewData.clear();
        testCaseListView.setItems(testCaseListViewData);
        testCaseListViewData.addAll(programData.getCurrentTarget().getTestCaseList());
        testCaseListViewData.forEach(s -> testCaseListViewResult.put(s, false));
        testCaseListView.setCellFactory(CheckBoxListCell.forListView(item -> {
            BooleanProperty observable = new SimpleBooleanProperty();
            observable.addListener((obs, wasSelected, isNowSelected) -> testCaseListViewResult.put(item, !testCaseListViewResult.get(item)));
            return observable;
        }));
    }

    @FXML
    public void exitButtonClick() {
        programData.getTaskRouter().goTo(TaskRouter.HIDE);
        programData.getStageRouter().goTo(StageRouter.MAIN_WINDOW);
    }

    @FXML
    public void endButtonClick() {
        programData.setTestData(testCaseListViewResult);
        programData.getTaskRouter().goTo(TaskRouter.HIDE);
        programData.getStageRouter().goTo(StageRouter.RESULT_WINDOW);
    }

    @FXML
    public void reloadButtonClick() {
        programData.getTaskRouter().goTo(TaskRouter.HIDE);
        programData.getStageRouter().goTo(StageRouter.TASK_WINDOW);
    }

    @FXML
    public void runButtonClick() {
        programData.getTaskRouter().goTo(programData.getIndexCurrentTarget());
    }

}
