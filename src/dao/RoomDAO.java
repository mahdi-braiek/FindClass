package dao;

import model.Room;
import util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * RoomDAO — Data Access Object for Room operations.
 * Handles room CRUD and availability checking.
 */
public class RoomDAO {

    /**
     * Get all rooms in the system.
     */
    public List<Room> findAll() {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM rooms ORDER BY building, name";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                rooms.add(extractRoom(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rooms;
    }

    /**
     * Find a room by its ID.
     */
    public Room findById(int id) {
        String sql = "SELECT * FROM rooms WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return extractRoom(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Search available rooms based on criteria.
     * Finds rooms that are available and have no conflicting reservations.
     */
    public List<Room> searchAvailable(String date, String startTime, String endTime,
            int minCapacity, String requiredEquipment) {
        List<Room> rooms = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT r.* FROM rooms r WHERE r.status = 'AVAILABLE' ");
        sql.append("AND r.capacity >= ? ");

        // Equipment filter
        if (requiredEquipment != null && !requiredEquipment.trim().isEmpty()) {
            sql.append("AND LOWER(r.equipment) LIKE ? ");
        }

        // No conflicting reservations
        sql.append("AND r.id NOT IN (");
        sql.append("  SELECT res.room_id FROM reservations res ");
        sql.append("  WHERE res.reservation_date = ? ");
        sql.append("  AND res.status IN ('CONFIRMED', 'PENDING') ");
        sql.append("  AND res.start_time < ? AND res.end_time > ?");
        sql.append(") ");
        sql.append("ORDER BY r.capacity ASC");

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            ps.setInt(paramIndex++, minCapacity);

            if (requiredEquipment != null && !requiredEquipment.trim().isEmpty()) {
                ps.setString(paramIndex++, "%" + requiredEquipment.toLowerCase() + "%");
            }

            ps.setString(paramIndex++, date);
            ps.setString(paramIndex++, endTime);
            ps.setString(paramIndex, startTime);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                rooms.add(extractRoom(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rooms;
    }

    /**
     * Suggest the best room based on requirements.
     * Returns the room with the closest capacity match that has all required
     * equipment.
     */
    public Room suggestBestRoom(String date, String startTime, String endTime,
            int requiredCapacity, String requiredEquipment) {
        List<Room> available = searchAvailable(date, startTime, endTime,
                requiredCapacity, requiredEquipment);
        if (!available.isEmpty()) {
            // Return the room with the smallest capacity >= required (best fit)
            return available.get(0);
        }
        return null;
    }

    /**
     * Insert a new room.
     */
    public boolean insert(Room room) {
        String sql = "INSERT INTO rooms (name, capacity, equipment, status, building, floor_number) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, room.getName());
            ps.setInt(2, room.getCapacity());
            ps.setString(3, room.getEquipment());
            ps.setString(4, room.getStatus());
            ps.setString(5, room.getBuilding());
            ps.setInt(6, room.getFloorNumber());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) {
                    room.setId(keys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Update an existing room.
     */
    public boolean update(Room room) {
        String sql = "UPDATE rooms SET name = ?, capacity = ?, equipment = ?, status = ?, building = ?, floor_number = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, room.getName());
            ps.setInt(2, room.getCapacity());
            ps.setString(3, room.getEquipment());
            ps.setString(4, room.getStatus());
            ps.setString(5, room.getBuilding());
            ps.setInt(6, room.getFloorNumber());
            ps.setInt(7, room.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Delete a room by its ID.
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM rooms WHERE id = ?";
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
     * Count total available rooms.
     */
    public int countAvailable() {
        String sql = "SELECT COUNT(*) FROM rooms WHERE status = 'AVAILABLE'";
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
     * Count total rooms.
     */
    public int countAll() {
        String sql = "SELECT COUNT(*) FROM rooms";
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
     * Get the most used rooms with their reservation counts.
     * Returns an array of Object[] where [0]=roomName, [1]=count.
     */
    public List<Object[]> getMostUsedRooms(int limit) {
        List<Object[]> results = new ArrayList<>();
        String sql = "SELECT r.name, COUNT(res.id) AS cnt FROM rooms r " +
                "JOIN reservations res ON r.id = res.room_id " +
                "WHERE res.status IN ('CONFIRMED', 'COMPLETED') " +
                "GROUP BY r.id, r.name ORDER BY cnt DESC LIMIT ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                results.add(new Object[] { rs.getString("name"), rs.getInt("cnt") });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    /**
     * Extract a Room object from a ResultSet row.
     */
    private Room extractRoom(ResultSet rs) throws SQLException {
        return new Room(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getInt("capacity"),
                rs.getString("equipment"),
                rs.getString("status"),
                rs.getString("building"),
                rs.getInt("floor_number"));
    }
}
