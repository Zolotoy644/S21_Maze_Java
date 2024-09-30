package edu.school21.model.enums;

/**
 * Перечисление действий (движений) для навигации в лабиринте.
 * <p>
 * Каждый элемент перечисления соответствует направлению движения (вверх, вправо, вниз, влево)
 * и содержит метод для получения противоположного направления.
 * </p>
 */
public enum Action {
    /**
     * Действие: движение вверх.
     * Возвращает противоположное действие — {@code DOWN}.
     */
    UP {
        @Override
        public Action getCounterAction() {
            return DOWN;
        }
    },

    /**
     * Действие: движение вправо.
     * Возвращает противоположное действие — {@code LEFT}.
     */
    RIGHT {
        @Override
        public Action getCounterAction() {
            return LEFT;
        }
    },

    /**
     * Действие: движение вниз.
     * Возвращает противоположное действие — {@code UP}.
     */
    DOWN {
        @Override
        public Action getCounterAction() {
            return UP;
        }
    },

    /**
     * Действие: движение влево.
     * Возвращает противоположное действие — {@code RIGHT}.
     */
    LEFT {
        @Override
        public Action getCounterAction() {
            return RIGHT;
        }
    };

    /**
     * Абстрактный метод для получения противоположного действия.
     * Каждое направление должно переопределять этот метод, чтобы возвращать противоположное ему направление.
     *
     * @return Противоположное действие.
     */
    public abstract Action getCounterAction();
}
