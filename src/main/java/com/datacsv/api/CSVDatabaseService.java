package com.datacsv.api;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CSVDatabaseService implements Database {

    private final String filePath;

    private Long nextId;

    public CSVDatabaseService(@Value("${csv.file.path}") String filePath) {
        this.filePath = filePath;
        nextId = null;
    }
    @Override
    public List<String> select(Long id) {
        try {
            if (Files.notExists(Paths.get(filePath)))
                return List.of();

            CSVReader reader = new CSVReader(new FileReader(filePath));
            List<String[]> rows = reader.readAll();
            reader.close();

            if (id == -1)
                return rows.stream().map(row -> String.join(",", row)).collect(Collectors.toList());
            else
                return rows.stream().filter(row -> Long.parseLong(row[0]) == id)
                    .map(row -> String.join(",", row)).collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Error reading from CSV file", e);
        } catch (CsvException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public synchronized Long insert(String row) {
        try {
            CSVWriter writer = new CSVWriter(new FileWriter(filePath, true));
            Long id = getNextId();
            String[] rowArr = row.split(",");
            rowArr[0] = id.toString();
            writer.writeNext(rowArr);
            writer.close();
            setNextId();
            return id;
        } catch (IOException ex) {
            throw new RuntimeException("Error writing to CSV file", ex);
        }
    }

    @Override
    public synchronized boolean update(Long id, String newRow) {
        try {
            if (Files.notExists(Paths.get(filePath)))
                return false;

            CSVReader reader = new CSVReader(new FileReader(filePath));
            CSVWriter writer = new CSVWriter(new FileWriter(filePath + ".tmp"));
            boolean idFound = false;
            String[] rowArr = newRow.split(",");
            rowArr[0] = String.valueOf(id);
            for (String[] row : reader.readAll()) {
                if (stringIdToLong(row[0]).equals(id)) {
                    writer.writeNext(rowArr);
                    idFound = true;
                } else {
                    writer.writeNext(row);
                }
            }

            reader.close();
            writer.close();

            if (!idFound) {
                return false;
            }
            Files.move(Paths.get(filePath + ".tmp"), Paths.get(filePath),
                    StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (IOException ex) {
            throw new RuntimeException("Error updating CSV file", ex);
        } catch (CsvException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public synchronized boolean delete(Long id) {
        if (id == null)
            throw new RuntimeException("id can't be null");

        try {
            if (Files.notExists(Paths.get(filePath)))
                return false;

            CSVReader reader = new CSVReader(new FileReader(filePath));
            CSVWriter writer = new CSVWriter(new FileWriter(filePath + ".tmp"));

            boolean idFound = false;
            for (String[] row : reader.readAll()) {
                if (stringIdToLong(row[0]).equals(id)) {
                    idFound = true;
                } else {
                    writer.writeNext(row);
                }
            }

            writer.close();

            if (!idFound) {
                return false;
            }
            Files.move(Paths.get(filePath + ".tmp"), Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (IOException ex) {
            throw new RuntimeException("Error deleting from CSV file", ex);
        } catch (CsvException ex) {
            throw new RuntimeException(ex);
        }
    }

    private synchronized void setNextId() {
        this.nextId++;
    }

    private Long getNextId() {
        try {
            if (nextId == null) {
                String line = "";
                if (Files.exists(Paths.get(filePath))) {
                    BufferedReader reader = new BufferedReader(new FileReader(filePath));
                    List<String> lines = reader.lines().collect(Collectors.toList());
                    if (!lines.isEmpty())
                        line = lines.get(lines.size() - 1).split(",")[0].replaceAll("\"", "");

                    reader.close();
                }
                nextId = line.isEmpty() ? 1L : Long.parseLong(line);
            }

            return nextId;
        } catch (IOException ex) {
            throw new RuntimeException("Error reading from CSV file", ex);
        }
    }

    private Long stringIdToLong(String id) {
        return Long.parseLong(id.replaceAll("\"", ""));
    }
}