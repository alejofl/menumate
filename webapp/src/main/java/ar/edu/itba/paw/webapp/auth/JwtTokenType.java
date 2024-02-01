package ar.edu.itba.paw.webapp.auth;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum JwtTokenType {

    REFRESH("refresh"),
    ACCESS("access");

    private final String type;

    private static final JwtTokenType[] VALUES = JwtTokenType.values();

    private static final Map<String, JwtTokenType> VALUES_BY_CODE = Arrays.stream(VALUES).collect(Collectors.toMap(r -> r.type, r -> r));

    JwtTokenType(String type){
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static JwtTokenType fromOrdinal(int ordinal) {
        return ordinal >= 0 && ordinal < VALUES.length ? VALUES[ordinal] : null;
    }

    public static JwtTokenType fromCode(String code) {
        return code == null ? null : VALUES_BY_CODE.get(code.trim().toLowerCase());
    }

    public boolean isRefreshToken() {
        return this == REFRESH;
    }
}