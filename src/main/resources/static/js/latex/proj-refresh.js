hasError = false;

var updateButton = document.querySelector('.update-button');
var loader = updateButton.querySelector('.loader');
var imageElement = document.getElementById('resultImage');
var problemId = $("meta[id='taskId']").attr("content")
var token = $("meta[name='_csrf']").attr("content");
var header = $("meta[name='_csrf_header']").attr("content");

function performPostRequest() {

    toggleOpacity();

    updateButton.disabled = true;
    loader.style.display = 'inline-block';

    fetch('/weekly-problems/refresh?id=' + problemId, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
             [header]: token
        }
    }).then(function (response) {
        toggleOpacity();
        stat = response.status;
        if(stat==406) return response.json();
        return response;
    }).then(function (data) {

        if (stat === 406) {
           highlightErrorLine(data.errorLine, data.message);
        }

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

function highlightErrorLine(lineNumber, errorMessage) {
  editor.gotoLine(lineNumber, 0, true); // Переходим на указанную строку и выделяем её

  // Добавляем маркер на указанную строку с ошибкой
  editor.getSession().setAnnotations([{
    row: lineNumber - 1,
    text: errorMessage,
    type: "error"
  }]);

  var Range = ace.require('ace/range').Range;
  editor.session.addMarker(new Range(lineNumber-1, 0, lineNumber-1, 1), "myMarker", "fullLine");
  hasError = true;
}

function clearErrorHighlight() {
  var markers = editor.session.getMarkers(false);
  for (var markerId in markers) {
    if (markers[markerId].clazz === "myMarker") {
      editor.session.removeMarker(markerId);
    }
  }
  editor.getSession().clearAnnotations();
  hasError = false;
}

function toggleOpacity() {
  const kekElement = $("#result-content");
  setTimeout(function(){
  kekElement.toggleClass('fade-in');
  }, 200);
}

$('.update-button').trigger('click');