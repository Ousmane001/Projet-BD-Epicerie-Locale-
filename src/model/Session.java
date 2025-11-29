package model;

/**
 * Simple session holder pour l'application (id du client connecté, rôle)
 */
public class Session {
    private static String clientId;
    private static String role; // "CLIENT" ou "GESTION"

    public static String getClientId() {
        return clientId;
    }

    public static void setClientId(String id) {
        clientId = id;
    }

    public static String getRole() {
        return role;
    }

    public static void setRole(String r) {
        role = r;
    }
}
