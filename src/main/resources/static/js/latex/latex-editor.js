let editorElem = document.querySelector("#editor");

ace.require("ace/ext/language_tools");

var editor = ace.edit(editorElem, {
  mode: "ace/mode/latex"
});

//NOT WORKING FOR LATEX
editor.setOptions({
    enableBasicAutocompletion: true,
    enableLiveAutocompletion: true
  });
  editor.getSession().setUseWrapMode(true);

editor.getSession().on('change', function() {
  saveLatexCode()
});

/*var top1m = document.querySelector(".top1m");
var calcus = $(window).height() - document.querySelector('header').offsetHeight
top1m.style.height = calcus+"px";

document.querySelector("#contentdiv").style.height=100+"%";
document.querySelector(".splitter").style.height=100+"%";*/


function dragElement(element, direction)
{
    var   md; // remember mouse down info
    const first  = document.getElementById("code-content");
    const second = document.getElementById("result-content");

    const underKek = document.querySelectorAll('.under-editor, .under-result');

    element.onmousedown = onMouseDown;

    function onMouseDown(e)
    {
        //console.log("mouse down: " + e.clientX);
        md = {e,
              offsetLeft:  element.offsetLeft,
              offsetTop:   element.offsetTop,
              firstWidth:  first.offsetWidth,
              secondWidth: second.offsetWidth
             };

        document.onmousemove = onMouseMove;
        document.onmouseup = () => {
            //console.log("mouse up");
            document.onmousemove = document.onmouseup = null;
        }
    }

    function onMouseMove(e)
    {
        //console.log("mouse move: " + e.clientX);
        var delta = {x: e.clientX - md.e.clientX,
                     y: e.clientY - md.e.clientY};

        var percent = 4;

        percent /=100;

        if (direction === "H" ) // Horizontal
        {
            // Prevent negative-sized elements
            delta.x = Math.min(Math.max(delta.x, -md.firstWidth),
                       md.secondWidth);

            if(md.offsetLeft + delta.x>=$(window).width()*percent && md.offsetLeft + delta.x<=$(window).width()*(1-percent))
            {

            var oneP = (md.firstWidth + delta.x)
            var twoP = (md.secondWidth - delta.x);
            var summ = oneP+twoP;

            element.style.left = md.offsetLeft + delta.x + "px";
            first.style.width = oneP*100/summ + "%";
            second.style.width = twoP*100/summ + "%";

            underKek[0].style.width = (md.firstWidth + delta.x) + "px";
            underKek[1].style.width = (md.secondWidth - delta.x) + "px";


            editor.resize();
            }
        }
    }
}


dragElement( document.getElementById("separator"), "H" );

function saveLatexCode(){
    var texCode = editor.getSession().getValue();
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    var taskId = $("meta[id='taskId']").attr("content");

    var xhr = new XMLHttpRequest();
    xhr.open("POST", "save-texcode?id="+taskId, true);
    xhr.setRequestHeader(header, token);
    xhr.setRequestHeader("Content-Type", "application/json");

    xhr.onload = function() {
        if (xhr.status === 200) {

        }
        else {
            console.error("Error on saving latex code.");
        }
    };
    xhr.send(texCode);
    if(hasError){
        clearErrorHighlight();
    }
}