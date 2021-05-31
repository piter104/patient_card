package com.joder.iwm.karta_pacjenta.Model;

import lombok.Data;
import org.hl7.fhir.r4.model.Timing.UnitsOfTime;

import java.math.BigDecimal;

@Data
public class Timing {
    private Integer frequency;
    private Integer period;
    private String periodUnit;

    public Timing(Integer frequency, BigDecimal period, UnitsOfTime periodUnit) {
        this.frequency = frequency;
        if (period != null)
            this.period = period.intValueExact();
        if (periodUnit != null)
            this.periodUnit = periodUnit.getDefinition();
    }
}
