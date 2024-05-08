(function (global, factory) {
    typeof exports === 'object' && typeof module !== 'undefined' ? module.exports = factory() :
        typeof define === 'function' && define.amd ? define(factory) :
            (global = global || self, (global.FullCalendarLocales = global.FullCalendarLocales || {}, global.FullCalendarLocales.arm = factory()));
}(this, function () {
        'use strict';
        let monthsShort = 'Հունվար_Փետրվար_Մարտ_Ապրիլ_Մայիս_Հունիս_Հուլիս_Օգոստոս_Սեպտեմբեր_Հոկտեմբեր_Նոյեմբեր_Դեկտեմբեր'.split('_');
        let weekdaysShort = 'Կրկ_Երկ_Երք_Չրք_Հնգ_Ուրբ_Շբթ'.split('_');

        var arm = {
            code: "arm",
            week: {
                dow: 1,
                doy: 4 // The week that contains Jan 4th is the first week of the year.
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
            },

            buttonText: {
                prev: "Հաջորդ",
                next: "Նախորդ",
                today: "Այսօր",
                month: "Ամիս",
                week: "Շաբաթ",
                day: "Օր",
                list: "Օր",
            },
            customButtons: {
                activityButton: {
                    text: 'ԱՄՐԱԳՐԵԼ ԷՔՍԿՈՒՐՍԻԱ',
                    click: function () {
                        location.href = "/event";
                    },
                },
                listDay: {
                    text: 'Օր',
                    click: function () {
                        calendar.changeView('listDay');
                    }
                },
            },
            weekLabel: "Շաբ",
            allDayText: "Ամբողջ օրը",
            eventLimitText: function (n) {
                return "+" + n + " ավելին";
            },
            columnHeaderText: function (mom) {

                return weekdaysShort[mom.getDay()];
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
            viewSkeletonRender: function (arg) {
                if (arg.view.type === 'dayGridWeek') {
                    $('.fc-view').prepend('<tr class="week-days"><td class="fc-head-container fc-widget-header">' +
                        '<div class="fc-row fc-widget-header"><table class=""><thead>' +
                        '<tr><th class="fc-day-header fc-widget-header fc-mon " >' +
                        '<span>Երկ</span></th>' +
                        '<th class="fc-day-header fc-widget-header fc-tue my-week-header"><span>Երք</span></th>' +
                        '<th class="fc-day-header fc-widget-header fc-wed my-week-header" ><span>Չրք</span></th>' +
                        '<th class="fc-day-header fc-widget-header fc-thu my-week-header" ><span>Հնգ</span></th>' +
                        '<th class="fc-day-header fc-widget-header fc-fri my-week-header" ><span>Ուրբ</span></th>' +
                        '<th class="fc-day-header fc-widget-header fc-sat my-week-header" ><span>Շաբ</span></th>' +
                        '<th class="fc-day-header fc-widget-header fc-sun my-week-header" ><span>Կրկ</span></th>' +
                        '</tr></thead></table></div></td></tr>')
                }
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
                        language: 'hy'
                        // startDate: minDate
                    });

                    $("#dateScroller").change(function () {
                        calendar.gotoDate(new Date($(this).val()).toISOString());
                    })

                    $(".fc-right").css("z-index", 50);
                }
            }


        };

        return arm;

    }
));
