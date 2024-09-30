package edu.school21.mazejavafx;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import edu.school21.mazejavafx.servise.JsonMazeMessageHandler;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * Контроллер для управления графическим интерфейсом.
 * Содержит методы для получения данных для отрисовки графической части приложения.
 */

public class MazeJavaFxController {
    @FXML
    private Spinner<Integer> lifeLimitSpinner;
    @FXML
    private Spinner<Integer> deathLimitSpinner;
    @FXML
    private Spinner<Integer> timeDelaySpinner;
    @FXML
    private Spinner<Integer> mazeWidth;
    @FXML
    private Spinner<Integer> mazeHeight;
    @FXML
    private Spinner<Integer> fromX;
    @FXML
    private Spinner<Integer> fromY;
    @FXML
    private Spinner<Integer> toX;
    @FXML
    private Spinner<Integer> toY;
    @FXML
    private Pane mazePane;
    @FXML
    private Spinner<Integer> caveWidth;
    @FXML
    private Spinner<Integer> caveHeight;
    @FXML
    private Spinner<Integer> probability;
    @FXML
    private Button buildAgentRouteButton;


    private final JsonMazeMessageHandler messageHandler;
    private ScheduledExecutorService scheduler;


    public MazeJavaFxController() {
        messageHandler = new JsonMazeMessageHandler();
        scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    /**
     * Метод для генерации и отображения лабиринта.
     */
    @FXML
    protected void onGenerateMazeButtonClicked() {
        buildAgentRouteButton.setDisable(true);
        int width = mazeWidth.getValue();
        int height = mazeHeight.getValue();

        try {
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(String.format("http://localhost:8080/maze?rows=%d&cols=%d", height, width)))
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            String responseBody = response.body();
            messageHandler.handleMessage(responseBody);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();

        } catch (Exception e) {
            e.printStackTrace();
        }

        drawMaze(width, height);
    }

