package ua.feo.app.inf.task;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.util.Pair;
import javafx.util.converter.DoubleStringConverter;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Target14 implements TargetInf {

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
        return "Побудова кривої Безьє";
    }

    @Override
    public List<String> getTestCaseList() {
        return new ArrayList<>(data.keySet());
    }

    @Override
    public Map<String, Boolean> getEtalonTestCaseList() {
        return data;
    }

    @FXML private Canvas canvas;
    @FXML private ImageView imageView;
    @FXML private Label messageLabel;
    @FXML private Button setButton;
    @FXML private Button cancelButton;
    @FXML private AnchorPane helpPane;

    private boolean studyMode;
    private int studyStep;

    private Point[] points = new Point[] {
            new Point(0,0),
            new Point(1,2),
            new Point(2,2),
            new Point(3,-10)
    };

    @FXML
    public void initialize() {
        try {
            studyMode = true;
            //imageView.setImage(new Image("resources/images/1.png"));
            messageLabel.setText("Натисніть кнопку \"Задати точки\"");
            studyStep = 0;
            drawAxis(10);

            table.setItems(list);
            table.setEditable(true);
            xColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getX()));
            yColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getY()));
            xColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
            yColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
            xColumn.setOnEditCommit(t -> (t.getTableView().getItems().get(t.getTablePosition().getRow())).setX(t.getNewValue()));
            yColumn.setOnEditCommit(t -> (t.getTableView().getItems().get(t.getTablePosition().getRow())).setY(t.getNewValue()));
        } catch (Exception e) {

        }
    }

    @FXML
    private void setButtonClick() {
        try {
            if (studyMode && studyStep != 0) {
                return;
            }
            studyStep++;
            //imageView.setImage(new Image("resources/images/2.png"));
            messageLabel.setText("Введіть координати тогчок в таблиця та натисніть \"ОК\"");
            Dialog dialog = new Dialog();
            dialog.setTitle("Про автора");
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getClassLoader().getResource("fxml/task/Target13_TableWindow.fxml"));
            DialogPane root = loader.load();
            root.getButtonTypes().add(new ButtonType("OK", ButtonBar.ButtonData.OK_DONE));
            Target14 controller = loader.getController();
            controller.setPoints(this.points);
            dialog.setDialogPane(root);
            dialog.showAndWait();
            this.points = controller.getPoints();
            draw(controller.getPoints(), new Bezier(this.points).getBezierPoints(100));
            if (studyMode && studyStep != 1) {
                return;
            }
            studyStep++;
            imageView.setImage(new Image("resources/images/3.png"));
            messageLabel.setText("Перегляньте відомості про програму та про її автора");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void cancelButtonClick() {
        if (studyMode) {
            if (studyMode && studyStep != 3) {
                return;
            }
            studyStep = 0;
            imageView.setVisible(false);
            messageLabel.setVisible(false);
            cancelButton.setText("Підказки");
        } else {
            imageView.setVisible(true);
            messageLabel.setVisible(true);
            cancelButton.setText("Закрити");
            imageView.setImage(new Image("resources/images/1.png"));
            messageLabel.setText("Натисніть кнопку \"Задати точки\"");
            studyStep = 0;
        }
        studyMode = !studyMode;
    }


    private void draw(Point[] points, Point[] bPoints) {
        canvas.getGraphicsContext2D().clearRect(0,0, canvas.getWidth(), canvas.getHeight());
        double max = Arrays.stream(points).map(p -> (Math.abs(p.getX()) > Math.abs(p.getY())) ? Math.abs(p.getX()) : Math.abs(p.getY())).max(Comparator.comparingDouble(d -> d)).get();
        max *= 1.05;
        drawAxis((int) Math.floor(max));
        drawPoints((int) Math.floor(max), points);
        drawBezierPoints((int) Math.floor(max), bPoints);
    }

    private void drawAxis(int max) {
        double h = canvas.getHeight();
        double w = canvas.getWidth();
        double h2 = h / 2.;
        double w2 = w / 2.;

        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setLineWidth(1);
        gc.setStroke(Color.GRAY);

        gc.strokeLine(0, h2, w, h2);
        gc.strokeLine(w2, 0, w2, h);

        gc.strokeLine(w, h2, w - 10, h2 - 5);
        gc.strokeLine(w, h2, w - 10, h2 + 5);
        gc.strokeLine(w2, 0, w2 - 5, 10);
        gc.strokeLine(w2, 0, w2 + 5, 10);
    }

    private void drawPoints(int max, Point[] points) {
        double h = canvas.getHeight();
        double w = canvas.getWidth();
        double h2 = h / 2.;
        double w2 = w / 2.;

        double step = (h > w) ? h / max : w / max;
        step /= 2;

        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setLineWidth(1);
        gc.setStroke(Color.BLUE);
        for (int i = 1; i < points.length; i++) {
            gc.strokeLine(w2 + step * points[i].getX(), -1 * step * points[i].getY() + h2, w2 + step * points[i - 1].getX(), -1 * step * points[i - 1].getY() + h2);
        }
    }

    private void drawBezierPoints(int max, Point[] bPoints) {
        double h = canvas.getHeight();
        double w = canvas.getWidth();
        double h2 = h / 2.;
        double w2 = w / 2.;

        double step = (h > w) ? h / max : w / max;
        step /= 2;

        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setLineWidth(1);
        gc.setStroke(Color.GREEN);
        for (int i = 1; i < bPoints.length; i++) {
            gc.strokeLine(w2 + step * bPoints[i].getX(), -1 * step * bPoints[i].getY() + h2, w2 + step * bPoints[i - 1].getX(), -1 * step * bPoints[i - 1].getY() + h2);
        }
    }

    @FXML
    private void clickMenu(ActionEvent event) {
        if (studyMode && studyStep != 2) {
            return;
        }
        studyStep++;
        imageView.setImage(new Image("resources/images/4.png"));
        messageLabel.setText("Навчання завершено. Ви можете закрити підказки. Для подальшого відкриття підказок, натисніть \"Підказки\"");
        MenuItem source = (MenuItem) event.getSource();
        switch (source.getText()) {
            case "Про програму":
                aboutProgramCommand();
                break;
            case "Про автора":
                aboutAuthorCommand();
                break;
            case "Вийти":
                System.exit(0);
                break;
        }
    }

    private void aboutAuthorCommand() {
        try {
            Dialog dialog = new Dialog();
            dialog.setTitle("Про автора");
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getClassLoader().getResource("fxml/task/Target14_WindowDialog.fxml"));
            DialogPane root = loader.load();
            root.getButtonTypes().add(new ButtonType("Закрити", ButtonBar.ButtonData.CANCEL_CLOSE));
            Target14 controller = loader.getController();
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
    }

    private void aboutProgramCommand() {
        try {
            Dialog dialog = new Dialog();
            dialog.setTitle("Про програму");
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getClassLoader().getResource("fxml/task/Target14_WindowDialog.fxml"));
            DialogPane root = loader.load();
            root.getButtonTypes().add(new ButtonType("Закрити", ButtonBar.ButtonData.CANCEL_CLOSE));
            Target14 controller = loader.getController();
            controller.setHeader("Лабораторна робота 5 з \"Комп'ютерної графіки\"");
            controller.setText("Версія:\t1.0" +
                    "\nВи використовуєте актуальну версію програми.\n" +
                    "\n" +
                    "Розробник:\tДубень Богдан Іванович\n" +
                    "\n" +
                    "Використання програми:\n" +
                    "\tНатисність \'Встановити точки\' та оберіть необхідне точки.\n" +
                    "\tДля закриття навчання натисніть \'Закрити\'.\n" +
                    "\tДля відкриття підказок, натисніть \'Підказки\'.\n" +
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
    }

    @FXML private TableView<Point> table;
    @FXML private TableColumn<Point, Double> xColumn;
    @FXML private TableColumn<Point, Double> yColumn;

    private ObservableList<Point> list = FXCollections.observableArrayList();

    public void setPoints(Point[] points) {
        list.addAll(points);
    }

    @FXML
    public void addButtonClick() {
        list.add(new Point(0,0));
        table.refresh();
    }

    @FXML
    public void deleteButtonClick() {
        table.getSelectionModel().getSelectedItems().forEach(item -> list.remove(item));
        table.refresh();
    }

    @FXML
    public void upButtonClick() {
        int index = table.getSelectionModel().getSelectedIndex();
        if (index == 0) {
            return;
        }
        Point pointA = list.get(index);
        Point pointB = list.get(index - 1);
        list.set(index - 1, pointA);
        list.set(index, pointB);
    }

    @FXML
    public void downButtonClick() {
        int index = table.getSelectionModel().getSelectedIndex();
        if (index == list.size() - 1) {
            return;
        }
        Point pointA = list.get(index);
        Point pointB = list.get(index + 1);
        list.set(index + 1, pointA);
        list.set(index, pointB);
    }

    public Point[] getPoints() {
        return table.getItems().toArray(new Point[table.getItems().size()]);
    }

    @FXML private Label header;
    @FXML private TextArea text;

    public void setHeader(String text) {
        this.header.setText(text);
    }

    public void setText(String text) {
        this.text.setText(text);
    }

    public static void showError(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    class Bezier {

        private final Point[] points;
        private final List<Point> bPoints;

        public Bezier(Point[] points) {
            this.points = points;
            bPoints = new ArrayList<>();
        }

        private void getBezierCurve(int step) {
            double t;
            for (int j = 0; j < step; j++) {
                t = 1. * j / step;
                if (t > 1) {
                    t = 1;
                }
                double x = 0;
                double y = 0;

                for (int i = 0; i < points.length; i++) {
                    double b = getBezierBasis(i, points.length - 1, t);
                    x += points[i].getX() * b;
                    y += points[i].getY() * b;
                }
                bPoints.add(new Point(x, y));
            }
        }

        private double getBezierBasis(int i, int n, double t) {
            return (f(n) / (f(i) * f(n - i))) * Math.pow(t, i) * Math.pow(1 - t, n - i);
        }

        private double f(int n) {
            return (n <= 1) ? 1 : n * f(n - 1);
        }

        public Point[] getBezierPoints(int count) {
            getBezierCurve(count);
            return bPoints.toArray(new Point[bPoints.size()]);
        }

    }

    class Point {

        private SimpleDoubleProperty x;
        private SimpleDoubleProperty y;

        public Point(double x, double y) {
            this.x = new SimpleDoubleProperty(x);
            this.y = new SimpleDoubleProperty(y);
        }

        public double getX() {
            return x.get();
        }

        public void setX(double x) {
            this.x.set(x);
        }

        public double getY() {
            return y.get();
        }

        public void setY(double y) {
            this.y.set(y);
        }

    }



}
