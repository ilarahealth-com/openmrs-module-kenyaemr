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
import org.openmrs.Encounter;
import org.openmrs.Form;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.api.ConceptService;
import org.openmrs.api.ObsService;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.Metadata;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.fragment.FragmentModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Patient risk fragment
 */
public class PatientRiskFragmentController {

    protected static final Log log = LogFactory.getLog(PatientRiskFragmentController.class);

    ObsService obsService = Context.getObsService();
    ConceptService conceptService = Context.getConceptService();
    SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MMM-yyyy");

    // triage concepts
    Concept HAEMOGLOBIN = Dictionary.getConcept("21AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
    Concept BP_SYSTOLIC = Dictionary.getConcept("5085AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
    Concept BP_DIASTOLIC = Dictionary.getConcept("5086AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
    Concept PALLOR = Dictionary.getConcept("5245AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
    Concept PULSE_RATE = Dictionary.getConcept("5087AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
    Concept ANAEMIA = Dictionary.getConcept("121629AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
    Concept ESTIMATED_DELIVERY_DATE = Dictionary.getConcept("5596AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
    Concept FOETAL_HEART_RATE = Dictionary.getConcept("1440AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
    Concept ANC_VISIT_NUMBER = Dictionary.getConcept("1425AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");

    public void controller(@FragmentParam("patient") Patient patient, FragmentModel model) {
        List<Obs> obs = null;
        Obs haemoglobin = getLatestObs(patient, HAEMOGLOBIN);
        if (haemoglobin != null) {
            obs.add(haemoglobin);
        }
        Obs bp_systolic = getLatestObs(patient, BP_SYSTOLIC);
        if (bp_systolic != null) {
            obs.add(bp_systolic);
        }
        Obs bp_diastolic = getLatestObs(patient, BP_DIASTOLIC);
        if (bp_diastolic != null) {
            obs.add(bp_diastolic);
        }
        Obs pallor = getLatestObs(patient, PALLOR);
        if (pallor != null) {
            obs.add(pallor);
        }
        Obs pulse = getLatestObs(patient, PULSE_RATE);
        if (pulse != null) {
            obs.add(pulse);
        }
        Obs anaemia = getLatestObs(patient, ANAEMIA);
        if (anaemia != null) {
            obs.add(anaemia);
        }
        Obs edd = getLatestObs(patient, ESTIMATED_DELIVERY_DATE);
        if (edd != null) {
            obs.add(edd);
        }
        Obs fhr = getLatestObs(patient, FOETAL_HEART_RATE);
        if (fhr != null) {
            obs.add(fhr);
        }
        Obs visitNumber = getLatestObs(patient, ANC_VISIT_NUMBER);
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
            if (obs.getConcept().equals(HAEMOGLOBIN)) {
                haemoglobin = obs.getValueNumeric();
                if (!riskParamsMap.keySet().contains("haemoglobin")) {
                    riskParamsMap.put("haemoglobin", haemoglobin);
                }
            } else if (obs.getConcept().equals(PALLOR)) {
                pallor = obs.getValueCoded();
                if (!riskParamsMap.keySet().contains("pallor")) {
                    riskParamsMap.put("pallor", pallor);
                }
            } else if (obs.getConcept().equals(PULSE_RATE)) {
                pulse = obs.getValueNumeric();
                if (!riskParamsMap.keySet().contains("pulse")) {
                    riskParamsMap.put("pulse", pallor);
                }
            } else if (obs.getConcept().equals(ANAEMIA)) {
                anaemia = obs.getValueCoded();
                if (!riskParamsMap.keySet().contains("anaemia")) {
                    riskParamsMap.put("anaemia", pallor);
                }
            } else if (obs.getConcept().equals(BP_SYSTOLIC)) {
                bp_systolic = obs.getValueNumeric();
                if (!riskParamsMap.keySet().contains("bp_systolic")) {
                    riskParamsMap.put("bp_systolic", bp_systolic.intValue());
                }
            } else if (obs.getConcept().equals(BP_DIASTOLIC)) {
                bp_diastolic = obs.getValueNumeric();
                if (!riskParamsMap.keySet().contains("bp_diastolic")) {
                    riskParamsMap.put("bp_diastolic", bp_diastolic.intValue());
                }
            } else if (obs.getConcept().equals(ESTIMATED_DELIVERY_DATE)) {
                edd = DATE_FORMAT.format(obs.getValueDate());
                if (!riskParamsMap.keySet().contains("edd")) {
                    riskParamsMap.put("edd", edd);
                }
            } else if (obs.getConcept().equals(FOETAL_HEART_RATE)) {
                fhr = obs.getValueNumeric();
                if (!riskParamsMap.keySet().contains("fhr")) {
                    riskParamsMap.put("fhr", fhr.intValue());
                }
            } else if (obs.getConcept().equals(ANC_VISIT_NUMBER)) {
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