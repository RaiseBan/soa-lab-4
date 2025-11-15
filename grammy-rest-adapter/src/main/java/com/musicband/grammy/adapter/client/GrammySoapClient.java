package com.musicband.grammy.adapter.client;

import com.musicband.grammy.adapter.model.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.xml.soap.*;
import jakarta.xml.ws.soap.SOAPFaultException;

import javax.xml.namespace.QName;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.time.LocalDate;
import java.util.logging.Logger;

@ApplicationScoped
public class GrammySoapClient {

    private static final Logger LOGGER = Logger.getLogger(GrammySoapClient.class.getName());
    private static final String NAMESPACE = "http://grammy.musicband.com/";
    private static final String SERVICE_URL_PROPERTY = "grammy.soap.service.url";
    private static final String DEFAULT_URL = "https://localhost:9443/grammy-soap-service/GrammyService";

    private String getServiceUrl() {
        return System.getProperty(SERVICE_URL_PROPERTY, DEFAULT_URL);
    }

    public AddSingleResponse addSingleToBand(Integer bandId, Single single) throws SoapClientException {
        try {
            // Создаем SOAP сообщение
            MessageFactory messageFactory = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);
            SOAPMessage soapMessage = messageFactory.createMessage();
            SOAPPart soapPart = soapMessage.getSOAPPart();

            // Создаем SOAP Envelope
            SOAPEnvelope envelope = soapPart.getEnvelope();
            envelope.addNamespaceDeclaration("gram", NAMESPACE);

            // Создаем SOAP Body
            SOAPBody soapBody = envelope.getBody();
            SOAPElement operation = soapBody.addChildElement("addSingleToBand", "gram");

            // Добавляем bandId
            SOAPElement bandIdElement = operation.addChildElement("bandId", "gram");
            bandIdElement.addTextNode(bandId.toString());

            // Добавляем single
            SOAPElement singleElement = operation.addChildElement("single", "gram");
            
            if (single.getTitle() != null) {
                SOAPElement titleElement = singleElement.addChildElement("title");
                titleElement.addTextNode(single.getTitle());
            }
            
            if (single.getDuration() != null) {
                SOAPElement durationElement = singleElement.addChildElement("duration");
                durationElement.addTextNode(single.getDuration().toString());
            }
            
            if (single.getReleaseDate() != null) {
                SOAPElement releaseDateElement = singleElement.addChildElement("releaseDate");
                releaseDateElement.addTextNode(single.getReleaseDate().toString());
            }
            
            if (single.getChartPosition() != null) {
                SOAPElement chartPositionElement = singleElement.addChildElement("chartPosition");
                chartPositionElement.addTextNode(single.getChartPosition().toString());
            }

            soapMessage.saveChanges();

            // Логируем запрос
            logSoapMessage("Request", soapMessage);

            // Отправляем запрос
            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection soapConnection = soapConnectionFactory.createConnection();

            URL endpoint = new URL(getServiceUrl());
            SOAPMessage soapResponse = soapConnection.call(soapMessage, endpoint);

            soapConnection.close();

            // Логируем ответ
            logSoapMessage("Response", soapResponse);

            // Парсим ответ
            return parseAddSingleResponse(soapResponse);

        } catch (SOAPFaultException e) {
            throw handleSoapFault(e);
        } catch (SOAPException e) {
            LOGGER.severe("SOAP Exception: " + e.getMessage());
            throw new SoapClientException(500, "SOAP communication error", e.getMessage());
        } catch (Exception e) {
            LOGGER.severe("General Exception: " + e.getMessage());
            throw new SoapClientException(500, "Internal error", e.getMessage());
        }
    }

    public AddParticipantResponse addParticipantToBand(Integer bandId, Participant participant) 
            throws SoapClientException {
        try {
            // Создаем SOAP сообщение
            MessageFactory messageFactory = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);
            SOAPMessage soapMessage = messageFactory.createMessage();
            SOAPPart soapPart = soapMessage.getSOAPPart();

            // Создаем SOAP Envelope
            SOAPEnvelope envelope = soapPart.getEnvelope();
            envelope.addNamespaceDeclaration("gram", NAMESPACE);

            // Создаем SOAP Body
            SOAPBody soapBody = envelope.getBody();
            SOAPElement operation = soapBody.addChildElement("addParticipantToBand", "gram");

            // Добавляем bandId
            SOAPElement bandIdElement = operation.addChildElement("bandId", "gram");
            bandIdElement.addTextNode(bandId.toString());

            // Добавляем participant
            SOAPElement participantElement = operation.addChildElement("participant", "gram");
            
            if (participant.getName() != null) {
                SOAPElement nameElement = participantElement.addChildElement("name");
                nameElement.addTextNode(participant.getName());
            }
            
            if (participant.getRole() != null) {
                SOAPElement roleElement = participantElement.addChildElement("role");
                roleElement.addTextNode(participant.getRole());
            }
            
            if (participant.getJoinDate() != null) {
                SOAPElement joinDateElement = participantElement.addChildElement("joinDate");
                joinDateElement.addTextNode(participant.getJoinDate().toString());
            }
            
            if (participant.getInstrument() != null) {
                SOAPElement instrumentElement = participantElement.addChildElement("instrument");
                instrumentElement.addTextNode(participant.getInstrument());
            }

            soapMessage.saveChanges();

            // Логируем запрос
            logSoapMessage("Request", soapMessage);

            // Отправляем запрос
            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection soapConnection = soapConnectionFactory.createConnection();

            URL endpoint = new URL(getServiceUrl());
            SOAPMessage soapResponse = soapConnection.call(soapMessage, endpoint);

            soapConnection.close();

            // Логируем ответ
            logSoapMessage("Response", soapResponse);

            // Парсим ответ
            return parseAddParticipantResponse(soapResponse);

        } catch (SOAPFaultException e) {
            throw handleSoapFault(e);
        } catch (SOAPException e) {
            LOGGER.severe("SOAP Exception: " + e.getMessage());
            throw new SoapClientException(500, "SOAP communication error", e.getMessage());
        } catch (Exception e) {
            LOGGER.severe("General Exception: " + e.getMessage());
            throw new SoapClientException(500, "Internal error", e.getMessage());
        }
    }

    private AddSingleResponse parseAddSingleResponse(SOAPMessage soapMessage) throws SOAPException {
        SOAPBody body = soapMessage.getSOAPBody();
        
        // Проверяем на fault
        if (body.hasFault()) {
            SOAPFault fault = body.getFault();
            throw new SoapClientException(500, "SOAP Fault", fault.getFaultString());
        }

        AddSingleResponse response = new AddSingleResponse();
        Single single = new Single();
        AddSingleResponse.BandInfo bandInfo = new AddSingleResponse.BandInfo();

        // Парсим response
        SOAPElement responseElement = (SOAPElement) body.getFirstChild();
        
        java.util.Iterator<?> iterator = responseElement.getChildElements();
        while (iterator.hasNext()) {
            Object obj = iterator.next();
            if (obj instanceof SOAPElement) {
                SOAPElement element = (SOAPElement) obj;
                String localName = element.getLocalName();
                
                if ("single".equals(localName)) {
                    single = parseSingleElement(element);
                } else if ("bandInfo".equals(localName)) {
                    bandInfo = parseBandInfoElement(element, new AddSingleResponse.BandInfo());
                }
            }
        }

        response.setSingle(single);
        response.setBandInfo(bandInfo);
        return response;
    }

    private AddParticipantResponse parseAddParticipantResponse(SOAPMessage soapMessage) throws SOAPException {
        SOAPBody body = soapMessage.getSOAPBody();
        
        // Проверяем на fault
        if (body.hasFault()) {
            SOAPFault fault = body.getFault();
            throw new SoapClientException(500, "SOAP Fault", fault.getFaultString());
        }

        AddParticipantResponse response = new AddParticipantResponse();
        Participant participant = new Participant();
        AddParticipantResponse.BandInfo bandInfo = new AddParticipantResponse.BandInfo();

        // Парсим response
        SOAPElement responseElement = (SOAPElement) body.getFirstChild();
        
        java.util.Iterator<?> iterator = responseElement.getChildElements();
        while (iterator.hasNext()) {
            Object obj = iterator.next();
            if (obj instanceof SOAPElement) {
                SOAPElement element = (SOAPElement) obj;
                String localName = element.getLocalName();
                
                if ("participant".equals(localName)) {
                    participant = parseParticipantElement(element);
                } else if ("updatedParticipantsCount".equals(localName)) {
                    response.setUpdatedParticipantsCount(Integer.parseInt(element.getTextContent()));
                } else if ("bandInfo".equals(localName)) {
                    bandInfo = parseParticipantBandInfoElement(element);
                }
            }
        }

        response.setParticipant(participant);
        response.setBandInfo(bandInfo);
        return response;
    }

    private Single parseSingleElement(SOAPElement singleElement) {
        Single single = new Single();
        
        java.util.Iterator<?> iterator = singleElement.getChildElements();
        while (iterator.hasNext()) {
            Object obj = iterator.next();
            if (obj instanceof SOAPElement) {
                SOAPElement element = (SOAPElement) obj;
                String localName = element.getLocalName();
                String value = element.getTextContent();
                
                switch (localName) {
                    case "id":
                        single.setId(Integer.parseInt(value));
                        break;
                    case "title":
                        single.setTitle(value);
                        break;
                    case "duration":
                        single.setDuration(Integer.parseInt(value));
                        break;
                    case "releaseDate":
                        single.setReleaseDate(LocalDate.parse(value));
                        break;
                    case "chartPosition":
                        if (value != null && !value.isEmpty()) {
                            single.setChartPosition(Integer.parseInt(value));
                        }
                        break;
                }
            }
        }
        
        return single;
    }

    private Participant parseParticipantElement(SOAPElement participantElement) {
        Participant participant = new Participant();
        
        java.util.Iterator<?> iterator = participantElement.getChildElements();
        while (iterator.hasNext()) {
            Object obj = iterator.next();
            if (obj instanceof SOAPElement) {
                SOAPElement element = (SOAPElement) obj;
                String localName = element.getLocalName();
                String value = element.getTextContent();
                
                switch (localName) {
                    case "id":
                        participant.setId(Integer.parseInt(value));
                        break;
                    case "name":
                        participant.setName(value);
                        break;
                    case "role":
                        participant.setRole(value);
                        break;
                    case "joinDate":
                        participant.setJoinDate(LocalDate.parse(value));
                        break;
                    case "instrument":
                        participant.setInstrument(value);
                        break;
                }
            }
        }
        
        return participant;
    }

    private AddSingleResponse.BandInfo parseBandInfoElement(SOAPElement bandInfoElement, 
                                                             AddSingleResponse.BandInfo bandInfo) {
        java.util.Iterator<?> iterator = bandInfoElement.getChildElements();
        while (iterator.hasNext()) {
            Object obj = iterator.next();
            if (obj instanceof SOAPElement) {
                SOAPElement element = (SOAPElement) obj;
                String localName = element.getLocalName();
                String value = element.getTextContent();
                
                if ("id".equals(localName)) {
                    bandInfo.setId(Integer.parseInt(value));
                } else if ("name".equals(localName)) {
                    bandInfo.setName(value);
                }
            }
        }
        return bandInfo;
    }

    private AddParticipantResponse.BandInfo parseParticipantBandInfoElement(SOAPElement bandInfoElement) {
        AddParticipantResponse.BandInfo bandInfo = new AddParticipantResponse.BandInfo();
        
        java.util.Iterator<?> iterator = bandInfoElement.getChildElements();
        while (iterator.hasNext()) {
            Object obj = iterator.next();
            if (obj instanceof SOAPElement) {
                SOAPElement element = (SOAPElement) obj;
                String localName = element.getLocalName();
                String value = element.getTextContent();
                
                if ("id".equals(localName)) {
                    bandInfo.setId(Integer.parseInt(value));
                } else if ("name".equals(localName)) {
                    bandInfo.setName(value);
                }
            }
        }
        return bandInfo;
    }

    private SoapClientException handleSoapFault(SOAPFaultException e) {
        String faultString = e.getFault().getFaultString();
        LOGGER.severe("SOAP Fault: " + faultString);
        
        // Пытаемся извлечь код ошибки из fault
        int errorCode = 500;
        if (faultString.contains("not found")) {
            errorCode = 404;
        } else if (faultString.contains("Validation") || faultString.contains("validation")) {
            errorCode = 422;
        } else if (faultString.contains("unavailable")) {
            errorCode = 503;
        }
        
        return new SoapClientException(errorCode, "Service error", faultString);
    }

    private void logSoapMessage(String type, SOAPMessage message) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            message.writeTo(out);
            LOGGER.info(type + ": " + out.toString());
        } catch (Exception e) {
            LOGGER.warning("Failed to log SOAP message: " + e.getMessage());
        }
    }
}
