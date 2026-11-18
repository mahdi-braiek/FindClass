package dao;

import util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * SearchDAO — Global search across rooms, reservations, users, and timetable.
 * Returns results as Object[] arrays for flexible display.
 */
public class SearchDAO {

    /**
     * Search rooms by name, building, or equipment.
     * Returns: [id, name, capacity, equipment, status, building]
     */
    public List<Object[]> searchRooms(String query) {
        List<Object[]> results = new ArrayList<>();
        String sql = "SELECT id, name, capacity, equipment, status, building FROM rooms " +
                "WHERE name LIKE ? OR building LIKE ? OR equipment LIKE ? " +
                "ORDER BY name LIMIT 20";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            String pattern = "%" + query + "%";
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            ps.setString(3, pattern);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                results.add(new Object[] {
                        "ROOM", rs.getInt("id"), rs.getString("name"),
                        rs.getInt("capacity") + " places",
                        rs.getString("status"), rs.getString("building")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    /**
     * Search reservations by purpose, user name, or room name.
     * Returns: [type, id, description, detail1, detail2, detail3]
     */
    public List<Object[]> searchReservations(String query) {
        List<Object[]> results = new ArrayList<>();
        String sql = "SELECT r.id, r.purpose, r.reservation_date, r.start_time, r.end_time, r.status, " +
                "u.name AS user_name, rm.name AS room_name " +
                "FROM reservations r " +
                "LEFT JOIN users u ON r.user_id = u.id " +
                "LEFT JOIN rooms rm ON r.room_id = rm.id " +
                "WHERE r.purpose LIKE ? OR u.name LIKE ? OR rm.name LIKE ? " +
                "ORDER BY r.reservation_date DESC LIMIT 20";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            String pattern = "%" + query + "%";
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            ps.setString(3, pattern);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                results.add(new Object[] {
                        "RESERVATION", rs.getInt("id"),
                        rs.getString("purpose"),
                        rs.getString("room_name") + " | " + rs.getString("user_name"),
                        rs.getString("reservation_date") + " " + rs.getString("start_time") + "-"
                                + rs.getString("end_time"),
                        rs.getString("status")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    /**
     * Search users by name or email.
     */
    public List<Object[]> searchUsers(String query) {
        List<Object[]> results = new ArrayList<>();
        String sql = "SELECT id, name, email, role FROM users " +
                "WHERE name LIKE ? OR email LIKE ? ORDER BY name LIMIT 20";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            String pattern = "%" + query + "%";
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                results.add(new Object[] {
                        "USER", rs.getInt("id"), rs.getString("name"),
                        rs.getString("email"), rs.getString("role"), ""
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    /**
     * Search timetable by subject or group.
     */
    public List<Object[]> searchTimetable(String query) {
        List<Object[]> results = new ArrayList<>();
        String sql = "SELECT t.id, t.subject, t.day_of_week, t.start_time, t.end_time, t.type, " +
                "t.group_name, r.name AS room_name " +
                "FROM timetable t LEFT JOIN rooms r ON t.room_id = r.id " +
                "WHERE t.subject LIKE ? OR t.group_name LIKE ? " +
                "ORDER BY FIELD(t.day_of_week,'LUNDI','MARDI','MERCREDI','JEUDI','VENDREDI','SAMEDI') LIMIT 20";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            String pattern = "%" + query + "%";
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                results.add(new Object[] {
                        "TIMETABLE", rs.getInt("id"), rs.getString("subject"),
                        rs.getString("day_of_week") + " " + rs.getString("start_time") + "-" + rs.getString("end_time"),
                        rs.getString("type") + " | " + rs.getString("group_name"),
                        rs.getString("room_name") != null ? rs.getString("room_name") : ""
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    /**
     * Global search — searches everything and merges results.
     */
    public List<Object[]> globalSearch(String query) {
        List<Object[]> all = new ArrayList<>();
        all.addAll(searchRooms(query));
        all.addAll(searchReservations(query));
        all.addAll(searchUsers(query));
        all.addAll(searchTimetable(query));
        return all;
    }
}
