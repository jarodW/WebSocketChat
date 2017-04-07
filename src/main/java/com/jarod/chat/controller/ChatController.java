package com.jarod.chat.controller;

import java.security.Principal;import java.util.Collection;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.jarod.chat.models.ChatMessage;
import com.jarod.chat.models.Login;
import com.jarod.chat.models.User;
import com.jarod.chat.repository.ParticipantRepository;
import com.jarod.chat.repository.UserRepository;


/**
 * Controller that handles WebSocket chat messages
 */
@Controller
public class ChatController {
	
		
	@Autowired private ParticipantRepository participantRepository;
	
	@Autowired private SimpMessagingTemplate simpMessagingTemplate;
	private UserRepository userRepository; 
	@Autowired
	public ChatController(UserRepository userRepository){
		this.userRepository = userRepository;
	}
	@SubscribeMapping("/chat.participants")
	public Collection<Login> retrieveParticipants() {
		return participantRepository.getActiveSessions().values();
	}
	@MessageMapping("/chat.message")
	public ChatMessage filterMessage(@Payload ChatMessage message, Principal principal) {
		
		message.setUsername(principal.getName());
		return message;
	}
	@MessageMapping("/chat.private.{username}")
	public void filterPrivateMessage(@Payload ChatMessage message, @DestinationVariable("username") String username, Principal principal) {
		
		message.setUsername(principal.getName());
		simpMessagingTemplate.convertAndSend("/user/" + username + "/exchange/amq.direct/chat.message", message);
	}
	
	@RequestMapping(value="/register", method = RequestMethod.POST)
	ResponseEntity<?> add(@RequestBody User user) throws JSONException {
		JSONObject obj=new JSONObject();   
		List list = userRepository.findByUsername(user.getUsername());
		System.out.println(list.size());
		if(list.isEmpty() == false){
			  obj.put("error","Username not available");    
		      return ResponseEntity.status(HttpStatus.CONFLICT).body(obj.toString());
		}
		userRepository.save(user);
		obj.put("success","User Created"); 
		return ResponseEntity.status(HttpStatus.CREATED).body(obj.toString());
	}
	

}