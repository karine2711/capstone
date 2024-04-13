
//importScripts('https://www.gstatic.com/firebasejs/10.0.0/firebase-app.js');
//importScripts('https://www.gstatic.com/firebasejs/10.0.0/firebase-messaging.js');
importScripts("https://www.gstatic.com/firebasejs/7.15.4/firebase-app.js")
importScripts("https://www.gstatic.com/firebasejs/7.15.4/firebase-messaging.js")

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



/*
messaging.setBackgroundMessageHandler(function (payload) {
    console.log("inside worker handler")
    console.log(payload)
    const title = 'Hello world';
    const options = {
        body: payload.data.name
    };

    return self.registration.showNotification(title, options);
});
*/
// self.addEventListener('notificationclick', function(event) {
//     self.clients.openWindow('/homepage')
// }, false);
messaging.onBackgroundMessage((payload) => {
  console.log(
    '[firebase-messaging-sw.js] Received background message ',
    payload
  );
  // Customize notification here
  const notificationTitle = 'Background Message Title';
  const notificationOptions = {
    body: 'Background Message body.',

  };

  self.registration.showNotification(notificationTitle, notificationOptions);
});

messaging.onMessage((payload) => {
  console.log(
    '[firebase-messaging-sw.js] Received background message ',
    payload
  );
  // Customize notification here
  const notificationTitle = 'Background Message Title';
  const notificationOptions = {
    body: 'Background Message body.',

  };

  self.registration.showNotification(notificationTitle, notificationOptions);
});