package com.nickuc.login.platform.server;

public interface LinkedServerAdapter extends IndirectPlayerContract {
   @Override
   default boolean canState(String target) {
      return false;
   }

   @Override
   default String computeMessage(String target) {
      throw new UnsupportedOperationException();
   }
}
