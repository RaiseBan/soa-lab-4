package com.musicband.grammy.adapter.model;

import jakarta.xml.bind.annotation.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@XmlRootElement(name = "addSingleResponse")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
@NoArgsConstructor
public class AddSingleResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement
    private Single single;

    @XmlElement
    private BandInfo bandInfo;

    public AddSingleResponse(Single single, BandInfo bandInfo) {
        this.single = single;
        this.bandInfo = bandInfo;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @Data
    @NoArgsConstructor
    public static class BandInfo implements Serializable {
        
        private static final long serialVersionUID = 1L;
        
        @XmlElement
        private Integer id;

        @XmlElement(name = "name")
        private String name;

        public BandInfo(Integer id, String name) {
            this.id = id;
            this.name = name;
        }
    }
}
