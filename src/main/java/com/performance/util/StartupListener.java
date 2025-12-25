package com.performance.util;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.File;

@WebListener
public class StartupListener implements ServletContextListener {
    
    private static final String CSV_FILENAME = "Uncleaned_employees_final_dataset (1).csv";
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("=================================================");
        System.out.println("Employee Performance Analytics - Starting...");
        System.out.println("=================================================");
        
        String projectRoot = System.getProperty("user.dir");
        String csvPath = projectRoot + "/employee-performance-analytics/dataset/" + CSV_FILENAME;
        
        String dataDir = System.getProperty("jboss.server.data.dir");
        String arffPath = dataDir + "/employee_performance.arff";
        
        System.out.println("CSV Path: " + csvPath);
        System.out.println("ARFF Path: " + arffPath);
        
        File csvFile = new File(csvPath);
        File arffFile = new File(arffPath);
        
        if (!csvFile.exists()) {
            System.err.println("ERROR: CSV file not found at: " + csvPath);
            System.err.println("Please ensure the dataset file exists in the dataset directory.");
            return;
        }
        
        System.out.println("CSV file found: " + csvFile.length() + " bytes");
        
        if (arffFile.exists() && arffFile.lastModified() >= csvFile.lastModified()) {
            System.out.println("ARFF file already exists and is up-to-date");
            System.out.println("Size: " + arffFile.length() + " bytes");
        } else {
            System.out.println("Converting CSV to ARFF format...");
            try {
                CsvToArffConverter.convertCsvToArff(csvPath, arffPath);
                System.out.println("Conversion complete!");
                System.out.println("ARFF file created: " + arffFile.length() + " bytes");
            } catch (Exception e) {
                System.err.println("ERROR: Failed to convert CSV to ARFF");
                e.printStackTrace();
            }
        }
        
        System.out.println("=================================================");
        System.out.println("Application ready!");
        System.out.println("=================================================");
    }
    
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("Employee Performance Analytics - Shutting down...");
    }
}
