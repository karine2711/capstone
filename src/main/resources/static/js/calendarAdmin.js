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
                maxTime: '17:00:00',
                displayEventTime: false,
            },
            timeGridDay: {
                slotDuration: {hours: 1},
                allDaySlot: false,
                minTime: '10:00:00',
                maxTime: '17:00:00',
                columnHeader: false,
                displayEventTime: false,
                nowIndicator: true,
            }
        },
        events: function () {
            return getAllEvents()
        },
        eventRender: function (arg) {
                                let {event, view, el} = arg;
                                let {id, start, end, title, extendedProps} = event;
                                let {count, type, desc, groupSizes, username, typeByLocale, eventState} = extendedProps;
                                let circleType = getCircleType(arg.view, arg.event);
                                let circle = "<span  class='circle " + circleType + "'></span>";
                                let sum = count;
                                groupSizes.forEach(s => sum = sum + s);

                                addMore( $.i18n("calendar.event.show.more"), $.i18n("calendar.event.show.less"),  $.i18n("calendar.event.daily.activities"));

                               if (view.type === 'timeGridWeek' || view.type === 'timeGridDay') {
                                    $(el).addClass(type);
                                    addEventId(event, el);
                                    if (eventState == "PRE_BOOKED") {
                                        $(el).css('border', 'none');
                                        let div = "<div class='pre-booked-before' ";
                                        $(el).find('.fc-content').css('margin-left', '13px');
                                        let color1 = $(`.${getClassName(type)}`).css('background-color');
                                        let color2 = "#ffffff";
                                        let background = "linear-gradient(54deg";
                                        for (let i = 5; i <= 100; i = i + 5) {
                                            if (i % 10 === 0)
                                                background = background + ", " + color2 + " " + i + "%"
                                            else
                                                background = background + ", " + color1 + " " + i + "%"
                                        }
                                        background = background + ")";
                                        div = div + " style='background-image:" + background + "'></div>"
                                        $(el).prepend(div);
                                    } else {
                                        $(el).css('border-left-color', $(`.${getClassName(type)}`).css('background-color'));
                                    }
                                    if (groupSizes.length > 0) {
                                        $(el).append('<span class="group-sign" style="">' + '<i class="fa fa-users group-icon" aria-hidden="true"></i>' + '</span>');
                                        groupsPopover(el, count, groupSizes, username)
                                    }
                                } else {
                                    addEventId(event, el);
                                    addCircle(view, event, el);
                                    if (type.toLowerCase() === 'event') {
                                        addEventDesc(event, el);
                                        makePopover(el, event)
                                    } else if (groupSizes.length > 0) {
                                        addGroupSign(event, el,);
                                        groupsPopover(el, count, groupSizes, username)
                                    }
                                }
                                if (+new Date(start) >= +new Date()) {
                                    if ($('#fc-username').html() == event.extendedProps.username && event.extendedProps.eventTypeId != 7) {
                                        addContextMenu(el, $.i18n("calendar.event.reschedule"), $.i18n("calendar.event.delete"), $.i18n("calendar.event.confirm"),);
                                    }
                                    if ($("#admin").length > 0 && event.extendedProps.eventTypeId == 7) {
                                        addContextMenu(el, $.i18n("calendar.event.reschedule"), $.i18n("calendar.event.delete"), null, $.i18n("calendar.event.update"),);
                                    }

                                }
                            },
        datesRender: function (arg) {

                        if (arg.view.type === 'timeGridWeek') {
                            $(arg.el).find('.fc-slats tr').each(function () {
                                let elem = $(this).children('.fc-axis')[0];
                                $(this).html(
                                    '<td class="fc-widget-content"></td>' +
                                    '<td class="fc-widget-content"></td>' +
                                    '<td class="fc-widget-content"></td>' +
                                    '<td class="fc-widget-content"></td>' +
                                    '<td class="fc-widget-content"></td>' +
                                    '<td class="fc-widget-content"></td>' +
                                    '<td class="fc-widget-content"></td>'
                                )
                                this.prepend(elem);
                            })
                        }
                        $('.fc-slats tbody tr').last().children('.fc-axis').append("<span style='position: relative; top: 300%; display: block;'>17:00</span>");

                        $("#dateScroller").datepicker('setDate', formatDate(calendar.getDate()));
                    },


    });

    if (date) {
        calendar.gotoDate(new Date(date).toISOString());
    }
    sessionStorage.removeItem("calendarCurrentDate");
    sessionStorage.removeItem("view");
    calendar.render();

});

