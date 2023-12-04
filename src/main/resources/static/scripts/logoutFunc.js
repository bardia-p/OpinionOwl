const logoutButton = $("#logout-btn");
const profileName = $("#profile-name");
const dropDownSelections = $("#drop-down-selections");

profileName.click(() => {
    if (dropDownSelections.css("opacity") === "1") {
        dropDownSelections.css("opacity", "0");
        dropDownSelections.css("visibility", "hidden");
        return;
    }
    dropDownSelections.css("opacity", "1");
    dropDownSelections.css("visibility", "visible");
})

/**
 * The JavaScript AJAX call for when a user logs out of their account
 */
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