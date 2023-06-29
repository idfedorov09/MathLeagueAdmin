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
