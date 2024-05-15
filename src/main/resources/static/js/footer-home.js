$(document).ready(function () {
    let langName, imgName;
    const angle = "#lang-selector i";
    let workingDays = $("#working-days");
    let workingHours = $("#working-hours");
    let phoneNumber = $("#phone-number");
    let footerEmail = $("#footer-email");
    let footerAddress = $("#footer-address");
    let locale = $("#locale").val();

    switch ($("#footer-locale").val()) {
        case "en":
            langName = "English";
            imgName = "/images/us-circle.png";
            break;
        case "hy":
            langName = "Armenian";
            imgName = "/images/am-circle.png";
            break;
        case "ru":
            langName = "Russian";
            imgName = "/images/ru-circle.png";
    }
    $("#lang-selector small").html(langName);
    $("#lang-selector img").attr("src", imgName);

    $("#lang-selector").click(function () {
        $(this).find("i").toggleClass("fa-angle-up fa-angle-down");
    });

    $(".lang-block").click(function () {
        $("#lang-selector").find("i").toggleClass("fa-angle-up fa-angle-down");
    });

    $(document).click(function () {
        if ($(angle).hasClass("fa-angle-down")) {
            $(angle).toggleClass("fa-angle-down fa-angle-up");
        }
    });

    $.ajax({
        type: 'GET',
        url: "/footer-info",
        success: function (data) {
            switch (locale) {
                case "ru":
                    if (workingDays.length) {
                        workingDays.html(data['startWorkingDay']['displayValueRU'] + " - " + data['endWorkingDay']['displayValueRU']);
                    }
                    footerAddress.html(data['addressRU']);
                    break;
                case "en":
                    if (workingDays.length) {
                        workingDays.html(data['startWorkingDay']['displayValueEN'] + " - " + data['endWorkingDay']['displayValueEN']);
                    }
                    footerAddress.html(data['addressEN']);
                    break;
                default:
                    if (workingDays.length) {
                        workingDays.html(data['startWorkingDay']['displayValueAM'] + " - " + data['endWorkingDay']['displayValueAM']);
                    }
                    footerAddress.html(data['addressAM']);
                    break;
            }
            workingHours.html(data['startWorkingTime'].slice(0, -3) + " - " + data['endWorkingTime'].slice(0, -3))
        
            phoneNumber.html(data.phone.slice(0, 4) + " (" + data.phone.slice(4, 6) + ") " + data.phone.slice(6, 8) +
                "-" + data.phone.slice(8, 10) + "-" + data.phone.slice(10, 12));
            footerEmail.html(data.email);
        },
    });
})
;
