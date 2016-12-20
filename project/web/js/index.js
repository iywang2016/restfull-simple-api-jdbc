var serverHostName = window.location.hostname;
var serverProtocolName = window.location.protocol;
var portName = window.location.port;

var userName;
var password;

if (portName.length == 0) {
    portName = "80";
}

if (serverHostName === "localhost") {
    serverPath = serverProtocolName + "//" + serverHostName + ":" + portName;
}
else {
    serverPath = serverProtocolName + "//" + serverHostName;
}

function serverConnectFunc(serverUrl, jsonData) {
    $.ajax({
        url: "localhost:8080"+"/",
        type: 'POST',
        data: jsonData,

        dataType: 'json',
        async: true,

        success: function (event) {
            switch (event["answer"])
            {
                case "ok":
                    alert("success");
                    break;

                case "reg":
                    alert("registration succes, now confirm your password");
                    break;
                case "Retry":
                    alert("re-enter your password");
                    break;
                case "top10requests":
                    break;
                case "userDeleted":
                    alert("your acount was deleted, now you can sign in again");
                    break;
                case "userInformation":

                    break;
                case "language":
                    alert("");
                    break;
            }
        },
        error: function (xhr, status, error) {
            alert(error);
        }
    });
}

function logIn() {
    var jsonData = new Object();
    jsonData.command = "0";
    userName =  $('#UserName').val();
    password = $('#Password').val();
    jsonData.UserName = userName;
    jsonData.password = password;
    serverConnectFunc(serverPath, JSON.stringify(jsonData));
}

function top10Requests()
{
    var jsonData = new Object();
    jsonData.command = "1";
    serverConnectFunc(serverPath, JSON.stringify(jsonData));
}
function deleteUser() {
    var jsonData = new Object();
    jsonData.command = "2";
    jsonData.UserName = userName;
    serverConnectFunc(serverPath, JSON.stringify(jsonData));
}

function userInformation() {
    var jsonData = new Object();
    jsonData.command = "3";
    jsonData.UserName = userName;
    serverConnectFunc(serverPath, JSON.stringify(jsonData));
}

function getPassword() {
    var password = $('#Password').val();

}

function detectLanguage() {
    var jsonData = new Object();
    jsonData.command = "4";
    jsonData.text =  $('#Password').val();
    serverConnectFunc(serverPath, JSON.stringify(jsonData));
}