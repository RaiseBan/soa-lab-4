package com.musicband.grammy.adapter.model;

import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@XmlRootElement(name = "participant")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Participant implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement
    private Integer id;

    @XmlElement(name = "name", required = true)
    private String name;

    @XmlElement(required = true)
    private String role;

    @XmlElement(required = true)
    @XmlJavaTypeAdapter(LocalDateAdapter.class)
    private LocalDate joinDate;

    @XmlElement
    private String instrument;
}
