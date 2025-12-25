package com.performance.service;

import com.performance.model.ModelStorage;
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
import weka.filters.supervised.instance.Resample;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;

@Stateless
public class ModelTrainerBean {
    
    private static final Logger LOGGER = Logger.getLogger(ModelTrainerBean.class.getName());
    
    @PersistenceContext(unitName = "PerformanceAnalyticsPU")
    private EntityManager em;
    
    // Dataset path (configurable)
    private static final String DEFAULT_ARFF_PATH = 
        System.getProperty("jboss.server.data.dir") + "/employee_performance.arff";
    
    /**
     * Get best performing model from database
     */
    public ModelStorage getBestModel() {
        List<ModelStorage> models = em.createQuery(
            "SELECT m FROM ModelStorage m ORDER BY m.accuracy DESC", ModelStorage.class)
            .setMaxResults(1)
            .getResultList();
        
        return models.isEmpty() ? null : models.get(0);
    }
    
    /**
     * Compare all three algorithms and return REAL accuracies
     */
    public Map<String, Double> compareAlgorithms(String datasetPath) {
        Map<String, Double> result = new HashMap<>();
        
        try {
            String arffPath = (datasetPath != null && !datasetPath.isEmpty()) 
                ? datasetPath : DEFAULT_ARFF_PATH;
            
            LOGGER.info("Loading dataset from: " + arffPath);
            
            // Load and prepare dataset
            Instances data = loadAndPrepareDataset(arffPath);
            
            // Train and evaluate each algorithm
            LOGGER.info("Training J48 Decision Tree...");
            double j48Accuracy = trainAndEvaluate(new J48(), data);
            result.put("J48", j48Accuracy);
            LOGGER.info("J48 Accuracy: " + j48Accuracy + "%");
            
            LOGGER.info("Training Random Forest...");
            RandomForest rf = new RandomForest();
            rf.setNumIterations(100);
            double rfAccuracy = trainAndEvaluate(rf, data);
            result.put("RandomForest", rfAccuracy);
            LOGGER.info("Random Forest Accuracy: " + rfAccuracy + "%");
            
            LOGGER.info("Training Naive Bayes...");
            double nbAccuracy = trainAndEvaluate(new NaiveBayes(), data);
            result.put("NaiveBayes", nbAccuracy);
            LOGGER.info("Naive Bayes Accuracy: " + nbAccuracy + "%");
            
            LOGGER.info("Algorithm comparison complete!");
            
        } catch (Exception e) {
            LOGGER.severe("Error comparing algorithms: " + e.getMessage());
            e.printStackTrace();
            // Return error indicator instead of fake values
            result.put("ERROR", -1.0);
            result.put("J48", 0.0);
            result.put("RandomForest", 0.0);
            result.put("NaiveBayes", 0.0);
        }
        
        return result;
    }
    
    /**
     * Train specific algorithm and save to database
     */
    public String trainAndSaveModel(String algorithm, String datasetPath) {
        try {
            String arffPath = (datasetPath != null && !datasetPath.isEmpty()) 
                ? datasetPath : DEFAULT_ARFF_PATH;
            
            LOGGER.info("Training " + algorithm + " model...");
            
            // Load dataset
            Instances data = loadAndPrepareDataset(arffPath);
            
            // Create classifier
            Classifier classifier = createClassifier(algorithm);
            
            // Evaluate with cross-validation
            Evaluation eval = new Evaluation(data);
            eval.crossValidateModel(classifier, data, 10, new Random(1));
            
            // Train on full dataset
            classifier.buildClassifier(data);
            
            // Serialize model
            byte[] serializedModel = serializeModel(classifier);
            
            // Save to database
            ModelStorage modelStorage = new ModelStorage();
            modelStorage.setAlgorithm(algorithm);
            modelStorage.setModelName(algorithm + "_" + System.currentTimeMillis());
            modelStorage.setModelData(serializedModel);
            modelStorage.setAccuracy(eval.pctCorrect());
            modelStorage.setPrecisionScore(eval.weightedPrecision() * 100);
            modelStorage.setRecallScore(eval.weightedRecall() * 100);
            modelStorage.setF1Score(eval.weightedFMeasure() * 100);
            modelStorage.setCreatedAt(new Date());
            
            em.persist(modelStorage);
            
            LOGGER.info("Model saved successfully! ID: " + modelStorage.getId());
            
            return String.format("Model trained successfully! Accuracy: %.2f%%", eval.pctCorrect());
            
        } catch (Exception e) {
            LOGGER.severe("Error training model: " + e.getMessage());
            e.printStackTrace();
            return "Error training model: " + e.getMessage();
        }
    }
    
