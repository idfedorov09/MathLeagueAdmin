const inputElement = document.getElementById('secretKey');

function removeCharacterAtIndex(inputText, index) {
    if (index < 0 || index >= inputText.length) {
        return inputText;
    }

    var partBeforeIndex = inputText.slice(0, index);
    var partAfterIndex = inputText.slice(index + 1);

    var resultString = partBeforeIndex + partAfterIndex;

    return resultString;
}

function setCaretPosition(elemId, caretPos) {
    var elem = document.getElementById(elemId);

    if(elem != null) {
        if(elem.createTextRange) {
            var range = elem.createTextRange();
            range.move('character', caretPos);
            range.select();
        }
        else {
            if(elem.selectionStart) {
                elem.focus();
                elem.setSelectionRange(caretPos, caretPos);
            }
            else
                elem.focus();
        }
    }
}

function isDigitOrLetter(character) {
    return /^[0-9a-zA-Z]$/.test(character);
}

function getWithoutDefis(text){
    return text.replace(/-/g, '');
}

function insertCharacterAtPosition(originalString, character, position) {
    if(position>originalString.length){
        return originalString;
    }
    if (position == originalString.length) {
        return originalString + character;
    } else if (position <= 0) {
        return character + originalString;
    } else {
        const start = originalString.slice(0, position);
        const end = originalString.slice(position);
        return start + character + end;
    }
}

function truncateString(str) {
    if (str.length > 16) {
        return str.substring(0, 16);
    }
    return str;
}

function getResult(newText){
    newText = insertCharacterAtPosition(newText, '-', 4);
    newText = insertCharacterAtPosition(newText, '-', 9);
    newText = insertCharacterAtPosition(newText, '-', 14);
    return newText;
}

inputElement.addEventListener('input', function(event) {
    let inputText = event.target.value.toUpperCase();
    let index = event.target.selectionStart - 1;
    let enteredCharacter = inputText.charAt(index);
    //console.log(index);
    insertSymbol(event.target, enteredCharacter, index)
});

inputElement.addEventListener('paste', function(event) {
    event.preventDefault();
    let index = event.target.selectionStart;

    const clipboardData = event.clipboardData || window.clipboardData;
    const pastedText = clipboardData.getData('text');

    for (let i = 0, cnt = 0; i < pastedText.length && cnt<20; i++, cnt++) {
        let kek = event.target.value;
        kek += pastedText[i];
        event.target.value = kek;
        var toAdd = insertSymbol(event.target, pastedText[i], index+i);
        index+=toAdd;
    }
});


function insertSymbol(target, enteredCharacter, index){

    let inputText = target.value.toUpperCase();
    let newText = inputText;
    inputText = removeCharacterAtIndex(inputText, index);

    if(enteredCharacter=='-' && (index==4 || index==9 || index==14)){
        target.value = inputText;
        setCaretPosition('secretKey', index+1);
        return 0;
    }

    if(!isDigitOrLetter(enteredCharacter)){
        target.value = inputText;
        setCaretPosition('secretKey', index);
        return 0;
    }

    newText = getWithoutDefis(newText);
    newText = truncateString(newText);
    newText = getResult(newText);


    target.value = newText;
    if(index == 3 || index == 8 || index == 13){
        setCaretPosition('secretKey', index+2);
        return 1;
    }
    else{
        setCaretPosition('secretKey', index+1);
        return 0;
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
