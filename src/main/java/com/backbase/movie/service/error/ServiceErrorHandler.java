package com.backbase.movie.service.error;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ServiceErrorHandler implements ExceptionMapper<ServiceError> {
    @Override
    public Response toResponse(ServiceError exception) {
        return Response.status(exception.getStatusCode(), exception.getAddOnmessage()).build();
    }

}
