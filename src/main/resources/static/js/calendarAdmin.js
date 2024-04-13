// let source = new Map();

document.addEventListener('DOMContentLoaded', async function (e) {

    await $.i18n().load({
        "en": "/i18n/en.json",
        "hy": "/i18n/hy.json",
        "ru": "/i18n/ru.json",
    });
    let date = sessionStorage.getItem("calendarCurrentDate");
    let view = sessionStorage.getItem("view")

    let calendarEl = document.getElementById('calendar');

    calendar = new FullCalendar.Calendar(calendarEl, {
        defaultView: view,
        plugins: ['dayGrid', 'timeGrid', 'list'],
        header: {
            left: 'activityButton',
            center: 'prev, title, next',
            right: 'dayGridMonth, timeGridWeek, timeGridDay'
        },
        firstDay: 1,
        views: {
            dayGridMonth: {
                eventLimit: 3,
                fixedWeekCount: false,
                displayEventEnd: true,
            },
            timeGridWeek: {
                slotDuration: '01:00:00',
                slotLabelInterval: {hours: 1},
                allDaySlot: false,
                minTime: '10:00:00',
                maxTime: '16:00:00',
                displayEventTime: false,
            },
            timeGridDay: {
                slotDuration: {hours: 1},
                allDaySlot: false,
                minTime: '10:00:00',
                maxTime: '16:00:00',
                columnHeader: false,
                displayEventTime: false,
                nowIndicator: true,
            }
        },
        events: function () {
            return getAllEvents()
        }
    });

    if (date) {
        calendar.gotoDate(new Date(date).toISOString());
    }
    sessionStorage.removeItem("calendarCurrentDate");
    sessionStorage.removeItem("view");
    calendar.render();

});