    private void drawMaze(int width, int height) {
        Platform.runLater(()->{
            mazePane.getChildren().clear();

            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    double rectWidth = 500.0 / width;
                    double rectHeight = 500.0 / height;
                    //Line top = new Line(i * rectWidth, j * rectHeight, (i + 1) * rectWidth, j * rectHeight);
                    //Line left = new Line(i * rectWidth, j * rectHeight, i * rectWidth, (j + 1) * rectHeight);
                    Line bottom = new Line(i * rectWidth, (j + 1) * rectHeight, (i + 1) * rectWidth, (j + 1) * rectHeight);
                    Line right = new Line((i + 1) * rectWidth, j * rectHeight, (i + 1) * rectWidth, (j + 1) * rectHeight);

                    if (messageHandler.getOnBottom()[j][i] == 1) {
                        bottom.setStroke(Color.BLACK);
                    } else {
                        bottom.setStroke(Color.TRANSPARENT);
                    }
                    if (messageHandler.getOnRight()[j][i] == 1) {
                        right.setStroke(Color.BLACK);
                    } else {
                        right.setStroke(Color.TRANSPARENT);
                    }
                    mazePane.getChildren().addAll(/*top, left,*/ bottom, right);
                }
            }

        });
    }

    /**
     * Метод для генерации и отображения пещеры.
     */
    @FXML
    protected void onGenerateCaveButtonClicked() {
        int width = caveWidth.getValue();
        int height = caveHeight.getValue();
        int prob = probability.getValue();

        try {
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(String.format("http://localhost:8080/cave?rows=%d&cols=%d&probability=%d", width, height, prob)))
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            String responseBody = response.body();
            messageHandler.handleCaveMessage(responseBody);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();

        } catch (Exception e) {
            e.printStackTrace();
        }

        drawCaveField(width, height);
    }

    /**
     * Метод для генерации и отображения следующего поколения пещеры.
     */
    @FXML
    protected void onGetNextGenerationButtonClicked() {
        getNextGeneration();
    }

    private void getNextGeneration() {
        int lifeLimit = lifeLimitSpinner.getValue();
        int deathLimit = deathLimitSpinner.getValue();
        int width = caveWidth.getValue();
        int height = caveHeight.getValue();

        try {
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(String.format("http://localhost:8080/cave?lifeLimit=%d&deathLimit=%d", lifeLimit, deathLimit)))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(messageHandler.getMessage()))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();
            messageHandler.handleCaveMessage(responseBody);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();

        } catch (Exception e) {
            e.printStackTrace();
        }

        drawCaveField(width, height);
    }
    /**
     * Метод для отображения пещеры.
     */
    private void drawCaveField(int width, int height) {
        Platform.runLater(()->{
            mazePane.getChildren().clear();

            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    double rectWidth = 500.0 / width;
                    double rectHeight = 500.0 / height;

                    Rectangle rectangle = new Rectangle(i * rectWidth, j * rectHeight, rectWidth, rectHeight);

                    if (messageHandler.getIsAlive()[i][j] == 1) {
                        rectangle.setFill(Color.BLACK);
                        rectangle.setStroke(Color.BLACK);
                    } else {
                        rectangle.setFill(Color.TRANSPARENT);
                        rectangle.setStroke(Color.TRANSPARENT);
                    }

                    mazePane.getChildren().addAll(rectangle);
                }
            }
        });
    }

    @FXML
    protected void onAutoButtonClicked() throws InterruptedException {
       int delay = timeDelaySpinner.getValue();
       if (scheduler.isShutdown()) {
           scheduler = Executors.newSingleThreadScheduledExecutor();
       }
       scheduler.scheduleAtFixedRate(this::getNextGeneration, 0, delay, TimeUnit.MILLISECONDS);
       scheduler.schedule(() -> scheduler.shutdown(), 15000, TimeUnit.MILLISECONDS);
    }

    @FXML
    protected void onStopButtonClicked() {
        scheduler.shutdown();
    }

    @FXML
    protected void onOpenCaveFileButtonClicked() throws IOException {
        loadCaveField();
    }
    /**
     * Метод для загрузки пещеры из файла.
     */
    private void loadCaveField() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выберите файл");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        File selectedFile = fileChooser.showOpenDialog(new Stage());
        if (selectedFile != null) {
            BufferedReader reader = new BufferedReader(new FileReader(selectedFile));
            String[] parameterLine = reader.readLine().split(" ");
            int width = Integer.parseInt(parameterLine[0]);
            int height = Integer.parseInt(parameterLine[1]);
            ArrayList<ArrayList<Integer>> isAlive = new ArrayList<ArrayList<Integer>>();

            for (int i = 0; i < width; i++) {
                ArrayList<Integer> row = new ArrayList<Integer>();
                String[] line = reader.readLine().split(" ");
                for (String value : line) {
                    row.add(Integer.parseInt(value));
                }
                isAlive.add(row);
            }
            int[][] isAliveArray = new int[isAlive.size()][isAlive.get(0).size()];
            for (int i = 0; i < isAlive.size(); i++) {
                for (int j = 0; j < isAlive.get(0).size(); j++) {
                    isAliveArray[i][j] = isAlive.get(i).get(j);
                }
            }
            messageHandler.setIsAlive(isAliveArray);
            drawCaveField(width, height);
        }
    }
    /**
     * Метод для открытия лабиринта из файла.
     */
    @FXML
    protected void onOpenMazeButtonClicked() {
        loadMaze();
    }
    private void loadMaze() {
        buildAgentRouteButton.setDisable(true);
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выберите файл");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        File selectedFile = fileChooser.showOpenDialog(new Stage());

        try (BufferedReader inputFile = new BufferedReader(new FileReader(selectedFile))) {
            String[] size = inputFile.readLine().split(" ");
            int width = Integer.parseInt(size[0]);
            messageHandler.setCols(width);
            int height = Integer.parseInt(size[1]);
            messageHandler.setRows(height);
            if (width > 50 || height > 50) {
                throw new IndexOutOfBoundsException("Invalid maze size. Exceeds maximum dimensions.");
            }
            ArrayList<ArrayList<Integer>> onRight = new ArrayList<ArrayList<Integer>>();
            for (int i = 0; i < width; i++) {
                ArrayList<Integer> row = new ArrayList<Integer>();
                String[] line = inputFile.readLine().split(" ");
                for (String value : line) {
                    row.add(Integer.parseInt(value));
                }
                onRight.add(row);
            }
            int[][] onRightArray = new int[onRight.size()][onRight.get(0).size()];
            for (int i = 0; i < onRight.size(); i++) {
                for (int j = 0; j < onRight.get(0).size(); j++) {
                    onRightArray[i][j] = onRight.get(i).get(j);
                }
            }
            messageHandler.setOnRight(onRightArray);
            inputFile.readLine();
            ArrayList<ArrayList<Integer>> onBottom = new ArrayList<ArrayList<Integer>>();
            for (int i = 0; i < width; i++) {
                ArrayList<Integer> row = new ArrayList<Integer>();
                String[] line = inputFile.readLine().split(" ");
                for (String value : line) {
                    row.add(Integer.parseInt(value));
                }
                onBottom.add(row);
            }
            int[][] onBottomArray = new int[onBottom.size()][onBottom.get(0).size()];
            for (int i = 0; i < onBottom.size(); i++) {
                for (int j = 0; j < onBottom.get(0).size(); j++) {
                    onBottomArray[i][j] = onBottom.get(i).get(j);
                }
            }
            messageHandler.setOnBottom(onBottomArray);

            Platform.runLater(()->{
                mazePane.getChildren().clear();

                for (int i = 0; i < width; i++) {
                    for (int j = 0; j < height; j++) {
                        double rectWidth = 500.0 / width;
                        double rectHeight = 500.0 / height;

                        Line bottom = new Line(i * rectWidth, (j + 1) * rectHeight, (i + 1) * rectWidth, (j + 1) * rectHeight);
                        Line right = new Line((i + 1) * rectWidth, j * rectHeight, (i + 1) * rectWidth, (j + 1) * rectHeight);

                        if (messageHandler.getOnBottom()[j][i] == 1) {
                            bottom.setStroke(Color.BLACK);
                        } else {
                            bottom.setStroke(Color.TRANSPARENT);
                        }
                        if (messageHandler.getOnRight()[j][i] == 1) {
                            right.setStroke(Color.BLACK);
                        } else {
                            right.setStroke(Color.TRANSPARENT);
                        }
                        mazePane.getChildren().addAll(bottom, right);
                    }
                }

            });
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Метод для построения маршрута в лабиринте.
     */
    @FXML
    protected void onBuildRouteButtonClicked() {
        int fromXValue = fromX.getValue();
        int fromYValue = fromY.getValue();
        int toXValue = toX.getValue();
        int toYValue = toY.getValue();
        int width = messageHandler.getCols();
        int height = messageHandler.getRows();

        if (fromXValue >= width || fromYValue >= height || toXValue >= width || toYValue >= height) {
            showAlert();
        } else {
            try {
                HttpClient client = HttpClient.newHttpClient();
                String message = messageHandler.toJson();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(String.format("http://localhost:8080/maze?fromX=%d&fromY=%d&toX=%d&toY=%d", fromXValue, fromYValue, toXValue, toYValue)))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(message))
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                String responseBody = response.body();
                messageHandler.handleRouteMessage(responseBody);
            } catch (Exception e) {
                e.printStackTrace();
            }
            drawRoute(width, height);
        }

    }
    /**
     * Метод для сохранения лабиринта.
     */
    @FXML
    public void onSaveMazeButtonClicked() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выберите директорию для сохранения");
        fileChooser.setInitialFileName("maze.txt");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("maze.txt", "*.txt"));
        File file = fileChooser.showSaveDialog(new Stage());
        if (file != null) {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
            String size = messageHandler.getRows() + " " + messageHandler.getCols() + "\n";
            bufferedWriter.write(size);
            for (int i = 0; i < messageHandler.getRows(); i++) {
                for (int j = 0; j < messageHandler.getCols(); j++) {
                    if (j == messageHandler.getCols() - 1) {
                        bufferedWriter.write(messageHandler.getOnRight()[i][j] + "\n");
                    } else {
                        bufferedWriter.write(messageHandler.getOnRight()[i][j] + " ");
                    }
                }
            }
            bufferedWriter.newLine();
            for (int i = 0; i < messageHandler.getRows(); i++) {
                for (int j = 0; j < messageHandler.getCols(); j++) {
                    if (j == messageHandler.getCols() - 1) {
                        bufferedWriter.write(messageHandler.getOnBottom()[i][j] + "\n");
                    } else {
                        bufferedWriter.write(messageHandler.getOnBottom()[i][j] + " ");
                    }
                }
            }
            bufferedWriter.close();
        }
    }
    /**
     * Метод для запуска тренировки агента.
     */
    @FXML
    public void onTrainAgentButtonClicked() {
        int toXValue = toX.getValue();
        int toYValue = toY.getValue();
        if (toXValue >= messageHandler.getCols() || toYValue >= messageHandler.getRows()) {
            showAlert();
        } else {
            try {
                HttpClient client = HttpClient.newHttpClient();
                String message = messageHandler.toJson();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(String.format("http://localhost:8080/maze/agent/train?toX=%d&toY=%d", toXValue, toYValue)))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(message))
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                String responseBody = response.body();
                buildAgentRouteButton.setDisable(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Метод для построения маршрута в лабиринте.
     */
    @FXML
    protected void onBuildAgentRouteButtonClicked() {
        int fromXValue = fromX.getValue();
        int fromYValue = fromY.getValue();

        int width = messageHandler.getCols();
        int height = messageHandler.getRows();

        if (fromXValue >= width || fromYValue >= height) {
            showAlert();
        } else {
            try {
                HttpClient client = HttpClient.newHttpClient();
                String message = messageHandler.toJson();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(String.format("http://localhost:8080/maze/agent/solve?fromX=%d&fromY=%d", fromXValue, fromYValue)))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(message))
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                String responseBody = response.body();
                messageHandler.handleRouteMessage(responseBody);
            } catch (Exception e) {
                e.printStackTrace();
            }
            drawRoute(width, height);
        }
    }

    private void drawRoute(int width, int height) {
        drawMaze(width, height);
        Platform.runLater(()->{
            //mazePane.getChildren().clear();
            double rectWidth = 500.0 / width;
            double rectHeight = 500.0 / height;
            for (int i = 0; i < messageHandler.getCoords().size() - 1; i++) {
                Line routeLine = new Line(messageHandler.getCoords().get(i)[0] * rectWidth + (rectWidth / 2), messageHandler.getCoords().get(i)[1] * rectHeight + (rectHeight / 2), messageHandler.getCoords().get(i + 1)[0] * rectWidth + (rectWidth / 2), messageHandler.getCoords().get(i + 1)[1] * rectHeight + (rectHeight / 2));
                routeLine.setStroke(Color.RED);
                mazePane.getChildren().add(routeLine);
            }

        });
    }

    private void showAlert() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Out of range warning");
        alert.setContentText("Start or end of the route is out of range");
        alert.showAndWait();
    }

    /*private void showTrainAlert() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Train warning");
        alert.setContentText("The agent didn't train yet");
        alert.showAndWait();
    }*/

}