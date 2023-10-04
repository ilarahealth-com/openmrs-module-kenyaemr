/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.fragment.controller.patient;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.ConceptService;
import org.openmrs.api.ObsService;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.fragment.FragmentModel;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Patient risk fragment
 */
public class PatientRiskFragmentController {

    protected static final Log log = LogFactory.getLog(PatientRiskFragmentController.class);

    ObsService obsService = Context.getObsService();
    ConceptService conceptService = Context.getConceptService();
    SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MMM-yyyy");

    // triage concepts
    String HAEMOGLOBIN_CONCEPT_ID = "21AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    String BP_SYSTOLIC_CONCEPT_ID = "5085AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    String BP_DIASTOLIC_CONCEPT_ID = "5086AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    String PALLOR_CONCEPT_ID = "5245AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    String PULSE_RATE_CONCEPT_ID = "5087AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    String ANAEMIA_CONCEPT_ID = "121629AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    String ESTIMATED_DELIVERY_DATE_CONCEPT_ID = "5596AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    String FOETAL_HEART_RATE_CONCEPT_ID = "1440AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    String ANC_VISIT_NUMBER_CONCEPT_ID = "1425AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";

    public void controller(@FragmentParam("patient") Patient patient, FragmentModel model) {
        List<Obs> obs = null;
        Obs haemoglobin = getLatestObs(patient, HAEMOGLOBIN_CONCEPT_ID);
        if (haemoglobin != null) {
            obs.add(haemoglobin);
        }
        Obs bp_systolic = getLatestObs(patient, BP_SYSTOLIC_CONCEPT_ID);
        if (bp_systolic != null) {
            obs.add(bp_systolic);
        }
        Obs bp_diastolic = getLatestObs(patient, BP_DIASTOLIC_CONCEPT_ID);
        if (bp_diastolic != null) {
            obs.add(bp_diastolic);
        }
        Obs pallor = getLatestObs(patient, PALLOR_CONCEPT_ID);
        if (pallor != null) {
            obs.add(pallor);
        }
        Obs pulse = getLatestObs(patient, PULSE_RATE_CONCEPT_ID);
        if (pulse != null) {
            obs.add(pulse);
        }
        Obs anaemia = getLatestObs(patient, ANAEMIA_CONCEPT_ID);
        if (anaemia != null) {
            obs.add(anaemia);
        }
        Obs edd = getLatestObs(patient, ESTIMATED_DELIVERY_DATE_CONCEPT_ID);
        if (edd != null) {
            obs.add(edd);
        }
        Obs fhr = getLatestObs(patient, FOETAL_HEART_RATE_CONCEPT_ID);
        if (fhr != null) {
            obs.add(fhr);
        }
        Obs visitNumber = getLatestObs(patient, ANC_VISIT_NUMBER_CONCEPT_ID);
        if (visitNumber != null) {
            obs.add(visitNumber);
        }
        model.addAttribute("riskParams", getRiskParams(obs));
    }

