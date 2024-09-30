package edu.school21;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.school21.model.Maze;
import edu.school21.service.MazeService;

class MazeServiceTest {

    private MazeService mazeService;

    @BeforeEach
    void setUp() {
        mazeService = new MazeService();
    }

    @Test
    void testGenerateMaze() {
        int rows = 5;
        int cols = 5;
        Maze maze = mazeService.generateMaze(rows, cols);

        assertNotNull(maze, "Maze should not be null");
        assertEquals(rows, maze.getRows(), "Number of rows should match");
        assertEquals(cols, maze.getCols(), "Number of columns should match");

        ArrayList<ArrayList<Integer>> onRight = maze.getOnRight();
        ArrayList<ArrayList<Integer>> onBottom = maze.getOnBottom();

        assertEquals(rows, onRight.size(), "Number of rows in onRight should match");
        assertEquals(rows, onBottom.size(), "Number of rows in onBottom should match");

        for (int i = 0; i < rows; i++) {
            assertEquals(cols, onRight.get(i).size(), "Number of columns in onRight should match for row " + i);
            assertEquals(cols, onBottom.get(i).size(), "Number of columns in onBottom should match for row " + i);
        }

        boolean pathExists = false;
        for (int i = 0; i < cols; i++) {
            if (onBottom.get(rows - 1).get(i) == 0) {
                pathExists = true;
                break;
            }
        }
        assertTrue(pathExists, "There should be at least one path from the top to the bottom");

    }

    @Test
    void testMazeProperties() {
        int rows = 10;
        int cols = 10;
        Maze maze = mazeService.generateMaze(rows, cols);

        ArrayList<ArrayList<Integer>> onRight = maze.getOnRight();
        ArrayList<ArrayList<Integer>> onBottom = maze.getOnBottom();

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if (row < rows - 1) {
                    assertNotNull(onBottom.get(row).get(col), "Bottom border should be initialized");
                }
                if (col < cols - 1) {
                    assertNotNull(onRight.get(row).get(col), "Right border should be initialized");
                }
            }
        }
    }

    @Test
    void testMazeCompleteness() {
        int rows = 20;
        int cols = 20;
        Maze maze = mazeService.generateMaze(rows, cols);

        ArrayList<ArrayList<Integer>> onRight = maze.getOnRight();
        ArrayList<ArrayList<Integer>> onBottom = maze.getOnBottom();

        for (int i = 0; i < rows - 1; i++) {
            for (int j = 0; j < cols; j++) {
                assertTrue(onBottom.get(i).get(j) == 0 || onBottom.get(i).get(j) == 1, "Bottom border value should be 0 or 1");
            }
        }

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols - 1; j++) {
                assertTrue(onRight.get(i).get(j) == 0 || onRight.get(i).get(j) == 1, "Right border value should be 0 or 1");
            }
        }
    }
}
