const submitButton = $("#submit-button");

submitButton.click((e) => {
    e.preventDefault();
    // assuming it's only survey Id in the params
    const urlParams = new URLSearchParams(window.location.search);
    const surveyId = urlParams.toString().split("=")[1];
    const data = {}
    $("#answer-survey-form input").each(function() {
        const id = $(this).attr("id");
        data[id] = $(this).val();
    });
    const dataJson = JSON.stringify(data);
    $.ajax({
        type: $("#answer-survey-form").attr("method"),
        url: `/api/v1/postSurveyResponses?surveyId=${surveyId}`,
        data: dataJson,
        contentType: 'application/json',
        success: function(res) {
            if (res === 200) {
                console.log("successfully posted survey response");
                window.location.href = "/";
            } else {
                console.log("Something went wrong with posting the survey response")
            }
        },
        error: function(err) {
            console.error("error posing survey response");
        }
    });
});