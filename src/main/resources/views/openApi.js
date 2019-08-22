/* exported openApi */
/* jshint forin:true, noarg:true, noempty:true, eqeqeq:true, bitwise:true, strict:true, undef:true, unused:true, curly:true, browser:true, devel:true */
var openApi = (function() {
  "use strict";

  function rapipdf(url) {
    var pdf = document.createElement('rapi-pdf'), btStyle = pdf.btnEl.style;
    pdf.setAttribute('spec-url', url);
    pdf.setAttribute('hide-input', true);
    pdf.setAttribute('button-bg', '#b44646');
    pdf.classList.add('rapipdf');
    btStyle.paddingLeft = '6px';
    btStyle.paddingRight = '6px';
    return pdf;
  }

  function createMenu(apis, list, onClick) {
    apis.forEach(function(api) {
      var listitem = document.createElement('li'),
        name = document.createElement('div'),
        rapipdfnode = rapipdf(api.url);
      listitem.setAttribute('data-link', api.url);
      name.innerText = api.name;
      name.classList.add('menu_label');
      listitem.appendChild(name);
      listitem.appendChild(rapipdfnode);
      listitem.addEventListener('click', onClick);
      list.appendChild(listitem);
    });
  }
  return {
    createMenu: createMenu
  };
})();