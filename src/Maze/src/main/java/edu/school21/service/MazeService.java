package edu.school21.service;

import java.util.ArrayList;
import java.util.Objects;

import org.springframework.stereotype.Service;

import edu.school21.model.Maze;

/**
 * Сервис для генерации лабиринтов.
 * <p>
 * Этот класс реализует алгоритм генерации случайного лабиринта с использованием множества клеток.
 * В процессе генерации устанавливаются вертикальные и горизонтальные стены, формируя лабиринт.
 * </p>
 * <h2>Основные функции:</h2>
 * <ul>
 * <li>Генерация лабиринта с заданными размерами.</li>
 * <li>Установка границ (стен) между клетками.</li>
 * <li>Создание пути через лабиринт с учетом множеств.</li>
 * </ul>
 * 
 * <p>Пример использования:</p>
 * <pre>
 * {@code
 * MazeService mazeService = new MazeService();
 * Maze maze = mazeService.generateMaze(10, 10);
 * }
 * </pre>
 */
@Service
public class MazeService {
    private int counter = 1;

    public Maze generateMaze(int rows, int cols) {
        Maze maze = new Maze(rows, cols);
        ArrayList<Integer> line = new ArrayList<>(cols);
        for (int i = 0; i < cols; i++) {
            line.add(0);
        }

        for (int row = 0; row < rows - 1; row++) {
            fillWithSet(line);
            setRightBorder(line, maze, row);
            setBottomBorder(line, maze, row);
            fixBottomBorder(line, maze, row);
            prepareNewLine(line, maze, row);
        }
        addFinalRow(line, maze);
        return maze;
    }

    // boolean generator
    private boolean randomBool() {
        return Math.random() > 0.5;
    }

    /**
     * Заполнение массива значениями множества.
     * @param line Массив для заполнения.
     */
    private void fillWithSet(ArrayList<Integer> line) {
        for (int i = 0; i < line.size(); ++i) {
            if (line.get(i) == 0) {
                line.set(i, counter++);
            }
        }
    }

    /**
     * Установка правых границ лабиринта.
     * @param line Массив, представляющий текущую строку лабиринта.
     * @param maze Объект Maze, представляющий лабиринт.
     * @param row Текущая строка лабиринта.
     */
    private void setRightBorder(ArrayList<Integer> line, Maze maze, int row) {
        for (int i = 0; i < line.size() - 1; ++i) {
            boolean choice = randomBool();
            if (choice || line.get(i) == line.get(i + 1)) {
                maze.getOnRight().get(row).set(i, 1);
            } else {
                mergeSet(line, i);
                line.set(i + 1, line.get(i));
            }
        }
    }

    /**
     * Объединение множеств в массива.
     * @param line Массив, представляющий текущую строку лабиринта.
     * @param index Индекс элемента в массиве.
     */
    private void mergeSet(ArrayList<Integer> line, int index) {
        int mutableSet = line.get(index + 1);
        for (int j = 0; j < line.size(); ++j) {
            if (line.get(j) == mutableSet) {
                line.set(j, line.get(index));
            }
        }
    }

    /**
     * Установка нижних границ лабиринта.
     * @param line Массив, представляющий текущую строку лабиринта.
     * @param maze Объект Maze, представляющий лабиринт.
     * @param row Текущая строка лабиринта.
     */
    private void setBottomBorder(ArrayList<Integer> line, Maze maze, int row) {
        for (int i = 0; i < line.size(); ++i) {
            boolean choise = randomBool();
            if (choise && countBottomExits(line, maze, i, row) != 1) {
                maze.getOnBottom().get(row).set(i, 1);
            }
        }

    }

    /**
     * Подсчет количества нижних выходов из множества.
     * @param line Массив, представляющий текущую строку лабиринта.
     * @param maze Объект Maze, представляющий лабиринт.
     * @param index Индекс элемента в массиве.
     * @param row Текущая строка лабиринта.
     * @return Количество нижних выходов из множества.
     */
    private int countBottomExits(ArrayList<Integer> line, Maze maze, int index, int row) {
        int currentSet = line.get(index);
        int bottomCounter = 0;
        for (int i = 0; i < line.size(); ++i) {
            if (line.get(i) == currentSet && maze.getOnBottom().get(row).get(i)  == 0) {
                bottomCounter++;
            }
        }
        return bottomCounter;
    }

    /**
     * Подготовка новой строки лабиринта.
     * @param line Массив, представляющий текущую строку лабиринта.
     * @param maze Объект Maze, представляющий лабиринт.
     * @param row Текущая строка лабиринта.
     */
    private void prepareNewLine(ArrayList<Integer> line, Maze maze, int row) {
        for (int i = 0; i < maze.getCols(); ++i) {
            if (maze.getOnBottom().get(row).get(i) == 1) {
                line.set(i, 0);
            }
        }
    }

    /**
     * Коррекция нижних границ лабиринта.
     * @param line Массив, представляющий текущую строку лабиринта.
     * @param maze Объект Maze, представляющий лабиринт.
     * @param row Текущая строка лабиринта.
     */
    private void fixBottomBorder(ArrayList<Integer> line, Maze maze, int row) {
        for (int i = 0; i < line.size(); ++i) {
            if (countBottomExits(line, maze, i, row) == 0) {
                maze.getOnBottom().get(row).set(i, 0);
            }
        }
    }

    /**
     * Добавление последней строки лабиринта.
     * @param line Массив, представляющий текущую строку лабиринта.
     * @param maze Объект Maze, представляющий лабиринт.
     */
    void addFinalRow(ArrayList<Integer> line, Maze maze) {
        fillWithSet(line);
        setRightBorder(line, maze, maze.getRows() - 1);
        fixLastLine(line, maze);
    }

    /**
     * Коррекция последней строки лабиринта.
     * @param line Массив, представляющий текущую строку лабиринта.
     * @param maze Объект Maze, представляющий лабиринт.
     */
    void fixLastLine(ArrayList<Integer> line, Maze maze) {
        for (int i = 0; i < maze.getCols() - 1; i++) {
            if (!Objects.equals(line.get(i), line.get(i + 1))) {
                maze.getOnRight().get(maze.getRows() - 1).set(i, 0);
                mergeSet(line, i);
            }
        }
    }


}
