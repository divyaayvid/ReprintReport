/**
 * 
 */
var app = angular.module('angularApp', []);
app.controller('angularReprintReportController', function ($scope, $http, $window) {
var ALL_REPRINTS =['Order#','Reprint date','Initial Ship date','Tracking number','Total# reprints','Store#','Store Name','Badge ID','Associate Name'];
	
	var REPRIENT_PREVIOUSLY_SHIPPED=['Order#','Reprint date','Initial Ship date','Tracking number','Total# reprints','Store#','Store Name','Badge ID','Associate Name'];


	var REPRINT_BY_STORE =['Store#','Store Name','No. of Reprints'];


	var REPRINT_BY_EMPLOYEE =['Badge#','Associate Name'];


	var SUMMARIZED_REPRIENTS=['Badge#','Associate Name','Store#','Store Name','Total Reprints Count'];


    var TABLET_DEVICE_REPORTS=['Store#','Device','OPPS_Actions#'];
    $scope.header = [];
    

	$('#startDate1').datetimepicker({
		format: 'Y-m-d H:i ',
	});
	
		$('#endDate1').datetimepicker({
			format: 'Y-m-d H:i ',
			});
var url = "./getStoreList.action";
$http.get(url).then(function (response) {
    $scope.storeList = (response.data);
}, function (error) {
    alert("error");
});
$scope.downloadXLS = function (query,eventType,xlsHeader) {
    var url = './xlsDownload.action?query=' + encodeURIComponent(query)+'&header='+xlsHeader+'&storeName='+eventType;
    $window.location = url;
}
$scope.getReprintReportByStoreName = function (storeName, startDate, endDate) { 
	$scope.eventType = storeName;
	alert($scope.eventType)
	if(storeName == "REPRIENT_PREVIOUSLY_SHIPPED"){
		$scope.header = REPRIENT_PREVIOUSLY_SHIPPED;
	} else if(storeName =="REPRINT_BY_STORE"){
		$scope.header = REPRINT_BY_STORE;
	} else if(storeName == "REPRINT_BY_EMPLOYEE"){
		$scope.header = REPRINT_BY_EMPLOYEE;
	}else if(storeName == "SUMMARIZED_REPRIENTS"){
		$scope.header = SUMMARIZED_REPRIENTS;
	}else if(storeName == "TABLET_DEVICE_REPORTS"){
		$scope.header = TABLET_DEVICE_REPORTS;
	}else if(storeName == "ALL_REPRINTS"){
		$scope.header = ALL_REPRINTS;
	} else {
		$scope.header = [];
	}
	$scope.xlsHeader = $scope.header.toString();
    var url = './chartData.action?storeName=' + storeName + '&startDate=' + startDate + '&endDate=' + endDate;
    alert(url)
    $http.get(url).then(function (response) {
        alert(JSON.stringify(response) + "---------------------")
        $scope.reprintData = (response.data);
        for (var i = 0; i < $scope.reprintData.length; i++) {
          $scope.query = $scope.reprintData[i].query;
      }
    }, function (error) {
        alert("error");
    });
}
$scope.getReprintReportByStoreName();
});
