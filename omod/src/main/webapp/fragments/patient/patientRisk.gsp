<%
    ui.includeCss("kenyaemr", "referenceapplication.css", 100)
%>

<html>
<h3>Patient risk report</h3>
<body>
<button id="apiButton">Calculate Risk Score</button>
<div id="apiResponse"></div>
<script type="text/javascript">
    document.getElementById("apiButton").addEventListener("click", function() {
        callMihic();
    });
    function callMihic() {
        const apiUrl = 'http://35.241.202.94:5001/pph_process';
        const requestData = {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                "first_name": personName.givenName,
                "last_name": personName.familyName,
                "patient_id": currentPatient.id,
                "data": {
                    "Pallor  (Y/N/NA)": riskParams.pallor,
                    "Haemoglobin (Level/ND/NA)": riskParams.haemoglobin,
                    "Respiratory rate": riskParams.pulse,
                    "Blood Pressure": riskParams.bp,
                    "Anaemia": riskParams.anaemia,
                    "Estimated Date of Child Birth (EDC)": riskParams.edd,
                    "Date of visit": riskParams.visitDate,
                    "ANC Number  (Re Visit)": riskParams.visitNumber,
                    "FHR (bpm)": riskParams.fhr,
                    "Pallor  (Y/N/NA)_stage": "intrapartum",
                    "Haemoglobin (Level/ND/NA)_stage": "antepartum",
                    "Respiratory rate_stage": "intrapartum",
                    "Blood Pressure_stage": "intrapartum",
                    "Anaemia_stage": "intrapartum",
                    "FHR (bpm)_stage": "intrapartum"
                }
            }),
        };

        console.log("Mihic request data" + requestData);

        // Make the API POST request
        fetch(apiUrl, requestData)
            .then(response => response.json())
            .then(data => {
                kenyaui.notifySuccess(data.message);
            })
            .catch(error => {
                console.error('Error:', error);
                kenyaui.notifyError('An error occurred while calculating risk score');
            });
    }

</script>
<div><a href="https://app.powerbi.com/groups/me/reports/c47f24ee-4c78-4439-9412-0cda3ceb8d96/ReportSection6a634c30e01bad307396?experience=power-bi&filter=unique_patient_id%2Funique_patient_id%20eq%20%27${currentPatient.id}%27"
        target="_blank">View patient risk report</a></div>
</body>
</html>