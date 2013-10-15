var preparingDownloadDialog = $("#Photolude_Dialog_PreparingDownload");

function StartDownloadingAllPhotos() {
    preparingDownloadDialog.css("left", ($(window).width() - preparingDownloadDialog.width()) / 2 + "px");
    preparingDownloadDialog.css("top", ($(window).height() - preparingDownloadDialog.height()) / 2 + "px");
    $(document.body).after(preparingDownloadDialog);

    AddAllPhotosToPage();
}

function AddAllPhotosToPage() {
    if (photoPage.imageManagement.photoComplete == false) {
        $("#LoadMorePhotos").click();
        setTimeout(AddAllPhotosToPage, 1000);
    }
    else {
        preparingDownloadDialog.remove();
        DownloadAllPhotos();
    }
}

function DownloadAllPhotos() {
    var imageIds = new Array();
    for (var i = 0; i < photoPage.imageManagement.pageImageArray.length; i++) {
        imageIds[i] = photoPage.imageManagement.pageImageArray[i].id;
    }
    bulkDownloadDialog.DownloadImages(imageIds);
}

$(document).ready(function () {
	var source = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABgAAAAYCAYAAADgdz34AAAABmJLR0QAAAAAAAD5Q7t/AAAACXBIWXMAAABIAAAASABGyWs+AAAACXZwQWcAAAAYAAAAGAB4TKWmAAAGNElEQVRIx7WWW2yUxxmGn/+wu8bsgUDsJYBtsPFyloOx1wFiJaU1VUiK0t61VKhUjSoqVYU2alL1ouoVIa2wSwWJetPGCSGVUqlXHNYJLV23wYCNl8PWXnuxsbHX68VgL2t79///memFwYBCLvtKczMzep/55pvDpymliEajPE2RM6f8oJoUNAK1CkKgSlBkgIRSdIGKKqXadr26O/s0D+0hoLGx8YmBX//qrb0KuWd19ZqdlasqKSsrp6S0FK/XSy6XY3w8zfDwEMlkkt6engioE7/7fXPr4x7RaPTLgLfferNMSnWwqqrqQF19vVZbW4emaXyVlFJcvnyJixcvqERvTwtKNbccPT4M0N7ejvn45Dd/caBMSvnbcMML+3bs+DqlpUEymXH6+npJp1LMzOSYmJjANE38fj/lFZWsW7ee+vowFeUVWlvbmYP/bm9ftP/Hb/zm+Pt/GgaeBNiWdTD8wtZ9r722G6/Xyxf/aSd2rZPAc8/gq3iGJV4/C7VnmcnluJu+w53+S1yPx9iyuZ5wwza+tfvbOI7YF/3X+Ukp5c+fyMEnJz/cu7o69Jfvfff7WnDpUiJnTzMwnKSyPsTCJT6uj9+g/14/OSuHpuks9y6j2F2Mkyrgvg3rQ5v42o4mUqlRPmz9QPX0xH/wozf2t5oAn3zc6rcdZ0843KCVBoO0R88zMNTP+pefR7gl9/KTxNJXOfbK0flof3L6p7xU3sjo0jEyjBNPxCguXkDtlgbC4QbtxvWre947dvTvOoBlWU2h6tDOrVu3MzaW4nJnB1UNa7FMm6n8FB0jHWQLc6ewt7d3bjuFzcj9UTyGGwIGuaBFZ+cFxlKj1G6pY82atTsVqkkHsB2nsTq0BoAb165SunIp0gvp6XFO9Z9h4N4gCgWAx+MBYNaZpTsd43Kqk8n8FLcXjjNrWMTj17Asi4qVq1BSNj6MoHblqkqUUty82UfxUh+x9FXOJiNM29Pz5o8DAKSSWMJiyppiRuSYXpRncDDJ7OwsweBzCCFqzQenJ7Rs2TKUUoyMDDG68i7J/MATxg9lGMaX+iQSpSmmfPdx9UsKhQKBQAAhRcgEsGy7xO8PIKXEEZK0PTpv/vb2XxJaXI1Ld5HNZhFCMDg4yEevf8C0PU3vRILmzmZ0tyRvzOLYRViWhWEaCEeU6ACObWUm7txBCIGmNDx51/zqziYjaGhkMhny+TyFQgHLskgkErh0F+eGPkd3S/QiiWmDIyzy+TwTE3cRjpOZS7JtJ24NDSKlxOf34r73aBuujHXz2cDnBJYE0DQN27Zxu92UrSojMhjh2lQ3+oI5gJ7WcLmLKBQKpNNjOI6TmItAiK7/xuMIIVi5ajVaysHQDdAADU7cOEkql8IX8KHrOsFlQUamR/j01kkMr8AolhgehbopWPJsECEEQ7cGsR27SwcQjhONdXchhMPGjTWQVSxOe9EMNd8OdxwGHcrKyhCa4Ej8HQzfA/NigR53wZRGRUUljuMwcDOJI0T0AUC0dXR0RNraIpSUBgnXbcO+PsPCSQ+6W6F7JDPGfY7Fj2LrNs2JQ1gL7mMUS/QFAnVLgwsa61ZvwucPEOvuIpHoiQjHadMBDr17JKuUOBE5e1oNDw/xYuPLbFxbg35J4Lu9ALMIjGJBvNDFges/ZFj1zZm7FCpmwik31cvXsXHTZjLj43RevqiEECcOHT6S1R8m80zkn62xK10tf/v0r0xOTvKNplcI12yFK4KiiJui/iI8OTcuTIxJE9VtID52YZz38HyonvqGF8lms5w/f47+/kTLO+/OfT7zz7Wu6yhUc+Ts6UVCiH27Xt1NXXgry1eU05uIM9Q7gBWzwFZIQ1JUZLCipILqzevwBxaRTo/xj3OfcenihT8DzaZpIqV89FzX1NRg2zbfbHqpDDi4YcOmA9u2N2pb6sLYto2UEiEESimklI9usZTEuru42PGFSvb3tQDNLX98f9jlclEoFB4BamtrsSwLy7IoFAp85/Vde4E96zds3FlVVc2KsnIWL16C1+sjm53iTibD7dtDJJN9JHp7IsCJIy3HW03TxDRNPB4P2Wz2EeBpOviz/X6giQdVBRACSmCuqgC6gCjQ1vyH9766qvh/6n/6pEG2h6S17gAAACV0RVh0Y3JlYXRlLWRhdGUAMjAwOS0xMS0yM1QxMTo1ODoxNi0wNTowMA/PgfsAAAAldEVYdG1vZGlmeS1kYXRlADIwMDktMDgtMjBUMTM6MDE6MzAtMDU6MDAOeeUiAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAABJRU5ErkJggg==";
    CreateToolBarIcon(source, "Download All", "Download all photos", StartDownloadingAllPhotos);
});