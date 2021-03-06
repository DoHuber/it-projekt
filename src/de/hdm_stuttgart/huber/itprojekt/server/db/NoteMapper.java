package de.hdm_stuttgart.huber.itprojekt.server.db;

import de.hdm_stuttgart.huber.itprojekt.shared.domainobjects.Note;
import de.hdm_stuttgart.huber.itprojekt.shared.domainobjects.UserInfo;

import java.sql.*;
import java.util.Vector;

/**
 * <code>Mapper</code>, welcher zwischen <code>Note</code>-Objekten und der Datenbank hin- und herübersetzt.
 * Da die <code>Mapper</code> dieser Applikation sich grundsätzlich sehr ähneln, sei hier nur dieser detailliert beschreiben
 *
 * @author Küchler, Behr
 */
public class NoteMapper extends DataMapper {

    // Statisches Attribut, welches den Singleton-NoteMapper enthält.
    private static NoteMapper noteMapper = null;

    private UserInfoMapper noteUserMapper = UserInfoMapper.getUserInfoMapper();
    private NoteBookMapper noteBookMapper = NoteBookMapper.getNoteBookMapper();

    /**
     * Nichtöffentlicher Konstruktor, um "unauthorisiertes" Instanziieren dieser
     * Klasse zu verhindern.
     *
     * @throws ClassNotFoundException wenn Treiber nicht gefunden wird
     * @throws SQLException wenn die Datenbank ein Problem hat
     */
    private NoteMapper() throws ClassNotFoundException, SQLException {

    }

    /**
     * Gibt den aktuellen <code>Mapper</code> zurück, da dieser als sog. Singleton realisiert ist.
     *
     * @return einen <code>NoteMapper</code>
     */
    public static NoteMapper getNoteMapper() {

        if (noteMapper == null) {

            try {
                noteMapper = new NoteMapper();
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            }

        }

        return noteMapper;
    }

    /**
     * Erstellt ein übergebenes <code>Note</code>-Objekt als neuen Eintrag in der note-Tabelle
     * in der mySQL-Datenbank, gibt das erstelle Objekt mit id zurück
     *
     * @param note zu erstellende Notiz
     * @return Fertig gespeicherte Notiz
     * @throws ClassNotFoundException wenn
     * @throws SQLException wenn die Datenbank Probleme macht
     */
    public Note create(Note note) throws ClassNotFoundException, SQLException {

        try {

            PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO notizbuch.note"
                            + "(title, subtitle, content, note_source, creation_date, due_date, modification_date, notebook_id, author_id) VALUES (?,?,?,?,?,?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS);

            stmt.setString(1, note.getTitle());
            stmt.setString(2, note.getSubtitle());
            stmt.setString(3, note.getContent());

            if (!(note.getSource() == null)) {
                stmt.setString(4, note.getSource());
            } else {
                stmt.setString(4, null);
            }

            stmt.setDate(5, note.getCreationDate());
            stmt.setDate(6, note.getDueDate());
            stmt.setDate(7, note.getModificationDate());

            if (!(note.getNoteBook() == null)) {
                int noteBookId = note.getNoteBook().getId();
                stmt.setInt(8, noteBookId);
            } else {
                stmt.setNull(8, 0);
            }

            stmt.setInt(9, note.getOwner().getId());

            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();

            if (rs.next()) {
                return findById(rs.getInt(1));

            }

        } catch (SQLException sqlExp) {
            sqlExp.printStackTrace();

        }
        return note;

    }

