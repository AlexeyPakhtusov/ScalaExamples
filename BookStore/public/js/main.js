$(document).ready(function() {
    $('#booksTable').DataTable();
});

function sendDeleteRequest(dUrl, rUrl) {
    $.ajax({
        url: dUrl,
        method: "DELETE",
        success: function() {
            window.location = rUrl;
        },
        error: function() {
            window.location.reload();
        }
    });
}

function sendPutRequest(formId, rUrl) {
    var form = $('#' + formId)
    $.ajax({
        url: form.attr('action'),
        method: "PUT",
        data: form.serialize(),
        success: function() {
            window.location = rUrl;
        },
        error: function() {
            window.location.reload();
        }
    });
}