function success() {
    if (document.getElementById("username").value === "" || document.getElementById("password").value === "") {
        document.getElementById('submit-button').disabled = true;
    } else {
        document.getElementById('submit-button').disabled = false;
    }
}

let attemptsCount = 0;
const attemptsMaxCount = 3;

$(document).ready(function () {
    $.i18n().load({
        "en": "/i18n/en.json",
        "hy": "/i18n/hy.json",
        "ru": "/i18n/ru.json",
    });
    let localeValue = $("#locale").val();
    $.i18n().locale = localeValue;
    $("#username").val('');
});

$("#username").focus();
