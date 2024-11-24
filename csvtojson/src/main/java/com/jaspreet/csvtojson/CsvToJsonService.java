package com.jaspreet.csvtojson;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CsvToJsonService {

    public List<Map<String, Object>> convertCsvToJson(String schemaContent, String csvContent) throws Exception {
        Map<String, Object> schema = parseSchema(schemaContent);
        List<Map<String, String>> csvData = parseCsv(csvContent);
        List<Map<String, Object>> jsonObjects = new ArrayList<>();

        for (Map<String, String> csvRow : csvData) {
            Map<String, Object> jsonObject = processSchema(schema, csvRow, "");
            jsonObjects.add(jsonObject);
        }

        return jsonObjects; // Return List<Map<String, Object>> directly
    }

    private Map<String, Object> processSchema(Map<String, Object> schema, Map<String, String> csvRow, String prefix)
            throws ValidationException {
        Map<String, Object> jsonObject = new LinkedHashMap<>();

        for (Map.Entry<String, Object> entry : schema.entrySet()) {
            String key = entry.getKey();
            Map<String, Object> field = (Map<String, Object>) entry.getValue();

            String fullKey = prefix.isEmpty() ? key : prefix + "." + key;
            String rawValue = csvRow.get(fullKey);

            if (field.containsKey("required") && (boolean) field.get("required")
                    && (rawValue == null || rawValue.isEmpty())) {
                throw new ValidationException("Field '" + fullKey + "' is required but missing.");
            }

            Object value = parseValue(rawValue, field);
            if (field.get("type").equals("object")) {
                value = processSchema((Map<String, Object>) field.get("properties"), csvRow, fullKey);
            }
            // else if (field.get("type").equals("array")) {
            // value = processArray(rawValue, (Map<String, Object>) field.get("items"));
            // }

            jsonObject.put(key, value);
        }

        return jsonObject;
    }

    private Object parseValue(String rawValue, Map<String, Object> field) throws ValidationException {
        if (rawValue == null) {
            return null;
        }

        String type = (String) field.get("type");
        switch (type) {
            case "string":
                return rawValue;
            case "integer":
                try {
                    int intValue = Integer.parseInt(rawValue);
                    if (field.containsKey("min") && intValue < (int) field.get("min")) {
                        throw new ValidationException(
                                "Value '" + intValue + "' is less than minimum allowed: " + field.get("min"));
                    }
                    return intValue;
                } catch (NumberFormatException e) {
                    throw new ValidationException("Invalid integer: " + rawValue);
                }
            default:
                throw new ValidationException("Unsupported type: " + type);
        }
    }

    // private List<Object> processArray(String rawValue, Map<String, Object>
    // itemSchema) throws ValidationException {
    // List<Object> array = new ArrayList<>();
    // if (rawValue != null && !rawValue.isEmpty()) {
    // String[] items = rawValue.split(";");
    // for (String item : items) {
    // array.add(parseValue(item, itemSchema));
    // }
    // }
    // return array;
    // }

    private List<Map<String, String>> parseCsv(String csvContent) {
        List<Map<String, String>> rows = new ArrayList<>();
        String[] lines = csvContent.split("\n");
        String[] headers = lines[0].split(",");
        for (int i = 1; i < lines.length; i++) {
            String[] values = lines[i].split(",");
            Map<String, String> row = new LinkedHashMap<>();
            for (int j = 0; j < headers.length; j++) {
                row.put(headers[j].trim(), values[j].trim());
            }
            rows.add(row);
        }
        return rows;
    }

    private Map<String, Object> parseSchema(String schemaContent) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(schemaContent, Map.class);
    }
}
