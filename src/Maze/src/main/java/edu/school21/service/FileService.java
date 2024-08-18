package edu.school21.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import edu.school21.model.Cave;
import edu.school21.model.Maze;

@Service
public class FileService {

    /**
     * @brief Загрузка данных о лабиринте из файла.
     * @param fileName Имя файла с данными о лабиринте.
     * @return Объект Maze, представляющий загруженный лабиринт.
     * @throws IndexOutOfBoundsException, если размер лабиринта превышает максимальные
     * размеры.
     */
    public Maze loadMaze(String fileName) {
        Maze result = new Maze();

        try (BufferedReader inputFile = new BufferedReader(new FileReader(fileName))) {
            String[] size = inputFile.readLine().split(" ");
            result.setRows(Integer.parseInt(size[0]));
            result.setCols(Integer.parseInt(size[1]));
            if (result.getRows() > 50 || result.getCols() > 50) {
                throw new IndexOutOfBoundsException("Invalid maze size. Exceeds maximum dimensions.");
            }

            result.setOnRight(new ArrayList<>(result.getRows()));
            for (int i = 0; i < result.getRows(); i++) {
                ArrayList<Integer> row = new ArrayList<>(result.getCols());
                String[] line = inputFile.readLine().split(" ");
                for (String value : line) {
                    row.add(Integer.parseInt(value));
                }
                result.getOnRight().add(row);
            }

            result.setOnBottom(new ArrayList<>(result.getRows()));
            for (int i = 0; i < result.getRows(); i++) {
                ArrayList<Integer> row = new ArrayList<>(result.getCols());
                String[] line = inputFile.readLine().split(" ");
                for (String value : line) {
                    row.add(Integer.parseInt(value));
                }
                result.getOnBottom().add(row);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return result;
    }


    /**
     * @brief Сохранение данных о лабиринте в новый файл.
     * @param maze Объект Maze для сохранения.
     */
    public void saveMaze(Maze maze, String directory) {
        String newFileName = getNewFileName(directory);
        try (BufferedWriter outputFile = new BufferedWriter(new FileWriter(newFileName))) {
            outputFile.write(maze.getRows() + " " + maze.getCols());
            outputFile.newLine();
            for (int i = 0; i < maze.getRows(); i++) {
                for (int j = 0; j < maze.getCols(); j++) {
                    outputFile.write(maze.getOnRight().get(i).get(j));
                }
                outputFile.newLine();
            }
            outputFile.newLine();
            for (int i = 0; i < maze.getRows(); i++) {
                for (int j = 0; j < maze.getCols(); j++) {
                    outputFile.write(maze.getOnBottom().get(i).get(j));
                }
            }
            outputFile.newLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @brief Генерация нового имени файла для сохранения лабиринта.
     * @return Строка, представляющая новое имя файла.
     */
    public String getNewFileName(String directory) {
        int maxFileNum = 0;
        File dir = new File(directory);
        if (dir.exists() && dir.isDirectory()) {
            for (File entry : dir.listFiles()) {
                String name = entry.getName();
                Pattern pattern = Pattern.compile("\\d+");
                Matcher matcher = pattern.matcher(name);
                while (matcher.find()) {
                    int currentFileNum = Integer.parseInt(matcher.group());
                    if (maxFileNum < currentFileNum) {
                        maxFileNum = currentFileNum;
                    }
                }
            }
        }
        return "./" + directory + "/maze" + (maxFileNum + 1) + ".txt";
    }

    /**
     * @brief Загрузка данных о пещере из файла.
     * @param fileName Имя файла с данными о пещере.
     * @return Объект Cave, представляющий загруженную пещеру.
     */
    public Cave loadCave(String fileName) {
        Cave result = new Cave();
        try (BufferedReader inputFile = new BufferedReader(new FileReader(fileName))) {
            String[] size = inputFile.readLine().split(" ");
            result.setRows(Integer.parseInt(size[0]));
            result.setCols(Integer.parseInt(size[1]));
            result.setIsAlive(new ArrayList<>(result.getRows()));
            for (int i = 0; i < result.getRows(); i++) {
                ArrayList<Integer> row = new ArrayList<>(result.getCols());
                String[] line = inputFile.readLine().split(" ");
                for (String value : line) {
                    row.add(Integer.parseInt(value));
                }
                result.getIsAlive().add(row);
            }

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

}