    /**
     * Train Naive Bayes and generate confusion matrix file
     */
    public void trainAndSaveConfusionMatrix() throws Exception {
        trainAndSaveConfusionMatrix(DEFAULT_ARFF_PATH);
    }
    
    /**
     * Train Naive Bayes and generate confusion matrix file with custom dataset path
     */
    public void trainAndSaveConfusionMatrix(String datasetPath) throws Exception {
        LOGGER.info("Generating confusion matrix...");
        
        // Load and prepare data
        Instances data = loadAndPrepareDataset(datasetPath);
        
        // Train and evaluate Naive Bayes
        NaiveBayes nb = new NaiveBayes();
        Evaluation eval = new Evaluation(data);
        eval.crossValidateModel(nb, data, 10, new Random(1));
        
        // Save confusion matrix to file
        String logDir = System.getProperty("jboss.server.log.dir");
        if (logDir == null) {
            logDir = System.getProperty("user.dir");
        }
        String outputPath = logDir + "/confusion_matrix.txt";
        
        saveConfusionMatrixToFile(eval, outputPath);
        
        LOGGER.info("Confusion matrix saved to: " + outputPath);
        System.out.println("\n✓ Confusion matrix generated!");
        System.out.println("✓ View with: cat " + outputPath);
    }
    
    /**
     * Load dataset, convert to categorical, and apply stratified sampling to 2000 instances
     */
    private Instances loadAndPrepareDataset(String arffPath) throws Exception {
        // Step 1: Load original dataset
        DataSource source = new DataSource(arffPath);
        Instances data = source.getDataSet();
        
        // Set class index (last attribute - numeric avg_training_score)
        if (data.classIndex() == -1) {
            data.setClassIndex(data.numAttributes() - 1);
        }
        
        LOGGER.info("Original dataset: " + data.numInstances() + " instances");
        
        // Step 2: Convert numeric class to categorical (Low/Medium/High)
        LOGGER.info("Converting numeric class to categorical...");
        Instances categoricalData = convertToCategorical(data);
        categoricalData.setClassIndex(categoricalData.numAttributes() - 1);
        
        LOGGER.info("Class attribute after conversion: " + categoricalData.classAttribute().name());
        LOGGER.info("Class type: " + (categoricalData.classAttribute().isNominal() ? "NOMINAL" : "NUMERIC"));
        
        // Step 3: Apply stratified sampling to 2000 instances
        Instances sampledData;
        if (categoricalData.numInstances() > 2000) {
            LOGGER.info("Applying stratified sampling to 2000 instances...");
            
            Resample resample = new Resample();
            resample.setInputFormat(categoricalData);
            resample.setSampleSizePercent((2000.0 / categoricalData.numInstances()) * 100);
            resample.setNoReplacement(true);
            resample.setRandomSeed(1); // Same seed for reproducibility
            
            sampledData = Filter.useFilter(categoricalData, resample);
            LOGGER.info("Sampled dataset: " + sampledData.numInstances() + " instances");
        } else {
            sampledData = categoricalData;
            LOGGER.info("Dataset already has " + sampledData.numInstances() + " instances, no sampling needed");
        }
        
        return sampledData;
    }
    
