package edu.school21.service;

import java.util.Random;

import org.springframework.stereotype.Service;

import edu.school21.model.Cave;

/**
 * Сервис для управления генерацией и эволюцией пещеры.
 * Этот класс реализует алгоритмы создания пещеры случайной структуры и
 * выполнения ее эволюции через определенные поколения на основе простых
 * правил клеточных автоматов.
 * <h2>Основные функции:</h2>
 * <ul>
 * <li>Генерация случайной пещеры с заданными размерами и плотностью живых клеток.</li>
 * <li>Расчет следующего поколения пещеры по правилам "жизни и смерти" клеток.</li>
 * </ul>
 * Пример использования:
 * <pre>
 * {@code
 * CaveService caveService = new CaveService();
 * Cave initialCave = caveService.generateCave(10, 10, 40);
 * Cave nextGeneration = caveService.getNextGeneration(initialCave, 4, 3);
 * }
 * </pre>
 */
@Service
public class CaveService {
    /**
     * Генерация пещеры заданных размеров с заданной вероятностью живых
     * клеток.
     * @param rows Количество строк в пещере.
     * @param cols Количество столбцов в пещере.
     * @param probability Вероятность живой клетки в процентах (0-100).
     * @return Объект Cave, представляющий сгенерированную пещеру.
     */
    public Cave generateCave(int rows, int cols, int probability) {
        Cave result = new Cave(rows, cols);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                boolean isAlive = randomBool(probability);
                if (isAlive) {
                    result.getIsAlive().get(i).set(j, 1);
                }
            }
        }

        return result;
    }

    /**
     * Получение следующего поколения пещеры на основе текущего состояния.
     * @param cave Текущее состояние пещеры.
     * @param liveLimit Минимальное количество соседей для выживания живой клетки.
     * @param deathLimit Максимальное количество соседей для выживания мертвой
     * клетки.
     * @return Объект Cave, представляющий следующее поколение пещеры.
     */
    public Cave getNextGeneration(Cave cave, int liveLimit, int deathLimit) {
        Cave result = new Cave(cave.getRows(), cave.getCols());
        for (int i = 0; i < cave.getRows(); i++) {
            for (int j = 0; j < cave.getCols(); j++) {
                int neighbs = countNeighborhoods(cave, i, j);
                if (cave.getIsAlive().get(i).get(j) == 0 && neighbs > liveLimit) {
                    result.getIsAlive().get(i).set(j, 1);
                } else if (cave.getIsAlive().get(i).get(j) == 1 && neighbs < deathLimit) {
                    result.getIsAlive().get(i).set(j, 0);
                } else {
                    result.getIsAlive().get(i).set(j, cave.getIsAlive().get(i).get(j));
                }
            }
        }
        return result;
    }




    /**
     * Генерация случайного значения типа bool с заданной вероятностью.
     * @param probability Вероятность в процентах (0-100).
     * @return true, если случайное значение меньше заданной вероятности, иначе
     * false.
     */
    private boolean randomBool(int probability) {
        Random random = new Random();
        int randomValue = random.nextInt(100);
        return randomValue < probability;
    }

    /**
     * Подсчет количества соседей у клетки в пещере.
     * @param cave Текущее состояние пещеры.
     * @param i Индекс строки клетки.
     * @param j Индекс столбца клетки.
     * @return Количество соседей у клетки.
     */
    private int countNeighborhoods(Cave cave, int i, int j) {
        int neighborhoodsCounter = 0;
        for (int k = i - 1; k <= i + 1; k++) {
            for (int m = j - 1; m <= j + 1; m++) {
                if (k == i && m == j) {
                    continue;
                } else if (k < 0 || k == cave.getRows() || m < 0 || m == cave.getCols()) {
                    neighborhoodsCounter++;
                } else {
                    neighborhoodsCounter += cave.getIsAlive().get(k).get(m);
                }
            }
        }
        return neighborhoodsCounter;
    }

}
