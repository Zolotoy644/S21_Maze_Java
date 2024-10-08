package edu.school21.model;

import java.util.ArrayList;

import lombok.Data;

/**
 * Класс Route представляет маршрут в виде списка координат.
 * Маршрут состоит из списка пар целых чисел, где каждое целое число
 * представляет координаты одного из шагов маршрута.
 */
@Data
public class Route {

    /**
     * Список координат, представляющий маршрут.
     * Каждый элемент списка - массив из двух целых чисел,
     * представляющий x и y координаты одной точки маршрута.
     */
    private ArrayList<int[]> coords;

    /**
     * Конструктор, создающий объект Route с заданным списком координат.
     *
     * @param coords список координат (массивы из двух элементов),
     *               представляющих маршрут.
     */
    public Route(ArrayList<int[]> coords) {
        this.coords = coords;
    }

    /**
     * Конструктор по умолчанию, создающий пустой маршрут.
     * Инициализирует пустой список координат.
     */
    public Route() {
        this.coords = new ArrayList<int[]>();
    }    
}

