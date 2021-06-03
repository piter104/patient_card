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

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
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
        Integer total = -1;

        while (true) {
            RestTemplate restTemplate = new RestTemplate();
            String result = restTemplate.getForObject(uri, String.class);

            // Parse it
            Bundle parsed = parser.parseResource(Bundle.class, result);
            uri = parsed.getLink().get(1).getUrl();
            if (total == -1)
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

            if (patients.size() >= total) {
                break;
            }
        }
        return patients;
    }


    public static List<ObservationPretty> getObservations() {
        String uri = "http://localhost:8080/baseR4/Observation";
        List<ObservationPretty> observations = new ArrayList<>();

        Integer total = -1;

        while (true) {
            RestTemplate restTemplate = new RestTemplate();
            String result = restTemplate.getForObject(uri, String.class);

            // Parse it
            Bundle parsed = parser.parseResource(Bundle.class, result);
            uri = parsed.getLink().get(1).getUrl();

            if (total == -1)
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
            if (observations.size() >= total) {
                break;
            }
        }
        return observations;
    }

    public static List<MedicationRequestPretty> getMedicationRequests() {
        String uri = "http://localhost:8080/baseR4/MedicationRequest";
        List<MedicationRequestPretty> medicationRequestPretties = new ArrayList<>();
        Integer total = -1;

        while (true) {

            RestTemplate restTemplate = new RestTemplate();
            String result = restTemplate.getForObject(uri, String.class);

            // Parse it
            Bundle parsed = parser.parseResource(Bundle.class, result);
            uri = parsed.getLink().get(1).getUrl();

            if (total == -1)
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
            if (medicationRequestPretties.size() >= total) {
                break;
            }
        }
        return medicationRequestPretties;
    }

    public PatientPretty getAllPatientInfo(String id) {
        List<PatientPretty> patients = getPatients();
        List<ObservationPretty> observations = getObservations();
        List<MedicationRequestPretty> medicationRequests = getMedicationRequests();

        List<ObservationPretty> observationPrettyList = new ArrayList<>();
        List<MedicationRequestPretty> medicationRequestPrettyList = new ArrayList<>();

        for (PatientPretty patient : patients) {
            String val = patient.getId();
            if (val.equals(id)) {
                for (ObservationPretty observation : observations) {
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
                return patient;
            }
        }
        return null;
    }

    public List<PatientPretty> getSearchedPatients(String search) {
        List<PatientPretty> patients = new ArrayList<>();
        List<PatientPretty> data = getPatients();
        for (PatientPretty patient : data) {
            if (
                    patient.getName().toLowerCase().contains(search.toLowerCase())
                            ||
                            patient.getSurname().toLowerCase().contains(search.toLowerCase())) {
                patients.add(patient);
            }
        }
        return patients;
    }

    public PatientPretty getFilteredPatient(Filter filter) {
        PatientPretty patient = getAllPatientInfo(filter.id);
        List<MedicationRequestPretty> medicationRequests = patient.getMedicationRequests();
        List<ObservationPretty> observations = patient.getObservations();

        List<ObservationPretty> observationPrettyList = new ArrayList<>();
        List<MedicationRequestPretty> medicationRequestPrettyList = new ArrayList<>();

        ZonedDateTime startDate = null;
        ZonedDateTime endDate = null;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        if (filter.getStartDate() != "") {
            LocalDate localStartDate = LocalDate.parse(filter.getStartDate(), formatter);

            startDate = localStartDate.atStartOfDay(ZoneId.systemDefault());
        }
        if (filter.getEndDate() != "") {
            LocalDate localEndDate = LocalDate.parse(filter.getEndDate(), formatter);

            endDate = localEndDate.atStartOfDay(ZoneId.systemDefault());
        }

        for (ObservationPretty observation : observations) {
            if (startDate == null || observation.getDateTime().isAfter(startDate)) {
                if (endDate == null || observation.getDateTime().isBefore(endDate)) {
                    observationPrettyList.add(observation);
                }
            }
        }
        for (MedicationRequestPretty medicationRequest : medicationRequests) {
            if (startDate == null || medicationRequest.getDateTime().isAfter(startDate)) {
                if (endDate == null || medicationRequest.getDateTime().isBefore(endDate)) {
                    medicationRequestPrettyList.add(medicationRequest);
                }
            }
        }
        patient.setObservations(observationPrettyList);
        patient.setMedicationRequests(medicationRequestPrettyList);
        return patient;
    }
}
