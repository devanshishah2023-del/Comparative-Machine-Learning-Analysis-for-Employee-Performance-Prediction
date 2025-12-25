package com.performance.rest;

import com.performance.service.PredictionServiceBean;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;

@Path("/predict")
@Stateless
public class PredictionResource {
    
    @EJB
    private PredictionServiceBean predictionServiceBean;
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response predict(Map<String, Object> request) {
        try {
            int employeeId = ((Number) request.get("employeeId")).intValue();
            String department = (String) request.get("department");
            String region = (String) request.get("region");
            String education = (String) request.get("education");
            String gender = (String) request.get("gender");
            String recruitmentChannel = (String) request.get("recruitmentChannel");
            int noOfTrainings = ((Number) request.get("noOfTrainings")).intValue();
            int age = ((Number) request.get("age")).intValue();
            int previousYearRating = ((Number) request.get("previousYearRating")).intValue();
            int lengthOfService = ((Number) request.get("lengthOfService")).intValue();
            int kpisMet = ((Number) request.get("kpisMet")).intValue();
            int awardsWon = ((Number) request.get("awardsWon")).intValue();
            
            Map<String, Object> result = predictionServiceBean.predictPerformance(
                employeeId, department, region, education, gender,
                recruitmentChannel, noOfTrainings, age, previousYearRating,
                lengthOfService, kpisMet, awardsWon
            );
            
            return Response.ok(result).build();
            
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        }
    }
}
