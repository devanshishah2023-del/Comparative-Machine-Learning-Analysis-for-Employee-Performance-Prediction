package com.performance.rest;

import com.performance.model.ModelHistory;
import com.performance.model.ModelStorage;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import weka.classifiers.Classifier;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Instances;
import weka.core.Instance;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.converters.ConverterUtils.DataSource;
import weka.classifiers.Evaluation;
import weka.filters.Filter;
import weka.filters.unsupervised.instance.Resample;
import java.util.*;
import java.io.*;
import java.text.SimpleDateFormat;

@Path("/admin")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Stateless
public class AdminResource {

    private static final String DATASET_PATH = System.getProperty("jboss.server.data.dir") + "/employee_performance.arff";
    private static final int SAMPLE_SIZE = 2000;
    
    @PersistenceContext(unitName = "PerformanceAnalyticsPU")
    private EntityManager em;
    
    @POST
    @Path("/compare")
    public Response compareAlgorithms() {
        System.out.println("=== COMPARE ALGORITHMS CALLED ===");
        
        try {
            File datasetFile = new File(DATASET_PATH);
            if (!datasetFile.exists()) {
                System.err.println("Dataset not found at: " + DATASET_PATH);
                return Response.ok("{\"error\":\"Dataset not found\"}").build();
            }

            System.out.println("Loading dataset...");
            DataSource source = new DataSource(DATASET_PATH);
            Instances data = source.getDataSet();
            
            if (data.classIndex() == -1) {
                data.setClassIndex(data.numAttributes() - 1);
            }
            
            System.out.println("Original instances: " + data.numInstances());
            
            Instances sampledData = data;
            if (data.numInstances() > SAMPLE_SIZE) {
                System.out.println("Sampling to " + SAMPLE_SIZE + " instances...");
                Resample resample = new Resample();
                resample.setSampleSizePercent((double) SAMPLE_SIZE * 100 / data.numInstances());
                resample.setNoReplacement(true);
                resample.setInputFormat(data);
                sampledData = Filter.useFilter(data, resample);
                System.out.println("Sampled instances: " + sampledData.numInstances());
            }
            
            System.out.println("Converting numeric scores to categories...");
            Instances categoricalData = convertToCategorical(sampledData);
            categoricalData.setClassIndex(categoricalData.numAttributes() - 1);
            
            List<Map<String, Object>> results = new ArrayList<>();
            List<Classifier> trainedClassifiers = new ArrayList<>();
            
            System.out.println("Training J48...");
            J48 j48 = new J48();
            j48.setUnpruned(false);
            j48.setConfidenceFactor(0.25f);
            j48.buildClassifier(categoricalData);
            trainedClassifiers.add(j48);
            results.add(evaluateModel(j48, "J48 Decision Tree", categoricalData));
            
            System.out.println("Training Random Forest...");
            RandomForest rf = new RandomForest();
            rf.setNumIterations(30);
            rf.setNumFeatures(0);
            rf.buildClassifier(categoricalData);
            trainedClassifiers.add(rf);
            results.add(evaluateModel(rf, "Random Forest", categoricalData));
            
            System.out.println("Training Naive Bayes...");
            NaiveBayes nb = new NaiveBayes();
            nb.buildClassifier(categoricalData);
            trainedClassifiers.add(nb);
            results.add(evaluateModel(nb, "Naive Bayes", categoricalData));
            
            // SAVE ALL MODELS TO model_storage WITH BLOB
            String[] algorithmNames = {"J48 Decision Tree", "Random Forest", "Naive Bayes"};
            for (int i = 0; i < trainedClassifiers.size(); i++) {
                saveModelToStorage(trainedClassifiers.get(i), algorithmNames[i], results.get(i));
            }
            
            StringBuilder json = new StringBuilder();
            json.append("{");
            json.append("\"totalInstances\":").append(data.numInstances()).append(",");
            json.append("\"sampledInstances\":").append(sampledData.numInstances()).append(",");
            json.append("\"totalAttributes\":").append(data.numAttributes()).append(",");
            json.append("\"classAttribute\":\"performance_category\",");
            json.append("\"algorithms\":[");
            
            for (int i = 0; i < results.size(); i++) {
                if (i > 0) json.append(",");
                Map<String, Object> result = results.get(i);
                json.append("{");
                json.append("\"algorithm\":\"").append(result.get("algorithm")).append("\",");
                json.append("\"accuracy\":").append(result.get("accuracy")).append(",");
                json.append("\"precision\":").append(result.get("precision")).append(",");
                json.append("\"recall\":").append(result.get("recall")).append(",");
                json.append("\"fMeasure\":").append(result.get("fMeasure")).append(",");
                json.append("\"kappa\":").append(result.get("kappa")).append(",");
                json.append("\"meanAbsoluteError\":").append(result.get("mae")).append(",");
                json.append("\"rootMeanSquaredError\":").append(result.get("rmse"));
                json.append("}");
            }
            
            json.append("]}");
            
            System.out.println("Success! Returning results.");
            return Response.ok(json.toString()).build();
            
        } catch (Exception e) {
            System.err.println("ERROR in compareAlgorithms:");
            e.printStackTrace();
            return Response.ok("{\"error\":\"" + e.getMessage().replace("\"", "'") + "\"}").build();
        }
    }
    
