package com.performance.soap.dto;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "PerformanceRequest")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"employeeId", "satisfactionLevel", "lastEvaluation", 
                      "numberProject", "averageMonthlyHours", "timeSpendCompany",
                      "workAccident", "promotionLast5Years", "department", "salary"})
public class PerformanceRequest {
    
    @XmlElement(required = true)
    private String employeeId;
    
    @XmlElement(required = true)
    private double satisfactionLevel;
    
    @XmlElement(required = true)
    private double lastEvaluation;
    
    @XmlElement(required = true)
    private int numberProject;
    
    @XmlElement(required = true)
    private int averageMonthlyHours;
    
    @XmlElement(required = true)
    private int timeSpendCompany;
    
    @XmlElement(required = true)
    private boolean workAccident;
    
    @XmlElement(required = true)
    private boolean promotionLast5Years;
    
    @XmlElement(required = true)
    private String department;
    
    @XmlElement(required = true)
    private String salary;
    
    public PerformanceRequest() {}
    
    // Getters and Setters
    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
    
    public double getSatisfactionLevel() { return satisfactionLevel; }
    public void setSatisfactionLevel(double satisfactionLevel) { this.satisfactionLevel = satisfactionLevel; }
    
    public double getLastEvaluation() { return lastEvaluation; }
    public void setLastEvaluation(double lastEvaluation) { this.lastEvaluation = lastEvaluation; }
    
    public int getNumberProject() { return numberProject; }
    public void setNumberProject(int numberProject) { this.numberProject = numberProject; }
    
    public int getAverageMonthlyHours() { return averageMonthlyHours; }
    public void setAverageMonthlyHours(int averageMonthlyHours) { this.averageMonthlyHours = averageMonthlyHours; }
    
    public int getTimeSpendCompany() { return timeSpendCompany; }
    public void setTimeSpendCompany(int timeSpendCompany) { this.timeSpendCompany = timeSpendCompany; }
    
    public boolean isWorkAccident() { return workAccident; }
    public void setWorkAccident(boolean workAccident) { this.workAccident = workAccident; }
    
    public boolean isPromotionLast5Years() { return promotionLast5Years; }
    public void setPromotionLast5Years(boolean promotionLast5Years) { this.promotionLast5Years = promotionLast5Years; }
    
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    
    public String getSalary() { return salary; }
    public void setSalary(String salary) { this.salary = salary; }
}
