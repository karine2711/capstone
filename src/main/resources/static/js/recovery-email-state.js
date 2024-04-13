$(document).ready(function () {
    let attemptsCount = 0;
    const attemptsMaxCount = 3;
    const csrfHeader = "X-CSRF-TOKEN";
    const submitButton = $('#btSubmit');
    const emailInput = $("#userEmail");
    const errorMessage = $("#error-message")
    emailInput.val("")

    $.i18n().load({
        "en": "/i18n/en.json",
        "hy": "/i18n/hy.json",
        "ru": "/i18n/ru.json",
    });

    let localeValue = $("#locale").val();
    $.i18n().locale = localeValue;

    submitButton.click(function (e) {
        e.preventDefault();
        if (!validateEmail($(emailInput).val())) {
            showErrorMessage();
            errorMessage.html($.i18n("registration.email.validation"));
        } else {
            submitEmail();
        }
    });

    function validateEmail(email) {
        const re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
        return re.test(String(email).toLowerCase())
    }

    function submitEmail() {
        const token = $('input[name^="_csrf"]').val();
        const emailValue = $(emailInput).val();
        $.ajax({
            type: "POST",
            url: location.origin + location.pathname + "/" + emailValue,
            contentType: "application/json; charset=utf-8",
            beforeSend: function (request) {
                request.setRequestHeader(csrfHeader, token);
            },
            success: function (xhr, status) {
                sessionStorage.setItem("email", emailValue);
                location.href = location.origin + location.pathname + "/questions";
            },
            error: function (xhr, status) {
                ++attemptsCount;
                errorMessage.html($.i18n("email.not.found"));
                if (attemptsCount >= attemptsMaxCount) {
                    $("#userEmail").prop("readonly", true);
                    $(submitButton).val("SIGN UP");
                    $(submitButton).click(function () {
                        window.location.href = location.origin + "/register";
                    })
                }
                if (xhr.status == 302 || xhr.status == 303) {
                    sessionStorage.setItem("email", emailValue);
                    location.href = location + "/reset-password/" + emailValue;
                } else {
                    showErrorMessage();
                }
            }
        });
    }

    function disableSubmitButton() {
        submitButton.attr('disabled', true);
        submitButton.css('opacity', 0.6);
        submitButton.css('cursor', 'not-allowed');
    }

    disableSubmitButton();

    function enableSubmitButton() {
        submitButton.attr('disabled', false);
        submitButton.removeAttr('tool');
        submitButton.css('opacity', 'unset');
        submitButton.css('cursor', 'pointer');
    }

    function showErrorMessage() {
        $('#alertIcon').removeClass("d-none");
        errorMessage.removeClass("d-none").addClass("d-inline");
    }

    function hideErrorMessage() {
        $('#alertIcon').addClass("d-none");
        errorMessage.removeClass("d-inline").addClass("d-none");
    }


    $(emailInput).on('input', function () {
        hideErrorMessage();
        if ($(this).val().length === 0) {
            disableSubmitButton();
        } else {
            enableSubmitButton()
        }
    });
});

