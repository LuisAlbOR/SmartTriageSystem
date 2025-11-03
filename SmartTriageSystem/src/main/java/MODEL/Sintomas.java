package MODEL;

import java.sql.Timestamp;

public class Sintomas {
    private int id;
    private int turnoId;
    private String motivoConsulta;
    private String signosVitales; // Mapeado desde JSONB (la serialización se maneja en DAO/Service)
    private int prioridadCalculada;
    private Timestamp tsRegistro;

    // Constructor vacío
    public Sintomas() {}

    // Constructor para inserción (el ID del turno se obtiene al crear el turno)
    public Sintomas(int turnoId, String motivoConsulta, String signosVitales, int prioridadCalculada) {
        this.turnoId = turnoId;
        this.motivoConsulta = motivoConsulta;
        this.signosVitales = signosVitales;
        this.prioridadCalculada = prioridadCalculada;
    }

    // Getters
    public int getId() { return id; }
    public int getTurnoId() { return turnoId; }
    public String getMotivoConsulta() { return motivoConsulta; }
    public String getSignosVitales() { return signosVitales; }
    public int getPrioridadCalculada() { return prioridadCalculada; }
    public Timestamp getTsRegistro() { return tsRegistro; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setTurnoId(int turnoId) { this.turnoId = turnoId; }
    public void setMotivoConsulta(String motivoConsulta) { this.motivoConsulta = motivoConsulta; }
    public void setSignosVitales(String signosVitales) { this.signosVitales = signosVitales; }
    public void setPrioridadCalculada(int prioridadCalculada) { this.prioridadCalculada = prioridadCalculada; }
    public void setTsRegistro(Timestamp tsRegistro) { this.tsRegistro = tsRegistro; }
}