(function (global, factory) {
    typeof exports === 'object' && typeof module !== 'undefined' ? module.exports = factory() :
        typeof define === 'function' && define.amd ? define(factory) :
            (global = global || self, (global.FullCalendarLocales = global.FullCalendarLocales || {}, global.FullCalendarLocales.en = factory()));
}(this, function () {
        'use strict';

        var en = {
                code: "en",
                week: {
                    dow: 1,
                    doy: 4 // The week that contains Jan 4th is the first week of the year.
                },
                buttonText: {
                    month: "Month",
                    week: "Week",
                    day: "Day",
                    list: "List",
                },
                customButtons: {
                    activityButton: {
                        text: 'BOOK ACTIVITY',
                        click: function () {
                            location.href = "/event";
                        },
                    },
                    listDay: {
                        text: 'Day',
                        click: function () {
                            calendar.changeView('listDay');
                        }
                    },
                },
                weekLabel: "week",
                columnHeaderFormat: {
                    weekday: 'short'
                },
                slotLabelFormat: {
                    hour: 'numeric',
                    minute: '2-digit',
                    meridiem: 'short',
                    omitZeroMinute: false,
                },
                eventTimeFormat: {
                    hour: 'numeric',
                    minute: '2-digit',
                    meridiem: false,
                    hour12:false
                },
                viewSkeletonRender: function (arg) {
                    if (arg.view.type === 'dayGridWeek') {
                        $('.fc-view').prepend('<tr class="week-days"><td class="fc-head-container fc-widget-header">' +
                            '<div class="fc-row fc-widget-header"><table class=""><thead>' +
                            '<tr><th class="fc-day-header fc-widget-header fc-mon " >' +
                            '<span>Mon</span></th>' +
                            '<th class="fc-day-header fc-widget-header fc-tue my-week-header"><span>Tue</span></th>' +
                            '<th class="fc-day-header fc-widget-header fc-wed my-week-header" ><span>Wed</span></th>' +
                            '<th class="fc-day-header fc-widget-header fc-thu my-week-header" ><span>Thu</span></th>' +
                            '<th class="fc-day-header fc-widget-header fc-fri my-week-header" ><span>Fri</span></th>' +
                            '<th class="fc-day-header fc-widget-header fc-sat my-week-header" ><span>Sat</span></th>' +
                            '<th class="fc-day-header fc-widget-header fc-sun my-week-header" ><span>Sun</span></th>' +
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
                            orientation: "auto bottom"
                            // startDate: minDate
                        });

                        $("#dateScroller").change(function () {
                            calendar.gotoDate(new Date($(this).val()).toISOString());
                        })

                        $(".fc-right").css("z-index", 50);
                    }

                },
            }
        ;

        return en;

    }
))
;
