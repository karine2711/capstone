var filters = new Set();
const csrfHeader = "X-CSRF-TOKEN";
let source = new Map();
let myActivities;
let calendar;
var localedMore;
var localedLess;

function filterButtonListener() {
    $('.filter-button').click(function () {
        filters.has(this.id) ? filters.delete(this.id) : filters.add(this.id);
        $(this).toggleClass(this.id);
        $(this).toggleClass('text-white');
        $(this).children('.circle').toggleClass(this.id);

        filterActivities();
    });
}

$(document).ready(async function () {
    let form = $("#downloadForm");
    let select = $('select');
    let currentOption = select.find('option').filter(':selected').text();
    let calendarContainer = $("#calendar");
    let downloadContainer = $(".download-container");

    let today = new Date();
    let dd = String(today.getDate()).padStart(2, '0');
    let mm = String(today.getMonth() + 1).padStart(2, '0');
    let yyyy = today.getFullYear();
    today = dd + '_' + mm + '_' + yyyy;

    await $.i18n().load({
        "en": "/i18n/en.json",
        "hy": "/i18n/hy.json",
        "ru": "/i18n/ru.json",
    });


    let localeValue = $("#locale").val();
    $.i18n().locale = localeValue;
    switch (localeValue) {
        case "hy" :
            calendar.setOption("locale", "arm");
            break;
        default:
            calendar.setOption("locale", localeValue)
    }

    $('#date').datepicker({
        language: localeValue
    });

    $('#dateScroller').datepicker({
        language: localeValue
    });

    $(window).on('click', hideContextMenu);
    $(window).on('click', removePopoverHandler);
    if (document.referrer.includes('/register')) {
        $('.congrats-popover').removeClass('invisible');
        setTimeout(function () {
            $('.congrats-popover').fadeOut(1000);
        }, 3000)
    }
    //Draw filter buttons
    // function drawFilterButtons() {
    $.ajax({
        type: "get",
        url: location.origin + "/event" + "/getEventTypes",
        success: function (data) {
            let filterButtons = " <div style='margin: 20px'>";
            data = new Map(Object.entries(data));
            for (const [key, value] of data.entries()) {
                let button = "<button class='filter-button' id='" + getClassName(key) + "'>" +
                    "                <span class='circle " + getClassName(key) + "'>" +
                    "                </span>" +
                    value +
                    "            </button>";
                filterButtons = filterButtons + button;

            }
            filterButtons = filterButtons + "<button class='btn fc-button-primary ' id='calendarDownload'>" + $.i18n("download.button") + "</button>";
            filterButtons = filterButtons + "</div>";
            $("#calendar>div:first-child").after(filterButtons);
        }


    }).then(function () {
        filterButtonListener()
        $('#calendarDownload').on("click", (function () {
            $('#currentDate').val(today);
            calendarContainer.css("opacity", 0.6);
            calendarContainer.css("background-color", "#F0F2FB");
            downloadContainer.css("display", "block");
            downloadContainer.css("margin-left", "26%");
            calendarContainer.css("pointer-events", "none");
        }));
    })
    ;

    filterButtonListener();


    $('button').on('click', function () {
        removePopover();
    });

    select.on('change', function () {
        currentOption = $(this).find('option').filter(':selected').text();
    });

    $('.download-button').on('click', (function (e) {
        e.preventDefault();
        let currentAction = 'download-events';
        let urlAttachment = currentOption === 'CSV (Comma delimited)' ? 'csv' : 'pdf';
        let isFromMyActivities = $('#my-activities').hasClass("select-tab-border") ? "true" : "false";

        if (filters.size !== 0) {
            let eventFilters = Array.from(filters).join(",");
            $("<input type='hidden'/>")
                .attr("name", "eventFilters")
                .attr("id", "eventFilters")
                .attr("value", eventFilters)
                .prependTo(form);
            console.log(eventFilters);
        }
        $("<input type='hidden'/>")
            .attr("name", "isFromMyActivities")
            .attr("id", "isFromMyActivities")
            .attr("value", isFromMyActivities)
            .prependTo(form);
        form.attr('action', currentAction + '/' + urlAttachment).submit();
    }));

})
;

