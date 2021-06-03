package com.joder.iwm.karta_pacjenta.Controller;

import ca.uhn.fhir.rest.annotation.RequiredParam;
import com.joder.iwm.karta_pacjenta.Model.Filter;
import com.joder.iwm.karta_pacjenta.Service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

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
    public String patientDetails(@PathVariable(value = "id") String id, @RequiredParam(name = "filter") Filter filter, Model model) {
        model.addAttribute("patient", resourceService.getAllPatientInfo(id));
        model.addAttribute("filter", new Filter());
        model.addAttribute("id", id);
        return "patient-details";
    }

    @PostMapping(value = "/search")
    public String patientSearch(@RequiredParam(name = "search") String search, Model model) {
        model.addAttribute("search", new String());
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