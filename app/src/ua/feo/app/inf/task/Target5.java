package ua.feo.app.inf.task;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Pair;

import java.util.*;
import java.util.stream.Collectors;

public class Target5 implements TargetInf {

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
        return "Інтерполяція та апроксимація функції";
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
    @FXML private TextField x1Field;
    @FXML private TextField x2Field;
    @FXML private TextField x3Field;
    @FXML private TextField x4Field;
    @FXML private TextField x5Field;
    @FXML private TextField x6Field;
    @FXML private TextField x7Field;
    @FXML private TextField x8Field;
    @FXML private TextField x9Field;
    @FXML private TextField x10Field;
    @FXML private TextField y1Field;
    @FXML private TextField y2Field;
    @FXML private TextField y3Field;
    @FXML private TextField y4Field;
    @FXML private TextField y5Field;
    @FXML private TextField y6Field;
    @FXML private TextField y7Field;
    @FXML private TextField y8Field;
    @FXML private TextField y9Field;
    @FXML private TextField y10Field;
    @FXML private TextField xField;
    @FXML private Label yField;
    @FXML private TextArea logText;
    @FXML private CheckBox random;

    @FXML
    public void initialize() {
        this.method.setItems(FXCollections.observableArrayList(
                "Інтерполяційний поліном Лагранжа",
                "Інтерполяційний поліном Ньютона"
        ));
        clearFields();
    }

    private void clearFields() {
        method.getSelectionModel().selectFirst();
        x1Field.setText("1.340");
        x2Field.setText("1.345");
        x3Field.setText("1.350");
        x4Field.setText("1.360");
        x5Field.setText("1.365");
        x6Field.setText("1.370");
        x7Field.setText("1.375");
        x8Field.setText("1.380");
        x9Field.setText("1.385");
        x10Field.setText("1.390");
        y1Field.setText("4.26");
        y2Field.setText("4.35");
        y3Field.setText("4.46");
        y4Field.setText("4.56");
        y5Field.setText("4.67");
        y6Field.setText("4.79");
        y7Field.setText("4.91");
        y8Field.setText("5.01");
        y9Field.setText("5.18");
        y10Field.setText("5.42");
        xField.setText("1.362");
        //xField.setText("1.382");
    }

    @FXML
    public void onAction() {
        try {
            double[] x;
            double[] y;
            double currentX;
            if (random.isSelected()) {
                Random rnd = new Random();
                x = new double[10];
                y = new double[10];
                x[0] = (-10000.0 + rnd.nextInt(2000)) / 100;
                for (int i = 1; i < x.length; i++) {
                    x[i] = (100 * x[i-1] + rnd.nextInt(4000)) / 100;
                }
                for (int i = 0; i < y.length; i++) {
                    y[i] = 10000 * rnd.nextDouble();
                }
                //currentX = 10000 * rnd.nextDouble();
                x1Field.setText(x[0] + "");
                x2Field.setText(x[1] + "");
                x3Field.setText(x[2] + "");
                x4Field.setText(x[3] + "");
                x5Field.setText(x[4] + "");
                x6Field.setText(x[5] + "");
                x7Field.setText(x[6] + "");
                x8Field.setText(x[7] + "");
                x9Field.setText(x[8] + "");
                x10Field.setText(x[9] + "");
                y1Field.setText(y[0] + "");
                y2Field.setText(y[1] + "");
                y3Field.setText(y[2] + "");
                y4Field.setText(y[3] + "");
                y5Field.setText(y[4] + "");
                y6Field.setText(y[5] + "");
                y7Field.setText(y[6] + "");
                y8Field.setText(y[7] + "");
                y9Field.setText(y[8] + "");
                y10Field.setText(y[9] + "");
                //xField.setText(currentX + "");
            } else {
                x = new double[]{
                        Double.parseDouble(x1Field.getText()),
                        Double.parseDouble(x2Field.getText()),
                        Double.parseDouble(x3Field.getText()),
                        Double.parseDouble(x4Field.getText()),
                        Double.parseDouble(x5Field.getText()),
                        Double.parseDouble(x6Field.getText()),
                        Double.parseDouble(x7Field.getText()),
                        Double.parseDouble(x8Field.getText()),
                        Double.parseDouble(x9Field.getText()),
                        Double.parseDouble(x10Field.getText()),
                };
                y = new double[]{
                        Double.parseDouble(y1Field.getText()),
                        Double.parseDouble(y2Field.getText()),
                        Double.parseDouble(y3Field.getText()),
                        Double.parseDouble(y4Field.getText()),
                        Double.parseDouble(y5Field.getText()),
                        Double.parseDouble(y6Field.getText()),
                        Double.parseDouble(y7Field.getText()),
                        Double.parseDouble(y8Field.getText()),
                        Double.parseDouble(y9Field.getText()),
                        Double.parseDouble(y10Field.getText()),
                };
                currentX = Double.parseDouble(xField.getText());
            }
            currentX = Double.parseDouble(xField.getText());
            Method method = new Method(x, y, currentX);
            this.logText.setText(method.work(this.method.getSelectionModel().getSelectedItem()));
            double result = method.getResult();
            yField.setText(result + "");
        } catch (Exception ex) {
            this.logText.appendText("Error 1: некоректні дані. поле не може бути порожнім.\n");
        }
    }