function makePopover(elem, event, showMoreText, showLessText) {
    localedMore = showMoreText;
    localedLess = showLessText;
    $(elem).find('.show-more').popover({
        placement: 'auto',
        content: function () {
            $(".popover").remove();
            let timeElement = $(elem).find('.fc-time');
            let timeText = timeElement.text().replace(/am/gi, " am");
            timeText = timeText.replace(/pm/gi, " pm");
            let filterCircle = $('<span class="event-filter-circle"></span>');
            let filterButton = $(`#${event.extendedProps.type}`);
            let filterColor = $(elem).find('.circle').css('background-color');
            filterCircle.css('background-color', filterColor);
            let rgb = filterColor.match(/(\d+\.*\d+)/gm);
            rgb.splice(3, 1);
            // let errorEventColor = `rgb(${rgb.join()})`;
            // let displayValue = event.extendedProps.isActive ? 'block' : 'none';
            // let errorMessage = $.i18n("event.booking.error.message");
            return $(
                // `<div class='event-error' style="background-color: ${errorEventColor}; display: ${displayValue}">` +
                // `<div class='event-error-message'>${errorMessage}</div>` +
                // `</div>` +
                `<div class='popover-container container' style='border-left-color: ${filterColor}'>` +
                `<div class='event-filter row'>${filterCircle[0].outerHTML} ${filterButton.text()}</div>` +
                `<div class='event-header row'>${event.title}</div>` +
                `<div class='event-hours row'>${timeText}</div>` +
                `<div class='event-content row'>${event.extendedProps.desc}</div>` +
                "</div>"
            )
        },
        trigger: "manual",
        html: true,
        animation: false
    }).on("click", function () {
        showMoreListener.call(this);
        $('.popover-container').parents(".popover").attr('style', 'width: 37% !important; border:none;  box-shadow: 0 23px 37px 0 rgba(0, 96, 167, 0.35);');
        $(".group-sign").css("color", "");
        $(".show-more").css("color", "#A1A3A8");
        $(this).css("color", $(".event").css("background-color"));

    });
}


function groupsPopover(elem, myGroupSize, groupSizes, user) {
    let filterColor = 'rgba(0, 96, 167, 0.35)';
    $(elem).find('.group-sign').popover({
        placement: 'auto',
        content: function () {
            $(".popover").remove();
            filterColor = $(elem).find('.circle').css('border-color');
            if (filterColor == undefined) filterColor = $(elem).css('background-color')
            let container = `<div class='group-popover container' style='border-left-color: ${filterColor}'>`;
            let boxShadow = 'none';
            if (user == $("#fc-username").text())
                boxShadow = '0 0 5px ' + filterColor;
            container = container + "<div class='group-circle' style='background-color:" + filterColor + "; box-shadow:" + boxShadow + "'>" + myGroupSize + "</div>"
            groupSizes.forEach(g => {
                container = container + "<div class='group-circle' style='background-color:" + filterColor + "'>" + g + "</div>"
            })
            container = container + "</div>";
            return $(
                container
            )
        },
        trigger: "manual",
        html: true,
        animation: false
    }).on("click", function () {
        $(this).popover("show");
        $('.group-popover').parents(".popover").attr('style', 'width: auto !important; border:none; box-shadow: 0 0 10px 0 ' + filterColor);
        $(".group-sign").css("color", "");
        $(this).css("color", filterColor);
    });

}


function addEventId(event, elem) {

    let id = "<span class='d-none id'>".concat(event.id).concat("</span>");
    $(elem).append(id);

}

function addEventDesc(event, elem) {
    $(elem).find('.fc-title').before('<i class="fa fa-ellipsis-h show-more" aria-hidden="true" style="font-size: 20px; top: -2px; color: #A1A3A8;"></i>');
    if (event.extendedProps.desc !== null && event.extendedProps.desc.length !== 0) {
        let hiddenDescription = '<div class="d-none hidden-description">' + event.extendedProps.desc + '</div>';
        $(elem).append(hiddenDescription)
    }
}

function addGroupSign(event, elem) {
    $(elem).find('.fc-title').before('<span class="group-sign" style="">' + '<i class="fa fa-users group-icon" aria-hidden="true"></i>' + '</span>');

}

function addCircle(view, event, element) {
    let circleType = getCircleType(view, event);
    let circle = "<span  class='circle " + circleType + "'></span>";
    $(element).children(":not('.id')").prepend(circle);
}

function getCircleType(view, event) {
    let circleType = getClassName(event.extendedProps.type);
    let activeEvent = source.get(parseInt(event.id));
    if (event.start < new Date())
        circleType = circleType + ' gray';
    if (activeEvent.eventState === 'PRE_BOOKED') {
        circleType = circleType.concat('-prebooked')
    }
    return circleType;
}


