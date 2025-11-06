package Backend.MODEL;

import java.sql.Timestamp;

public class Turno {
    private int id;
    private int pacienteId;
    private int recepcionistaId;
    private Integer medicoId; // Wrapper Integer para permitir NULL en DB
    private int prioridad;    // 1 (Alta) a 5 (Baja)
    private String estado;    // 'EN_COLA', 'ATENDIENDO', 'FINALIZADO', 'CANCELADO'
    private Timestamp tsCreado;
    private Timestamp tsInicio; // NULLABLE
    private Timestamp tsFin;    // NULLABLE

    // Constructor vacío
    public Turno() {}

    // Constructor para inserción (el servidor asignará prioridad y estado inicial)
    public Turno(int pacienteId, int recepcionistaId, int prioridad, String estado) {
        this.pacienteId = pacienteId;
        this.recepcionistaId = recepcionistaId;
        this.prioridad = prioridad;
        this.estado = estado;
    }

    // Getters
    public int getId() { return id; }
    public int getPacienteId() { return pacienteId; }
    public int getRecepcionistaId() { return recepcionistaId; }
    public Integer getMedicoId() { return medicoId; }
    public int getPrioridad() { return prioridad; }
    public String getEstado() { return estado; }
    public Timestamp getTsCreado() { return tsCreado; }
    public Timestamp getTsInicio() { return tsInicio; }
    public Timestamp getTsFin() { return tsFin; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setPacienteId(int pacienteId) { this.pacienteId = pacienteId; }
    public void setRecepcionistaId(int recepcionistaId) { this.recepcionistaId = recepcionistaId; }
    public void setMedicoId(Integer medicoId) { this.medicoId = medicoId; }
    public void setPrioridad(int prioridad) { this.prioridad = prioridad; }
    public void setEstado(String estado) { this.estado = estado; }
    public void setTsCreado(Timestamp tsCreado) { this.tsCreado = tsCreado; }
    public void setTsInicio(Timestamp tsInicio) { this.tsInicio = tsInicio; }
    public void setTsFin(Timestamp tsFin) { this.tsFin = tsFin; }
}