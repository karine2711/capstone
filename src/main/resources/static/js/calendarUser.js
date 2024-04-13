document.addEventListener('DOMContentLoaded', async function () {
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
        plugins: ['dayGrid', 'timeGrid', 'list', 'moment'],
        locales: ['ruLocale', 'enLocale', 'armLocale'],
        firstDay: 1,
        eventLimit: 3,
        fixedWeekCount: false,
        displayEventEnd: true,
        eventTimeFormat: {
            hour: 'numeric',
            minute: '2-digit',
            meridiem: false,
            hour12: false
        },
        header: {
            left: 'activityButton',
            center: 'prev, title, next',
            right: 'dayGridMonth, dayGridWeek, listDay'
        },
        views: {
            dayGridWeek: {
                columnHeaderText: {day: 'numeric'},
                columnHeaderFormat: {day: 'numeric'},
                displayEventEnd: true
            },
            dayGridMonth: {
                eventTimeFormat: {
                    hour: 'numeric',
                    minute: '2-digit',
                    meridiem: false,
                    hour12: false
                }
            },
            listDay: {
                displayEventEnd: false,
                displayEventTime: false,
                handleWindowResize: false,
                columnHeader: false,
            }
        },
        events: function () {
            return getAllEvents()
        }
    })

    calendar.setOption('locale', 'arm');
    if (date) {
        calendar.gotoDate(new Date(date).toISOString());
    }
    sessionStorage.removeItem("calendarCurrentDate");
    sessionStorage.removeItem("view");

    calendar.render();
});