function showMoreListener(showMoreText, showLessText) {
    if ($(this).text() === showMoreText) {
        $(this).text(showLessText);
        $(this).popover("show");
    } else if ($(this).text() === showLessText) {
        $(this).text(showMoreText);
        $(this).popover("hide");
    } else {
        $(this).popover("show");
    }
}

function getClassName(eventType) {
    eventType = eventType.toLowerCase();
    eventType = eventType.replace(" school", "");
    eventType = eventType.replace("_school", "");
    return eventType;
}

function getClassNameByTypeId(eventTypeId) {
    let eventType = "";
    switch (parseInt(eventTypeId)) {
        case 1:
            eventType = "preschool";
            break;
        case 2:
            eventType = "elementary";
            break;
        case 3:
            eventType = "middle";
            break;
        case 4:
            eventType = "high";
            break;
        case 5:
            eventType = "students";
            break;
        case 6:
            eventType = "individuals";
            break;
        case 7:
            eventType = "event";
            break;
        default:
            eventType = "event"
    }
    return eventType;
}

function positionPopover(activityTitle) {
    let dt = $('.fc-more-popover .fc-header .fc-title').html();
    let headerDiv = $("<div class='more-title'>" + activityTitle + "</div>");
    $('.fc-more-popover .more-title').remove()
    $('.fc-more-popover').prepend(headerDiv);
    if (new Date(dt).getDay() >= 3) {
        $('.fc-more-popover').css("transform", "translate(-100%, 0px)");
    } else if (new Date(dt).getDay() === 0) {
        $('.fc-more-popover').css("transform", "translate(-67%, 0px)");
    } else {
        $('.fc-more-popover').css("transform", `translate(${$('.fc-day').css('width')}, 0px)`);
    }
}


function addMore(showMoreText, showLessText, activityTitle) {
    setTimeout(function () {
        onAddMoreClick(showMoreText, showLessText, activityTitle);
    }, 0);
}

function formatAMPM(date) {
    let hours = date.getHours();
    let minutes = date.getMinutes();
    minutes = minutes < 10 ? '0' + minutes : minutes;
    return hours + ':' + minutes;
}

function addContextMenu(el, rescheduleText, deleteText, confirmText, updateText) {
    setTimeout(function () {
        $(el)
            .on('click', function (e) {
                contextMenuHandler(e, $(this), rescheduleText, deleteText, confirmText, updateText)
            })
            .on('contextmenu', function (e) {
                e.preventDefault();
                contextMenuHandler(e, $(this), rescheduleText, deleteText, confirmText, updateText)
            })
    }, 0)
}

function getDuration(eventType) {
    if (eventType === 'preschool' || eventType === 'elementary' || eventType === 'middle') {
        return 45;
    } else if (eventType === 'high' || eventType === 'students' || eventType === 'individuals') {
        return 60;
    } else if (eventType === 'event') {
        return 120;
    }
}

function addStartTimes(eventTypeId) {
    let dateInput = $('input[name=date]').val();
    let date = new Date(dateInput.split("-").reverse().join("-"));
    $('#start-time').empty();

    let eventType = getClassNameByTypeId(eventTypeId);
    // let startTime = 10;
    let duration = getDuration(eventType);
    let lastPossibleHour = Math.floor((16 * 60 - duration) / 60)
    let lastPossibleMinutes = (Math.abs(60 - duration)) % 60;
    let startHour = 10;
    let startMinute = 0;
    let now = new Date();
    let options = {
        timeZone: 'Asia/Yerevan',
        year: 'numeric',
        month: 'numeric',
        day: 'numeric',
        hour: 'numeric',
        minute: 'numeric',
        second: 'numeric',
    };
    let formatter = new Intl.DateTimeFormat([], options);
    let amDateTime = new Date(formatter.format(new Date()));
    let amHour = amDateTime.getHours();
    let sameDate = amDateTime.getFullYear() === date.getFullYear() &&
        amDateTime.getMonth() === date.getMonth() &&
        amDateTime.getDate() === date.getDate();

    if (sameDate && startHour >= 9) {
        startHour = (amHour + 1) % 24;
        startMinute = (Math.floor(now.getMinutes() / 15) * 15 + 15);
        if (startMinute >= 60) {
            startHour = startHour + 1;
            startMinute = startMinute % 60;
        }
    }
    if (startHour < 10) {
        startHour = 10;
        startMinute = 0;
    }
    for (let hour = startHour; hour <= lastPossibleHour; hour++) {
        for (let minute = startMinute; minute < 60; minute = minute + 15) {
            if (hour == lastPossibleHour && minute > lastPossibleMinutes) {
                break;
            }
            let option = "<option>" + hour + ":";
            option = minute === 0 ? option + "00" : option + minute;
            option = option.concat("</option>");
            $('#start-time').append(option);

        }
    }
    let invalidFeedback = $('#date').parent().find('.custom-invalid-feedback');
    if ($('#start-time').val() == null) {
        showNoTimeAvailable(invalidFeedback)
    } else {
        hideInvalidFeedback(invalidFeedback)
    }

}

