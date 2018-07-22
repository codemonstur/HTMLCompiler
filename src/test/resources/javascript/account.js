function setMenuStyle() {
    $("body").removeClass("nav-sm");
    $("body").removeClass("nav-md");
    $("body").addClass(orDefault(localStorage.getItem('nav'), "nav-sm"));
}
function orDefault(value, _default) {
    if (value == null || value == '') return _default;
    return value;
}

$(document).ready(function() {
    setMenuStyle();

    $.ajax({
        type: "GET",
        url: "/api/v1/csrf",
        success: function( data, textStatus, jqXHR ) {
            $.ajaxSetup({
                headers: { 'X-CSRF': jqXHR.responseText }
            });

            $.get( "/api/v1/museum", function( data ) {
                $( "#menu-tours" ).empty();
                $( "#menu-tours" ).html( $( "#template-menu-tour" ).render( data.tours ) );
            });

            $.get( "/api/v1/account", function( data ) {
                $( ".username" ).each(function(){
                    $(this).text(data.name)
                });
                $( "#form-name" ).attr("placeholder", data.name);
                $( "#last-login" ).text(new Date(data.lastLogin).toLocaleString());
                $( "#created-date" ).text(new Date(data.createdDate).toLocaleString());
                $( "#email" ).text(data.email);
                $( "#form-email" ).attr("placeholder", data.email);
                $( "#account-type" ).text(getStringForType(data.accountType));
                $( "#balance" ).text(data.balance / 100);
            });
        },
    });
});

function getStringForType(type) {
    return (type) ? "Free account" : "Paid account";
}

$("#btnLogout").click(function() {
    $.ajax({
        type: "POST",
        url: "/api/v1/logout",
        success: function( data ) {
            window.location.href = "/";
        },
        error: function (xhr, ajaxOptions, thrownError) {
            console.log("error");
            console.log(xhr.responseText);
        }
    });
    return false;
});

$("#btnUpload").click(function() {
    $("#file").click();
});
$("#file").on('change', function(evt) {
    file = evt.target.files[0];
    if (file != undefined) {
        $("#btnUpload").append( "<div class='loader'></div>" );
        $.ajax({
            method: 'PUT',
            url: '/api/v1/avatar',
            processData: false,
            contentType: false,
            data: file
        }).done(function () {
            $("#btnUpload").empty();
            $("#imgAvatar").attr("src", "/api/v1/avatar?" + new Date().getTime());
        });
    }
});

$("#btnChangeName").click(function() {
    name = $('#form-name').val();
    if (name == null || name == '') return false;

    $.ajax({
        type: "POST",
        url: "/api/v1/account",
        data: { 'name': name },
        success: function( data ) {
            location.reload();
        },
        error: function (xhr, ajaxOptions, thrownError) {
            console.log("error");
            console.log(xhr.responseText);
        }
    });
    return false;
});

$("#btnChangePassword").click(function() {
    new1 = $('#pass-new-one').val();
    new2 = $('#pass-new-two').val();

    if (new1 == null || new1 == '' || new1 != new2) return false;

    $.ajax({
        type: "POST",
        url: "/api/v1/password",
        data: { 'current': $('#pass-current').val(), 'newpass': new1 },
        success: function( data ) {
            location.reload();
        },
        error: function (xhr, ajaxOptions, thrownError) {
            console.log("error");
            console.log(xhr.responseText);
        }
    });
    return false;
});

$("#btnChangeEmail").click(function() {
    $.ajax({
        type: "POST",
        url: "/api/v1/email",
        data: { 'password': $('#password').val(), 'email': $('#form-email').val() },
        success: function( data ) {
            location.reload();
        },
        error: function (xhr, ajaxOptions, thrownError) {
            console.log("error");
            console.log(xhr.responseText);
        }
    });
    return false;
});
