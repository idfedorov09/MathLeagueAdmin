// Анимация появления при прокрутке страницы
function animateSections() {
  const sections = document.querySelectorAll('.section');

  sections.forEach((section) => {
    const sectionPosition = section.getBoundingClientRect().top;
    const windowHeight = window.innerHeight;

    if (sectionPosition < windowHeight) {
      section.classList.add('section-visible');
    }
  });
}

// Обработчик события прокрутки страницы
function scrollHandler() {
  animateSections();
}

// Добавление обработчика события прокрутки
window.addEventListener('scroll', scrollHandler);

// Анимация при загрузке страницы
window.addEventListener('load', () => {
  animateSections();
});

// Получение кнопок панели и обработка событий
const tabButtons = document.querySelectorAll('.tab-button');

tabButtons.forEach((button) => {
    button.addEventListener('click', () => {
        // Удаление класса "active" у всех кнопок
        tabButtons.forEach((btn) => {
            btn.classList.remove('active');
        });

        // Добавление класса "active" для нажатой кнопки
        button.classList.add('active');

        // Выполнение соответствующих действий при нажатии кнопки
        const target = button.getAttribute('data-target');
        // Например, можно скрывать/отображать соответствующую секцию
        const section = document.getElementById(target);
        section.style.display = 'block';
        // И скрывать/отображать остальные секции при необходимости
    });
});

