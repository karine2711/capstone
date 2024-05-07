$(document).ready(async function () {
    let image = $('#image');
    const csrfHeader = "X-CSRF-TOKEN";
    let csrfToken = $('input[name^="csrf_token"]').val();
    let username = $('#fc-username').text();

    await $.i18n().load({
        "en": "/i18n/en.json",
        "hy": "/i18n/hy.json",
        "ru": "/i18n/ru.json",
    });

    let localeValue = $("#locale").val();
    $.i18n().locale = localeValue;

    $.ajax({
        type: 'GET',
        url: location.origin + '/edit-profile/avatar/' + username,
        beforeSend: function (request) {
            request.setRequestHeader(csrfHeader, csrfToken);
        },
        success: function (data, textStatus, xhr) {
            if (xhr.status === 204) image.attr('src', 'images/no-avatar.png');
            else image.attr('src', 'data:image/jpg;base64,' + data)
        },
        error: function () {
            image.attr('src', 'images/no-avatar.png');
        }
    });


    if (location.pathname === "/myActivities") {
        $('#activities').removeClass("select-tab-border");
        $("#my-activities").addClass("select-tab-border");
    } else if (location.pathname === "/homepage") {
        $('#activities').addClass("select-tab-border");
        $("#my-activities").removeClass("select-tab-border");
    } else if (location.pathname === "/homepage/update-content") {
        $('#activities').removeClass("select-tab-border");
        $("#update-content").addClass("select-tab-border");
    } else if (location.pathname === "/users") {
        $('#users').addClass("select-tab-border");
        $('#activities').removeClass("select-tab-border");
    } else if (location.pathname === "/waiting-list") {
        $('#waiting-list').addClass("select-tab-border");
        $('#activities').removeClass("select-tab-border");
    } else if (location.pathname === "/events") {
        $('#events').addClass("select-tab-border");
        $('#activities').removeClass("select-tab-border");
    }


    $('#activities').click(function () {
            if (source != null) {
                $(this).addClass("select-tab-border");
                $('#my-activities').removeClass("select-tab-border");

                myActivities = null;
                filterActivities();
            }
        }
    );

    $('#update-content').click(function () {
        $('#activities').removeClass("select-tab-border");
    });


    function handleNotifications() {
        $.ajax({
            type: 'GET',
            url: location.origin + '/notification/unseen/' + username,
            beforeSend: function (request) {
                request.setRequestHeader(csrfHeader, csrfToken);
            },
            success: function (data, textStatus, xhr) {
                let unseen = 0;
                let ids = [];

                if (data && data.length) {
                    // console.log('logged in user notifications', data);
                    let count = 0;
                    for (let notification in data) {
                        if (data[notification].seen === false) {
                            unseen++;
                        }
                        if (!data[notification].shown) {
                            ids.push(data[notification].id);
                        }
                        // console.log(count);
                        if (count < 3) {
                            if (data[notification].shown === false) {
                                if (data[notification].body) {
                                    notificateUserInsideApp(data[notification].title, data[notification].body);
                                } else {
                                    notificateUserInsideAppWithTitleOnly(data[notification].title)
                                }
                                count++;
                            }
                        }
                    }
                    if (ids.length) {
                        setNotificationsShown(ids);
                        // console.log("notifications should be set to shown", ids)
                    }
                }
                showUnseenNotificationCount(unseen);
            },
            error: function () {
                // console.log('error was thrown while fetching notifications');
            }
        });
    }

    handleNotifications();
    setInterval(handleNotifications, 10000);

    function notificateUserInsideApp(title, body) {
        $.notify({
            title: title,
            message: body
        }, {
            type: 'minimalist',
            onShown: addSound(),
            onClosed: setTimeout(mute, 10000),
            newest_on_top: true,
            animate: {
                enter: "animated fadeInRight",
                exit: "animated fadeOutRight"
            },
            delay: 10000,
            timer: 50,
            offset: {
                x: 18,
                y: 110
            },
            mouse_over: 'pause',
            template:
                '<div  data-notify="container" class="row alert alert-{0} notification-popup"  role="alert">' +
                '<div  class="pl-0 left" style="width: 75%">' +
                '<span data-notify="title" class="title notification-custom-title">{1}</span>' +
                '<span data-notify="message" class="message">{2}</span>' +
                '</div>' +
                '<div class="right" >' +
                '<button onclick="window.location.href= \'/notification\'" name="notification_button"  id="look" ></button>' +
                '</div>' +
                '</div>'
        });
        $("button[name*='notification_button']").text($.i18n("notification.take.look"));
    }


    function notificateUserInsideAppWithTitleOnly(title) {
        $.notify({
            title: title,
        }, {
            type: 'minimalist',
            newest_on_top: true,
            animate: {
                enter: "animated fadeInRight",
                exit: "animated fadeOutRight"
            },
            delay: 10000,
            timer: 50,
            offset: {
                x: 18,
                y: 110
            },
            mouse_over: 'pause',
            onShown: addSound(),
            onClosed: setTimeout(mute, 10000),
            template:
                '<div  data-notify="container" class="row alert alert-{0} notification-popup"  role="alert">' +
                '<div  class="pl-0 left" style="width: 75%" >' +
                '<span data-notify="title" class="title notification-custom-title">{1}</span>' +
                '</div>' +
                '<div class="right" >' +
                '<button onclick="window.location.href= \'/notification\'" name="notification_button"  id="look" ></button>' +
                '</div>' +
                '</div>'
        });
        $("button[name*='notification_button']").text($.i18n("notification.take.look"));
        $(".notification-custom-title").css({
            "margin": 0,
            "width": "70%",
            "position": "absolute",
            "top": "50%",
            "-ms-transform": " translateY(-50%)",
            "transform": "translateY(-50%)"
        });
    }

    var audio = document.getElementById('notificationSound');

    function addSound() {
        audio.muted = false;
        audio.volume = 1.0;
        audio.play();
    }

    function mute() {
        audio.currentTime = 0;
        audio.pause();
    }

    function showUnseenNotificationCount(unseen) {
        let badge = $("span[name*='badge']");
        if (unseen === 0) {
            badge.addClass("invisible");
        } else {
            badge.removeClass("invisible");
            badge.text(unseen);
        }
    }

    function setNotificationsShown(idArray) {
        $.ajax({
            type: 'PUT',
            url: location.origin + '/notification/' + username,
            data: JSON.stringify(idArray),
            contentType: 'application/json',
            beforeSend: function (request) {
                request.setRequestHeader(csrfHeader, csrfToken);
            },
            success: function (data, textStatus, xhr) {
                // console.log('logged in user notifications', data);
            },
            error: function () {
                // console.log('error was thrown while setting notifications seen');
            }
        });
    }

    function sendToken(tokenToSend) {
        $.ajax({
            type: "POST",
            beforeSend: function (request) {
                request.setRequestHeader(csrfHeader, csrfToken);
            },
            url: location.origin + "/notification/post/" + tokenToSend,
            success: function (data) {
                // console.log("Sent token to server", data);
            },
            error: function (e, d, f) {
                // console.log("Failed to send token", e, d, f);
            }
        });
    }

    $("#my-activities").click(function () {
        saveViewDateAndMoveTo("/myActivities");
    });

    $("#users").click(function () {
        saveViewDateAndMoveTo("/users");
    })

    $("#activities").click(function () {
        saveViewDateAndMoveTo("/");
    });

    $("#update-content").click(function () {
        saveViewDateAndMoveTo("/homepage/update-content")
    })

    $("#waiting-list").click(function () {
        saveViewDateAndMoveTo("/waiting-list")
    })
});

function saveViewDateAndMoveTo(endpoint) {
    if (typeof (calendar) != "undefined") {
        sessionStorage.setItem("view", calendar.view.type);
        sessionStorage.setItem("calendarCurrentDate", calendar.getDate());
    }
    location.href = location.origin + endpoint;
}
