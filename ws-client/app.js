var stompClient = null;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    } else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

function connect() {
    //var socket = new SockJS('http://localhost:8080/ws');
    var socket = new SockJS('http://localhost:8080/ws');
    stompClient = Stomp.over(socket);
    stompClient.connect({'Token': 'eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJ1eVg5U0o4eVNJTjlzZVBFSnlKY0ttWlZIMlpFYXg1X1EtQXgzYUpvUGFnIn0.eyJleHAiOjE2ODk2OTg4ODUsImlhdCI6MTY4OTY2NDAzOCwiYXV0aF90aW1lIjoxNjg5NjYyODg2LCJqdGkiOiJkMDIzOGQ2My03MTAxLTQzMjktODk3My1lZjQzY2ZmOTVlOTUiLCJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjI4MDgwL2F1dGgvcmVhbG1zL21hc3RlciIsInN1YiI6ImM2Mjk3NzNlLTVmODUtNDczMS1iZGZjLWEyNmYzOWYxYWRkOCIsInR5cCI6IkJlYXJlciIsImF6cCI6ImFkbWluLWNsaSIsInNlc3Npb25fc3RhdGUiOiIwOWRkYzE5Ni0xYjM2LTQ4NzctOWVjYy0xNDgxM2ZmYjE5MzkiLCJhY3IiOiIwIiwiYWxsb3dlZC1vcmlnaW5zIjpbIioiXSwic2NvcGUiOiJwcm9maWxlIGVtYWlsIiwic2lkIjoiMDlkZGMxOTYtMWIzNi00ODc3LTllY2MtMTQ4MTNmZmIxOTM5IiwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJuYW1lIjoiUmljYXJkbyBCYXVtYW5uIiwicHJlZmVycmVkX3VzZXJuYW1lIjoiYWRtaW4iLCJnaXZlbl9uYW1lIjoiUmljYXJkbyIsImZhbWlseV9uYW1lIjoiQmF1bWFubiJ9.cMg5zWlS9Mg3Ar_8XTJm45sZec7tb5NWuvkTPmhBX57kHQ2VXimbsxGiedWc8wI5nMlVyHiPXnWHI54Hem3mE8ajPOA5SCtFHgQfx6Kc8eLN_KLWHgO8LxdbuMgliVnAoM0PrkGgHG7FhSvd6qI1taiFlfrOghL-UZnK_chWbhn6rGFbD_BbMKW4gzPNC8PmcAi5cJ4OI2GIax3W7OkgZ-8YN0bxr9yzqtQCh13sNJbsIZn4CPEqOzmkNJbRQQpWEaoiN6RxZy-jMYCz4dwNt6-uWM1sAsCuY7wxLQHKjwvc8IYrGbOevIElIBiTayAzOk71fTMfGvOLi8f73QS6NA'}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topics/user/c629773e-5f85-4731-bdfc-a26f39f1add8', function (greeting) {
            console.log(greeting)
        });
    });

    displayStompObject();
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function showGreeting(message) {
    $("#greetings").append("<tr><td>" + message + "</td></tr>");
}

function sendName() {
    stompClient.send("/app/hello", {}, JSON.stringify({
        'name': $("#name").val()
    }));
}


$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $("#connect").click(function () {
        connect();
    });
    $("#disconnect").click(function () {
        disconnect();
    });
    $("#send").click(function () {
        sendName();
    });
});