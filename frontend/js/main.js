var host = "localhost";
// var host = "ec2-35-178-134-147.eu-west-2.compute.amazonaws.com"
var port = "8080";
Vue.component('classpath-panel', {
    template: '<h5>Current Classpath:</h5>'
})
new Vue(
    {
        el: '#app',
        data: {
            currentType: sessionStorage.getItem("currentType"),
            currentUrl: sessionStorage.getItem("currentUrl"),
            typeSearchQuery: '',
            typeSearchResults: [],
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
                }).then(response => (this.typeSearchResults = response.data.typeGavCuList))
            },
            clearTimer() {
                if (this.timer) {
                    clearTimeout(this.timer);
                }
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
            }
        }

    })
String.prototype.replaceAll = function (search, replacement) {
    var target = this;
    return target.replace(new RegExp(search, 'g'), replacement);
};