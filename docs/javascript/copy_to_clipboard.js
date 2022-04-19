function copyToClipboard(event) {
    const textToCopy = event.target.value;
    if (navigator.clipboard && window.isSecureContext) {
        return navigator.clipboard.writeText(textToCopy);
    }
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

const codeBlocks = document.getElementsByClassName('highlight');
for (let codeBlock of codeBlocks) {
    let codeText = codeBlock.firstChild.innerText;
    const btn = document.createElement('button');
    btn.setAttribute('class', 'copy-button');
    btn.setAttribute('value', codeText);
    btn.setAttribute('onclick', 'copyToClipboard(event)');
    codeBlock.appendChild(btn)
}