    /**
     * Gibt die Notiz zurück, die in der Datenbank die übergebene <code>id</code> hat.
     *
     * @param id <code>id</code> der zu suchenden Notiz
     * @return Gggf. gefunden <code>Note</code>, sonst null
     * @throws ClassNotFoundException im Fehlerfall
     * @throws SQLException bei Datenbankproblemen, auch Fehleingaben
     */
    public Note findById(int id) throws ClassNotFoundException, SQLException {


        try {

            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM notizbuch.note WHERE id = ?");
            stmt.setInt(1, id);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {

            	Note n = new Note(id);
            	
            	// Textfelder
            	n.setTitle(rs.getString("title"));
            	n.setSubtitle(rs.getString("subtitle"));
            	n.setContent(rs.getString("content"));
            	n.setSource(rs.getString("note_source"));

            	// Daten
            	n.setCreationDate(rs.getDate("creation_date"));
            	n.setDueDate(rs.getDate("due_date"));
            	n.setModificationDate(rs.getDate("modification_date"));
            	
            	// Verweise
            	UserInfoMapper uim = UserInfoMapper.getUserInfoMapper();
            	n.setOwner(uim.findById(rs.getInt("author_id")));
            	n.setNoteBook(NoteBookMapper.getNoteBookMapper().findById(rs.getLong("notebook_id")));
            
            	return n;
            	
            }

        } catch (SQLException sqlExp) {
            sqlExp.printStackTrace();
            return null;
        }

        return null;
    }

    /**
     * Speichert die übergebene Notiz via UDPATE in der Datenbank
     *
     * @param note Zu speicherende Notiz
     * @return Gespeicherte Notiz mit aktuellen Werten
     * @throws ClassNotFoundException im Fehlerfall
     * @throws SQLException bei Datenbankproblemen
     */
    public Note save(Note note) throws ClassNotFoundException, SQLException {

        try {

            PreparedStatement stmt = connection.prepareStatement(
                    "UPDATE notizbuch.note SET title=?, subtitle=?, content=?, note_source=?, creation_date=?, due_date=?, modification_date=?, notebook_id=?, author_id=? WHERE id = ?");

            // Alle String-Inhalte
            stmt.setString(1, note.getTitle());
            stmt.setString(2, note.getSubtitle());
            stmt.setString(3, note.getContent());
            stmt.setString(4, note.getSource()); // TODO

            // Daten
            stmt.setDate(5, note.getCreationDate());
            stmt.setDate(6, note.getDueDate());
            stmt.setDate(7, new Date(System.currentTimeMillis()));

            // IDs, gesonderte Nullbehandlung
            if (note.getNoteBook() == null) {
                stmt.setObject(8, null);
            } else {
                stmt.setInt(8, note.getNoteBook().getId());
            }
            stmt.setInt(9, note.getOwner().getId());

            // Id der zu speichernden Notiz
            stmt.setInt(10, note.getId());

            stmt.executeUpdate();

        } catch (SQLException sqlExp) {
            sqlExp.printStackTrace();
            throw new IllegalArgumentException();
        }

        return findById(note.getId());
    }

    /**
     * Löscht eine übergebene Notiz dauerhaft anhand ihrer <code>id</code> mit DELETE FROM aus der
     * Datenbank.
     *
     * @param note zu löschende Notiz
     */
    public void delete(Note note) {

        try {

            PreparedStatement stmt = connection.prepareStatement("DELETE FROM notizbuch.note WHERE id = ?");
            stmt.setInt(1, note.getId());
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new IllegalArgumentException();
        }

    }

