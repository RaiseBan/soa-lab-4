package com.musicband.grammy.soap;

import com.musicband.grammy.model.*;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebResult;
import jakarta.jws.WebService;
import jakarta.jws.soap.SOAPBinding;

@WebService(name = "GrammyWebService", targetNamespace = "http://grammy.musicband.com/")
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL)
public interface GrammyWebService {

    @WebMethod(operationName = "addSingleToBand")
    @WebResult(name = "addSingleResponse")
    AddSingleResponse addSingleToBand(
            @WebParam(name = "bandId") Integer bandId,
            @WebParam(name = "single") Single single
    ) throws GrammyServiceFault;

    @WebMethod(operationName = "addParticipantToBand")
    @WebResult(name = "addParticipantResponse")
    AddParticipantResponse addParticipantToBand(
            @WebParam(name = "bandId") Integer bandId,
            @WebParam(name = "participant") Participant participant
    ) throws GrammyServiceFault;
}
