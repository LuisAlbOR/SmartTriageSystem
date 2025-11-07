package Frontend.DTO;

// Un DTO simple para poblar la tabla de la pantalla pública.
// El servidor debe ser actualizado para enviar una lista de estos.
public class TurnoPublicoDTO {
    private int id;
    private String nombre; // Nombre del paciente (¡no el ID!)
    private String estado; // EN_COLA, ATENDIENDO
    private int prioridad;

    // Getters y Setters (o hacerlos públicos)
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public int getPrioridad() { return prioridad; }
    public void setPrioridad(int prioridad) { this.prioridad = prioridad; }
}