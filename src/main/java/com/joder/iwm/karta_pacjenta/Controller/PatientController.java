package com.joder.iwm.karta_pacjenta.Controller;

import ca.uhn.fhir.rest.annotation.RequiredParam;
import com.joder.iwm.karta_pacjenta.Model.MedicationRequestPretty;
import com.joder.iwm.karta_pacjenta.Model.ObservationPretty;
import com.joder.iwm.karta_pacjenta.Model.PatientPretty;
import com.joder.iwm.karta_pacjenta.Service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class PatientController {

    @Autowired
    ResourceService resourceService;

    @GetMapping(value = "/")
    public String patientList(Model model) {
        model.addAttribute("search", new String());
        model.addAttribute("patients", resourceService.getPatients());
        return "index";
    }

    @GetMapping(value = "/{id}")
    public String patientDetails(@PathVariable(value = "id") String id, Model model) {
        List<PatientPretty> patients = resourceService.getPatients();
        List<ObservationPretty> observations = resourceService.getObservations();
        List<MedicationRequestPretty> medicationRequests = resourceService.getMedicationRequests();
        List<ObservationPretty> observationPrettyList = new ArrayList<>();
        List<MedicationRequestPretty> medicationRequestPrettyList = new ArrayList<>();

        for (PatientPretty patient : patients) {
            String val = patient.getId();
            if (val.equals(id)) {
                for (ObservationPretty observation : observations) {
                    Boolean aa = observation.getId().equals(id);
                    if (observation.getId().equals(id)) {
                        observationPrettyList.add(observation);
                    }
                }
                for (MedicationRequestPretty medicationRequest : medicationRequests) {
                    if (medicationRequest.getId().equals(id)) {
                        medicationRequestPrettyList.add(medicationRequest);
                    }
                }
                patient.setObservations(observationPrettyList);
                patient.setMedicationRequests(medicationRequestPrettyList);
                model.addAttribute("patient", patient);
                break;
            }
        }
        model.addAttribute("observation", resourceService.getObservations());
        model.addAttribute("medicationRequest", resourceService.getMedicationRequests());
        return "patient-details";
    }

    @PostMapping(value = "/search")
    public String patientSearch(@RequiredParam(name = "search") String search, Model model) {
        List<PatientPretty> patients = new ArrayList<>();
        for (PatientPretty patient : resourceService.getPatients()) {
            if (
                    patient.getName().toLowerCase().contains(search.toLowerCase())
                            ||
                            patient.getSurname().toLowerCase().contains(search.toLowerCase())) {
                patients.add(patient);
            }
        }
        model.addAttribute("search", new String());
        model.addAttribute("patients", patients);
        return "index";
    }
}