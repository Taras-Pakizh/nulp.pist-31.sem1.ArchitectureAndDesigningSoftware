package ua.feo.app.inf.task;

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
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

import static java.lang.Math.E;
import static java.lang.Math.exp;
import static java.lang.Math.pow;

public class Target6 implements TargetInf {

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
        return "Чисельні методи інтегрування";
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
    @FXML private TextField stepsField;
    @FXML private TextArea logText;
    @FXML private AnchorPane chartPane;
    @FXML private TabPane tabPane;
    @FXML private Label precisionLabel;
    @FXML private Label integralLabel;
    @FXML private TableView<TableRow> tableView;
    @FXML private TableColumn<TableRow, String> iColumn;
    @FXML private TableColumn<TableRow, String> xColumn;
    @FXML private TableColumn<TableRow, String> yColumn;
    @FXML private TextField precisionField;
    @FXML private ComboBox<String> stepMethod;
    @FXML private Label stepLabel;

    private ObservableList<TableRow> table = FXCollections.observableArrayList();
    private AreaChart<Double, Double> chart;
    final private Function function = new MyFunction();

    @FXML
    private void initialize() {
        iColumn.setCellValueFactory(cellData -> cellData.getValue().iProperty());
        xColumn.setCellValueFactory(cellData -> cellData.getValue().xProperty());
        yColumn.setCellValueFactory(cellData -> cellData.getValue().yProperty());

        tableView.setItems(table);
        tabPane.getSelectionModel().select(0);
        chart = new AreaChart(new NumberAxis(), new NumberAxis());
        chart.setLegendVisible(false);
        chart.setCreateSymbols(false);
        chartPane.getChildren().add(chart);
        this.method.setItems(FXCollections.observableArrayList(
                "Метод лівих прямокутників",
                "Метод середніх прямокутників",
                "Метод правих прямокутників",
                "Метод трапецій",
                "Метод Сімпсона"
        ));
        this.stepMethod.setItems(FXCollections.observableArrayList(
                "Теоретичний метод",
                "Емпіричний метод",
                "Метод за схемою Ейткена"
        ));
        clearFields();
    }

    private void clearFields() {
        method.getSelectionModel().selectFirst();
        stepMethod.getSelectionModel().selectFirst();
        beginPointField.setText("-1");
        endPointField.setText("10");
        stepsField.setText("1000");
        precisionField.setText("0.0000001");
    }

    @FXML
    public void onAction() {
        clearLog();
        table.clear();
        try {
            double beginPoint = Double.parseDouble(beginPointField.getText());
            double endPoint = Double.parseDouble(endPointField.getText());
            int steps = Integer.parseInt(stepsField.getText());
            Method method = new Method(function, beginPoint, endPoint, steps);

            this.logText.appendText(method.work(this.method.getSelectionModel().getSelectedItem(), table));
            chart.getData().add(new XYChart.Series<Double, Double>(FXCollections.observableArrayList(
                    method.getGraphicPoints()
            )));
            precisionLabel.setText("Похибка методу: " + method.getPrecision());
            integralLabel.setText("Інтеграл функції: " + method.getResult());
        } catch (Exception ex) {
            this.logText.appendText("Error 1: некоректні дані. поле не може бути порожнім.\n");
        }
        tabPane.getSelectionModel().select(2);
    }

