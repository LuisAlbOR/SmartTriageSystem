package DTO;


import MODEL.Paciente;
import MODEL.Turno;

/**
 * DTO (Data Transfer Object) para retornar la información completa 
 * después de que un médico ha tomado el siguiente turno.
 */
public class AsignacionTurnoDTO {
    private final Turno turno;
    private final Paciente paciente;

    public AsignacionTurnoDTO(Turno turno, Paciente paciente) {
        this.turno = turno;
        this.paciente = paciente;
    }

    public Turno getTurno() {
        return turno;
    }

    public Paciente getPaciente() {
        return paciente;
    }
}