package com.nickuc.login.listener.bukkit;

import com.nickuc.login.platform.connection.QuickConnectionContract;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import org.bukkit.entity.Player;

public class TitleFilter implements QuickConnectionContract {
   private final Method method;
   private final Constructor<?> constructor;

   public TitleFilter() {
      Class target = Class.forName("org.github.paperspigot.Title");
      this.constructor = target.getConstructor(String.class, String.class, int.class, int.class, int.class);
      this.method = Player.class.getMethod("sendTitle", target);
   }

   @Override
   public void performPlayer(Player target, String input, String output, int context, int data, int value) {
      if (input.isEmpty() && output.isEmpty()) {
         this.dispatchPlayer(target);
      } else {
         if (input.isEmpty()) {
            input = "§r";
         }

         if (output.isEmpty()) {
            output = "§r";
         }

         try {
            Object result = this.constructor.newInstance(input, output, context, data, value);
            this.method.invoke(target, result);
         } catch (ReflectiveOperationException request) {
            throw new RuntimeException("Cannot send title for " + target.getName() + "!", request);
         }
      }
   }

   @Override
   public void dispatchPlayer(Player target) {
      target.resetTitle();
   }
}
