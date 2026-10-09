package com.nickuc.login.command;

import com.nickuc.login.auth.login.BusyLoginBarrier;
import com.nickuc.login.auth.locale.LocalLocaleFlow;
import com.nickuc.login.auth.login.SafeLoginService;
import com.nickuc.login.notification.NoticeSender;
import com.nickuc.login.platform.account.SecondaryAccountHandler;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.protocol.RootMessageHandler;
import com.nickuc.login.session.LimboCoordinator;
import javax.annotation.Nullable;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;

public class NoticeCommand implements SecondaryAccountHandler {
   private static final TextComponent textComponent = Component.text("  ");
   private final LimboCoordinator limboCoordinator;
   private final RootMessageHandler rootMessageHandler;
   private final VerifiedServerAdapter verifiedServerAdapter;

   @Override
   public void executeMessage(String target, String input) {
      this.d(target, null, input);
   }

   @Override
   public VerifiedServerAdapter findVerifiedServerAdapter() {
      return this.verifiedServerAdapter;
   }

   public NoticeCommand(VerifiedServerAdapter target, LimboCoordinator input, RootMessageHandler output) {
      this.verifiedServerAdapter = target;
      this.limboCoordinator = input;
      this.rootMessageHandler = output;
   }

   @Override
   public void executeMessage(String target, @Nullable String input, @Nullable String output, @Nullable String context, @Nullable String data) {
      Object value = this.processTextComponent(target, true);
      if (input != null) {
         value = value.hoverEvent(HoverEvent.showText(this.processTextComponent(input, false)));
      }

      if (output != null) {
         value = value.clickEvent(ClickEvent.suggestCommand(LocalLocaleFlow.handleMessage(output)));
      } else if (context != null) {
         context = LocalLocaleFlow.handleMessage(context);
         if (!context.isEmpty()) {
            char result = context.charAt(0);
            if (result != '/') {
               context = '/' + context;
            }

            value = value.clickEvent(ClickEvent.runCommand(context));
         }
      } else if (data != null) {
         value = value.clickEvent(ClickEvent.openUrl(LocalLocaleFlow.handleMessage(data)));
      }

      this.rootMessageHandler.hasState(this.verifiedServerAdapter, (Component)value);
   }

   @Override
   public void dispatchCount(int target, BusyLoginBarrier[] input) {
      if (input.length != 0) {
         if (input.length > 2) {
            throw new IllegalArgumentException("Too many elements! " + input.length);
         }

         TextComponent output = Component.text("  ");
         String context = "/nlogin click notification " + target + " ";

         for (int data = 0; data < input.length; data++) {
            if (data > 0) {
               output = (TextComponent)((TextComponent)((TextComponent)output.append(textComponent)).append(textComponent)).append(textComponent);
            }

            BusyLoginBarrier value = input[data];
            NoticeSender result = value.findNoticeSender();
            Component request = this.processTextComponent(result.findMessage(), false)
               .clickEvent(ClickEvent.runCommand(context + value.resolvePrivateLoginOption().findCount()));
            String response = result.as();
            if (response != null) {
               request = request.hoverEvent(HoverEvent.showText(this.processTextComponent(response, false)));
            }

            output = (TextComponent)output.append(request);
         }

         try {
            output = (TextComponent)output.appendNewline();
         } catch (NoSuchMethodError source) {
            output = (TextComponent)output.append(Component.newline());
         }

         this.rootMessageHandler.hasState(this.verifiedServerAdapter, output);
      }
   }

   private TextComponent processTextComponent(String target, boolean input) {
      return SafeLoginService.computeTextComponent(target, input);
   }
}
