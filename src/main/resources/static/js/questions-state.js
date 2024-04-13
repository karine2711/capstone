$(document).ready(function () {
    let email = sessionStorage.getItem("email");
    if (email == null) location.href = location.origin;

    $.i18n().load({
        "en": "/i18n/en.json",
        "hy": "/i18n/hy.json",
        "ru": "/i18n/ru.json",
    });

    let localeValue = $("#locale").val();
    $.i18n().locale = localeValue;

    $(".main-carousel").parentsUntil("main").addClass('margin-bottom-50');
    const csrfHeader = "X-CSRF-TOKEN";
    const token = $('input[name^="_csrf"]').val();
    let questionId = 0;
    let attempt = 0;
    let questions = [];

    const action = $('form').attr("action");
    const submitButton = $('#submitAnswer');
    const signUpButton = $('#btSubmit-2');
    const question = $('#question');
    const form = $('#form');
    const answerInput = $('#answer');
    const errorMessage = $('#error-message');

    disableSubmitButton();


    $.ajax({
        url: action + "/" + sessionStorage.getItem("email"),
        type: 'post',
        contentType: "application/json",
        beforeSend: function (request) {
            request.setRequestHeader(csrfHeader, token);
        },
        success: function (data) {
            questions = data;
            showQuestions();
        }
    });

    function showQuestions() {
        $('#email').val(sessionStorage.getItem("email"));
        $('#questionId').html(questionId + 1);
        hideErrorMessage();
        question.html(questions[questionId]['description']);
        answerInput.prop("readonly", false);
        answerInput.val('');
    }

    form.submit(function (e) {
        e.preventDefault();
        let answer = answerInput.val();
        let dto = {
            "questionId": questions[questionId].id,
            "answer": answer
        };
        $.ajax({
            url: action + "/check-answers/" + sessionStorage.getItem("email"),
            type: 'post',
            contentType: "application/json; charset=utf-8",
            beforeSend: function (request) {
                request.setRequestHeader(csrfHeader, token);
            },
            data: JSON.stringify(dto),
            success: function () {
                answerInput.prop("readonly", true);
                showErrorMessage();
                errorMessage.removeClass('text-danger').addClass('text-success');
                errorMessage.text($.i18n('answered.correctly'));
                $('.alert-icon').addClass("d-none");
                disableSubmitButton();
                setTimeout(function () {
                    redirectToChangePassword()
                }, 3000);
            },

            error: async function () {
                attempt++;
                showErrorMessage();
                if (attempt >= 6) {
                    answerInput.prop("readonly", true);
                    errorMessage.text($.i18n('account.blocked'));
                    $(submitButton).removeAttr("type").attr("type", "hidden");
                    $(signUpButton).removeAttr("type").attr("type", "submit");
                    enableSubmitButton();
                    form.off('submit');
                    form.submit(function (e) {
                        e.preventDefault();
                        redirectToSignUp();
                    });
                    blockUser();
                } else if (attempt === 3) {
                    answerInput.prop("readonly", true);
                    errorMessage.html($.i18n("answered.incorrectly.third.attempt"));
                    questionId++;
                    disableSubmitButton();
                    setTimeout(function () {
                        showQuestions()
                    }, 2000);
                } else {
                    let attempts = (6 - attempt) % 3;

                    if (localeValue === 'en') {
                        errorMessage.html($.i18n('answered.incorrectly', attempts, attempts > 1 ? 's' : ''));
                    } else if (localeValue === 'ru') {
                        errorMessage.html($.i18n('answered.incorrectly', attempts, attempts > 1 ? 'и' : 'а'));
                    } else if (localeValue === 'hy') {
                        errorMessage.html($.i18n('answered.incorrectly', attempts, attempts > 1 ? '' : ''));
                    }

                    answerInput.val('');
                    disableSubmitButton();
                }
            }
        });
    })

    function blockUser() {
        $.ajax({
            url: action + "/block-user/" + sessionStorage.getItem("email"),
            type: 'post',
            contentType: "application/json; charset=utf-8",
            beforeSend: function (request) {
                request.setRequestHeader(csrfHeader, token);
            },
        });

    }

    function getParentUrl(url) {
        let temp = url.toString().split("/");
        temp.pop();
        return temp.join("/");
    }

    function redirectToSignUp() {
        location.href = location.origin + "/register";
    }

    function redirectToChangePassword() {
        location.href = getParentUrl(action) + "/reset-password/" + email;
    }

    function disableSubmitButton() {
        submitButton.attr('disabled', true);
        submitButton.css('opacity', 0.6);
        submitButton.css('cursor', 'not-allowed');
    }

    function enableSubmitButton() {
        submitButton.attr('disabled', false);
        submitButton.removeAttr('tool');
        submitButton.css('opacity', 'unset');
        submitButton.css('cursor', 'pointer');
    }

    answerInput.on('input', function () {
        if ($(this).val().length === 0) {
            disableSubmitButton();
            submitButton.attr('title', $.i18n('security.answer.empty'));
        } else if ($(this).val().length >= 2 && $(this).val().length <= 25) {
            enableSubmitButton();
        } else {
            disableSubmitButton();
            submitButton.attr('title', $.i18n('registration.answers.validation', 2, 25));
        }
    });


    function showErrorMessage() {
        $('#alertIcon').removeClass("d-none");
        errorMessage.removeClass("d-none").addClass("d-inline");
    }

    function hideErrorMessage() {
        $('#alertIcon').addClass("d-none");
        errorMessage.removeClass("d-inline").addClass("d-none");
    }

})
;
