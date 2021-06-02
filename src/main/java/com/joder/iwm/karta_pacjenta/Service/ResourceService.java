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
    static FhirContext ctx = FhirContext.forR4();

    // Instantiate a new parser
    static IParser parser = ctx.newJsonParser();

    public static List<PatientPretty> getPatients() {
        String uri = "http://localhost:8080/baseR4/Patient";
        List<PatientPretty> patients = new ArrayList<>();
        Integer total;

        while (true) {
            RestTemplate restTemplate = new RestTemplate();
            String result = restTemplate.getForObject(uri, String.class);

            // Parse it
            Bundle parsed = parser.parseResource(Bundle.class, result);
            uri = parsed.getLink().get(1).getUrl();

            total = parsed.getTotal();

            for (int i = 0; i < parsed.getEntry().size(); i++) {
                Patient patient = (Patient) parsed.getEntry().get(i).getResource();
                String id = patient.getId();
                id = id.replace("http://localhost:8080/baseR4/Patient/", "");
                id = id.replace("/_history/1", "");
                PatientPretty patientPretty = new PatientPretty(
                        id,
                        patient.getName().get(0).getGiven().get(0).toString(),
                        patient.getName().get(0).getFamily(),
                        patient.getBirthDate().toString(),
                        patient.getGender().toString());
                patients.add(patientPretty);
            }

            if (patients.size() > total) {
                break;
            }
        }
        return patients;
    }


    public static List<ObservationPretty> getObservations() {
        String uri = "http://localhost:8080/baseR4/Observation";
        List<ObservationPretty> observations = new ArrayList<>();

        Integer total;

        while (true) {
            RestTemplate restTemplate = new RestTemplate();
            String result = restTemplate.getForObject(uri, String.class);

            // Parse it
            Bundle parsed = parser.parseResource(Bundle.class, result);
            uri = parsed.getLink().get(1).getUrl();

            total = parsed.getTotal();

            for (int i = 0; i < parsed.getEntry().size(); i++) {
                try {
                    Observation observation = (Observation) parsed.getEntry().get(i).getResource();
                    String id = observation.getSubject().getReference();
                    id = id.replace("Patient/", "");
                    ObservationPretty observationPretty = new ObservationPretty(
                            id,
                            observation.getCode().getText(),
                            observation.getEffectiveDateTimeType(),
                            observation.getValueQuantity()
                    );
                    observations.add(observationPretty);

                } catch (Exception e) {

                }
            }
            if (observations.size() > total) {
                break;
            }
        }
        return observations;
    }

    public static List<MedicationRequestPretty> getMedicationRequests() {
        String uri = "http://localhost:8080/baseR4/MedicationRequest";
        List<MedicationRequestPretty> medicationRequestPretties = new ArrayList<>();
        Integer total;

        while (true) {

            RestTemplate restTemplate = new RestTemplate();
            String result = restTemplate.getForObject(uri, String.class);

            // Parse it
            Bundle parsed = parser.parseResource(Bundle.class, result);
            uri = parsed.getLink().get(1).getUrl();

            total = parsed.getTotal();

            for (int i = 0; i < parsed.getEntry().size(); i++) {
                MedicationRequest medicationRequest = (MedicationRequest) parsed.getEntry().get(i).getResource();
                List<DosageInstructionPretty> dosageInstructionPretties = new ArrayList<>();
                try {
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

                    String id = medicationRequest.getSubject().getReference();
                    id = id.replace("Patient/", "");

                    MedicationRequestPretty medicationRequestPretty = new MedicationRequestPretty(
                            id,
                            medicationRequest.getMedicationCodeableConcept().getText(),
                            medicationRequest.getAuthoredOn(),
                            dosageInstructionPretties
                    );

                    medicationRequestPretties.add(medicationRequestPretty);
                } catch (Exception e) {

                }
            }
            if (medicationRequestPretties.size() > total) {
                break;
            }
        }
        return medicationRequestPretties;
    }
}
