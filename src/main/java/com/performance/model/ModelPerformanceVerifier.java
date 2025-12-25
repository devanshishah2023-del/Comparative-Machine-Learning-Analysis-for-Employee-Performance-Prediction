package com.performance.util;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import weka.core.Instance;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.unsupervised.instance.Resample;

import java.io.*;
import java.util.*;

/**
 * Utility to verify Table III metrics:
 * - Model Accuracy
 * - BLOB Size
 * - Deserialization Time
 * - Prediction Time
 */
public class ModelPerformanceVerifier {
    
    private static final String DATASET_PATH = 
        System.getProperty("jboss.server.data.dir") + "/employee_performance.arff";
    
    public static void main(String[] args) {
        try {
            System.out.println("================================================================================");
            System.out.println("TABLE III VERIFICATION - Model Persistence and Loading Performance");
            System.out.println("================================================================================\n");
            
            // Load and prepare dataset
            Instances data = loadAndPrepareDataset(DATASET_PATH);
            
            System.out.println("Dataset prepared: " + data.numInstances() + " instances\n");
            
            // Create test instance for prediction timing
            Instance testInstance = createTestInstance(data);
            
            // Test each algorithm
            System.out.println("Testing Naive Bayes...");
            testModel("Naive Bayes", new NaiveBayes(), data, testInstance);
            
            System.out.println("\nTesting J48 Decision Tree...");
            J48 j48 = new J48();
            j48.setConfidenceFactor(0.25f);
            j48.setMinNumObj(2);
            testModel("J48 Tree", j48, data, testInstance);
            
            System.out.println("\nTesting Random Forest...");
            RandomForest rf = new RandomForest();
            rf.setNumIterations(100);
            testModel("Random Forest", rf, data, testInstance);
            
            System.out.println("\n================================================================================");
            System.out.println("VERIFICATION COMPLETE");
            System.out.println("================================================================================");
            
        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Test a single model and measure all metrics
     */
    private static void testModel(String name, Classifier classifier, 
                                  Instances data, Instance testInstance) throws Exception {
        
        // 1. Train model and get accuracy
        System.out.println("  Training and evaluating...");
        classifier.buildClassifier(data);
        
        Evaluation eval = new Evaluation(data);
        eval.crossValidateModel(classifier, data, 3, new Random(1));
        double accuracy = eval.pctCorrect();
        
        // 2. Serialize to get BLOB size
        System.out.println("  Serializing to measure BLOB size...");
        byte[] serializedModel = serializeClassifier(classifier);
        String blobSize = formatSize(serializedModel.length);
        
        // 3. Measure deserialization time (average of 100 runs)
        System.out.println("  Measuring deserialization time (100 iterations)...");
        long deserializationTime = measureDeserializationTime(serializedModel, 100);
        
        // 4. Measure prediction time (average of 1000 runs)
        System.out.println("  Measuring prediction time (1000 iterations)...");
        long predictionTime = measurePredictionTime(classifier, testInstance, 1000);
        
        // Print results
        System.out.println("\n  RESULTS FOR " + name + ":");
        System.out.println("  " + "=".repeat(60));
        System.out.printf("  %-25s: %.2f%%\n", "Accuracy", accuracy);
        System.out.printf("  %-25s: %s\n", "BLOB Size", blobSize);
        System.out.printf("  %-25s: %d ms\n", "Deserialization Time", deserializationTime);
        System.out.printf("  %-25s: %d ms\n", "Prediction Time", predictionTime);
        System.out.println("  " + "=".repeat(60));
    }
    
    /**
     * Serialize classifier to byte array
     */
    private static byte[] serializeClassifier(Classifier classifier) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(classifier);
        oos.close();
        return baos.toByteArray();
    }
    
    /**
     * Measure average deserialization time over multiple iterations
     */
    private static long measureDeserializationTime(byte[] serializedModel, int iterations) throws Exception {
        long totalTime = 0;
        
        // Warmup (5 iterations)
        for (int i = 0; i < 5; i++) {
            deserializeClassifier(serializedModel);
        }
        
        // Actual measurement
        for (int i = 0; i < iterations; i++) {
            long start = System.nanoTime();
            deserializeClassifier(serializedModel);
            long end = System.nanoTime();
            totalTime += (end - start);
        }
        
        // Return average in milliseconds
        return (totalTime / iterations) / 1_000_000;
    }
    
    /**
     * Deserialize classifier from byte array
     */
    private static Classifier deserializeClassifier(byte[] data) throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        ObjectInputStream ois = new ObjectInputStream(bais);
        Classifier classifier = (Classifier) ois.readObject();
        ois.close();
        return classifier;
    }
    
