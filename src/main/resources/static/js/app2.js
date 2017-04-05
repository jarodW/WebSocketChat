'use strict';

var app = angular.module('myApp', []);
app.controller('regController', function($scope,$http,$window) {
	$scope.register = function(){
		var data = {username : $scope.username, password : $scope.password};
        var config = {
                headers : {
            		'Content-Type': 'application/json'
                }
            }
		$http.post("http://localhost:8080/register", data).success(function(re){
			$window.alert("Success");
			window.location.href = "http://localhost:8080/";
		}).error(function(re){
			$window.alert("User Name Exist");
		});
	} 
});