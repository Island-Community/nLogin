package com.nickuc.login.api.enums;

public enum TwoFactorType {
    DISCORD,
    EMAIL;


    public static TwoFactorType convert(Enum<?> twoFactorType) {
        switch (twoFactorType.name()) {
            case "DISCORD": {
                return DISCORD;
            }
            case "EMAIL": {
                return EMAIL;
            }
        }
        throw new IllegalArgumentException("Unsupported type " + twoFactorType + ".");
    }
}

