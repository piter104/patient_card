package com.joder.iwm.karta_pacjenta.Model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Patient {
    String name;
    String gender;
    LocalDate birthDate;
    int identifier;

    public Patient(String name, String gender, LocalDate birthDate, int identifier) {
        this.name = name;
        this.gender = gender;
        this.birthDate = birthDate;
        this.identifier = identifier;
    }
}
