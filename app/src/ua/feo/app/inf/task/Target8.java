package ua.feo.app.inf.task;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.util.Pair;

import java.util.*;
import java.util.stream.Collectors;

public class Target8 implements TargetInf {

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
        return "Перевизначені системи рівнянь";
    }

    @Override
    public List<String> getTestCaseList() {
        return new ArrayList<>(data.keySet());
    }

    @Override
    public Map<String, Boolean> getEtalonTestCaseList() {
        return data;
    }



    @FXML private TextField command;
    @FXML private TextArea log;
    private int step;
    private double[][] a;
    private double accuracy;
    private Method method;

    private int countB;
    private int countX;

    @FXML
    public void initialize() {
        step = -1;
        log.appendText("введіть кількість рівнянь\n");
        command.setText("4");
    }

    private void clearFields() {
        command.setText("");
    }

    @FXML
    public void onAction() {
        String str = command.getText();
        log.appendText("user say > " + str + "\n");
        try {
            switch (step) {
                case 0:
                    countB = Integer.parseInt(str);
                    log.appendText("введіть кількість невідомих\n");
                    command.setText("3");
                    break;
                case 1:
                    countX = Integer.parseInt(str);
                    a = new double[countB][countX + 1];
                    log.appendText("введіть елементи матриці через пробіл\n");
//                    command.setText("0.17 0.31 -0.18 0.22 -1.71 -0.21 0 0.33 0.22 0.62 0.32 -0.18 0.05 -0.19 -0.89 0.12 0.28 -0.14 0 0.94 0.34 0.62 -0.36 0.44 -3.42");
                    command.setText("110.13 37.59 -67.27 202.56 37.59 117.47 -16.43 140.46 -67.27 -16.43 65.10 -148.58 -35.51 -23.22 24.17 -133.96");
                    break;
                case 2:
                    String s1;
                    str += " ";
                    for (int i = 0; i < a.length; i++) {
                        for (int j = 0; j < a[i].length; j++) {
                            int k = str.indexOf(' ');
                            s1 = str.substring(0, k);
                            str = str.substring(k + 1);
                            a[i][j] = Double.parseDouble(s1);
                        }
                    }
                    method = new Method(a);
                    log.appendText(method.work() + "\n");
                    log.appendText(Arrays.toString(method.getResult()) + "\n");
                    break;
                default:
                    log.setText("");
                    initialize();
            }
            step++;
        } catch (Exception ex) {
            ex.printStackTrace();
            this.log.appendText("Error 1: некоректні дані. поле не може бути порожнім.\n");
        }
    }

    @FXML
    public void clearLog() {
        log.setText("");
    }
    class Method {

        private final double[][] a;
        private double[] x;

        public Method(double[][] a) {
            this.a = a;
            this.x = new double[a.length - 1];
        }

        public String work() {
            String result = "\n---------- Найменші квадрати ----------\n";
            result += "\n--- A ---\n" + array2ToString(a);
            double[] b = new double[a.length];
            for (int i = 0; i < b.length; i++) {
                b[i] = -1 * a[i][a.length - 1];
                a[i][a.length - 1] = -1 * a[i][a.length - 1];
            }
            result += "\n--- A ---\n" + array2ToString(a);
            result += "\n--- B ---\n" + arrayToString(b);
            double[][] n = new double[a.length][a.length];
            for (int i = 0; i < n.length; i++) {
                for (int j = 0; j < n[i].length; j++) {
                    for (int k = 0; k < a.length; k++) {
                        n[i][j] += a[i][k] * a[k][j];
                    }
                }
            }
            result += "\n\n--- N = A T A ---\n" + array2ToString(n);
            double[] c = new double[a.length];
            for (int i = 0; i < n.length; i++) {
                for (int j = 0; j < a.length; j++) {
                    c[i] += a[i][j] * b[j];
                }
            }
            result += "\n--- C = A T B ---\n" + arrayToString(c);
            double[][] newA = new double[n.length][n[0].length + 1];
            for (int i = 0; i < newA.length; i++) {
                for (int j = 0; j < newA[i].length - 1; j++) {
                    newA[i][j] = n[i][j];
                }
                newA[i][newA[i].length - 1] = b[i];
            }
            result += "\n\n--- A ---\n" + array2ToString(newA);
            result += methodJacobi(newA ,0.1);
            return result;
        }

        private String array2ToString(double[][] array) {
            String result = "";
            for (double[] t : array) {
                result += "\t" + arrayToString(t) + "\n";
            }
            return result;
        }

        private String arrayToString(double[] array) {
            String result = "";
            for (double t : array) {
                result += t + " ";
            }
            return result;
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

        private String methodJacobi(double[][] a, double accuracy) {
            String result = "--- Розв'язання СЛАР (nxn) ---\n";
            String convergence = convergence();
            int step = -1;
            if (convergence != null) {
                step = 3;
            }
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
                if (iteration == 27) {
                    break;
                }
                iteration++;
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
