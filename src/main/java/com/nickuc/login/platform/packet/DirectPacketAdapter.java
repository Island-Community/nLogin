package com.nickuc.login.platform.packet;

import org.json.JSONObject;
import javax.annotation.Nonnull;

public interface DirectPacketAdapter<T> {
   JSONObject buildJSONObject(@Nonnull T target);

   T loadObject(@Nonnull JSONObject target);

   Class<?> loadClass();
}
