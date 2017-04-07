'use strict';

/* Controllers */

angular.module('springChat.controllers', ['toaster'])
	.controller('ChatController', ['$scope', '$location','$window', '$interval', '$compile','toaster', 'ChatSocket', function($scope, $location, $window, $interval, $compile,toaster, chatSocket) {
		  
		var typing = undefined;
		
		$scope.username     = '';
		$scope.sendTo       = 'everyone';
		$scope.participants = [];
		$scope.messages     = [];
		$scope.privateMessages = [];
		$scope.newMessage   = '';
		$scope.newPrivateMessage = '';
		 
		//send public group messages
		$scope.sendMessage = function() {
			//send to app handler
			var destination = "/app/chat.message";
			$scope.sendTo = "everyone";
			chatSocket.send(destination, {}, JSON.stringify({message: $scope.newMessage}));
			$scope.newMessage = '';
		};
		//send private group messages
		$scope.sendPrivate = function(id){
			$scope.sendTo = id;
			console.log(id);
			//send to app handler
			var destination = "/app/chat.private." + $scope.sendTo;
			$scope.privateMessages.unshift({message: $scope.newMessage, username: 'you', priv: true, to: $scope.sendTo});
			chatSocket.send(destination, {}, JSON.stringify({message: $scope.newMessage}));
			$scope.newMessage = '';
		}
		
		$scope.startTyping = function() {
			// Don't send notification if we are still typing or we are typing a private message
	        if (angular.isDefined(typing) || $scope.sendTo != "everyone") return;
	        
	        typing = $interval(function() {
	                $scope.stopTyping();
	            }, 500);
	        
	        chatSocket.send("/topic/chat.typing", {}, JSON.stringify({username: $scope.username, typing: true}));
		};
		
		$scope.stopTyping = function() {
			if (angular.isDefined(typing)) {
		        $interval.cancel(typing);
		        typing = undefined;
		        
		        chatSocket.send("/topic/chat.typing", {}, JSON.stringify({username: $scope.username, typing: false}));
			}
		};
		
		$scope.privateSending = function(username) {
				$scope.sendTo = (username != $scope.sendTo) ? username : 'everyone';
		};
		
		
		
		var initStompClient = function() {
			chatSocket.init('/ws');
			
			chatSocket.connect(function(frame) {
				  
				$scope.username = frame.headers['user-name'];

				chatSocket.subscribe("/app/chat.participants", function(message) {
					$scope.participants = JSON.parse(message.body);
				});
				  
				chatSocket.subscribe("/topic/chat.login", function(message) {
					$scope.participants.unshift({username: JSON.parse(message.body).username, typing : false});
				});
		        	 
				chatSocket.subscribe("/topic/chat.logout", function(message) {
					var username = JSON.parse(message.body).username;
					for(var index in $scope.participants) {
						if($scope.participants[index].username == username) {
							$scope.participants.splice(index, 1);
						}
					}
		        });
		        	 
				chatSocket.subscribe("/topic/chat.typing", function(message) {
					var parsed = JSON.parse(message.body);
					if(parsed.username == $scope.username) return;
				  					
					for(var index in $scope.participants) {
						var participant = $scope.participants[index];
						  
						if(participant.username == parsed.username) {
							$scope.participants[index].typing = parsed.typing;
						}
				  	} 
				});
		        	 
				chatSocket.subscribe("/topic/chat.message", function(message) {
					$scope.messages.unshift(JSON.parse(message.body));
		        });
				  
				chatSocket.subscribe("/user/exchange/amq.direct/chat.message", function(message) {
					var parsed = JSON.parse(message.body);
					parsed.priv = true;
					$scope.privateMessages.unshift(parsed);
		        });
				  
				chatSocket.subscribe("/user/exchange/amq.direct/errors", function(message) {
					toaster.pop('error', "Error", message.body);
		        });
		          
			}, function(error) {
				toaster.pop('error', 'Error', 'Connection error ' + error);
				
		    });
            calculate_popups();
            
            $window.addEventListener("resize", calculate_popups);
            $window.addEventListener("load", calculate_popups);
		};
		
		Array.remove = function(array, from, to) {
            var rest = array.slice((to || from) + 1 || array.length);
            array.length = from < 0 ? array.length + from : from;
            return array.push.apply(array, rest);
        };
    
        //this variable represents the total number of popups can be displayed according to the viewport width
        var total_popups = 0;
        
        //arrays of popups ids
        var popups = [];
    
        //this is used to close a popup
        $scope.close_popup = function(id)
        {
            for(var iii = 0; iii < popups.length; iii++)
            {
                if(id == popups[iii])
                {
                    Array.remove(popups, iii);
                    
                    document.getElementById(id).style.display = "none";
                    
                    calculate_popups();
                    
                    return;
                }
            }   
        }
    
        //displays the popups. Displays based on the maximum number of popups that can be displayed on the current viewport width
        var display_popups = function()
        {
            var right = 220;
            
            var iii = 0;
            for(iii; iii < total_popups; iii++)
            {
                if(popups[iii] != undefined)
                {
                    var element = document.getElementById(popups[iii]);
                    element.style.right = right + "px";
                    right = right + 320;
                    element.style.display = "block";
                }
            }
            
            for(var jjj = iii; jjj < popups.length; jjj++)
            {
                var element = document.getElementById(popups[jjj]);
                element.style.display = "none";
            }
        }
        
        //creates markup for a new popup. Adds the id to popups array.
        $scope.registerPopup = function(id, name)
        {
            
            for(var iii = 0; iii < popups.length; iii++)
            {   
                //already registered. Bring it to front.
                if(id == popups[iii])
                {
                    Array.remove(popups, iii);
                
                    popups.unshift(id);
                    
                    calculate_popups();
                    
                    
                    return;
                }
            }               
            
            var element = '<div class="popup-box chat-popup" id="'+ id +'">';
            element = element + '<div class="popup-head">';
            element = element + '<div class="popup-head-left">'+ name +'</div>';
            element = element + '<div class="popup-head-right"><a href="" ng-click="close_popup(\''+ id +'\')">&#10005;</a></div>';
            element = element + '<div style="clear: both"></div></div>';
            element = element + '<div class ="private-box"><div ng-repeat="message in privateMessages"><div ng-if=" message.username == \''+name+'\' || message.to == \''+name+'\'"><small private-message></small></div></div>';
            element = element + '</div><input type="text" ng-model="newMessage" class = "private-input form-control" ng-keyup="$event.keyCode == 13 ? sendPrivate(\''+name+'\') : startTyping()"/>';
 
            angular.element(document.body).append($compile(element)($scope));
            popups.unshift(id);
            console.log(id);
            $scope.privateSending(id);     	
            
        }
        
        //calculate the total number of popups suitable and then populate the toatal_popups variable.
        var calculate_popups = function()
        {
            var width = $window.innerWidth;
            if(width < 540)
            {
                total_popups = 0;
            }
            else
            {
                width = width - 200;
                //320 is width of a single popup box
                total_popups = parseInt(width/320);
            }
            
            display_popups();
            
        }
        
		initStompClient();
		console.log(total_popups);
		
	}]);
	