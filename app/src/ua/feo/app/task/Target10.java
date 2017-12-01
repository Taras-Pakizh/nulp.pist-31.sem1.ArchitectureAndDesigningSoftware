package ua.feo.app.task;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class Target10 implements TargetInf {

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
        return "Чисельне диференціювання";
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
    @FXML private TextField stepsField;
    @FXML private TextArea logText;
    @FXML private TabPane tabPane;
    @FXML private TextField x0Field;
    @FXML private Label result1;
    @FXML private Label result2;

    final private Function function = new MyFunction();

    public void init() {
        this.method.setItems(FXCollections.observableArrayList(
                "Перша похідна",
                "Друга похідна"
        ));
        clearFields();
    }

    private void clearFields() {
        method.getSelectionModel().selectFirst();
        stepsField.setText("0.01");
        x0Field.setText("1.2");
    }

    @FXML
    public void onAction() {
        clearLog();
        try {
            double step = Double.parseDouble(stepsField.getText());
            double x0 = Double.parseDouble(x0Field.getText());
            Method method = new Method(function, x0, step);
            this.logText.appendText(method.work(this.method.getSelectionModel().getSelectedItem()));
            switch (this.method.getSelectionModel().getSelectedItem()) {
                case "Перша похідна":
                    result1.setText(method.getResult() + "");
                    break;
                case "Друга похідна":
                    result2.setText(method.getResult() + "");
                    break;
            }
        } catch (Exception ex) {
            this.logText.appendText("Error 1: некоректні дані. поле не може бути порожнім.\n");
        }
        tabPane.getSelectionModel().select(2);
    }

    @FXML
    public void clearLog() {
        this.logText.setText("");
    }

    interface Function {

        double function(double x);

    }

    class Method {

        private Function function;
        private double x0;
        private double step;
        private double result;

        public Method(Function function, double x0, double step) {
            this.function = function;
            this.x0 = x0;
            this.step = step;
        }

        public String work(String item) {
            String table = table();
            switch (item) {
                case "Перша похідна":
                    return table +  method1();
                case "Друга похідна":
                    return table + method2();
                default:
                    return "Error 0: вибраний метод не підтримується.";
            }
        }

        private String table() {
            String str = "";
            str += "x \t y \t d1y \t d2y \t d3y \t d4y \td5y\n";
            for (int i = 0; i < 5; i++) {
                str += (x0 + i * step) + " \t" + (function.function(x0 + i * step)) + "\t";
                for (int j = 0; j < 5 - i; j++) {
                    str += delta(j, i);
                    str += "\t ";
                }
                str += "\n ";
            }
            return str + "\n";
        }

        private String method1() {
            String str = "---------- Перша похідна ----------\n";
            double sum = 0.0;
            for (int i = 1; i < 6; i++) {
                str += "\tsum = " + sum + "\n";
                sum += Math.pow(-1, i) * delta(i, 0) / i;
            }
            this.result = sum / step;
            str += "\tresult = " + result;
            return str;
        }

        private String method2() {
            String str = "---------- Друга похідна ----------\n";
            double sum = delta(2, 0) - delta(3, 0) + 11 / 12 * delta(4, 0) - 5 / 6 * delta(5, 0);
            this.result = sum / (step * step);
            str += "\tsum = " + sum + "\n\tresult = " + result;
            return str;
        }

        private double delta(int n, int i) {
            if (n == 0) {
                return function.function(x0 + (i + 1) * step) - function.function(x0 + i * step);
            }
            return delta(n - 1, i + 1) - delta(n - 1, i);
        }

        public double getResult() {
            return result;
        }



    }

    class MyFunction implements Function {

        @Override
        public double function(double x) {
            return (sin(x) / cos(x));
        }

    }

}