    private SimpleObject getRiskParams(List<Obs> obsList) {
        Double haemoglobin = null;
        String pallor = null;
        Double pulse = null;
        Double bp_systolic = null;
        Double bp_diastolic = null;
        String anaemia = null;
        String bp = null;
        String edd = null;
        Double fhr = null;
        Double visitNumber = null;
        String visitDate = null;

        Map<String, Object> riskParamsMap = new HashMap<String, Object>();
        for (Obs obs : obsList) {
            if (obs.getConcept().getConceptId().equals(HAEMOGLOBIN_CONCEPT_ID)) {
                haemoglobin = obs.getValueNumeric();
                if (!riskParamsMap.keySet().contains("haemoglobin")) {
                    riskParamsMap.put("haemoglobin", haemoglobin);
                }
            } else if (obs.getConcept().getConceptId().equals(PALLOR_CONCEPT_ID)) {
                pallor = obs.getValueCoded().getDisplayString();
                if (!riskParamsMap.keySet().contains("pallor")) {
                    riskParamsMap.put("pallor", pallor);
                }
            } else if (obs.getConcept().getConceptId().equals(PULSE_RATE_CONCEPT_ID)) {
                pulse = obs.getValueNumeric();
                if (!riskParamsMap.keySet().contains("pulse")) {
                    riskParamsMap.put("pulse", pallor);
                }
            } else if (obs.getConcept().getConceptId().equals(ANAEMIA_CONCEPT_ID)) {
                anaemia = obs.getValueCoded().getDisplayString();
                if (!riskParamsMap.keySet().contains("anaemia")) {
                    riskParamsMap.put("anaemia", pallor);
                }
            } else if (obs.getConcept().getConceptId().equals(BP_SYSTOLIC_CONCEPT_ID)) {
                bp_systolic = obs.getValueNumeric();
                if (!riskParamsMap.keySet().contains("bp_systolic")) {
                    riskParamsMap.put("bp_systolic", bp_systolic.intValue());
                }
            } else if (obs.getConcept().getConceptId().equals(BP_DIASTOLIC_CONCEPT_ID)) {
                bp_diastolic = obs.getValueNumeric();
                if (!riskParamsMap.keySet().contains("bp_diastolic")) {
                    riskParamsMap.put("bp_diastolic", bp_diastolic.intValue());
                }
            } else if (obs.getConcept().getConceptId().equals(ESTIMATED_DELIVERY_DATE_CONCEPT_ID)) {
                edd = DATE_FORMAT.format(obs.getValueDate());
                if (!riskParamsMap.keySet().contains("edd")) {
                    riskParamsMap.put("edd", edd);
                }
            } else if (obs.getConcept().getConceptId().equals(FOETAL_HEART_RATE_CONCEPT_ID)) {
                fhr = obs.getValueNumeric();
                if (!riskParamsMap.keySet().contains("fhr")) {
                    riskParamsMap.put("fhr", fhr.intValue());
                }
            } else if (obs.getConcept().getConceptId().equals(ANC_VISIT_NUMBER_CONCEPT_ID)) {
                visitNumber = obs.getValueNumeric();
                if (!riskParamsMap.keySet().contains("visitNumber")) {
                    riskParamsMap.put("visitNumber", visitNumber.intValue());
                }
                visitDate = DATE_FORMAT.format(obs.getDateCreated());
                if (!riskParamsMap.keySet().contains("visitDate")) {
                    riskParamsMap.put("visitDate", visitDate);
                }
            }

        }

        if (bp_diastolic != null && bp_systolic != null)
            riskParamsMap.put("bp", new StringBuilder().append(bp_systolic.intValue()).append("/").append(bp_diastolic.intValue()));


        return SimpleObject.create(
                "haemoglobin", riskParamsMap.get("haemoglobin") != null ? new StringBuilder().append(riskParamsMap.get("haemoglobin")):"",
                "pallor", riskParamsMap.get("pallor") != null ? new StringBuilder().append(riskParamsMap.get("pallor")):"",
                "pulse", riskParamsMap.get("pulse") != null ? new StringBuilder().append(riskParamsMap.get("pulse")):"",
                "anaemia", riskParamsMap.get("anaemia") != null ? new StringBuilder().append(riskParamsMap.get("anaemia")):"",
                "edd", riskParamsMap.get("edd") != null ? new StringBuilder().append(riskParamsMap.get("edd")):"",
                "bp", riskParamsMap.get("bp") != null ? new StringBuilder().append(riskParamsMap.get("bp")):"",
                "fhr", riskParamsMap.get("fhr") != null ? new StringBuilder().append(riskParamsMap.get("fhr")):"",
                "visitNumber", riskParamsMap.get("visitNumber") != null ? new StringBuilder().append(riskParamsMap.get("visitNumber")):"",
                "visitDate", riskParamsMap.get("visitDate") != null ? new StringBuilder().append(riskParamsMap.get("visitDate")):""
        );
    }

    private Obs getLatestObs(Patient patient, String conceptIdentifier) {
        Concept concept = Dictionary.getConcept(conceptIdentifier);
        List<Obs> obs = Context.getObsService().getObservationsByPersonAndConcept(patient, concept);
        if (obs.size() > 0) {
            // these are in reverse chronological order
            return obs.get(0);
        }
        return null;
    }
}