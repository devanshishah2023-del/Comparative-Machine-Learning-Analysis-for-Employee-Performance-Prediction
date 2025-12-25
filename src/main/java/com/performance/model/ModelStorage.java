package com.performance.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "model_storage")
public class ModelStorage implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "model_name", nullable = false)
    private String modelName;
    
    @Column(name = "algorithm", nullable = false)
    private String algorithm;
    
    @Column(name = "accuracy", nullable = false)
    private Double accuracy;
    
    @Column(name = "precision_score")
    private Double precisionScore;
    
    @Column(name = "recall_score")
    private Double recallScore;
    
    @Column(name = "f1_score")
    private Double f1Score;
    
    @Lob
    @Column(name = "model_data", nullable = false)
    private byte[] modelData;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date createdAt;
    
    public ModelStorage() {
        this.createdAt = new Date();
    }
    
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    public String getModelName() { return modelName; }
    public void setModelName(String modelName) { this.modelName = modelName; }
    
    public String getAlgorithm() { return algorithm; }
    public void setAlgorithm(String algorithm) { this.algorithm = algorithm; }
    
    public Double getAccuracy() { return accuracy; }
    public void setAccuracy(Double accuracy) { this.accuracy = accuracy; }
    
    public Double getPrecisionScore() { return precisionScore; }
    public void setPrecisionScore(Double precisionScore) { this.precisionScore = precisionScore; }
    
    public Double getRecallScore() { return recallScore; }
    public void setRecallScore(Double recallScore) { this.recallScore = recallScore; }
    
    public Double getF1Score() { return f1Score; }
    public void setF1Score(Double f1Score) { this.f1Score = f1Score; }
    
    public byte[] getModelData() { return modelData; }
    public void setModelData(byte[] modelData) { this.modelData = modelData; }
    
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}
