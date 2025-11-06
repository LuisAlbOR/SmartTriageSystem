package Backend.MODEL;

import java.sql.Timestamp;

public class Bitacora {
    private int id;
    private Integer usuarioId; // Wrapper Integer para NULLABLE
    private Integer turnoId;   // Wrapper Integer para NULLABLE
    private String evento;
    private String detalle;    // Mapeado desde JSONB
    private Timestamp ts;

    // Constructor vacío
    public Bitacora() {}

    // Constructor para inserción (la hora es generada por la DB o el código Java)
    public Bitacora(Integer usuarioId, Integer turnoId, String evento, String detalle) {
        this.usuarioId = usuarioId;
        this.turnoId = turnoId;
        this.evento = evento;
        this.detalle = detalle;
    }

    // Getters
    public int getId() { return id; }
    public Integer getUsuarioId() { return usuarioId; }
    public Integer getTurnoId() { return turnoId; }
    public String getEvento() { return evento; }
    public String getDetalle() { return detalle; }
    public Timestamp getTs() { return ts; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setUsuarioId(Integer usuarioId) { this.usuarioId = usuarioId; }
    public void setTurnoId(Integer turnoId) { this.turnoId = turnoId; }
    public void setEvento(String evento) { this.evento = evento; }
    public void setDetalle(String detalle) { this.detalle = detalle; }
    public void setTs(Timestamp ts) { this.ts = ts; }
}