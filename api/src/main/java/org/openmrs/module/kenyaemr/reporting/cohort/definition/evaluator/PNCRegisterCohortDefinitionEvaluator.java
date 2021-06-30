/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.cohort.definition.evaluator;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.annotation.Handler;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.PNCRegisterCohortDefinition;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.SqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.openmrs.module.reporting.query.encounter.EncounterQueryResult;
import org.openmrs.module.reporting.query.encounter.definition.EncounterQuery;
import org.openmrs.module.reporting.query.encounter.evaluator.EncounterQueryEvaluator;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

/**
 * Evaluator for patients for PNC Register
 */
@Handler(supports = {PNCRegisterCohortDefinition.class})
public class PNCRegisterCohortDefinitionEvaluator implements EncounterQueryEvaluator {

    private final Log log = LogFactory.getLog(this.getClass());
	@Autowired
	EvaluationService evaluationService;

	public EncounterQueryResult evaluate(EncounterQuery definition, EvaluationContext context) throws EvaluationException {
		context = ObjectUtil.nvl(context, new EvaluationContext());
		EncounterQueryResult queryResult = new EncounterQueryResult(definition, context);

		Date startDate = (Date)context.getParameterValue("startDate");
		Date endDate = (Date)context.getParameterValue("endDate");
		String facilityOptions = (String) context.getParameterValue("facility");

		SqlQueryBuilder builder = new SqlQueryBuilder();
		String qry = "";
		Integer reportFacilityId = null;
		if (StringUtils.isNotBlank(facilityOptions) && facilityOptions.equalsIgnoreCase("All")) {
			qry = "SELECT pv.encounter_id from kenyaemr_etl.etl_mch_postnatal_visit pv inner join kenyaemr_etl.etl_mch_enrollment e on e.patient_id = pv.patient_id " +
					" where e.date_of_discontinuation is null and e.visit_date <= pv.visit_date and date(pv.visit_date) BETWEEN date(:startDate) AND date(:endDate);";
		} else {
			reportFacilityId = Integer.valueOf(facilityOptions);
			qry = "SELECT pv.encounter_id from kenyaemr_etl.etl_mch_postnatal_visit pv inner join kenyaemr_etl.etl_mch_enrollment e on e.patient_id = pv.patient_id " +
					" where pv.location_id in (:facilityList) and e.date_of_discontinuation is null and e.visit_date <= pv.visit_date and date(pv.visit_date) BETWEEN date(:startDate) AND date(:endDate);";
		}

		builder.append(qry);
		builder.addParameter("endDate", endDate);
		builder.addParameter("startDate", startDate);
		if (reportFacilityId != null) {
			builder.addParameter("facilityList", reportFacilityId);
		}

		List<Integer> results = evaluationService.evaluateToList(builder, Integer.class, context);
		queryResult.getMemberIds().addAll(results);
		return queryResult;
	}

}
