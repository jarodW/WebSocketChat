package com.jarod.chat.repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.jarod.chat.models.Login;


public class ParticipantRepository {

	private Map<String, Login> activeSessions = new ConcurrentHashMap<>();

	public void add(String sessionId, Login event) {
		activeSessions.put(sessionId, event);
	}

	public Login getParticipant(String sessionId) {
		return activeSessions.get(sessionId);
	}

	public void removeParticipant(String sessionId) {
		activeSessions.remove(sessionId);
	}

	public Map<String, Login> getActiveSessions() {
		return activeSessions;
	}

	public void setActiveSessions(Map<String, Login> activeSessions) {
		this.activeSessions = activeSessions;
	}
}
