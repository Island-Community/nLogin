package com.nickuc.login.platform.command;

public interface StrictCommandHandler extends ChildListenerContract {
   boolean fetchState();

   String loadMessage();

   void performTask();
}