    @POST
    @Path("/train")
    public Response trainModel(Map<String, String> request) {
        System.out.println("=== TRAIN MODEL CALLED ===");
        
        try {
            String algorithm = request.get("algorithm");
            if (algorithm == null || algorithm.isEmpty()) {
                return Response.ok("{\"error\":\"Algorithm required\"}").build();
            }
            
            System.out.println("Training algorithm: " + algorithm);
            
            File datasetFile = new File(DATASET_PATH);
            if (!datasetFile.exists()) {
                return Response.ok("{\"error\":\"Dataset not found\"}").build();
            }

            DataSource source = new DataSource(DATASET_PATH);
            Instances data = source.getDataSet();
            data.setClassIndex(data.numAttributes() - 1);
            
            Instances sampledData = data;
            if (data.numInstances() > SAMPLE_SIZE) {
                Resample resample = new Resample();
                resample.setSampleSizePercent((double) SAMPLE_SIZE * 100 / data.numInstances());
                resample.setNoReplacement(true);
                resample.setInputFormat(data);
                sampledData = Filter.useFilter(data, resample);
            }
            
            Instances categoricalData = convertToCategorical(sampledData);
            categoricalData.setClassIndex(categoricalData.numAttributes() - 1);
            
            Classifier classifier = getClassifier(algorithm);
            if (classifier == null) {
                return Response.ok("{\"error\":\"Unknown algorithm\"}").build();
            }
            
            // TRAIN THE MODEL
            classifier.buildClassifier(categoricalData);
            Map<String, Object> result = evaluateModel(classifier, algorithm, categoricalData);
            
            // SAVE TO model_history (metrics only)
            try {
                ModelHistory history = new ModelHistory();
                history.setAlgorithm(algorithm);
                history.setAccuracy((Double) result.get("accuracy"));
                history.setPrecisionScore((Double) result.get("precision"));
                history.setRecallScore((Double) result.get("recall"));
                history.setFMeasure((Double) result.get("fMeasure"));
                history.setKappa((Double) result.get("kappa"));
                history.setMae((Double) result.get("mae"));
                history.setRmse((Double) result.get("rmse"));
                history.setTotalInstances(data.numInstances());
                history.setSampledInstances(sampledData.numInstances());
                history.setTrainedBy("admin");
                
                em.persist(history);
                System.out.println("SUCCESS: Training metrics saved to model_history");
                
            } catch (Exception e) {
                System.err.println("Warning: Could not save to model_history: " + e.getMessage());
                e.printStackTrace();
            }
            
            // SAVE TO model_storage (with BLOB)
            saveModelToStorage(classifier, algorithm, result);
            
            StringBuilder json = new StringBuilder();
            json.append("{");
            json.append("\"algorithm\":\"").append(algorithm).append("\",");
            json.append("\"totalInstances\":").append(data.numInstances()).append(",");
            json.append("\"sampledInstances\":").append(sampledData.numInstances()).append(",");
            json.append("\"totalAttributes\":").append(data.numAttributes()).append(",");
            json.append("\"metrics\":{");
            json.append("\"accuracy\":").append(result.get("accuracy")).append(",");
            json.append("\"precision\":").append(result.get("precision")).append(",");
            json.append("\"recall\":").append(result.get("recall")).append(",");
            json.append("\"fMeasure\":").append(result.get("fMeasure")).append(",");
            json.append("\"kappa\":").append(result.get("kappa")).append(",");
            json.append("\"meanAbsoluteError\":").append(result.get("mae")).append(",");
            json.append("\"rootMeanSquaredError\":").append(result.get("rmse"));
            json.append("},");
            json.append("\"timestamp\":\"").append(new Date().toString()).append("\"");
            json.append("}");
            
            return Response.ok(json.toString()).build();
            
        } catch (Exception e) {
            System.err.println("ERROR in trainModel:");
            e.printStackTrace();
            return Response.ok("{\"error\":\"" + e.getMessage().replace("\"", "'") + "\"}").build();
        }
    }
    
