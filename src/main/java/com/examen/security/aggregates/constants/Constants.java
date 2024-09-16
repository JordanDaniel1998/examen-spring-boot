package com.examen.security.aggregates.constants;

public class Constants {
    public static final String USU_CREA = "Jordan Daniel";
    public static final Integer ERROR_DNI_CODE = 2004;
    public static final String USER_NOT_EXIST_DNI = "USUARIO NO EXISTE EN EL SISTEMA DE APIS";
    public static final Integer ERROR_TRX_CODE = 4009;
    public static final String ERROR_TRX_MESS = "ERROR DURANTE LA TRANSACCION";
    public static final Integer OK_DNI_CODE = 2000;
    public static final String USER_CREATE_MESSAGE = "USUARIO CREADO CORRECTAMENTE";
    public static final String USER_FOUND_MESSAGE = "USUARIO ENCONTRADO";
    public static final String USER_UPDATE_MESSAGE = "USUARIO ACTUALIZADO";
    public static final String USER_NOT_FOUND_MESSAGE = "USUARIO NO ENCONTRADO";
    public static final Integer REDIS_EXP = 5;
    public static final String REDIS_KEY_API_PERSON = "MS:APIS:SECURITY:EXTERNAS:";
    public static final Integer ERROR_DNI_CODE_USER = 4004;
    public static final Integer ERROR_INFORMATION_USER = 4000;
    public static final String USER_NOT_UPDATE = "TODOS LOS CAMPOS SON OBLIGATORIOS";
    public static final String USER_EXISTS_DNI = "EL USUARIO YA EXISTE";
    public static final Boolean STATUS_ACTIVE = true;
    public static final Boolean STATUS_INACTIVE = false;
    public static final String USER_STATE_INACTIVE = "USUARIO ELIMINADO";
    public static final String USERS_ACTIVOS_LIST = "LISTADO DE USUARIOS ACTIVOS";
    public static final Integer ERROR_CODE_LIST_EMPTY= 2009;
    public static final String USERS_NOT_FOUND = "NO EXISTE USUARIOS";

    public static final String CLAVE_AccountNonExpired ="isAccountNonExpired";
    public static final String CLAVE_AccountNonLocked ="isAccountNonLocked";
    public static final String CLAVE_CredentialsNonExpired = "isCredentialsNonExpired";
    public static final String CLAVE_Enabled = "isEnabled";
    public static final String CLAIM_ROLE = "rol";

    public static final String ENPOINTS_PERMIT = "/api/authentication/v1/**";
    public static final String ENPOINTS_USER = "/api/users/v1/**";
    public static final String ENPOINTS_ADMIN = "/api/users/v1/**";

}
