function performPostRequest() {
    var updateButton = document.querySelector('.update-button');
    var loader = updateButton.querySelector('.loader');
    var imageElement = document.getElementById('resultImage');
    var problemId = $("meta[id='taskId']").attr("content")

    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");

    updateButton.disabled = true;
    loader.style.display = 'inline-block';

    fetch('/weekly-problems/refresh?id=' + problemId, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
             [header]: token
        }
    }).then(function (response) {
        return response;
    }).then(function () {
        updateButton.disabled = false;
        loader.style.display = 'none';

        var timestamp = new Date().getTime();
        var newImageUrl = '/weekly-problems/image/'+problemId+'?timestamp=' + timestamp;
        imageElement.src = newImageUrl;
    }).catch(function (error) {
        console.error('Error on loading result image:', error);
        updateButton.disabled = false;
        loader.style.display = 'none';
    });
}

function changeImageSrc(problemId) {
    var imageElement = document.getElementById('resultImage');
    var currentSrc = imageElement.src;
    var newSrc = currentSrc + problemId;
    imageElement.src = newSrc;
}

changeImageSrc($("meta[id='taskId']").attr("content"));