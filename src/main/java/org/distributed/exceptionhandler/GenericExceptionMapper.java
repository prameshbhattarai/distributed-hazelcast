package org.distributed.exceptionhandler;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import org.glassfish.grizzly.http.util.HttpStatus;

public class GenericExceptionMapper implements ExceptionMapper<Exception> {

    private static final String REASON = "Request failed.";
    private static final String DESCRIPTION = "Request failed.";

    public GenericExceptionMapper() {
    }

    @Override
    public Response toResponse(Exception exception) {
        if (exception instanceof WebApplicationException webApplicationException) {
            var response = webApplicationException.getResponse();
            if (response.getStatus() != HttpStatus.INTERNAL_SERVER_ERROR_500.getStatusCode()) {
                return Response.status(response.getStatus()).entity(new ErrorResponse(response.getStatus(), exception.getMessage(), DESCRIPTION)).type(MediaType.APPLICATION_JSON_TYPE).build();
            }
        }

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .entity(new ErrorResponse(500, REASON, DESCRIPTION))
                .build();
    }
}
