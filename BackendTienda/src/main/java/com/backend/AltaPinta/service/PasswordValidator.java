package com.backend.AltaPinta.service;

public class PasswordValidator {

    // RF035: mínimo 8 caracteres, una mayúscula, una minúscula y un número
    private static final String REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$";

    public static final String MENSAJE_ERROR =
            "La contraseña debe tener mínimo 8 caracteres, una mayúscula, una minúscula y un número";

    private PasswordValidator() {}

    public static boolean esValida(String password) {
        return password != null && password.matches(REGEX);
    }
}
