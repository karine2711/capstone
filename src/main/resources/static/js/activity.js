$(document).ready(async function () {
    let titleEn = "";
    let titleRu = "";
    let descEn = "";
    let descRu = "";
    let mainContainer = $(".activity-rectangle");
    let hiddenDiv = $("#hidden-div");
    let spinner = $(".lds-dual-ring");

    $.i18n().locale = $("#locale").val();

    await $.i18n().load({
        "en": "/i18n/en.json",
        "hy": "/i18n/hy.json",
        "ru": "/i18n/ru.json",
    });

    const action = $('#activity-form').attr("action");
    const csrfHeader = "X-CSRF-TOKEN";
    const token = $('input[name^="_csrf"]').val();

    const GROUP_SIZE_MAX_VALUE = 50

    const disableBackground = () => {
        mainContainer.css("opacity", 0.6);
        mainContainer.css("background-color", "#F0F2FB");
        mainContainer.css("pointer-events", "none");
    };

    $("#date").change(function () {
        changeMandatoryInput()
    });

    $("#event-type").change(function () {
        changeMandatoryInput();
    })

    $("#group-size").change(function () {
        changeMandatoryInput()
    })

    function changeMandatoryInput() {
        $('#start-time').attr('disabled', true).empty();
        $('#start-time').append('<option selected>' + $.i18n("start.time") + "</option>");
        $('#end-time').attr('disabled', true);
        let dateInput = $('input[name=date]').val();
        let date = dateInput.split("-").reverse().join("-");
        let selectedTypeId = $("#event-type").val();
        let groupSize = $("#group-size").val();
        $('#end-time').val('');
        if (date !== '' && selectedTypeId !== '' && ((groupSize !== '' && groupSize <= GROUP_SIZE_MAX_VALUE) || selectedTypeId == 7)) {
            $('#group-size').validate();
            $('#date').validate();
            let allValid = $('#group-size').valid() && $('#date').valid();
            if (allValid)
                fetchFreeTimes(date, selectedTypeId, groupSize);
        }
    }

    function fetchFreeTimes(date, selectedTypeId, groupSize) {
        $("#add-activity-btn").prop("disabled", true);
        $("#add-activity-btn").attr(
            "title", $.i18n("fetching.times")
        );
        if ($('#admin').length > 0 && selectedTypeId == 7) {
            addStartTimes(selectedTypeId)
            let eventTypeId = parseInt($("#event-type").val())
            addEndTimeWhenSelected(getClassNameByTypeId(eventTypeId))
            $('#end-time').attr('disabled', false);
            $("#add-activity-btn").attr('disabled', false);
        } else {

            $.ajax({
                type: "get",
                url: action + "/free-times" + '/' + date + '/' + selectedTypeId + "/" + groupSize,
                beforeSend: function (request) {
                    request.setRequestHeader(csrfHeader, token);
                },
                success: function (data) {
                    fetchStartTimesForNotEvent(data, selectedTypeId)
                }

            })

        }
    }

    $("#start-time").change(function () {
        let selectedTypeId = parseInt($("#event-type").val());
        addEndTimeWhenSelected(getClassNameByTypeId(selectedTypeId))
    });

    $("#clear-input").on("click", function (e) {
        $(this).remove();
    });

    $('#event-type').change(function () {
        if ($(this).children("option:selected").val() == 7) {
            $('#description-section').removeClass("d-none");
            $('.activity-input').addClass("d-none");
            $(".custom-invalid-feedback").hide();
        } else {
            $('#description-section').addClass("d-none");
            $('.activity-input').removeClass("d-none");
        }
    });

    if ($('#event-type').children("option:selected").val() == 7) {
        showDescriptionSection();
    }
    setOptionalLangView();


    function showDescriptionSection() {
        $('#description-section').removeClass("d-none");
        $('.activity-input').addClass("d-none");
        $(".custom-invalid-feedback").hide();
    }


    function showLang(buttonSelector, sectionSelector) {
        $(buttonSelector).addClass('button-shadow');
        $(sectionSelector).removeClass("d-none");
    }

    function hideLang(buttonSelector, sectionSelector) {
        $(buttonSelector).removeClass('button-shadow');
        $(sectionSelector).addClass("d-none");
    }

    function setOptionalLangView() {
        $('.btn-ru').click(function () {
            showLang('.btn-ru', '#ru-section')
            hideLang('.btn-en', '#en-section')

            refreshVariables();
        });

        $('.btn-en').click(function () {

            showLang('.btn-en', '#en-section')
            hideLang('.btn-ru', '#ru-section')

            refreshVariables();
        });

        showLang('.btn-en', '#en-section')
        hideLang('.btn-ru', '#ru-section')

        refreshVariables();
    }


    function refreshVariables() {
        titleEn = $('#title-en').val();
        titleRu = $('#title-ru').val();
        descEn = $('#description-en').val();
        descRu = $('#description-ru').val();
    }

    addDatePicker()
    addValidationHtml()


    jQuery.validator.addMethod("onlyNumbers", function (value, element, params) {
        return /^\d*$/.test(value);
    }, $.validator.messages.onlyNumbers);

    jQuery.validator.addMethod('notSunday', function (value, element, param) {
        return new Date(value.split("-").reverse().join("-")).getDay() !== 0;
    }, "");

    jQuery.validator.addMethod('validTime', function (value) {
        let eventType = getClassNameByTypeId($("#event-type").val());
        let duration = getDuration(eventType);
        let hour = parseInt(value.substr(0, 2));
        let minute=parseInt(value.substr(value.length-2,2));
        return hour >= 10 && hour <= 16 && minute>=0;
    }, "");


    $('#activity-form').validate({
        rules: {
            eventType: {
                required: true,
            },
            date: {
                required: true,
                notSunday: true
            },
            time: {
                required: true,
                notEqualTo: $.i18n("start.time"),
                validTime: true
            },
            school: {
                customRequired: $("#school").text(),
                maxlength: 65,
            },
            group: {
                customRequired: $("#class").text(),
                maxlength: 10,
            },
            groupSize: {
                customRequired: $("#group-size").text(),
                onlyNumbers: true,
                min: 1,
                max: GROUP_SIZE_MAX_VALUE,
            },
            title_AM: {
                customRequired: $("#title-am").text(),
            },
            description_AM: {
                customRequired: $("#description-am").text(),
                maxlength: 500,
            }
        },
        messages: {
            group: {
                customRequired: $.i18n("event.activityClass.empty"),
                maxlength: $.i18n("event.class.maxLength"),
            },
            date: {
                required: $.i18n("event.date.empty"),
                notSunday: $.i18n("exclude.sunday")
            },
            time: {
                required: $.i18n("event.startTime.empty"),
                notEqualTo: $.i18n("event.startTime.empty"),
                validTime: $.i18n("event.startTime.valid")
            },
            school: {
                customRequired: $.i18n("event.school.empty"),
                maxlength: $.i18n("event.school.maxLength"),
            },
            eventType: {
                required: $.i18n("event.eventType.empty"),
            },
            groupSize: {
                customRequired: $.i18n("groupSize.empty"),
                onlyNumbers: $.i18n("groupSize.only.numbers"),
                min: $.i18n("groupSize.min.value"),
                max: $.i18n("groupSize.max.value"),
            },
            title_AM: {
                customRequired: $.i18n("title.am.required")
            },
            description_AM: {
                customRequired: $.i18n("desc.am.required")
            }
        },
        errorPlacement: function (label, element) {
            element.removeClass('error');
            element.removeClass('valid');
            label.removeClass('error');
            let errorMessage = label.text();

            let hasPreviousErrors = element.parent().find('.custom-invalid-feedback').css('display') !== 'none';
            let customInvalidFeedback = element.parent().find('.custom-invalid-feedback');

            if (hasPreviousErrors) {
                if (errorMessage) {
                    customInvalidFeedback.find('.error-message').text(errorMessage);
                    customInvalidFeedback.show()
                } else {
                    customInvalidFeedback.hide();
                }
            } else if (errorMessage) {
                customInvalidFeedback.find('.error-message').text(errorMessage);
                customInvalidFeedback.show();
            }
        },
        submitHandler: function (form) {
            if ($('#start-time').val() != null) {
                submitActivityForm(form)
            } else {
                let invalidFeedback = $('#date').parent().find('.custom-invalid-feedback');
                showNoTimeAvailable(invalidFeedback)

            }

        },
        success: function (a, b) {
        }

    })

    addErrorIcon()

    function submitActivityForm(form) {
        if ($("#group-size").val() > 35) {
            let popupMessage = "<div class='position-fixed container popup-message'>\n" +
                "    <button type=\"button\" class=\"close\" aria-label=\"CZlose\">\n" +
                "        <span aria-hidden=\"true\">&times;</span>\n" +
                "    </button>" +
                "    <div class='text-center'>" +
                "        <i class=\"fas fa-info-circle fa-2x\" style='color: #35B8EC; margin: 15px auto;'></i>\n" +
                "        <h6>" + $.i18n("event.popup.message") + "</h6>\n" +
                "    </div>" +
                "    <div class='row justify-content-center' style='margin: 20px auto;'>\n" +
                "        <div class='col-5'>\n" +
                "            <button class='btn activity-whiteButton' id='cancel-btn'>\n" +
                $.i18n("event.popup.cancel") + "\n" +
                "            </button>\n" +
                "        </div>\n" +
                "        <div class='col-5'>\n" +
                "            <button class='btn activity-main-button' id='confirm-btn'>\n" +
                $.i18n("event.popup.confirm") + "\n" +
                "            </button>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</div>";

            hiddenDiv.append(popupMessage);
            hiddenDiv.css("display", "block");
            hiddenDiv.css("margin-left", "26%");
            mainContainer.css("opacity", 0.6);
            mainContainer.css("background-color", "#F0F2FB");
            mainContainer.css("pointer-events", "none");

            $(' #confirm-btn').on('click', function () {
                doSubmit(form);
            });

            $(' #cancel-btn').on('click', function () {
                enableBackground();
            });

            $('.close').on('click', function () {
                enableBackground();
            });
        } else {
            doSubmit(form);
        }
    }

    function doSubmit(form) {

        let formData = new FormData();
        let data = $("#activity-form").serializeArray();
        let dataJson = {};
        data.forEach((el) => {
            if (el.name === 'date') {
                el.value = $('input[name=date]').val().split("-").reverse().join("-");
            }
            dataJson[el.name] = el.value;
        });
        let jsonArray = [];
        jsonArray.push(JSON.stringify(dataJson));

        formData.append('event', new Blob(jsonArray, {
            type: "application/json"
        }));
        const token = $('input[name^="_csrf"]').val();
        $.ajax({
            type: "post",
            url: location.href,
            contentType: false,
            processData: false,
            data: formData,
            beforeSend: function (request) {
                spinner.show();
                disableBackground();
                request.setRequestHeader(csrfHeader, token);
            },
            success: function () {
                location.href = location.origin + "/homepage"
            },
            error: function (xhr) {
                console.log(xhr);
                location.reload();
            }

        })
    }

    function enableBackground() {
        $(".popup-message").remove();
        hiddenDiv.css("display", "none");
        mainContainer.css("opacity", "unset");
        mainContainer.css("background-color", "unset");
        mainContainer.css("pointer-events", "unset");
    }
});
