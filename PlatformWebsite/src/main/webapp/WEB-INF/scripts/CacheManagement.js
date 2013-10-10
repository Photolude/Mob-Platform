function createCookie(name, value, days) {
    if (days) {
        var date = new Date();
        date.setTime(date.getTime() + (days * 24 * 60 * 60 * 1000));
        var expires = "; expires=" + date.toGMTString();
    }
    else var expires = "";
    document.cookie = name + "=" + value + expires + "; path=/";
}

function readCookie(name) {
    var nameEQ = name + "=";
    var ca = document.cookie.split(';');
    for (var i = 0; i < ca.length; i++) {
        var c = ca[i];
        while (c.charAt(0) == ' ') c = c.substring(1, c.length);
        if (c.indexOf(nameEQ) == 0) return c.substring(nameEQ.length, c.length);
    }
    return null;
}

function Get_Cookie(name) {

    var start = document.cookie.indexOf(name + "=");
    var len = start + name.length + 1;
    if ((!start) &&
(name != document.cookie.substring(0, name.length))) {
        return null;
    }
    if (start == -1) return null;
    var end = document.cookie.indexOf(";", len);
    if (end == -1) end = document.cookie.length;
    return unescape(document.cookie.substring(len, end));
}

function Delete_Cookie(name, path, domain) {
    if (Get_Cookie(name)) document.cookie = name + "=" +
    	((path) ? ";path=" + path : "") +
    	((domain) ? ";domain=" + domain : "") +
    	";expires=Thu, 01-Jan-1970 00:00:01 GMT";
}

function FormatDate(date) {
    var month = date.getMonth() + 1;
    var day = date.getDate();
    var year = date.getFullYear();
    return month + "/" + day + "/" + year;
}

function FormatDateTime(date) {
    var hour = date.getHours();
    var minute = date.getMinutes();
    var dayHalf = "am";

    if (minute < 10) {
        minute = "0" + minute;
    }

    if (hour > 12) {
        dayHalf = "pm";
    }

    return FormatDate(date) + " " + hour + ":" + minute + " " + dayHalf;
}

function htmlEncode(value) {
    return $('<div/>').text(value).html();
}
