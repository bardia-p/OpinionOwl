const logoutButton = $("#logout-btn");

logoutButton.click((e) => {
    e.preventDefault();
    $.ajax({
        type: "POST",
        url: "/api/v1/logout",
        success: (res) => {
            if (res === 200) {
                window.location.href = "/";
            }
        },
        error: (error) => {
            console.error(error);
        }
    });
});