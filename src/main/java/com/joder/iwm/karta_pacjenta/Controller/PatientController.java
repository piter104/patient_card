package com.joder.iwm.karta_pacjenta.Controller;

import com.joder.iwm.karta_pacjenta.Model.Patient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
public class PatientController {
    @GetMapping(value = "/")
    public String patientList(Model model) {
        model.addAttribute("patients", getPatients());
        return "index";
    }

    @GetMapping(value = "/{id}")
    public String patientDetails(@PathVariable(value = "id") int id, Model model) {
        model.addAttribute("patient", getPatients().get(id));
        return "patient-details";
    }

    private static List<Patient> getPatients() {
        final String uri = "http://localhost:8080/baseR4/Patient?_pretty=true";
        List<Patient> patients = new ArrayList<>();

        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject(uri, String.class);
        try {
            JSONObject obj = new JSONObject(result);

            JSONArray arr = obj.getJSONArray("entry"); // notice that `"posts": [...]`
            for (int i = 0; i < arr.length(); i++) {
                Patient patient = new Patient();
                JSONObject arr2 = arr.getJSONObject(i).getJSONObject("resource");
                JSONArray arr3 = arr2.getJSONArray("name");

                patient.setIdentifier(i);
                for (int j = 0; j < arr3.length(); j++) {
                    String name = arr3.getJSONObject(j).getString("given");
                    name = name.replaceAll("[^a-zA-Z]", " ");
                    String family = arr3.getJSONObject(j).getString("family");
                    family = family.replaceAll("[^a-zA-Z]", " ");
                    patient.setName(name + " " + family);
                }
                patient.setGender(arr2.getString("gender"));
                patient.setBirthDate(LocalDate.parse(arr2.getString("birthDate")));
                patients.add(patient);
                // System.out.println(arr2.getJSONArray("address"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //  System.out.println(result);
        return patients;
    }

}