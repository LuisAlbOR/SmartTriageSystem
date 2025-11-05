package Service;

// La clase es estática o de utilidad; no necesita ser instanciada.
public class PrioridadEngine {

    /**
     * Calcula la prioridad del turno basada en el motivo de consulta.
     * Triage ESI simplificado: 1 (Emergencia) a 5 (No Urgente).
     * @param motivoConsulta Texto ingresado por la recepcionista.
     * @return Nivel de prioridad (1 a 5).
     */
    public static int calcularPrioridad(String motivoConsulta) {
        if (motivoConsulta == null || motivoConsulta.trim().isEmpty()) {
            return 5; // Por defecto: No Urgente
        }
        
        String motivo = motivoConsulta.toLowerCase();

        // PRIORIDAD 1: EMERGENCIA (Riesgo vital inminente)
        if (motivo.contains("dolor torácico") || 
            motivo.contains("desmayo") || 
            motivo.contains("inconsciente") ||
            motivo.contains("paro")) {
            return 1;
        }

        // PRIORIDAD 2: URGENCIA ALTA (Riesgo potencial de deterioro rápido)
        if (motivo.contains("dificultad respirar") || 
            motivo.contains("sangrado profuso") || 
            motivo.contains("shock") ||
            motivo.contains("convulsiones")) {
            return 2;
        }

        // PRIORIDAD 3: URGENCIA MEDIA (Necesidad de intervención temprana)
        if (motivo.contains("fractura") || 
            motivo.contains("quemadura extensa") || 
            motivo.contains("dolor abdominal fuerte") ||
            motivo.contains("vómito constante")) {
            return 3;
        }

        // PRIORIDAD 4: URGENCIA MENOR (Atención necesaria, puede tolerar espera)
        if (motivo.contains("fiebre alta") || 
            motivo.contains("resfriado severo") || 
            motivo.contains("dolor garganta")) {
            return 4;
        }

        // PRIORIDAD 5: NO URGENTE (Consulta general, chequeo, síntomas leves)
        return 5;
    }
}