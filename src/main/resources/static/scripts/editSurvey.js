const updateSurveyBtn = $("#update-survey");

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

updateSurveyBtn.click((e) => {
    e.preventDefault();
    const surveyId = $("#surveyId").text();
    const dataDictionary = parseSurveyFormData();
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
                window.location.href = "/";
            } else {
                alert('You cannot update this survey at this timme.');
            }
        },
        error: function(xhr, status, error) {
            // error handling
            console.error('Error creating survey:', error);
        }
    });
});

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
                alert('You cannot complete this request');
            }
        },
        error: function(xhr, status, error) {
            // error handling
            console.error('Error creating survey:', error);
        }
    });
});

