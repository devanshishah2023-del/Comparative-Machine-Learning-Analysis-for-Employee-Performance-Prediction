package com.performance.service;

import com.performance.model.ModelStorage;
import com.performance.model.Prediction;
import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Stateless
public class PredictionServiceBean {
    
    @PersistenceContext(unitName = "PerformanceAnalyticsPU")
    private EntityManager em;
    
    @EJB
    private ModelTrainerBean modelTrainerBean;
    
    public Map<String, Object> predictPerformance(
            int employeeId, String department, String region, String education,
            String gender, String recruitmentChannel, int noOfTrainings, int age,
            int previousYearRating, int lengthOfService, int kpisMet, int awardsWon) throws Exception {
        
        System.out.println("========================================");
        System.out.println("PREDICTION REQUEST RECEIVED");
        System.out.println("Employee ID: " + employeeId);
        System.out.println("Department: " + department);
        
        // Load best model from database
        ModelStorage modelStorage = modelTrainerBean.getBestModel();
        if (modelStorage == null) {
            throw new Exception("No trained model found. Please train a model first.");
        }
        
        System.out.println("Loading model: " + modelStorage.getAlgorithm() + " (Accuracy: " + modelStorage.getAccuracy() + "%)");
        
        // Deserialize the classifier from BLOB
        ByteArrayInputStream bais = new ByteArrayInputStream(modelStorage.getModelData());
        ObjectInputStream ois = new ObjectInputStream(bais);
        Classifier classifier = (Classifier) ois.readObject();
        ois.close();
        
        System.out.println("Model loaded successfully from BLOB");
        
        // Create Weka attributes matching training data structure
        ArrayList<Attribute> attributes = new ArrayList<>();
        
        // 1. employee_id (numeric)
        attributes.add(new Attribute("employee_id"));
        
        // 2. department (nominal)
        ArrayList<String> deptValues = new ArrayList<>();
        deptValues.add("Analytics");
        deptValues.add("Finance");
        deptValues.add("HR");
        deptValues.add("Legal");
        deptValues.add("Operations");
        deptValues.add("Procurement");
        deptValues.add("R&D");
        deptValues.add("Sales & Marketing");
        deptValues.add("Technology");
        attributes.add(new Attribute("department", deptValues));
        
        // 3. region (nominal)
        ArrayList<String> regionValues = new ArrayList<>();
        for (int i = 1; i <= 34; i++) {
            regionValues.add("region_" + i);
        }
        attributes.add(new Attribute("region", regionValues));
        
        // 4. education (nominal)
        ArrayList<String> eduValues = new ArrayList<>();
        eduValues.add("Bachelors");
        eduValues.add("Below Secondary");
        eduValues.add("Masters & above");
        attributes.add(new Attribute("education", eduValues));
        
        // 5. gender (nominal)
        ArrayList<String> genderValues = new ArrayList<>();
        genderValues.add("f");
        genderValues.add("m");
        attributes.add(new Attribute("gender", genderValues));
        
        // 6. recruitment_channel (nominal)
        ArrayList<String> channelValues = new ArrayList<>();
        channelValues.add("other");
        channelValues.add("referred");
        channelValues.add("sourcing");
        attributes.add(new Attribute("recruitment_channel", channelValues));
        
        // 7. no_of_trainings (numeric)
        attributes.add(new Attribute("no_of_trainings"));
        
        // 8. age (numeric)
        attributes.add(new Attribute("age"));
        
        // 9. previous_year_rating (numeric)
        attributes.add(new Attribute("previous_year_rating"));
        
        // 10. length_of_service (numeric)
        attributes.add(new Attribute("length_of_service"));
        
        // 11. KPIs_met_more_than_80 (nominal)
        ArrayList<String> kpisValues = new ArrayList<>();
        kpisValues.add("0");
        kpisValues.add("1");
        attributes.add(new Attribute("KPIs_met_more_than_80", kpisValues));
        
        // 12. awards_won (nominal)
        ArrayList<String> awardsValues = new ArrayList<>();
        awardsValues.add("0");
        awardsValues.add("1");
        attributes.add(new Attribute("awards_won", awardsValues));
        
        // 13. performance_category (class attribute)
        ArrayList<String> performanceValues = new ArrayList<>();
        performanceValues.add("Low");
        performanceValues.add("Medium");
        performanceValues.add("High");
        attributes.add(new Attribute("performance_category", performanceValues));
        
        // Create Instances object
        Instances instances = new Instances("employee_performance_categorical", attributes, 0);
        instances.setClassIndex(instances.numAttributes() - 1);
        
        // Create instance with input values
        DenseInstance instance = new DenseInstance(13);
        instance.setDataset(instances);
        
        instance.setValue(0, employeeId);
        instance.setValue(1, department);
        instance.setValue(2, region);
        instance.setValue(3, education);
        instance.setValue(4, gender);
        instance.setValue(5, recruitmentChannel);
        instance.setValue(6, noOfTrainings);
        instance.setValue(7, age);
        instance.setValue(8, previousYearRating);
        instance.setValue(9, lengthOfService);
        instance.setValue(10, String.valueOf(kpisMet));
        instance.setValue(11, String.valueOf(awardsWon));
        
        instances.add(instance);
        
        System.out.println("Instance created successfully");
        
        // Make prediction using the trained model
        double prediction = classifier.classifyInstance(instances.instance(0));
        double[] distribution = classifier.distributionForInstance(instances.instance(0));
        
        String predictedClass = performanceValues.get((int) prediction);
        double confidence = distribution[(int) prediction] * 100;
        
        System.out.println("========================================");
        System.out.println("PREDICTION COMPLETED");
        System.out.println("Predicted Performance: " + predictedClass);
        System.out.println("Confidence: " + String.format("%.2f", confidence) + "%");
        System.out.println("========================================");
        
        // Save prediction to database (simplified - keeping old table structure for now)
        try {
            Prediction pred = new Prediction();
            pred.setEmployeeId(String.valueOf(employeeId));
            pred.setPredictedPerformance(predictedClass);
            pred.setConfidenceScore(confidence);
            // Set other fields with defaults or actual values
            pred.setSatisfactionLevel(0.0);
            pred.setLastEvaluation(0.0);
            pred.setNumberProject(0);
            pred.setAverageMonthlyHours(0);
            pred.setTimeSpendCompany(lengthOfService);
            pred.setWorkAccident(false);
            pred.setPromotionLast5Years(false);
            pred.setDepartment(department);
            pred.setSalary("medium");
            
            em.persist(pred);
            System.out.println("Prediction saved to database");
        } catch (Exception e) {
            System.err.println("Warning: Could not save to database: " + e.getMessage());
        }
        
        // Return result
        Map<String, Object> result = new HashMap<>();
        result.put("prediction", predictedClass);
        result.put("confidence", Math.round(confidence * 100.0) / 100.0);
        result.put("employeeId", employeeId);
        result.put("algorithm", modelStorage.getAlgorithm());
        
        return result;
    }
}
