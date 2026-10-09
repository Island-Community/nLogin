package com.nickuc.login.platform.connection;

import com.nickuc.login.listener.bukkit.SettingsGuard;
import javax.annotation.Nullable;

public interface StrictConnectionContract {
   @Nullable
   SettingsGuard findSettingsGuard();
}
