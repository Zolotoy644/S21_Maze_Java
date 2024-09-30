package edu.school21.model;

import java.util.ArrayList;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Класс, представляющий пещеру в лабиринте.
 */
@Data
@NoArgsConstructor
public class Cave {
    private int cols;
    private int rows;
    private ArrayList<ArrayList<Integer>> isAlive;

    public Cave(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.isAlive = new ArrayList<>(rows);
        for (int i = 0; i < rows; i++) {
            this.isAlive.add(new ArrayList<>(cols));
            for (int j = 0; j < cols; j++) {
                this.isAlive.get(i).add(0);
            }
        }
    }

}
