package com.musicband.grammy.soap;

import jakarta.xml.ws.WebFault;

@WebFault(name = "GrammyServiceFault", targetNamespace = "http://grammy.musicband.com/")
public class GrammyServiceFault extends Exception {

    private FaultInfo faultInfo;

    public GrammyServiceFault(String message, FaultInfo faultInfo) {
        super(message);
        this.faultInfo = faultInfo;
    }

    public GrammyServiceFault(String message, FaultInfo faultInfo, Throwable cause) {
        super(message, cause);
        this.faultInfo = faultInfo;
    }

    public FaultInfo getFaultInfo() {
        return faultInfo;
    }

    public static class FaultInfo {
        private int code;
        private String message;
        private String details;

        public FaultInfo() {}

        public FaultInfo(int code, String message, String details) {
            this.code = code;
            this.message = message;
            this.details = details;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getDetails() {
            return details;
        }

        public void setDetails(String details) {
            this.details = details;
        }
    }
}
