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
        String uri = "http://localhost:8080/baseR4/Patient";
        List<PatientPretty> patients = new ArrayList<>();
        String firstUrl = "";
        String url = "";
        while (true) {
            if (url != "") {
                uri = url;
            }
            RestTemplate restTemplate = new RestTemplate();
            String result = restTemplate.getForObject(uri, String.class);

            FhirContext ctx = FhirContext.forR4();

            // Instantiate a new parser
            IParser parser = ctx.newJsonParser();

            // Parse it
            Bundle parsed = parser.parseResource(Bundle.class, result);
            url = parsed.getLink().get(1).getUrl();
            if (url.equals(firstUrl))
                break;
            if (patients.size() == 0)
                firstUrl = url;
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
        }
        return patients;
    }


    public static List<ObservationPretty> getObservations() {
        String uri = "http://localhost:8080/baseR4/Observation";
        List<ObservationPretty> observations = new ArrayList<>();
        String firstUrl = "";
        String url = "";
        FhirContext ctx = FhirContext.forR4();

        // Instantiate a new parser
        IParser parser = ctx.newJsonParser();


        while (true) {
            if (url != "") {
                uri = url;
            }
            RestTemplate restTemplate = new RestTemplate();
            String result = restTemplate.getForObject(uri, String.class);

            // Parse it
            Bundle parsed = parser.parseResource(Bundle.class, result);
            url = parsed.getLink().get(1).getUrl();
            if (url.equals(firstUrl))
                break;
            if (observations.size() == 0)
                firstUrl = url;

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
            if (observations.size() > 50) {
                break;
            }
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

            String id = medicationRequest.getSubject().getReference();
            id = id.replace("Patient/", "");

            MedicationRequestPretty medicationRequestPretty = new MedicationRequestPretty(
                    id,
                    medicationRequest.getMedicationCodeableConcept().getText(),
                    medicationRequest.getAuthoredOn(),
                    dosageInstructionPretties
            );

            medicationRequestPretties.add(medicationRequestPretty);
        }
        return medicationRequestPretties;
    }
//
//    public static List<PatientPretty> getPatient(String id) {
//        final String uri = "http://localhost:8080/baseR4/Patient/adccf2c3-9dc4-4067-ba23-98982c4875da" + /*id +*/ "/_history/1";
//        List<Object> toParse = new ArrayList<>();
//
//
//        RestTemplate restTemplate = new RestTemplate();
//        String result = restTemplate.getForObject(uri, String.class);
//
//        FhirContext ctx = FhirContext.forR4();
//
//        // Instantiate a new parser
//        IParser parser = ctx.newJsonParser();
//
//        // Parse it
//        Patient parsed = parser.parseResource(Patient.class, result);
//
//
//        parsed.stream().forEach(entry -> toParse.add(callConstructor(entry)));
//
//        List<PatientPretty> patients = new ArrayList<>();
//        toParse.stream().filter(o -> o instanceof PatientPretty).forEach(patient -> patients.add((PatientPretty) patient));
//        List<ObservationPretty> observations = new ArrayList<>();
//        toParse.stream().filter(o -> o instanceof ObservationPretty).forEach(observation -> observations.add((ObservationPretty) observation));
//        List<MedicationRequestPretty> medicationRequests = new ArrayList<>();
//        toParse.stream().filter(o -> o instanceof MedicationRequestPretty).forEach(medicationRequest -> medicationRequests.add((MedicationRequestPretty) medicationRequest));
//        patients.get(0).setMedicationRequests(medicationRequests);
//        patients.get(0).setObservations(observations);
//        return patients;
//    }
//
//    private static Object callConstructor(Resource resource) {
//        if (resource.getResourceType().equals("Patient"))
//            return readPatient(resource);
//        else if (resource.getResourceType().equals("MedicationRequest"))
//            return readMedicationRequest(resource);
//        else if (resource.getResourceType().equals("Observation"))
//            return readObservation(resource);
//        return new Object();
//    }
//
//    private static PatientPretty readPatient(Resource resource) {
//        Patient patient = (Patient) resource;
//        PatientPretty patientPretty = new PatientPretty(
//                patient.getIdentifier().get(0).getValue(),
//                patient.getName().get(0).getGiven().get(0).toString(),
//                patient.getName().get(0).getFamily(),
//                patient.getBirthDate().toString(),
//                patient.getGender().toString());
//        return patientPretty;
//    }
//
//    private static ObservationPretty readObservation(Resource resource) {
//        Observation observation = (Observation) resource;
//        ObservationPretty observationPretty = new ObservationPretty(
//                observation.getId(),
//                observation.getCode().getText(),
//                observation.getEffectiveDateTimeType(),
//                observation.getValueQuantity().getValue(),
//                observation.getValueQuantity().getUnit()
//        );
//        return observationPretty;
//    }
//
//    private static MedicationRequestPretty readMedicationRequest(Resource resource) {
//        MedicationRequest medicationRequest = (MedicationRequest) resource;
//        List<DosageInstructionPretty> dosageInstructionPretties = new ArrayList<>();
//
//        for (int j = 0; j < medicationRequest.getDosageInstruction().size(); j++) {
//            Timing timing;
//            if (medicationRequest.getDosageInstruction().get(j).getTiming() != null) {
//                timing = new Timing(
//                        medicationRequest.getDosageInstruction().get(j).getTiming().getRepeat().getFrequency(),
//                        medicationRequest.getDosageInstruction().get(j).getTiming().getRepeat().getPeriod(),
//                        medicationRequest.getDosageInstruction().get(j).getTiming().getRepeat().getPeriodUnit()
//                );
//            } else
//                timing = null;
//            DosageInstructionPretty dosageInstructionPretty = new DosageInstructionPretty(
//                    medicationRequest.getDosageInstruction().get(j).getAsNeededBooleanType().booleanValue(),
//                    medicationRequest.getDosageInstruction().get(j).getDoseAndRateFirstRep().getDoseQuantity().getValue(),
//                    timing
//            );
//
//            dosageInstructionPretties.add(dosageInstructionPretty);
//        }
//
//        MedicationRequestPretty medicationRequestPretty = new MedicationRequestPretty(
//                medicationRequest.getId(),
//                medicationRequest.getMedicationCodeableConcept().getText(),
//                medicationRequest.getAuthoredOn(),
//                dosageInstructionPretties
//        );
//
//        return medicationRequestPretty;
//    }
}
