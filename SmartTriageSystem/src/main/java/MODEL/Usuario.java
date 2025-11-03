package MODEL;

public class Usuario {
    private int id;
    private String login;
    private String hash; // Almacena el hash seguro (e.g., BCrypt)
    private String rol;  // 'recepcion', 'medico', 'pantalla', 'admin'
    private boolean activo;

    // Constructor vacío
    public Usuario() {}

    // Getters
    public int getId() { return id; }
    public String getLogin() { return login; }
    public String getHash() { return hash; }
    public String getRol() { return rol; }
    public boolean isActivo() { return activo; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setLogin(String login) { this.login = login; }
    public void setHash(String hash) { this.hash = hash; }
    public void setRol(String rol) { this.rol = rol; }
    public void setActivo(boolean activo) { this.activo = activo; }
}