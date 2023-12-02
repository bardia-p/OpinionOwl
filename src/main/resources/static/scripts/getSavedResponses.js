const urlParams = new URLSearchParams(window.location.search);
const userId = urlParams.toString().split("=")[1];
const responseContainer = $(".user-response");
/**
 * Generates the appropriate response classes.
 * @param {Object} res
 */
const displayResponses = (res) => {
    let jsonRes = JSON.parse(res);
    console.log(jsonRes);
    for(let r of Object.keys(jsonRes)) {
        // Check to see if there is a response for the question.
        let response = jsonRes[r];
        if (Object.keys(response).length !== 0){
            let responseHTML = `
            <ul id="text-container">
                <span className="question-title">Response #: ${r}</span>
                <span className="question-title">Survey #: ${response["surveyId"]}</span>
            `;

            responseContainer.append(responseHTML);

            for(let q of Object.keys(response["answers"])) {
                let answerHTML = `
                <li className="text-answers">
                    <span className="question-title">Question #: ${q} | Answer: ${response["answers"][q]}</span>
                </li>
                `;
                responseContainer.append(answerHTML);
            }

            responseContainer.append(`</ul>`);
        }
    }
};

$(document).ready(function () {
    $.ajax({
        type: 'GET',
        url: '/api/v1/savedResponses/' + userId,
        success: function(res) {
            // success handling
            if (res !== ""){
                displayResponses(res);
            } else {
                alert('You cannot complete this request');
            }
        },
        error: function(xhr, status, error) {
            // error handling
            console.error('Error getting the responses:', error);
        }
    });
});