package edu.school21.controller;

import edu.school21.model.Route;
import edu.school21.service.RouteService;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import edu.school21.model.Maze;
import edu.school21.service.FileService;
import edu.school21.service.MazeService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/maze")
@CrossOrigin(origins = "http://localhost:3000")
public class MazeController {
    private final MazeService mazeService;
    private final FileService fileService;
    private final ObjectMapper objectMapper;
    private final RouteService routeService;

    public MazeController(MazeService mazeService, FileService fileService, ObjectMapper objectMapper, RouteService routeService) {
        this.mazeService = mazeService;
        this.fileService = fileService;
        this.objectMapper = objectMapper;
        this.routeService = routeService;
        this.objectMapper.registerModule(new JavaTimeModule());
    }

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

    @PostMapping
    @ResponseBody
    public String generateRoute(@RequestBody Maze maze,
                                @RequestParam Integer fromX,
                                @RequestParam Integer fromY,
                                @RequestParam Integer toX,
                                @RequestParam Integer toY) {
        log.info("запрос на генерацию маршрута: fromX={}, fromY={}, toX={}, toY={}", fromX, fromY, toX, toY);
        Route route = routeService.buildRoute(maze, fromX, fromY, toX, toY);
        try {
            String response = objectMapper.writeValueAsString(route);
            log.info("Маршрут построен");
            return response;
        } catch (JsonProcessingException e) {
            log.error("Ошибка при построении маршрута", e);
            throw new RuntimeException(e);
        }

    }
}