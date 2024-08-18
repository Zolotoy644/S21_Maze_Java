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

import edu.school21.model.Cave;
import edu.school21.service.CaveService;
import edu.school21.service.FileService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/cave")
@CrossOrigin(origins = "http://localhost:3000")
public class CaveController {
    private final CaveService caveService;
    private final FileService fileService;
    private final ObjectMapper objectMapper;

    public CaveController(CaveService caveService, FileService fileService, ObjectMapper objectMapper) {
        this.caveService = caveService;
        this.fileService = fileService;
        this.objectMapper = objectMapper;
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @GetMapping(produces = "application/json")
    @ResponseBody
    public String generateCave(@RequestParam Integer rows, @RequestParam Integer cols, @RequestParam Integer probability) {
        log.info("Запрос на генерацию пещеры с параметрами: rows={}, cols={}, probability={}", rows, cols, probability);
        Cave cave = caveService.generateCave(rows, cols, probability);
        try {
            String response = objectMapper.writeValueAsString(cave);
            log.info("Пещера сгенерирована успешно.");
            return response;
        } catch (JsonProcessingException e) {
            log.error("Ошибка при сериализации объекта пещеры", e);
            throw new RuntimeException(e);
        }
    }

    @PostMapping
    public String getNewGeneration(@RequestBody Cave cave, 
                                   @RequestParam Integer lifeLimit, 
                                   @RequestParam Integer deathLimit) {
        log.info("Запрос на получение нового поколения пещеры с параметрами: lifeLimit={}, deathLimit={}", lifeLimit, deathLimit);
        Cave newGeneration = caveService.getNextGeneration(cave, lifeLimit, deathLimit);
        try {
            String response = objectMapper.writeValueAsString(newGeneration);
            log.info("Новое поколение пещеры успешно сгенерировано.");
            return response;
        } catch (JsonProcessingException e) {
            log.error("Ошибка при сериализации объекта нового поколения пещеры", e);
            throw new RuntimeException(e);
        }
    }
}