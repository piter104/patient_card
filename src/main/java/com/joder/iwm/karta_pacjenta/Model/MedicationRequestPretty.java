package com.joder.iwm.karta_pacjenta.Model;

import lombok.Data;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

@Data
public class MedicationRequestPretty {
    private String id;
    private String name;
    private ZonedDateTime dateTime;
    private Boolean asNeededBoolean;
    private Integer doseQuantity;
    private Integer frequency;
    private Integer period;
    private String periodUnit;
    private String toPrintDate;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public MedicationRequestPretty(String id, String medicationCodeableConcept, Date authoredOn, List<DosageInstructionPretty> dosageInstructions) {
        this.id = id;
        this.name = medicationCodeableConcept;
        this.dateTime = ZonedDateTime.ofInstant(authoredOn.toInstant(), ZoneId.systemDefault());
        this.toPrintDate = dateTime.format(formatter);
        if (dosageInstructions != null) {
            if (dosageInstructions.size() > 0) {
                this.asNeededBoolean = dosageInstructions.get(0).getAsNeededBoolean();
                if (dosageInstructions.get(0).getDoseQuantity() != null)
                    this.doseQuantity = dosageInstructions.get(0).getDoseQuantity();

                if (dosageInstructions.get(0).getTiming() != null) {
                    this.frequency = dosageInstructions.get(0).getTiming().getFrequency();
                    this.period = dosageInstructions.get(0).getTiming().getPeriod();
                    this.periodUnit = dosageInstructions.get(0).getTiming().getPeriodUnit();
                }
            }
        }
    }
}
