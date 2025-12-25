package com.performance.rest;

import com.performance.model.ModelHistory;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.text.SimpleDateFormat;
import java.util.List;

@Path("/models")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Stateless
public class ModelHistoryResource {
    
    @PersistenceContext(unitName = "PerformanceAnalyticsPU")
    private EntityManager em;
    
    @GET
    public Response getModelHistory() {
        try {
            List<ModelHistory> models = em.createQuery(
                "SELECT m FROM ModelHistory m ORDER BY m.trainingDate DESC", 
                ModelHistory.class
            ).getResultList();
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            
            StringBuilder json = new StringBuilder();
            json.append("{\"models\":[");
            
            for (int i = 0; i < models.size(); i++) {
                if (i > 0) json.append(",");
                ModelHistory m = models.get(i);
                json.append("{");
                json.append("\"id\":").append(m.getId()).append(",");
                json.append("\"algorithm\":\"").append(escape(m.getAlgorithm())).append("\",");
                json.append("\"accuracy\":").append(m.getAccuracy()).append(",");
                json.append("\"precision\":").append(m.getPrecisionScore()).append(",");
                json.append("\"recall\":").append(m.getRecallScore()).append(",");
                json.append("\"fMeasure\":").append(m.getFMeasure()).append(",");
                json.append("\"kappa\":").append(m.getKappa()).append(",");
                json.append("\"mae\":").append(m.getMae()).append(",");
                json.append("\"rmse\":").append(m.getRmse()).append(",");
                json.append("\"totalInstances\":").append(m.getTotalInstances()).append(",");
                json.append("\"sampledInstances\":").append(m.getSampledInstances()).append(",");
                json.append("\"trainedBy\":\"").append(escape(m.getTrainedBy() != null ? m.getTrainedBy() : "admin")).append("\",");
                json.append("\"trainingDate\":\"").append(sdf.format(m.getTrainingDate())).append("\"");
                json.append("}");
            }
            
            json.append("]}");
            
            return Response.ok(json.toString()).build();
            
        } catch (Exception e) {
            e.printStackTrace();
            return Response.ok("{\"models\":[]}").build();
        }
    }
    
    @POST
    public Response saveModel(ModelHistory model) {
        try {
            em.persist(model);
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
