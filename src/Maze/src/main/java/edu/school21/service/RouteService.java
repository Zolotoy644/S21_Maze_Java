package edu.school21.service;

import java.util.ArrayDeque;
import java.util.Queue;

import org.springframework.stereotype.Service;

import edu.school21.model.Maze;
import edu.school21.model.Route;

@Service
public class RouteService {
    private static final int DIRECTION_UP = 1;
    private static final int DIRECTION_RIGHT = 2;
    private static final int DIRECTION_DOWN = 3;
    private static final int DIRECTION_LEFT = 4;
     /**
     * @brief Строит маршрут внутри лабиринта от указанной стартовой точки до конечной точки.
     * @param maze Лабиринт, для которого строится маршрут.
     * @param fromX X-координата стартовой точки.
     * @param fromY Y-координата стартовой точки.
     * @param toX X-координата конечной точки.
     * @param toY Y-координата конечной точки.
     * @return Объект Route, представляющий рассчитанный маршрут.
     */
    public Route buildRoute(Maze maze, int fromX, int fromY, int toX, int toY) {
        int[][] map = buildMap(maze, toX, toY);
        return readMap(map, fromX, fromY);
    }


    /**
     * @brief Строит карту направлений внутри лабиринта с использованием поиска в ширину.
     * @param maze Лабиринт, для которого строится карта.
     * @param toX X-координата конечной точки.
     * @param toY Y-координата конечной точки.
     * @return массив, представляющий карту направлений.
     */
    private int[][] buildMap(Maze maze, int toX, int toY) {
        int[][] map = new int[maze.getRows()][maze.getCols()];
        boolean[][] checked = new boolean[maze.getRows()][maze.getCols()];
        Queue<int[]> queue = new ArrayDeque<>();
        queue.add(new int[]{toX, toY});
        checked[toY][toX] = true;

        while (!queue.isEmpty()) {
            int[] currentCords = queue.poll();
            int x = currentCords[0];
            int y = currentCords[1];

            if (x < maze.getCols() - 1 && maze.getOnRight().get(y).get(x) == 0 && !checked[y][x + 1]) {
                queue.add(new int[]{x + 1, y});
                map[y][x + 1] = DIRECTION_LEFT;
                checked[y][x + 1] = true;
            }
            if (x > 0 && maze.getOnRight().get(y).get(x - 1) == 0 && !checked[y][x - 1]) {
                queue.add(new int[]{x - 1, y});
                map[y][x - 1] = DIRECTION_RIGHT;
                checked[y][x - 1] = true;
            }
            if (y > 0 && maze.getOnBottom().get(y - 1).get(x) == 0 && !checked[y - 1][x]) {
                queue.add(new int[]{x, y - 1});
                map[y - 1][x] = DIRECTION_DOWN;
                checked[y - 1][x] = true;
            }
            if (y < maze.getRows() - 1 && maze.getOnBottom().get(y).get(x) == 0 && !checked[y + 1][x]) {
                queue.add(new int[]{x, y + 1});
                map[y + 1][x] = DIRECTION_UP;
                checked[y + 1][x] = true;
            }
        }
        return map;
    }

    /**
     * @brief Считывает карту и строит из нее маршрут.
     * @param map Карта направлений внутри лабиринта.
     * @param fromX X-координата стартовой точки.
     * @param fromY Y-координата стартовой точки.
     * @return Объект Route, представляющий рассчитанный маршрут.
     */
    private Route readMap(int[][] map, int fromX, int fromY) {
        Route result = new Route();
        int[] startCoords = new int[]{fromX, fromY};
        result.getCoords().add(startCoords);
        int x = fromX;
        int y = fromY;
        while (map[y][x] != 0) {
            switch (map[y][x]) {
                case DIRECTION_UP:
                    y--;
                    break;
                case DIRECTION_RIGHT:
                    x++;
                    break;
                case DIRECTION_DOWN:
                    y++;
                    break;
                case DIRECTION_LEFT:
                    x--;
                    break;
            }
            int[] coords = new int[]{x, y};
            result.getCoords().add(coords);
        }

        return result;
    }
}
