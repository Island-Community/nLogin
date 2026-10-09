package com.nickuc.login.platform.player;

@FunctionalInterface
public interface PlayerContract<T> {
   void done(T target);
}
