package com.musicband.grammy.adapter.resource;

import com.musicband.grammy.adapter.client.GrammySoapClient;
import com.musicband.grammy.adapter.client.SoapClientException;
import com.musicband.grammy.adapter.model.*;
import com.musicband.grammy.adapter.model.Error;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * REST adapter that proxies requests to SOAP Grammy Service.
 * This maintains the same REST API as before, but internally calls SOAP service.
 */
@Path("/band")
public class GrammyRestAdapterResource {

    @Inject
    private GrammySoapClient soapClient;

    @POST
    @Path("/{band-id}/singles/add")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public Response addSingleToBand(
            @PathParam("band-id") Integer bandId,
            Single single) {
        
        try {
            // Валидация входных данных
            if (bandId == null || bandId < 1) {
                return createErrorResponse(422, "Validation failed",
                        "Band ID must be a positive integer");
            }

            if (single == null) {
                return createErrorResponse(400, "Invalid request body",
                        "Request body cannot be null");
            }

            // Вызываем SOAP сервис через клиент
            AddSingleResponse response = soapClient.addSingleToBand(bandId, single);
            return Response.status(Response.Status.CREATED).entity(response).build();

        } catch (SoapClientException e) {
            return createErrorResponse(e.getErrorCode(), e.getErrorMessage(), e.getErrorDetails());
        } catch (Exception e) {
            e.printStackTrace();
            return createErrorResponse(500, "Internal server error",
                    "An unexpected error occurred: " + e.getMessage());
        }
    }

    @POST
    @Path("/{band-id}/participants/add")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public Response addParticipantToBand(
            @PathParam("band-id") Integer bandId,
            Participant participant) {

        try {
            // Валидация входных данных
            if (bandId == null || bandId < 1) {
                return createErrorResponse(422, "Validation failed",
                        "Band ID must be a positive integer");
            }

            if (participant == null) {
                return createErrorResponse(400, "Invalid request body",
                        "Request body cannot be null");
            }

            // Вызываем SOAP сервис через клиент
            AddParticipantResponse response = soapClient.addParticipantToBand(bandId, participant);
            return Response.status(Response.Status.CREATED).entity(response).build();

        } catch (SoapClientException e) {
            return createErrorResponse(e.getErrorCode(), e.getErrorMessage(), e.getErrorDetails());
        } catch (Exception e) {
            e.printStackTrace();
            return createErrorResponse(500, "Internal server error",
                    "An unexpected error occurred: " + e.getMessage());
        }
    }

    private Response createErrorResponse(int code, String message, String details) {
        Error error = new Error(code, message, details);
        Response.Status status;

        switch (code) {
            case 400:
                status = Response.Status.BAD_REQUEST;
                break;
            case 404:
                status = Response.Status.NOT_FOUND;
                break;
            case 422:
                status = Response.Status.fromStatusCode(422);
                break;
            case 503:
                status = Response.Status.SERVICE_UNAVAILABLE;
                break;
            default:
                status = Response.Status.INTERNAL_SERVER_ERROR;
        }

        return Response.status(status).entity(error).build();
    }
}
