package com.example.sensordemovimiento;

public class Utils {
    public static final String MyPREFERENCES = "LoginRDS";
    public static final String TOKEN = "token";

    public static String getServidor(String ip, int puerto){
        return "http://" + ip + ":" + puerto;
    }

    public static String endPointLogin = "/api-token-auth/";

    /* error RED */
    public static final String ERROR_LOGIN_RED = "Respuesta no procesable. Intente más tarde.";
    public static final String ERROR_LOGIN_RED_ACCESO = "Sin respuesta del servidor. Intente más tarde.";
}
