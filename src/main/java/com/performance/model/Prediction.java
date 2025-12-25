package com.performance.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "predictions")
public class Prediction implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "employee_id")
    private String employeeId;
    
    @Column(name = "satisfaction_level")
    private Double satisfactionLevel;
    
    @Column(name = "last_evaluation")
    private Double lastEvaluation;
    
    @Column(name = "number_project")
    private Integer numberProject;
    
    @Column(name = "average_montly_hours")
    private Integer averageMonthlyHours;
    
    @Column(name = "time_spend_company")
    private Integer timeSpendCompany;
    
    @Column(name = "work_accident")
    private Boolean workAccident;
    
    @Column(name = "promotion_last_5years")
    private Boolean promotionLast5Years;
    
    @Column(name = "department")
    private String department;
    
    @Column(name = "salary")
    private String salary;
    
    @Column(name = "predicted_performance")
    private String predictedPerformance;
    
    @Column(name = "confidence_score")
    private Double confidenceScore;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "prediction_date")
    private Date predictionDate;
    
    public Prediction() {
        this.predictionDate = new Date();
    }
    
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
    
    public Double getSatisfactionLevel() { return satisfactionLevel; }
    public void setSatisfactionLevel(Double satisfactionLevel) { this.satisfactionLevel = satisfactionLevel; }
    
    public Double getLastEvaluation() { return lastEvaluation; }
    public void setLastEvaluation(Double lastEvaluation) { this.lastEvaluation = lastEvaluation; }
    
    public Integer getNumberProject() { return numberProject; }
    public void setNumberProject(Integer numberProject) { this.numberProject = numberProject; }
    
    public Integer getAverageMonthlyHours() { return averageMonthlyHours; }
    public void setAverageMonthlyHours(Integer averageMonthlyHours) { this.averageMonthlyHours = averageMonthlyHours; }
    
    public Integer getTimeSpendCompany() { return timeSpendCompany; }
    public void setTimeSpendCompany(Integer timeSpendCompany) { this.timeSpendCompany = timeSpendCompany; }
    
    public Boolean getWorkAccident() { return workAccident; }
    public void setWorkAccident(Boolean workAccident) { this.workAccident = workAccident; }
    
    public Boolean getPromotionLast5Years() { return promotionLast5Years; }
    public void setPromotionLast5Years(Boolean promotionLast5Years) { this.promotionLast5Years = promotionLast5Years; }
    
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    
    public String getSalary() { return salary; }
    public void setSalary(String salary) { this.salary = salary; }
    
    public String getPredictedPerformance() { return predictedPerformance; }
    public void setPredictedPerformance(String predictedPerformance) { this.predictedPerformance = predictedPerformance; }
    
    public Double getConfidenceScore() { return confidenceScore; }
    public void setConfidenceScore(Double confidenceScore) { this.confidenceScore = confidenceScore; }
    
    public Date getPredictionDate() { return predictionDate; }
    public void setPredictionDate(Date predictionDate) { this.predictionDate = predictionDate; }
}
