package com.joder.iwm.karta_pacjenta.Model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DosageInstructionPretty {
    private Boolean asNeededBoolean;
    private Integer doseQuantity;
    private Timing timing;

    public DosageInstructionPretty(Boolean asNeededBoolean, BigDecimal doseQuantity, Timing timing) {
        this.asNeededBoolean = asNeededBoolean;
        if (doseQuantity != null)
            this.doseQuantity = doseQuantity.intValue();
        this.timing = timing;
    }
}
