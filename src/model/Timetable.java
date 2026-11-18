package model;

/**
 * Timetable — Représente une entrée d'emploi du temps (un cours dans la
 * semaine).
 */
public class Timetable {

    private int id;
    private String dayOfWeek; // LUNDI, MARDI, MERCREDI, JEUDI, VENDREDI, SAMEDI
    private String startTime; // HH:mm
    private String endTime; // HH:mm
    private String subject; // Nom de la matière
    private int teacherId;
    private int roomId;
    private String groupName; // Groupe A, Groupe B, Tous
    private String semester; // S1, S2
    private String type; // COURS, TD, TP, EXAM
    private String color; // Couleur hex (#6366F1)

    // ── Champs supplémentaires pour l'affichage (jointures) ──
    private String teacherName;
    private String roomName;

    // ── Constructeurs ───────────────────────────────────────

    public Timetable() {
    }

    public Timetable(String dayOfWeek, String startTime, String endTime,
            String subject, int teacherId, int roomId,
            String groupName, String semester, String type, String color) {
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.subject = subject;
        this.teacherId = teacherId;
        this.roomId = roomId;
        this.groupName = groupName;
        this.semester = semester;
        this.type = type;
        this.color = color;
    }

    // ── Getters & Setters ───────────────────────────────────

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public int getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(int teacherId) {
        this.teacherId = teacherId;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    /**
     * Retourne l'heure de début sous forme d'entier (ex: "08:00" → 8).
     */
    public int getStartHour() {
        try {
            return Integer.parseInt(startTime.split(":")[0]);
        } catch (Exception e) {
            return 8;
        }
    }

    /**
     * Retourne l'heure de fin sous forme d'entier.
     */
    public int getEndHour() {
        try {
            return Integer.parseInt(endTime.split(":")[0]);
        } catch (Exception e) {
            return 10;
        }
    }

    /**
     * Durée en heures.
     */
    public int getDurationHours() {
        return getEndHour() - getStartHour();
    }

    @Override
    public String toString() {
        return subject + " (" + type + ") - " + dayOfWeek + " " + startTime + "-" + endTime;
    }
}
