package com.nickuc.login.platform.listener;

public interface ListenerContract {
   Class<?> getPlayerClass();

   boolean callEvent(Object target);
}
