document.addEventListener("DOMContentLoaded", function () {
    var submitButton = document.getElementById("saveBot");

    submitButton.addEventListener("click", function (event) {
        event.preventDefault();

        var botToken = document.getElementById("bot-token").value;
        var chatId = document.getElementById("chat-id").value;

        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");

        var xhr = new XMLHttpRequest();

        xhr.open("POST", "/admin/save-bot?bot-token="+botToken+"&chat-id="+chatId, true);
        xhr.setRequestHeader(header, token);
        xhr.setRequestHeader("Content-Type", "application/json");
        xhr.onload = function() {
                if (xhr.status === 200) {
                var myTextElement = document.getElementById("savedSuccesfull");
                 myTextElement.style.display = 'block';

                 setTimeout(function() {
                     myTextElement.classList.add("fade-out");
                   }, 500);

                 setTimeout(function() {
                     myTextElement.classList.remove("fade-out");
                     myTextElement.style.display = 'none';
                 }, 1100);



                }
                else {
                    console.error("Error on saving bot settings.");
                }
        };
        xhr.send();

    });
});
