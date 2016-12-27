var serverHostName = window.location.hostname;
var serverProtocolName = window.location.protocol;
var portName = window.location.port;
//РАЗБИТЬ ПО ФАЙЛАМ ИЗСПОЛЬЗОВАТЬ map вместо case сделать класс user
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
        url: serverUrl,
        type: 'POST',
        data: jsonData,

        dataType: 'json',
        async: true,

        success: function (data) {
            switch (data["answer"])
            {
                case "ok":
                    alert("success");
                    top10Requests();
                    userInformation();
                    $("#").empty();
                    break;

                case "reg":
                    alert("registration succes, now confirm your password");
                    break;
                case "Retry":
                    alert("re-enter your password");
                    break;
                case "top10requests":
                    var keysList = data["top10Requests"].replace("[", ""). replace("]", "").split(",");
                    $("#top10requests").empty();

                    keysList.forEach(function(item, i, arr) {
                        $("#top10requests").append("<tr><td>" + item + "</td></tr>");
                    });
                    break;
                case "userDeleted":
                    alert("your acount was deleted, now you can sign in again");
                    break;
                case "usersInformation":
                    content = JSON.stringify(data);
                    console.log(typeof (data["timeRequested"][0]));
                    $("#timeRequested").empty();
                    $("#averagetime").empty();
                    $("#users").empty();
                    $("#date-time").empty();
                    
                    var keysList = data["timeRequested"].replace("[", ""). replace("]", "").split(",");
                    keysList.forEach(function(item, i, arr) {
                        $("#timeRequested").append("<tr>" + item + "</tr>");
                    });
                    
                    keysList = data["averagetime"].replace("[", ""). replace("]", "").split(",");
                    keysList.forEach(function(item, i, arr) {
                        $("#averagetime").append("<tr>" + item + "</tr>");
                    });
                    
                    keysList = data["UserName"].replace("[", ""). replace("]", "").split(",");
                    keysList.forEach(function(item, i, arr) {
                        $("#users").append("<tr>" + item + "</tr>");
                    });
                    
                    keysList = data["date-time"].replace("[", ""). replace("]", "").split(",");
                    keysList.forEach(function(item, i, arr) {
                        $("#date-time").append("<tr>" + item + "</tr>");
                    });
                    break;
                    
                case "language":
                    var probability = data["probability"].replace("[", ""). replace("]", "").split(",");
                    $("#myPopup").empty();

                    var language = data["language"].replace("[", ""). replace("]", "").split(",");
                    $("#myPopup").append(language+"  "+probability);
                    userInformation();
                    break;
            }
        },
        error: function (xhr, status, error) {
            alert("error");
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


function detectLanguage() {
    console.log("in detectLanguage");
    var jsonData = new Object();
    jsonData.command = "4";
    jsonData.UserName = userName;
    jsonData.word =  $('#detectLanguage').val();
    serverConnectFunc(serverPath, JSON.stringify(jsonData));
}

function popUp() {
    var popup = document.getElementById('myPopup');
    popup.classList.toggle('show');
}
