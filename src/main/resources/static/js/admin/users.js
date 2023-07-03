$(document).ready(function() {
    $('.edit-btn').click(function() {
        var userId = $(this).data('user-id');
        var username = $(this).data('username');
        var userNick = $(this).data('user-nick');
        var telegram = $(this).data('telegram');
        var roles = $(this).data('roles');

        $('#userId').val(userId);
        $('#editUsername').val(username);
        $('#editUserNick').val(userNick);
        $('#editTelegramUsername').val(telegram);
        $('#edit-container').removeClass('hidden');
        editPopupContainer.classList.add("show");

        markSelectedRoles(roles);
    });
});

document.addEventListener("DOMContentLoaded", function() {
    var editPopupContainer = document.getElementById("editPopupContainer");
    var closeEditPopupBtn = document.getElementById("closeEditPopup");

    closeEditPopupBtn.addEventListener("click", function() {
        editPopupContainer.classList.remove("show");
    });

    document.addEventListener("keydown", function(event) {
        if (event.key === "Escape") {
            editPopupContainer.classList.remove("show");
        }
    });

    // Дополнительный код обработки нажатия на кнопку "Редактировать"
    // и заполнение полей формы данными пользователя
});

function markSelectedRoles(roles) {
    var roleItems = document.querySelectorAll(".role-list .role-item");

    roleItems.forEach(function(item) {
        var checkbox = item.querySelector("input[type='checkbox']");
        var role = item.querySelector("span").innerText;

        if (roles.includes(role)) {
            checkbox.checked = true;
        }
    });
}
