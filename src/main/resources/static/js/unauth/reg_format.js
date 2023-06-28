function formatSecretKey(input) {
  const filteredInput = input.value.replace(/[^A-Za-z0-9]/g, '');

  const uppercaseInput = filteredInput.toUpperCase();

  if (uppercaseInput.length > 16) {
    const truncatedInput = uppercaseInput.slice(-16);
    const formattedInput = truncatedInput.match(/.{1,4}/g).join('-');
    input.value = formattedInput;
  } else {
    const formattedInput = uppercaseInput.match(/.{1,4}/g).join('-');
    input.value = formattedInput;
  }
}

document.addEventListener('DOMContentLoaded', function() {
    const form = document.querySelector('form');
    const registerButton = document.getElementById('registerButton');
    const secretKeyInput = document.getElementById('secretKey');

    form.addEventListener('input', function() {
        const inputs = form.querySelectorAll('input');
        let isEmpty = false;
        let isValidKey = false;

        inputs.forEach(function(input) {
            if (input.value.trim() === '') {
                isEmpty = true;
            }
        });

        const secretKeyRegex = /^[A-Z0-9]{4}-[A-Z0-9]{4}-[A-Z0-9]{4}-[A-Z0-9]{4}$/;
        if (secretKeyRegex.test(secretKeyInput.value.trim())) {
            isValidKey = true;
        }

        registerButton.disabled = isEmpty || !isValidKey;
    });
});
