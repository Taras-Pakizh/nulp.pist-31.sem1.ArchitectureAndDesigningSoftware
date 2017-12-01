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

public class Target9 implements TargetInf {

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
        return "Наближення функції методом найменших квадратів";
    }

    @Override
    public List<String> getTestCaseList() {
        return new ArrayList<>(data.keySet());
    }

    @Override
    public Map<String, Boolean> getEtalonTestCaseList() {
        return data;
    }


    @FXML private TextArea logText;
    @FXML private AnchorPane chartPane;
    @FXML private TableView<TableRow> tableView;
    @FXML private TableColumn<TableRow, String> xColumn;
    @FXML private TableColumn<TableRow, String> yColumn;
    @FXML private TextField xField;
    @FXML private Label aLabel;
    @FXML private Label bLabel;
    @FXML private Label cLabel;
    @FXML private Label yLabel;
    @FXML private Label pLabel;
    @FXML private ComboBox<String> method;

    private ObservableList<TableRow> table = FXCollections.observableArrayList();
    private AreaChart<Double, Double> chart;

    @FXML
    private void initialize() {
        this.method.setItems(FXCollections.observableArrayList(
                "Лінійний поліном",
                "Квадратичний поліном"
        ));
        this.method.getSelectionModel().select(0);
        xField.setText("1.355");
        tableView.setItems(table);
        tableView.setEditable(true);
        initTable();
        chart = new AreaChart(new NumberAxis(), new NumberAxis());
        chart.setLegendVisible(false);
        chart.setCreateSymbols(false);
        chartPane.getChildren().add(chart);
        clearFields();
    }

    public void initTable() {
        xColumn.setCellValueFactory(cellData -> cellData.getValue().getX());
        yColumn.setCellValueFactory(cellData -> cellData.getValue().getY());
    }

    private void clearFields() {
        table.addAll(
                new TableRow(1.34, 4.26),
                new TableRow(1.345, 4.35),
                new TableRow(1.35, 4.46),
                new TableRow(1.36, 4.56),
                new TableRow(1.365, 4.67),
                new TableRow(1.37, 4.79),
                new TableRow(1.375, 4.91),
                new TableRow(1.38, 5.01),
                new TableRow(1.385, 5.18),
                new TableRow(1.39, 5.35)
        );
    }

    @FXML
    public void onActionGo() {
        clearLog();
        try {
            double[] x = new double[table.size()];
            double[] y = new double[table.size()];
            int i = 0;
            for (TableRow row : table) {
                x[i] = Double.parseDouble(row.x());
                y[i] = Double.parseDouble(row.y());
                i++;
            }
            Method method;
            method = new Method(x, y);
            logText.appendText(method.work(this.method.getSelectionModel().getSelectedItem()));
            aLabel.setText(method.getResultA() + "");
            bLabel.setText(method.getResultB() + "");
            cLabel.setText(method.getResultC() + "");
            switch (this.method.getSelectionModel().getSelectedItem()) {
                case "Лінійний поліном":
                    yLabel.setText(method.getY(Double.parseDouble(xField.getText())));
                    break;
                case "Квадратичний поліном":
                    pLabel.setText(method.getP(Double.parseDouble(xField.getText())));
                    break;
            }
            chart.getData().add(new XYChart.Series<Double, Double>(FXCollections.observableArrayList(
                    method.getGraphicPoints()
            )));
        } catch (Exception ex) {
            ex.printStackTrace();
            this.logText.appendText("Error 1: некоректні дані. поле не може бути порожнім.\n");
        }
    }

    @FXML
    public void clearLog() {
        this.logText.setText("");
    }

    @FXML
    public void clearGraphic() {
        this.chart.getData().removeAll(this.chart.getData());
    }

    class Method {

        private double[] x;
        private double[] y;
        private double[] z;

        private int size;

        private List<XYChart.Data> graphicPoints = new ArrayList<>();
        private double resultA = 0;
        private double resultB = 0;
        private double resultC = 0;

        public Method(double[] x, double[] y, double[] z) {
            this.x = x;
            this.y = y;
            this.z = z;
            size = 3;
            graphicPoints();
        }

        public Method(double[] x, double[] y) {
            this.x = x;
            this.y = y;
            size = 2;
            graphicPoints();
        }

        private void graphicPoints() {
            for (int i = 0; i < x.length; i++) {
                graphicPoints.add(new XYChart.Data(x[i], y[i]));
            }
        }

        public String work(String item) {
            switch (item) {
                case "Лінійний поліном":
                    return method2();
                case "Квадратичний поліном":
                    return method3();
                default:
                    return "Error 0: вибраний метод не підтримується.";
            }
        }

        private String method2() {
            String str = "---------- Метод 2 ----------\n";
            double xSum = 0.0;
            double x2Sum = 0.0;
            double ySum = 0.0;
            double xySum = 0.0;
            for (int i = 0; i < x.length; i++) {
                xSum += x[i];
                x2Sum += x[i] * x[i];
                ySum += y[i];
                xySum += x[i] * y[i];
            }
            resultA = (x.length * xySum - xSum * ySum) / (x.length * x2Sum - xSum * xSum);
            resultB = (ySum - resultA * xSum) / x.length;
            str += "\ttxSum = " + xSum + "\n" +
                    "\tx2Sum = " + x2Sum + "\n" +
                    "\tySum = " + ySum + "\n" +
                    "\txySum = " + xySum + "\n" +
                    "\ta = " + resultA + "\n" +
                    "\tb = " + resultB + "\n";
            return str;
        }

        private String method3() {
            String str = "---------- Метод 3 ----------\n";
            double xSum = 0.0;
            double x2Sum = 0.0;
            double x3Sum = 0.0;
            double x4Sum = 0.0;
            double ySum = 0.0;
            double xySum = 0.0;
            double x2ySum = 0.0;
            for (int i = 0; i < x.length; i++) {
                xSum += x[i];
                x2Sum += x[i] * x[i];
                x3Sum += x[i] * x[i] * x[i];
                x4Sum += x[i] * x[i] * x[i] * x[i];
                ySum += y[i];
                xySum += x[i] * y[i];
                x2ySum += x[i] * x[i] * y[i];
            }
            resultB = Math.abs(xySum / x2Sum);
            resultC = Math.abs((x2ySum - 4 * ySum) / (x4Sum - 4 * x2Sum));
            resultA = Math.abs((ySum + x2Sum * resultC) / x.length);
            str += "\ttxSum = " + xSum + "\n" +
                    "\tx2Sum = " + x2Sum + "\n" +
                    "\tx3Sum = " + x3Sum + "\n" +
                    "\tx4Sum = " + x4Sum + "\n" +
                    "\tySum = " + ySum + "\n" +
                    "\txySum = " + xySum + "\n" +
                    "\tx2ySum = " + x2ySum + "\n" +
                    "\ta = " + resultA + "\n" +
                    "\tb = " + resultB + "\n" +
                    "\ta = " + resultC + "\n";
            return str;
        }

        public double getResultA() {
            return resultA;
        }

        public double getResultB() {
            return resultB;
        }

        public double getResultC() {
            return resultC;
        }

        public XYChart.Data[] getGraphicPoints() {
            return graphicPoints.toArray(new XYChart.Data[graphicPoints.size()]);
        }

        public String getY(double x) {
            return (String.format("%(.2f", resultA) + " * x + " + String.format("%(.2f", resultB) + " = " + String.format("%(.2f", (resultA * x + resultB)));
        }

        public String getP(double x) {
            return (String.format("%(.2f", resultA) + " + x * " + String.format("%(.2f", resultB) + " + x^2 * " + String.format("%(.2f", resultC) + " = " + String.format("%(.2f", (resultA + x * resultB + x * x * resultC)));
        }


    }

    class TableRow {

        private String x;
        private String y;

        public TableRow(double x, double y) {
            this.x = x + "";
            this.y = y + "";
        }

        public String x() {
            return x;
        }

        public String y() {
            return y;
        }

        public StringProperty getX() {
            return new SimpleStringProperty(x);
        }

        public StringProperty getY() {
            return new SimpleStringProperty(y);
        }

    }

}
