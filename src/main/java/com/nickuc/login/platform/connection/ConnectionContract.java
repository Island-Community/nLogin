package com.nickuc.login.platform.connection;

import javax.annotation.Nullable;

public interface ConnectionContract {
   boolean filter(@Nullable String target, String input, Object... output);
}
