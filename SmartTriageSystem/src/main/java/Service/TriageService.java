package Service;


import DAO.PacienteDAO;
import DAO.SintomasDAO;
import DAO.TurnoDAO;
import MODEL.Paciente;
import MODEL.Sintomas;
import MODEL.Turno;

import java.sql.SQLException;

public class TriageService {

    private final PacienteDAO pacienteDAO = new PacienteDAO();
    private final TurnoDAO turnoDAO = new TurnoDAO();
    private final SintomasDAO sintomasDAO = new SintomasDAO();
    // private final BitacoraDAO bitacoraDAO = new BitacoraDAO(); // Opcional para logging

    /**
     * Procesa la solicitud completa de un nuevo paciente y crea un turno.
     * @return El ID del Turno creado.
     * @throws SQLException Si falla alguna operación de la base de datos.
     */
    public int processNewPatient(String nombre, String curp, int edad,
                                 String motivoConsulta, String signosVitalesJson,
                                 int recepcionistaId) throws SQLException {

        // 1. Calcular Prioridad
        int prioridad = PrioridadEngine.calcularPrioridad(motivoConsulta);

        // 2. Guardar Paciente y obtener su ID
        Paciente paciente = new Paciente(nombre, curp, edad);
        int pacienteId = pacienteDAO.save(paciente);

        // 3. Crear Turno y obtener su ID
        String estadoInicial = "EN_COLA";
        Turno turno = new Turno(pacienteId, recepcionistaId, prioridad, estadoInicial);
        // TurnoDAO.save() ahora retorna el ID
        int turnoId = turnoDAO.save(turno);

        // 4. Guardar Síntomas (ligado al ID del turno)
        Sintomas sintomas = new Sintomas(turnoId, motivoConsulta, signosVitalesJson, prioridad);
        sintomasDAO.save(sintomas);

        // Opcional: registrar la creación del turno en bitácora
        // bitacoraDAO.log(new Bitacora(recepcionistaId, turnoId, "NEW_PATIENT", "Turno " + turnoId + " creado."));

        return turnoId;
    }
}