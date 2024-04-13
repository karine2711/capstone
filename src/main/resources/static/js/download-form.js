$(document).ready(async function () {
    let calendarContainer = $("#calendar");
    let downloadContainer = $(".download-container");

    await $.i18n().load({
        "en": "/i18n/en.json",
        "hy": "/i18n/hy.json",
        "ru": "/i18n/ru.json",
    });

    $.i18n().locale = $("#locale").val();
    let localeValue = $("#locale").val();
    $("#date1").datepicker({
        format: "dd-mm-yyyy",
        autoclose: true,
        todayHighlight: true,
        weekStart: 1,
        daysOfWeekDisabled: "0",
        orientation: "auto bottom",
        language: localeValue
    });

    $('#date1').on('changeDate', function (selected) {
        let minDate = new Date(selected.date.valueOf());
        $('#date2').datepicker('setStartDate', minDate);
    });

    $("#date2").datepicker({
        format: "dd-mm-yyyy",
        autoclose: true,
        todayHighlight: true,
        weekStart: 1,
        daysOfWeekDisabled: "0",
        orientation: "auto bottom",
        language: localeValue
    });


    $("input[name='cancel-btn']").prop("type", "button");
    jQuery.validator.addMethod("validFilename", function (value, element) {
        return this.optional(element) || /^(?!.{256,})(?!(aux|clock\$|con|nul|prn|com[1-9]|lpt[1-9])(?:$|\.))[^ ][ .\w-$()+=[\];#@~,&amp;']+[^. ]$/i.test(value);
    });
    jQuery.validator.addMethod("greaterThan", function (value, element, params) {
        if ($(params).val() === "") return true;
        return new Date(value.split("-").reverse().join("-")) >= new Date($(params).val().split("-").reverse().join("-"));
    });
    let date1 = $('#date1');
    let date2 = $('#date2');

    date1.change(function () {
        let startDate = new Date(date1.val().split("-").reverse().join("-"));
        let endDate = moment(startDate).add(3, 'months').format('DD-MM-YYYY');
        if (date2.val() === "") {
            date2.val(endDate);
            date2.datepicker("update", endDate);
        }
        let date1Message = $("#error-message-date1");
        if (date1Message.parent().css("display") === "block") date1Message.parent().css("display", "none");
    });

    date2.change(function () {
        let date2Message = $("#error-message-date2");
        if (date2Message.parent().css("display") === "block") date2Message.parent().css("display", "none");
    });
    document.querySelectorAll(".form-control").forEach(function (element) {
        let parent = element.parentElement;
        let invalidFeedback = document.createElement('DIV');
        invalidFeedback.classList.add('custom-invalid-feedback');
        if (element.classList.contains('currentDate')) invalidFeedback.classList.add('margin-left-25');
        let span = document.createElement('SPAN');
        span.classList.add('error-message');
        let inputName = element.getAttribute('name');
        span.id = `error-message-${inputName}`;
        invalidFeedback.appendChild(span);
        parent.appendChild(invalidFeedback);
    });

    $(".downloadCancel").click(function () {
        setDefault();
    });

    $(".close").click(function () {
        setDefault();
    });

    const setDefault = () => {
        calendarContainer.css("opacity", "unset");
        calendarContainer.css("background-color", "unset");
        downloadContainer.css("display", "none");
        calendarContainer.css("pointer-events", "unset");
        date1.val(null);
        date2.val(null);
        date1.datepicker("update", null);
        date2.datepicker("update", null);
        date2.datepicker("setStartDate", null);
        $('#selected-download-format').val('csv');
        let errorContainers = document.querySelectorAll(".custom-invalid-feedback");
        for (let i = 0; i < errorContainers.length; i++) {
            errorContainers[i].style.display = "none";
        }
    };

    $('#downloadForm').validate({
        rules: {
            date1: {
                required: true,
            },
            date2: {
                greaterThan: "#date1"
            },
            currentDate: {
                validFilename: true,
                required: true
            }
        },
        messages: {
            date1: {
                required: $.i18n("downloadForm.startTime.empty"),
            },
            date2: {
                greaterThan: $.i18n("downloadForm.endTime.mustBeGreater"),
            },
            currentDate: {
                validFilename: $.i18n("downloadForm.fileName.validation"),
                required: $.i18n("downloadForm.fileName.empty")
            }
        },
        errorPlacement: function (label, element) {
            element.removeClass('error');
            element.removeClass('valid');
            label.removeClass('error');
            let errorMessage = label.text();
            console.log(errorMessage);
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
        success: function (a, b) {
        }
    });

    addErrorIcon()


    const downloadBtn = $('#add-activity-btn');
    const cancelBtn = $('#cancel-btn');
    downloadBtn.focus();

    downloadBtn.hover(function () {
            swapClass("#cancel-btn", "activity-main-button", "#add-activity-btn", "activity-whiteButton");
        },
    );
    downloadBtn.focus(function () {
        swapClass("#cancel-btn", "activity-main-button", "#add-activity-btn", "activity-whiteButton");
    });
    cancelBtn.hover(function () {
        swapClass("#add-activity-btn", "activity-main-button", "#cancel-btn", "activity-whiteButton");
    }, function () {
        swapClass("#cancel-btn", "activity-main-button", "#add-activity-btn", "activity-whiteButton");
    });
    cancelBtn.focus(function () {
        swapClass("#add-activity-btn", "activity-main-button", "#cancel-btn", "activity-whiteButton");
    });

    function swapClass(selector1, selector1Class, selector2, selector2Class) {
        $(selector1).removeClass(selector1Class).addClass(selector2Class);
        $(selector2).removeClass(selector2Class).addClass(selector1Class);
    }

});
