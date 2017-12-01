package ua.feo.app.task;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class Target2 implements TargetInf {

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
        return "Чисельні методи розв’язування нелінійних рівнянь";
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
    @FXML private TextField precision;
    @FXML private TextField a0Field;
    @FXML private TextField a1Field;
    @FXML private TextField a2Field;
    @FXML private TextField a3Field;
    @FXML private TextField a4Field;
    @FXML private TextField a5Field;
    @FXML private TextArea logText;
    @FXML private Pane chartPane;
    private LineChart<Double, Double> chart;

    @FXML
    public void initialize() {
        chart = new LineChart(new NumberAxis(), new NumberAxis());
        chart.setLegendVisible(false);
        chart.setCreateSymbols(false);
        chartPane.getChildren().add(chart);
        this.method.setItems(FXCollections.observableArrayList(
                "Метод поділу відрізка навпіл",
                "Метод хорд",
                "Метод Ньютона (метод дотичних)",
                "Метод ітерацій (метод послідовних наближень)"
        ));
        clearFields();
    }

    private void clearFields() {
        this.beginPointField.setText("1");
        this.endPointField.setText("4");
        this.precision.setText("0.01");
        this.a0Field.setText("-4");
        this.a1Field.setText("1");
        this.a2Field.setText("-2");
        this.a3Field.setText("1");
        this.a4Field.setText("0");
        this.a5Field.setText("0");
        this.method.getSelectionModel().selectFirst();
    }

    @FXML
    public void onAction() {
        try {
            double beginPoint = Double.parseDouble(this.beginPointField.getText());
            double endPoint = Double.parseDouble(this.endPointField.getText());
            double precision = Double.parseDouble(this.precision.getText());
            double[] coefficients = new double[]{
                    Double.parseDouble(this.a0Field.getText()),
                    Double.parseDouble(this.a1Field.getText()),
                    Double.parseDouble(this.a2Field.getText()),
                    Double.parseDouble(this.a3Field.getText()),
                    Double.parseDouble(this.a4Field.getText()),
                    Double.parseDouble(this.a5Field.getText()),
            };
            if (precision == 0) {
                this.logText.appendText("Error 3: некоректні дані. похибка не може бути рівна нулю.\n");
                return;
            }
            chart.getData().add(new XYChart.Series<Double, Double>(FXCollections.observableArrayList(
                    buildGraphic(coefficients, beginPoint, endPoint, precision)
            )));
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

    public XYChart.Data[] buildGraphic(double[] coefficients, double beginPoint, double endPoint, double precision) {
        Method method = new Method(coefficients, beginPoint, endPoint, precision);
        this.logText.setText(method.work(this.method.getSelectionModel().getSelectedItem()));
        List<XYChart.Data> result = new ArrayList<>();
        double i = beginPoint;
        while (i <= endPoint) {
            result.add(new XYChart.Data(i, method.function(i)));
            i += precision;
        }
        return result.toArray(new XYChart.Data[result.size()]);
    }

    class Method {

        private final double[] value;
        private final double[] coefficients;
        private final double beginPoint;
        private final double endPoint;
        private final double precision;

        public Method(double[] coefficients, double beginPoint, double endPoint, double precision) {
            this.coefficients = coefficients;
            this.beginPoint = beginPoint;
            this.endPoint = endPoint;
            this.precision = precision;
            this.value = new double[(int)((endPoint - beginPoint) / precision)];
        }

        public String work(String item) {
            if (beginPoint >= endPoint) {
                return "Error 2: некоректний проміжок. початкова точка має бути меншою кінцевої.\n";
            }
            switch (item) {
                case "Метод поділу відрізка навпіл":
                    return methodOfDividingTheSegmentInHalf();
                case "Метод хорд":
                    return methodChords();
                case "Метод Ньютона (метод дотичних)":
                    return methodNewton();
                case "Метод ітерацій (метод послідовних наближень)":
                    return methodOfIterations();
                default:
                    return "Error 0: вибраний метод не підтримується.";
            }
        }

        public double function(double x) {
            double value = 0;
            for (int index = 0; index < 6; index++) {
                value += coefficients[index] * Math.pow(x, index);
            }
            return value;
        }

        public double derivative(double x) {
            double value = 0;
            for (int index = 1; index < 6; index++) {
                value += index * coefficients[index] * Math.pow(x, index - 1);
            }
            return value;
        }

        private String methodOfDividingTheSegmentInHalf() {
            String result = "---------- Метод поділу відрізка навпіл ----------\n";
            if (!(function(beginPoint)*function(endPoint) <= 0)) {
                result += "не виконується умова: f(a)*f(b) <= 0\nна відрізку більше одного коренів або жодного\n";
                return result;
            }
            result += "--- початок роботи методу ---\n";
            double a = beginPoint;
            double b = endPoint;
            double e = precision;
            double x = a;
            int i = 0;
            while(!(Math.abs(b - a) < e) && !(function(x) == 0)) {
                x = (a + b) / 2;
                result += "--- ітерація ---\n\tітерація #" + i + "\n\t[" + a + ";" + b + "]\n\tx = " + x + "\n";
                result += "\tf(x) = " + function(x) + "\n";
                if (function(a)*function(x) < 0) {
                    b = x;
                } else {
                    a = x;
                }
                i++;
            }
            result += "--- кінець роботи методу ---\n\tвиконалась умова:\n";
            if (Math.abs(b - a) < e) {
                result += "\t|b - a| < e\n";
            } else {
                result += "\tf(x) = 0\n";
            }
            result += "--- результат ---\n\tx = " + x + "\n";
            return result;
        }

        private String methodChords() {
            String result = "---------- Метод хорд ----------\n";
            if (!(function(beginPoint)*function(endPoint) <= 0)) {
                result += "не виконується умова: f(a)*f(b) <= 0\nна відрізку більше одного коренів або жодного\n";
                return result;
            }
            result += "--- початок роботи методу ---\n";
            double a = beginPoint;
            double b = endPoint;
            double e = precision;
            double x = a;
            int i = 0;
            do {
                result += "--- ітерація ---\n\tітерація #" + i + "\n\t[" + a + ";" + b + "]\n\tx = " + x + "\n";
                result += "\tf(x) = " + function(x) + "\n";
                x = a - ((function(a) * (b - a)) / (function(b) - function(a)));
                result += "\tштука = " + x + "\n";
                if (function(a) * function(x) > 0) {
                    a = x;
                } else {
                    b= x;
                }
                i++;
            } while (Math.abs(function(x)) >= e);
            result += "--- кінець роботи методу ---\n\tвиконалась умова:\n\t|f(x)| < e\n";
            result += "--- результат ---\n\tx = " + x + "\n";
            return result;
        }

        private String methodNewton() {
            String result = "---------- Метод Ньютона (метод дотичних) ----------\n";
            if (!(function(beginPoint)*function(endPoint) <= 0)) {
                result += "не виконується умова: f(a)*f(b) <= 0\nна відрізку більше одного коренів або жодного\n";
                return result;
            }
            result += "--- початок роботи методу ---\n";
            double a = beginPoint;
            double b = endPoint;
            double e = precision;
            double x = a;
            int i = 0;
            x = a - function(a) / derivative(a);
            while ((Math.abs(function(a)) >= e) || (Math.abs(x - a) >= e)) {
                result += "--- ітерація ---\n\tітерація #" + i + "\n\t[" + a + ";" + b + "]\n\tx = " + x + "\n";
                result += "\tf(x) = " + function(x) + "\n";
                result += "\tf`(x)" + derivative(x) + "\n";
                result += "\t-f`(x) / f(x) " + (- function(x) / derivative(x)) + "\n";
                a = x;
                x = a - function(a) / derivative(a);
                i++;
            }
            result += "--- кінець роботи методу ---\n\tвиконалась умова:\n";
            if (Math.abs(function(a)) > e) {
                result += "\t|f(a)| < e\n";
            } else {
                result += "\tf(x - a)| < e\n";
            }
            result += "--- результат ---\n\tx = " + x + "\n";
            return result;
        }

        private String methodOfIterations() {
            String result = "---------- Метод ітерацій (метод послідовних наближень) ----------\n";
            if (!(function(beginPoint)*function(endPoint) <= 0)) {
                result += "не виконується умова: f(a)*f(b) <= 0\nна відрізку більше одного коренів або жодного\n";
                return result;
            }
            result += "--- початок роботи методу ---\n";
            double a = beginPoint;
            double b = endPoint;
            double e = precision;
            double x = (a + b) / 2;
            double lambda = Math.abs(2 / max());
            int i = 0;
            while ((Math.abs(function(x)) - e) > 0) {
                if (Double.isNaN(x)) {
                    break;
                }
                result += "--- ітерація ---\n\tітерація #" + i + "\n\t[" + a + ";" + b + "]\n\tx = " + x + "\n";
                result += "\tf(x) = " + function(x) + "\n";
                result += "\tfi = " + (x - (function(x) * lambda)) + "\n";
                x = x - (function(x) * lambda);

                i++;
            }

            result += "--- кінець роботи методу ---\n\tвиконалась умова:\n";
            if (Math.abs(function(x)) > e) {
                result += "\t|f(a)| < e\n";
            }
            result += "--- результат ---\n\tx = " + x + "\n";
            return result;
        }

        private double max() {
            double index = beginPoint;
            double max = derivative(index);
            while (index < endPoint) {
                if (derivative(index) > max) {
                    max = derivative(index);
                }
                index += precision;
            }
            return max;
        }

    }
}