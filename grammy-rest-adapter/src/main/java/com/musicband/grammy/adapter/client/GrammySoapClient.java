package com.musicband.grammy.adapter.client;

import com.musicband.grammy.adapter.model.*;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.xml.soap.*;
import jakarta.xml.ws.soap.SOAPFaultException;

import javax.net.ssl.*;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.time.LocalDate;
import java.util.logging.Logger;

@ApplicationScoped
public class GrammySoapClient {

    private static final Logger LOGGER = Logger.getLogger(GrammySoapClient.class.getName());
    private static final String NAMESPACE = "http://grammy.musicband.com/";
    private static final String SERVICE_URL_PROPERTY = "grammy.soap.service.url";
    private static final String DEFAULT_URL = "https://localhost:9443/grammy-soap-service/GrammyService";

    @PostConstruct
    public void init() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() { return null; }
                        public void checkClientTrusted(X509Certificate[] certs, String authType) { }
                        public void checkServerTrusted(X509Certificate[] certs, String authType) { }
                    }
            };

            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            HostnameVerifier allHostsValid = (hostname, session) -> true;
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

            LOGGER.info("SSL configured to trust all certificates");
        } catch (Exception e) {
            LOGGER.severe("Failed to configure SSL: " + e.getMessage());
        }
    }

    private String getServiceUrl() {
        return System.getProperty(SERVICE_URL_PROPERTY, DEFAULT_URL);
    }

    public AddSingleResponse addSingleToBand(Integer bandId, Single single) throws SoapClientException {
        try {
            LOGGER.info("Calling SOAP service at: " + getServiceUrl());

            MessageFactory messageFactory = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);
            SOAPMessage soapMessage = messageFactory.createMessage();
            SOAPPart soapPart = soapMessage.getSOAPPart();

            SOAPEnvelope envelope = soapPart.getEnvelope();

            SOAPBody soapBody = envelope.getBody();

            // Операция с namespace
            SOAPElement operation = soapBody.addChildElement("addSingleToBand", "", NAMESPACE);

            // Дочерние элементы с ЯВНЫМ пустым namespace
            SOAPElement bandIdElement = operation.addChildElement("bandId");
            bandIdElement.removeNamespaceDeclaration("");
            bandIdElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns", "");
            bandIdElement.addTextNode(bandId.toString());

            SOAPElement singleElement = operation.addChildElement("single");
            singleElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns", "");

            addChildWithEmptyNS(singleElement, "title", single.getTitle());
            addChildWithEmptyNS(singleElement, "duration", single.getDuration().toString());
            addChildWithEmptyNS(singleElement, "releaseDate", single.getReleaseDate().toString());

            if (single.getChartPosition() != null) {
                addChildWithEmptyNS(singleElement, "chartPosition", single.getChartPosition().toString());
            }

            soapMessage.saveChanges();

            logSoapMessage("Request", soapMessage);

            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection soapConnection = soapConnectionFactory.createConnection();

            URL endpoint = new URL(getServiceUrl());
            LOGGER.info("Sending SOAP request to: " + endpoint);

            SOAPMessage soapResponse = soapConnection.call(soapMessage, endpoint);

            soapConnection.close();

            logSoapMessage("Response", soapResponse);

            return parseAddSingleResponse(soapResponse);

        } catch (SOAPFaultException e) {
            LOGGER.severe("SOAP Fault Exception: " + e.getMessage());
            throw handleSoapFault(e);
        } catch (SOAPException e) {
            LOGGER.severe("SOAP Exception: " + e.getMessage());
            e.printStackTrace();
            throw new SoapClientException(500, "SOAP communication error", e.getMessage());
        } catch (SoapClientException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.severe("General Exception: " + e.getClass().getName() + " - " + e.getMessage());
            e.printStackTrace();
            throw new SoapClientException(500, "Internal error", e.getMessage());
        }
    }

    public AddParticipantResponse addParticipantToBand(Integer bandId, Participant participant)
            throws SoapClientException {
        try {
            LOGGER.info("Calling SOAP service at: " + getServiceUrl());

            MessageFactory messageFactory = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);
            SOAPMessage soapMessage = messageFactory.createMessage();
            SOAPPart soapPart = soapMessage.getSOAPPart();

            SOAPEnvelope envelope = soapPart.getEnvelope();

            SOAPBody soapBody = envelope.getBody();

            // Операция с namespace
            SOAPElement operation = soapBody.addChildElement("addParticipantToBand", "", NAMESPACE);

            // Дочерние элементы с ЯВНЫМ пустым namespace
            SOAPElement bandIdElement = operation.addChildElement("bandId");
            bandIdElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns", "");
            bandIdElement.addTextNode(bandId.toString());

            SOAPElement participantElement = operation.addChildElement("participant");
            participantElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns", "");

            addChildWithEmptyNS(participantElement, "name", participant.getName());
            addChildWithEmptyNS(participantElement, "role", participant.getRole());
            addChildWithEmptyNS(participantElement, "joinDate", participant.getJoinDate().toString());

            if (participant.getInstrument() != null) {
                addChildWithEmptyNS(participantElement, "instrument", participant.getInstrument());
            }

            soapMessage.saveChanges();

            logSoapMessage("Request", soapMessage);

            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection soapConnection = soapConnectionFactory.createConnection();

            URL endpoint = new URL(getServiceUrl());
            LOGGER.info("Sending SOAP request to: " + endpoint);

            SOAPMessage soapResponse = soapConnection.call(soapMessage, endpoint);

            soapConnection.close();

            logSoapMessage("Response", soapResponse);

            return parseAddParticipantResponse(soapResponse);

        } catch (SOAPFaultException e) {
            LOGGER.severe("SOAP Fault Exception: " + e.getMessage());
            throw handleSoapFault(e);
        } catch (SOAPException e) {
            LOGGER.severe("SOAP Exception: " + e.getMessage());
            e.printStackTrace();
            throw new SoapClientException(500, "SOAP communication error", e.getMessage());
        } catch (SoapClientException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.severe("General Exception: " + e.getClass().getName() + " - " + e.getMessage());
            e.printStackTrace();
            throw new SoapClientException(500, "Internal error", e.getMessage());
        }
    }

    private void addChildWithEmptyNS(SOAPElement parent, String name, String value) throws SOAPException {
        if (value != null) {
            SOAPElement child = parent.addChildElement(name);
            // Не нужно устанавливать xmlns="" для детей, они унаследуют от parent
            child.addTextNode(value);
        }
    }

    private AddSingleResponse parseAddSingleResponse(SOAPMessage soapMessage) throws SOAPException, SoapClientException {
        SOAPBody body = soapMessage.getSOAPBody();

        if (body.hasFault()) {
            SOAPFault fault = body.getFault();
            throw new SoapClientException(500, "SOAP Fault", fault.getFaultString());
        }

        AddSingleResponse response = new AddSingleResponse();
        Single single = new Single();
        AddSingleResponse.BandInfo bandInfo = new AddSingleResponse.BandInfo();

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

    private AddParticipantResponse parseAddParticipantResponse(SOAPMessage soapMessage) throws SOAPException, SoapClientException {
        SOAPBody body = soapMessage.getSOAPBody();

        if (body.hasFault()) {
            SOAPFault fault = body.getFault();
            throw new SoapClientException(500, "SOAP Fault", fault.getFaultString());
        }

        AddParticipantResponse response = new AddParticipantResponse();
        Participant participant = new Participant();
        AddParticipantResponse.BandInfo bandInfo = new AddParticipantResponse.BandInfo();

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