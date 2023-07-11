var textElement = document.getElementById("taskname");
var editBtn = document.getElementById("edit-name-btn");

function toggleEditMode() {

  if (textElement.contentEditable === "true") {
    // Режим редактирования уже активен
    textElement.contentEditable = "false";
    editBtn.innerHTML = '<i class="fas fa-edit"></i>';
    // Здесь можно добавить код для сохранения изменений
    // Например, можно использовать AJAX-запрос, чтобы отправить изменения на сервер

    // Пример кода для сохранения изменений:
    var updatedText = textElement.innerText;
    // Отправить updatedText на сервер для сохранения

  } else {
    // Режим редактирования не активен, включаем его
    textElement.contentEditable = "true";
    textElement.focus();
    editBtn.innerHTML = '<i class="fas fa-save"></i>';
  }
}


textElement.addEventListener("keydown", function(event) {
      if (event.key === "Enter") {
        event.preventDefault(); // Предотвращаем перевод строки в текстовом поле
        toggleEditMode(); // Вызываем функцию переключения режима редактирования
      }
    });