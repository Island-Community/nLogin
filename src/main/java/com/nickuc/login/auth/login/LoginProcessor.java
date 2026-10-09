package com.nickuc.login.auth.login;

import java.net.URL;
import java.net.URLClassLoader;

public class LoginProcessor extends URLClassLoader {
   public LoginProcessor(URL[] target) {
      super(target, ClassLoader.getSystemClassLoader().getParent());
   }

   static {
      ClassLoader.registerAsParallelCapable();
   }
}
