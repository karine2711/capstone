$(document).ready(async function () {
    const csrfHeader = "X-CSRF-TOKEN";
    const token = $('input[name^="_csrf"]').val();
    let titleEn = "";
    let titleRu = "";
    let descEn = "";
    let descRu = "";
    let startTimeSelect = $("#start-time");
    let endTimeSelect = $("#end-time");
    const TITLE_MIN_LENGTH = 10;
    const TITLE_MAX_LENGTH = 70;

    await $.i18n().load({
        "en": "/i18n/en.json",
        "hy": "/i18n/hy.json",
        "ru": "/i18n/ru.json",
    });

    addTimesToSelect(startTimeSelect);
    addTimesToSelect(endTimeSelect);

    $("#cancel-btn").click(function () {
        window.location.href = location.origin + "/homepage";
    });
    $(".close").click(function () {
        window.location.href = location.origin + "/homepage";
    });

    $('.btn-ru').click(function () {

        $('.btn-ru').addClass('button-shadow');
        $('.btn-en').removeClass('button-shadow');

        refreshVariables();
        $('#ru-section').removeClass("d-none");
        $('#en-section').addClass("d-none");
    });

    $('.btn-en').click(function () {

        $('.btn-en').addClass('button-shadow');
        $('.btn-ru').removeClass('button-shadow');

        refreshVariables();
        $('#en-section').removeClass("d-none");
        $('#ru-section').addClass("d-none");
    });

    $('.btn-en').addClass('button-shadow');
    $('.btn-ru').removeClass('button-shadow');
    refreshVariables();
    $('#en-section').removeClass("d-none");
    $('#ru-section').addClass("d-none");

    addValidationHtml()

    jQuery.validator.addMethod("requiredAM", function (value, element) {
        return value !== undefined && value !== null && value.trim().length > 0;
    }, $.validator.messages.requiredAM);
    jQuery.validator.addMethod("requiredRU", function (value, element) {
        return value !== undefined && value !== null && value.trim().length > 0;
    }, $.validator.messages.requiredRU);
    jQuery.validator.addMethod("requiredEN", function (value, element) {
        return value !== undefined && value !== null && value.trim().length > 0;
    }, $.validator.messages.requiredEN);
    jQuery.validator.addMethod("titleRangeLength", function (value, element, params) {
        return this.optional(element) || jQuery.validator.methods.rangelength.call(this, value.trim(), element, params);
    });
    jQuery.validator.addMethod("validPhone", function (value, element, params) {
        return this.optional(element) || /^\+374[\d]{7,10}$/.test(value);
    }, $.validator.messages.validPhone);
    jQuery.validator.addMethod("validEmail", function (value, element, params) {
        return this.optional(element) || /^[\w!#$%&’*+/=?`{|}~^-]+(?:\.[\w!#$%&’*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\.)+[a-zA-Z]{2,6}$/.test(value);
    }, $.validator.messages.validEmail);
    jQuery.validator.addMethod("earlierThanDay", function (value, element, params) {
        return value > params.val();
    }, $.validator.messages.earlierThanDay);
    jQuery.validator.addMethod("earlierThanTime", function (value, element, params) {
        return value.replace(':', '') > params.val().replace(':', '');
    }, $.validator.messages.earlierThanTime);

    jQuery.validator.addMethod("laterThanDay", function (value, element, params) {
        return value < params.val();
    }, $.validator.messages.laterThanDay);
    jQuery.validator.addMethod("laterThanTime", function (value, element, params) {
        return value.replace(':', '') < params.val().replace(':', '');
    }, $.validator.messages.laterThanTime);

    $('#start-day').on('change', function () {
        $(this).val($(this).find('option:selected').val());
        $(this).trigger("chosen:updated");
    });

    $('#admin_form').validate({
        rules: {
            titleAM: {
                requiredAM: true,
                titleRangeLength: [TITLE_MIN_LENGTH, TITLE_MAX_LENGTH]
            },
            titleRU: {
                requiredRU: true,
                titleRangeLength: [TITLE_MIN_LENGTH, TITLE_MAX_LENGTH]
            },
            titleEN: {
                requiredEN: true,
                titleRangeLength: [TITLE_MIN_LENGTH, TITLE_MAX_LENGTH]
            },
            email: {
                validEmail: true
            },
            phone: {
                validPhone: true
            },
            endWorkingDayId: {
                earlierThanDay: $('#start-day')
            },
            endWorkingTime: {
                earlierThanTime: $('#start-time')
            },
            startWorkingDayId: {
                laterThanDay: $('#end-day')
            },
            startWorkingTime: {
                laterThanTime: $("#end-time")
            }
        },
        errorPlacement: function (label, element) {
            element.removeClass('error');
            element.removeClass('valid');
            label.removeClass('error');
            let errorMessage = label.text();

            let hasPreviousErrors = element.parent().find('.custom-invalid-feedback').css('display') !== 'none';
            let customInvalidFeedback = element.parent().find('.custom-invalid-feedback');

            if (element[0].getAttribute('id') === 'residency') {
            }

            if (hasPreviousErrors) {
                if (errorMessage) {
                    customInvalidFeedback.find('.error-message').text(errorMessage);
                    customInvalidFeedback.show()
                } else {
                    if (element.attr("id") === "start-day") $('#end-day').parent().find('.custom-invalid-feedback').hide();
                    if (element.attr("id") === "end-day") $('#start-day').parent().find('.custom-invalid-feedback').hide();
                    if (element.attr("id") === "start-time") $('#end-time').parent().find('.custom-invalid-feedback').hide();
                    if (element.attr("id") === "end-time") $('#start-time').parent().find('.custom-invalid-feedback').hide();
                    customInvalidFeedback.hide();
                }
            } else if (errorMessage) {
                customInvalidFeedback.find('.error-message').text(errorMessage);
                customInvalidFeedback.show();
            }
        },
        success: function (a, b) {
        },
        submitHandler: function (form) {
            let data = new FormData(form);
            $.ajax({
                type: 'POST',
                url: form.action,
                processData: false,
                contentType: false,
                data: data,
                beforeSend: function (request) {
                    request.setRequestHeader(csrfHeader, token);
                },
                success: function () {
                    location.href = location.origin + "/homepage";
                },
            });
        }
    });

    $.extend($.validator.messages, {
        validEmail: $.i18n("registration.email.validation"),
        requiredAM: $.i18n("title.am.required"),
        requiredEN: $.i18n("title.en.required"),
        requiredRU: $.i18n("title.ru.required"),
        validPhone: $.i18n("registration.phone.validation"),
        earlierThanDay: $.i18n("update.content.day.earlierThan"),
        earlierThanTime: $.i18n("update.content.time.earlierThan"),
        laterThanDay: $.i18n("update.content.day.laterThan"),
        laterThanTime: $.i18n("update.content.time.laterThan"),
        titleRangeLength: $.i18n("title.lengthRange")
    });

    function refreshVariables() {
        titleEn = $('#title-en').val();
        titleRu = $('#title-ru').val();
        descEn = $('#description-en').val();
        descRu = $('#description-ru').val();
    }

    function addTimesToSelect(select) {
        let option = "<option value='" + "10" + ":" + "00" + "'>" + "10" + ":";
        option = option + "00";
        option = option.concat("</option>");
        select.append(option);
        for (let hour = 10; hour < 17; hour++) {
            for (let minute = 15; minute <= 60; minute += 15) {
                let option = null;
                let minute2 = minute === 0 ? "00" : minute;
                if (minute === 60) {
                    let hour1 = hour + 1;
                    option = "<option value='" + hour1 + ":" + "00" + "'>" + hour1 + ":";
                    option = option + "00";
                    option = option.concat("</option>");
                } else {
                    option = "<option value='" + hour + ":" + minute2 + "'>" + hour + ":";
                    option = option + minute2;
                    option = option.concat("</option>");
                }
                if (option !== null) select.append(option)
            }
        }
    }

    document.querySelectorAll(".custom-invalid-feedback").forEach(
        field => {
            let errorIcon = document.createElement("IMG");
            errorIcon.classList.add("error-icon");
            errorIcon.setAttribute("src", "/images/icon.svg");
            field.prepend(errorIcon);
        }
    );
});

