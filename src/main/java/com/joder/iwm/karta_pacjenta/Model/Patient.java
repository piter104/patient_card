package com.joder.iwm.karta_pacjenta.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Patient {
    String name;
    String gender;
    LocalDate birthDate;
    int identifier;

}
