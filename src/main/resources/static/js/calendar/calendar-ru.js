(function (global, factory) {
    typeof exports === 'object' && typeof module !== 'undefined' ? module.exports = factory() :
        typeof define === 'function' && define.amd ? define(factory) :
            (global = global || self, (global.FullCalendarLocales = global.FullCalendarLocales || {}, global.FullCalendarLocales.ru = factory()));
}(this, function () {
        'use strict';

        let weekdaysShort = 'Вс_Пн_Вт_Ср_Чт_Пт_Сб'.split('_');
        let monthsShort = 'Январь_Февраль_Март_Апрель_Май_Июнь_Июль_Август_Сентябрь_Октябрь_Ноябрь_Декабрь'.split('_');

        var ru = {
            code: "ru",
            week: {
                dow: 1,
                doy: 4 // The week that contains Jan 4th is the first week of the year.
            },
            buttonText: {
                month: "Месяц",
                week: "Неделя",
                day: "День"
            },
            customButtons: {
                activityButton: {
                    text: 'ЗАБРОНИРОВАТЬ ЭКСКУРСИЮ',
                    click: function () {
                        location.href = "/event";
                    },
                },

            },
            weekLabel: "Нед",
            allDayText: "Весь день",
            eventLimitText: function (n) {
                return "+ ещё " + n;
            },
            columnHeaderFormat: {
                weekday: 'short'
            },
            slotLabelFormat: {
                hour: '2-digit',
                minute: '2-digit',
                meridiem: false,
                hour12: false
            },
            eventTimeFormat: {
                hour: '2-digit',
                minute: '2-digit',
                meridiem: false,
                hour12: false
            },
            columnHeaderText: function (mom) {
                return weekdaysShort[mom.getDay()];
            },
            viewSkeletonRender: function (arg) {
                if (arg.view.type === 'dayGridWeek') {
                    $('.fc-view').prepend('<tr class="week-days"><td class="fc-head-container fc-widget-header">' +
                        '<div class="fc-row fc-widget-header"><table class=""><thead>' +
                        '<tr><th class="fc-day-header fc-widget-header fc-mon fc-past" data-date="2020-06-22">' +
                        '<span>Пн</span></th>' +
                        '<th class="fc-day-header fc-widget-header fc-tue fc-past my-week-header" data-date="2020-06-23"><span>Вт</span></th>' +
                        '<th class="fc-day-header fc-widget-header fc-wed fc-past my-week-header" data-date="2020-06-24"><span>Ср</span></th>' +
                        '<th class="fc-day-header fc-widget-header fc-thu fc-today my-week-header" data-date="2020-06-25"><span>Чт</span></th>' +
                        '<th class="fc-day-header fc-widget-header fc-fri fc-future my-week-header" data-date="2020-06-26"><span>Пт</span></th>' +
                        '<th class="fc-day-header fc-widget-header fc-sat fc-future my-week-header"  data-date="2020-06-27"><span>Сб</span></th>' +
                        '<th class="fc-day-header fc-widget-header fc-sun fc-future my-week-header" data-date="2020-06-28"><span>Вс</span></th>' +
                        '</tr></thead></table></div></td></tr>')
                }
                // $(".fc-right #scrollerContainer").remove();
                if ($(".fc-center #scrollerContainer").length == 0) {
                    $(".fc-center").prepend("<div style='display: flex'" +
                        "  id='scrollerContainer'>\n" +
                        "                                <input type=\"text\" id=\"dateScroller\" class=\"form-control datepicker bg-white\" readonly autocomplete=\"off\" th:placeholder=\"#{field.activity.date.placeholder}\" th:field=\"*{date}\" aria-required=\"true\"  style=\"cursor: pointer;width: 1px;position:relative;left:26px;opacity: 0\">\n" +
                        "                                <span style=\"\n" +
                        "                                     margin-top: 10px;" +
                        "    \t\t\t\t                         font-size: 30px;\n" +
                        "    \t\t\t\t                         pointer-events: none;\n" +
                        "    \t\t\t\t                         color: #35B8EC;\n" +
                        "    \t\t\t\t                          " +
                        "    \t\t\t\t                         /* right: 10%; */\n" +
                        "    \t\t\t\t                         \" class=\"glyphicon glyphicon-calendar\"></span> \n" +
                        "\n" +
                        "                            <div class=\"custom-invalid-feedback\"><img class=\"error-icon\" src=\"images/icon.svg\"><span class=\"error-message\" id=\"error-message-date\"></span></div></div>")

                    $("#dateScroller").datepicker({
                        format: "yyyy-mm-dd",
                        autoclose: true,
                        todayHighlight: true,
                        weekStart: 1,
                        orientation: "auto bottom",
                        language: 'ru'
                        // startDate: minDate
                    });

                    $("#dateScroller").change(function () {
                        calendar.gotoDate(new Date($(this).val()).toISOString());

                    })

                    $(".fc-right").css("z-index", 50);
                }
            },
            titleFormat: function (moment) {
                if (moment.end.day === moment.start.day) {
                    return moment.end.day + " " + monthsShort[moment.date.month] + " " + moment.end.year;
                } else if (moment.end.day - moment.start.day === 6 || moment.end.day - moment.start.day < 0) {
                    let title = moment.start.day;
                    if (moment.start.month !== moment.end.month) title = title + " " + monthsShort[moment.start.month] + " ";
                    if (moment.start.year !== moment.end.year) title = title + " " + moment.start.year;
                    title = title + " - ";
                    title = title + moment.end.day + " " + monthsShort[moment.end.month] + " " + moment.end.year;
                    return title;
                } else {
                    return monthsShort[moment.date.month] + " " + moment.end.year;
                }
            }
        };

        return ru;

    }
));
