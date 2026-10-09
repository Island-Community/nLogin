package com.nickuc.login.api.enums;

import javax.annotation.Nullable;

public enum AccountType {
    PREMIUM,
    BEDROCK,
    OFFLINE;


    @Nullable
    public static AccountType convert(Enum<?> twoFactorType) {
        switch (twoFactorType.name()) {
            case "PREMIUM": {
                return PREMIUM;
            }
            case "BEDROCK": {
                return BEDROCK;
            }
            case "OFFLINE": {
                return OFFLINE;
            }
            case "UNREGISTERED": {
                return null;
            }
        }
        throw new IllegalArgumentException("Unsupported type " + twoFactorType + ".");
    }
}

