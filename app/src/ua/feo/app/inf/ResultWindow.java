package ua.feo.app.inf;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.paint.Color;
import javafx.util.Pair;
import ua.feo.app.inf.data.ProgramData;

import java.util.List;
import java.util.Map;

public class ResultWindow {

    private final ProgramData programData = ProgramData.get();

    @FXML private Label titleLabel;
    @FXML private Label resultLabel;
    @FXML private ListView<String> testCaseListView;
    @FXML private Button mainButton;
    @FXML private Button repeatButton;
    @FXML private Button nextButton;

    private static final ObservableList<String> testCaseListViewData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        programData.getStage().setTitle("Результати");
        testCaseListViewData.clear();
        titleLabel.setText(programData.getCurrentTarget().getName());
        resultLabel.setText("Завдання виконано на : " + programData.getResult() + " %");
        testCaseListView.setItems(testCaseListViewData);
        testCaseListView.setEditable(false);
        testCaseListViewData.addAll(programData.getCurrentTarget().getTestCaseList());
        Pair<Map<String, Boolean>, Map<String, Boolean>> testData = programData.getTestData();
        testCaseListView.setCellFactory(listView -> {
            CheckBoxListCell<String> cell = new CheckBoxListCell<String>() {

                @Override
                public void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setDisable(true);
                    Boolean etalon = testData.getKey().get(item);
                    Boolean data = testData.getValue().get(item);
                    if (etalon != null && data != null && getTextFill() != Color.GREEN && getTextFill() != Color.RED) {
                        if (etalon == data) {
                            setTextFill(Color.GREEN);
                        } else {
                            setTextFill(Color.RED);
                        }
                    }

                }
            };
            cell.setSelectedStateCallback((String item) -> new SimpleBooleanProperty(testData.getValue().get(item)));
            return cell;
        });
        nextButton.setDisable(!programData.hasNextTarget() || programData.getCurrentUser().targetIsDisabled(programData.getTargetList().get(programData.getIndexCurrentTarget() + 1)));
    }

    @FXML
    public void mainButtonClick() {
        programData.getStageRouter().goTo(StageRouter.MAIN_WINDOW);
    }

    @FXML
    public void repeatButtonClick() {
        programData.getStageRouter().goTo(StageRouter.TASK_WINDOW);
    }

    @FXML
    public void nextButtonClick() {
        programData.setNextCurrentTarget();
        programData.getStageRouter().goTo(StageRouter.TASK_WINDOW);
    }

}
