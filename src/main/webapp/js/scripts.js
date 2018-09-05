var request = new XMLHttpRequest();
request.open('GET', 'http://localhost:8080/FantaWebService/statisticheRESTImpl/downloadFromSite', true);
request.onload = function () {
  if (request.status >= 200 && request.status < 400) {
    console.log("OK")
  } else {
    console.log("KO")
  }
}

request.send();