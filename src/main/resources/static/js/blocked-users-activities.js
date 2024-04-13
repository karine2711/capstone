$(document).ready(async function () {
    const csrfHeaderName = "X-CSRF-TOKEN";
    const sortBtn = $("#sort-blocked");
    const eventBox = $('#blocked-list-flexbox');
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

    sortBtn.on('click', function () {
        let notificationsBox = document.getElementById("blocked-list-flexbox");
        if (notificationsBox.classList.contains('flex-column')) {
            notificationsBox.classList.replace('flex-column', 'flex-column-reverse');
        } else if (notificationsBox.classList.contains('flex-column-reverse')) {
            notificationsBox.classList.replace('flex-column-reverse', 'flex-column');
        }
    })


    $.ajax({
        method: 'GET',
        url: location.origin + location.pathname + "/events/blocked",
        beforeSend: function (request) {
            request.setRequestHeader(csrfHeaderName, token);
        },
        success: function (data) {
            console.log(data)
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
                container.attr('name', 'blocked-event-rectangle');

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
                    // let preBookedEventRow = $('<div class="row pre-booked-event-row"></div>');
                    // preBookedEventsContainer.append(preBookedEventRow);

                    let preBookedEventForm = $(`<form name="event-form-${elem.id}" id="${elem.id}" class="form-inline pre-booked-event-form"></form>`);
                    // preBookedEventRow.append(preBookedEventForm);
                    preBookedEventsContainer.append(preBookedEventForm);

                    let schoolContainer = $(`<input type="text" class="col-2 school-input" value="${elem.school}" readonly />`);
                    let classContainer = $(`<input type="text" class="col-2 group-input" value="${elem.group}" readonly />`);
                    let groupSizeContainer = $(`<input type="text" class="col-2 group-size-input" value="${elem.groupSize}" readonly />`);
                    let eventCheckBox = $(`<input type="checkbox" class="col-1 event-checkbox-input" />`);
                    let createdBy = $(`<input type="text" class="col-4 created-by-input pl-0" style="color: red" value="${elem.username}" readonly />`);
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

                let cancelButton = $('<button class="delete btn main_button cancel center" type="button">' + $.i18n('waiting.list.delete') + '</button>');

                buttonContainer.append(cancelButton);

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
                    })
                    $("#cancel-reschedule").click(function () {
                        $(".reschedule-popup").remove();
                    })
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
                        })
                        $(".reschedule-popup").remove();
                    })
                };
            }
        },
        error: function (jqXHR, textStatus, errorThrown) {
        }
    });
});
