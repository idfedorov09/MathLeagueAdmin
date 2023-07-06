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

            element.style.left = md.offsetLeft + delta.x + "px";
            first.style.width = (md.firstWidth + delta.x) + "px";
            second.style.width = (md.secondWidth - delta.x) + "px";

            editor.resize();
            }
        }
    }
}


dragElement( document.getElementById("separator"), "H" );