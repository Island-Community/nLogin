package com.nickuc.login.i18n;

import com.nickuc.login.auth.message.FastMessageHandler;
import com.nickuc.login.model.StrictPlatformCatalog;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import javax.annotation.Nullable;

public class LocaleBundle {
   @Nullable
   public static StrictPlatformCatalog loadStrictPlatformCatalog() {
      try {
         InputStream instance = FastMessageHandler.buildInputStream("META-INF/LANGUAGE.ID", true);

         try {
            if (instance != null) {
               BufferedReader target = new BufferedReader(new InputStreamReader(instance, StandardCharsets.UTF_8));

               try {
                  return StrictPlatformCatalog.createStrictPlatformCatalog(target.readLine());
               } finally {
                  if (Collections.singletonList(target).get(0) != null) {
                     target.close();
                  }
               }
            }
         } finally {
            if (Collections.singletonList(instance).get(0) != null) {
               instance.close();
            }
         }

         return null;
      } catch (IOException item) {
         throw new RuntimeException(item);
      }
   }
}
