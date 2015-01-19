package com.test;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.directwebremoting.Browser;
import org.directwebremoting.ScriptBuffer;
import org.directwebremoting.ScriptSession;
import org.directwebremoting.ScriptSessionFilter;
import org.directwebremoting.ServerContextFactory;
import org.directwebremoting.WebContextFactory;
import org.directwebremoting.extend.ScriptSessionManager;

import com.test.listener.myScriptSessionListener;

public class Message {

	private static Map<String, ScriptSession> currentUsers = new HashMap<String, ScriptSession>();
	private static ScriptSessionManager manager = ServerContextFactory.get()
			.getContainer().getBean(ScriptSessionManager.class);

	private static myScriptSessionListener listener;

	static {
		manager.addScriptSessionListener(getScriptSessionListener());
	}

	public static myScriptSessionListener getScriptSessionListener() {
		if (listener == null)
			listener = new myScriptSessionListener();
		return listener;
	}

	public void addMessage(String userid, String message) {
		final String userId = userid;
		final String autoMessage = message;
		Browser.withAllSessionsFiltered(new ScriptSessionFilter() {
			public boolean match(ScriptSession session) {
				if (userId == null || userId.equals(""))
					return true;
				return userId.equals(session.getAttribute("name"));
			}
		}, new Runnable() {
			private ScriptBuffer script = new ScriptBuffer();
			public void run() {
				script.appendCall("receiveMessages", autoMessage);
				Collection<ScriptSession> sessions = Browser
						.getTargetSessions();
				for (ScriptSession scriptSession : sessions) {
						scriptSession.addScript(script);
				}
			}
		});
	}

	public void push(ScriptSession session, String message, String reciveFunction) {
		final ScriptSession userSession = session;
		final String autoMessage = message;
		final String function = reciveFunction;
		Browser.withAllSessionsFiltered(new ScriptSessionFilter() {
			public boolean match(ScriptSession session) {
				if(userSession == null)return true;
				return session.equals(userSession);
			}
		}, new Runnable() {
			private ScriptBuffer script = new ScriptBuffer();
			public void run() {
				script.appendCall(function, autoMessage);
				Collection<ScriptSession> sessions = Browser
						.getTargetSessions();
				for (ScriptSession scriptSession : sessions) {
						scriptSession.addScript(script);
				}
			}
		});
	}

	public void onPageLoad(String name) {
		if (currentUsers == null) {
			return;
		}
		if (currentUsers.get(name) != null) {
			push(currentUsers.get(name), name, "alreadyOnline");
			currentUsers.remove(name);
		}
		WebContextFactory.get().getScriptSession().setAttribute("name", name);
		currentUsers.put(name, WebContextFactory.get().getScriptSession());
		
		push(null, getUsers(), "updateUsers");
	}


	public void unlogUser(String name) {
		if (currentUsers.isEmpty() || currentUsers.get(name) == null) {
			return;
		}
		ScriptSession user = WebContextFactory.get().getScriptSession();
		currentUsers.remove(user.getAttribute("name"));
		user.setAttribute("name", name);
		push(null, getUsers(), "updateUsers");
	}

	public String getUsers() {
		if (currentUsers.isEmpty()) {	
			return "no user!";
		}
		String users = "";
		for (ScriptSession user : (Collection<ScriptSession>) currentUsers
				.values()) {
			users += user.getAttribute("name") + ", ";
		}
		return users;
	}

}