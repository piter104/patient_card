package com.joder.iwm.karta_pacjenta.Model;

import lombok.Data;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class PatientPretty {
    private String id;
    private String name;
    private String surname;
    private String gender;
    private ZonedDateTime birthDate;
    private String toPrintDate;
    private List<ObservationPretty> observations;
    private List<MedicationRequestPretty> medicationRequests;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public PatientPretty(String id, String name, String surname, Date birthDate, String gender) {
        this.id = id;
        this.gender = gender;
        this.name = filterName(name);
        this.surname = filterName(surname);
        this.birthDate = ZonedDateTime.ofInstant(birthDate.toInstant(), ZoneId.systemDefault());
        this.toPrintDate = this.birthDate.format(formatter);
        this.observations = new ArrayList<>();
        this.medicationRequests = new ArrayList<>();
    }

    String filterName(String name) {
        return name.replaceAll("[^a-zA-Z]", "");
    }
}