    @FXML
    public void secondOnAction() {
        try {
            double beginPoint = Double.parseDouble(beginPointField.getText());
            double endPoint = Double.parseDouble(endPointField.getText());
            double precision = Double.parseDouble(precisionField.getText());
            int steps = Integer.parseInt(stepsField.getText());
            Method method = new Method(function, beginPoint, endPoint, steps);
            stepLabel.setText("Оптимальний крок: " + method.calculPrecision(precision, this.method.getSelectionModel().getSelectedItem(), stepMethod.getSelectionModel().getSelectedItem()));
        } catch (Exception ex) {
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
    interface Function {

        double function(double x);

        double derivative(double x);

        double derivative2(double x);

        double derivative4(double x);

    }

    class Method {

        private Function function;
        private double beginPoint;
        private double endPoint;
        private int steps;
        private List<XYChart.Data> graphicPoints;
        private double precision;
        private double result;

        public Method(Function function, double beginPoint, double endPoint, int steps) {
            this.function = function;
            this.beginPoint = beginPoint;
            this.endPoint = endPoint;
            this.steps = steps;
            this.graphicPoints = new ArrayList<>();
        }

        public String work(String item, List<TableRow> table) {
            buildGraphic(table);
            switch (item) {
                case "Метод лівих прямокутників":
                    return methodLeftRectangles (table);
                case "Метод середніх прямокутників":
                    return methodCenterRectangles(table);
                case "Метод правих прямокутників":
                    return methodRightRectangles(table);
                case "Метод трапецій":
                    return methodTrapezoidal(table);
                case "Метод Сімпсона":
                    return methodSimpsons(table);
                default:
                    return "Error 0: вибраний метод не підтримується.";
            }
        }

        private void buildGraphic(List<TableRow> table) {
            double step = (endPoint - beginPoint) / steps;
            double x = beginPoint;
            for (int i = 0; i < steps; i++, x += step) {
                graphicPoints.add(new XYChart.Data(x, function.function(x)));
            }
        }

        private String methodLeftRectangles (List<TableRow> table) {
            String result = "---------- Метод лівих прямокутників ----------\n";
            this.result = 0.0;
            double x = beginPoint;
            double y;
            double h = (endPoint - beginPoint) / steps;
            for (int i = 0; i < steps; i++, x += h) {
                y = function.function(x);
                result += "--- крок " + i + " ---\n\tx = " + x + "\n\ty = " + y + "\n\tsuma = " + this.result +"\n";
                table.add(new TableRow(i, x, y));
                this.result += y;
            }
            this.result *= h;
            precision = Math.abs(function.derivative2(endPoint - beginPoint) * (endPoint - beginPoint) * h * h / 24);
            result += "\n--- Результати ---\n\tінтеграл функції : " + this.result + "\n\tпохибка : " + precision + "\n";
            return result;
        }

        private String methodCenterRectangles(List<TableRow> table) {
            String result = "---------- Метод середніх прямокутників ----------\n";
            this.result = 0.0;
            double x = beginPoint;
            double y;
            double h = (endPoint - beginPoint) / steps;
            for (int i = 0; i < steps; i++, x += h) {
                y = function.function(x + h / 2);
                result += "--- крок " + i + " ---\n\tx = " + x + "\n\ty = " + y + "\n\tsuma = " + this.result +"\n";
                table.add(new TableRow(i, x, y));
                this.result += y;
            }
            this.result *= h;
            precision = Math.abs(function.derivative2(endPoint - beginPoint) * (endPoint - beginPoint) * h * h / 24);
            result += "\n--- Результати ---\n\tінтеграл функції : " + this.result + "\n\tпохибка : " + precision + "\n";
            return result;
        }

        private String methodRightRectangles(List<TableRow> table) {
            String result = "---------- Метод правих прямокутників ----------\n";
            this.result = 0.0;
            double x = beginPoint;
            double y;
            double h = (endPoint - beginPoint) / steps;
            x += h;
            for (int i = 1; i <= steps; i++, x += h) {
                y = function.function(x);
                result += "--- крок " + i + " ---\n\tx = " + x + "\n\ty = " + y + "\n\tsuma = " + this.result +"\n";
                table.add(new TableRow(i, x, y));
                this.result += y;
            }
            this.result *= h;
            precision = Math.abs(function.derivative2(endPoint - beginPoint) * (endPoint - beginPoint) * h * h / 24);
            result += "\n--- Результати ---\n\tінтеграл функції : " + this.result + "\n\tпохибка : " + precision + "\n";
            return result;
        }

        private String methodTrapezoidal(List<TableRow> table) {
            String result = "---------- Метод трапецій ----------\n";
            this.result = 0.0;
            double x = beginPoint;
            double y;
            double h = (endPoint - beginPoint) / steps;
            for (int i = 0; i < steps; i++, x += h) {
                y = (function.function(x) + function.function(x + h)) / 2;
                result += "--- крок " + i + " ---\n\tx = " + x + "\n\ty = " + y + "\n\tsuma = " + this.result +"\n";
                table.add(new TableRow(i, x, y));
                this.result += y;
            }
            this.result *= h;
            precision = Math.abs(function.derivative2(endPoint - beginPoint) * (endPoint - beginPoint) * h * h / 12);
            result += "\n--- Результати ---\n\tінтеграл функції : " + this.result + "\n\tпохибка : " + precision + "\n";
            return result;
        }

        private String methodSimpsons(List<TableRow> table) {
            String result = "---------- Метод Сімпсона ----------\n";
            this.result = 0.0;
            double x = beginPoint;
            double y;
            double h = (endPoint - beginPoint) / steps;
            this.result += function.function(beginPoint) + function.function(endPoint);
            for (int i = 1; i < steps; i++, x += h) {
                y = 4 * function.function(x - h / 2) + 2 * function.function(x);
                result += "--- крок " + i + " ---\n\tx = " + x + "\n\ty = " + y * h / 6 + "\n\tsuma = " + this.result +"\n";
                table.add(new TableRow(i, x, y / 6));
                this.result += y;
            }
            this.result = this.result * h / 6;
            precision = Math.abs(pow(endPoint - beginPoint, 5) * function.derivative4(endPoint - beginPoint) / (180 * pow(steps, 4)));
            result += "\n--- Результати ---\n\tінтеграл функції : " + this.result + "\n\tпохибка : " + precision + "\n";
            return result;
        }

        public XYChart.Data[] getGraphicPoints() {
            return graphicPoints.toArray(new XYChart.Data[graphicPoints.size()]);
        }

        public double getPrecision() {
            return precision;
        }

        public double getResult() {
            return result;
        }

        public int calculPrecision(double e, String item, String method) {
            int m;
            switch (item) {
                case "Метод лівих прямокутників":
                case "Метод середніх прямокутників":
                case "Метод правих прямокутників":
                case "Метод трапецій":
                    m = 2;
                    break;
                case "Метод Сімпсона":
                    m = 4;
                    break;
                default:
                    m = 0;
            }
            switch (method) {
                case "Теоретичний метод":
                    return teorPrecision(e);
                case "Емпіричний метод":
                    return precision(e, m);
                case "Метод за схемою Ейткена":
                    return eitcenPrecision(e, m);
                default:
                    return -1;
            }
        }

        private int teorPrecision(double e) {
            return (int) (6 * e / function.derivative2(endPoint - beginPoint) / (endPoint - beginPoint));
        }

        private int precision(double e, int m) {
            double h = pow(e, 1 / m);
            int n = (int) ((endPoint - beginPoint) / h);
            Method meth1 = new Method(function, beginPoint, endPoint, n);
            meth1.work("Метод Сімпсона", new ArrayList<>());
            double i1 = meth1.getResult();
            Method meth2 = new Method(function, beginPoint, endPoint, 2 * n);
            meth2.work("Метод Сімпсона", new ArrayList<>());
            i1 -= meth2.getResult();
            if (Math.abs(i1) < e) {
                return 2 * n;
            } else {
                return 4 * n;
            }
        }

        private int eitcenPrecision(double e, int m) {
            double h = pow(e, 1 / m);
            int n = (int) ((endPoint - beginPoint) / h);
            Method meth1 = new Method(function, beginPoint, endPoint, n);
            meth1.work("Метод Сімпсона", new ArrayList<>());
            double i1 = meth1.getResult();
            Method meth2 = new Method(function, beginPoint, endPoint, 2 * n);
            meth2.work("Метод Сімпсона", new ArrayList<>());
            double i2 = meth2.getResult();
            Method meth3 = new Method(function, beginPoint, endPoint, 4 * n);
            meth3.work("Метод Сімпсона", new ArrayList<>());
            double i3 = meth3.getResult();
            double i = i1 - (pow(i1 - i2, 2) / (i1 - 2 * i2 + i3));
            if (Math.abs(i - i1) < e) {
                return n;
            } else if (Math.abs(i - i2) < e) {
                return 2 * n;
            } else {
                return 4 * n;
            }
        }

    }

    class MyFunction implements Function {

        @Override
        public double function(double x) {
            return (pow(x, 3/2) * exp(-x*x/4));
        }

        @Override
        public double derivative(double x) {
            return (-0.5 * pow(E, -1 * x * x / 4) * pow(x, 1 / 2) * (x * x -3));
        }

        @Override
        public double derivative2(double x) {
            return ((pow(E, -1 * x * x / 4) * (x * x * x * x - 8 * x * x + 3)) / (4 * pow(x, 1 / 2)));
        }

        @Override
        public double derivative4(double x) {
            return ((pow(E, -1 * x * x / 4) * (pow(x, 8) - 24 * pow(x, 6) + 102 * pow(x, 4) - 24 * pow(x, 2) + 9)) / (16 * pow(x, 5 / 2)));
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
