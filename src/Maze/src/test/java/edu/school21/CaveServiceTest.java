package edu.school21;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.school21.model.Cave;
import edu.school21.service.CaveService;

public class CaveServiceTest {

    private CaveService caveService;

    @BeforeEach
    public void setUp() {
        caveService = new CaveService();
    }

    @Test
    public void testGenerateCave() {
        int rows = 3;
        int cols = 3;
        int probability = 50;

        Cave cave = caveService.generateCave(rows, cols, probability);
        assertNotNull(cave);
        assertEquals(rows, cave.getRows());
        assertEquals(cols, cave.getCols());

        ArrayList<ArrayList<Integer>> isAlive = cave.getIsAlive();
        assertNotNull(isAlive);
        assertEquals(rows, isAlive.size());
        for (List<Integer> row : isAlive) {
            assertEquals(cols, row.size());
            for (Integer cell : row) {
                assertTrue(cell == 0 || cell == 1);
            }
        }
    }

    @Test
    public void testGetNextGeneration() {
        Cave initialCave = new Cave(3, 3);
        initialCave.getIsAlive().get(0).set(0, 1);
        initialCave.getIsAlive().get(0).set(1, 1);
        initialCave.getIsAlive().get(0).set(2, 1);

        int liveLimit = 2;
        int deathLimit = 3;

        Cave nextGeneration = caveService.getNextGeneration(initialCave, liveLimit, deathLimit);
        assertNotNull(nextGeneration);
        assertEquals(3, nextGeneration.getRows());
        assertEquals(3, nextGeneration.getCols());

        ArrayList<ArrayList<Integer>> isAlive = nextGeneration.getIsAlive();
        assertNotNull(isAlive);
        assertEquals(3, isAlive.size());
        for (List<Integer> row : isAlive) {
            assertEquals(3, row.size());
            for (Integer cell : row) {
                assertTrue(cell == 0 || cell == 1);
            }
        }

        assertEquals(1, isAlive.get(0).get(0));
        assertEquals(1, isAlive.get(1).get(1));
        assertEquals(1, isAlive.get(2).get(2));
    }

    @Test
    public void testGenerateCave_WithZeroProbability() {
        int rows = 3;
        int cols = 3;
        int probability = 0;

        Cave cave = caveService.generateCave(rows, cols, probability);
        assertNotNull(cave);

        ArrayList<ArrayList<Integer>> isAlive = cave.getIsAlive();
        for (List<Integer> row : isAlive) {
            for (Integer cell : row) {
                assertEquals(0, cell);
            }
        }
    }

    @Test
    public void testGenerateCave_WithHundredProbability() {
        int rows = 3;
        int cols = 3;
        int probability = 100;

        Cave cave = caveService.generateCave(rows, cols, probability);
        assertNotNull(cave);

        ArrayList<ArrayList<Integer>> isAlive = cave.getIsAlive();
        for (List<Integer> row : isAlive) {
            for (Integer cell : row) {
                assertEquals(1, cell);
            }
        }
    }
}
