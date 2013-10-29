package com.ambimmort.dmc.server.core;
import javax.servlet.http.HttpSession;

public interface IHttpSessionAware {
	public void bind(HttpSession session);
}
