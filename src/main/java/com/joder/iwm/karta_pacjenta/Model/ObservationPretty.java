package com.joder.iwm.karta_pacjenta.Model;

import org.hl7.fhir.r4.model.DateTimeType;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public class ObservationPretty {
    private String id;
    private String name;
    private ZonedDateTime dateTime;
    private Double value;
    private String unit;

    public ObservationPretty(String id, String code, DateTimeType effectiveDateTime, BigDecimal value, String unit) {
        this.id = id;
        this.name = code;
        this.dateTime = ZonedDateTime.parse(effectiveDateTime.getValueAsString());
        if (value != null)
            this.value = value.toBigInteger().doubleValue();
        if (unit != null)
            this.unit = unit;
    }
}