function showNoTimeAvailable(invalidFeedback) {
    invalidFeedback.show();
    invalidFeedback.find('.error-message').text($.i18n("event.date.full"));

    $('#start-time').attr('disabled', true).empty();
    $('#end-time').val('');
    $('#end-time').attr('disabled', true);
}

function hideInvalidFeedback(invalidFeedback) {
    invalidFeedback.hide()
    $('#start-time').attr('disabled', false);
    $('#end-time').val('');
    $('#end-time').attr('disabled', false);
}

function fetchStartTimesForNotEvent(data, selectedTypeId) {
    let invalidFeedback = $('#date').parent().find('.custom-invalid-feedback');
    if (!data || data.length === 0) {
        showNoTimeAvailable(invalidFeedback);
        ($("#add-activity-btn").prop("disabled", true))
        $("#add-activity-btn").removeAttr("title");

    } else {
        hideInvalidFeedback(invalidFeedback);
        $('#start-time').empty()
        let element = '';
        data.forEach(function (d) {
            element = "<option>" + d.substring(0, 5) + "</option>";
            $('#start-time').append(element);
        });
        $("#add-activity-btn").prop("disabled", false);
        $("#add-activity-btn").removeAttr("title");
        addEndTimeWhenSelected(getClassNameByTypeId(selectedTypeId))
    }
}

function fetchFreeTimes(date, selectedTypeId, id) {
    if ($('#admin').length > 0 && selectedTypeId == 7) {
        addStartTimes(selectedTypeId);
        addEndTimeWhenSelected(getClassNameByTypeId(selectedTypeId))
        return;
    }
    $('#reschedule-btn').prop('disabled', true)

    $.ajax({
            type: "get",
            url: location.origin + "/event" + "/free-times" + '/' + date + '/' + id,
            success: function (data) {


                fetchStartTimesForNotEvent(data, selectedTypeId);

            }
        }
    );
}


function addEndTimeWhenSelected(eventType) {
    let startTime = $("#start-time");
    if (startTime.val() == null || startTime.val() == '' || startTime.val() == $.i18n("start.time")) {
        $('#reschedule-btn').prop('disabled', true)
        // $("#end-time").val($.i18n("end.time"));
        return;
    }
    $('#reschedule-btn').prop('disabled', false)

    let endTime = new Date();

    let hours = parseInt(startTime.val().substring(0, 2));
    let minutes = parseInt(startTime.val().substring(3, 5));

    endTime.setHours(hours);


    let plusMinutes = getDuration(eventType)

    endTime.setMinutes(minutes + plusMinutes);

    let endTimeString = endTime.toLocaleTimeString(['ru-RU'], {hour: '2-digit', minute: '2-digit'});

    $("#end-time").val(endTimeString);
}

function onDateChange(eventTypeId, id) {
    let dateInput = $('input[name=date]').val();
    let date = dateInput.split("-").reverse().join("-");
    fetchFreeTimes(date, eventTypeId, id);
}


