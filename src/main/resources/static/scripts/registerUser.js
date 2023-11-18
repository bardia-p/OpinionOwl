const submitButton = $("#registerUser-submit");

submitButton.click((e) => {
    e.preventDefault();
    const dataDictionary = {};
    dataDictionary["username"] = $("#username").val();
    dataDictionary["password"] = $("#password").val();
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
            if (res === 200) window.location.href = "/";
        },
        error: function (xhr, status, error) {
            // error handling
            console.error('Error creating user:', error);
        }
    })

});