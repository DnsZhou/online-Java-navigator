var host = "localhost";
// var host = "CHANGE_TO_YOUR_HOST"
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
        currentSelectId: null,
        currentClasspathList: [],
        tempClasspathList: [],
        showClasspathModal: false,
    },
    mounted() {
        this.fetchClasspathList()
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
                location.reload();
            }
        },
        initializeClasspathModal() {
            if (this.currentClasspathList == null || this.currentClasspathList.length == 0) {
                this.fetchClasspathList();
            }
            this.tempClasspathList = this.currentClasspathList.slice(0);
        },
        classpathDel(index) {
            this.tempClasspathList.splice(index, 1);
        },
        upmoveClasspath(index) {
            if (index > 0) {
                var temp = this.tempClasspathList[index - 1];
                // this.tempClasspathList[index - 1] = this.tempClasspathList[index];
                // this.tempClasspathList[index] = temp;
                Vue.set(this.tempClasspathList, index - 1, this.tempClasspathList[index])
                Vue.set(this.tempClasspathList, index, temp)
            }
        },
        downmoveClasspath(index) {
            if (index < this.tempClasspathList.length - 1) {
                var temp = this.tempClasspathList[index + 1];
                Vue.set(this.tempClasspathList, index + 1, this.tempClasspathList[index])
                // this.tempClasspathList[index + 1] = this.tempClasspathList[index];
                Vue.set(this.tempClasspathList, index, temp)
                // this.tempClasspathList[index] = temp;
            }
        },
        addClasspath() {
            this.tempClasspathList.push("");
        },
        clearClasspathDraft() {
            this.tempClasspathList = [];
        },
        saveClasspath() {
            this.currentClasspathList = this.tempClasspathList.slice(0);
            this.updateClasspathHandler();
            $("#classpathModal").modal('hide');
        },
        updateClasspathHandler() {
            axios({
                method: 'post',
                url: 'http://' + host + ':' + port + '/setClasspath',
                headers: {
                    'Content-Type': "application/json",
                },
                data: {
                    classpathList: this.currentClasspathList
                }
            }).then((result) => {
                var hash = result.data.classpathHash;
                console.log('Update classpath successfully with classpath hash: ' + hash);
                document.cookie = "classpath-hash=" + hash;
            }).catch(function (error) {
                console.log('Error while update classpath: ' + error);
            })
        },
        fetchClasspathList() {
            var hashCookie = this.getCookie("classpath-hash");
            if (hashCookie != null && hashCookie.length != 0) {
                axios({
                    method: 'get',
                    url: 'http://' + host + ':' + port + '/getClasspath',
                }).then((result) => {
                    this.currentClasspathList = result.data.classpathList;
                    console.log('Fetch classpath List successfully -- ' + this.currentClasspathList.length + " records");
                }).catch(function (error) {
                    console.log('Error while fetch classpath List: ' + error);
                })
            }
        },
        getCookie(name) {
            var arr = document.cookie.match(new RegExp("(^| )" + name + "=([^;]*)(;|$)"));
            if (arr != null) {
                console.log(arr);
                return unescape(arr[2]);
            }
            return null;
        },
        frameBack() {
            var codeFrameObj = document.getElementById("codeFrame");
            var codeFrame = (codeFrameObj.contentWindow || codeFrameObj.contentDocument)
            if (codeFrame) {
                codeFrame.history.back();
            }
        },
        frameForward() {
            var codeFrameObj = document.getElementById("codeFrame");
            var codeFrame = (codeFrameObj.contentWindow || codeFrameObj.contentDocument)
            if (codeFrame) {
                codeFrame.history.forward();
            }
        },
    }
});

String.prototype.replaceAll = function (search, replacement) {
    var target = this;
    return target.replace(new RegExp(search, 'g'), replacement);
};
window.onload = function () {
    $("#content-wrapper").removeClass("hidden")
}