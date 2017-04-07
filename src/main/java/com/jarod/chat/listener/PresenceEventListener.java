package com.jarod.chat.listener;

import java.util.Optional;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.jarod.chat.models.Login;
import com.jarod.chat.models.Logout;
import com.jarod.chat.repository.ParticipantRepository;

//notifies when a user logs in or logs out. 
public class PresenceEventListener {
	
	private ParticipantRepository participantRepository;
	
	private SimpMessagingTemplate messagingTemplate;
	
	private String loginDestination = "/topic/chat.login";
	
	private String logoutDestination = "/topic/chat.logout";
	
	public PresenceEventListener(SimpMessagingTemplate messagingTemplate, ParticipantRepository participantRepository) {
		this.messagingTemplate = messagingTemplate;
		this.participantRepository = participantRepository;
	}
	
	//handle logins
	@EventListener
	private void handleSessionConnected(SessionConnectEvent event) {
		System.out.println("session connect event");
		SimpMessageHeaderAccessor headers = SimpMessageHeaderAccessor.wrap(event.getMessage());

		String username = headers.getUser().getName();
		Login loginEvent = new Login(username);
		messagingTemplate.convertAndSend(loginDestination, loginEvent);
		
		participantRepository.add(headers.getSessionId(), loginEvent);
	}
	
	//handle logouts
	@EventListener
	private void handleSessionDisconnect(SessionDisconnectEvent event) {
		
		Optional.ofNullable(participantRepository.getParticipant(event.getSessionId()))
				.ifPresent(login -> {
					messagingTemplate.convertAndSend(logoutDestination, new Logout(login.getUsername()));
					participantRepository.removeParticipant(event.getSessionId());
				});
	}
}
