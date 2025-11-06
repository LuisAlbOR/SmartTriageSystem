package Backend.MODEL;

import java.sql.Timestamp;

public class Paciente {
    private int id;
    private String nombre;
    private String curp; // Puede ser NULL en DB, pero String en Java es el estándar
    private int edad;
    private Timestamp tsCreado;

    // Constructor vacío
    public Paciente() {}

    // Constructor para la creación (sin ID ni tsCreado)
    public Paciente(String nombre, String curp, int edad) {
        this.nombre = nombre;
        this.curp = curp;
        this.edad = edad;
    }

    // Getters
    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getCurp() { return curp; }
    public int getEdad() { return edad; }
    public Timestamp getTsCreado() { return tsCreado; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setCurp(String curp) { this.curp = curp; }
    public void setEdad(int edad) { this.edad = edad; }
    public void setTsCreado(Timestamp tsCreado) { this.tsCreado = tsCreado; }
}