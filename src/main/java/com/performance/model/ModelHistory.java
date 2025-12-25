package com.performance.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "model_history")
public class ModelHistory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "algorithm")
    private String algorithm;
    
    @Column(name = "accuracy")
    private Double accuracy;
    
    @Column(name = "precision_score")
    private Double precisionScore;
    
    @Column(name = "recall_score")
    private Double recallScore;
    
    @Column(name = "f_measure")
    private Double fMeasure;
    
    @Column(name = "kappa")
    private Double kappa;
    
    @Column(name = "mae")
    private Double mae;
    
    @Column(name = "rmse")
    private Double rmse;
    
    @Column(name = "total_instances")
    private Integer totalInstances;
    
    @Column(name = "sampled_instances")
    private Integer sampledInstances;
    
    @Column(name = "training_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date trainingDate;
    
    @Column(name = "trained_by")
    private String trainedBy;
    
    public ModelHistory() {
        this.trainingDate = new Date();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getAlgorithm() { return algorithm; }
    public void setAlgorithm(String algorithm) { this.algorithm = algorithm; }
    
    public Double getAccuracy() { return accuracy; }
    public void setAccuracy(Double accuracy) { this.accuracy = accuracy; }
    
    public Double getPrecisionScore() { return precisionScore; }
    public void setPrecisionScore(Double precisionScore) { this.precisionScore = precisionScore; }
    
    public Double getRecallScore() { return recallScore; }
    public void setRecallScore(Double recallScore) { this.recallScore = recallScore; }
    
    public Double getFMeasure() { return fMeasure; }
    public void setFMeasure(Double fMeasure) { this.fMeasure = fMeasure; }
    
    public Double getKappa() { return kappa; }
    public void setKappa(Double kappa) { this.kappa = kappa; }
    
    public Double getMae() { return mae; }
    public void setMae(Double mae) { this.mae = mae; }
    
    public Double getRmse() { return rmse; }
    public void setRmse(Double rmse) { this.rmse = rmse; }
    
    public Integer getTotalInstances() { return totalInstances; }
    public void setTotalInstances(Integer totalInstances) { this.totalInstances = totalInstances; }
    
    public Integer getSampledInstances() { return sampledInstances; }
    public void setSampledInstances(Integer sampledInstances) { this.sampledInstances = sampledInstances; }
    
    public Date getTrainingDate() { return trainingDate; }
    public void setTrainingDate(Date trainingDate) { this.trainingDate = trainingDate; }
    
    public String getTrainedBy() { return trainedBy; }
    public void setTrainedBy(String trainedBy) { this.trainedBy = trainedBy; }
}