    /**
     * ========================================================================
     * ENDPOINT: Generate Confusion Matrix for Naive Bayes
     * POST /api/admin/generate-confusion-matrix
     * ========================================================================
     */
    @POST
    @Path("/generate-confusion-matrix")
    public Response generateConfusionMatrix() {
        System.out.println("=== GENERATE CONFUSION MATRIX CALLED ===");
        
        try {
            File datasetFile = new File(DATASET_PATH);
            if (!datasetFile.exists()) {
                System.err.println("Dataset not found at: " + DATASET_PATH);
                return Response.ok("{\"error\":\"Dataset not found at: " + DATASET_PATH + "\"}").build();
            }

            System.out.println("Loading dataset from: " + DATASET_PATH);
            DataSource source = new DataSource(DATASET_PATH);
            Instances data = source.getDataSet();
            
            if (data.classIndex() == -1) {
                data.setClassIndex(data.numAttributes() - 1);
            }
            
            System.out.println("Original instances: " + data.numInstances());
            
            // Sample to 2000 instances
            Instances sampledData = data;
            if (data.numInstances() > SAMPLE_SIZE) {
                System.out.println("Sampling to " + SAMPLE_SIZE + " instances...");
                Resample resample = new Resample();
                resample.setSampleSizePercent((double) SAMPLE_SIZE * 100 / data.numInstances());
                resample.setNoReplacement(true);
                resample.setRandomSeed(1);
                resample.setInputFormat(data);
                sampledData = Filter.useFilter(data, resample);
                System.out.println("Sampled instances: " + sampledData.numInstances());
            }
            
            // Convert to categorical
            System.out.println("Converting numeric scores to categories (Low <50, Medium 50-74, High ≥75)...");
            Instances categoricalData = convertToCategorical(sampledData);
            categoricalData.setClassIndex(categoricalData.numAttributes() - 1);
            
            // Train and evaluate Naive Bayes with 3-fold cross-validation
            System.out.println("Training Naive Bayes with 3-fold cross-validation...");
            NaiveBayes nb = new NaiveBayes();
            Evaluation eval = new Evaluation(categoricalData);
            eval.crossValidateModel(nb, categoricalData, 3, new Random(1));
            
            // Get log directory
            String logDir = System.getProperty("jboss.server.log.dir");
            if (logDir == null) {
                logDir = System.getProperty("user.dir");
            }
            String filePath = logDir + "/confusion_matrix.txt";
            
            System.out.println("Saving confusion matrix to: " + filePath);
            
            // Save confusion matrix to file
            saveConfusionMatrixToFile(eval, filePath, sampledData.numInstances());
            
            // Build JSON response
            StringBuilder json = new StringBuilder();
            json.append("{");
            json.append("\"success\":true,");
            json.append("\"message\":\"Confusion matrix generated successfully!\",");
            json.append("\"filePath\":\"").append(filePath).append("\",");
            json.append("\"viewCommand\":\"cat ").append(filePath).append("\",");
            json.append("\"algorithm\":\"Naive Bayes\",");
            json.append("\"instances\":").append(sampledData.numInstances()).append(",");
            json.append("\"crossValidation\":\"3-fold stratified\",");
            json.append("\"timestamp\":\"").append(new Date().toString()).append("\"");
            json.append("}");
            
            System.out.println("SUCCESS: Confusion matrix generated!");
            return Response.ok(json.toString()).build();
            
        } catch (Exception e) {
            System.err.println("ERROR in generateConfusionMatrix:");
            e.printStackTrace();
            
            StringBuilder json = new StringBuilder();
            json.append("{");
            json.append("\"success\":false,");
            json.append("\"error\":\"").append(e.getMessage().replace("\"", "'")).append("\",");
            json.append("\"details\":\"").append(e.getClass().getName()).append("\"");
            json.append("}");
            
            return Response.ok(json.toString()).build();
        }
    }
    
