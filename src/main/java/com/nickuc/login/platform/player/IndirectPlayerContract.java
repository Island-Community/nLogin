package com.nickuc.login.platform.player;

public interface IndirectPlayerContract {
   boolean canState(String target);

   String computeMessage(String target);

   boolean verifyState(String target, String input);
}