    /**
     * Convert numeric avg_training_score to categorical performance_category
     * ORIGINAL THRESHOLDS (Best performance: ~79.53% accuracy)
     * Low: < 50, Medium: 50-74, High: >= 75
     */
    private Instances convertToCategorical(Instances data) throws Exception {
        LOGGER.info("Converting numeric avg_training_score to categorical performance_category");
        LOGGER.info("Using thresholds: Low <50, Medium 50-74, High ≥75");
        
        // Create new attribute list (copy all except last attribute)
        ArrayList<Attribute> attributes = new ArrayList<>();
        
        for (int i = 0; i < data.numAttributes() - 1; i++) {
            attributes.add((Attribute) data.attribute(i).copy());
        }
        
        // Add categorical class attribute
        ArrayList<String> performanceLevels = new ArrayList<>();
        performanceLevels.add("Low");
        performanceLevels.add("Medium");
        performanceLevels.add("High");
        Attribute performanceCategory = new Attribute("performance_category", performanceLevels);
        attributes.add(performanceCategory);
        
        // Create new dataset with categorical class
        Instances newData = new Instances("employee_performance_categorical", 
                                          attributes, data.numInstances());
        newData.setClassIndex(newData.numAttributes() - 1);
        
        // Copy instances and convert numeric scores to categories
        int lowCount = 0, mediumCount = 0, highCount = 0;
        
        for (int i = 0; i < data.numInstances(); i++) {
            Instance oldInst = data.instance(i);
            Instance newInst = new DenseInstance(newData.numAttributes());
            newInst.setDataset(newData);
            
            // Copy all attributes except class
            for (int j = 0; j < data.numAttributes() - 1; j++) {
                if (oldInst.isMissing(j)) {
                    newInst.setMissing(j);
                } else {
                    newInst.setValue(j, oldInst.value(j));
                }
            }
            
            // ========================================================================
            // ORIGINAL THRESHOLDS (Gives ~79.53% accuracy - BEST RESULT)
            // ========================================================================
            double score = oldInst.value(data.numAttributes() - 1);
            
            if (score < 50) {
                newInst.setValue(newData.numAttributes() - 1, "Low");
                lowCount++;
            } else if (score < 75) {
                newInst.setValue(newData.numAttributes() - 1, "Medium");
                mediumCount++;
            } else {
                newInst.setValue(newData.numAttributes() - 1, "High");
                highCount++;
            }
            // ========================================================================
            
            newData.add(newInst);
        }
        
        // Log distribution
        LOGGER.info("Categorical conversion complete:");
        LOGGER.info("  Low (<50):      " + lowCount + " (" + 
                   String.format("%.2f", (lowCount*100.0/newData.numInstances())) + "%)");
        LOGGER.info("  Medium (50-74): " + mediumCount + " (" + 
                   String.format("%.2f", (mediumCount*100.0/newData.numInstances())) + "%)");
        LOGGER.info("  High (≥75):     " + highCount + " (" + 
                   String.format("%.2f", (highCount*100.0/newData.numInstances())) + "%)");
        LOGGER.info("  Total:          " + newData.numInstances());
        
        return newData;
    }
    
    /**
     * Train and evaluate classifier with 10-fold cross-validation
     */
    private double trainAndEvaluate(Classifier classifier, Instances data) throws Exception {
        Evaluation eval = new Evaluation(data);
        eval.crossValidateModel(classifier, data, 10, new Random(1));
        return eval.pctCorrect();
    }
    
    /**
     * Create classifier instance based on algorithm name
     */
    private Classifier createClassifier(String algorithm) throws Exception {
        switch (algorithm.toLowerCase()) {
            case "j48":
            case "j48 decision tree":
                J48 j48 = new J48();
                j48.setConfidenceFactor(0.25f);
                j48.setMinNumObj(2);
                return j48;
                
            case "randomforest":
            case "random forest":
                RandomForest rf = new RandomForest();
                rf.setNumIterations(100);
                return rf;
                
            case "naivebayes":
            case "naive bayes":
                return new NaiveBayes();
                
            default:
                throw new IllegalArgumentException("Unknown algorithm: " + algorithm);
        }
    }
    
    /**
     * Serialize Weka classifier to byte array
     */
    private byte[] serializeModel(Classifier classifier) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(classifier);
        oos.close();
        return baos.toByteArray();
    }
    
    /**
     * Deserialize Weka classifier from byte array
     */
    public Classifier deserializeModel(byte[] modelData) throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(modelData);
        ObjectInputStream ois = new ObjectInputStream(bais);
        Classifier classifier = (Classifier) ois.readObject();
        ois.close();
        return classifier;
    }
    
    /**
     * Save formatted confusion matrix to text file
     */
    private void saveConfusionMatrixToFile(Evaluation eval, String filePath) throws Exception {
        PrintWriter writer = new PrintWriter(new FileWriter(filePath));
        
        // Header
        writer.println("================================================================================");
        writer.println("NAIVE BAYES CONFUSION MATRIX");
        writer.println("================================================================================");
        writer.println("Generated: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        writer.println("Dataset: Employee Performance (N=2,000)");
        writer.println("Algorithm: Naive Bayes");
        writer.println("Evaluation: 10-fold stratified cross-validation (seed=1)");
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
    }
    
    /**
     * Get all trained models
     */
    public List<ModelStorage> getAllModels() {
        return em.createQuery(
            "SELECT m FROM ModelStorage m ORDER BY m.createdAt DESC", ModelStorage.class)
            .getResultList();
    }
    
    /**
     * Get model by ID
     */
    public ModelStorage getModelById(Integer id) {
        return em.find(ModelStorage.class, id);
    }
}