package com.joder.iwm.karta_pacjenta.Model;

import lombok.Data;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.Quantity;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Data
public class ObservationPretty {
    private String id;
    private String name;
    private ZonedDateTime dateTime;
    private Double value;
    private String unit;
    private String toPrintDate;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public ObservationPretty(String id, String code, DateTimeType effectiveDateTime, Quantity value) {
        this.id = id;
        this.name = code;
        this.dateTime = ZonedDateTime.parse(effectiveDateTime.getValueAsString());
        if (value != null) {
            if (value.getValue() != null)
                this.value = value.getValue().toBigInteger().doubleValue();
            if (value.getUnit() != null)
                this.unit = value.getUnit();
        }
        this.toPrintDate = dateTime.format(formatter);
    }
}
