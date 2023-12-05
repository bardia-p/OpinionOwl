const submitButton = $("#submit-button");

submitButton.click((e) => {
    e.preventDefault();
    // assuming it's only survey Id in the params
    const urlParams = new URLSearchParams(window.location.search);
    const surveyId = urlParams.toString().split("=")[1];
    const data = {}
    $("#answer-survey-form input").each(function() {
        const id = $(this).attr("id");
        if ($(this).attr("type") === "radio" && $(this).is(':checked')){
            data[id] = $(this).next().text();
        } else if ($(this).attr("type") !== "radio") {
            data[id] = $(this).val();
        }
    });
    const dataJson = JSON.stringify(data);
    $.ajax({
        type: $("#answer-survey-form").attr("method"),
        url: `/api/v1/postSurveyResponses/${surveyId}`,
        data: dataJson,
        contentType: 'application/json',
        success: function(res) {
            if (res === 200) {
                setToast("success", "Response Submitted", "Posted Survey Response!", true);
                window.location.href = "/";
            } else {
                setToast("error", "Something went wrong", "Could not post survey response at this time!");
                console.error("Something went wrong with posting the survey response");
            }
        },
        error: function(err) {
            console.error("error posting survey response");
            setToast("error", "Something went wrong", "Something went wrong with responding to the survey");
        }
    });
});