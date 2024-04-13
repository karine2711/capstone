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
                eventRender: function (arg) {
                    let {event, view, el} = arg;
                    let {id, start, end, title, extendedProps} = event;
                    let {count, type, desc, groupSizes, username, eventState} = extendedProps;
                    let circleType = getCircleType(arg.view, arg.event);
                    let circle = "<span  class='circle " + circleType + "'></span>";
                    let sum = count;
                    groupSizes.forEach(s => sum = sum + s);
                    addMore("Show more", "Show less", "Daily Activities");

                    if (view.type === 'listDay') {
                        let myType = type.replace(type.charAt(0), type.charAt(0).toUpperCase());
                        $(el).find('.fc-list-item-marker').remove();
                        let smth = "<div class='show-time align-items-center'>" +
                            "<span class='col-2'>" + formatAMPM(start) + "</span>" + "<hr style='transform: translate(-10px, -25px)' class='col-10'>" +
                            "</div>" +
                            "<div class='content-div' style='line-height: 25px'> " +
                            "<img alt='event photo' class='event-photo d-none' id='img_" + id + "'/>";
                        if (count != null) smth = smth + "<div class='row'><p class='for-who'>For groups of " + sum + " people </p>";

                        if (groupSizes.length > 0) smth = smth + "<span class=\"group-sign\" style=\"color: #BDBDBD; margin-left: 20px\"><i class=\"fa fa-users group-icon\" aria-hidden=\"true\"></i></span>";
                        smth = smth + "</div>";
                        smth = smth +
                            "<div class='row'><p class='title'>" + title + "</p></div>" +
                            "<div class='row'>" +
                            "<div class='col-3 p-0'><span class='hours'>" + formatAMPM(start) + "-" + formatAMPM(end) + "</span></div>" +
                            "<div class='col-8'><span>" + circle + " " + myType + "</span></div>" +
                            "</div>" +
                            "<div class='row' style='margin-top: 16px;'><p class='description'>" + desc + "</p>" + "</div></div>"

                        ;
                        if (+new Date(start) >= +new Date()) {

                            if ($('#fc-username').html() == event.extendedProps.username || $("#admin").length > 0) {
                                smth = smth.substring(0, smth.length - 6);
                                smth = smth + " <div class='row w-75'>";
                                let remainsLessThan48Hours = new Date(event.start) - new Date() < (1000 * 60 * 60 * 48);
                                if (remainsLessThan48Hours) {
                                    let disabled = event.extendedProps.confirmed ? "disabled" : "";
                                    smth = smth + "<div class='col-3 p-0'>" +
                                        "<button id='confirmBtn' onclick='confirmEvent(" + id + ")' " + disabled +
                                        " class='btn activity-main-button w-75' style='padding: 3px 10px'>Confirm</button>" +
                                        "</div>";
                                }
                                smth = smth + "<div class='col-3 p-0'>" +
                                    "<button onclick='drawReschedulePopup(" + id + ", $(\"#calendar\"))'" +
                                    " class='btn activity-whiteButton w-75' style='padding: 3px 10px'>Reschedule</button>" +
                                    "</div>" +
                                    "<div class='col-3 p-0' onclick='sendDeleteRequest(" + id + ", $(\"#calendar\"))'>" +
                                    "<button class='btn activity-whiteButton w-75' style='padding: 3px 10px'>Delete</button>" +
                                    "</div>";

                                smth = smth + "</div></div>";
                            }
                        }

                        $(el).find('.fc-title, .fc-list-item-title')
                            .html(smth
                            );

                        getEventPhoto(id)

                        groupsPopover(el, count, groupSizes, username)

                        return;

                    } else if (view.type === 'timeGridWeek' || view.type === 'timeGridDay'
                    ) {
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
                            $(el).append('<span class="group-sign" style="color: white; position:absolute; bottom: 4px; right: 10px">' + '<i class="fa fa-users group-icon" aria-hidden="true"></i>' + '</span>');

                            groupsPopover(el, count, groupSizes, username)
                        }
                    } else {
                        addCircle(view, event, el);
                        addEventId(event, el);
                        if (type.toLowerCase() === 'event') {
                            addEventDesc(event, el, "Show more");
                            makePopover(el, event, "Show more", "Show less")
                        } else if (groupSizes.length > 0) {
                            addGroupSign(event, el,);
                            groupsPopover(el, count, groupSizes, username)
                        }
                    }
                    if (+new Date(start) >= +new Date()) {
                        if ($('#fc-username').html() == event.extendedProps.username && event.extendedProps.eventTypeId != 7) {
                            addContextMenu(el, "Reschedule", "Delete", "Confirm",);
                        }
                        if ($("#admin").length > 0 && event.extendedProps.eventTypeId == 7) {
                            addContextMenu(el, "Reschedule", "Delete", null, "Update");
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
                    $('.fc-slats tbody tr').last().children('.fc-axis').append("<span style='position: relative; top: 300%; display: block;'>4:00pm</span>");


                    $("#dateScroller").datepicker('setDate', formatDate(calendar.getDate()));
                }
            }
        ;

        return en;

    }
))
;