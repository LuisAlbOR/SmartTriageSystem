package Backend.DTO;

/**
 * DTO (Data Transfer Object) para la pantalla pública.
 * Contiene solo la información no sensible de un turno
 * (incluyendo el nombre del paciente) para mostrar en la sala de espera.
 */
public class TurnoPublicoDTO {

    private int id; // El ID del turno (para mostrar "Turno #X")
    private String nombre; // El nombre del paciente
    private String estado; // EN_COLA, ATENDIENDO
    private int prioridad; // Se usa para ordenar, aunque no se muestre

    // Constructor vacío (necesario para algunas librerías de serialización)
    public TurnoPublicoDTO() {
    }

    // Constructor para facilitar la creación en el DAO
    public TurnoPublicoDTO(int id, String nombre, String estado, int prioridad) {
        this.id = id;
        this.nombre = nombre;
        this.estado = estado;
        this.prioridad = prioridad;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public int getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(int prioridad) {
        this.prioridad = prioridad;
    }
}