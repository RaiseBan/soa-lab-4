package com.musicband.api;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.PreMatching;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
@PreMatching
public class PreflightRequestFilter implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        if ("OPTIONS".equalsIgnoreCase(requestContext.getMethod())) {
            Response.ResponseBuilder builder = Response.ok();
            
            builder.header("Access-Control-Allow-Origin", "https://se.ifmo.ru");
            builder.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, PATCH, OPTIONS");
            builder.header("Access-Control-Allow-Headers", 
                "Origin, Content-Type, Accept, Authorization, X-Requested-With");
            builder.header("Access-Control-Max-Age", "3600");
            builder.header("Access-Control-Allow-Private-Network", "true");
            
            requestContext.abortWith(builder.build());
        }
    }
}