package edu.school21.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import edu.school21.model.Maze;
import edu.school21.model.Route;
import edu.school21.service.MazeService;
import edu.school21.service.MazeSolverService;
import edu.school21.service.RouteService;
import lombok.extern.slf4j.Slf4j;

/**
 * Контроллер для создания лабиринта.
 * Содержит методы для генерации лабиринта, его решения и обучения агента.
 */

@Slf4j
@RestController
@RequestMapping("/maze")
@CrossOrigin
public class MazeController {
    private final MazeService mazeService;
    private final MazeSolverService mazeSolverService;
    private final ObjectMapper objectMapper;
    private final RouteService routeService;

    public MazeController(MazeService mazeService, ObjectMapper objectMapper, RouteService routeService, MazeSolverService mazeSolverService) {
        this.mazeService = mazeService;
        this.objectMapper = objectMapper;
        this.routeService = routeService;
        this.mazeSolverService = mazeSolverService;
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    /**
     * Получает контакты пользователя по его имени и фамилии.
     *
     * @param rows количество строк
     * @param cols количество столбцов
     * @return возвращает сгенерированный лабиринт
     */
    @GetMapping(produces = "application/json")
    @ResponseBody
    public String generateMaze(@RequestParam Integer rows, @RequestParam Integer cols) {
        log.info("Запрос на генерацию лабиринта с параметрами: rows={}, cols={}", rows, cols);
        Maze maze = mazeService.generateMaze(rows, cols);
        try {
            String response = objectMapper.writeValueAsString(maze);
            log.info("Лабиринт успешно сгенерирован.");
            return response;
        } catch (JsonProcessingException e) {
            log.error("Ошибка при сериализации объекта лабиринта", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Получает контакты пользователя по его имени и фамилии.
     *
     * @param maze лабиринт для решения
     * @param fromX начальная координата Х
     * @param fromY начальная координата У
     * @param toX конечная координата У
     * @param toY конечная координата У
     *
     * @return возвращает маршрут движения к конечной точке лабиринта
     */
    @PostMapping
    @ResponseBody
    public String generateRoute(@RequestBody Maze maze,
                                @RequestParam Integer fromX,
                                @RequestParam Integer fromY,
                                @RequestParam Integer toX,
                                @RequestParam Integer toY) {
        log.info("Запрос на генерацию маршрута: fromX={}, fromY={}, toX={}, toY={}", fromX, fromY, toX, toY);
        try {
            Route route = routeService.buildRoute(maze, fromX, fromY, toX, toY);
            String response = objectMapper.writeValueAsString(route);
            log.info("Маршрут построен");
            return response;
        } catch (JsonProcessingException e) {
            log.error("Ошибка при построении маршрута", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Обучает агента для решения лабиринта.
     *
     * @param maze лабиринт для решения
     * @param toX конечная координата У
     * @param toY конечная координата У
     * @return возвращает сообщение об успешном обучении агента
     */
    // Путь для обучения агента
    @PostMapping("/agent/train")
    public String trainAgent(
            @RequestBody Maze maze,
            @RequestParam Integer toX,
            @RequestParam Integer toY
    ) {
        log.info("Запрос на обучение агента с конечной точкой: toX={}, toY={}", toX, toY);
        mazeSolverService.trainAgent(maze, toX, toY);
        log.info("Обучение успешно завершено для конечной точки toX={}, toY={}", toX, toY);
        try {
            return objectMapper.writeValueAsString("Обучение успешно завершено");
        } catch (JsonProcessingException e) {
            log.error("Ошибка при сериализации объекта маршрута", e);
            throw new RuntimeException("Ошибка при решении лабиринта", e);
        }
    }

    /**
     * Получает решение лабиринта для агента.
     *
     * @param fromX начальная координата Х
     * @param fromY начальная координата У
     * @return возвращает маршрут движения агента по лабиринту
     */
    
    // Путь для решения лабиринта агентом
    @PostMapping("/agent/solve")
    @ResponseBody
    public String solveMaze(@RequestParam Integer fromX,
                            @RequestParam Integer fromY) {
        log.info("Запрос на решение лабиринта с начальной точкой: fromX={}, fromY={}", fromX, fromY);
        try {
            Route route = mazeSolverService.solveMaze(fromX, fromY); // Решение лабиринта
            String response = objectMapper.writeValueAsString(route); // Преобразование объекта Route в JSON
            log.info("Лабиринт успешно решён для начальной точки fromX={}, fromY={}", fromX, fromY);
            return response;
        } catch (JsonProcessingException e) {
            log.error("Ошибка при сериализации объекта маршрута", e);
            throw new RuntimeException("Ошибка при решении лабиринта", e);
        }
    }
}