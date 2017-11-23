package ua.feo.app.inf.task;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.util.Pair;

import java.util.*;
import java.util.stream.Collectors;

public class Target3 implements TargetInf {

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
        return "Чисельні методи розв’язування системи лінійних рівнянь";
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
    @FXML private TextField a11Field;
    @FXML private TextField a12Field;
    @FXML private TextField a13Field;
    @FXML private TextField a21Field;
    @FXML private TextField a22Field;
    @FXML private TextField a23Field;
    @FXML private TextField a31Field;
    @FXML private TextField a32Field;
    @FXML private TextField a33Field;
    @FXML private TextField b1Field;
    @FXML private TextField b2Field;
    @FXML private TextField b3Field;
    @FXML private Label x1Field;
    @FXML private Label x2Field;
    @FXML private Label x3Field;
    @FXML private TextArea logText;

    @FXML
    public void initialize() {
        this.method.setItems(FXCollections.observableArrayList(
                "Метод Гауса",
                "Метод Гауса з вибором головного елемента",
                "Метод LU - розкладу"
        ));
        clearFields();
    }

    private void clearFields() {
        method.getSelectionModel().selectFirst();
        a11Field.setText("0.32");
        a12Field.setText("-0.42");
        a13Field.setText("0.85");

        a21Field.setText("0.63");
        a22Field.setText("-1.43");
        a23Field.setText("-0.58");

        a31Field.setText("0.84");
        a32Field.setText("-2.23");
        a33Field.setText("-0.52");

        b1Field.setText("1.32");
        b2Field.setText("-0.44");
        b3Field.setText("0.64");
    }

