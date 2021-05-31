package com.joder.iwm.karta_pacjenta.Service;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import com.joder.iwm.karta_pacjenta.Model.*;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.MedicationRequest;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class ResourceService {
    public static List<PatientPretty> getPatients() {
        final String uri = "http://localhost:8080/baseR4/Patient";
        List<PatientPretty> patients = new ArrayList<>();

        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject(uri, String.class);

        FhirContext ctx = FhirContext.forR4();

        // Instantiate a new parser
        IParser parser = ctx.newJsonParser();

        // Parse it
        Bundle parsed = parser.parseResource(Bundle.class, result);

        for (int i = 0; i < parsed.getEntry().size(); i++) {
            Patient patient = (Patient) parsed.getEntry().get(i).getResource();
            PatientPretty patientPretty = new PatientPretty(
                    patient.getIdentifier().get(0).getValue(),
                    patient.getName().get(0).getGiven().get(0).toString(),
                    patient.getName().get(0).getFamily(),
                    patient.getBirthDate().toString(),
                    patient.getGender().toString());
            patients.add(patientPretty);
        }
        return patients;
    }

    public static List<ObservationPretty> getObservations() {
        final String uri = "http://localhost:8080/baseR4/Observation";
        List<ObservationPretty> observations = new ArrayList<>();

        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject(uri, String.class);

        FhirContext ctx = FhirContext.forR4();

        // Instantiate a new parser
        IParser parser = ctx.newJsonParser();

        // Parse it
        Bundle parsed = parser.parseResource(Bundle.class, result);

        for (int i = 0; i < parsed.getEntry().size(); i++) {
            Observation observation = (Observation) parsed.getEntry().get(i).getResource();
            ObservationPretty observationPretty = new ObservationPretty(
                    observation.getId(),
                    observation.getCode().getText(),
                    observation.getEffectiveDateTimeType(),
                    observation.getValueQuantity().getValue(),
                    observation.getValueQuantity().getUnit()
            );
            observations.add(observationPretty);
        }
        return observations;
    }

    public static List<MedicationRequestPretty> getMedicationRequests() {
        final String uri = "http://localhost:8080/baseR4/MedicationRequest";
        List<MedicationRequest> medicationRequests = new ArrayList<>();

        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject(uri, String.class);

        FhirContext ctx = FhirContext.forR4();

        // Instantiate a new parser
        IParser parser = ctx.newJsonParser();

        // Parse it
        Bundle parsed = parser.parseResource(Bundle.class, result);

        List<MedicationRequestPretty> medicationRequestPretties = new ArrayList<>();

        for (int i = 0; i < parsed.getEntry().size(); i++) {
            MedicationRequest medicationRequest = (MedicationRequest) parsed.getEntry().get(i).getResource();
            List<DosageInstructionPretty> dosageInstructionPretties = new ArrayList<>();

            for (int j = 0; j < medicationRequest.getDosageInstruction().size(); j++) {
                Timing timing;
                if (medicationRequest.getDosageInstruction().get(j).getTiming() != null) {
                    timing = new Timing(
                            medicationRequest.getDosageInstruction().get(j).getTiming().getRepeat().getFrequency(),
                            medicationRequest.getDosageInstruction().get(j).getTiming().getRepeat().getPeriod(),
                            medicationRequest.getDosageInstruction().get(j).getTiming().getRepeat().getPeriodUnit()
                    );
                } else
                    timing = null;
                DosageInstructionPretty dosageInstructionPretty = new DosageInstructionPretty(
                        medicationRequest.getDosageInstruction().get(j).getAsNeededBooleanType().booleanValue(),
                        medicationRequest.getDosageInstruction().get(j).getDoseAndRateFirstRep().getDoseQuantity().getValue(),
                        timing
                );

                dosageInstructionPretties.add(dosageInstructionPretty);
            }

            MedicationRequestPretty medicationRequestPretty = new MedicationRequestPretty(
                    medicationRequest.getId(),
                    medicationRequest.getMedicationCodeableConcept().getText(),
                    medicationRequest.getAuthoredOn(),
                    dosageInstructionPretties
            );

            medicationRequestPretties.add(medicationRequestPretty);
        }
        return medicationRequestPretties;
    }

    public static List<MedicationRequest> getPatientHistory(String id) {
        final String uri = "http://localhost:8080/baseR4/Patient/" + "id" + "/everything";
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
}
