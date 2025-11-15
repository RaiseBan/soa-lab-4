package com.musicband.grammy.soap;

import com.musicband.grammy.ejb.ParticipantServiceRemote;
import com.musicband.grammy.ejb.SingleServiceRemote;
import com.musicband.grammy.model.*;
import jakarta.ejb.EJB;
import jakarta.jws.WebService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.util.Set;
import java.util.stream.Collectors;

@WebService(
        serviceName = "GrammyService",
        portName = "GrammyServicePort",
        endpointInterface = "com.musicband.grammy.soap.GrammyWebService",
        targetNamespace = "http://grammy.musicband.com/"
)
public class GrammyWebServiceImpl implements GrammyWebService {

    @EJB
    private SingleServiceRemote singleService;

    @EJB
    private ParticipantServiceRemote participantService;

    private final Validator validator;

    public GrammyWebServiceImpl() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    @Override
    public AddSingleResponse addSingleToBand(Integer bandId, Single single) throws GrammyServiceFault {
        try {
            // Валидация bandId
            if (bandId == null || bandId < 1) {
                throw new GrammyServiceFault(
                        "Validation failed",
                        new GrammyServiceFault.FaultInfo(422, "Validation failed", 
                                "Band ID must be a positive integer")
                );
            }

            // Валидация single
            if (single == null) {
                throw new GrammyServiceFault(
                        "Invalid request",
                        new GrammyServiceFault.FaultInfo(400, "Invalid request", 
                                "Single cannot be null")
                );
            }

            // Bean Validation
            Set<ConstraintViolation<Single>> violations = validator.validate(single);
            if (!violations.isEmpty()) {
                String details = violations.stream()
                        .map(ConstraintViolation::getMessage)
                        .collect(Collectors.joining(", "));
                throw new GrammyServiceFault(
                        "Business validation failed",
                        new GrammyServiceFault.FaultInfo(422, "Business validation failed", details)
                );
            }

            return singleService.addSingleToBand(bandId, single);

        } catch (GrammyServiceFault e) {
            throw e;
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("not found")) {
                throw new GrammyServiceFault(
                        "Music band not found",
                        new GrammyServiceFault.FaultInfo(404, "Music band not found", e.getMessage())
                );
            }
            throw new GrammyServiceFault(
                    "Business validation failed",
                    new GrammyServiceFault.FaultInfo(422, "Business validation failed", e.getMessage())
            );
        } catch (RuntimeException e) {
            if (e.getMessage() != null && e.getMessage().contains("Main API service unavailable")) {
                throw new GrammyServiceFault(
                        "Main API service unavailable",
                        new GrammyServiceFault.FaultInfo(503, "Main API service unavailable", 
                                "Unable to connect to Music Band Management API")
                );
            }
            throw new GrammyServiceFault(
                    "Internal server error",
                    new GrammyServiceFault.FaultInfo(500, "Internal server error", 
                            "An unexpected error occurred: " + e.getMessage())
            );
        } catch (Exception e) {
            throw new GrammyServiceFault(
                    "Internal server error",
                    new GrammyServiceFault.FaultInfo(500, "Internal server error", 
                            "An unexpected error occurred: " + e.getMessage())
            );
        }
    }

    @Override
    public AddParticipantResponse addParticipantToBand(Integer bandId, Participant participant) 
            throws GrammyServiceFault {
        try {
            // Валидация bandId
            if (bandId == null || bandId < 1) {
                throw new GrammyServiceFault(
                        "Validation failed",
                        new GrammyServiceFault.FaultInfo(422, "Validation failed", 
                                "Band ID must be a positive integer")
                );
            }

            // Валидация participant
            if (participant == null) {
                throw new GrammyServiceFault(
                        "Invalid request",
                        new GrammyServiceFault.FaultInfo(400, "Invalid request", 
                                "Participant cannot be null")
                );
            }

            // Bean Validation
            Set<ConstraintViolation<Participant>> violations = validator.validate(participant);
            if (!violations.isEmpty()) {
                String details = violations.stream()
                        .map(ConstraintViolation::getMessage)
                        .collect(Collectors.joining(", "));
                throw new GrammyServiceFault(
                        "Business validation failed",
                        new GrammyServiceFault.FaultInfo(422, "Business validation failed", details)
                );
            }

            return participantService.addParticipantToBand(bandId, participant);

        } catch (GrammyServiceFault e) {
            throw e;
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("not found")) {
                throw new GrammyServiceFault(
                        "Music band not found",
                        new GrammyServiceFault.FaultInfo(404, "Music band not found", e.getMessage())
                );
            }
            throw new GrammyServiceFault(
                    "Business validation failed",
                    new GrammyServiceFault.FaultInfo(422, "Business validation failed", e.getMessage())
            );
        } catch (RuntimeException e) {
            if (e.getMessage() != null && e.getMessage().contains("Main API service unavailable")) {
                throw new GrammyServiceFault(
                        "Main API service unavailable",
                        new GrammyServiceFault.FaultInfo(503, "Main API service unavailable", 
                                "Unable to connect to Music Band Management API")
                );
            }
            throw new GrammyServiceFault(
                    "Internal server error",
                    new GrammyServiceFault.FaultInfo(500, "Internal server error", 
                            "An unexpected error occurred: " + e.getMessage())
            );
        } catch (Exception e) {
            throw new GrammyServiceFault(
                    "Internal server error",
                    new GrammyServiceFault.FaultInfo(500, "Internal server error", 
                            "An unexpected error occurred: " + e.getMessage())
            );
        }
    }
}