function drawReschedulePopup(id, bgContainer, eSource) {
    // if (id == null) id = sessionStorage.getItem("id");
    let eventSource = eSource || source;
    let event = eventSource.get(parseInt(id));
    let type = getClassName(event.type);
    let start = (new Date(event.start));
    let end = (new Date(event.end));
    let title = event.title;
    let eventType = event.type;
    let eventTypeId = event.eventTypeId;
    let localeType = event.typeByLocale;

    let reschedulePopup = "<div class='position-fixed container reschedule-popup'>\n" +
        "<button type=\"button\" class=\"close\" aria-label=\"CZlose\">\n" +
        "  <span aria-hidden=\"true\">&times;</span>\n" +
        "</button>" +
        "    <h6>" + $.i18n('reschedule.title') + "</h6>\n" +
        "    <div class='main-content'>\n" +
        "        <div class='row justify-content-end align-items-center event-type'><span class='circle " + type + "'></span>  " + capitalizeFirstLetter(localeType) + "</div>\n" +
        "        <div class='container row'><h6 class='event-title'>" + title + "</h6></div>\n" +
        "        <div class='row container'>" + formatAMPM(start) + "-" + formatAMPM(end) + "</div>\n" +
        "        <div style='margin: 20px 0;'>" +
        // "<input id='date' type='date' class='form-control'" +
        // "                                             name='date' value='" + formatDate(start) + "' min=" + formatDate(new Date()) + ">" +
        "<div class='position-relative'>" +
        "<input type='text' id='date' class='form-control datepicker bg-white' readonly='true' autocomplete='off' placeholder='Дата' aria-required='true' name='date' value='" + formatDateReverse(start) + "'>       " +
        "<span style=\"font-size: 15px;\n" +
        "position: absolute;\n" +
        "pointer-events: none;\n" +
        "color: dimgray;\n" +
        "top: 10px;" +
        "z-index: 2000;" +
        "right: 10%;\" class=\"glyphicon glyphicon-calendar\">\n" +
        "</span>" +
        "</div>" +
        " </div>\n" +
        "        <div class='row' style='margin: 20px 0;'>\n" +
        "            <div class='col-5 p-0'>\n" +
        "                <select name='startTime' id='start-time' class='select form-control required-input'>\n" +
        "                </select>\n" +
        "            </div>\n" +
        "\n" +
        "            <div class='col-2'>\n" +
        "                <hr class='activity-line'>\n" +
        "            </div>\n" +
        "            <div class='col-5 p-0'>\n" +
        "                <input readonly=\"\" name='endTime' id='end-time' type='text' class='form-control required-input bg-white'\n" +
        "                       disabled=\"\" value=" + $.i18n('end.time') + ">\n" +
        "            </div>\n" +
        "        </div>\n" +
        "        <div  style='margin-bottom: 20px; width: fit-content'>\n";

    let cancelBtn = "<button class='btn cancel ' id='cancel-reschedule'>" + $.i18n("delete.activity.cancel") + "</button>";
    let confirmButton = "<button class='btn confirm ml-4' id='reschedule-btn'>" + $.i18n("reschedule.activity.confirm") + "</button>";


    reschedulePopup = reschedulePopup + cancelBtn;
    reschedulePopup = reschedulePopup + confirmButton;
    reschedulePopup = reschedulePopup + "</div></div></div>";
    let minDate = new Date();
    $("#hidden-form").append(reschedulePopup);
    setDisabled(bgContainer);

    $(".confirm").hover(function () {
        swapClass("#cancel-reschedule", "confirm", "#reschedule-btn", "cancel");
    }, function () {
    });
    $(".confirm").focus(function () {
        swapClass("#cancel-reschedule", "confirm", "#reschedule-btn", "cancel");
    });
    $(".cancel").hover(function () {
        swapClass("#reschedule-btn", "confirm", "#cancel-reschedule", "cancel");
    }, function () {
        swapClass("#reschedule-btn", "cancel", "#cancel-reschedule", "confirm");
    });
    $(".cancel").focus(function () {
        swapClass("#reschedule-btn", "confirm", "#cancel-reschedule", "cancel");
    });
    addDatePicker()

    let invalidFeedback = $("<div class='custom-invalid-feedback'><span class='error-message'></span></div>");
    $('#date').parent().append(invalidFeedback);


    fetchFreeTimes(formatDate(start), eventTypeId, id);

    let color = $(".reschedule-popup .main-content .circle").css("background-color");
    $(".reschedule-popup .main-content").css({"border-color": color})

    $(".close").click(function () {
        $(".reschedule-popup").remove();
        setDefault(bgContainer);
    })
    $("#cancel-reschedule").click(function () {
        $(".reschedule-popup").remove();
        setDefault(bgContainer);
    })

    $("#date").change(function () {
        onDateChange(eventTypeId, id)
    });

    $("#start-time").change(function () {
        addEndTimeWhenSelected(eventType)
    });

    $('#reschedule-btn').click(function (e) {
        e.preventDefault();
        let dateInput = $('input[name=date]').val().toString().padStart(2, "0");
        let date = dateInput.split("-").reverse().join("-");
        let time = $('#start-time').val();
        let eventObj = {
            "date": date,
            "eventId": event.id,
            "time": time
        }
        const token = $('input[name^="_csrf"]').val();

        $.ajax({
            type: "post",
            url: location.origin + "/event" + "/reschedule",
            data: JSON.stringify(eventObj),
            contentType: "application/json; charset=utf-8",
            beforeSend: function (request) {
                request.setRequestHeader(csrfHeader, token);
            },
            success: function () {
                $(".reschedule-popup").remove();
                setDefault(bgContainer);
                if (calendar) {
                    calendar.refetchEvents();
                    calendar.rerenderEvents();
                } else {
                    location.reload();
                }
            },
            error: function (xhr) {
                location.reload();
            }
        })
    })
}