    /**
     * ========================================================================
     * ENDPOINT: Verify Table III Metrics
     * GET /api/admin/verify-table-metrics
     * ========================================================================
     */
    @GET
    @Path("/verify-table-metrics")
    public Response verifyTableMetrics() {
        System.out.println("=== VERIFY TABLE III METRICS ===");
        
        try {
            File datasetFile = new File(DATASET_PATH);
            if (!datasetFile.exists()) {
                return Response.ok("{\"error\":\"Dataset not found\"}").build();
            }

            // Load dataset
            DataSource source = new DataSource(DATASET_PATH);
            Instances data = source.getDataSet();
            data.setClassIndex(data.numAttributes() - 1);
            
            // Sample to 2000
            Instances sampledData = data;
            if (data.numInstances() > SAMPLE_SIZE) {
                Resample resample = new Resample();
                resample.setSampleSizePercent((double) SAMPLE_SIZE * 100 / data.numInstances());
                resample.setNoReplacement(true);
                resample.setRandomSeed(1);
                resample.setInputFormat(data);
                sampledData = Filter.useFilter(data, resample);
            }
            
            // Convert to categorical
            Instances categoricalData = convertToCategorical(sampledData);
            categoricalData.setClassIndex(categoricalData.numAttributes() - 1);
            
            // Create test instance for prediction timing
            Instance testInstance = categoricalData.instance(0);
            
            List<Map<String, Object>> results = new ArrayList<>();
            
            System.out.println("Testing Naive Bayes...");
            results.add(testSingleModel("Naive Bayes", new NaiveBayes(), categoricalData, testInstance));
            
            System.out.println("Testing J48...");
            J48 j48 = new J48();
            j48.setUnpruned(false);
            j48.setConfidenceFactor(0.25f);
            results.add(testSingleModel("J48 Tree", j48, categoricalData, testInstance));
            
            System.out.println("Testing Random Forest...");
            RandomForest rf = new RandomForest();
            rf.setNumIterations(30);
            rf.setNumFeatures(0);
            results.add(testSingleModel("Random Forest", rf, categoricalData, testInstance));
            
            // Build JSON response
            StringBuilder json = new StringBuilder();
            json.append("{");
            json.append("\"tableIII_verification\":[");
            
            for (int i = 0; i < results.size(); i++) {
                if (i > 0) json.append(",");
                Map<String, Object> result = results.get(i);
                json.append("{");
                json.append("\"model\":\"").append(result.get("model")).append("\",");
                json.append("\"accuracy\":").append(result.get("accuracy")).append(",");
                json.append("\"blobSize\":\"").append(result.get("blobSize")).append("\",");
                json.append("\"blobSizeBytes\":").append(result.get("blobSizeBytes")).append(",");
                json.append("\"deserializationMs\":").append(result.get("deserializationMs")).append(",");
                json.append("\"predictionMs\":").append(result.get("predictionMs"));
                json.append("}");
            }
            
            json.append("]}");
            
            System.out.println("SUCCESS: Table III verification complete!");
            return Response.ok(json.toString()).build();
            
        } catch (Exception e) {
            System.err.println("ERROR in verifyTableMetrics:");
            e.printStackTrace();
            return Response.ok("{\"error\":\"" + e.getMessage().replace("\"", "'") + "\"}").build();
        }
    }
    
