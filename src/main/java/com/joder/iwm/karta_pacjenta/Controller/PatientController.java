package com.joder.iwm.karta_pacjenta.Controller;

import com.joder.iwm.karta_pacjenta.Model.PatientPretty;
import com.joder.iwm.karta_pacjenta.Service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class PatientController {

    @Autowired
    ResourceService resourceService;

    @GetMapping(value = "/")
    public String patientList(Model model) {
        model.addAttribute("medicationRequests", resourceService.getMedicationRequests());
        model.addAttribute("observations", resourceService.getObservations());
        model.addAttribute("patients", resourceService.getPatients());
        return "index";
    }

    @GetMapping(value = "/{id}")
    public String patientDetails(@PathVariable(value = "id") String id, Model model) {
        List<PatientPretty> patients = resourceService.getPatients();
        for (PatientPretty patient : patients) {
            String val = patient.getId();
            if (val.equals(id)) {
                model.addAttribute("patient", patient);
                break;
            }
        }
        return "patient-details";
    }
}