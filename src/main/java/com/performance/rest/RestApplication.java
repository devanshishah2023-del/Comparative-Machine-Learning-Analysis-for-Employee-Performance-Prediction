package com.performance.rest;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/api")
public class RestApplication extends Application {
    
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();
        classes.add(AdminResource.class);
        classes.add(PredictionResource.class);
        classes.add(ModelHistoryResource.class);
        classes.add(PredictionHistoryResource.class);
        return classes;
    }
}
