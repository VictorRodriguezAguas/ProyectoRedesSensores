package com.example.sensordemovimiento;

/**
 * Esta clase contiene mensajes y constantes que son fijas y compartidas por varias activities.
 * @author: Mauricio Leiton Lázaro(mdleiton)
 * @version: 1.0
 */
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
    //configuracion servidor
    public static final String CONF_ACTUALIZADO = "IP y puerto actualizados exitosamente. ";
    public static final String CONF_ERROR_1 = "IP y/o puerto del servidor no configurado. ";
    public static final String CONF_ERROR_2 = "IP y/o puerto vacios.";

}
