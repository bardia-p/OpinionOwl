const submitButton = $("#loginUser-submit");
const errorMessage = $("#error-message");

/**
 * The JavaScript AJAX call for when a new user is registered (i.e. the submit button is clicked).
 */
submitButton.click((e) => {
    e.preventDefault();
    const dataDictionary = {};
    dataDictionary["username"] = $("#username").val();
    dataDictionary["password"] = $("#password").val();
    const formData = JSON.stringify(dataDictionary);

    $.ajax({
        type: "POST",
        url: "/api/v1/loginUser",
        data: formData,
        dataType: "json",
        contentType: "application/json",
        success: function (res) {
            console.log('User registered successfully');
            if (res === 200) {
                setCookie(formData)
            }
            else if (res === 401) errorMessage.text("Invalid Username or Password");
        },
        error: function (xhr, status, error) {
            // error handling
            console.error('Error logging in user:', error);
        }
    })

});


/**
 * The Javascript AJAX call that creates a cookie when a user logs in
 */
const setCookie = (formData) => {
    $.ajax({
        type: "POST",
        url: "/api/v1/setCookie",
        data: formData,
        dataType: "json",
        contentType: "application/json",
        success: function (res) {
            console.log('cookie created successfully');
            if (res === 200) window.location.href = '/';
        },
        error: function (xhr, status, error) {
            // error handling
            console.error('Error creating cookie:', error);
        }
    })
}