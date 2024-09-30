package edu.school21;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.school21.model.Maze;
import edu.school21.model.Route;
import edu.school21.service.MazeService;
import edu.school21.service.MazeSolverService;

public class MazeSolverTest {

    private MazeSolverService mazeSolver;
    private MazeService mazeService;
    private Maze maze;

    @BeforeEach
    public void setUp() {
        mazeSolver = new MazeSolverService();
        mazeService = new MazeService();
        // Инициализация тестового лабиринта
        maze = mazeService.generateMaze(50, 50); // Создаем лабиринт размером 4x4
        // Здесь добавьте код для заполнения лабиринта
        // Например, установка стен, начальной и конечной точек
    }

    @Test
    public void testSolveMaze() {
        // Устанавливаем целевую точку
        int toX = 49;
        int toY = 49;

        // Обучаем агента
        mazeSolver.trainAgent(maze, toX, toY);

        // Решаем лабиринт от начальной точки
        Route route = mazeSolver.solveMaze(0, 0);

        // Проверка, что маршрут не пустой
        assertNotNull(route);
        assertFalse(route.getCoords().isEmpty());

        // Проверка, что маршрут действительно приводит к целевой точке
        int[] lastPosition = route.getCoords().get(route.getCoords().size() - 1);
        assertEquals(toX, lastPosition[0]);
        assertEquals(toY, lastPosition[1]);
    }
}
