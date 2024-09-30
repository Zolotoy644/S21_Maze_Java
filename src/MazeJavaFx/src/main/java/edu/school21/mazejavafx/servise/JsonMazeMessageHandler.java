package edu.school21.mazejavafx.servise;

import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class JsonMazeMessageHandler {
    @Setter
    private int rows;
    @Setter
    private int cols;
    @Setter
    private int[][] onRight;
    @Setter
    private int[][] onBottom;
    @Setter
    private int[][] isAlive;
    @Setter
    private ArrayList<int[]> coords;
    String message;


    public void handleMessage(String jsonMessage) {
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(jsonMessage, JsonObject.class);

        rows = jsonObject.get("rows").getAsInt();
        cols = jsonObject.get("cols").getAsInt();

        JsonArray onRightArray = jsonObject.getAsJsonArray("onRight");
        setOnRight(parseJsonArray(onRightArray));
        onRight = parseJsonArray(onRightArray);

        JsonArray onBottomArray = jsonObject.getAsJsonArray("onBottom");
        setOnBottom(parseJsonArray(onBottomArray));
        onBottom = parseJsonArray(onBottomArray);
    }

    public void handleCaveMessage(String jsonMessage) {
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(jsonMessage, JsonObject.class);
        message = jsonMessage;
        rows = jsonObject.get("rows").getAsInt();
        cols = jsonObject.get("cols").getAsInt();
        JsonArray isAliveArray = jsonObject.getAsJsonArray("isAlive");
        isAlive = parseJsonArray(isAliveArray);

    }

    public  ArrayList<int[]> handleRouteMessage(String jsonMessage) {
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(jsonMessage, JsonObject.class);
        JsonArray coordsArray = jsonObject.getAsJsonArray("coords");
        ArrayList<int[]> coordsNew = new ArrayList<>();
        for (int i = 0; i < coordsArray.size(); i++) {
            JsonArray coord = coordsArray.get(i).getAsJsonArray();
            int x = coord.get(0).getAsInt();
            int y = coord.get(1).getAsInt();
            coordsNew.add(new int[]{x, y});
        }
        setCoords(coordsNew);
        return coordsNew;
    }

    private int[][] parseJsonArray(JsonArray jsonArray) {
        int[][] array = new int[jsonArray.size()][];
        for (int i = 0; i < jsonArray.size(); i++) {
            JsonArray row = jsonArray.get(i).getAsJsonArray();
            array[i] = new int[row.size()];
            for (int j = 0; j < row.size(); j++) {
                array[i][j] = row.get(j).getAsInt();
            }
        }
        return array;
    }

    public String toJson() {
        Gson gson = new Gson();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("rows", rows);
        jsonObject.addProperty("cols", cols);
        JsonArray jsonArray = new JsonArray();
        for (var item : onRight) {
            jsonArray.add(gson.toJsonTree(item));
        }
        JsonArray jsonArray2 = new JsonArray();
        for (var item : onBottom) {
            jsonArray2.add(gson.toJsonTree(item));
        }
        jsonObject.add("onRight", jsonArray);
        jsonObject.add("onBottom", jsonArray2);
        String json = gson.toJson(jsonObject);
        return json;
    }

}
