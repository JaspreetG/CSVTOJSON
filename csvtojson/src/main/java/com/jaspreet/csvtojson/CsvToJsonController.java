package com.jaspreet.csvtojson;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class CsvToJsonController {

    @Autowired
    private CsvToJsonService csvToJsonService;

    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping("/convert")
    public ResponseEntity<?> convertCsvToJson(
            @RequestParam("configFile") MultipartFile schemaFile,
            @RequestParam("csvFile") MultipartFile csvFile) {
        try {
            String schemaContent = new String(schemaFile.getBytes());
            String csvContent = new String(csvFile.getBytes());

            List<Map<String, Object>> jsonResult = csvToJsonService.convertCsvToJson(schemaContent, csvContent);
            return ResponseEntity.ok(jsonResult);
        } catch (ValidationException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Validation Error", "message", e.getMessage()));
        } catch (IOException e) {
            return ResponseEntity.status(500).body(Map.of("error", "Error reading files", "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("error", "An unexpected error occurred", "message", e.getMessage()));
        }
    }
}