function contextMenuHandler(event, object, rescheduleText, deleteText, confirmText, updateText) {

    let id = object.find(".id").html();

    let actualEvent = source.get(parseInt(id));
    console.log(actualEvent);

    let left = event.pageX + "px";
    let top = event.pageY + "px";
    $(".context-menu").remove();
    if ($(".context-menu").length == 0) {
        let contextMenu = "<div class=\"context-menu-wrapper\">\n" +
            "        <ul class=\"context-menu\">\n" +
            "            <li id='reschedule-btn'>\n" +
            "                <a href=\"#\" class=\"\">\n" +
            "                    <i class=\"fa fa-file\" aria-hidden=\"true\"></i>\n" +
            rescheduleText +
            "                </a>\n" +
            "            </li>\n" +
            "            <li id='delete-button'>\n" +
            "                <a href=\"#\" class=\"\">\n" +
            "                    <i class=\"fa fa-trash\" aria-hidden=\"true\"></i>\n" +
            deleteText +
            "                </a>\n" +
            "            </li>\n";
        let remainsLessThan48Hours = new Date(actualEvent.start) - new Date() < (1000 * 60 * 60 * 48);
        if (remainsLessThan48Hours && confirmText != null && actualEvent.eventState==="BOOKED") {
            contextMenu = contextMenu + "            <li id='confirm-button'>\n" +
                "                <a href=\"#\" class=\"\">\n" +
                "                    <i class=\"fa fa-plus\" aria-hidden=\"true\"></i>\n" +
                confirmText +
                "                </a>\n" +
                "            </li>\n";
        }


        if ($("#admin").length > 0 && source.get(parseInt(id)).eventTypeId == 7 && updateText != null) {
            contextMenu = contextMenu + "            <li>\n" +
                "                <a href='" + location.origin + "/event/" + id + "' class=\"\">\n" +
                "                <i class=\"fa fa-edit\"></i> \n" +
                updateText +
                "                </a>\n" +
                "            </li>\n";
        }
        contextMenu = contextMenu + "</ul></div>";
        $("body").append(contextMenu);
    }

    $("#reschedule-btn").on('click', function () {
        drawReschedulePopup(id, $("#calendar"));
    })
    $(' #delete-button').on('click', function () {
        sendDeleteRequest(id, $("#calendar"));
    })

    $(".context-menu-wrapper").css({"left": left, "top": top});
    $(".context-menu").show();

    $(' #confirm-button').on('click', function () {
        if (!actualEvent.confirmed) {
            confirmEvent(id);
        }
    });


}

function confirmEvent(id) {

    const token = $('input[name^="_csrf"]').val();
    $.ajax({
        method: 'PUT',
        url: location.origin + "/event/confirm/" + id,
        beforeSend: function (request) {
            request.setRequestHeader(csrfHeader, token);
        },
        success: function () {
            // temporary
            location.reload();
        },
        error: function (jqXHR, textStatus, errorThrown) {
        }
    })
    $(".reschedule-popup").remove();
}

