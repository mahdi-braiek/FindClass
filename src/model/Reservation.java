package model;

/**
 * Reservation — Represents a room booking with all details.
 */
public class Reservation {

    private int id;
    private int userId;
    private int roomId;
    private String reservationDate; // yyyy-MM-dd format
    private String startTime; // HH:mm format
    private String endTime; // HH:mm format
    private String status; // CONFIRMED, CANCELLED, COMPLETED, PENDING
    private String purpose;
    private String qrCode;

    // ── Extra fields for display (joined from other tables) ──
    private String userName;
    private String roomName;

    // ── Constructors ────────────────────────────────────────

    public Reservation() {
    }

    public Reservation(int userId, int roomId, String reservationDate,
            String startTime, String endTime, String purpose) {
        this.userId = userId;
        this.roomId = roomId;
        this.reservationDate = reservationDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.purpose = purpose;
        this.status = "CONFIRMED";
    }

    // ── Getters & Setters ───────────────────────────────────

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public String getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(String reservationDate) {
        this.reservationDate = reservationDate;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    @Override
    public String toString() {
        return "Reservation #" + id + " - Room: " + roomId
                + " [" + reservationDate + " " + startTime + "-" + endTime + "]";
    }
}
