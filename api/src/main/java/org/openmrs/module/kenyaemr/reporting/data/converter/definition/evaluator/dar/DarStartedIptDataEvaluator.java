/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.data.converter.definition.evaluator.dar;

import org.openmrs.annotation.Handler;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.dar.DarStartedIptDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.dar.DarTbScreeningDataDefinition;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.data.person.evaluator.PersonDataEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.SqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.Map;

/**
 * Evaluates DarTbScreeningDataDefinition to produce a VisitData
 */
@Handler(supports= DarStartedIptDataDefinition.class, order=50)
public class DarStartedIptDataEvaluator implements PersonDataEvaluator {

    @Autowired
    private EvaluationService evaluationService;

    public EvaluatedPersonData evaluate(PersonDataDefinition definition, EvaluationContext context) throws EvaluationException {
        EvaluatedPersonData c = new EvaluatedPersonData(definition, context);
        DarStartedIptDataDefinition def = (DarStartedIptDataDefinition) definition;
        Integer minAge = def.getMinAge();
        Integer maxAge = def.getMaxAge();
        String sex = def.getSex();
        String qry = "";
        if (sex != null) {
            qry = "SELECT f.patient_id, 'X' iptStarted \n" +
                    "FROM kenyaemr_etl.etl_patient_hiv_followup f\n" +
                    "         inner join kenyaemr_etl.etl_patient_demographics p on p.patient_id = f.patient_id and  p.voided = 0 and p.Gender = ':sex'\n" +
                    "         inner join kenyaemr_etl.etl_ipt_initiation i on f.patient_id = i.patient_id  and date(i.visit_date) = date(:startDate) and i.voided = 0\n" +
                    "where date(f.visit_date) = date(:startDate) and f.voided = 0 ";
        } else {
            qry = "SELECT f.patient_id, 'X' iptStarted \n" +
                    "FROM kenyaemr_etl.etl_patient_hiv_followup f\n" +
                    "         inner join kenyaemr_etl.etl_patient_demographics p on p.patient_id = f.patient_id and  p.voided = 0 \n" +
                    "         inner join kenyaemr_etl.etl_ipt_initiation i on f.patient_id = i.patient_id  and date(i.visit_date) = date(:startDate) and i.voided = 0\n" +
                    "where date(f.visit_date) = date(:startDate) and f.voided = 0 ";
        }
        String ageConditionString = "";
        if (minAge != null && maxAge != null) {
            ageConditionString = " and TIMESTAMPDIFF(YEAR, date(p.DOB), date(:startDate)) between :minAge and :maxAge ";
        } else if (minAge != null) {
            ageConditionString = " and TIMESTAMPDIFF(YEAR, date(p.DOB), date(:startDate)) >= :minAge ";
        } else if (maxAge != null) {
            ageConditionString = " and TIMESTAMPDIFF(YEAR, date(p.DOB), date(:startDate)) <= :maxAge ";
        }
        qry = qry.concat(ageConditionString);

        if (maxAge != null) {
            qry = qry.replace(":maxAge", maxAge.toString());
        }

        if (minAge != null) {
            qry = qry.replace(":minAge", minAge.toString());
        }

        if (sex != null) {
            qry = qry.replace(":sex", sex);
        }
        SqlQueryBuilder queryBuilder = new SqlQueryBuilder();
        queryBuilder.append(qry);
        Date startDate = (Date)context.getParameterValue("startDate");
        queryBuilder.addParameter("startDate", startDate);
        Map<Integer, Object> data = evaluationService.evaluateToMap(queryBuilder, Integer.class, Object.class, context);
        c.setData(data);
        return c;
    }
}