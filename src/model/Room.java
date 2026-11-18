package model;

/**
 * Room — Represents a university room with its properties.
 */
public class Room {

    private int id;
    private String name;
    private int capacity;
    private String equipment; // Comma-separated list of equipment
    private String status; // AVAILABLE, MAINTENANCE, CLOSED
    private String building;
    private int floorNumber;

    // ── Constructors ────────────────────────────────────────

    public Room() {
    }

    public Room(int id, String name, int capacity, String equipment,
            String status, String building, int floorNumber) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
        this.equipment = equipment;
        this.status = status;
        this.building = building;
        this.floorNumber = floorNumber;
    }

    public Room(String name, int capacity, String equipment,
            String status, String building, int floorNumber) {
        this.name = name;
        this.capacity = capacity;
        this.equipment = equipment;
        this.status = status;
        this.building = building;
        this.floorNumber = floorNumber;
    }

    // ── Getters & Setters ───────────────────────────────────

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getEquipment() {
        return equipment;
    }

    public void setEquipment(String equipment) {
        this.equipment = equipment;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public int getFloorNumber() {
        return floorNumber;
    }

    public void setFloorNumber(int floorNumber) {
        this.floorNumber = floorNumber;
    }

    /**
     * Check if the room has a specific piece of equipment.
     */
    public boolean hasEquipment(String item) {
        if (equipment == null)
            return false;
        return equipment.toLowerCase().contains(item.toLowerCase());
    }

    public boolean isAvailable() {
        return "AVAILABLE".equalsIgnoreCase(status);
    }

    @Override
    public String toString() {
        return name + " (Cap: " + capacity + ")";
    }
}
