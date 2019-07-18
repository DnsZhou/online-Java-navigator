var host = "localhost";
// var host = "ec2-35-178-134-147.eu-west-2.compute.amazonaws.com"
var port = "8080";
Vue.component('classpath-panel', {
    template: '<h5>Current Classpath:</h5>'
});
new Vue({
    el: '#app',
    data: {
        currentType: sessionStorage.getItem("currentType"),
        currentUrl: sessionStorage.getItem("currentUrl"),
        typeSearchQuery: '',
        typeSearchResults: [],
        currentSelectGavCu: null,
        currentSelectId: null
    },
    methods: {
        findGavCusByType() {
            this.clearTimer();
            if (this.typeSearchQuery && this.typeSearchQuery.length > 0) {
                this.timer = setTimeout(() => {
                    this.searchHandler();
                }, 500);
            }

        },
        searchHandler() {
            axios({
                method: 'get',
                url: 'http://' + host + ':' + port + '/findType?typeName=' + this.typeSearchQuery,
            }).then(response => (this.typeSearchResults = response.data.typeGavCuList)).then(response => this.clearSearch())
        },
        clearTimer() {
            if (this.timer) {
                clearTimeout(this.timer);
            }
        },
        clearSearch() {
            currentSelectId = null;
            currentSelectGavCu = null;
        },
        navigateByGavCu(gavCu) {
            var pathTokens = gavCu.split(':');
            var group = pathTokens[0];
            var artifact = pathTokens[1];
            var version = pathTokens[2];
            var cuName = pathTokens[3];
            var pathResult = "";
            pathResult = group.replaceAll('\\.', '/') + '/' + artifact.replaceAll('\\.', '/') + '/' + version + '/' + artifact + '-' + version + '/'
                + cuName.replaceAll('\\.', '/');
            sessionStorage.setItem("currentUrl", 'http://' + host + ':' + port + '/repository/' + pathResult + '.html');
            sessionStorage.setItem("currentType", cuName);
            this.currentType = sessionStorage.getItem("currentType");
            this.currentUrl = sessionStorage.getItem("currentUrl");
            this.clearSearch();
        },
        clearSession() {
            sessionStorage.clear();
            this.currentType = null;
            this.currentUrl = null;
        },
        changeItem(dirDown) {
            var items = document.getElementsByClassName("searchbox")[0];
            if (dirDown) {
                if (this.currentSelectId == null) {
                    this.currentSelectId = 0;
                } else {
                    if (items.children && this.currentSelectId + 1 < items.children.length) {
                        this.currentSelectId = this.currentSelectId + 1;
                    }
                }
                $(".selected").removeClass("selected");
                $(".search-option").attr('style', '');
                items.children[this.currentSelectId].classList.add("selected");
                items.children[this.currentSelectId].style.background = "rgb(214, 237, 255)";
                this.currentSelectGavCu = items.children[this.currentSelectId].innerText;
            } else {
                if (this.currentSelectId > 0) {
                    this.currentSelectId = this.currentSelectId - 1;
                }
                $(".selected").removeClass("selected");
                $(".search-option").attr('style', '');
                items.children[this.currentSelectId].classList.add("selected");
                items.children[this.currentSelectId].style.background = "rgb(214, 237, 255)";
                this.currentSelectGavCu = items.children[this.currentSelectId].innerText;
            }
        },
        enterOption() {
            if (this.currentSelectGavCu != null) {
                this.clearSearch();
                this.navigateByGavCu(this.currentSelectGavCu)
                location.reload();z
            }
        }
    }
});

String.prototype.replaceAll = function (search, replacement) {
    var target = this;
    return target.replace(new RegExp(search, 'g'), replacement);
};
window.onload = function () { $("#content-wrapper").removeClass("hidden") }