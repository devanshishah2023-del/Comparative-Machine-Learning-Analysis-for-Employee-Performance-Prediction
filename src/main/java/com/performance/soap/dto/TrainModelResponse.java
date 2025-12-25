package com.performance.soap.dto;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "TrainModelResult")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TrainModelResult", propOrder = {"algorithm", "accuracy", "message", "success"})
public class TrainModelResponse {
    
    @XmlElement
    private String algorithm;
    
    @XmlElement
    private double accuracy;
    
    @XmlElement(required = true)
    private String message;
    
    @XmlElement(required = true)
    private boolean success;
    
    public TrainModelResponse() {}
    
    public TrainModelResponse(String algorithm, double accuracy, String message, boolean success) {
        this.algorithm = algorithm;
        this.accuracy = accuracy;
        this.message = message;
        this.success = success;
    }
    
    public String getAlgorithm() { return algorithm; }
    public void setAlgorithm(String algorithm) { this.algorithm = algorithm; }
    
    public double getAccuracy() { return accuracy; }
    public void setAccuracy(double accuracy) { this.accuracy = accuracy; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
}
