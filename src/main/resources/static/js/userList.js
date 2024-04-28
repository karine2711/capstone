$(document).ready(async function () {

    await $.i18n().load({
        "en": "/i18n/en.json",
        "hy": "/i18n/hy.json",
        "ru": "/i18n/ru.json",
    });


    $.extend($.fn.dataTableExt.oStdClasses, {
        'sPageNumber': 'paginate_number',
        'sPageNumbers': 'paginate_numbers'
    });

    $.fn.dataTableExt.oPagination.twoNumbers = {
        'oDefaults': {
            'iShowPages': 2
        },
        //for changing the page
        'fnClickHandler': function (e) {
            var fnCallbackDraw = e.data.fnCallbackDraw,
                oSettings = e.data.oSettings,
                sPage = e.data.sPage;

            if ($(this).is('[disabled]')) {
                return false;
            }

            oSettings.oApi._fnPageChange(oSettings, sPage);
            fnCallbackDraw(oSettings);

            return true;
        },
        // fnInit is called once for each instance of pager
        //before loading only called once
        'fnInit': function (oSettings, nPager, fnCallbackDraw) {
            var oClasses = oSettings.oClasses,
                oLang = oSettings.oLanguage.oPaginate,
                that = this;

            var iShowPages = oSettings.oInit.iShowPages || this.oDefaults.iShowPages,
                iShowPagesHalf = Math.floor(iShowPages / 2);

            $.extend(oSettings, {
                _iShowPages: iShowPages,
                _iShowPagesHalf: iShowPagesHalf,
            });

            var oFirst = $('<a class="' + oClasses.sPageButton + ' ' + oClasses.sPageFirst + '" style="cursor: pointer">' + oLang.sFirst + '</a>'),
                oPrevious = $('<a class="' + oClasses.sPageButton + ' ' + oClasses.sPagePrevious + '" style="cursor: pointer">' + oLang.sPrevious + '</a>'),
                oNumbers = $('<span class="' + oClasses.sPageNumbers + '"></span>'),
                oNext = $('<a class="' + oClasses.sPageButton + ' ' + oClasses.sPageNext + '" style="cursor: pointer">' + oLang.sNext + '</a>'),
                oLast = $('<a class="' + oClasses.sPageButton + ' ' + oClasses.sPageLast + '">' + oLang.sLast + '</a>');

            oFirst.click({
                'fnCallbackDraw': fnCallbackDraw,
                'oSettings': oSettings,
                'sPage': 'first'
            }, that.fnClickHandler);
            oPrevious.click({
                'fnCallbackDraw': fnCallbackDraw,
                'oSettings': oSettings,
                'sPage': 'previous'
            }, that.fnClickHandler);
            oNext.click({
                'fnCallbackDraw': fnCallbackDraw,
                'oSettings': oSettings,
                'sPage': 'next'
            }, that.fnClickHandler);
            oLast.click({
                'fnCallbackDraw': fnCallbackDraw,
                'oSettings': oSettings,
                'sPage': 'last'
            }, that.fnClickHandler);

            // Draw
            $(nPager).append(oPrevious, oNumbers, oNext);
        },
        // fnUpdate is only called once while table is rendered
        'fnUpdate': function (oSettings, fnCallbackDraw) {
            var oClasses = oSettings.oClasses,
                that = this;

            var tableWrapper = oSettings.nTableWrapper;

            // Update stateful properties
            this.fnUpdateState(oSettings);

            if (oSettings._iCurrentPage === 1) {
                $('.' + oClasses.sPageFirst, tableWrapper).attr('disabled', true);
                $('.' + oClasses.sPagePrevious, tableWrapper).attr('disabled', true);
            } else {
                $('.' + oClasses.sPageFirst, tableWrapper).attr('disabled', false);
                $('.' + oClasses.sPagePrevious, tableWrapper).attr('disabled', false);
            }

            if (oSettings._iTotalPages === 0 || oSettings._iCurrentPage === oSettings._iTotalPages) {
                $('.' + oClasses.sPageNext, tableWrapper).attr('disabled', true);
                $('.' + oClasses.sPageLast, tableWrapper).attr('disabled', true);
                $('.' + oClasses.sPageFirst, tableWrapper).attr('disabled', false);
                $('.' + oClasses.sPagePrevious, tableWrapper).attr('disabled', false);
            } else {
                $('.' + oClasses.sPageNext, tableWrapper).attr('disabled', false);
                $('.' + oClasses.sPageLast, tableWrapper).attr('disabled', false);
                $('.' + oClasses.sPageFirst, tableWrapper).attr('disabled', false);
                $('.' + oClasses.sPagePrevious, tableWrapper).attr('disabled', false);
            }

            var i, oNumber, oNumbers = $('.' + oClasses.sPageNumbers, tableWrapper);

            // Erase
            oNumbers.html('');

            for (i = oSettings._iFirstPage; i <= oSettings._iLastPage; i++) {
                oNumber = $('<button class="' + oClasses.sPageButton + ' ' + oClasses.sPageNumber + '">' + oSettings.fnFormatNumber(i) + '</button>');

                if (oSettings._iCurrentPage === i) {
                    oNumber.attr('active', true).attr('disabled', true);
                    oNumber.addClass("activePage");
                } else {
                    oNumber.click({
                        'fnCallbackDraw': fnCallbackDraw,
                        'oSettings': oSettings,
                        'sPage': i - 1
                    }, that.fnClickHandler);
                }

                // Draw
                oNumbers.append(oNumber);
            }
        },
        // fnUpdateState used to be part of fnUpdate
        // The reason for moving is so we can access current state info before fnUpdate is called
        'fnUpdateState': function (oSettings) {
            var iCurrentPage = Math.ceil((oSettings._iDisplayStart + 1) / oSettings._iDisplayLength),
                iTotalPages = Math.ceil(oSettings.fnRecordsDisplay() / oSettings._iDisplayLength),
                iFirstPage = iCurrentPage,
                iLastPage = iCurrentPage + oSettings._iShowPagesHalf;

            if (iTotalPages < oSettings._iShowPages) {
                iFirstPage = 1;
                iLastPage = iTotalPages;
            } else if (iFirstPage < 1) {
                iFirstPage = 1;
                iLastPage = oSettings._iShowPages;
            } else if (iLastPage > iTotalPages) {
                iFirstPage = (iTotalPages - oSettings._iShowPages) + 1;
                iLastPage = iTotalPages;
            }

            $.extend(oSettings, {
                _iCurrentPage: iCurrentPage,
                _iTotalPages: iTotalPages,
                _iFirstPage: iFirstPage,
                _iLastPage: iLastPage
            });
        }
    };

    $.fn.dataTable.ext.search.push(
        function( settings, data, dataIndex ) {
            var search = $("#myTable_filter input").val();
            var title = data[1];

            return data[0].toLowerCase().startsWith(search.toLocaleLowerCase())
                || data[1].toLowerCase().startsWith(search.toLocaleLowerCase())
                || data[2].toLowerCase().startsWith(search.toLocaleLowerCase())
                || data[3].toLowerCase().startsWith(search.toLocaleLowerCase());
        }
    );

    let dtable = $("#myTable").DataTable({
        "bLengthChange": false,
        "order": [[3, 'asc'], [1, 'asc']],
        "initComplete": function (settings, json) {
            let word = sessionStorage.getItem('userName');
            if (word != null) {
                this.api().search(word).draw();
                sessionStorage.removeItem('userName');
            }
        },
        "pageLength": 25,
        "infoCallback": function (settings, start, end, max, total, pre) {
            return $.i18n("list.users") + " " + +start + "-" + end + " " + $.i18n("list.from") + " " + total + $.i18n("list.form.arm");

        },
        'sPaginationType': 'twoNumbers',
        language: {
            searchPlaceholder: $.i18n("list.search"),
            search: "",
            paginate: {
                next: '>',
                previous: '<'
            }
        },
        "aoColumns": [
            {"orderSequence": ["asc"]},
            {"orderSequence": ["asc"]},
            {"orderSequence": ["asc"]},
            {"orderSequence": ["asc"]},
            {"orderSequence": ["asc"]},
            {"orderSequence": ["asc"]},
            {"orderSequence": ["asc"]},
        ],
        columnDefs: [
            {
                render: function (data, type, full, meta) {
                    return "<div class='text-wrap width-200'>" + data + "</div>";
                },
                targets: [0,2]
            }
        ]
    });
    $("#myTable_filter input")
        .unbind()
        .bind("input", function (e) {
            if (this.value.length >= 0 || e.keyCode == 13) {
                dtable.search(this.value,false,true).draw();
                $('.goToPageInput').val(Math.ceil(dtable.page.info().recordsDisplay / dtable.page.info().length));
            }
            if (this.value == "") {
                dtable.search("",true,false).draw();
                $('.goToPageInput').val(Math.ceil(dtable.page.info().recordsDisplay / dtable.page.info().length));
            }
            return;
        });

    var total_records = dtable.rows().count();
    var page_length = dtable.page.info().length;
    var total_pages = Math.ceil(total_records / page_length);

    //adding go to page btn
    $('#myTable_info').parent().removeClass("col-md-5").addClass("col-md-4");
    $('#myTable_paginate').parent().removeClass("col-md-7").addClass("col-md-4");
    let gotopage = document.createElement('DIV');
    gotopage.classList.add("col-md-4");
    let goToPagebtn = document.createElement('BUTTON');
    goToPagebtn.classList.add("goToPageBtn");
    $(goToPagebtn).text($.i18n("list.page"));
    gotopage.appendChild(goToPagebtn);
    let myinput = document.createElement("INPUT");
    myinput.classList.add("goToPageInput");
    myinput.setAttribute("id", "pageInput");
    $(myinput).val(total_pages).css("padding-left", "7px");
    gotopage.appendChild(myinput);
    $('#myTable_info').parent().parent().append(gotopage);
    let line = document.createElement("HR");
    line.classList.add("line");
    $('#myTable_info').parent().parent().prepend(line);

    let parent = document.getElementById('pageInput').parentElement;
    let invalidFeedback = document.createElement('DIV');
    invalidFeedback.classList.add('custom-invalid-feedback');

    let span = document.createElement('SPAN');
    span.classList.add('error-message');

    invalidFeedback.appendChild(span);
    parent.appendChild(invalidFeedback)
    let errorMessage;

    //adding go to page btn functionality
    $('.goToPageBtn').click(function () {
        let input = $('.goToPageInput').val();
        if (validInput(input)) {
            dtable.page(input - 1).draw('page');
            $('.custom-invalid-feedback').hide();
        } else {
            showMessage(errorMessage);
        }
    })

    function validInput(input) {
        if ((!(input.match(/^-{0,1}\d+$/)))) {
            errorMessage = $.i18n("list.error.notNumeric");
            return false;
        }
        if (input > total_pages || input <= 0) {
            errorMessage = $.i18n("list.error.greater");
            return false;
        }
        return true;
    }

    function showMessage(msg) {
        let errorMessage = msg;
        let element = $(document.getElementById('pageInput'));

        let hasPreviousErrors = element.parent().find('.custom-invalid-feedback').css('display') !== 'none';
        let customInvalidFeedback = element.parent().find('.custom-invalid-feedback');

        if (hasPreviousErrors) {
            if (errorMessage) {
                customInvalidFeedback.find('.error-message').text(errorMessage);
                customInvalidFeedback.show()
            } else {
                customInvalidFeedback.hide();
            }
        } else if (errorMessage) {
            customInvalidFeedback.find('.error-message').text(errorMessage);
            customInvalidFeedback.show();
        }
    }

    addErrorIcon()

});