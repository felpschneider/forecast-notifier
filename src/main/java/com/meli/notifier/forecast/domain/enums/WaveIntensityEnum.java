package com.meli.notifier.forecast.domain.enums;

import lombok.Getter;

@Getter
public enum WaveIntensityEnum {

    FRACO("fraco", "Fraco"),
    MODERADO("moderado", "Moderado"),
    FORTE("forte", "Forte"),
    DESCONHECIDO("desconhecido", "Desconhecido");

    private final String code;
    private final String description;

    WaveIntensityEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static WaveIntensityEnum fromCode(String code) {
        if (code == null) {
            return DESCONHECIDO;
        }

        String normalizedCode = code.toLowerCase();
        for (WaveIntensityEnum intensity : values()) {
            if (intensity.getCode().equals(normalizedCode)) {
                return intensity;
            }
        }
        return DESCONHECIDO;
    }
}