    @FXML
    public void onAction() {
        try {
            double[][] a = new double[][]{
                    { Double.parseDouble(a11Field.getText()), Double.parseDouble(a12Field.getText()), Double.parseDouble(a13Field.getText()), },
                    { Double.parseDouble(a21Field.getText()), Double.parseDouble(a22Field.getText()), Double.parseDouble(a23Field.getText()), },
                    { Double.parseDouble(a31Field.getText()), Double.parseDouble(a32Field.getText()), Double.parseDouble(a33Field.getText()), },
            };
            double[] b = new double[] {
                    Double.parseDouble(b1Field.getText()),
                    Double.parseDouble(b2Field.getText()),
                    Double.parseDouble(b3Field.getText()),
            };
            Method method = new Method(a, b);
            this.logText.setText(method.work(this.method.getSelectionModel().getSelectedItem()));
            double[] result = method.getResult();
            x1Field.setText("x1 = " + result[0]);
            x2Field.setText("x2 = " + result[1]);
            x3Field.setText("x3 = " + result[2]);
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
        private final double[] b;
        private double[] x;

        public Method(double[][] a, double[] b) {
            this.a = a;
            this.b = b;
            this.x = new double[a.length];
        }

        public String work(String item) {
            switch (item) {
                case "Метод Гауса":
                    return methodGauss();
                case "Метод Гауса з вибором головного елемента":
                    return methodGaussMainElementOfChoice();
                case "Метод LU - розкладу":
                    return methodLUDecomposition();
                default:
                    return "Error 0: вибраний метод не підтримується.";
            }
        }

        private String methodGauss() {
            String result = "---------- Метод Гауса ----------\n";
            for (int i = 0; i < a.length; i++) {
                result += "\t" + Arrays.toString(a[i]) + " = [" + b[i] + "]\n";
            }
            result += "\n";
            result += "--- прямий хід ---\n";
            for (int head = 0; head < a.length; head++) {
                result += "--- елемент a[" + head + "][" + head + "]---\n";
                for (int row = head + 1; row < a.length; row++) {
                    result += "\t---" + row + " рядок---\n";
                    if (a[row][head] == 0 || a[head][head] == 0) {
                        result += "\tзаміна рядків : " + row + " & " + (row + 1) + "\n";
                        double[] tmp = a[row];
                        a[row] = a[row+1];
                        a[row+1] = tmp;
                    }
                    double m = a[row][head] / a[head][head];
                    result += "\tдільник рядка : " + m + "\n";
                    for (int column = head; column < a.length; column++) {
                        a[row][column] -= a[head][column] * m;
                    }
                    b[row] -= b[head] * m;
                    for (int i = 0; i < a.length; i++) {
                        result += "\t" + Arrays.toString(a[i]) + " = [" + b[i] + "]\n";
                    }
                    result += "\n";
                }
            }
            result += "\n--- зворотній хід ---\n";
            for (int i = a.length - 1; i >= 0; i--) {
                double s = 0;
                for (int j = i + 1; j < a.length; j++) {
                    s += a[i][j] * x[j];
                }
                x[i] = (b[i] - s) / a[i][i];
                result += "\tx[" + i + "] = " + "(" + b[i] + " - " + s + ") / " + a[i][i] + " = " + x[i] + "\n";
            }
            return result;
        }

        private String methodGaussMainElementOfChoice() {
            String result = "---------- Метод Гауса з вибором головного елемента ----------\n";for (int i = 0; i < a.length; i++) {
                result += "\t" + Arrays.toString(a[i]) + " = [" + b[i] + "]\n";
            }
            result += "\n";
            result += "--- прямий хід ---\n";
            for (int head = 0; head < a.length; head++) {
                result += "--- елемент a[" + head + "][" + head + "]---\n";
                for (int row = head + 1; row < a.length; row++) {
                    result += "\t---" + row + " рядок---\n";
                    int maxRow = row;
                    for (int i = row; i< a.length; i++) {
                        if (Math.abs(a[i][head]) > Math.abs(a[maxRow][head])) {
                            maxRow = i;
                        }
                    }
                    if (maxRow != row) {
                        result += "\tзаміна рядків : " + row + " & " + maxRow + "\n";
                        double[] tmp = a[row];
                        a[row] = a[maxRow];
                        a[maxRow] = tmp;
                    }
                    double m = a[row][head] / a[head][head];
                    result += "\tдільник рядка : " + m + "\n";
                    for (int column = head; column < a.length; column++) {
                        a[row][column] -= a[head][column] * m;
                    }
                    b[row] -= b[head] * m;
                    for (int i = 0; i < a.length; i++) {
                        result += "\t" + Arrays.toString(a[i]) + " = [" + b[i] + "]\n";
                    }
                    result += "\n";
                }
            }
            result += "\n--- зворотній хід ---\n";
            for (int i = a.length - 1; i >= 0; i--) {
                double s = 0;
                for (int j = i + 1; j < a.length; j++) {
                    s += a[i][j] * x[j];
                }
                x[i] = (b[i] - s) / a[i][i];
                result += "\tx[" + i + "] = " + "(" + b[i] + " - " + s + ") / " + a[i][i] + " = " + x[i] + "\n";
            }
            return result;
        }

        private void print(double[][] a) {
            for (double[] b : a) {
                for (double c : b) {
                    System.out.print(c + " ");
                }
                System.out.println();
            }
            System.out.println();
        }

        private void print(double[] a) {
            for (double b : a) {
                System.out.print(b + " ");
            }
            System.out.println();
            System.out.println();
        }

        private String methodLUDecomposition() {
            String result = "---------- Метод LU - розкладу ----------\n";
            double[][] l = new double[a.length][a.length];
            double[][] u = new double[a.length][a.length];
            double[] y = new double[a.length];
            result += "\n--- прямий хід ---\n";
            result += "\n--- 1 етап розрахунків ---\n";
            for (int i = 0; i < a.length; i++){
                for (int j=0; j < a.length; j++){
                    l[i][j] = 0;
                    u[i][j] = 0;
                    result += "\tl[" + i + "][" + j + "] = " + 0 + "    ";
                    result += "\tu[" + i + "][" + j + "] = " + 0 + "\n";
                    result +=  "#L\n" + arrayToString(l) + "\n";
                    result += "#U\n" + arrayToString(u) + "\n";
                }
                y[i] = 0;
                x[i] = 0;
                u[i][i] = 1;
                result += "\ty[" + i + "] = " + 0 + "    ";
                result += "\tx[" + i + "] = " + 0 + "    ";
                result += "\tu[" + i + "] = " + 1 + "\n";
                result +=  "#L\n" + arrayToString(l) + "\n";
                result += "#U\n" + arrayToString(u) + "\n";
            }
            result += "\n--- 2 етап розрахунків ---\n";
            for (int i = 0; i < a.length; i++) {
                for (int j = 0; j < a.length; j++) {
                    double sum = 0;
                    if (i >= j) {
                        for (int s = 0; s <= j - 1; s++) {
                            sum += (l[i][s]) * (u[s][j]);
                        }
                        l[i][j] = a[i][j] - sum;
                        result += "\tl[" + i + "][" + j + "] = " + a[i][j] + " - " + sum + " = " + l[i][j] + "\n";
                        result +=  "#L\n" + arrayToString(l) + "\n";
                    } else {
                        for (int s = 0; s <= i - 1; s++) {
                            sum += l[i][s] * u[s][j];
                        }
                        u[i][j] = (a[i][j] - sum) / l[i][i];
                        result += "\tu[" + i + "][" + j + "] = (" + a[i][j] + " - " + sum + ") / " + l[i][i] + " = " + u[i][j] + "\n";
                        result += "#U\n" + arrayToString(u) + "\n";
                    }
                }
            }
            result += "\n--- зворотній хід ---\n";
            y[0] = b[0] / l[0][0];
            result += "\n--- результати y ---\n";
            result += "\ty[0] = " + b[0] + " / " + l[0][0] + " = " + y[0] + "\n";
            for (int i = 1;  i< a.length; i++){
                double sum = 0;
                for (int j = 0; j < i; j++) {
                    sum += l[i][j] * y[j];
                }
                y[i] = (b[i] - sum) / l[i][i];
                result += "\ty[" + i + "] = (" + b[i] + " - " + sum + ") / " + l[i][i] + " = " + y[i] + "\n";
            }
            x[a.length - 1] = y[a.length - 1];
            result += "\n--- результати x ---\n";
            result += "\tx[" + (a.length - 1) + "] = " + y[a.length - 1] + "\n";
            for (int i = a.length - 2; i >= 0; i--){
                double sum = 0;
                for (int j = a.length - 1; j >= i + 1; j--) {
                    sum += x[j] * u[i][j];
                }
                x[i] = y[i] - sum;
                result += "\tx[" + i + "] = " + y[i] + " - " + sum + " = " + x[i] + "\n";
            }
            return result;
        }

        private String arrayToString(double[][] array) {
            String result = "";
            for (double[] a : array) {
                for (double b : a) {
                    result += b + " ";
                }
                result += "\n";
            }
            return result;
        }

        public double[] getResult() {
            return x;
        }

    }


}