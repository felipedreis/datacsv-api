package com.datacsv.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class FileDatabaseController {
    @Autowired
    private Database database;

    @GetMapping("/select/{id}")
    public ResponseEntity<List<String>> select(@PathVariable Long id) {
        List<String> rows = database.select(id);
        return new ResponseEntity<>(rows, HttpStatus.OK);
    }

    @PostMapping("/insert")
    public ResponseEntity<Long> insert(@RequestBody String row) {
        Long id = database.insert(row);
        return new ResponseEntity<>(id, HttpStatus.CREATED);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<String> update(@PathVariable Long id, @RequestBody String newRow) {
        boolean success = database.update(id, newRow);
        if (success) {
            return new ResponseEntity<>("Update succeeded", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Update failed", HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        boolean success = database.delete(id);
        if (success) {
            return new ResponseEntity<>("Delete succeeded", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Delete failed", HttpStatus.NOT_FOUND);
        }
    }
}