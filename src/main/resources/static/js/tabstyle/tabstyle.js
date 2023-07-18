(function() {
  "use strict";

  const table = document.getElementById('table');
  const tbody = table.querySelector('tbody');

  var currRow = null,
      dragElem = null,
      mouseDownX = 0,
      mouseDownY = 0,
      mouseX = 0,
      mouseY = 0,
      mouseDrag = false,
      oldRow = null;

  function init() {
    bindMouse();
  }

  function bindMouse() {
    document.addEventListener('mousedown', (event) => {
      if (event.button != 0) return true;

      let target = getTargetRow(event.target);
      if (target) {
        oldRow = Array.from(tbody.children).indexOf(target);
        currRow = target;
        addDraggableRow(target);
        currRow.classList.add('is-dragging');

        let coords = getMouseCoords(event);
        mouseDownX = coords.x;
        mouseDownY = coords.y;

        mouseDrag = true;
      }
    });

    document.addEventListener('mousemove', (event) => {
      if (!mouseDrag) return;

      let coords = getMouseCoords(event);
      mouseX = coords.x - mouseDownX;
      mouseY = coords.y - mouseDownY;

      moveRow(mouseX, mouseY);

      // Обновляем значения id ячеек
      updateCellIds();
    });

    document.addEventListener('mouseup', (event) => {
      if (!mouseDrag) return;

      console.log("Old num: ", oldRow);
      console.log("New num: ", Array.from(tbody.children).indexOf(currRow));


      var token = $("meta[name='_csrf']").attr("content");
      var header = $("meta[name='_csrf_header']").attr("content");
      var xhr = new XMLHttpRequest();
      var url = '/weekly-problems/updatePriority';
      var oldIndex = oldRow;
      var newIndex = Array.from(tbody.children).indexOf(currRow);

      xhr.open('POST', url, true);
      xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
      xhr.setRequestHeader(header, token);

      xhr.onreadystatechange = function() {
        if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 200) {
          var response = xhr.responseText;
          console.log("Ok!"); // Обработайте полученный ответ
        }
      };

      var params = 'oldPriority=' + encodeURIComponent(oldIndex) + '&newPriority=' + encodeURIComponent(newIndex);
      xhr.send(params);

      currRow.classList.remove('is-dragging');
      table.removeChild(dragElem);

      dragElem = null;
      mouseDrag = false;
    });
  }

  function swapRow(row, index) {
    let currIndex = Array.from(tbody.children).indexOf(currRow),
        row1 = currIndex > index ? currRow : row,
        row2 = currIndex > index ? row : currRow;

    //save data value
    let row1Value = row1.children[3].textContent;
    let row2Value = row2.children[3].textContent;

    tbody.insertBefore(row1, row2);

    //upd saved data value
    row1.children[3].textContent = row2Value;
    row2.children[3].textContent = row1Value;
  }

  function moveRow(x, y) {
    dragElem.style.transform = "translate3d(" + x + "px, " + y + "px, 0)";

    let dPos = dragElem.getBoundingClientRect(),
        currStartY = dPos.y,
        currEndY = currStartY + dPos.height,
        rows = getRows();

    for (var i = 0; i < rows.length; i++) {
      let rowElem = rows[i],
          rowSize = rowElem.getBoundingClientRect(),
          rowStartY = rowSize.y,
          rowEndY = rowStartY + rowSize.height;

      if (currRow !== rowElem && isIntersecting(currStartY, currEndY, rowStartY, rowEndY)) {
        if (Math.abs(currStartY - rowStartY) < rowSize.height / 2)
          swapRow(rowElem, i);
      }
    }
  }

  function addDraggableRow(target) {
    dragElem = target.cloneNode(true);
    dragElem.classList.add('draggable-table__drag');
    dragElem.style.height = getStyle(target, 'height');
    dragElem.style.background = getStyle(target, 'backgroundColor');
    for (var i = 0; i < target.children.length; i++) {
      let oldTD = target.children[i],
          newTD = dragElem.children[i];
        newTD.style.width = getStyle(oldTD, 'width');
        newTD.style.height = getStyle(oldTD, 'height');
        newTD.style.padding = getStyle(oldTD, 'padding');
        newTD.style.margin = getStyle(oldTD, 'margin');
    }

    table.appendChild(dragElem);

    let tPos = target.getBoundingClientRect(),
        dPos = dragElem.getBoundingClientRect();
    dragElem.style.bottom = (dPos.y - tPos.y) + "px";
    dragElem.style.left = "-1px";

    document.dispatchEvent(new MouseEvent('mousemove', {
      view: window,
      cancelable: true,
      bubbles: true
    }));
  }

  function getRows() {
    return table.querySelectorAll('tbody tr');
  }

  function getTargetRow(target) {
    let elemName = target.tagName.toLowerCase();

    if (elemName == 'tr') return target;
    if (elemName == 'td') return target.closest('tr');
  }

  function getMouseCoords(event) {
    return {
      x: event.clientX,
      y: event.clientY
    };
  }

  function getStyle(target, styleName) {
    let compStyle = getComputedStyle(target),
        style = compStyle[styleName];

    return style ? style : null;
  }

  function isIntersecting(min0, max0, min1, max1) {
    return Math.max(min0, max0) >= Math.min(min1, max1) &&
           Math.min(min0, max0) <= Math.max(min1, max1);
  }

  // Обновление значений id ячеек
  function updateCellIds() {
    let rows = getRows();

    rows.forEach((row, index) => {
      let idCell = row.querySelector('td:first-child');
      idCell.textContent = index.toString();
    });
  }


  init();
})();



$(document).ready(function() {
    $('.action-btn.delete-btn').click(function(){

            var problemId = $(this).data('problem-id');
            var token = $("meta[name='_csrf']").attr("content");
            var header = $("meta[name='_csrf_header']").attr("content");

            var xhr = new XMLHttpRequest();

            xhr.open("POST", "/weekly-problems/delete/"+problemId, true);
            xhr.setRequestHeader(header, token);
            xhr.setRequestHeader("Content-Type", "application/json");
            var raw = this;
            xhr.onload = function() {
              if (xhr.status === 200) {
                 $(raw).closest("tr").remove();
              } else {
                console.error("Error on removing");
              }
            };
            xhr.send();
    });

    $('.action-btn.edit-btn').click(function(){
                var problemId = $(this).data('problem-id');
                window.location.href = "/weekly-problems/"+problemId;
        });
});
