package com.performance.soap;

import com.performance.soap.dto.*;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

@WebService(name = "PerformanceWebService", 
            targetNamespace = "http://soap.performance.com/")
public interface PerformanceWebService {
    
    @WebMethod(operationName = "predictPerformance")
    @WebResult(name = "PerformanceResponse")
    PerformanceResponse predictPerformance(
        @WebParam(name = "request") PerformanceRequest request
    );
    
    @WebMethod(operationName = "compareAlgorithms")
    @WebResult(name = "ModelComparisonResponse")
    ModelComparisonResponse compareAlgorithms();
    
    @WebMethod(operationName = "trainModel")
    @WebResult(name = "TrainModelResponse")
    TrainModelResponse trainModel(
        @WebParam(name = "algorithm") String algorithm
    );
    
    @WebMethod(operationName = "getModelInfo")
    @WebResult(name = "ModelInfo")
    String getModelInfo();
}
