package ua.feo.app.inf.task;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.*;

public class Target1 implements TargetInf {

    private final static HashMap<String, Boolean> data;

    static {
        data = new HashMap<String, Boolean>(){{
            put("ArithmeticException", false);
            put("Помилка вводу в поле A", false);
            put("Коректність написів", true);
            put("Ігнорування даних поля A", true);
            put("Помилка при введенні від'ємних чисел", false);
            put("Опрацювання помилок", true);
            put("Помилка при введенні від'ємних чисел", false);
        }};
    }

    @Override
    public String getName() {
        return "Трьохвимірна гратка";
    }

    @Override
    public List<String> getTestCaseList() {
        return new ArrayList<>(data.keySet());
    }

    @Override
    public Map<String, Boolean> getEtalonTestCaseList() {
        return data;
    }


    @FXML private TextField aField;
    @FXML private TextField eField;
    @FXML private Canvas canvas;

    @FXML private CheckBox basic;
    @FXML private CheckBox diagonals;
    @FXML private CheckBox lines;
    @FXML private CheckBox points;
    @FXML private CheckBox labels;

    @FXML
    public void initialize() {
        aField.setText("x1 x2 x3");
        eField.setText("0.0 0.5 1.0");
        basic.setSelected(true);
        diagonals.setSelected(false);
        lines.setSelected(false);
        points.setSelected(true);
        labels.setSelected(false);
    }