    /**
     * Measure average prediction time over multiple iterations
     */
    private static long measurePredictionTime(Classifier classifier, Instance testInstance, int iterations) throws Exception {
        long totalTime = 0;
        
        // Warmup (10 iterations)
        for (int i = 0; i < 10; i++) {
            classifier.classifyInstance(testInstance);
        }
        
        // Actual measurement
        for (int i = 0; i < iterations; i++) {
            long start = System.nanoTime();
            classifier.classifyInstance(testInstance);
            long end = System.nanoTime();
            totalTime += (end - start);
        }
        
        // Return average in milliseconds
        return (totalTime / iterations) / 1_000_000;
    }
    
    /**
     * Create a test instance for prediction timing
     */
    private static Instance createTestInstance(Instances data) {
        Instance instance = new DenseInstance(data.numAttributes());
        instance.setDataset(data);
        
        // Use first instance from dataset as template
        if (data.numInstances() > 0) {
            Instance template = data.instance(0);
            for (int i = 0; i < data.numAttributes() - 1; i++) {
                instance.setValue(i, template.value(i));
            }
        }
        
        return instance;
    }
    
    /**
     * Format byte size to human-readable format
     */
    private static String formatSize(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        } else if (bytes < 1024 * 1024) {
            return String.format("%.2f KB", bytes / 1024.0);
        } else {
            return String.format("%.2f MB", bytes / (1024.0 * 1024.0));
        }
    }
    
    /**
     * Load dataset and prepare (same as production code)
     */
    private static Instances loadAndPrepareDataset(String arffPath) throws Exception {
        // Load dataset
        DataSource source = new DataSource(arffPath);
        Instances data = source.getDataSet();
        data.setClassIndex(data.numAttributes() - 1);
        
        // Sample to 2000 instances
        Instances sampledData = data;
        if (data.numInstances() > 2000) {
            Resample resample = new Resample();
            resample.setSampleSizePercent((2000.0 / data.numInstances()) * 100);
            resample.setNoReplacement(true);
            resample.setRandomSeed(1);
            resample.setInputFormat(data);
            sampledData = Filter.useFilter(data, resample);
        }
        
        // Convert to categorical
        Instances categoricalData = convertToCategorical(sampledData);
        categoricalData.setClassIndex(categoricalData.numAttributes() - 1);
        
        return categoricalData;
    }
    
    /**
     * Convert numeric scores to categorical (same thresholds as production)
     */
    private static Instances convertToCategorical(Instances data) throws Exception {
        ArrayList<Attribute> attributes = new ArrayList<>();
        
        for (int i = 0; i < data.numAttributes() - 1; i++) {
            attributes.add((Attribute) data.attribute(i).copy());
        }
        
        ArrayList<String> performanceLevels = new ArrayList<>();
        performanceLevels.add("Low");
        performanceLevels.add("Medium");
        performanceLevels.add("High");
        Attribute performanceCategory = new Attribute("performance_category", performanceLevels);
        attributes.add(performanceCategory);
        
        Instances newData = new Instances("employee_performance_categorical", 
                                          attributes, data.numInstances());
        newData.setClassIndex(newData.numAttributes() - 1);
        
        for (int i = 0; i < data.numInstances(); i++) {
            Instance oldInst = data.instance(i);
            Instance newInst = new DenseInstance(newData.numAttributes());
            newInst.setDataset(newData);
            
            for (int j = 0; j < data.numAttributes() - 1; j++) {
                if (oldInst.isMissing(j)) {
                    newInst.setMissing(j);
                } else {
                    newInst.setValue(j, oldInst.value(j));
                }
            }
            
            double score = oldInst.value(data.numAttributes() - 1);
            if (score < 50) {
                newInst.setValue(newData.numAttributes() - 1, "Low");
            } else if (score < 75) {
                newInst.setValue(newData.numAttributes() - 1, "Medium");
            } else {
                newInst.setValue(newData.numAttributes() - 1, "High");
            }
            
            newData.add(newInst);
        }
        
        return newData;
    }
}
