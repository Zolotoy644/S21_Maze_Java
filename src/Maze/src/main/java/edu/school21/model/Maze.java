package edu.school21.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@NoArgsConstructor
public class Maze {
    private int cols;
    private int rows;
    private ArrayList<ArrayList<Integer>> onRight;
    private ArrayList<ArrayList<Integer>> onBottom;

    public Maze(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.onRight = new ArrayList<>(rows);
        this.onBottom = new ArrayList<>(rows);
        for (int i = 0; i < rows; i++) {
            this.onRight.add(new ArrayList<>(cols));
            this.onBottom.add(new ArrayList<>(cols));
            for (int j = 0; j < cols; j++) {
                this.onRight.get(i).add(0);
                this.onBottom.get(i).add(0);
            }
        }
    }
}
