package com.meli.notifier.forecast.domain.enums;

import lombok.Getter;

@Getter
public enum WeatherConditionEnum {

    ENCOBERTO_CHUVAS_ISOLADAS("ec", "Encoberto com Chuvas Isoladas"),
    CHUVAS_ISOLADAS("ci", "Chuvas Isoladas"),
    CHUVA("c", "Chuva"),
    INSTAVEL("in", "Instável"),
    POSSIBILIDADE_PANCADAS_CHUVA("pp", "Possibilidade de Pancadas de Chuva"),
    CHUVA_PELA_MANHA("cm", "Chuva pela Manhã"),
    CHUVA_A_NOITE("cn", "Chuva a Noite"),
    PANCADAS_CHUVA_A_TARDE("pt", "Pancadas de Chuva a Tarde"),
    PANCADAS_CHUVA_PELA_MANHA("pm", "Pancadas de Chuva pela Manhã"),
    NUBLADO_PANCADAS_CHUVA("np", "Nublado e Pancadas de Chuva"),
    PANCADAS_CHUVA("pc", "Pancadas de Chuva"),
    PARCIALMENTE_NUBLADO("pn", "Parcialmente Nublado"),
    CHUVISCO("cv", "Chuvisco"),
    CHUVOSO("ch", "Chuvoso"),
    TEMPESTADE("t", "Tempestade"),
    PREDOMINIO_SOL("ps", "Predomínio de Sol"),
    ENCOBERTO("e", "Encoberto"),
    NUBLADO("n", "Nublado"),
    CEU_CLARO("cl", "Céu Claro"),
    NEVOEIRO("nv", "Nevoeiro"),
    GEADA("g", "Geada"),
    NEVE("ne", "Neve"),
    NAO_DEFINIDO("nd", "Não Definido"),
    PANCADAS_CHUVA_A_NOITE("pnt", "Pancadas de Chuva a Noite"),
    POSSIBILIDADE_CHUVA("psc", "Possibilidade de Chuva"),
    POSSIBILIDADE_CHUVA_PELA_MANHA("pcm", "Possibilidade de Chuva pela Manhã"),
    POSSIBILIDADE_CHUVA_A_TARDE("pct", "Possibilidade de Chuva a Tarde"),
    POSSIBILIDADE_CHUVA_A_NOITE("pcn", "Possibilidade de Chuva a Noite"),
    NUBLADO_PANCADAS_A_TARDE("npt", "Nublado com Pancadas a Tarde"),
    NUBLADO_PANCADAS_A_NOITE("npn", "Nublado com Pancadas a Noite"),
    NUBLADO_POSSIBILIDADE_CHUVA_A_NOITE("ncn", "Nublado com Possibilidade de Chuva a Noite"),
    NUBLADO_POSSIBILIDADE_CHUVA_A_TARDE("nct", "Nublado com Possibilidade de Chuva a Tarde"),
    NUBLADO_POSSIBILIDADE_CHUVA_PELA_MANHA("ncm", "Nublado com Possibilidade de Chuva pela Manhã"),
    NUBLADO_PANCADAS_PELA_MANHA("npm", "Nublado com Pancadas pela Manhã"),
    NUBLADO_POSSIBILIDADE_CHUVA("npp", "Nublado com Possibilidade de Chuva"),
    VARIACAO_NEBULOSIDADE("vn", "Variação de Nebulosidade"),
    CHUVA_A_TARDE("ct", "Chuva a Tarde"),
    POSSIBILIDADE_PANCADAS_CHUVA_A_NOITE("ppn", "Possibilidade de Pancadas de Chuva a Noite"),
    POSSIBILIDADE_PANCADAS_CHUVA_A_TARDE("ppt", "Possibilidade de Pancadas de Chuva a Tarde"),
    POSSIBILIDADE_PANCADAS_CHUVA_PELA_MANHA("ppm", "Possibilidade de Pancadas de Chuva pela Manhã"),
    DESCONHECIDO("", "Desconhecido");

    private final String code;
    private final String description;

    WeatherConditionEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static WeatherConditionEnum fromCode(String code) {
        if (code == null) {
            return DESCONHECIDO;
        }

        for (WeatherConditionEnum condition : values()) {
            if (condition.getCode().equals(code)) {
                return condition;
            }
        }
        return DESCONHECIDO;
    }
}
