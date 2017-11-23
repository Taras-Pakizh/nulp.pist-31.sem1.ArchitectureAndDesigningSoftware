package ua.feo.app.inf.task;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.util.Pair;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Target12 implements TargetInf {

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
        return "Фрактальні зобаження";
    }

    @Override
    public List<String> getTestCaseList() {
        return new ArrayList<>(data.keySet());
    }

    @Override
    public Map<String, Boolean> getEtalonTestCaseList() {
        return data;
    }

    @FXML private TextField xField;
    @FXML private TextField yField;
    @FXML private TextField countField;
    @FXML private TextField sizeField;
    @FXML private Canvas canvas;

    private Stack<Triangle> stack = new Stack<>();
    private GraphicsContext gc;

    private int count;
    private Triangle root;

    private boolean inputError;

    @FXML
    public void initialize() {
        gc = canvas.getGraphicsContext2D();
        //test:
        {
            xField.setText("300");
            yField.setText("300");
            sizeField.setText("300");
            countField.setText("1");
            inputError = false;
        }
        //end test
    }

    @FXML
    private void clickClearCanvas() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    @FXML
    private void clickBuildFigure() {
        if (inputError) {
            new Dialog().showError("Помилка", "Невірні дані", "Введіть вірні дані");
            return;
        }
        stack = new Stack<>();
        double x = Double.parseDouble(xField.getText());
        double y = Double.parseDouble(yField.getText());
        if (x > canvas.getWidth() || y > canvas.getHeight()) {
            return;
        }
        double min = getMin();
        this.root = new Triangle(new Point(x - min, (y + min) * 0.95), new Point(x, (y - min) * 0.95), new Point(x + min, (y + min) * 0.95));
        stack.push(this.root);
        this.count = Integer.parseInt(countField.getText());
        if (count > 11) {
            count = 11;
        }
        draw(this.count);
    }

    private double getMin() {
        double x = Double.parseDouble(xField.getText());
        double y = Double.parseDouble(yField.getText());
        double d = Double.parseDouble(sizeField.getText());
        double min = Arrays.stream(new double[]{
                x, y, canvas.getWidth() - x, canvas.getHeight() - y, d
        }).min().getAsDouble();
        return min;
    }

    @FXML
    private void clickPreview() {
        clickClearCanvas();
        stack.clear();
        stack.push(root);
        count--;
        if (count < 1) {
            count = 1;
        }
        draw(count);
    }

    @FXML
    private void clickNext() {
        if (count > 11) {
            return;
        }
        count++;
        draw(1);
    }

    private void draw(int count) {
        if (count == 0) {
            return;
        }
        Stack<Triangle> newStack = new Stack<>();
        while (!stack.isEmpty()) {
            Triangle t = stack.pop();
            drawTriangle(t);
            Point p1 = t.getP1();
            Point p2 = t.getP2();
            Point p3 = t.getP3();
            newStack.push(new Triangle(p1, getMiddlePoint(p1, p2), getMiddlePoint(p1, p3)));
            newStack.push(new Triangle(getMiddlePoint(p2, p1), p2, getMiddlePoint(p2, p3)));
            newStack.push(new Triangle(getMiddlePoint(p3, p1), getMiddlePoint(p3, p2), p3));
        }
        while (!newStack.isEmpty()) {
            stack.push(newStack.pop());
        }
        draw(count - 1);
    }

    private Point getMiddlePoint(Point p1, Point p2) {
        return new Point(Math.abs(p1.getX() + p2.getX()) / 2., Math.abs(p1.getY() + p2.getY()) / 2.);
    }

    private void drawTriangle(Triangle t) {
        drawLine(t.getP1(), t.getP2());
        drawLine(t.getP2(), t.getP3());
        drawLine(t.getP3(), t.getP1());
    }

    private void drawLine(Point p1, Point p2) {
        gc.strokeLine(p1.getX(), p1.getY(), p2.getX(), p2.getY());
    }

    @FXML
    private void inputText(KeyEvent event) {
        TextField field = (TextField) event.getSource();
        try {
            double i = Double.parseDouble(field.getText());
            if (i < 0) {
                throw new IllegalArgumentException();
            }
            inputError = false;
            field.setStyle("-fx-text-inner-color: black;");
        } catch (IllegalArgumentException ex) {
            inputError = true;
            field.setStyle("-fx-text-inner-color: red;");
        }
    }

    @FXML
    private void clickMenu(ActionEvent event) {
        MenuItem source = (MenuItem) event.getSource();
        switch (source.getText()) {
            case "Про програму":
                try {
                    javafx.scene.control.Dialog dialog = new javafx.scene.control.Dialog();
                    dialog.setTitle("Про програму");
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(getClass().getClassLoader().getResource("fxml/task/Target12_WindowDialog.fxml"));
                    DialogPane root = loader.load();
                    root.getButtonTypes().add(new ButtonType("Закрити", ButtonBar.ButtonData.CANCEL_CLOSE));
                    Target12 controller = loader.getController();
                    controller.setHeader("Лабораторна робота 3 з \"Комп'ютерної графіки\"");
                    controller.setText("Версія:\t1.0" +
                            "\nВи використовуєте актуальну версію програми.\n" +
                            "\n" +
                            "Розробник:\tДубень Богдан Іванович\n" +
                            "\n" +
                            "Використання програми:\n" +
                            "\tОбираєте центр побудови фрактала, його розмір та кількість ітерацій.\n" +
                            "\tНатискаєте кнопку \'Побудова\'.\n" +
                            "\tДля зміни кількості ітерацій на один слугують кнопки \'<<\' та \'>>\'.\n" +
                            "\tДля очистки полотна натисніть кнопку \'Очистити полотно\'.\n" +
                            "\n" +
                            "Гарячі клавіші:\n" +
                            "\tctrl + Q - вихід\n" +
                            "\tctrl + P - про програму\n" +
                            "\tctrl + M - про автора\n" +
                            "");
                    dialog.setDialogPane(root);
                    dialog.showAndWait();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case "Про автора":
                try {
                    javafx.scene.control.Dialog dialog = new javafx.scene.control.Dialog();
                    dialog.setTitle("Про автора");
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(getClass().getClassLoader().getResource("fxml/task/Target12_WindowDialog.fxml"));
                    DialogPane root = loader.load();
                    root.getButtonTypes().add(new ButtonType("Закрити", ButtonBar.ButtonData.CANCEL_CLOSE));
                    Target12 controller = loader.getController();
                    controller.setHeader("Про автора");
                    controller.setText("Розробник:\tДубень Богдан Іванович\n" +
                            "студент групи ПІст-21 НУ ЛП\n" +
                            "\temail:\tFeodott@gmail.com\n" +
                            "\tтел.н.:\t+380664971511\n" +
                            "\tskype:\tfeodott\n" +
                            "\tgithub:\tfeodott\n" +
                            "\tgitlab:\tfeodott\n");
                    dialog.setDialogPane(root);
                    dialog.showAndWait();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case "Вийти":
                System.exit(0);
                break;
        }
    }

    @FXML private Label header;
    @FXML private TextArea text;

    public void setHeader(String text) {
        this.header.setText(text);
    }

    public void setText(String text) {
        this.text.setText(text);
    }

    class Dialog {

        public void showError(String title, String header, String content) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(header);
            alert.setContentText(content);
            alert.showAndWait();
        }

    }

    class Point {

        private final double x;
        private final double y;

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }

        public Point(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }

    class Triangle {

        private final Point p1;
        private final Point p2;
        private final Point p3;

        public Point getP1() {
            return p1;
        }

        public Point getP2() {
            return p2;
        }

        public Point getP3() {
            return p3;
        }

        public Triangle(Point p1, Point p2, Point p3) {
            this.p1 = p1;
            this.p2 = p2;
            this.p3 = p3;
        }

    }

}
