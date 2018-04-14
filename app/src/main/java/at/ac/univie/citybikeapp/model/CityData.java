package at.ac.univie.citybikeapp.model;

/**
 * Die Klasse CityData enhaelt die folgenden Instanzvariablen:
 * String name - Name der Stadt
 * int empty_slots - Anzahl der freien Plaetze
 * int free_bikes - Anzahl der frein Raeder
 * String href - Hyperlink, wo die Daten der einzelnen CityBike-Anbieter in der jeweiligen Stadt abzurufen sind im XML-File
 */
public class CityData {
    private String name;
    private int empty_slots;
    private int free_bikes;
    private String href;

    // Konstruktor ohne Parameter
    public CityData(){}

    // Konstruktor mit Parametern
    public CityData(String name, int empty_slots, int free_bikes, String href) {
        this.name = name;
        this.empty_slots = empty_slots;
        this.free_bikes = free_bikes;
        this.href = href;
    }

    // Setter und Getter fuer die Instanzvariablen
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public int getEmpty_slots() {
        return empty_slots;
    }
    public void setEmpty_slots(int empty_slots) {
        this.empty_slots = empty_slots;
    }

    public int getFree_bikes() {
        return free_bikes;
    }
    public void setFree_bikes(int free_bikes) {
        this.free_bikes = free_bikes;
    }

    public String getHref() {
        return href;
    }
    public void setHref(String href) {
        this.href = href;
    }
}

