package com.datacsv.api;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)

public class CSVDatabaseServiceTest {

    private Database database;

    private static final String dataFileName = "inventory.csv";

    @BeforeAll
    public static void init(){
        Path filePath = Paths.get(dataFileName);
        if (Files.exists(filePath)) {
            try {
                Files.delete(filePath);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private BufferedReader getReader(String fileName) {
        try {
            return new BufferedReader(new FileReader(fileName));
        } catch (IOException ex) {
            throw new RuntimeException("cant open " + dataFileName + " file");
        }
    }

    public CSVDatabaseServiceTest(){
        database = new CSVDatabaseService("inventory.csv");
    }

    @Test
    @Order(1)
    public void testInsert(){
        database.insert("0,shoes,adidas,10");
        database.insert("0,t-shirt,nike,15");
        database.insert("0,watch,xiaomi,12");
        BufferedReader reader = getReader(dataFileName);

        List<String> lines = reader.lines().collect(Collectors.toList());
        assertEquals(3, lines.size());
        assertEquals("\"1\"", lines.get(0).split(",")[0]);
    }

    @Test
    @Order(2)
    public void testUpdate() {
        database.update(2l, "0,t-shirt,nike,20");
        BufferedReader reader = getReader(dataFileName);

        List<String> lines = reader.lines().collect(Collectors.toList());
        assertEquals(3, lines.size());
        assertEquals("\"20\"", lines.get(1).split(",")[3]);
    }

    @Test
    @Order(3)
    public void testSelect(){
        List<String> rows = database.select(-1l);

        assertEquals(rows.size(), 3);

        rows = database.select(2l);
        assertEquals(1l, rows.size());
        assertEquals("2,t-shirt,nike,20", rows.get(0));
    }

    @Test
    @Order(4)
    public void testDelete(){
        database.delete(2l);

        BufferedReader reader = getReader(dataFileName);

        List<String> lines = reader.lines().collect(Collectors.toList());
        assertEquals(2, lines.size());
    }


}
