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
    stompClient.connect({'Token': 'eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJ3ZlNLbFRGT2cxVTRjSERPSkNERGotYUxDamtmSVczRDhqakZMNUE5U0g0In0.eyJleHAiOjE2OTM1MjI0NzgsImlhdCI6MTY5MzQ4NjQ4MCwiYXV0aF90aW1lIjoxNjkzNDg2NDc4LCJqdGkiOiIyNDUzZDY2YS02NjM4LTRlYjgtYTMwMS1lMmQ4NTcxOWQ4MTEiLCJpc3MiOiJodHRwczovL2F1dGgucmFtaWZpY2EuZXUvYXV0aC9yZWFsbXMvbWFzdGVyIiwic3ViIjoiNTY4Y2IxNDMtNzJjMC00MmIxLThlNmYtMTc4NGE1NTYzMWRjIiwidHlwIjoiQmVhcmVyIiwiYXpwIjoiYWRtaW4tY2xpIiwibm9uY2UiOiI2NjJjMTlhOC1kYmFhLTQzMGMtYmUwNS0wNTYyZmZmMjAwYTIiLCJzZXNzaW9uX3N0YXRlIjoiNDUyZGFmN2QtODk0Ny00OTliLTg0YmQtOGQ1NjMxZjlmNmNlIiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyIqIl0sInNjb3BlIjoib3BlbmlkIHByb2ZpbGUgZW1haWwiLCJzaWQiOiI0NTJkYWY3ZC04OTQ3LTQ5OWItODRiZC04ZDU2MzFmOWY2Y2UiLCJlbWFpbF92ZXJpZmllZCI6dHJ1ZSwibmFtZSI6IlJpY2FyZG8gQmF1bWFubiIsInByZWZlcnJlZF91c2VybmFtZSI6ImdlbWluaS5yaWNoYXJkQGdtYWlsLmNvbSIsImdpdmVuX25hbWUiOiJSaWNhcmRvIiwiZmFtaWx5X25hbWUiOiJCYXVtYW5uIiwiZW1haWwiOiJnZW1pbmkucmljaGFyZEBnbWFpbC5jb20ifQ.dbhj6pr6YF-hPMkDmmgrQEtBh-hOZoMoyQU2dUpUDzKWqplHCHaEuX7L2Va44JXiGnM_mSUyPtNerlXjCh2_4_iQrVx8gDwy8D2ev8ne0DrbCuywVyM1eLOR0BUvlDeYGfaEc-p8yRQn67YAX95Qt-8FA1SKYLlcKXrEejbikDpZfhpWdoULVijlM6lDSsbyWCQi2ktSjKNwk3DIY7xIBdlh9uol4EAxavtlBVX_faODK-GaraLmgO6G_aNfw0pyzWZWE7x3IShmW8kdy5V10FLq37RcfhF6fBBkwzJdXpXZ8GBwvPL_vEDplRyP6kDtaYbGip5wI_a9EflYN7'}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topics/user/568cb143-72c0-42b1-8e6f-1784a55631dc', function (greeting) {
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