    /**
     * Test a single model and collect all Table III metrics
     */
    private Map<String, Object> testSingleModel(String name, Classifier classifier, 
                                               Instances data, Instance testInstance) throws Exception {
        Map<String, Object> metrics = new HashMap<>();
        
        // 1. Train and get accuracy (3-fold CV)
        classifier.buildClassifier(data);
        Evaluation eval = new Evaluation(data);
        eval.crossValidateModel(classifier, data, 3, new Random(1));
        double accuracy = round(eval.pctCorrect(), 2);
        
        // 2. Serialize and get BLOB size
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(classifier);
        byte[] serializedModel = baos.toByteArray();
        oos.close();
        baos.close();
        
        long blobSizeBytes = serializedModel.length;
        String blobSizeFormatted = formatBlobSize(blobSizeBytes);
        
        // 3. Measure deserialization time (average of 100 iterations)
        long deserializationMs = measureDeserialization(serializedModel, 100);
        
        // 4. Measure prediction time (average of 1000 iterations)
        long predictionMs = measurePrediction(classifier, testInstance, 1000);
        
        metrics.put("model", name);
        metrics.put("accuracy", accuracy);
        metrics.put("blobSize", blobSizeFormatted);
        metrics.put("blobSizeBytes", blobSizeBytes);
        metrics.put("deserializationMs", deserializationMs);
        metrics.put("predictionMs", predictionMs);
        
        System.out.println("  " + name + ": " + accuracy + "%, " + blobSizeFormatted + 
                         ", " + deserializationMs + "ms deser, " + predictionMs + "ms pred");
        
        return metrics;
    }
    
    /**
     * Measure average deserialization time (FIXED VERSION)
     */
    private long measureDeserialization(byte[] data, int iterations) throws Exception {
        long totalNano = 0;
        
        // Warmup (5 iterations)
        for (int i = 0; i < 5; i++) {
            ByteArrayInputStream bais = new ByteArrayInputStream(data);
            ObjectInputStream ois = new ObjectInputStream(bais);
            ois.readObject();
            ois.close();
        }
        
        // Measure
        for (int i = 0; i < iterations; i++) {
            long start = System.nanoTime();
            ByteArrayInputStream bais = new ByteArrayInputStream(data);
            ObjectInputStream ois = new ObjectInputStream(bais);
            Classifier c = (Classifier) ois.readObject();
            ois.close();
            long end = System.nanoTime();
            totalNano += (end - start);
        }
        
        // Convert to microseconds first, then to ms with better precision
        long avgMicroseconds = (totalNano / iterations) / 1_000;
        long milliseconds = avgMicroseconds / 1_000;
        
        // Return at least 1ms if operation took any measurable time
        return avgMicroseconds > 0 ? Math.max(1, milliseconds) : 0;
    }
    
    /**
     * Measure average prediction time (FIXED VERSION)
     */
    private long measurePrediction(Classifier classifier, Instance instance, int iterations) throws Exception {
        long totalNano = 0;
        
        // Warmup (10 iterations)
        for (int i = 0; i < 10; i++) {
            classifier.classifyInstance(instance);
        }
        
        // Measure
        for (int i = 0; i < iterations; i++) {
            long start = System.nanoTime();
            classifier.classifyInstance(instance);
            long end = System.nanoTime();
            totalNano += (end - start);
        }
        
        // Convert to microseconds first, then to ms with better precision
        long avgMicroseconds = (totalNano / iterations) / 1_000;
        long milliseconds = avgMicroseconds / 1_000;
        
        // Return at least 1ms if operation took any measurable time
        return avgMicroseconds > 0 ? Math.max(1, milliseconds) : 0;
    }
    
