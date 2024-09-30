package edu.school21.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import edu.school21.model.Maze;
import edu.school21.model.Route;
import edu.school21.model.enums.Action;

/**
 * Класс {@code MazeSolver} реализует решение лабиринтов с помощью обучения с подкреплением (Q-обучение).
 * <p>
 * Класс обучает агента проходить лабиринт, находя кратчайший путь от любой стартовой точки до фиксированной целевой точки.
 * Используется Q-обучение для оценки действий в каждом состоянии и выбора оптимального пути.
 * <p>
 * Основные методы класса:
 * <ul>
 *   <li>{@link #trainAgent(Maze, int, int)} - Инициализирует Q-таблицу и обучает агента на основе заданного лабиринта и целевой точки.</li>
 *   <li>{@link #solveMaze(int, int)} - Находит маршрут от начальной точки до целевой точки, используя обученную Q-таблицу.</li>
 *   <li>{@link #initializeQTable()} - Инициализирует Q-таблицу для всех возможных состояний и действий.</li>
 *   <li>{@link #train(int, int)} - Обучает агента на основе текущих координат и структуры лабиринта.</li>
 *   <li>{@link #countDeadEnds(int, int)} - Подсчитывает количество тупиков в текущем состоянии.</li>
 *   <li>{@link #countWays(int, int)} - Подсчитывает количество допустимых направлений для текущей позиции.</li>
 *   <li>{@link #chooseAction(int, int)} - Выбирает лучшее действие на основе максимального Q-значения для текущего состояния.</li>
 *   <li>{@link #takeAction(int, int, Action)} - Выполняет действие и возвращает новые координаты агента.</li>
 * </ul>
 */
@Service
public class MazeSolverService {
    private Maze maze; // Хранение лабиринта
    private final double STEP_PENALTY = -0.04;
    private final double VISITED_PENALTY = -0.25;//штраф за переход в уже посещенную ячейку
    private final double DEAD_END_PENALTY = -0.5;//штраф за тупик
    private Map<String, Map<Action, Double>> qTable; // Q-таблица
    private Integer toX;//расположение цели по X
    private Integer toY;//расположение цели по Y

    /**
     * Обучает агента для решения лабиринта, используя заданный лабиринт и целевую точку.
     * <p>
     * Метод выполняет инициализацию Q-таблицы и затем обучает агента, начиная с каждой возможной стартовой позиции в лабиринте.
     * Для каждой стартовой позиции выполняется обучение, чтобы агент мог находить путь к целевой точке.
     * <p>
     * Основные шаги:
     * <ul>
     *   <li>Установка лабиринта и целевой точки.</li>
     *   <li>Инициализация Q-таблицы для представления всех возможных состояний и действий.</li>
     *   <li>Цикл по всем возможным стартовым позициям в лабиринте. Для каждой позиции выполняется обучение агента.</li>
     * </ul>
     *
     * @param maze лабиринт, в котором агент будет обучаться.
     * @param toX горизонтальная координата целевой точки.
     * @param toY вертикальная координата целевой точки.
     */
    public void trainAgent(Maze maze, int toX, int toY) {
        this.maze = maze;
        this.toX = toX;
        this.toY = toY;
        initializeQTable();
        for(int startY = 0; startY < maze.getRows(); startY++){
            for(int startX = 0; startX < maze.getCols(); startX++){
                train(startX, startY);
            }
        }
    }

    /**
     * Инициализирует Q-таблицу для алгоритма Q-обучения агента.
     * <p>
     * Q-таблица представлена как {@code HashMap}, где ключом является строковое представление состояния
     * (определяемое позицией агента в лабиринте), а значением является другой {@code HashMap}, который отображает 
     * каждое возможное действие (UP, DOWN, LEFT, RIGHT) в Q-значение, инициализированное значением 0.0.
     * <p>
     * Каждое состояние соответствует конкретной позиции (x, y) в лабиринте, а каждое действие в карте действий 
     * инициализируется с Q-значением 0.0, что указывает на отсутствие у агента предварительных знаний 
     * о среде или ожидаемых вознаграждениях.
     *
     * @see Action Перечисление возможных действий (UP, DOWN, LEFT, RIGHT).
     */

    private void initializeQTable() {
        qTable = new HashMap<>();

        for (int y = 0; y < maze.getRows(); y++) {
            for (int x = 0; x < maze.getCols(); x++) {
                String state = getState(x, y);
                Map<Action, Double> actions = new HashMap<>();
                actions.put(Action.UP, 0.0);
                actions.put(Action.DOWN, 0.0);
                actions.put(Action.LEFT, 0.0);
                actions.put(Action.RIGHT, 0.0);
                qTable.put(state, actions);
            }
        }
    }

    /**
     * Возвращает строковое представление состояния агента по его координатам в лабиринте.
     * <p>
     * Состояние представляет собой строку в формате "x,y", где x — это горизонтальная координата агента, 
     * а y — вертикальная координата.
     * <p>
     * Данное представление используется в качестве ключа для хранения и извлечения Q-значений из Q-таблицы.
     *
     * @param x горизонтальная координата позиции агента.
     * @param y вертикальная координата позиции агента.
     * @return строковое представление состояния в формате "x,y".
     */
    private String getState(int x, int y) {
        return x + "," + y;
    }

