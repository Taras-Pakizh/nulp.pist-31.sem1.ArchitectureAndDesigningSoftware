package ua.feo.app.task;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class Target13 implements TargetInf {

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
        return "Колірні моделі та алгоритми їхнього перетворення";
    }

    @Override
    public List<String> getTestCaseList() {
        return new ArrayList<>(data.keySet());
    }

    @Override
    public Map<String, Boolean> getEtalonTestCaseList() {
        return data;
    }

    @FXML private Canvas originalCanvas;
    @FXML private Canvas canvas;
    @FXML private TextField rField;
    @FXML private TextField gField;
    @FXML private TextField bField;
    @FXML private TextField hField;
    @FXML private TextField sField;
    @FXML private TextField lField;
    @FXML private Slider rSlider;
    @FXML private Slider gSlider;
    @FXML private Slider bSlider;
    @FXML private Slider hSlider;
    @FXML private Slider sSlider;
    @FXML private Slider lSlider;


    private final FileChooser fileChooser = new FileChooser();
    private File currentFile;
    private boolean inputError;

    private final double[] coodr = new double[2];

    public void init() {
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("all", "*.*"),
                new FileChooser.ExtensionFilter("png", "*.png"),
                new FileChooser.ExtensionFilter("jpg", "*.jpg")
        );
        moveSlider();
    }

    @FXML
    private void inputText(KeyEvent event) {
        TextField field = (TextField) event.getSource();
        try {
            Integer.parseInt(field.getText());
            inputError = false;
            field.setStyle("-fx-text-inner-color: black;");
        } catch (IllegalArgumentException ex) {
            inputError = true;
            field.setStyle("-fx-text-inner-color: red;");
        }
        try {
            int i = Integer.parseInt(rField.getText());
            rSlider.setValue(i < -255 || i > 255 ? 0 : i);
            i = Integer.parseInt(gField.getText());
            gSlider.setValue(i < -255 || i > 255 ? 0 : i);
            i = Integer.parseInt(bField.getText());
            bSlider.setValue(i < -255 || i > 255 ? 0 : i);
            i = Integer.parseInt(hField.getText());
            hSlider.setValue(i < -360 || i > 360 ? 0 : i);
            i = Integer.parseInt(sField.getText());
            sSlider.setValue(i < -100 || i > 100 ? 0 : i);
            i = Integer.parseInt(lField.getText());
            lSlider.setValue(i < -100 || i > 100 ? 0 : i);
        } catch (IllegalArgumentException ex) {
        }
    }

    @FXML
    private void moveSlider() {
        rField.setText(Integer.toString((int) rSlider.getValue()));
        gField.setText(Integer.toString((int) gSlider.getValue()));
        bField.setText(Integer.toString((int) bSlider.getValue()));
        hField.setText(Integer.toString((int) hSlider.getValue()));
        sField.setText(Integer.toString((int) sSlider.getValue()));
        lField.setText(Integer.toString((int) lSlider.getValue()));
    }

    @FXML
    private void changeRGB() {
        double rValue = rSlider.getValue() / 255;
        double gValue = gSlider.getValue() / 255;
        double bValue = bSlider.getValue() / 255;
        double[] hsl = rgbTohsl(rValue, gValue, bValue);
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        WritableImage image = canvas.snapshot(params, null);
        PixelReader reader = image.getPixelReader();
        PixelWriter writer = image.getPixelWriter();
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                Color color = reader.getColor(x, y);
                double[] colorHSL = rgbTohsl(color.getRed(), color.getGreen(), color.getBlue());
                double l = colorHSL[2] + hsl[2];
                l =  l > 1 ? 1 : (l < 0 ? 0 : l);
                double[] rgb = hslToRgb(colorHSL[0], colorHSL[1], l);
                writer.setColor(x, y, Color.color(rgb[0], rgb[1], rgb[2]));
            }
        }
        canvas.getGraphicsContext2D().drawImage(image, 0, 0);
    }

    @FXML
    private void changeHSL() {
        double hValue = hSlider.getValue() / 360;
        double sValue = sSlider.getValue() / 100;
        double lValue = lSlider.getValue() / 100;
        double[] rgb = hslToRgb(hValue, sValue, lValue);
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        WritableImage image = canvas.snapshot(params, null);
        PixelReader reader = image.getPixelReader();
        PixelWriter writer = image.getPixelWriter();
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                Color color = reader.getColor(x, y);
                double b = color.getBlue() + rgb[2];
                b = b > 1 ? 1 : (b < 0 ? 0 : b);
                writer.setColor(x, y, Color.color(color.getRed(), color.getGreen(), b));
            }
        }
        canvas.getGraphicsContext2D().drawImage(image, 0, 0);
    }

    @FXML
    private void originalCanvasPressed(MouseEvent event) {
        coodr[0] = event.getSceneX();
        coodr[1] = event.getSceneY() - 216;
    }

    @FXML
    private void originalCanvasReleased(MouseEvent event) {
        double x1 = coodr[0];
        double y1 = coodr[1];
        double x2 = event.getSceneX();
        double y2 = event.getSceneY() - 216;
        if (x1 == x2 || y1 == y2 || x2 < 0 || y2 < 0) {
            return;
        }
        canvas.getGraphicsContext2D().clearRect(0, 0,canvas.getWidth(), canvas.getHeight());
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        WritableImage image = originalCanvas.snapshot(params, null);
        image = new WritableImage(image.getPixelReader(), (int) (x1 > x2 ? x2 : x1), (int) (y1 > y2 ? y2 : y1), (int) Math.abs(x2 - x1), (int) Math.abs(y2 - y1));
        double width;
        double height;
        if (image.getWidth() > image.getHeight()) {
            width = originalCanvas.getWidth();
            height = originalCanvas.getHeight() * image.getHeight() / image.getWidth();
        } else {
            width = originalCanvas.getWidth() * image.getWidth() / image.getHeight();
            height = originalCanvas.getHeight();
        }
        canvas.getGraphicsContext2D().drawImage(image, 0, 0, width, height);
    }

    @FXML
    private void clickMenu(ActionEvent event) {
        MenuItem source = (MenuItem) event.getSource();
        switch (source.getText()) {
            case "Відкрити":
                openFileCommand();
                break;
            case "Зберегти":
                saveFileCommand();
                break;
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
            javafx.scene.control.Dialog dialog = new javafx.scene.control.Dialog();
            dialog.setTitle("Про автора");
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getClassLoader().getResource("fxml/task/Target13_WindowDialog.fxml"));
            DialogPane root = loader.load();
            root.getButtonTypes().add(new ButtonType("Закрити", ButtonBar.ButtonData.CANCEL_CLOSE));
            Target13 controller = loader.getController();
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
            javafx.scene.control.Dialog dialog = new javafx.scene.control.Dialog();
            dialog.setTitle("Про програму");
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getClassLoader().getResource("fxml/task/Target13_WindowDialog.fxml"));
            DialogPane root = loader.load();
            root.getButtonTypes().add(new ButtonType("Закрити", ButtonBar.ButtonData.CANCEL_CLOSE));
            Target13 controller = loader.getController();
            controller.setHeader("Лабораторна робота 4 з \"Комп'ютерної графіки\"");
            controller.setText("Версія:\t1.0" +
                    "\nВи використовуєте актуальну версію програми.\n" +
                    "\n" +
                    "Розробник:\tДубень Богдан Іванович\n" +
                    "\n" +
                    "Використання програми:\n" +
                    "\tНатисність \'Відкрити\' та оберіть необхідне зображення.\n" +
                    "\tВиділіть прямокутну область на лівому зображенні для роботи з частиною зображення.\n" +
                    "\tЗмінюйте положення повзунків для зміни зображення.\n" +
                    "\tПідтвердіть зміни клавішами \'Застосувати\'.\n" +
                    "\tДля збереження зображення натисність \'Зберегти\'.\n" +
                    "\n" +
                    "Гарячі клавіші:\n" +
                    "\tctrl + O - відкрити зображення\n" +
                    "\tctrl + S - зберегти зобрнаження\n" +
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

    private void openFileCommand() {
        currentFile = fileChooser.showOpenDialog(null);
        if (currentFile != null) {
            originalCanvas.getGraphicsContext2D().clearRect(0, 0, originalCanvas.getWidth(), originalCanvas.getHeight());
            canvas.getGraphicsContext2D().clearRect(0, 0,canvas.getWidth(), canvas.getHeight());
            try {
                Image image = new Image("file:" + currentFile.getCanonicalPath());
                double width;
                double height;
                if (image.getWidth() > image.getHeight()) {
                    width = originalCanvas.getWidth();
                    height = originalCanvas.getHeight() * image.getHeight() / image.getWidth();
                } else {
                    width = originalCanvas.getWidth() * image.getWidth() / image.getHeight();
                    height = originalCanvas.getHeight();
                }
                originalCanvas.getGraphicsContext2D().drawImage(image, 0, 0, width, height);
                canvas.getGraphicsContext2D().drawImage(image, 0, 0, width, height);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            new Dialog().showError("Помилка 1", "Не можу відкрити файл", "Зробіть щось");
        }
    }

    private void saveFileCommand() {
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try {
                SnapshotParameters params = new SnapshotParameters();
                params.setFill(Color.TRANSPARENT);
                WritableImage image = canvas.snapshot(params, null);
                ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
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

    public class Dialog {

        public void showError(String title, String header, String content) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(header);
            alert.setContentText(content);
            alert.showAndWait();
        }

    }

        public double[] hslToRgb(double h, double s, double l){
            double r;
            double g;
            double b;
            if (s == 0) {
                r = g = b = l;
            } else {
                double q = l < 0.5 ? l * (1 + s) : l + s - l * s;
                double p = 2 * l - q;
                r = hue2rgb(p, q, h + 1/3);
                g = hue2rgb(p, q, h);
                b = hue2rgb(p, q, h - 1/3);
            }
            return new double[] {r, g, b};
        }


        public double hue2rgb (double p, double q, double t){
            if(t < 0) t += 1;
            if(t > 1) t -= 1;
            if(t < 1/6) return p + (q - p) * 6 * t;
            if(t < 1/2) return q;
            if(t < 2/3) return p + (q - p) * (2/3 - t) * 6;
            return p;
        }

        public double[] rgbTohsl(double r, double g, double b){
            double h;
            double s;
            double l;
            double min, max, delta;
            min = Math.min(Math.min(r, g), b);
            max = Math.max(Math.max(r, g), b);
            l = max;
            delta = max - min;
            if (max != 0) {
                s = delta / max;
            } else {
                s = 0;
                h = -1;
                return new double[]{h, s, l};
            }
            if (r == max) {
                h = (g - b) / delta;
            } else if( g == max ) {
                h = 2 + (b - r) / delta;
            } else {
                h = 4 + (r - g) / delta;
            }
            h *= 60;
            if (h < 0) {
                h += 360;
            }
            return new double[]{h / 360, s, l};
        }


}
