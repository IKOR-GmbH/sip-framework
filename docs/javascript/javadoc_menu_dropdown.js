var navItems = $("div.wy-menu-vertical ul li")
  .filter(function(idx) {return $(this).text().indexOf("JavaDoc") >= 0;})
  .map(function() {
      var elem = $($(this).children()[0]);
      elem.text(elem.text().replaceAll("JavaDoc ", ""));
      return this;
  })

var itemToReplace = navItems[0];
$(itemToReplace).attr('id', "javadoc-nav")

var javaDocVersions = navItems
  .map(function() { return $(this).clone();})
  .map(function() {
      $(this).attr('class', 'toctree-l2');
      return this;
  });

var tmpList = $("<ul>");
javaDocVersions
  .each(function (a,b) {
    console.log($(this).text());
    tmpList.append(this);
});
var newItems = "<li class=\"toctree-l1\" id=\"javadoc-nav\" onClick=\"javadocOnClick()\"><span>JavaDoc</span> <ul>" + tmpList.html() + "</ul></li>";

//console.log(newItems);
//$(itemToReplace).innerHtml = newItems;
$("#javadoc-nav").replaceWith(newItems);
navItems
  .filter(function(idx) {return $(this).attr("id") !== "javadoc-nav";})
  .each(function(a,b) { $(this).remove()});

function javadocOnClick() {
    console.log("test output")
    $("div.wy-menu-vertical ul.current").attr("class", "");
    $("div.wy-menu-vertical ul li.current span").remove();
    $("div.wy-menu-vertical ul li.current").attr("class", "");
    $("#javadoc-nav").attr("class", "toctree-l1 current");
    $("#javadoc-nav").parent().attr("class", "current")
}