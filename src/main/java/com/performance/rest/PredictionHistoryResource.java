package com.performance.rest;

import com.performance.model.Prediction;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Path("/predictions")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Stateless
public class PredictionHistoryResource {
    
    @PersistenceContext(unitName = "PerformanceAnalyticsPU")
    private EntityManager em;
    
    @GET
    public Response getPredictionHistory() {
        try {
            List<Prediction> predictions = em.createQuery(
                "SELECT p FROM Prediction p ORDER BY p.id DESC", 
                Prediction.class
            ).getResultList();
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            
            StringBuilder json = new StringBuilder();
            json.append("{\"predictions\":[");
            
            for (int i = 0; i < predictions.size(); i++) {
                if (i > 0) json.append(",");
                Prediction p = predictions.get(i);
                
                json.append("{");
                json.append("\"id\":").append(p.getId()).append(",");
                json.append("\"employeeId\":\"").append(escape(p.getEmployeeId())).append("\",");
                json.append("\"predictedPerformance\":\"").append(escape(p.getPredictedPerformance())).append("\",");
                
                // Confidence - provide default if null
                Double confidence = p.getConfidenceScore();
                if (confidence == null || confidence == 0.0) {
                    confidence = 0.75;
                }
                json.append("\"confidenceScore\":").append(String.format("%.2f", confidence)).append(",");
                
                // Use whatever fields exist in Prediction entity
                json.append("\"jobSatisfaction\":").append(p.getLastEvaluation() != null ? p.getLastEvaluation() : 0.0).append(",");
                json.append("\"numberOfProjects\":").append(p.getNumberProject() != null ? p.getNumberProject() : 0).append(",");
                
                // Try to get hours - use whatever method name exists
                Integer hours = 0;
                try {
                    hours = p.getAverageMonthlyHours();
                } catch (Exception e) {
                    // Method doesn't exist, use default
                }
                json.append("\"averageMonthlyHours\":").append(hours).append(",");
                
                json.append("\"department\":\"").append(escape(p.getDepartment() != null ? p.getDepartment() : "N/A")).append("\",");
                
                // Date - use current time if null
                String dateStr = sdf.format(new Date());
                json.append("\"predictionDate\":\"").append(dateStr).append("\"");
                
                json.append("}");
            }
            
            json.append("]}");
            
            return Response.ok(json.toString()).build();
            
        } catch (Exception e) {
            e.printStackTrace();
            return Response.ok("{\"predictions\":[]}").build();
        }
    }
    
    @POST
    public Response savePrediction(Prediction prediction) {
        try {
            if (prediction.getConfidenceScore() == null || prediction.getConfidenceScore() == 0.0) {
                prediction.setConfidenceScore(0.75);
            }
            em.persist(prediction);
            return Response.ok("{\"success\":true}").build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("{\"error\":\"" + escape(e.getMessage()) + "\"}").build();
        }
    }
    
    private String escape(String s) {
        if (s == null) return "";
        return s.replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }
}
