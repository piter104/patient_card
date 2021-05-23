package com.joder.iwm.karta_pacjenta.Controller;

import com.joder.iwm.karta_pacjenta.Model.Patient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
public class PatientController {
    @GetMapping(value = "/")
    public String patientList(Model model) {
        List<Patient> patients = new ArrayList<>();
        Patient patient = new Patient("Andrew", "Man", LocalDate.now(), 5);

        patients.add(patient);

        model.addAttribute("patients", patients);
        return "index";
    }

    @GetMapping(value = "/{id}")
    public String patientDetails(@PathVariable(value = "id") Long id, Model model) {
        model.addAttribute("id", id);
        Patient patient = new Patient("Andrew", "Man", LocalDate.now(), 5);

        model.addAttribute("patient", patient);
        return "patient-details";
    }
}