    /**
     * Format BLOB size to human-readable format
     */
    private String formatBlobSize(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        } else if (bytes < 1024 * 1024) {
            return String.format("%.2f KB", bytes / 1024.0);
        } else {
            return String.format("%.2f MB", bytes / (1024.0 * 1024.0));
        }
    }
    
    /**
     * Save formatted confusion matrix to text file
     */
    private void saveConfusionMatrixToFile(Evaluation eval, String filePath, int numInstances) throws Exception {
        PrintWriter writer = new PrintWriter(new FileWriter(filePath));
        
        // Header
        writer.println("================================================================================");
        writer.println("NAIVE BAYES CONFUSION MATRIX");
        writer.println("================================================================================");
        writer.println("Generated: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        writer.println("Dataset: Employee Performance (N=" + numInstances + ")");
        writer.println("Algorithm: Naive Bayes");
        writer.println("Evaluation: 3-fold stratified cross-validation (seed=1)");
        writer.println("Thresholds: Low <50, Medium 50-74, High ≥75");
        writer.println("================================================================================\n");
        
        // Confusion Matrix
        double[][] cm = eval.confusionMatrix();
        String[] classNames = {"Low", "Medium", "High"};
        
        writer.println("CONFUSION MATRIX:");
        writer.println("--------------------------------------------------------------------------------");
        writer.printf("%-20s %-10s %-10s %-10s %-10s%n", 
                     "Actual \\ Predicted", "Low", "Medium", "High", "Total");
        writer.println("--------------------------------------------------------------------------------");
        
        // Print matrix rows
        for (int i = 0; i < cm.length; i++) {
            int rowSum = 0;
            for (int j = 0; j < cm[i].length; j++) {
                rowSum += (int)cm[i][j];
            }
            
            writer.printf("%-20s %-10d %-10d %-10d %-10d%n",
                classNames[i] + " (n=" + rowSum + ")",
                (int)cm[i][0], (int)cm[i][1], (int)cm[i][2], rowSum);
        }
        
        writer.println("--------------------------------------------------------------------------------");
        
        // Recall row
        writer.printf("%-20s ", "Recall (%)");
        for (int i = 0; i < cm.length; i++) {
            int rowSum = 0;
            for (int j = 0; j < cm[i].length; j++) {
                rowSum += (int)cm[i][j];
            }
            double recall = (cm[i][i] / rowSum) * 100;
            writer.printf("%-10.1f ", recall);
        }
        
        // Total
        int total = 0;
        for (int i = 0; i < cm.length; i++) {
            for (int j = 0; j < cm[i].length; j++) {
                total += (int)cm[i][j];
            }
        }
        writer.printf("%-10d%n", total);
        writer.println("================================================================================\n");
        
        // Performance Metrics
        writer.println("PERFORMANCE METRICS:");
        writer.println("--------------------------------------------------------------------------------");
        writer.printf("Accuracy:                %.2f%%%n", eval.pctCorrect());
        writer.printf("Kappa Statistic:         %.3f%n", eval.kappa());
        writer.printf("Mean Absolute Error:     %.3f%n", eval.meanAbsoluteError());
        writer.printf("Root Mean Squared Error: %.3f%n", eval.rootMeanSquaredError());
        writer.printf("Precision (weighted):    %.2f%%%n", eval.weightedPrecision() * 100);
        writer.printf("Recall (weighted):       %.2f%%%n", eval.weightedRecall() * 100);
        writer.printf("F1-Score (weighted):     %.2f%%%n", eval.weightedFMeasure() * 100);
        writer.println("================================================================================\n");
        
        // Class-specific metrics
        writer.println("CLASS-SPECIFIC METRICS:");
        writer.println("--------------------------------------------------------------------------------");
        writer.printf("%-15s %-12s %-12s %-12s %-12s%n", 
                     "Class", "Precision", "Recall", "F1-Score", "Instances");
        writer.println("--------------------------------------------------------------------------------");
        
        for (int i = 0; i < cm.length; i++) {
            int rowSum = 0;
            for (int j = 0; j < cm[i].length; j++) {
                rowSum += (int)cm[i][j];
            }
            
            writer.printf("%-15s %-12.1f %-12.1f %-12.1f %-12d%n",
                classNames[i],
                eval.precision(i) * 100,
                eval.recall(i) * 100,
                eval.fMeasure(i) * 100,
                rowSum);
        }
        writer.println("================================================================================\n");
        
        // Weka's detailed output
        writer.println("WEKA DETAILED OUTPUT:");
        writer.println("================================================================================");
        writer.println(eval.toSummaryString("\nSummary:\n", false));
        writer.println("\n" + eval.toClassDetailsString());
        writer.println("\n" + eval.toMatrixString());
        writer.println("================================================================================");
        
        writer.close();
        
        System.out.println("Confusion matrix saved successfully to: " + filePath);
    }
    
    private void saveModelToStorage(Classifier classifier, String algorithm, Map<String, Object> metrics) {
        try {
            // SERIALIZE CLASSIFIER TO BYTE ARRAY (BLOB)
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(classifier);
            byte[] modelData = baos.toByteArray();
            oos.close();
            baos.close();
            
            // CREATE ModelStorage ENTITY
            ModelStorage modelStorage = new ModelStorage();
            modelStorage.setModelName("EmployeePerformanceModel");
            modelStorage.setAlgorithm(algorithm);
            modelStorage.setAccuracy((Double) metrics.get("accuracy"));
            modelStorage.setPrecisionScore((Double) metrics.get("precision"));
            modelStorage.setRecallScore((Double) metrics.get("recall"));
            modelStorage.setF1Score((Double) metrics.get("fMeasure"));
            modelStorage.setModelData(modelData);  // THE BLOB!
            
            // SAVE TO DATABASE
            em.persist(modelStorage);
            
            System.out.println("SUCCESS: Model saved to model_storage with BLOB (" + modelData.length + " bytes)");
            
        } catch (Exception e) {
            System.err.println("ERROR: Could not save model to model_storage: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @POST
    @Path("/predict")
    public Response predict(Map<String, Object> request) {
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("prediction", "Implementation pending");
            response.put("confidence", 0.0);
            return Response.ok(response).build();
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("error", e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorMap).build();
        }
    }
    
    @GET
    @Path("/dataset/stats")
    public Response getDatasetStats() {
        try {
            File datasetFile = new File(DATASET_PATH);
            if (!datasetFile.exists()) {
                return Response.ok("{\"error\":\"Dataset not found\"}").build();
            }

            DataSource source = new DataSource(DATASET_PATH);
            Instances data = source.getDataSet();
            
            StringBuilder json = new StringBuilder();
            json.append("{");
            json.append("\"totalInstances\":").append(data.numInstances()).append(",");
            json.append("\"totalAttributes\":").append(data.numAttributes()).append(",");
            json.append("\"relationName\":\"").append(data.relationName()).append("\"");
            json.append("}");
            
            return Response.ok(json.toString()).build();
            
        } catch (Exception e) {
            e.printStackTrace();
            return Response.ok("{\"error\":\"" + e.getMessage().replace("\"", "'") + "\"}").build();
        }
    }
    
    private Instances convertToCategorical(Instances data) throws Exception {
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
        
        Instances newData = new Instances("employee_performance_categorical", attributes, data.numInstances());
        
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
            
            // Thresholds: Low <50, Medium 50-74, High ≥75
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
    
    private Map<String, Object> evaluateModel(Classifier classifier, String name, Instances data) throws Exception {
        Evaluation eval = new Evaluation(data);
        eval.crossValidateModel(classifier, data, 3, new Random(1));
        
        Map<String, Object> result = new HashMap<>();
        result.put("algorithm", name);
        result.put("accuracy", round(eval.pctCorrect(), 2));
        result.put("precision", round(eval.weightedPrecision() * 100, 2));
        result.put("recall", round(eval.weightedRecall() * 100, 2));
        result.put("fMeasure", round(eval.weightedFMeasure() * 100, 2));
        result.put("kappa", round(eval.kappa(), 4));
        result.put("mae", round(eval.meanAbsoluteError(), 4));
        result.put("rmse", round(eval.rootMeanSquaredError(), 4));
        
        return result;
    }
    
    private Classifier getClassifier(String algorithm) {
        switch (algorithm.toLowerCase()) {
            case "j48":
            case "decision tree":
            case "j48 decision tree":
                J48 j48 = new J48();
                j48.setUnpruned(false);
                j48.setConfidenceFactor(0.25f);
                return j48;
                
            case "random forest":
                RandomForest rf = new RandomForest();
                rf.setNumIterations(30);
                rf.setNumFeatures(0);
                return rf;
                
            case "naive bayes":
                return new NaiveBayes();
                
            default:
                return null;
        }
    }
    
    private double round(double value, int places) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            return 0.0;
        }
        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
}