    /**
     * Тренирует агента для нахождения пути в лабиринте от начальной позиции до конечной точки.
     * <p>
     * Агент начинает с заданной позиции (startX, startY) и двигается по лабиринту, принимая решения на каждом шаге на основе 
     * стратегии выбора действий. Во время движения агент обновляет Q-таблицу, учитывая возможные пути, тупики и стены.
     * <p>
     * Алгоритм повторяется до тех пор, пока агент не достигнет целевой точки (toX, toY).
     * 
     * Шаги обучения:
     * <ul>
     * <li>Агент получает текущее состояние и подсчитывает количество доступных путей и тупиков.</li>
     * <li>Выбирается действие с помощью метода {@code chooseAction}.</li>
     * <li>Агент выполняет выбранное действие и проверяет его корректность.</li>
     * <li>Q-таблица обновляется на основе результатов: если действие привело к тупику, агент получает штраф за тупик; 
     * если действие допустимо, агент получает штраф за обычный шаг; если действие недопустимо (стена), назначается штраф за стену.</li>
     * </ul>
     *
     * @param startX начальная горизонтальная координата агента.
     * @param startY начальная вертикальная координата агента.
     */
    private void train(int startX, int startY) {
        int currentX = startX;
        int currentY = startY;
        while (currentX != toX || currentY != toY) {
            String currentState = getState(currentX, currentY);
            int waysCount = countWays(currentX, currentY);
            int deadEndsCount = countDeadEnds(currentX, currentY);
            int waysWithoutDeadEnds = waysCount - deadEndsCount;
            Action action = chooseAction(currentX, currentY);
            int[] nextState = takeAction(currentX, currentY, action);
            
            String nextStateString = getState(nextState[0], nextState[1]);
            if(waysWithoutDeadEnds == 1){
                qTable.get(nextStateString).put(action.getCounterAction(), DEAD_END_PENALTY);
            } else {
                qTable.get(nextStateString).put(action.getCounterAction(), VISITED_PENALTY);
            }
            qTable.get(currentState).put(action, STEP_PENALTY);
            currentX = nextState[0];
            currentY = nextState[1];
        }
    }
    /**
     * Подсчитывает количество тупиков для текущей позиции агента.
     * <p>
     * Метод проверяет Q-таблицу для текущего состояния агента и подсчитывает, сколько действий в этом состоянии 
     * имеют значение штрафа за тупик ({@code DEAD_END_PENALTY}).
     * <p>
     * Каждый раз, когда агент сталкивается с тупиком, соответствующее действие помечается в Q-таблице, и метод
     * использует эту информацию для подсчета тупиков.
     *
     * @param currentX текущая горизонтальная координата агента.
     * @param currentY текущая вертикальная координата агента.
     * @return количество действий, помеченных как тупики для текущего состояния.
     */
    private int countDeadEnds(int currentX, int currentY) {
        int counter = 0;
        String state = getState(currentX, currentY);
        Map<Action, Double> actions = qTable.get(state);
        for(Map.Entry<Action, Double> pair : actions.entrySet()){
            if(pair.getValue() == DEAD_END_PENALTY){
                counter++;
            }
        }
        return counter;
    }

    /**
     * Подсчитывает количество допустимых направлений (путей) для текущей позиции агента.
     * <p>
     * Метод проверяет Q-таблицу для текущего состояния агента и удаляет действия, которые недопустимы
     * из-за наличия стен или границ лабиринта. После удаления недопустимых действий метод возвращает 
     * количество оставшихся возможных направлений.
     * <p>
     * Проверки выполняются по четырем направлениям (ВЛЕВО, ВПРАВО, ВВЕРХ, ВНИЗ) на основе структуры лабиринта:
     * <ul>
     *   <li>ВЛЕВО: если агент в крайнем левом столбце или справа от клетки стена — действие ВЛЕВО удаляется.</li>
     *   <li>ВПРАВО: если агент в крайнем правом столбце или справа от клетки стена — действие ВПРАВО удаляется.</li>
     *   <li>ВВЕРХ: если агент в верхнем ряду или снизу от клетки выше стена — действие ВВЕРХ удаляется.</li>
     *   <li>ВНИЗ: если агент в нижнем ряду или снизу от клетки стена — действие ВНИЗ удаляется.</li>
     * </ul>
     *
     * @param currentX текущая горизонтальная координата агента.
     * @param currentY текущая вертикальная координата агента.
     * @return количество возможных направлений, по которым агент может двигаться из текущей позиции.
     */
    private int countWays(int currentX, int currentY){
        String state = getState(currentX, currentY);
        Map<Action, Double> actions = qTable.get(state);
        if(currentX == 0 || maze.getOnRight().get(currentY).get(currentX - 1) == 1){
            actions.remove(Action.LEFT);
        }
        if(currentX == maze.getCols() - 1 || maze.getOnRight().get(currentY).get(currentX) == 1){
            actions.remove(Action.RIGHT);
        }
        if(currentY == 0 || maze.getOnBottom().get(currentY-1).get(currentX) == 1){
            actions.remove(Action.UP);
        }
        if(currentY == maze.getRows() - 1 || maze.getOnBottom().get(currentY).get(currentX) == 1) {
            actions.remove(Action.DOWN);
        }
        return actions.entrySet().size();
    }

