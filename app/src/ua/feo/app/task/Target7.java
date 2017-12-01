package ua.feo.app.task;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class Target7 implements TargetInf {

    private final static HashMap<String, Boolean> data;

    static {
        data = new HashMap<String, Boolean>(){{
            put("тесткейс 1", false);
            put("тесткейс 2", true);
            put("тесткейс 3", true);
            put("тесткейс 4", true);
            put("тесткейс 5", false);
        }};
    }

    @Override
    public String getName() {
        return "Наближене розв’язування задачі Коші для звичайних  диференціальних рівнянь";
    }

    @Override
    public List<String> getTestCaseList() {
        return new ArrayList<>(data.keySet());
    }

    @Override
    public Map<String, Boolean> getEtalonTestCaseList() {
        return data;
    }

    @FXML private ComboBox<String> method;
    @FXML private TextField beginPointField;
    @FXML private TextField endPointField;
    @FXML private TextField stepField;
    @FXML private TextField y0Field;
    @FXML private TextArea logText;
    @FXML private AnchorPane chartPane;
    @FXML private Label resultLabel;
    @FXML private TableView<TableRow> tableView;
    @FXML private TableColumn<TableRow, String> iColumn;
    @FXML private TableColumn<TableRow, String> xColumn;
    @FXML private TableColumn<TableRow, String> yColumn;

    private ObservableList<TableRow> table = FXCollections.observableArrayList();
    private AreaChart<Double, Double> chart;
    final private Function function = new MyFunction();

    @FXML
    private void initialize() {
        iColumn.setCellValueFactory(cellData -> cellData.getValue().iProperty());
        xColumn.setCellValueFactory(cellData -> cellData.getValue().xProperty());
        yColumn.setCellValueFactory(cellData -> cellData.getValue().yProperty());

        tableView.setItems(table);
        chart = new AreaChart(new NumberAxis(), new NumberAxis());
        chart.setLegendVisible(false);
        chart.setCreateSymbols(false);
        chartPane.getChildren().add(chart);
        this.method.setItems(FXCollections.observableArrayList(
                "Метод Ейлера",
                "Метод Рунге-Кутта",
                "Метод Адамса N=1",
                "Метод Адамса N=2",
                "Метод Адамса N=3"
        ));
        clearFields();
    }

    private void clearFields() {
        method.getSelectionModel().selectFirst();
        beginPointField.setText("1.7");
        endPointField.setText("2.7");
        stepField.setText("0.01");
        y0Field.setText("5.3");
    }

    @FXML
    public void onAction() {
        clearLog();
        try {
            double beginPoint = Double.parseDouble(beginPointField.getText());
            double endPoint = Double.parseDouble(endPointField.getText());
            double step = Double.parseDouble(stepField.getText());
            double y0 = Double.parseDouble(y0Field.getText());
            Method method = new Method(function, beginPoint, endPoint, step, y0);
            this.logText.appendText(method.work(this.method.getSelectionModel().getSelectedItem()));
            chart.getData().add(new XYChart.Series<Double, Double>(FXCollections.observableArrayList(
                    method.getGraphicPoints()
            )));
            method.setPointsInList(table);
            resultLabel.setText("Розв'язок задачі Коші: " + method.getResult());
        } catch (Exception ex) {
            ex.printStackTrace();
            this.logText.appendText("Error 1: некоректні дані. поле не може бути порожнім.\n");
        }
    }

    @FXML
    public void onActionComboBox() {
        boolean flag;
        switch (method.getSelectionModel().getSelectedItem()) {
            case "Метод Ейлера":
            case "Метод Адамса N=1":
            case "Метод Адамса N=2":
            case "Метод Адамса N=3":
                flag = true;
                break;
            case "Метод Рунге-Кутта":
                flag = false;
                break;
            default:
                flag = true;
                break;
        }
        stepField.setEditable(flag);
        stepField.setOpacity(flag ? 1 : 0.5);
    }

    @FXML
    public void clearLog() {
        this.logText.setText("");
    }

    @FXML
    public void clearGraphic() {
        this.chart.getData().removeAll(this.chart.getData());
    }

    interface Function {

        double function(double x, double y);

    }

    class Method {

        private Function function;
        private double beginPoint;
        private double endPoint;
        private double step;
        private double y0;

        private List<XYChart.Data> graphicPoints = new ArrayList<>();
        private double result;

        public Method(Function function, double beginPoint, double endPoint, double step, double y0) {
            this.function = function;
            this.beginPoint = beginPoint;
            this.endPoint = endPoint;
            this.step = step;
            this.y0 = y0;
        }

        public String work(String item) {
            switch (item) {
                case "Метод Ейлера":
                    return methodEilers ();
                case "Метод Рунге-Кутта":
                    return methodRungeKutts();
                case "Метод Адамса N=1":
                    return methodAdamss1();
                case "Метод Адамса N=2":
                    return methodAdamss2();
                case "Метод Адамса N=3":
                    return methodAdamss3();
                default:
                    return "Error 0: вибраний метод не підтримується.";
            }
        }

        private String methodEilers () {
            String str = "---------- Метод Ейлера ----------\n";
            double y = y0;
            double x = beginPoint;
            while (x <= endPoint) {
                y = y + step * function.function(x, y);
                str += "--- ітерація ---\n\tx = " + x + "\n\ty = " + y + "\n\tdelta = " + (step * y) + "\n\tточне y = " + Math.sqrt(2 * x + 1) + "\n";
                x += step;
                graphicPoints.add(new XYChart.Data(x, y));
            }
            str += "\n--- результати ---\n\t" + y + "\n";
            result = y;
            return str;
        }

        private String methodRungeKutts() {
            String str = "---------- Метод Рунге-Кутта ----------\n";
            double y = y0;
            double x = beginPoint;
            double step = Math.pow(0.00001, 1 / 4.);
            double delta;
            double k1, k2, k3, k4;
            while (x <= endPoint) {
                k1 = function.function(x, y);
                k2 = function.function(x + step / 2, y + k1 / 2);
                k3 = function.function(x + step / 2, y + k2 / 2);
                k4 = function.function(x + step, y + k3);
                delta = (k1 + 2 * k2 + 2 * k3 + k4) * step / 6;
                y = y + delta;
                str += "--- ітерація ---\n\tx = " + x + "\n\ty = " + y + "\n\tk1 = " + k1 + "\n\tk2 = " + k2 + "\n\tk3 = " + k3 + "\n\tk4 = " + k4 +  "\n\tdelta = " + delta + "\n";
                x += step;
                graphicPoints.add(new XYChart.Data(x, y));
            }
            str += "\n--- результати ---\n\t" + y + "\n";
            result = y;
            return str;
        }

        private String methodAdamss1() {
            String str = "---------- Метод Адамса N=1 ----------\n";
            double y = y0;
            double x = beginPoint;
            while (x <= endPoint) {
                y = y + step * (3 * function.function(x, y) - function.function(x - step, y)) / 2;
                str += "--- ітерація ---\n\tx = " + x + "\n\ty = " + y + "\n";
                x += step;
                graphicPoints.add(new XYChart.Data(x, y));
            }
            str += "\n--- результати ---\n\t" + y + "\n";
            result = y;
            return str;
        }

        private String methodAdamss2() {
            String str = "---------- Метод Адамса N=2 ----------\n";
            double y = y0;
            double x = beginPoint;
            while (x <= endPoint) {
                y = y + step * (23 * function.function(x, y) - 16 * function.function(x - step, y) + 5 * function.function(x - 2 * step, y) - 2) / 12;
                str += "--- ітерація ---\n\tx = " + x + "\n\ty = " + y + "\n";
                x += step;
                graphicPoints.add(new XYChart.Data(x, y));
            }
            str += "\n--- результати ---\n\t" + y + "\n";
            result = y;
            return str;
        }

        private String methodAdamss3() {
            String str = "---------- Метод Адамса N=3 ----------\n";
            double y = y0;
            double x = beginPoint;
            while (x <= endPoint) {
                y = y + step * (55 * function.function(x, y) - 59 * function.function(x - step, y) + 37 * function.function(x - 2 * step, y) - 9 * function.function(x - 3 * step, y)) / 24;
                str += "--- ітерація ---\n\tx = " + x + "\n\ty = " + y + "\n";
                x += step;
                graphicPoints.add(new XYChart.Data(x, y));
            }
            str += "\n--- результати ---\n\t" + y + "\n";
            result = y;
            return str;
        }

        public double getResult() {
            return result;
        }

        public XYChart.Data[] getGraphicPoints() {
            return graphicPoints.toArray(new XYChart.Data[graphicPoints.size()]);
        }

        public void setPointsInList(ObservableList<TableRow> table) {
            table.clear();
            int i = 0;
            for (XYChart.Data data : graphicPoints) {
                table.add(new TableRow(i, (double) data.getXValue(), (double) data.getYValue()));
                i++;
            }
        }

    }

    class MyFunction implements Function {

        @Override
        public double function(double x, double y) {
            return (x + Math.sin(y / Math.PI));
        }

    }

    class TableRow {

        private StringProperty i = new SimpleStringProperty("0");
        private StringProperty x = new SimpleStringProperty("0.0");
        private StringProperty y = new SimpleStringProperty("0.0");

        public TableRow(int i, double x, double y) {
            this.i = new SimpleStringProperty(i + "");
            this.x = new SimpleStringProperty(x + "");
            this.y = new SimpleStringProperty(y + "");
        }

        public StringProperty iProperty() {
            return i;
        }

        public StringProperty xProperty() {
            return x;
        }

        public StringProperty yProperty() {
            return y;
        }
    }

}
