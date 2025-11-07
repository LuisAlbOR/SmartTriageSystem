# 🏥 Triage Comunitario con Turnos Inteligentes
![Java](https://img.shields.io/badge/Java-21-blue?logo=java&logoColor=white)
![JavaFX](https://img.shields.io/badge/JavaFX-17%2B-orange?logo=openjfx&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue?logo=postgresql&logoColor=white)
![Sockets](https://img.shields.io/badge/Sockets-TCP-green)

Un sistema cliente-servidor para la gestión de colas de pacientes en centros de salud, enfocado en organizar el flujo de atención y asignar prioridades clínicas. Este proyecto busca contribuir al **ODS 3 (Salud y Bienestar)** mejorando la calidad y el acceso a la atención médica.

## 🚀 Características

* **Gestión por Roles:** Soporte para múltiples roles (Recepción, Médico, Pantalla) con autenticación.
* **Priorización de Triage:** Asignación automática de prioridad (1-5) basada en los síntomas descritos.
* **Gestión de Cola:** Los médicos pueden "llamar" al siguiente paciente de mayor prioridad y antigüedad.
* **Concurrencia Real:** Múltiples médicos pueden atender pacientes simultáneamente sin conflictos, gracias al bloqueo de filas (`SELECT FOR UPDATE`).
* **Visualización en Tiempo Real:** Una vista de "Pantalla Pública" que se actualiza automáticamente (polling) para mostrar la cola de espera y los pacientes que están siendo llamados.
* **Arquitectura Desacoplada:** El `Backend` (servidor) y el `Frontend` (cliente) están completamente separados, permitiendo el despliegue en diferentes máquinas.

## 🛠️ Arquitectura y Stack Tecnológico

El sistema sigue un modelo Cliente-Servidor multihilo, dividido en dos componentes principales.

### Backend (Servidor)
Construido en Java, funciona como el cerebro del sistema.

* **Lenguaje:** Java 21 (LTS)
* **Red:** Sockets TCP (`java.net.ServerSocket`) puros para una comunicación de bajo nivel y alto rendimiento.
* **Concurrencia:** `ExecutorService` (Pool de Hilos) para manejar cada cliente en un hilo separado.
* **Protocolo:** JSON (serializado con **Gson**) sobre TCP.
* **Base de Datos:** PostgreSQL
* **Pool de Conexiones:** **HikariCP** para una gestión eficiente y rápida de las conexiones a la DB.
* **Arquitectura:**
    * **DAO (Data Access Object):** Encapsula toda la lógica de JDBC y SQL.
    * **Service:** Contiene la lógica de negocio (ej. `PrioridadEngine`, `TurnoService` con transacciones ACID).
    * **Server:** Maneja la lógica de red y el enrutamiento de peticiones.

### Frontend (Cliente)
Una aplicación de escritorio moderna construida con JavaFX.

* **Framework de UI:** JavaFX
* **Arquitectura:** **MVC (Model-View-Controller)** para separar la lógica de la interfaz de usuario.
    * `view`: Clases JavaFX pasivas que definen la UI.
    * `controller`: Manejan eventos de la UI, llaman al API Client y actualizan la vista.
* **Concurrencia (UI):** `javafx.concurrent.Task` para realizar todas las llamadas de red en segundo plano, evitando que la interfaz se congele.
* **Cliente API:** `TriageApiClient` que encapsula la lógica de sockets (`java.net.Socket`) para comunicarse con el servidor.

## 📊 Diagrama Entidad-Relación (Base de Datos)
<img width="1827" height="936" alt="mermaid-diagram-2025-11-07-122304" src="https://github.com/user-attachments/assets/356dfe97-3625-4fa4-8ac0-7b36faad798b" />

## ⚙️ Puesta en Marcha y Despliegue

Sigue estos pasos para ejecutar el sistema en un entorno de desarrollo o producción.

### 1\. Base de Datos (PostgreSQL)

1.  Asegúrate de tener PostgreSQL instalado.
2.  Crea una base de datos (ej. `triage_db`).
3.  Ejecuta los scripts SQL (ubicados en `docs`) para crear las tablas (`paciente`, `turno`, etc.) y los *triggers*.
4.  **Poblar Usuarios:** Inserta los usuarios de prueba para poder iniciar sesión:
    ```sql
    INSERT INTO usuario (login, hash, rol) VALUES 
    ('recepcion1', 'pass_recepcion', 'recepcion'),
    ('medico1', 'pass_medico', 'medico'),
    ('pantalla_principal', 'pass_pantalla', 'pantalla');
    ```

### 2\. Backend (Servidor)

1.  **Configurar Conexión:** Edita el archivo `Backend/src/.../dao/DatabaseUtil.java` con tus credenciales de PostgreSQL (usuario, contraseña, URL de JDBC).
2.  **Compilar el JAR:** Ejecuta el comando de Maven para generar el JAR del servidor.
    ```bash
    mvn clean package
    ```
3.  **Ejecutar:** Busca el JAR en la carpeta `target/` (ej. `triage-servidor.jar`) y ejecútalo.
    ```bash
    java -jar target/triage-servidor.jar
    ```
4.  **Firewall:** Asegúrate de que el puerto `5050` (o el que hayas configurado) esté abierto en el firewall de la máquina servidor.

### 3\. Frontend (Cliente)

1.  **Configurar IP del Servidor:** Edita el archivo `Frontend/src/.../controller/LoginController.java` y cambia la IP del `TriageApiClient` de `"localhost"` a la IP de la máquina donde corre el servidor.
    ```java
    // Antes: new TriageApiClient("localhost", 5050);
    this.apiClient = new TriageApiClient("192.168.1.100", 5050); // <-- IP real del servidor
    ```
2.  **Compilar el JAR:** El mismo comando `mvn clean package` habrá generado el JAR del cliente (ej. `triage-cliente.jar`).
3.  **Ejecutar:** Copia `triage-cliente.jar` a cualquier máquina cliente (Recepción, Médico, Pantalla) y ejecútalo.
    ```bash
    java -jar target/triage-cliente.jar
    ```

## 🖥️ Uso del Sistema

Para probar el flujo completo, ejecuta múltiples instancias del cliente (`triage-cliente.jar`):

1.  **Instancia 1 (Pantalla):** Inicia sesión con `pantalla_principal` / `pass_pantalla`. La pantalla de espera se abrirá y se actualizará automáticamente.
2.  **Instancia 2 (Recepción):** Inicia sesión con `recepcion1` / `pass_recepcion`. Registra un nuevo paciente.
3.  **Verificación:** Observa cómo el nuevo paciente aparece en la Instancia 1 (Pantalla) después de unos segundos.
4.  **Instancia 3 (Médico):** Inicia sesión con `medico1` / `pass_medico`. Haz clic en "Llamar Siguiente".
5.  **Verificación:** Observa cómo el estado del paciente cambia a "ATENDIENDO" en la Instancia 1 (Pantalla).
6.  **Finalizar:** El médico finaliza la consulta en la Instancia 3. El paciente desaparecerá de la cola en la Instancia 1.

## 📄 Licencia

Este proyecto se distribuye bajo la licencia MIT.

```
```
