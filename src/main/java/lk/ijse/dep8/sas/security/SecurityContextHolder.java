package lk.ijse.dep8.sas.security;

public class SecurityContextHolder {

    private static Principal principal;

    public static Principal getPrincipal() {
        return principal;
    }

    public static void setPrincipal(Principal principal) {
        SecurityContextHolder.principal = principal;
    }
}