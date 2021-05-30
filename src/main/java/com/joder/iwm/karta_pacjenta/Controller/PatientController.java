package com.joder.iwm.karta_pacjenta.Controller;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.MedicationRequest;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Controller
public class PatientController {
    private static List<Patient> getPatients() {
        final String uri = "http://localhost:8080/baseR4/Patient";
        List<Patient> patients = new ArrayList<>();

        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject(uri, String.class);

        FhirContext ctx = FhirContext.forR4();

        // Instantiate a new parser
        IParser parser = ctx.newJsonParser();

        // Parse it
        Bundle parsed = parser.parseResource(Bundle.class, result);

        for (int i = 0; i < parsed.getEntry().size(); i++) {
            Patient patient = (Patient) parsed.getEntry().get(i).getResource();
//            patient.getName().get(0).getGiven().toString().replaceAll("[^a-zA-Z]", " "));
//            patient.getName().get(0).getFamily().replaceAll("[^a-zA-Z]", ""));
            patients.add(patient);
        }
        return patients;
    }

    private static List<Observation> getObservations() {
        final String uri = "http://localhost:8080/baseR4/Observation";
        List<Observation> observations = new ArrayList<>();

        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject(uri, String.class);

        FhirContext ctx = FhirContext.forR4();

        // Instantiate a new parser
        IParser parser = ctx.newJsonParser();

        // Parse it
        Bundle parsed = parser.parseResource(Bundle.class, result);

        for (int i = 0; i < parsed.getEntry().size(); i++) {
            Observation observation = (Observation) parsed.getEntry().get(i).getResource();
            observations.add(observation);
        }
        return observations;
    }

    private static List<MedicationRequest> getMedicationRequests() {
        final String uri = "http://localhost:8080/baseR4/MedicationRequest";
        List<MedicationRequest> medicationRequests = new ArrayList<>();

        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject(uri, String.class);

        FhirContext ctx = FhirContext.forR4();

        // Instantiate a new parser
        IParser parser = ctx.newJsonParser();

        // Parse it
        Bundle parsed = parser.parseResource(Bundle.class, result);

        for (int i = 0; i < parsed.getEntry().size(); i++) {
            MedicationRequest medicationRequest = (MedicationRequest) parsed.getEntry().get(i).getResource();
            medicationRequests.add(medicationRequest);
        }
        return medicationRequests;
    }

    @GetMapping(value = "/")
    public String patientList(Model model) {
        getObservations();
        getMedicationRequests();
        model.addAttribute("patients", getPatients());
        return "index";
    }

    @GetMapping(value = "/{id}")
    public String patientDetails(@PathVariable(value = "id") String id, Model model) {
        List<Patient> patients = getPatients();
        for (Patient patient : patients) {
            String val = patient.getIdentifier().get(0).getValue();
            if (val.equals(id)) {
                model.addAttribute("patient", patient);
                break;
            }
        }
        return "patient-details";
    }
}