package com.performance.soap.dto;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement(name = "ModelComparisonResult")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ModelComparisonResult", propOrder = {"results", "message", "success"})
public class ModelComparisonResponse {
    
    @XmlElement
    private List<AlgorithmResult> results;
    
    @XmlElement
    private String message;
    
    @XmlElement(required = true)
    private boolean success;
    
    public ModelComparisonResponse() {}
    
    public ModelComparisonResponse(List<AlgorithmResult> results, String message, boolean success) {
        this.results = results;
        this.message = message;
        this.success = success;
    }
    
    public List<AlgorithmResult> getResults() { return results; }
    public void setResults(List<AlgorithmResult> results) { this.results = results; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    
    @XmlRootElement(name = "AlgorithmResult")
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class AlgorithmResult {
        @XmlElement
        private String algorithmName;
        
        @XmlElement
        private double accuracy;
        
        public AlgorithmResult() {}
        
        public AlgorithmResult(String algorithmName, double accuracy) {
            this.algorithmName = algorithmName;
            this.accuracy = accuracy;
        }
        
        public String getAlgorithmName() { return algorithmName; }
        public void setAlgorithmName(String algorithmName) { this.algorithmName = algorithmName; }
        
        public double getAccuracy() { return accuracy; }
        public void setAccuracy(double accuracy) { this.accuracy = accuracy; }
    }
}
