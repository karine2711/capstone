$(document).ready(function () {
    // Enable Carousel Indicators
    let title = $('.title');
    $(".item1").click(function () {
        $("#first-carousel").carousel(0);
        $("#second-carousel").carousel(0);
    });
    $(".item2").click(function () {
        $("#first-carousel").carousel(1);
        $("#second-carousel").carousel(1);
    });
    $(".item3").click(function () {
        $("#first-carousel").carousel(2);
        $("#second-carousel").carousel(2);
    });
    $(".item4").click(function () {
        $("#first-carousel").carousel(3);
        $("#second-carousel").carousel(3);
    });


    const signInBtn = $('#sign-in-btn');
    const signUpBtn = $('#sign-up-btn');

    signUpBtn.focus();
    signUpBtn.click(function () {
        window.location.href = "/register";
    });

    signInBtn.click(function () {
        window.location.href = "/login";
    });

    signInBtn.hover(function () {
        swapClass("#sign-up-btn", "main_button", "#sign-in-btn", "whiteButton");
    }, function () {
        swapClass("#sign-in-btn", "main_button", "#sign-up-btn", "whiteButton");
    });

    signInBtn.focus(function () {
        swapClass("#sign-up-btn", "main_button", "#sign-in-btn", "whiteButton");
    });

    signUpBtn.hover(function () {
        swapClass("#sign-in-btn", "main_button", "#sign-up-btn", "whiteButton");
    }, function () {
    });

    signUpBtn.focus(function () {
        swapClass("#sign-in-btn", "main_button", "#sign-up-btn", "whiteButton");
    });

    $.ajax({
        type: 'GET',
        url: "/title",
        success: function (data) {
            title.html(data);
        },
    });

    function swapClass(selector1, selector1Class, selector2, selector2Class) {
        $(selector1).removeClass(selector1Class).addClass(selector2Class);
        $(selector2).removeClass(selector2Class).addClass(selector1Class);
    }
});


