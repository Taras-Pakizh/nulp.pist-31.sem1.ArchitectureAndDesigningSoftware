package ua.feo.app.inf.task;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Pair;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class Target11 implements TargetInf {

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
        return "Побудова двомірних зображень";
    }

    @Override
    public List<String> getTestCaseList() {
        return new ArrayList<>(data.keySet());
    }

    @Override
    public Map<String, Boolean> getEtalonTestCaseList() {
        return data;
    }

    @FXML private ImageView image;
    @FXML private TextField xField;
    @FXML private TextField yField;
    @FXML private TextField dField;
    @FXML private ColorPicker brushColorPicker;
    @FXML private ColorPicker fillColorPicker;
    @FXML private CheckBox squareCheckBox;
    @FXML private CheckBox fillCheckBox;
    @FXML private Canvas canvas;

    private boolean square;
    private boolean fill;
    private boolean inputError;
    private Model model;

    //private final Image IMAGE_DEFAULT = new Image("./resources/image/triangle-default.png");
    //private final Image IMAGE_WITH_SQUARE = new Image("./resources/image/triangle-with-square.png");

    private Config config;

    public void init() {
        try {
            readConfig();
        } catch (JAXBException e) {
            showErrorDialog("Помилка", "Неможу прочитати конфіг", "Допоможи мені прочитати конфіг");
        }
        model = new Model(canvas.getWidth(), canvas.getHeight(), config);
        square = false;
        inputError = true;
        //image.setImage(IMAGE_DEFAULT);
        fillColorPicker.setDisable(fill);
        drawAxis();

        //test:
        {
            xField.setText("0");
            yField.setText("0");
            dField.setText("0");
            brushColorPicker.setValue(Color.BLACK);
            fillColorPicker.setValue(Color.WHITE);
            squareCheckBox.setSelected(true);
            //image.setImage(IMAGE_WITH_SQUARE);
            fillCheckBox.setSelected(true);
            square = true;
            fill = true;
            inputError = false;
        }
        //end test
    }

    private void readConfig() throws JAXBException {
        Unmarshaller jaxbUnmarshaller = JAXBContext.newInstance(Config.class).createUnmarshaller();
        config = (Config) jaxbUnmarshaller.unmarshal(new File("./resources/xml/config.xml"));
    }

    private void saveConfig() throws JAXBException {
        Marshaller jaxbMarshaller = JAXBContext.newInstance(Config.class).createMarshaller();
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        jaxbMarshaller.marshal(config, new File("./resources/xml/config.xml"));
    }

    @FXML
    private void checkSquare() {
        square = squareCheckBox.isSelected();
        if (square) {
            //image.setImage(IMAGE_WITH_SQUARE);
        } else {
            //image.setImage(IMAGE_DEFAULT);
        }
    }

    @FXML
    private void checkFill() {
        fill = fillCheckBox.isSelected();
        fillColorPicker.setDisable(!fill);
    }

    @FXML
    private void clickClearCanvas() {
        model.deleteFigures();
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        drawAxis();
    }

    @FXML
    private void clickBuildFigure() {
        if (inputError) {
            showErrorDialog("Помилка", "Невірні дані", "Введіть вірні дані");
            return;
        }
        double x = Double.parseDouble(xField.getText());
        double y = Double.parseDouble(yField.getText());
        double d = Double.parseDouble(dField.getText());
        {
            if (config == null) {
                System.err.println("config = null");
                return;
            }
            Config.Dimension c = config.getDimension();
            if (x < c.getMin() || x > c.getMax() || y < c.getMin() || y > c.getMax() || d < c.getMinD() || d > c.getMaxD()) {
                showErrorDialog("Помилка", "Некоректні дані", "Введіть коректні");
                return;
            }
        }
        Color brushColor = brushColorPicker.getValue();
        Color fillColor = fillColorPicker.getValue();
        model.addFigure(x, y, d, brushColor, fillColor, fill, square);

        drawAxis();
        drawFigures();
    }

    private void drawAxis() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        gc.setLineWidth(1);
        gc.setStroke(Color.GRAY);

        double w = canvas.getWidth() / 2;
        double h = canvas.getHeight() / 2;
        gc.strokeLine(0, h, 2 * w, h);
        gc.strokeLine(w, 0, w, 2 * h);

        gc.strokeLine(2 * w, h, 2 * w - 10, h - 5);
        gc.strokeLine(2 * w, h, 2 * w - 10, h + 5);
        gc.strokeLine(w, 0, w - 5, 10);
        gc.strokeLine(w, 0, w + 5, 10);

        double[][] divisions = model.getDivisions();
        gc.setFont(Font.font(8));
        for (int i = 0; i < divisions[0].length; i++) {
            double title = ((int) (divisions[0][i] * 10.) / 10.);
            double coord = divisions[1][i];
            gc.strokeLine(w + 2, h + coord, w - 2, h + coord);
            gc.strokeText(String.valueOf(-title) , w - 20,  h + coord);
            gc.strokeLine(w + 2, h - coord, w - 2, h - coord);
            gc.strokeText(String.valueOf(title) , w - 20,  h - coord);
            gc.strokeLine(w + coord, h + 2, w + coord, h - 2);
            gc.strokeText(String.valueOf(title) , w + coord - 7,  h + 20);
            gc.strokeLine(w - coord, h + 2, w - coord, h - 2);
            gc.strokeText(String.valueOf(-title) , w - coord - 7,  h + 20);
        }
        gc.setStroke(Color.LIGHTGRAY);
        for (int i = 0; i < divisions[0].length; i++) {
            double coord = divisions[1][i];
            gc.setFont(Font.font(8));
            gc.strokeLine(2 * w, h + coord, 0, h + coord);
            gc.strokeLine(2 * w, h - coord, 0, h - coord);
            gc.strokeLine(w + coord, 2 * h, w + coord, 0);
            gc.strokeLine(w - coord, 2 * h, w - coord, 0);
        }
        gc.setStroke(Color.BLACK);
        gc.setFont(Font.font(14));
        gc.strokeText("x" , 2 * w - 10,  h - 10);
        gc.strokeText("y" , w + 10, 10);
    }

    private void drawFigures() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setLineWidth(2);
        Iterator<FigureModel> iterator = model.getFigures();
        while (iterator.hasNext()) {
            FigureModel figure = iterator.next();
            gc.setFill(figure.getFillColor());
            gc.setStroke(figure.getBrushColor());
            double[][] trianglePoints = figure.getTrianglePoints();
            if (figure.isFill()) {
                gc.fillPolygon(trianglePoints[0], trianglePoints[1], 3);
            }
            gc.strokePolygon(trianglePoints[0], trianglePoints[1], 3);
            if (figure.isSquare()) {
                double[] s = figure.getSquarePoints();
                if (s[2] > 0) {
                    gc.strokeRect(s[0] + s[2] / 2., s[1] - s[2], s[2], s[2]);
                }
            }
        }
    }

    @FXML
    private void inputText(KeyEvent event) {
        TextField field = (TextField) event.getSource();
        try {
            Double.parseDouble(field.getText());
            inputError = false;
            field.setStyle("-fx-text-inner-color: black;");
        } catch (NumberFormatException ex) {
            inputError = true;
            field.setStyle("-fx-text-inner-color: red;");
        }
    }

    @FXML
    private void clickMenu(ActionEvent event) {
        MenuItem source = (MenuItem) event.getSource();
        switch (source.getText()) {
            case "Зберегти":
                model.save();
                break;
            case "Загрузити":
                model.load();
                drawAxis();
                drawFigures();
                break;
            case "Про програму":
                try {
                    Dialog dialog = new Dialog();
                    dialog.setTitle("Про програму");
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(getClass().getClassLoader().getResource("fxml/task/Target11_WindowDialog.fxml"));
                    DialogPane root = loader.load();
                    root.getButtonTypes().add(new ButtonType("Закрити", ButtonBar.ButtonData.CANCEL_CLOSE));
                    Target11 controller = loader.getController();
                    controller.setHeader("Лабораторна робота 1 з \"Комп'ютерної графіки\"");
                    controller.setText("Версія:\t1.0" +
                            "\nВи використовуєте актуальну версію програми.\n" +
                            "\n" +
                            "Розробник:\tДубень Богдан Іванович\n" +
                            "\n" +
                            "Використання програми:\n" +
                            "\tДля створення нової фігури, введіть у відповідні поля \n" +
                            "координати нижнього лівого кута трикутника (x та y), довжину сторони (d), \n" +
                            "колір ліній та колір заливки, якщо вона потрібна (встановлюється чек-боксом), \n" +
                            "та чи потрібно вписувати квадрат у трикутник.\n" +
                            "Після цього натисніть \"Побудувати\".\n" +
                            "\tДля зміни деяких налаштувань програми, \nзробіть зміни в файлі \"/resources/xml/config.xml\"\n" +
                            "\tЗберегти або відновити збережений стан програми, \nоберіть відповідний пункт в меню \"Файл\". (Не реалізовано в даній версії)\n" +
                            "\tДля отримання інформації про програму або автора програми, \nоберіть відповідний пункт у меню \"Допомога\".\n" +
                            "\tДля виходу з програми оберіть \"Файл > Вихід\"\n" +
                            "\n" +
                            "Гарячі клавіші:\n" +
                            "\tctrl + S - зберегти стан програми\n" +
                            "\tctrl + D - відновити збережений стан програми\n" +
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
                    Dialog dialog = new Dialog();
                    dialog.setTitle("Про автора");
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(getClass().getClassLoader().getResource("fxml/task/Target11_WindowDialog.fxml"));
                    DialogPane root = loader.load();
                    root.getButtonTypes().add(new ButtonType("Закрити", ButtonBar.ButtonData.CANCEL_CLOSE));
                    Target11 controller = loader.getController();
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

    private void showErrorDialog(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    class ColorUtil implements Serializable {

        private double red;
        private double green;
        private double blue;

        public ColorUtil(Color color) {
            this.red = color.getRed();
            this.green = color.getGreen();
            this.blue = color.getBlue();
        }

        public Color get() {
            return new Color(red, green, blue, 1);
        }

    }

    @XmlRootElement
    class Config {

        private Dimension dimension;
        private Graph graph;

        public Config() {
            this.dimension = new Dimension();
            this.graph = new Graph();
        }

        public Config(Dimension dimension, Graph graph) {
            this.dimension = dimension;
            this.graph = graph;
        }

        public Dimension getDimension() {
            return dimension;
        }

        @XmlElement
        public void setDimension(Dimension dimension) {
            this.dimension = dimension;
        }

        public Graph getGraph() {
            return graph;
        }

        @XmlElement
        public void setGraph(Graph graph) {
            this.graph = graph;
        }


        public class Dimension {

            private double defaultMin;
            private double defaultMax;
            private double min;
            private double max;

            private double defaultD;
            private double minD;
            private double maxD;

            public Dimension() {
                this(0, 0, 0, 0, 0, 0, 0);
            }

            public Dimension(double defaultMin, double defaultMax, double min, double max,
                             double defaultD, double minD, double maxD) {
                this.defaultMin = defaultMin;
                this.defaultMax = defaultMax;
                this.min = min;
                this.max = max;
                this.defaultD = defaultD;
                this.minD = minD;
                this.maxD = maxD;
            }

            public double getDefaultMin() {
                return defaultMin;
            }

            @XmlAttribute
            public void setDefaultMin(double defaultMin) {
                this.defaultMin = defaultMin;
            }

            public double getDefaultMax() {
                return defaultMax;
            }

            @XmlAttribute
            public void setDefaultMax(double defaultMax) {
                this.defaultMax = defaultMax;
            }

            public double getMin() {
                return min;
            }

            @XmlAttribute
            public void setMin(double min) {
                this.min = min;
            }

            public double getMax() {
                return max;
            }

            @XmlAttribute
            public void setMax(double max) {
                this.max = max;
            }

            public double getDefaultD() {
                return defaultD;
            }

            @XmlAttribute
            public void setDefaultD(double defaultD) {
                this.defaultD = defaultD;
            }

            public double getMinD() {
                return minD;
            }

            @XmlAttribute
            public void setMinD(double minD) {
                this.minD = minD;
            }

            public double getMaxD() {
                return maxD;
            }

            @XmlAttribute
            public void setMaxD(double maxD) {
                this.maxD = maxD;
            }

        }

        public class Graph {

            private int divisionsNumber;

            public Graph() {
                this.divisionsNumber = 0;
            }

            public Graph(int divisionsNumber) {
                this.divisionsNumber = divisionsNumber;
            }

            @XmlAttribute
            public int getDivisionsNumber() {
                return divisionsNumber;
            }

            public void setDivisionsNumber(int divisionsNumber) {
                this.divisionsNumber = divisionsNumber;
            }
        }

    }

    class FigureModel implements Serializable {

        private final double x;
        private final double y;
        private final double d;
        private final ColorUtil brushColor;
        private final ColorUtil fillColor;
        private final boolean square;
        private final boolean fill;

        private double[][] tPoints;
        private double[] sPoints;

        public FigureModel(double x, double y, double d, Color brushColor, Color fillColor, boolean fill, boolean square) {
            this.x = x;
            this.y = y;
            this.d = d;
            this.brushColor = new ColorUtil(brushColor);
            this.fillColor = new ColorUtil(fillColor);
            this.square = square;
            this.fill = fill;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }

        public double getD() {
            return d;
        }

        public Color getBrushColor() {
            return brushColor.get();
        }

        public Color getFillColor() {
            return fillColor.get();
        }

        public boolean isSquare() {
            return square;
        }

        public boolean isFill() {
            return fill;
        }

        public void setTrianglePoints(double[][] tPoints) {
            this.tPoints = tPoints;
        }

        public void setSquarePoints(double[] sPoints) {
            this.sPoints = sPoints;
        }

        public double[][] getTrianglePoints() {
            return tPoints;
        }

        public double[] getSquarePoints() {
            return sPoints;
        }

    }

    class Model {

        private List<FigureModel> models;
        private final double width;
        private final double height;
        private final Config config;

        private double max = 0;

        public Model(double width, double height, Config config) {
            this.width = width;
            this.height = height;
            this.config = config;
            models = new ArrayList<>();
            max = config.getDimension().getDefaultMax();
        }

        public void addFigure(double x, double y, double d, Color brushColor, Color fillColor, boolean fill, boolean square) {
            if (models.isEmpty()) {
                max = 0.;
            }
            models.add(new FigureModel(x, y, d, brushColor, fillColor, fill, square));
            if ((x + d) > max) {
                max = x + d;
            } else if (Math.abs(x) > max) {
                max = Math.abs(x);
            }
            if ((y + d) > max) {
                max = y + d;
            } else if (Math.abs(y) > max) {
                max = Math.abs(y);
            }
        }

        public Iterator<FigureModel> getFigures() {
            for (FigureModel model : models) {
                counted(model);
            }
            return models.iterator();
        }

        public void deleteFigures() {
            models.clear();
        }

        public double[][] getDivisions() {
            int divisionsNumber = config.getGraph().getDivisionsNumber();
            double division = max / divisionsNumber;
            double[][] result = new double[2][divisionsNumber];
            for (int i = 0; i < divisionsNumber; i++) {
                result[0][i] = (i + 1) * division;
                result[1][i] = result[0][i] / max / 2. * width;
            }
            return result;
        }

        private void counted(FigureModel model) {
            double w = width / 2;
            double h = height / 2;

            double x = model.getX();
            double y = model.getY();
            double d = model.getD();

            double[][] tPoints = new double[][] {
                    {
                            w + x * w / max,
                            w + (x + d) * w / max,
                            w + (x + (d / 2.)) * w / max,
                    }, {
                    h - y * h / max,
                    h - y * h / max,
                    h - (y + Math.pow(3., 1. / 2.) * d / 2.) * h / max,
            }
            };
            model.setTrianglePoints(tPoints);
            if (model.isSquare()) {
                double[] sPoints = new double[] {
                        w + (x + (d - (Math.pow(3., 1. / 2.)) / 2. * d) / (2. + Math.pow(3., 1. / 2.))) * w / max,
                        h - y * h / max,
                        (Math.pow(3., 1. / 2.) * d) / (2. + Math.pow(3., 1. / 2.)) * h / max
                };
                model.setSquarePoints(sPoints);
            } else {
                model.setSquarePoints(null);
            }
        }

        public void save() {
            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("./resources/tmp/data.ser"))) {
                out.writeObject(models);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void load() {
            try (ObjectInputStream out = new ObjectInputStream(new FileInputStream("./resources/tmp/data.ser"))) {
                models = (List<FigureModel>) out.readObject();
                double x;
                double y;
                double d;
                for (FigureModel m : models) {
                    x = m.getX();
                    y = m.getY();
                    d = m.getD();
                    if ((x + d) > max) {
                        max = x + d;
                    } else if (Math.abs(x) > max) {
                        max = Math.abs(x);
                    }
                    if ((y + d) > max) {
                        max = y + d;
                    } else if (Math.abs(y) > max) {
                        max = Math.abs(y);
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
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

}