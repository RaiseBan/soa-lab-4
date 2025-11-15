package com.musicband.grammy.adapter.model;

import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@XmlRootElement(name = "single")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Single implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement
    private Integer id;

    @XmlElement(required = true)
    private String title;

    @XmlElement(required = true)
    private Integer duration;

    @XmlElement(required = true)
    @XmlJavaTypeAdapter(LocalDateAdapter.class)
    private LocalDate releaseDate;

    @XmlElement
    private Integer chartPosition;
}
