package Backend.DTO;


import Backend.MODEL.Paciente;
import Backend.MODEL.Sintomas;
import Backend.MODEL.Turno;

/**
 * DTO para retornar la información completa (Turno, Paciente y Síntomas)
 * al médico cuando se le asigna un paciente.
 */
public class AsignacionTurnoDTO {
    private final Turno turno;
    private final Paciente paciente;
    // Nuevo campo agregado
    private final Sintomas sintomas;

    public AsignacionTurnoDTO(Turno turno, Paciente paciente, Sintomas sintomas) {
        this.turno = turno;
        this.paciente = paciente;
        this.sintomas = sintomas;
    }

    public Turno getTurno() {
        return turno;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public Sintomas getSintomas() {
        return sintomas;
    }
}