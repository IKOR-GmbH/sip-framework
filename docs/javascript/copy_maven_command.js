function copyToClipboard() {
    var textToCopy = "mvn archetype:generate -DarchetypeGroupId=de.ikor.sip.foundation -DarchetypeArtifactId=sip-archetype -DarchetypeVersion=<latest.sip-archetype.version> -DgroupId=de.ikor.sip.adapter -DartifactId=demo -DprojectName=DemoAdapter -Dversion=1.0.0-SNAPSHOT";
    if (navigator.clipboard && window.isSecureContext) {
        return navigator.clipboard.writeText(textToCopy);
    } else {
        let textArea = document.createElement("textarea");
        textArea.value = textToCopy;
        textArea.style.position = "fixed";
        textArea.style.left = "-999999px";
        textArea.style.top = "-999999px";
        document.body.appendChild(textArea);
        textArea.focus();
        textArea.select();
        return new Promise((res, rej) => {
            document.execCommand('copy') ? res() : rej();
            textArea.remove();
        });
    }
}

function getElementByXpath(path) {
  return document.evaluate(path, document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue;
}

const something = document.querySelector('body > div.wy-grid-for-nav > section > div > div > div:nth-child(2) > div > div.special_copy + pre');

// creating the span element, then add a class attribute
const btn = document.createElement('button');
btn.setAttribute('class', 'btn');
btn.innerHTML = 'Copy';
btn.setAttribute('onclick', 'copyToClipboard()')


// add the <a> element tree into the div#something
something.appendChild(btn);