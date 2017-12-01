package ua.feo.app.task;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class Target4 implements TargetInf {

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
        return "Ітераційні методи розв’язання СЛАР";
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
    @FXML private TextField accuracyField;
    @FXML private TextField a11Field;
    @FXML private TextField a12Field;
    @FXML private TextField a13Field;
    @FXML private TextField a14Field;
    @FXML private TextField a15Field;
    @FXML private TextField a21Field;
    @FXML private TextField a22Field;
    @FXML private TextField a23Field;
    @FXML private TextField a24Field;
    @FXML private TextField a25Field;
    @FXML private TextField a31Field;
    @FXML private TextField a32Field;
    @FXML private TextField a33Field;
    @FXML private TextField a34Field;
    @FXML private TextField a35Field;
    @FXML private TextField a41Field;
    @FXML private TextField a42Field;
    @FXML private TextField a43Field;
    @FXML private TextField a44Field;
    @FXML private TextField a45Field;
    @FXML private Label x1Field;
    @FXML private Label x2Field;
    @FXML private Label x3Field;
    @FXML private Label x4Field;
    @FXML private TextArea logText;

    @FXML
    public void initialize() {
        this.method.setItems(FXCollections.observableArrayList(
                "Метод Зейделя",
                "Метод послідовних наближень (метод Якобі)"
        ));
        clearFields();
    }

    private void clearFields() {
        method.getSelectionModel().selectFirst();
        a11Field.setText("0.17");
        a12Field.setText("0.31");
        a13Field.setText("-0.18");
        a14Field.setText("0.22");
        a15Field.setText("-1.71");

        a21Field.setText("-0.21");
        a22Field.setText("0");
        a23Field.setText("0.33");
        a24Field.setText("0.22");
        a25Field.setText("0.62");

        a31Field.setText("0.32");
        a32Field.setText("-0.18");
        a33Field.setText("0.05");
        a34Field.setText("-0.19");
        a35Field.setText("-0.89");

        a41Field.setText("0.12");
        a42Field.setText("0.28");
        a43Field.setText("-0.14");
        a44Field.setText("0");
        a45Field.setText("0.94");

//        a11Field.setText("0");
//        a12Field.setText("0.5");
//        a13Field.setText("-0.75");
//        a14Field.setText("0");
//        a15Field.setText("3.25");
//
//        a21Field.setText("0");
//        a22Field.setText("0");
//        a23Field.setText("-0.2");
//        a24Field.setText("0");
//        a25Field.setText("3.2");
//
//        a31Field.setText("-0.5");
//        a32Field.setText("0.75");
//        a33Field.setText("0");
//        a34Field.setText("0");
//        a35Field.setText("0");
//
//        a41Field.setText("0");
//        a42Field.setText("0");
//        a43Field.setText("0");
//        a44Field.setText("0");
//        a45Field.setText("0");

        accuracyField.setText("0.001");
    }

    @FXML
    public void onAction() {
        try {
            double[][] a = new double[][]{
                    { Double.parseDouble(a11Field.getText()), Double.parseDouble(a12Field.getText()), Double.parseDouble(a13Field.getText()), Double.parseDouble(a14Field.getText()), Double.parseDouble(a15Field.getText()), },
                    { Double.parseDouble(a21Field.getText()), Double.parseDouble(a22Field.getText()), Double.parseDouble(a23Field.getText()), Double.parseDouble(a24Field.getText()), Double.parseDouble(a25Field.getText()), },
                    { Double.parseDouble(a31Field.getText()), Double.parseDouble(a32Field.getText()), Double.parseDouble(a33Field.getText()), Double.parseDouble(a34Field.getText()), Double.parseDouble(a35Field.getText()), },
                    { Double.parseDouble(a41Field.getText()), Double.parseDouble(a42Field.getText()), Double.parseDouble(a43Field.getText()), Double.parseDouble(a44Field.getText()), Double.parseDouble(a45Field.getText()), },
            };
            double accuracy = Double.parseDouble(accuracyField.getText());
            Method method = new Method(a, accuracy);
            this.logText.setText(method.work(this.method.getSelectionModel().getSelectedItem()));
            double[] result = method.getResult();
            x1Field.setText("x1 = " + result[0]);
            x2Field.setText("x2 = " + result[1]);
            x3Field.setText("x3 = " + result[2]);
            x4Field.setText("x4 = " + result[3]);
        } catch (Exception ex) {
            this.logText.appendText("Error 1: некоректні дані. поле не може бути порожнім.\n");
        }
    }

    @FXML
    public void clearLog() {
        this.logText.setText("");
    }

    class Method {

        private final double[][] a;
        private final double accuracy;
        private double[] x;

        public Method(double[][] a, double accuracy) {
            this.a = a;
            this.accuracy = accuracy;
            this.x = new double[a.length];
        }

        public String work(String item) {
            switch (item) {
                case "Метод Зейделя":
                    return "Не виконалась умова збіжності\n"+ methodSeidel();
                case "Метод послідовних наближень (метод Якобі)":
                    return "Не виконалась умова збіжності\n" + methodJacobi();
                default:
                    return "Error 0: вибраний метод не підтримується.";
            }
        }

        private String convergence() {
            for (int i = 0; i < a.length; i++) {
                double suma = 0.0;
                for (int j = 0; j < a[i].length - 1; j++) {
                    suma += Math.abs(a[i][j]);
                }
                if (suma > 1) {
                    return "сума елементів " + i + "-го рядка = " + suma + " > 1";
                }
            }
            for (int i = 0; i < a[0].length - 1; i++) {
                double suma = 0.0;
                for (int j = 0; j < a.length; j++) {
                    suma += Math.abs(a[j][i]);
                }
                if (suma > 1) {
                    return "сума елементів " + i + "-го стовбця = " + suma + " > 1";
                }
            }
            return null;
        }

        private String methodSeidel() {
            String result = "---------- Метод Зейделя ----------\n";
            double[] temp = new double[x.length];
            result += "--- початкове значення x[i] ---\n";
            for (int i = 0; i < x.length; i++) {
                x[i] = a[i][a[i].length - 1];
                result += "\tx[" + i + "] = " + x[i] + "\n";
            }
            boolean flag = true;
            int iteration = 1;
            result += "\n--- початок роботи методу ---\n";
            while (flag) {
                result += "--- ітерація " + iteration + " ---\n";
                for (int i = 0; i < temp.length; i++) {
                    temp[i] = 0.0;
                    for (int j = 0; j < x.length; j++) {
                        temp[i] += a[i][j] * x[j];
                    }
                    temp[i] += a[i][a[i].length - 1];
                    result += "\tx[" + i + "] = " + temp[i] + "\n";
                }
                for (int i = 0; i < temp.length; i++) {
                    if (Math.abs(x[i] - temp[i]) > accuracy) {
                        break;
                    }
                    if (i == temp.length - 1) {
                        result += "\n--- виконалась умова виходу ---\n\tx(current)[i] - x(preview)[i] <= e\n\tкількість ітерацій: " + iteration + "\n";
                        flag = false;
                    }
                }
                for (int i = 0; i < x.length; i++) {
                    x[i] = temp[i];
                }
                iteration++;
            }
            result += "\n--- результати ---\n";
            for (int i = 0; i < x.length; i++) {
                result += "\tx[" + i + "] = " + x[i] + "\n";
            }
            return result;
        }

        private String methodJacobi() {
            String convergence = convergence();
            int step = -1;
            if (convergence != null) {
                step = 3;
            }
            String result = "---------- Метод послідовних наближень (метод Якобі) ----------\n";
            double[] temp = new double[x.length];
            result += "--- початкове значення x[i] ---\n";
            for (int i = 0; i < x.length; i++) {
                x[i] = a[i][a[i].length - 1];
                result += "\tx[" + i + "] = " + x[i] + "\n";
            }
            boolean flag = true;
            int iteration = 1;
            result += "\n--- початок роботи методу ---\n";
            while (flag && (step != 0)) {
                result += "--- ітерація " + iteration + " ---\n";
                for (int i = 0; i < temp.length; i++) {
                    double tmp = 0.0;
                    for (int j = 0; j < x.length; j++) {
                        tmp += a[i][j] * x[j];
                    }
                    tmp += a[i][a[i].length - 1];
                    x[i] = tmp;
                    result += "\tx[" + i + "] = " + x[i] + "\n";
                }
                for (int i = 0; i < temp.length; i++) {
                    if (Math.abs(x[i] - temp[i]) > accuracy) {
                        break;
                    }
                    if (i == temp.length - 1) {
                        result += "\n--- виконалась умова виходу ---\n\tx(current)[i] - x(preview)[i] <= e\n\tкількість ітерацій: " + iteration + "\n";
                        flag = false;
                    }
                }
                for (int i = 0; i < x.length; i++) {
                    temp[i] = x[i];
                }
                iteration++;
                step --;
            }
            result += "\n--- результати ---\n";
            for (int i = 0; i < x.length; i++) {
                result += "\tx[" + i + "] = " + x[i] + "\n";
            }
            return result;
        }

        public double[] getResult() {
            return x;
        }

    }

}