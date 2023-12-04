const submitButton = $("#registerUser-submit");
const errorMessage = $("#error-message");
/**
 * The JavaScript AJAX call for when a new user is registered (i.e. the submit button is clicked).
 */

submitButton.click((e) => {
    e.preventDefault();
    const dataDictionary = {};
    if (!$("#username").val()) {
        alert("No username inputted. Please provide a username!")
        return;
    }
    else {
        dataDictionary["username"] = $("#username").val();
    }
    if (!$("#password").val()) {
        alert("No password inputted. Please provide a username!")
        return;
    }
    else {
        dataDictionary["password"] = $("#password").val();
    }
    var formData = JSON.stringify(dataDictionary);

    console.log(formData);
    console.log("Got here");

    $.ajax({
        type: "POST",
        url: "/api/v1/createUser",
        data: formData,
        dataType: "json",
        contentType: "application/json",
        success: function (res) {
            console.log('User registered successfully');
            if (res === 200) {window.location.href = "/"}
            else if (res === 401) errorMessage.text("This username already exists!")
        },
        error: function (xhr, status, error) {
            // error handling
            console.error('Error creating user:', error);
        }
    })

});