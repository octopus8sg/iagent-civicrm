jQuery(document).ready(function ($) {
    $(".popUplink").click(function () {
        var hyperLink = $(this).attr('href');
        console.log(hyperLink);
        window.open(hyperLink, 'popup', 'width=800,height=800');
        return false;
    });
});