    @FXML
    public void clearLog() {
        this.logText.setText("");
    }

    class Method {

        private final double[] x;
        private final double[] y;
        private final double currentX;
        private double currentY;

        public Method(double[] x, double[] y, double currentX) {
            this.x = x;
            this.y = y;
            this.currentX = currentX;
        }

        public String work(String item) {
            switch (item) {
                case "Інтерполяційний поліном Лагранжа":
                    return lagrangeInterpolationPolynomial();
                case "Інтерполяційний поліном Ньютона":
                    return newtonInterpolationPolynomial();
                default:
                    return "Error 0: вибраний метод не підтримується.";
            }
        }

        private String lagrangeInterpolationPolynomial() {
            String result = "---------- Інтерполяційний поліном Лагранжа ----------\n";
            currentY = 0.0;
            for (int i = 0; i < x.length; i++) {
                result += "\n--- P[" + i + "](x) ---\n";
                double ch = 1;
                double z = 1;
                for (int j = 0; j < x.length; j++) {
                    if (j != i) {
                        ch *= currentX - x[j];
                        z *= x[i] - x[j];
                    }
                }
                result += "\tP[" + i + "](x) = " + (ch / z) + "\n";
                result += "\n--- Suma [" + i + "]---\n";
                result += "\ti = " + i + "\n";
                result += "\tsuma = " + currentY + "\n";
                currentY += (ch / z) * y[i];
            }
            result += "\n--- Результат---\n";
            result += "\tx = " + currentX + "\n";
            result += "\ty = " + currentY + "\n";
            return result;
        }

        private String newtonInterpolationPolynomial() {
            String result = "---------- Інтерполяційний поліном Ньютона ----------\n";
            currentY = y[0];
            for (int i = 1; i < x.length; i++) {
                result += "\n--- F---\n";
                double f = 0;
                for (int j = 0; j <= i; j++) {
                    double den = 1;
                    for (int k = 0; k <= i; k++) {
                        if (k != j) {
                            den *= (x[j] - x[k]);
                        }
                    }
                    result += "\tΔ[" + j + "] = " + den + "\n";
                    f += y[j] / den;
                }
                for (int k = 0; k < i; k++) {
                    f *= (currentX - x[k]);
                }
                result += "\tf = " + f + "\n";
                currentY += f;
            }
            result += "\n--- Результат---\n";
            result += "\tx = " + currentX + "\n";
            result += "\ty = " + currentY + "\n";
            return result;
        }

        public double getResult() {
            return currentY;
        }

    }

}
