package com.joder.iwm.karta_pacjenta.Model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PatientPretty {
    private String id;
    private String name;
    private String surname;
    private String gender;
    private String birthDate;
    private List<ObservationPretty> observations;
    private List<MedicationRequestPretty> medicationRequests;

    public PatientPretty(String id, String name, String surname, String birthDate, String gender) {
        this.id = id;
        this.birthDate = birthDate;
        this.gender = gender;
        this.name = filterName(name);
        this.surname = filterName(surname);
        this.observations = new ArrayList<>();
        this.medicationRequests = new ArrayList<>();
    }

    String filterName(String name) {
        return name.replaceAll("[^a-zA-Z]", "");
    }
}
