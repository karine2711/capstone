
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



var firebaseConfig = {
    apiKey: "AIzaSyClb5SUWfLCnQQNLHWP7S7M0LXJK0SKtX0",
    authDomain: "museum-booking-f4390.firebaseapp.com",
    projectId: "museum-booking-f4390",
    storageBucket: "museum-booking-f4390.appspot.com",
    messagingSenderId: "303409808583",
    appId: "1:303409808583:web:36fdb48b6c1ced4fa26ae2"
};

firebase.initializeApp(firebaseConfig);



let messaging = firebase.messaging();
console.log("inside worker")

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
// Get registration token. Initially this makes a network call, once retrieved
// subsequent calls to getToken will return from cache.
messaging.getToken({ vapidKey: 'BK82wugEJsBAflivo-4HiqOkiyDOZkG7_SPBXG9daKGl8bAl6n7lpkvGXZY68BNXJfg5AESEQL1X3EZoJzo2Q7A' }).then((currentToken) => {
  if (currentToken) {
    sendToken(token);
  } else {
    // Show permission request UI
    console.log('No registration token available. Request permission to generate one.');
    // ...
  }
}).catch((err) => {
  console.log('An error occurred while retrieving token. ', err);
  // ...
});
    // function doFireNotification() {
//    if ('serviceWorker' in navigator) {
//        let registration = navigator.serviceWorker.register('js/firebase-messaging-sw.js');
//        var firebaseConfig = {
//            apiKey: "AIzaSyClb5SUWfLCnQQNLHWP7S7M0LXJK0SKtX0",
//            authDomain: "museum-booking-f4390.firebaseapp.com",
//            projectId: "museum-booking-f4390",
//            storageBucket: "museum-booking-f4390.appspot.com",
//            messagingSenderId: "303409808583",
//            appId: "1:303409808583:web:36fdb48b6c1ced4fa26ae2"
//        };
//        let app;
//        if (!(firebase.apps.length)) {
//            app = firebase.initializeApp(firebaseConfig);
//        }
//        let messaging = firebase.messaging(app);
//
//        registration.then(r => {
//            messaging.useServiceWorker(r);
//            messaging.usePublicVapidKey('BK82wugEJsBAflivo-4HiqOkiyDOZkG7_SPBXG9daKGl8bAl6n7lpkvGXZY68BNXJfg5AESEQL1X3EZoJzo2Q7A');
//            /* messaging.onTokenRefresh(function () {
//                 // let refreshedToken = messaging.getToken();
//                 console.log("--------------------------------------------------------------------------------")
//                 console.log("-------------->>>token was refreshed")
//                 // console.log(refreshedToken)
//             });*/
//
//            // function deleteToken(tokenToDelete){
//            //     messaging.deleteToken(tokenToDelete);
//            // }
//
////            todo: delete console loggd
//            Notification.requestPermission()
//                .then(function () {
//                    console.log('Notification permission granted.');
//                    return messaging.getToken();
//                })
//                .then(function (token) {
//                    // console.log("old token", token)
//                    // setTimeout(function () {
//                    //     messaging.deleteToken(token).then((e)=>{
//                    //         console.log("deleted", e)
//                    //     });
//                    // }, 7000)
//                    sendToken(token);
//                     console.log('new token', token); // Display user token
//                })
//                .catch(function (err) { // Happen if user deny permission
//
//                   console.log('Unable to get permission to notify.', err);
//                });
//
//            let unsub = messaging.onMessage(function (payload) {
//                console.log('onMessage', payload);
//            });
//            // unsub = function(){
//            //     console.log('unsubcribe handler is running')
//            // }
//            // setTimeout(function () {
//            //     unsub()
//            //     console.log('unsubscribed', unsub)
//            // }, 10000)
//        });
//        /* messaging.onTokenRefresh(function () {
//             // let refreshedToken = messaging.getToken();
//             console.log("--------------------------------------------------------------------------------")
//             console.log("-------------->>>token was refreshed222")
//             // console.log(refreshedToken)
//         });*/
//        // }
//        // doFireNotification();
//    }

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
