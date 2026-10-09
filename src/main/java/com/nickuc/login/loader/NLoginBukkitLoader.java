package com.nickuc.login.loader;

import com.nickuc.login.loader.BuildData;
import com.nickuc.login.loader.platform.BukkitLoader;

public class NLoginBukkitLoader
extends BukkitLoader {
    public NLoginBukkitLoader() {
        super("com.nickuc.login.bukkit.BukkitPlatform");
        NLoginBukkitLoader.printBanner();
    }

    @Override
    public String getVersion() {
        return BuildData.getVersion();
    }

    private static void printBanner() {
        System.out.println(new StringBuilder("m0[\u001b \u2557\u2588\u2588\u2588\u2588\u2588\u2588\u2557\u2588\u2588  \u2557\u2588\u2588m43[\u001b").reverse().toString());
        System.out.println(new StringBuilder("m0[\u001b\u2557\u2588\u2588\u2550\u2550\u2554\u2588\u2588\u255d\u2554\u2588\u2588\u2557\u2588\u2588\u255am43[\u001b").reverse().toString());
        System.out.println(new StringBuilder("m0[\u001b\u2551\u2588\u2588  \u2551\u2588\u2588 \u255d\u2554\u2588\u2588\u2588\u255a m73[\u001b").reverse().toString());
        System.out.println(new StringBuilder("m0[\u001b\u2551\u2588\u2588  \u2551\u2588\u2588 \u2557\u2588\u2588\u2554\u2588\u2588 m73[\u001b").reverse().toString());
        System.out.println(new StringBuilder("m0[\u001b\u255d\u2554\u2588\u2588\u2588\u2588\u2588\u2588\u2557\u2588\u2588 \u255d\u2554\u2588\u2588m43[\u001b").reverse().toString());
        System.out.println(new StringBuilder("m0[\u001b \u255d\u2550\u2550\u2550\u2550\u2550\u255a\u255d\u2550\u255a  \u255d\u2550\u255am43[\u001b").reverse().toString());
        System.out.println(new StringBuilder("").reverse().toString());
        System.out.println(new StringBuilder("m0[\u001bodiuqilonegortinm73[\u001b yb dfboed ylluf & dekcarCm43[\u001b").reverse().toString());
    }

}