function sendDeleteRequest(id, bgContainer) {
    let deletePopup = "<div class='position-fixed container reschedule-popup'>\n" +
        "<button type=\"button\" class=\"close\" aria-label=\"CZlose\">\n" +
        "  <span aria-hidden=\"true\">&times;</span>\n" +
        "</button>" +
        "    <h6 id='deleteActivity'></h6>\n" +
        "     <h5 id='confirmation'></h5>\n" +
        "        <div class='row justify-content-between' style='margin-bottom: 20px; width: fit-content'>\n" +
        "            <div class='col-6'>\n" +
        "                <button class='btn cancel' id='cancel-reschedule'>\n" +
        "                    \n" + $.i18n("delete.activity.cancel") +
        "                </button>\n" +
        "            </div>\n" +
        "            <div class='col-6'>\n" +
        "                <button class='btn confirm' id='delete-btn'>\n" +
        "                    \n" + $.i18n("delete.activity.delete") +
        "                </button>\n" +
        "            </div>\n" +
        "\n" +
        "        </div>\n" +
        "</div>";
    $("#hidden-form").append(deletePopup);
    setDisabled(bgContainer);

    $(".confirm").hover(function () {
        swapClass("#cancel-reschedule", "confirm", "#delete-btn", "cancel");
    }, function () {
    });
    $(".confirm").focus(function () {
        swapClass("#cancel-reschedule", "confirm", "#delete-btn", "cancel");
    });
    $(".cancel").hover(function () {
        swapClass("#delete-btn", "confirm", "#cancel-reschedule", "cancel");
    }, function () {
        swapClass("#delete-btn", "cancel", "#cancel-reschedule", "confirm");
    });
    $(".cancel").focus(function () {
        swapClass("#delete-btn", "confirm", "#cancel-reschedule", "cancel");
    });
    $('#deleteActivity').text($.i18n("delete.activity.title"));
    $('#confirmation').text($.i18n("delete.activity.confirmation"));

    $(".close").click(function () {
        $(".reschedule-popup").remove();
        setDefault(bgContainer);
    })
    $("#cancel-reschedule").click(function () {
        $(".reschedule-popup").remove();
        setDefault(bgContainer);
    })

    $('#delete-btn').click(function (e) {
        e.preventDefault();
        const token = $('input[name^="_csrf"]').val();
        $.ajax({
            method: 'DELETE',
            url: location.origin + "/event/delete/" + id,
            beforeSend: function (request) {
                request.setRequestHeader(csrfHeader, token);
            },
            success: function () {
                if (calendar) {
                    calendar.refetchEvents();
                    calendar.rerenderEvents();
                } else {
                    location.reload();
                }
            },
            error: function (jqXHR, textStatus, errorThrown) {
            }
        })
        $(".reschedule-popup").remove();
        setDefault(bgContainer);
    })
}


function hideContextMenu(e) {
    let classList = e.target.classList;
    if (!classList.contains("fc-time-grid-event")) {
        $(".context-menu").hide()
    }
}

function onAddMoreClick(showMoreText, showLessText, activityTitle) {
    $(".fc-more").click(function () {
        positionPopover(activityTitle);
        $('.fc-popover .fc-event-container .fc-content').each(function () {
            let id = parseInt($(this).parent().children('.id').html());
            let time = $(this).children('.fc-time');
            $(time).show();
            let title = $(this).children('.fc-title');
            let circle = $(this).children('.circle');
            let eventType = deriveEvent(circle);
            let event = source.get(parseInt(id));
            if (new Date(event.start) < new Date()) circle.addClass('gray');
            eventType = eventType.replace(" gray", "");
            // circle.removeClass("gray");
            let eventTypeLocale = source.get(id).typeByLocale;
            eventTypeLocale = capitalizeFirstLetter(eventTypeLocale)

            let eventDiv = $("<div class='text-right'></div>");
            $(eventDiv).append(circle);
            $(eventDiv).append(" ");
            $(eventDiv).append(eventTypeLocale);

            $(this).empty();
            $(this).append(eventDiv);
            $(this).append(title);
            $(this).append(time);
            $(this).parent().css('border-left-color', circle.css('border-color'));
            if (eventType === 'event') {
                let showMoreOrLess = '<span class="show-more">' + showMoreText + '</span>';
                let hiddenDesc = $(this).parent().find('.hidden-description').html();
                if (hiddenDesc) {
                    let moreDescription = '<div class="more-description d-none">' + hiddenDesc + '</div>';

                    $(this).append(moreDescription);
                    $(this).append(showMoreOrLess);
                }
            }
            $(this).parent().find('.show-more').on('click', function () {
                showMoreListener.call(this, showMoreText, showLessText);
                $(this).parent().find('.more-description').toggleClass('d-none');
                // $('.popover-container').parents(".popover").attr('style', 'width: 37% !important; border:none;  box-shadow: 0 23px 37px 0 rgba(0, 96, 167, 0.35);' );
                // $(".group-sign").css("color", "#BDBDBD");
            })

        });
    })

}


function deriveEvent(circle) {
    let filters = ['elementary', 'preschool', 'students', 'high', 'individuals', 'event', 'middle'];
    let a = filters.filter((e) => $(circle).hasClass(e) || $(circle).hasClass(e + "-prebooked"));
    return a[0];
}


