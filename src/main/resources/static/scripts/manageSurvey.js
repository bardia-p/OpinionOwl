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
                setToast("success", "Survey Closed", "Successfully closed survey", true);
                location.reload();
            } else {
                alert('You cannot complete this action.');
                setToast("error", "Something went wrong", "Cannot close survey at this time!");
            }
        },
        error: function(xhr, status, error) {
            // error handling
            setToast("error", "Something went wrong", "Cannot close survey at this time!");
            console.error('Error closing survey:', error);
        }
    })
}