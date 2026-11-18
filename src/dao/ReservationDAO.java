package dao;

import model.Reservation;
import util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * ReservationDAO — Data Access Object for Reservation operations.
 * Handles booking, conflict detection, and statistics.
 */
public class ReservationDAO {

    /**
     * Get all reservations with user and room names (for admin view).
     */
    public List<Reservation> findAll() {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT res.*, u.name AS user_name, r.name AS room_name " +
                "FROM reservations res " +
                "JOIN users u ON res.user_id = u.id " +
                "JOIN rooms r ON res.room_id = r.id " +
                "ORDER BY res.reservation_date DESC, res.start_time ASC";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(extractReservation(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Get reservations for a specific user.
     */
    public List<Reservation> findByUserId(int userId) {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT res.*, u.name AS user_name, r.name AS room_name " +
                "FROM reservations res " +
                "JOIN users u ON res.user_id = u.id " +
                "JOIN rooms r ON res.room_id = r.id " +
                "WHERE res.user_id = ? " +
                "ORDER BY res.reservation_date DESC, res.start_time ASC";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(extractReservation(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Get a reservation by its ID.
     */
    public Reservation findById(int id) {
        String sql = "SELECT res.*, u.name AS user_name, r.name AS room_name " +
                "FROM reservations res " +
                "JOIN users u ON res.user_id = u.id " +
                "JOIN rooms r ON res.room_id = r.id " +
                "WHERE res.id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return extractReservation(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Check if there is a time conflict for a room on a given date.
     * Returns true if there IS a conflict (double booking would occur).
     */
    public boolean hasConflict(int roomId, String date, String startTime, String endTime) {
        String sql = "SELECT COUNT(*) FROM reservations " +
                "WHERE room_id = ? AND reservation_date = ? " +
                "AND status IN ('CONFIRMED', 'PENDING') " +
                "AND start_time < ? AND end_time > ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, roomId);
            ps.setString(2, date);
            ps.setString(3, endTime); // existing start < new end
            ps.setString(4, startTime); // existing end > new start

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true; // Assume conflict on error for safety
    }

    /**
     * Check conflict excluding a specific reservation (for updates).
     */
    public boolean hasConflictExcluding(int roomId, String date, String startTime,
            String endTime, int excludeId) {
        String sql = "SELECT COUNT(*) FROM reservations " +
                "WHERE room_id = ? AND reservation_date = ? " +
                "AND status IN ('CONFIRMED', 'PENDING') " +
                "AND start_time < ? AND end_time > ? AND id != ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, roomId);
            ps.setString(2, date);
            ps.setString(3, endTime);
            ps.setString(4, startTime);
            ps.setInt(5, excludeId);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * Insert a new reservation.
     * Returns the generated reservation ID, or -1 on failure.
     */
    public int insert(Reservation reservation) {
        // First check for conflicts
        if (hasConflict(reservation.getRoomId(), reservation.getReservationDate(),
                reservation.getStartTime(), reservation.getEndTime())) {
            return -1; // Conflict detected
        }

        String sql = "INSERT INTO reservations (user_id, room_id, reservation_date, " +
                "start_time, end_time, status, purpose, qr_code) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, reservation.getUserId());
            ps.setInt(2, reservation.getRoomId());
            ps.setString(3, reservation.getReservationDate());
            ps.setString(4, reservation.getStartTime());
            ps.setString(5, reservation.getEndTime());
            ps.setString(6, reservation.getStatus() != null ? reservation.getStatus() : "CONFIRMED");
            ps.setString(7, reservation.getPurpose());
            ps.setString(8, reservation.getQrCode() != null ? reservation.getQrCode() : "");

            int rows = ps.executeUpdate();
            if (rows > 0) {
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) {
                    int id = keys.getInt(1);
                    reservation.setId(id);
                    return id;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Update the QR code for a reservation.
     */
    public boolean updateQRCode(int reservationId, String qrCode) {
        String sql = "UPDATE reservations SET qr_code = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, qrCode);
            ps.setInt(2, reservationId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Cancel a reservation (set status to CANCELLED).
     */
    public boolean cancel(int reservationId) {
        String sql = "UPDATE reservations SET status = 'CANCELLED' WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, reservationId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Validate a reservation by QR code (mark as COMPLETED).
     */
    public Reservation validateByQRCode(String qrCode) {
        String sql = "SELECT res.*, u.name AS user_name, r.name AS room_name " +
                "FROM reservations res " +
                "JOIN users u ON res.user_id = u.id " +
                "JOIN rooms r ON res.room_id = r.id " +
                "WHERE res.qr_code = ? AND res.status = 'CONFIRMED'";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, qrCode);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Reservation reservation = extractReservation(rs);
                // Mark as completed
                String updateSql = "UPDATE reservations SET status = 'COMPLETED' WHERE id = ?";
                PreparedStatement updatePs = conn.prepareStatement(updateSql);
                updatePs.setInt(1, reservation.getId());
                updatePs.executeUpdate();
                reservation.setStatus("COMPLETED");
                return reservation;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Delete a reservation by ID.
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM reservations WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Count today's active reservations.
     */
    public int countToday() {
        String sql = "SELECT COUNT(*) FROM reservations WHERE reservation_date = CURDATE() " +
                "AND status IN ('CONFIRMED', 'PENDING')";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Count total reservations.
     */
    public int countAll() {
        String sql = "SELECT COUNT(*) FROM reservations";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Count reservations for a specific user.
     */
    public int countByUser(int userId) {
        String sql = "SELECT COUNT(*) FROM reservations WHERE user_id = ? AND status IN ('CONFIRMED', 'PENDING')";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Get peak hours data for statistics.
     * Returns array of Object[] where [0]=hour, [1]=count.
     */
    public List<Object[]> getPeakHours() {
        List<Object[]> results = new ArrayList<>();
        String sql = "SELECT HOUR(start_time) AS hr, COUNT(*) AS cnt " +
                "FROM reservations WHERE status IN ('CONFIRMED', 'COMPLETED') " +
                "GROUP BY hr ORDER BY hr";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                results.add(new Object[] { rs.getInt("hr"), rs.getInt("cnt") });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    /**
     * Get reservations per day for the last 7 days.
     */
    public List<Object[]> getReservationsPerDay() {
        List<Object[]> results = new ArrayList<>();
        String sql = "SELECT reservation_date, COUNT(*) AS cnt FROM reservations " +
                "WHERE reservation_date >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) " +
                "AND status IN ('CONFIRMED', 'COMPLETED') " +
                "GROUP BY reservation_date ORDER BY reservation_date";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                results.add(new Object[] { rs.getString("reservation_date"), rs.getInt("cnt") });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    /**
     * Suggest alternative time slots when the requested time is busy.
     * Finds free 2-hour slots on the same day for the same room.
     */
    public List<String[]> suggestAlternativeSlots(int roomId, String date) {
        List<String[]> freeSlots = new ArrayList<>();

        // Check each 2-hour block from 8:00 to 18:00
        String[] startTimes = { "08:00", "10:00", "12:00", "14:00", "16:00" };
        String[] endTimes = { "10:00", "12:00", "14:00", "16:00", "18:00" };

        for (int i = 0; i < startTimes.length; i++) {
            if (!hasConflict(roomId, date, startTimes[i], endTimes[i])) {
                freeSlots.add(new String[] { startTimes[i], endTimes[i] });
            }
        }
        return freeSlots;
    }

    /**
     * Extract a Reservation object from a ResultSet row.
     */
    private Reservation extractReservation(ResultSet rs) throws SQLException {
        Reservation r = new Reservation();
        r.setId(rs.getInt("id"));
        r.setUserId(rs.getInt("user_id"));
        r.setRoomId(rs.getInt("room_id"));
        r.setReservationDate(rs.getString("reservation_date"));
        r.setStartTime(rs.getString("start_time"));
        r.setEndTime(rs.getString("end_time"));
        r.setStatus(rs.getString("status"));
        r.setPurpose(rs.getString("purpose"));
        r.setQrCode(rs.getString("qr_code"));

        // Try to get joined fields
        try {
            r.setUserName(rs.getString("user_name"));
            r.setRoomName(rs.getString("room_name"));
        } catch (SQLException e) {
            // These fields are optional (only present in joined queries)
        }
        return r;
    }
}