function getAllEvents(url) {
    const token = $('input[name^="_csrf"]').val();

    return $.ajax({
        method: 'GET',
        url: url ? url : location.origin + "/event/all" + location.pathname,
        beforeSend: function (request) {
            request.setRequestHeader(csrfHeader, token);
        },
        dataType: "json",
        success: function (data) {
            console.log(data);
            data.forEach((event) => {
                if (event.title == null || event.title == "") event.title = capitalizeFirstLetter(event.typeByLocale);
                if (event.desc == null) event.desc = '';
                source.set(event.id, event)
            });
            return source.values();
        },
        error: function (jqXHR, textStatus, errorThrown) {

        }
    });
}

function filterActivities() {
    let newSource;
    if (myActivities == null) myActivities = Array.from(source.values());
    if (filters.size === 0) {
        newSource = myActivities;
    } else {
        newSource = myActivities.filter((e) => filters.has(getClassName(e.type)));
    }
    calendar.removeAllEventSources();
    calendar.addEventSource(newSource);
    calendar.refetchEvents();
    calendar.rerenderEvents();
}

function formatDate(date) {
    let d = new Date(date),
        month = '' + (d.getMonth() + 1),
        day = '' + d.getDate(),
        year = d.getFullYear();

    if (month.length < 2)
        month = '0' + month;
    if (day.length < 2)
        day = '0' + day;

    return [year, month, day].join('-');
}

function formatDateReverse(date) {
    let dateIsoFormat = formatDate(date);
    return dateIsoFormat.split("-").reverse().join("-");
}

function formatTime(date) {
    let d = new Date(date);
    return d.getHours().toString().padStart(2, "0") + ":" + d.getMinutes().toString().padStart(2, "0")
}

function capitalizeFirstLetter(string) {
    return string.replace(string[0], string[0].toUpperCase());
}

function getEventPhoto(eventId) {
    // let image='';
    let id = '#img_' + eventId;
    $.ajax({
        type: 'GET',
        url: location.origin + '/event/photo/' + eventId,
        success: function (data, textStatus, xhr) {
            if (xhr.status === 204) $(id).hide()
            ;
            // else image.attr('src', 'data:image/jpg;base64,' + data)
            else {
                if (data != null) {
                    $(id).removeClass("d-none");
                    $(id).prop('src', 'data:image/jpg;base64,' + data);
                }
            }
        },

    })

}


// swap buttons
// This is dublicate please factor  during refactor
const addActivityBtn = $('#reschedule-btn');
const cancelBtn = $('#cancel-reschedule');

addActivityBtn.hover(function () {
//done    swapClass("#cancel-reschedule", "confirm", "#reschedule-btn", "cancel");
}, function () {
    //done swapClass("#reschedule-btn", "confirm", "#cancel-reschedule", "cancel");
});
addActivityBtn.focus(function () {
    swapClass("#cancel-reschedule", "confirm", "#reschedule-btn", "cancel");
});
cancelBtn.hover(function () {
    swapClass("#reschedule-btn", "confirm", "#cancel-reschedule", "confirm");
}, function () {
});
cancelBtn.focus(function () {
    swapClass("#reschedule-btn", "confirm", "#cancel-reschedule", "cancel");
});

function swapClass(selector1, selector1Class, selector2, selector2Class) {
    $(selector1).removeClass(selector1Class).addClass(selector2Class);
    $(selector2).removeClass(selector2Class).addClass(selector1Class);
}

function removePopover() {
    $('.popover').remove();
    // $('.show-less').text('Show more');
}

function removePopoverHandler(e) {
    let classList = e.target.classList;
    if (!classList.contains("group-icon")) {
        $('.group-popover').parents(".popover").remove();
        $('.group-sign').css("color", '');
    }

    if (!classList.contains("popover") && !classList.contains("show-more") && !classList.contains("show-less")) {
        // $(".fc-content .show-more").html(localedMore);
        $(".show-more").css("color", "#A1A3A8");

        $('.popover-container').parents(".popover").remove();
    }

}

function setDisabled(container) {
    container.css("opacity", 0.6);
    container.css("background-color", "#F0F2FB");
    container.css("pointer-events", "none");
}

function setDefault(container) {
    container.css("opacity", "unset");
    container.css("background-color", "unset");
    container.css("pointer-events", "unset");
}
