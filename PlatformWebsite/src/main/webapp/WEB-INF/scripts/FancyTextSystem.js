function FancyTextSystem(elementId, placeHolder) {
    this.elementId = elementId;
    this.placeHolder = placeHolder;
    this.element = null;
    this.overlay = null;

    this.resize = function () {
        var offset = this.element.position();
        var top = offset.top + ((this.element.height() - this.overlay.height()) / 2);
        this.overlay.css("left", offset.left + 3 + "px");
        this.overlay.css("top", top + "px");
    };

    this.valueChanged = function () {
        if (this.element.val() == "") {
            this.overlay.show();
        }
        else {
            this.overlay.hide();
        }
    };

    this.load = function () {
        this.element = $("#" + elementId);

        var div = $("<div class=\"fancyTextDiv\"></div>");

        this.element.after(div);
        this.element.remove();
        this.element.attr("class", "fancyTextInput");

        this.overlay = $("<div class=\"fancyTextPlaceholder\">" + this.placeHolder + "</div>");
        this.element.val("");


        div.append(this.element);
        div.append(this.overlay);

        var self = this;
        this.element.keyup(function () { self.valueChanged(); });
        this.element.keypress(function () { self.overlay.hide(); });
        this.element.change(function () { self.valueChanged(); });
        this.overlay.click(function () { self.element.focus(); });
        this.resize();

        setInterval(function () { self.resize(); }, 300);
    }

    var self = this;
    $(document).ready(function () { self.load(); });
}