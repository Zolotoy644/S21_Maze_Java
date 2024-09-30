package edu.school21;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.school21.model.Maze;
import edu.school21.model.Route;
import edu.school21.service.RouteService;

public class RouteServiceTest {

    private RouteService routeService;
    private Maze mockMaze;

    @BeforeEach
    public void setUp() {
        routeService = new RouteService();
        mockMaze = mock(Maze.class);
    }

    @Test
    public void testBuildRoute_SimplePath() {
        when(mockMaze.getRows()).thenReturn(3);
        when(mockMaze.getCols()).thenReturn(3);
        ArrayList<ArrayList<Integer>> onRight = new ArrayList<>(Arrays.asList(
            new ArrayList<>(Arrays.asList(0, 0, 1)),
            new ArrayList<>(Arrays.asList(0, 1, 1)),
            new ArrayList<>(Arrays.asList(0, 0, 1))
        ));
        ArrayList<ArrayList<Integer>> onBottom = new ArrayList<>(Arrays.asList(
            new ArrayList<>(Arrays.asList(0, 1, 0)),
            new ArrayList<>(Arrays.asList(1, 0, 1)),
            new ArrayList<>(Arrays.asList(1, 0, 0))
        ));
        when(mockMaze.getOnRight()).thenReturn(onRight);
        when(mockMaze.getOnBottom()).thenReturn(onBottom);

        Route route = routeService.buildRoute(mockMaze, 0, 0, 2, 2);
        assertNotNull(route);
        assertFalse(route.getCoords().isEmpty());
        assertEquals(5, route.getCoords().size());
    }

    @Test
    public void testBuildRoute_ComplexPath() {
        when(mockMaze.getRows()).thenReturn(4);
        when(mockMaze.getCols()).thenReturn(4);
        ArrayList<ArrayList<Integer>> onRight = new ArrayList<>(Arrays.asList(
            new ArrayList<>(Arrays.asList(0, 1, 0, 1)),
            new ArrayList<>(Arrays.asList(0, 0, 1, 1)),
            new ArrayList<>(Arrays.asList(1, 0, 0, 1)),
            new ArrayList<>(Arrays.asList(0, 1, 0, 1))
        ));
        ArrayList<ArrayList<Integer>> onBottom = new ArrayList<>(Arrays.asList(
            new ArrayList<>(Arrays.asList(1, 0, 1, 0)),
            new ArrayList<>(Arrays.asList(0, 1, 0, 1)),
            new ArrayList<>(Arrays.asList(0, 1, 1, 0)),
            new ArrayList<>(Arrays.asList(1, 0, 1, 0))
        ));
        when(mockMaze.getOnRight()).thenReturn(onRight);
        when(mockMaze.getOnBottom()).thenReturn(onBottom);

        Route route = routeService.buildRoute(mockMaze, 0, 0, 3, 3);
        assertNotNull(route);
        assertFalse(route.getCoords().isEmpty());
    }

    @Test
    public void testBuildRoute_SingleCell() {
        when(mockMaze.getRows()).thenReturn(1);
        when(mockMaze.getCols()).thenReturn(1);
        ArrayList<ArrayList<Integer>> onRight = new ArrayList<>(Arrays.asList(new ArrayList<>(Arrays.asList(0))));
        ArrayList<ArrayList<Integer>> onBottom = new ArrayList<>(Arrays.asList(new ArrayList<>(Arrays.asList(0))));
        when(mockMaze.getOnRight()).thenReturn(onRight);
        when(mockMaze.getOnBottom()).thenReturn(onBottom);

        Route route = routeService.buildRoute(mockMaze, 0, 0, 0, 0);
        assertNotNull(route);
        assertFalse(route.getCoords().isEmpty());
        assertEquals(1, route.getCoords().size());
        assertArrayEquals(new int[]{0, 0}, route.getCoords().get(0));
    }

    @Test
    public void testBuildRoute_SameStartEnd() {
        when(mockMaze.getRows()).thenReturn(3);
        when(mockMaze.getCols()).thenReturn(3);
        ArrayList<ArrayList<Integer>> onRight = new ArrayList<>(Arrays.asList(
            new ArrayList<>(Arrays.asList(0, 0, 1)),
            new ArrayList<>(Arrays.asList(0, 1, 1)),
            new ArrayList<>(Arrays.asList(0, 0, 1))
        ));
        ArrayList<ArrayList<Integer>> onBottom = new ArrayList<>(Arrays.asList(
            new ArrayList<>(Arrays.asList(0, 1, 0)),
            new ArrayList<>(Arrays.asList(1, 0, 1)),
            new ArrayList<>(Arrays.asList(1, 0, 0))
        ));
        when(mockMaze.getOnRight()).thenReturn(onRight);
        when(mockMaze.getOnBottom()).thenReturn(onBottom);

        Route route = routeService.buildRoute(mockMaze, 1, 1, 1, 1);
        assertNotNull(route);
        assertFalse(route.getCoords().isEmpty());
        assertEquals(1, route.getCoords().size());
        assertArrayEquals(new int[]{1, 1}, route.getCoords().get(0));
    }
}
