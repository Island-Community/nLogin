package com.nickuc.login.platform.session;

import com.nickuc.login.auth.login.IncomingLoginGate;

public interface CachedSessionHandler {
   void performTask();

   IncomingLoginGate loadIncomingLoginGate();

   TightSenderAdapter[] findValues();

   void handleTask();

   OutgoingAccountHandler loadOutgoingAccountHandler();

   boolean verifyState(String target);

   TightConnectionContract retrieveTightConnectionContract();

   void processTask();

   void dispatchTask();

   void updateTask();

   LinkedSessionHandler computeLinkedSessionHandler(boolean target);

   ParentAccountHandler resolveParentAccountHandler();

   void sendTask();

   OutgoingAccountHandler getOutgoingAccountHandler();
}
