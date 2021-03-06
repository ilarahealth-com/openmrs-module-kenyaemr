/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.calculation.library.hiv.art;

import org.openmrs.Obs;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * Calculate the first date a client was started on arvs
 */
public class DateARV1Calculation extends AbstractPatientCalculation {

	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues,
										 PatientCalculationContext context) {

		CalculationResultMap patientsStartedAtThisFacility = calculate(new OriginalCohortCalculation(), cohort, context);
		CalculationResultMap transferInPatients = Calculations.lastObs(Dictionary.getConcept(Dictionary.ANTIRETROVIRAL_TREATMENT_START_DATE), cohort, context);

		CalculationResultMap result = new CalculationResultMap();
		for (Integer ptId : cohort) {
			Date dateArv1 = null;
			Date startedHere = EmrCalculationUtils.datetimeResultForPatient(patientsStartedAtThisFacility, ptId);
			Obs transferIns = EmrCalculationUtils.obsResultForPatient(transferInPatients, ptId);
			if(startedHere != null){
				dateArv1 = startedHere;
			}

			if((transferInPatients.containsKey(ptId)) && (transferIns != null)){
				dateArv1 = transferIns.getValueDate();
			}
			result.put(ptId, new SimpleResult(dateArv1, this));

		}

		return  result;
	}
}
