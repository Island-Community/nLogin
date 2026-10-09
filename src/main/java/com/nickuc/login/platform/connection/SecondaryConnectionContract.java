package com.nickuc.login.platform.connection;

import java.util.UUID;

public interface SecondaryConnectionContract {
   boolean fetchState();

   boolean isState(UUID target);
}
