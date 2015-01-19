package com.test.listener;

import org.directwebremoting.event.ScriptSessionEvent;
import org.directwebremoting.event.ScriptSessionListener;

public class myScriptSessionListener implements ScriptSessionListener {
	public void sessionCreated(ScriptSessionEvent ev) {
		System.out.println("a ScriptSession is created!");
	}
	public void sessionDestroyed(ScriptSessionEvent ev) {
		System.out.println("a ScriptSession is distroyed: " + ev.getSession().getAttribute("name"));
	}

}
