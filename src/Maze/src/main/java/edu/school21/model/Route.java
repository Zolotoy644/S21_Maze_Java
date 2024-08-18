package edu.school21.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;

@Data
public class Route {
    private ArrayList<int[]> coords;

    public Route(ArrayList<int[]> coords) {
        this.coords = coords;
    }
    public Route() {
        this.coords = new ArrayList<int[]>();
    }
}
