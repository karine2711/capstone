$(document).ready(async function () {
    let waitingListFilters = new Set();
    const csrfHeaderName = "X-CSRF-TOKEN";
    const sortBtn = $("#sort");
    const eventBox = $('#waiting-list-flexbox');
    let filters = $('#filters');
    let allEvents = new Map();

    let customMonths = {
        en: ["JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"],
        hy: 'ՀՆՎ_ՓՏՐ_ՄՐՏ_ԱՊՐ_ՄՅՍ_ՀՆՍ_ՀԼՍ_ՕԳՍ_ՍՊՏ_ՀԿՏ_ՆՄԲ_ԴԿՏ'.split('_'),
        ru: 'ЯНВ._ФЕВР._МАРТ_АПР._МАЙ_ИЮНЬ_ИЮЛЬ_АВГ._СЕНТ._ОКТ._НОЯБ._ДЕК.'.split('_'),
    };

    await $.i18n().load({
        "en": "/i18n/en.json",
        "hy": "/i18n/hy.json",
        "ru": "/i18n/ru.json",
    });

    let localeValue = $("#locale").val();
    $.i18n().locale = localeValue;


    const token = $('input[name^="_csrf"]').val();
    // draw filters
    $.ajax({
        type: "GET",
        url: location.origin + "/event" + "/getEventTypes",
        success: function (data) {
            let filterButtons = " <div style='margin-top: 20px'>";
            data = new Map(Object.entries(data));
            for (const [key, value] of data.entries()) {
                if (key != 'Event') {
                    let button = "<button type='button' class='waiting-filter-button' id='" + getClassName(key) + "'>" +
                        "                <span class='circle " + getClassName(key) + "'>" +
                        "                </span>" +
                        value +
                        "            </button>";
                    filterButtons = filterButtons + button;
                    waitingListFilters.add(getClassName(key));
                }
            }
            filterButtons = filterButtons + "</div>";
            filters.append(filterButtons);
        }
    }).then(function () {
        $('.waiting-filter-button').click(function () {
            waitingListFilters.has(this.id) ? waitingListFilters.delete(this.id) : waitingListFilters.add(this.id);
            $(this).toggleClass(this.id);
            $(this).toggleClass('text-white');
            $(this).children('.circle').toggleClass(this.id);
            filterWaitingList();
        });
    });


    function filterWaitingList() {
        $('.pre-booked-event-container').show();
        if (waitingListFilters.size > 0 && waitingListFilters.size < 6) {
            waitingListFilters.forEach(function (elem) {
                $("." + elem + "-event").parent().hide();
            })
        }
    }

    function getClassName(eventType) {
        eventType = eventType.toLowerCase();
        eventType = eventType.replace(" school", "");
        eventType = eventType.replace("_school", "");
        return eventType;
    }

    sortBtn.on('click', function () {
        let notificationsBox = document.getElementById("waiting-list-flexbox");
        if (notificationsBox.classList.contains('flex-column')) {
            notificationsBox.classList.replace('flex-column', 'flex-column-reverse');
        } else if (notificationsBox.classList.contains('flex-column-reverse')) {
            notificationsBox.classList.replace('flex-column-reverse', 'flex-column');
        }
    });


    getAllPreBookedActivities();

    function getAllPreBookedActivities() {
        $.ajax({
            method: 'GET',
            url: location.origin + location.pathname + "/events/active",
            beforeSend: function (request) {
                request.setRequestHeader(csrfHeaderName, token);
            },
            success: function (data) {

                // console.log(data)
                for (let key in data) {
                    let preBookedEvents = data[key];
                    preBookedEvents.forEach((event) => {
                        if (event.title == null || event.title == "") event.title = capitalizeFirstLetter(event.typeByLocale);
                        if (event.desc == null) event.desc = '';
                        allEvents.set(parseInt(event.id), event);
                    });
                    let preBookedEvent = preBookedEvents[0].type;
                    let preBookedEventType = preBookedEvent.charAt(0).toUpperCase() + preBookedEvent.slice(1)
                    console.log(preBookedEventType)

                    let container = $('<div></div>');

                    eventBox.append(container);

                    container.addClass('pre-booked-event-container');
                    container.attr('name', 'pre-booked-event-rectangle');

                    let containerFluid = $('<div></div>');
                    containerFluid.addClass(`container-fluid rectangle ${preBookedEvents[0].type}-event`);

                    container.append(containerFluid);

                    let formGroup = $('<div></div>');
                    formGroup.addClass('form-group row mt-2');

                    containerFluid.append(formGroup);

                    let eventDateContainer = $('<div></div>');
                    eventDateContainer.addClass('col-1 pre-booked-event-date');
                    let dayOfMonthContainer = $(`<div class="row ml-4">${new Date(key).getDate()}</div>`);
                    let monthContainer = $(`<div class="row ml-3">${customMonths[localeValue][new Date(key).getMonth()]}</div>`);

                    eventDateContainer.append(dayOfMonthContainer);
                    eventDateContainer.append(monthContainer);
                    formGroup.append(eventDateContainer);

                    let eventTypeContainer = $(`<div class="col-2 pre-booked-event-type">${preBookedEvents[0].typeByLocale}</div>`);

                    formGroup.append(eventTypeContainer);

                    let preBookedEventsContainer = $(`<div class="col-9"></div>`);
                    let preBookedEventsTimeContainer = $(`<div class="pl-3 row pre-booked-event-hours">
                ${eventStartAndEndDates(preBookedEvents[0])}</div>`);

                    function eventStartAndEndDates(preBookedEvent) {
                        return toAmPm(preBookedEvents[0].start).toLowerCase() + ' - ' +
                            toAmPm(preBookedEvents[0].end).toLowerCase();
                    }

                    function toAmPm(date) {
                        return new Date(date).toLocaleString('en-US', {
                            hour: 'numeric',
                            minute: 'numeric',
                            hour12: true
                        })
                    }

                    formGroup.append(preBookedEventsContainer);
                    preBookedEventsContainer.append(preBookedEventsTimeContainer);

                    let schoolLabel = $('<div class="col-2 school-label">' + $.i18n('waiting.list.school') + '</div>');
                    let groupLabel = $('<div class="col-2 group-label">' + $.i18n('waiting.list.class') + '</div>');
                    let groupSizeLabel = $('<div class="col-2 group-size-label">' + $.i18n('waiting.list.groupSize') + '</div>');
                    let createdByLabel = $('<div class="col-3 group-size-label" style="text-align: center">' + $.i18n('waiting.list.createdBy') + '</div>');
                    let labelRow = $('<div class="row pre-booked-event-label-row"></div>');
                    labelRow.append(schoolLabel, groupLabel, groupSizeLabel, createdByLabel);
                    preBookedEventsContainer.append(labelRow);


                    preBookedEvents.forEach(function (elem, index) {
                        let preBookedEventForm = $(`<form name="event-form-${elem.id}" id="${elem.id}" class="form-inline pre-booked-event-form"></form>`);
                        preBookedEventsContainer.append(preBookedEventForm);

                        let schoolContainer = $(`<input type="text" class="col-2 school-input" value="${elem.school}" readonly />`);
                        let classContainer = $(`<input type="text" class="col-2 group-input" value="${elem.group}" readonly />`);
                        let groupSizeContainer = $(`<input type="text" class="col-2 group-size-input" value="${elem.groupSize}" readonly />`);
                        let eventCheckBox = $(`<input type="checkbox" class="col-1 event-checkbox-input" />`);
                        let createdBy = $(`<input type="text" class="col-4 created-by-input pl-0" value="${elem.username}" readonly />`);
                        preBookedEventForm.append(schoolContainer, classContainer, groupSizeContainer, eventCheckBox, createdBy);

                        createdBy.on('click', function () {
                            let userName = elem.username;
                            sessionStorage.setItem('userName', userName);
                            window.location.href = location.origin + "/users";
                        });
                    });

                    let buttonRow = $(`<div class="row mt-4 pl-3"></div>`);
                    let buttonContainer = $(`<div class="col-8 buttons3 d-flex pl-0"></div>`);

                    preBookedEventsContainer.append(buttonRow);
                    buttonRow.append(buttonContainer);

                    let confirmButton = $('<button class="btn main_button center mr-2" type="button" name="confirm" style="">' + $.i18n('waiting.list.confirm') + '</button>');
                    let rescheduleButton = $('<button class="btn cancel center mx-2" type="button" name="reschedule">' + $.i18n('waiting.list.reschedule') + '</button>');
                    let cancelButton = $('<button class="delete btn cancel center mx-2" type="button" name="delete">' + $.i18n('waiting.list.delete') + '</button>');

                    buttonContainer.append(confirmButton, rescheduleButton, cancelButton);

                    function deleteChosenPreBookedActivities(preBookedIds) {
                        if ($(".reschedule-popup").length !== 0) {
                            $(".reschedule-popup").remove();
                        }
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
                        });
                        $("#cancel-reschedule").click(function () {
                            $(".reschedule-popup").remove();
                        });
                        $('#delete-btn').click(function (e) {
                            $.ajax({
                                method: 'DELETE',
                                data: JSON.stringify(preBookedIds),
                                contentType: 'application/json',
                                url: location.origin + "/event/delete/allChosen",
                                beforeSend: function (request) {
                                    request.setRequestHeader(csrfHeaderName, token);
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
                            });
                            $(".reschedule-popup").remove();
                        })
                    };

                    confirmButton.on('click', function (e) {
                        const token = $('input[name^="_csrf"]').val();
                        let preBookedIds = [];
                        preBookedEventsContainer.find('input[type=checkbox]')
                            .each(function () {
                                if ($(this).prop('checked')) {
                                    preBookedIds.push(parseInt(this.form.id))
                                }
                            });
                        if (preBookedIds.length > 0) {
                            $.ajax({
                                method: 'PUT',
                                data: JSON.stringify(preBookedIds),
                                contentType: 'application/json',
                                url: location.origin + "/event/book",
                                beforeSend: function (request) {
                                    request.setRequestHeader(csrfHeaderName, token);
                                },
                                success: function () {
                                    location.reload();
                                },
                                error: function (jqXHR, textStatus, errorThrown) {
                                    console.error(textStatus, errorThrown)
                                }
                            });
                        }
                    });

                    rescheduleButton.on('click', function (e) {
                        console.log('rescheduled clicked!!!');
                        const token = $('input[name^="_csrf"]').val();
                        let preBookedIds = [];
                        preBookedEventsContainer.find('input[type=checkbox]')
                            .each(function () {
                                console.log($(this).prop('checked'));
                                console.log(this.form.id);
                                if ($(this).prop('checked')) {
                                    preBookedIds.push(parseInt(this.form.id))
                                }
                            });
                        if (preBookedIds.length > 0) {
                            drawReschedulePopupPreBooked(preBookedIds);
                        }
                    });

                    function drawReschedulePopupPreBooked(preBookedIds) {
                        // if (id == null) id = sessionStorage.getItem("id");
                        let id = preBookedIds[0];
                        let event = allEvents.get(parseInt(id));
                        let type = getClassName(event.type);
                        let start = (new Date(event.start));
                        let end = (new Date(event.end));
                        let title = event.title;
                        let eventType = event.type;
                        let eventTypeId = event.eventTypeId;
                        let localeType = event.typeByLocale;
                        if ($(".reschedule-popup").length != 0) {
                            $(".reschedule-popup").remove();
                        }
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
                        })
                        $("#cancel-reschedule").click(function () {
                            $(".reschedule-popup").remove();
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
                            let eventList = [];
                            preBookedIds.forEach(pId => eventList.push({
                                "date": date,
                                "eventId": pId,
                                "time": time
                            }));
                            // let eventObj = {
                            //     "date": date,
                            //     "eventId": event.id,
                            //     "time": time
                            // }
                            const token = $('input[name^="_csrf"]').val();

                            $.ajax({
                                type: "post",
                                url: location.origin + "/event" + "/reschedule/allChosen",
                                data: JSON.stringify(eventList),
                                contentType: "application/json; charset=utf-8",
                                beforeSend: function (request) {
                                    request.setRequestHeader(csrfHeader, token);
                                },
                                success: function () {
                                    $(".reschedule-popup").remove();
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


                    cancelButton.on('click', function (e) {
                        let preBookedIds = [];
                        preBookedEventsContainer.find('input[type=checkbox]')
                            .each(function () {
                                if ($(this).prop('checked')) {
                                    preBookedIds.push(parseInt(this.form.id))
                                }
                            });
                        if (preBookedIds.length > 0) {
                            deleteChosenPreBookedActivities(preBookedIds);
                        }
                    });

                }
            },
            error: function (jqXHR, textStatus, errorThrown) {
            }
        });
    }

    function getAllEvents() {

        return $.ajax({
            method: 'GET',
            url: location.origin + "/event/all" + location.pathname,
            beforeSend: function (request) {
                const token = $('input[name^="_csrf"]').val();
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
});