    /**
     * Gibt alle Notizen zurück
     *
     * @return alle verfügbaren Notizen
     * @throws ClassNotFoundException im Fehlerfall
     * @throws SQLException im Fehlerfall
     */
    public Vector<Note> getAllNotes() throws ClassNotFoundException, SQLException {

        try {

            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM notizbuch.note");

            return makeNotesFromResultSet(rs);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new Vector<Note>();

    }

    /**
     * Gibt alle Notizen zurück, deren Autor die übergebene id hat.
     *
     * @param userId <code>id</code> des Autors
     * @return alle anwendbaren Notizen
     */
    public Vector<Note> getAllNotesForUserId(int userId) {

        try {

            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM notizbuch.note WHERE author_id = ?");
            stmt.setInt(1, userId);

            ResultSet rs = stmt.executeQuery();

            return makeNotesFromResultSet(rs);

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return new Vector<Note>();

    }

    /**
     * Gibt alle Notizen zurück, deren Autor die übergebene id hat.
     *
     * @param userId <code>id</code> des Autors
     * @return alle anwendbaren Notizen
     */
    public Vector<Note> getAllNotesForUser(int userId) {
        try {

            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM notizbuch.note WHERE author_id = ?");
            stmt.setInt(1, userId);

            ResultSet rs = stmt.executeQuery();

            return makeNotesFromResultSet(rs);

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return new Vector<Note>();


    }

    /**
     * Gibt alle Notizen zurück, die dem übergebenen Notizbuch (der id) zugeordnet sind.
     *
     * @param id <code>id</code> des übergebenen Notizbuches
     * @return alle entsprechenden Notizen
     */
    public Vector<Note> getAllNotesForNoteBookId(int id) {

        try {

            PreparedStatement ps = connection.prepareStatement("SELECT * FROM notizbuch.note WHERE notebook_id = ?");
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            return makeNotesFromResultSet(rs);

        } catch (SQLException | ClassNotFoundException e) {

            e.printStackTrace();
        }

        return new Vector<>();

    }

    /**
     * Gibt alle Notizen zurück, die mit dem übergebenen Nutzer geteilt wurden.
     *
     * @param u Nutzer
     * @return Notizen
     */
    public Vector<Note> getAllNotesSharedWith(UserInfo u) {

        try {

            String sql = "SELECT note.id AS id, title, subtitle, content, note_source, creation_date, due_date, modification_date, note.notebook_id AS notebook_id, note.author_id AS author_id FROM note "
                    + "JOIN permission ON note.id = permission.note_id WHERE beneficiary_id = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, u.getId());
            ResultSet rs = ps.executeQuery();

            return makeNotesFromResultSet(rs);

        } catch (Exception e) {
            e.printStackTrace();
            return new Vector<>();
        }

    }

    /**
     * Helfermethode, macht Note-Objekte aus einem mySQL-<code>ResultSet</code>
     *
     * @param rs mysql-ResultSet mit den Ergebnissen einer Abfrage
     * @return vollständige <code>Note</code>-Objekte
     * @throws SQLException im Fehlerfall
     * @throws ClassNotFoundException im Fehlerfall
     */
    private Vector<Note> makeNotesFromResultSet(ResultSet rs) throws SQLException, ClassNotFoundException {

        Vector<Note> v = new Vector<>();

        while (rs.next()) {

            Note note = new Note(rs.getInt("id"));

            note.setContent(rs.getString("content"));
            note.setTitle(rs.getString("title"));
            note.setSubtitle(rs.getString("subtitle"));
            note.setSource(rs.getString("note_source"));

            note.setOwner(noteUserMapper.findById(rs.getInt("author_id")));
            note.setNoteBook(noteBookMapper.findById(rs.getInt("notebook_id")));

            note.setCreationDate(rs.getDate("creation_date"));
            note.setDueDate(rs.getDate("due_date"));
            note.setModificationDate(rs.getDate("modification_date"));

            v.add(note);

        }

        return v;
    }

    /**
     * Gibt alle Notizen zurück, die vom übergebenen Nutzer geteilt wurden.
     *
     * @param u Nutzer
     * @return Notizen
     */
    public Vector<Note> getAllNotesSharedBy(UserInfo u) {

        String sql = "SELECT DISTINCT note.id AS id FROM note JOIN permission ON note.id = permission.note_id WHERE note.author_id = ?";
        Vector<Note> v = new Vector<>();

        try {

            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, u.getId());

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                v.add(findById(rs.getInt("id")));
            }

            return v;

        } catch (Exception e) {
            e.printStackTrace();
            return v;
        }

    }

}