    /**
     * Выбирает действие для агента на основе максимального Q-значения для текущего состояния.
     * <p>
     * Метод ищет действия с максимальным значением Q в текущем состоянии, заданном координатами (currentX, currentY),
     * и выбирает одно из них случайным образом, если несколько действий имеют одинаковое максимальное значение.
     * <p>
     * Основные шаги:
     * <ul>
     *   <li>Нахождение максимального Q-значения для всех доступных действий в текущем состоянии.</li>
     *   <li>Создание списка действий, соответствующих этому максимальному значению.</li>
     *   <li>Случайный выбор одного из таких действий.</li>
     * </ul>
     *
     * @param currentX текущая горизонтальная координата агента.
     * @param currentY текущая вертикальная координата агента.
     * @return одно из действий с максимальным Q-значением, выбранное случайным образом.
     */
    private Action chooseAction(int currentX, int currentY) {
        String state = getState(currentX, currentY);
        Double maxValue = Collections.max(qTable.get(state).entrySet(), Map.Entry.comparingByValue()).getValue();
        List<Action> actions = qTable.get(state).entrySet().stream().filter(pair -> pair.getValue() == maxValue).map(pair -> pair.getKey()).toList();
        return actions.get((int) Math.random() * actions.size());
    }

    /**
     * Выполняет действие и возвращает новые координаты агента после перемещения.
     * <p>
     * Метод принимает текущие координаты агента и направление действия, после чего вычисляет новые координаты,
     * исходя из направления перемещения:
     * <ul>
     *   <li>ВВЕРХ (UP): агент перемещается на одну клетку вверх (уменьшение Y на 1).</li>
     *   <li>ВНИЗ (DOWN): агент перемещается на одну клетку вниз (увеличение Y на 1).</li>
     *   <li>ВЛЕВО (LEFT): агент перемещается на одну клетку влево (уменьшение X на 1).</li>
     *   <li>ВПРАВО (RIGHT): агент перемещается на одну клетку вправо (увеличение X на 1).</li>
     * </ul>
     *
     * @param x текущая горизонтальная координата агента.
     * @param y текущая вертикальная координата агента.
     * @param action действие, которое агент хочет выполнить (ВВЕРХ, ВНИЗ, ВЛЕВО, ВПРАВО).
     * @return массив из двух элементов: новые координаты агента после выполнения действия.
     * @throws IllegalArgumentException если передано неизвестное действие.
     */
    private int[] takeAction(int x, int y, Action action) {
        switch (action) {
            case Action.UP:
                return new int[]{x, y - 1};
            case Action.DOWN:
                return new int[]{x, y + 1};
            case Action.LEFT:
                return new int[]{x - 1, y};
            case Action.RIGHT:
                return new int[]{x + 1, y};
            default:
                throw new IllegalArgumentException("Unknown action: " + action);
        }
    }

    /**
     * Находит маршрут от начальной точки до целевой в лабиринте, используя текущую Q-таблицу.
     * <p>
     * Метод выполняет поиск кратчайшего пути от заданной начальной позиции (fromX, fromY) к целевой точке (toX, toY),
     * выбирая наилучшие действия на каждом шаге на основе Q-таблицы. Если лабиринт не задан, выбрасывается исключение.
     * <p>
     * Основные шаги:
     * <ul>
     *   <li>Проверка, установлен ли лабиринт. Если нет, выбрасывается исключение {@code IllegalStateException}.</li>
     *   <li>Инициализация списка для хранения пути и добавление начальной позиции в список.</li>
     *   <li>Пока текущие координаты не совпадают с целевыми, выполняется выбор лучшего действия и обновление координат.</li>
     *   <li>Добавление каждой новой позиции в список пути до достижения целевой точки.</li>
     * </ul>
     *
     * @param fromX начальная горизонтальная координата для поиска пути.
     * @param fromY начальная вертикальная координата для поиска пути.
     * @return объект {@code Route}, представляющий путь от начальной точки до целевой.
     * @throws IllegalStateException если лабиринт не был установлен.
     */
    public Route solveMaze(int fromX, int fromY) {
        ArrayList<int[]> path = new ArrayList<>();
        int currentX = fromX;
        int currentY = fromY;
        path.add(new int[]{currentX, currentY});

        while (currentX != toX || currentY != toY) {
            String currentState = getState(currentX, currentY);
            Action bestAction = Collections.max(qTable.get(currentState).entrySet(), Map.Entry.comparingByValue()).getKey();
            int[] nextState = takeAction(currentX, currentY, bestAction);

            currentX = nextState[0];
            currentY = nextState[1];
            path.add(new int[]{currentX, currentY});
        }
        return new Route(path);
    }

}
