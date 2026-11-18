package dao;

import model.Timetable;
import util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * TimetableDAO — Data Access Object pour les emplois du temps.
 * Gère le CRUD et les requêtes filtrées par groupe/semestre.
 */
public class TimetableDAO {

    /**
     * Récupère toutes les entrées de l'emploi du temps avec noms enseignant et
     * salle.
     */
    public List<Timetable> findAll() {
        List<Timetable> list = new ArrayList<>();
        String sql = "SELECT t.*, u.name AS teacher_name, r.name AS room_name " +
                "FROM timetable t " +
                "LEFT JOIN users u ON t.teacher_id = u.id " +
                "LEFT JOIN rooms r ON t.room_id = r.id " +
                "ORDER BY FIELD(t.day_of_week, 'LUNDI','MARDI','MERCREDI','JEUDI','VENDREDI','SAMEDI'), " +
                "t.start_time ASC";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(extractTimetable(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Récupère l'emploi du temps filtré par groupe et/ou semestre.
     */
    public List<Timetable> findByFilter(String groupName, String semester) {
        List<Timetable> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT t.*, u.name AS teacher_name, r.name AS room_name ");
        sql.append("FROM timetable t ");
        sql.append("LEFT JOIN users u ON t.teacher_id = u.id ");
        sql.append("LEFT JOIN rooms r ON t.room_id = r.id ");
        sql.append("WHERE 1=1 ");

        if (groupName != null && !groupName.isEmpty() && !groupName.equals("Tous")) {
            sql.append("AND (t.group_name = ? OR t.group_name = 'Tous') ");
        }
        if (semester != null && !semester.isEmpty()) {
            sql.append("AND t.semester = ? ");
        }

        sql.append("ORDER BY FIELD(t.day_of_week, 'LUNDI','MARDI','MERCREDI','JEUDI','VENDREDI','SAMEDI'), ");
        sql.append("t.start_time ASC");

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            if (groupName != null && !groupName.isEmpty() && !groupName.equals("Tous")) {
                ps.setString(paramIndex++, groupName);
            }
            if (semester != null && !semester.isEmpty()) {
                ps.setString(paramIndex, semester);
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(extractTimetable(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Récupère une entrée par son ID.
     */
    public Timetable findById(int id) {
        String sql = "SELECT t.*, u.name AS teacher_name, r.name AS room_name " +
                "FROM timetable t " +
                "LEFT JOIN users u ON t.teacher_id = u.id " +
                "LEFT JOIN rooms r ON t.room_id = r.id " +
                "WHERE t.id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return extractTimetable(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Insérer une nouvelle entrée d'emploi du temps.
     */
    public boolean insert(Timetable t) {
        String sql = "INSERT INTO timetable (day_of_week, start_time, end_time, subject, " +
                "teacher_id, room_id, group_name, semester, type, color) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, t.getDayOfWeek());
            ps.setString(2, t.getStartTime());
            ps.setString(3, t.getEndTime());
            ps.setString(4, t.getSubject());
            ps.setInt(5, t.getTeacherId());
            ps.setInt(6, t.getRoomId());
            ps.setString(7, t.getGroupName());
            ps.setString(8, t.getSemester());
            ps.setString(9, t.getType());
            ps.setString(10, t.getColor() != null ? t.getColor() : "#6366F1");

            int rows = ps.executeUpdate();
            if (rows > 0) {
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) {
                    t.setId(keys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Mettre à jour une entrée existante.
     */
    public boolean update(Timetable t) {
        String sql = "UPDATE timetable SET day_of_week = ?, start_time = ?, end_time = ?, " +
                "subject = ?, teacher_id = ?, room_id = ?, group_name = ?, " +
                "semester = ?, type = ?, color = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, t.getDayOfWeek());
            ps.setString(2, t.getStartTime());
            ps.setString(3, t.getEndTime());
            ps.setString(4, t.getSubject());
            ps.setInt(5, t.getTeacherId());
            ps.setInt(6, t.getRoomId());
            ps.setString(7, t.getGroupName());
            ps.setString(8, t.getSemester());
            ps.setString(9, t.getType());
            ps.setString(10, t.getColor() != null ? t.getColor() : "#6366F1");
            ps.setInt(11, t.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Supprimer une entrée par ID.
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM timetable WHERE id = ?";
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
     * Récupère la liste des groupes distincts.
     */
    public List<String> getDistinctGroups() {
        List<String> groups = new ArrayList<>();
        String sql = "SELECT DISTINCT group_name FROM timetable ORDER BY group_name";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                groups.add(rs.getString("group_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return groups;
    }

    /**
     * Récupère la liste des semestres distincts.
     */
    public List<String> getDistinctSemesters() {
        List<String> semesters = new ArrayList<>();
        String sql = "SELECT DISTINCT semester FROM timetable ORDER BY semester";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                semesters.add(rs.getString("semester"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return semesters;
    }

    /**
     * Extraire un objet Timetable depuis un ResultSet.
     */
    private Timetable extractTimetable(ResultSet rs) throws SQLException {
        Timetable t = new Timetable();
        t.setId(rs.getInt("id"));
        t.setDayOfWeek(rs.getString("day_of_week"));
        t.setStartTime(rs.getString("start_time"));
        t.setEndTime(rs.getString("end_time"));
        t.setSubject(rs.getString("subject"));
        t.setTeacherId(rs.getInt("teacher_id"));
        t.setRoomId(rs.getInt("room_id"));
        t.setGroupName(rs.getString("group_name"));
        t.setSemester(rs.getString("semester"));
        t.setType(rs.getString("type"));
        t.setColor(rs.getString("color"));

        try {
            t.setTeacherName(rs.getString("teacher_name"));
            t.setRoomName(rs.getString("room_name"));
        } catch (SQLException e) {
            // Champs optionnels (jointures)
        }
        return t;
    }
}
