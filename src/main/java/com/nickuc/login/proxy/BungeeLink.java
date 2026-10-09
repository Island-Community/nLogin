package com.nickuc.login.proxy;

import com.nickuc.login.platform.command.SilentCommandHandler;
import java.lang.reflect.Array;
import java.util.Objects;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.entity.Player.Spigot;

public class BungeeLink implements SilentCommandHandler {
   public BungeeLink() {
      Objects.requireNonNull(Spigot.class.getMethod("sendMessage", ChatMessageType.class, Array.newInstance(BaseComponent.class, 0).getClass()));
   }

   @Override
   public void send(Player target, String input) {
      target.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(input));
   }
}