    @FXML
    public void onAction() {
        try {
            String aString = aField.getText() + " ";
            ArrayList<String> aList = new ArrayList<>();
            while (aString.length() != 0) {
                int i = aString.indexOf(" ");
                aList.add(aString.substring(0, i));
                aString = aString.substring(i + 1);
            }
            String eString = eField.getText() + " ";
            ArrayList<Double> eList = new ArrayList<>();
            while (eString.length() != 0) {
                int i = eString.indexOf(" ");
                try {
                    double d = Double.parseDouble(eString.substring(0, i));
                    if (d < 0 || d > 1) {
                        throw new Exception();
                    }
                    eList.add(d);
                } catch (Exception e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Введено некоректні дані");
                    alert.setContentText("Вирішення проблеми:\n\tВведіть коректні дані");
                    alert.showAndWait();
                    return;
                }
                eString = eString.substring(i + 1);
            }
            String[] a = aList.toArray(new String[aList.size()]);
            Double[] e = eList.toArray(new Double[eList.size()]);
            List<PairSet> work = new Worker().getWork(a, e);
            print(work, a, e);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void print(List<PairSet> work, String[] a, Double[] e) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, 400, 400);
        gc.setFont(new Font(9));
        int x;
        int y;
        int max;
        if (a.length == 2) {
            x = 100;
            y = 100;
            max = 200;
            if (diagonals.isSelected()) {
                gc.setStroke(Color.LIGHTGREEN);
                gc.setLineWidth(1);
                gc.strokeLine(x, y, x + max, y + max);
                gc.strokeLine(x + max, y, x, y + max);
            }
            if (lines.isSelected()) {
                gc.setStroke(Color.LIGHTGREEN);
                gc.setLineWidth(1);
                for (double d : e) {
                    gc.strokeLine(x + max * d, y, x + max * d, y + max);
                    gc.strokeLine(x, y + max * d, x + max, y + max * d);
                }
            }
            if (basic.isSelected()) {
                gc.setStroke(Color.GREEN);
                gc.setLineWidth(1);
                gc.strokeRect(x, y, max, max);
            }
            if (points.isSelected()) {
                gc.setFill(Color.BLACK);
                gc.setLineWidth(1);
                double x1;
                double y1;
                for (PairSet p : work) {
                    x1 = x + max * p.pairs[0].value;
                    y1 = y + max * p.pairs[1].value;
                    gc.fillOval(x1 - 3, y1 - 3, 6, 6);
                }
            }
            if (labels.isSelected()) {
                gc.setStroke(Color.BLACK);
                gc.setLineWidth(1);
                double x1;
                double y1;
                for (PairSet p : work) {
                    x1 = x + max * p.pairs[0].value;
                    y1 = y + max * p.pairs[1].value;
                    gc.strokeText(p.shortString(), x1 - 30, y1 - 10);
                }
            }
        } else if (a.length == 3) {
            x = 50;
            y = 50;
            int maxZ = 100;
            max = 200;
            if (diagonals.isSelected()) {
                gc.setStroke(Color.LIGHTGREEN);
                gc.setLineWidth(1);
                gc.strokeLine(x + max, y, x, y + max);
                gc.strokeLine(x, y, x + max, y + max);
                gc.strokeLine(x, y, x + maxZ, y + max + maxZ);
                gc.strokeLine(x, y, x + max + maxZ, y + maxZ);
                gc.strokeLine(x + max, y, x + maxZ, y + maxZ);
                gc.strokeLine(x, y + max, x + maxZ, y + maxZ);
                gc.strokeLine(x + max + maxZ, y + maxZ, x + maxZ, y + max + maxZ);
                gc.strokeLine(x + maxZ, y + maxZ, x + max + maxZ, y + max + maxZ);
                gc.strokeLine(x + max, y, x + max + maxZ, y + max + maxZ);
                gc.strokeLine(x, y + max, x + max + maxZ, y + max + maxZ);
                gc.strokeLine(x + max, y + max, x + maxZ, y + max + maxZ);
                gc.strokeLine(x + max, y + max, x + max + maxZ, y + maxZ);
                gc.strokeLine(x, y, x + max + maxZ, y + max + maxZ);
                gc.strokeLine(x, y + max, x + max + maxZ, y + maxZ);
                gc.strokeLine(x + max, y, x + maxZ, y + max + maxZ);
                gc.strokeLine(x + max, y + max, x + maxZ, y + maxZ);
            }
            if (lines.isSelected()) {
                gc.setStroke(Color.LIGHTGREEN);
                gc.setLineWidth(1);
                for (double d : e) {
                    gc.strokeLine(x + max * d, y, x + max * d, y + max);
                    gc.strokeLine(x + max * d + maxZ, y + maxZ, x + max * d + maxZ, y + max + maxZ);
                    gc.strokeLine(x + max * d, y, x + max * d + maxZ, y + maxZ);
                    gc.strokeLine(x + max * d, y + max, x + max * d + maxZ, y + max + maxZ);
                    gc.strokeLine(x, y + max * d, x + max, y + max * d);
                    gc.strokeLine(x + maxZ, y + max * d + maxZ, x + max + maxZ, y + max * d + maxZ);
                    gc.strokeLine(x, y + max * d, x + maxZ,  y + max * d + maxZ);
                    gc.strokeLine(x + max, y + max * d, x + max + maxZ, y + max * d + maxZ);
                    gc.strokeLine(x + maxZ * d, y + maxZ * d, x + max + maxZ * d, y + maxZ * d);
                    gc.strokeLine(x + maxZ * d, y + max + maxZ * d, x + max + maxZ * d, y + max + maxZ * d);
                    gc.strokeLine(x + maxZ * d, y + maxZ * d, x + maxZ * d, y + max + maxZ * d);
                    gc.strokeLine(x + max + maxZ * d, y + maxZ * d, x + max + maxZ * d, y + max + maxZ * d);
                }
            }
            if (basic.isSelected()) {
                gc.setStroke(Color.GREEN);
                gc.setLineWidth(1);
                gc.strokeRect(x, y, max, max);
                gc.strokeRect(x + maxZ, y + maxZ, max, max);
                gc.strokeLine(x, y, x + maxZ, y + maxZ);
                gc.strokeLine(x + max, y, x + max + maxZ, y + maxZ);
                gc.strokeLine(x, y + max, x + maxZ, y + max + maxZ);
                gc.strokeLine(x + max, y + max, x + max + maxZ, y + max + maxZ);
            }
            if (points.isSelected()) {
                gc.setFill(Color.BLACK);
                gc.setLineWidth(1);
                double x1;
                double y1;
                for (PairSet p : work) {
                    x1 = x + max * p.pairs[0].value + maxZ * p.pairs[2].value;
                    y1 = y + max * p.pairs[1].value + maxZ * p.pairs[2].value;
                    gc.fillOval(x1 - 3, y1 - 3, 6, 6);
                }
            }
            if (labels.isSelected()) {
                gc.setStroke(Color.BLACK);
                gc.setLineWidth(1);
                double x1;
                double y1;
                for (PairSet p : work) {
                    x1 = x + max * p.pairs[0].value + maxZ * p.pairs[2].value;
                    y1 = y + max * p.pairs[1].value + maxZ * p.pairs[2].value;
                    gc.strokeText(p.shortString(), x1 - 30, y1 - 10);
                }
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error. Слоооооооожна");
            alert.setHeaderText("Не можу відобразити фігуру");
            alert.setContentText("Вирішення проблеми:\n\tВведіть коректні дані");
            alert.showAndWait();
            return;
        }
    }



    class Worker {

        public List<PairSet> getWork(String[] a, Double[] e) {
            List<PairSet> structure = new ArrayList<>((int) Math.pow(e.length, a.length));
            PairSet pair;
            for (int i = 0; i < (int) Math.pow(e.length, a.length); i++) {
                Pair[] pairs = new Pair[a.length];
                for (int j = 0; j < pairs.length; j++) {
                    pairs[j] = new Pair(a[j], e[get(i, j, e.length)]);
                }
                pair = new PairSet(pairs);
                structure.add(pair);
            }
            return structure;
        }

        public void printWork(String[] a, Double[] e) {
            String result = "--- Булеан нечіткої множини ---\n{\n";
            List<PairSet> work = getWork(a, e);
            for (PairSet p : work) {
                result += "\t" + p + "\n";
            }
            result += "}\n";
            System.out.println(result);
        }

        private int get(int num, int count, int length) {
            for (int i = 0; i < count; i++) {
                num /= length;
            }
            return num % length;
        }

    }

    class PairSet {

        public Pair[] pairs;

        public PairSet(Pair ... pair) {
            this.pairs = pair;
        }

        @Override
        public String toString() {
            String result = "{";
            for (Pair p : pairs) {
                result += p + ";";
            }
            return result.substring(0, result.length() - 1) + "}";
        }

        public String shortString() {
            String result = "(";
            for (Pair p : pairs) {
                result += p.value + ";";
            }
            return result.substring(0, result.length() - 1) + ")";
        }

    }

    class Pair {

        public String name;
        public double value;

        public Pair(String name, double value) {
            this.name = name;
            this.value = value;
        }

        @Override
        public String toString() {
            return "(" + name + ";" + value + ")";
        }

    }

}
