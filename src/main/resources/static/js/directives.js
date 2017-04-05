/* Directives */

angular.module('springChat.directives', [])
	.directive('printMessage', function () {
	    return {
	    	restrict: 'A',
	        template: '<span ng-show="message.priv">[private] </span><strong>{{message.username}}</span>:</strong> {{message.message}}<br/>'
	       
	    };
	})
	.directive('privateMessage', function () {
	    return {
	    	restrict: 'A',
	        template: '<strong>{{message.username}}</span>:</strong> {{message.message}}<br/>'
	       
	    };
	});