package com.nickuc.login.platform.sender;

public interface SenderAdapter extends ChildListenerContract {
   void processMessage(String target, Throwable input);

   void performMessage(String target);

   void dispatchMessage(String target);

   void handleMessage(String target, Throwable input);

   void updateMessage(String target);
}
