$(document).ready(async function () {
    const csrfHeaderName = "X-CSRF-TOKEN";
    const rescheduleBtn = $("button[name*='reschedule']");
    const deleteBtn = $("button[name*='delete']");
    const confirmBtn = $("button[name*='confirm']");
    const sortBtn = $("#sort");
    const token = $('input[name^="_csrf"]').val();
    let allEvents = new Map();

    await $.i18n().load({
        "en": "/i18n/en.json",
        "hy": "/i18n/hy.json",
        "ru": "/i18n/ru.json",
    });

    let localeValue = $("#locale").val();
    $.i18n().locale = localeValue;

    let events = getAllEventsUnfiltered(location.origin + "/event/allWithoutFilter");

    function getAllEventsUnfiltered(url) {
        let eventSource = new Map();
        return $.ajax({
            method: 'GET',
            url: url ? url : location.origin + "/event/all" + location.pathname,
            beforeSend: function (request) {
                request.setRequestHeader(csrfHeader, token);
            },
            dataType: "json",
            success: function (data) {
                data.forEach((event) => {
                    if (event.title == null || event.title == "") event.title = capitalizeFirstLetter(event.typeByLocale);
                    if (event.desc == null) event.desc = '';
                    eventSource.set(event.id, event)
                });
                allEvents = eventSource;
            },
            error: function (jqXHR, textStatus, errorThrown) {
            }
        });
    }

    events.then(ev => {
        confirmBtn.each(function () {
            let id = parseInt($(this).attr('id'), 10);
            let event = allEvents.get(id);
            let remainsLessThan48Hours = new Date(event.start) - new Date() < (1000 * 60 * 60 * 48);
            if (remainsLessThan48Hours && event.eventState !== 'PRE_BOOKED') {
                $(this).show();
            }
        });

        deleteBtn.on('click', function () {
            sendDeleteRequest(this.id, $("#notification-container"));
        });

        rescheduleBtn.on('click', function () {
            drawReschedulePopup(this.id, $("#notification-container"), allEvents);
            addDatePicker()

        });
    });

    confirmBtn.on('click', function (e) {
        const token = $('input[name^="_csrf"]').val();
        $.ajax({
            method: 'PUT',
            url: location.origin + "/event/confirm/" + this.id,
            beforeSend: function (request) {
                request.setRequestHeader(csrfHeaderName, token);
            },
            success: function () {
                // temporary
                location.reload();
            },
            error: function (jqXHR, textStatus, errorThrown) {
            }
        });
        $(".reschedule-popup").remove();
    });

    sortBtn.on('click', function () {
        let notificationsBox = document.getElementById("notification-flexbox");
        if (notificationsBox.classList.contains('flex-column')) {
            notificationsBox.classList.replace('flex-column', 'flex-column-reverse');
        } else if (notificationsBox.classList.contains('flex-column-reverse')) {
            notificationsBox.classList.replace('flex-column-reverse', 'flex-column');
        }
    })
});
