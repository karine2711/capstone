$(document).ready(function () {
    const addActivityBtn = $('#add-activity-btn');
    const cancelBtn = $('#cancel-btn');
    cancelBtn.focus();
    cancelBtn.click(function () {
        $('input[type="text"]').val('')
        $('input[type="date"]').val('')
        $('#event-type').val('')
        window.location.href = location.origin + "/homepage";
    });

    $('#close').click(function () {
        $('input[type="text"]').val('')
        $('input[type="date"]').val('')
        $('input[type="select"]').val('')
        window.location.href = location.origin + "/homepage";
    });

    addActivityBtn.hover(function () {
        swapClass("#cancel-btn", "activity-main-button", "#add-activity-btn", "activity-whiteButton");
    }, function () {
    });
    addActivityBtn.focus(function () {
        swapClass("#cancel-btn", "activity-main-button", "#add-activity-btn", "activity-whiteButton");
    });
    cancelBtn.hover(function () {
        swapClass("#add-activity-btn", "activity-main-button", "#cancel-btn", "activity-whiteButton");
    }, function () {
        swapClass("#add-activity-btn", "activity-whiteButton", "#cancel-btn", "activity-main-button");
    });
    cancelBtn.focus(function () {
        swapClass("#add-activity-btn", "activity-main-button", "#cancel-btn", "activity-whiteButton");
    });

    function swapClass(selector1, selector1Class, selector2, selector2Class) {
        $(selector1).removeClass(selector1Class).addClass(selector2Class);
        $(selector2).removeClass(selector2Class).addClass(selector1Class);
    }
});