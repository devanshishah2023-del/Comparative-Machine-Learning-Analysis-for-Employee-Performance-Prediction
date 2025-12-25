package com.performance.util;

import java.io.*;
import java.util.*;

public class CsvToArffConverter {
    
    public static void convertCsvToArff(String csvPath, String arffPath) throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader(csvPath));
        
        String headerLine = reader.readLine();
        if (headerLine == null) {
            reader.close();
            throw new Exception("CSV file is empty");
        }
        
        String[] headers = headerLine.split(",");
        System.out.println("Found " + headers.length + " columns");
        
        Map<Integer, Set<String>> categoricalValues = new HashMap<>();
        for (int i = 0; i < headers.length; i++) {
            categoricalValues.put(i, new HashSet<>());
        }
        
        List<String[]> dataRows = new ArrayList<>();
        String line;
        int rowCount = 0;
        
        while ((line = reader.readLine()) != null) {
            String[] values = line.split(",", -1);
            dataRows.add(values);
            rowCount++;
            
            for (int i = 0; i < values.length && i < headers.length; i++) {
                String value = values[i].trim();
                if (!value.isEmpty() && !isNumeric(value)) {
                    addIfNotEmpty(categoricalValues.get(i), value);
                }
            }
            
            if (rowCount % 1000 == 0) {
                System.out.println("Read " + rowCount + " rows...");
            }
        }
        reader.close();
        
        System.out.println("Total rows read: " + rowCount);
        
        System.out.println("Analyzing categorical attributes...");
        System.out.println("Departments: " + categoricalValues.get(1).size());
        System.out.println("Regions: " + categoricalValues.get(2).size());
        System.out.println("Education levels: " + categoricalValues.get(3).size());
        
        BufferedWriter writer = new BufferedWriter(new FileWriter(arffPath));
        
        writer.write("@relation employee_performance\n\n");
        
        for (int i = 0; i < headers.length; i++) {
            String header = headers[i].trim();
            Set<String> values = categoricalValues.get(i);
            
            if (values.isEmpty() || isNumericColumn(dataRows, i)) {
                writer.write("@attribute " + header + " numeric\n");
            } else {
                writer.write("@attribute " + header + " {");
                List<String> sortedValues = new ArrayList<>(values);
                Collections.sort(sortedValues);
                
                for (int j = 0; j < sortedValues.size(); j++) {
                    String value = sortedValues.get(j);
                    if (value.contains(" ") || value.contains("&") || value.contains("-")) {
                        writer.write("'" + value + "'");
                    } else {
                        writer.write(value);
                    }
                    if (j < sortedValues.size() - 1) {
                        writer.write(",");
                    }
                }
                writer.write("}\n");
            }
        }
        
        writer.write("\n@data\n");
        
        System.out.println("Converting data rows...");
        
        for (String[] row : dataRows) {
            for (int i = 0; i < row.length; i++) {
                String value = row[i].trim();
                
                if (value.isEmpty()) {
                    writer.write("?");
                } else if (categoricalValues.get(i).contains(value)) {
                    if (value.contains(" ") || value.contains("&") || value.contains("-")) {
                        writer.write("'" + value + "'");
                    } else {
                        writer.write(value);
                    }
                } else {
                    writer.write(value);
                }
                
                if (i < row.length - 1) {
                    writer.write(",");
                }
            }
            writer.write("\n");
        }
        
        writer.close();
        System.out.println("Total rows converted: " + dataRows.size());
    }
    
    private static boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    private static boolean isNumericColumn(List<String[]> rows, int columnIndex) {
        int numericCount = 0;
        int totalCount = 0;
        
        for (String[] row : rows) {
            if (columnIndex < row.length) {
                String value = row[columnIndex].trim();
                if (!value.isEmpty()) {
                    totalCount++;
                    if (isNumeric(value)) {
                        numericCount++;
                    }
                }
            }
        }
        
        return totalCount > 0 && ((double) numericCount / totalCount) > 0.8;
    }
    
    private static void addIfNotEmpty(Set<String> set, String value) {
        if (value != null && !value.isEmpty()) {
            set.add(value);
        }
    }
}
