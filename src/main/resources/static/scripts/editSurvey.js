const updateSurveyBtn = $("#update-survey");

/**
 * Add survey questions to the survey
 * @param questionsJson
 */
const addEditableQuestions = (questionsJson) => {
    const jsonRes = JSON.parse(questionsJson);
    const questions = jsonRes['questions'];
    Object.values(questions).map((question) => {
        const type = question['type'];
        const prompt = question['prompt'];
        if (type === "Long Answer") {
            addTextQuestionRow(prompt);
        } else if (type === "Radio Choice") {
            // Handle Radio Choice
            addRadioChoicesRow(prompt, question['choices']);
        } else if (type === "Range") {
            // Handle Range
            addRangeQuestionRow(prompt, type['ranges']);
        }
    });
}

/**
 * Handle the survey update button functionality
 */
updateSurveyBtn.click((e) => {
    e.preventDefault();
    const surveyId = $("#surveyId").text();
    const dataDictionary = parseSurveyFormData();
    if(!dataDictionary) {
        return
    }
    console.log(dataDictionary);
    // send post using ajax
    const dataJson = JSON.stringify(dataDictionary);
    $.ajax({
        type: $("#survey-container").attr("method"),
        url: '/api/v1/updateSurvey/' + surveyId,
        data: dataJson,
        contentType: 'application/json',
        success: function(res) {
            // success handling
            if (res === 200) {
                console.log('Survey updated successfully');
                setToast("success", "Survey Updated!", "Successfully updated the survey", true);
                window.location.href = "/";
            } else {
                setToast("error", "Something went wrong", "You cannot complete this request at the moment");
            }
        },
        error: function(xhr, status, error) {
            // error handling
            console.error('Error creating survey:', error);
            setToast("error", "Something went wrong", "Could not update survey");
        }
    });
});

/**
 * On document ready, collect the data for the survey
 */
$(document).ready(function (){
    let surveyId = $("#surveyId").text();
    $.ajax({
        type: 'GET',
        url: '/api/v1/getSurveyQuestions/' + surveyId,
        success: function(res) {
            // success handling
            if (res !== ""){
                addEditableQuestions(res);
            } else {
                setToast("error", "Something went wrong", "Could not fetch survey data");
            }
        },
        error: function(xhr, status, error) {
            // error handling
            console.error('Error fetching survey data:', error);
            setToast("error", "Something went wrong", "Could not fetch survey data");
        }
    });
});

