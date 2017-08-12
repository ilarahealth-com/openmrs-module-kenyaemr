/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.module.kenyaemr.fragment.controller.patient;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.ObsService;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.Metadata;
import org.openmrs.module.kenyaemr.page.controller.FamilyAndPartnerTestingPageController;
import org.openmrs.module.kenyaemr.wrapper.VisitWrapper;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.openmrs.util.PrivilegeConstants;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Visit summary fragment
 */
public class CurrentVisitSummaryFragmentController {

	protected static final Log log = LogFactory.getLog(CurrentVisitSummaryFragmentController.class);

	PatientService patientService = Context.getPatientService();
	PersonService personService = Context.getPersonService();
	EncounterService encounterService = Context.getEncounterService();
	ObsService obsService = Context.getObsService();
	ConceptService conceptService = Context.getConceptService();
	SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

	// triage concepts
	Concept WEIGHT = Dictionary.getConcept(Metadata.Concept.WEIGHT_KG);
	Concept HEIGHT = Dictionary.getConcept(Metadata.Concept.HEIGHT_CM);
	Concept TEMP = Dictionary.getConcept("5088AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
	Concept PULSE_RATE = Dictionary.getConcept("5087AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
	Concept BP_SYSTOLIC = Dictionary.getConcept("5085AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
	Concept BP_DIASTOLIC = Dictionary.getConcept("5086AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
	Concept RESPIRATORY_RATE = Dictionary.getConcept("5242AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
	Concept OXYGEN_SATURATION = Dictionary.getConcept("5092AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
	Concept MUAC = Dictionary.getConcept("1343AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
	Concept LMP = Dictionary.getConcept("1427AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");



	
	public void controller(@FragmentParam(value = "visit", required = false) Visit visit, @FragmentParam("patient") Patient patient, FragmentModel model) {
		/**
		 * - date last seen (last 3 visits)
		 - diagnoses
		 - medications
		 - next appointment
		 - Triage
		 */

		if(visit != null) {
			// Get recorded triage
			List<Obs> obs = obsService.getObservations(
					Arrays.asList(Context.getPersonService().getPerson(patient.getPersonId())),
					null,
					Arrays.asList(WEIGHT, HEIGHT, TEMP, PULSE_RATE, BP_SYSTOLIC, BP_DIASTOLIC, RESPIRATORY_RATE, OXYGEN_SATURATION, MUAC, LMP),
					null,
					null,
					null,
					Arrays.asList("obsId"),
					null,
					null,
					visit.getStartDatetime(),
					visit.getStopDatetime(),
					false
			);

			if (obs != null) {

				model.addAttribute("vitals", getVisitVitals(obs));
				log.info("Existing Triage" + getVisitVitals(obs));
			} else {
				model.addAttribute("vitals", null);
			}
		}
		/*model.addAttribute("visit", visit);
		model.addAttribute("sourceForm", new VisitWrapper(visit).getSourceForm());
		model.addAttribute("allowVoid", Context.hasPrivilege(PrivilegeConstants.DELETE_VISITS));*/
	}

	private SimpleObject getVisitVitals(List<Obs> obsList) {

		Double weight = null;
		Double height = null;
		Double temp = null;
		Double pulse = null;
		Double bp_systolic = null;
		Double bp_diastolic = null;
		Double resp_rate = null;
		Double oxygen_saturation = null;
		Double muac = null;
		String lmp = null;

		Map<String, Object> vitalsMap = new HashMap<String, Object>();
		for (Obs obs : obsList) {
			if (obs.getConcept().equals(WEIGHT) ) {
				weight = obs.getValueNumeric();
				if(!vitalsMap.keySet().contains("weight")) {
					vitalsMap.put("weight", weight);
				}
			} else if (obs.getConcept().equals(HEIGHT )) {
				height = obs.getValueNumeric();
				if(!vitalsMap.keySet().contains("height")) {
					vitalsMap.put("height", height);
				}
			} else if (obs.getConcept().equals(TEMP) ) {
				temp = obs.getValueNumeric();
				if(!vitalsMap.keySet().contains("temp")) {
					vitalsMap.put("temp", temp);
				}
			} else if (obs.getConcept().equals(PULSE_RATE )) {
				pulse = obs.getValueNumeric();
				if(!vitalsMap.keySet().contains("pulse")) {
					vitalsMap.put("pulse", pulse.intValue());
				}
			} else if (obs.getConcept().equals(BP_SYSTOLIC )) {
				bp_systolic = obs.getValueNumeric();
				if(!vitalsMap.keySet().contains("bp_systolic")) {
					vitalsMap.put("bp_systolic", bp_systolic.intValue());
				}
			} else if (obs.getConcept().equals(BP_DIASTOLIC )) {
				bp_diastolic = obs.getValueNumeric();
				if(!vitalsMap.keySet().contains("bp_diastolic")) {
					vitalsMap.put("bp_diastolic", bp_diastolic.intValue());
				}
			} else if (obs.getConcept().equals(RESPIRATORY_RATE )) {
				resp_rate = obs.getValueNumeric();
				if(!vitalsMap.keySet().contains("resp_rate")) {
					vitalsMap.put("resp_rate", resp_rate.intValue());
				}
			} else if (obs.getConcept().equals(OXYGEN_SATURATION) ) {
				oxygen_saturation = obs.getValueNumeric();
				if(!vitalsMap.keySet().contains("oxygen_saturation")) {
					vitalsMap.put("oxygen_saturation", oxygen_saturation.intValue());
				}
			} else if (obs.getConcept().equals(MUAC) ) {
				muac = obs.getValueNumeric();
				if(!vitalsMap.keySet().contains("muac")) {
					vitalsMap.put("muac", muac);
				}
			} else if (obs.getConcept().equals(LMP) ) {
				lmp = DATE_FORMAT.format(obs.getValueDate());
				if(!vitalsMap.keySet().contains("lmp")) {
					vitalsMap.put("lmp", lmp);
				}
			}

		}

		if (bp_diastolic != null && bp_systolic != null)
			vitalsMap.put("bp", new StringBuilder().append(bp_systolic.intValue()).append("/").append(bp_diastolic.intValue()));


		return SimpleObject.create(
				"weight", vitalsMap.get("weight") != null? new StringBuilder().append(vitalsMap.get("weight")).append(" Kg"): "",
				"height", vitalsMap.get("height") != null? new StringBuilder().append(vitalsMap.get("height")).append(" cm"): "",
				"temperature", vitalsMap.get("temp") != null? new StringBuilder().append(vitalsMap.get("temp")): "",
				"pulse", vitalsMap.get("pulse") != null? new StringBuilder().append(vitalsMap.get("pulse")).append(" "): "",
				"bp", vitalsMap.get("bp") != null? new StringBuilder().append(vitalsMap.get("bp")).append(" mmHg"): "",
				"resp_rate", vitalsMap.get("resp_rate") != null? new StringBuilder().append(vitalsMap.get("resp_rate")).append(" "): "",
				"oxygen_saturation", vitalsMap.get("oxygen_saturation") != null? new StringBuilder().append(vitalsMap.get("oxygen_saturation")).append(" "): "",
				"muac", vitalsMap.get("muac") != null? new StringBuilder().append(vitalsMap.get("muac")).append(" "): "",
				"lmp", vitalsMap.get("lmp") != null? new StringBuilder().append(vitalsMap.get("lmp")): ""
		);
	}
}