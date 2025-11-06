package Backend.server;

import Backend.DAO.DatabaseUtil;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Servidor principal de Triage.
 * Escucha conexiones entrantes en el puerto TCP definido y las delega
 * a un pool de hilos para su procesamiento concurrente.
 */
public class TriageServer {

    private static final int PORT = 5050;
    // Define cuántos clientes simultáneos puede manejar el servidor activamente.
    private static final int THREAD_POOL_SIZE = 20;

    public static void main(String[] args) {
        System.out.println("Iniciando Servidor de Triage Inteligente...");

        // 1. Inicializar el Pool de Hilos para manejo concurrente
        ExecutorService pool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

        // 2. Registrar un shutdown hook para cerrar recursos al detener el servidor (Ctrl+C)
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\nApagando servidor...");
            pool.shutdown(); // Deja de aceptar nuevas tareas
            DatabaseUtil.closePool(); // Cierra conexiones a BD
            System.out.println("Servidor apagado correctamente.");
        }));

        // 3. Iniciar el ServerSocket y el bucle principal
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Servidor escuchando en el puerto: " + PORT);
            System.out.println("Esperando conexiones de clientes...");

            while (true) {
                // Se bloquea hasta que un cliente se conecta
                Socket clientSocket = serverSocket.accept();
                
                System.out.println("Nuevo cliente conectado desde: " + 
                                   clientSocket.getInetAddress().getHostAddress());

                // Delega la conexión al pool de hilos usando ClientHandler
                // Nota: ClientHandler debe ser implementado en el siguiente paso.
                pool.execute(new ClientHandler(clientSocket));
            }

        } catch (IOException e) {
            System.err.println("Error fatal en el servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }
}