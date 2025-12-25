package com.performance.soap.dto;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "PerformanceResult")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PerformanceResult", propOrder = {"employeeId", "predictedPerformance", "confidence", "message", "success"})
public class PerformanceResponse {
    
    @XmlElement(required = true)
    private String employeeId;
    
    @XmlElement(required = true)
    private String predictedPerformance;
    
    @XmlElement
    private double confidence;
    
    @XmlElement
    private String message;
    
    @XmlElement(required = true)
    private boolean success;
    
    public PerformanceResponse() {}
    
    public PerformanceResponse(String employeeId, String predictedPerformance, 
                              double confidence, String message, boolean success) {
        this.employeeId = employeeId;
        this.predictedPerformance = predictedPerformance;
        this.confidence = confidence;
        this.message = message;
        this.success = success;
    }
    
    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
    
    public String getPredictedPerformance() { return predictedPerformance; }
    public void setPredictedPerformance(String predictedPerformance) { 
        this.predictedPerformance = predictedPerformance; 
    }
    
    public double getConfidence() { return confidence; }
    public void setConfidence(double confidence) { this.confidence = confidence; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
}
