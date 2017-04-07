package com.jarod.chat.config;

import java.util.Arrays;import java.util.HashSet;import java.util.Set;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.socket.config.WebSocketMessageBrokerStats;

import com.jarod.chat.listener.PresenceEventListener;
import com.jarod.chat.repository.ParticipantRepository;

@Configuration
public class ChatConfig {

	@Bean
	//Tracks when users join leave
	public PresenceEventListener presenceEventListener(SimpMessagingTemplate messagingTemplate) {
		PresenceEventListener presence = new PresenceEventListener(messagingTemplate, participantRepository());
		return presence;
	}

	@Bean
	//Contains a list of connected users
	public ParticipantRepository participantRepository() {
		return new ParticipantRepository();
	}
}
