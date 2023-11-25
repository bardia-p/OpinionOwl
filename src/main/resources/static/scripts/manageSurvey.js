/**
 * JavaScript AJAX call for when a logged-in user closes a survey.
 */

function closeSurvey(id) {
    $.ajax({
        type: 'POST',
        url: `/api/v1/closeSurvey/${id}`,
        success: function (res) {
            if(res === 200) {
                console.log("Success");
                location.reload();
            }
        },
        error: function(xhr, status, error) {
            // error handling
            console.error('Error closing survey:', error);
        }
    })
}