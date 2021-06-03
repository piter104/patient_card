package com.joder.iwm.karta_pacjenta.Controller;

import ca.uhn.fhir.rest.annotation.RequiredParam;
import com.joder.iwm.karta_pacjenta.Model.Filter;
import com.joder.iwm.karta_pacjenta.Model.ObservationPretty;
import com.joder.iwm.karta_pacjenta.Model.PatientPretty;
import com.joder.iwm.karta_pacjenta.Service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
        model.addAttribute("patient", resourceService.getAllPatientInfo(id));
        model.addAttribute("filter", new Filter());
        model.addAttribute("id", id);
        return "patient-details";
    }

    @PostMapping(value = "/{id}/chart")
    public String showChart(@PathVariable(value = "id") String id, @RequiredParam(name = "filter") Filter filter, Model model) {
        PatientPretty patient = resourceService.getFilteredPatient(filter);
        Map<String, Double> data = new LinkedHashMap<>();
        List<ObservationPretty> observations = patient.getObservations();
        for (ObservationPretty observation : observations) {
            if (observation.getName().toLowerCase().contains("weight")) {
                data.put(observation.getToPrintDate(), observation.getValue());
            }
        }
        model.addAttribute("keySet", data.keySet());
        model.addAttribute("values", data.values());
        model.addAttribute("filter", filter);
        model.addAttribute("id", id);
        return "chart";
    }

    @PostMapping(value = "/search")
    public String patientSearch(@RequiredParam(name = "search") String search, Model model) {
        model.addAttribute("search", "");
        model.addAttribute("patients", resourceService.getSearchedPatients(search));
        return "index";
    }

    @PostMapping(value = "/{id}/filter")
    public String patientFilter(@RequiredParam(name = "filter") Filter filter, Model model) {
        model.addAttribute("patient", resourceService.getFilteredPatient(filter));
        model.addAttribute("filter", new Filter());
        model.addAttribute("id", filter.getId());
        return "patient-details";
    }
}