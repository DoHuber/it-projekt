package de.hdm_stuttgart.huber.itprojekt.server.db;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import de.hdm_stuttgart.huber.itprojekt.shared.domainobjects.NoteBook;
import de.hdm_stuttgart.huber.itprojekt.shared.domainobjects.NoteUser;

public class NotebookMapper {
	
private static NotebookMapper notebookMapper = null;
	
	public NotebookMapper getNotebookMapper(){
		return notebookMapper;
	}

	 protected static NotebookMapper notebookMapper() {
		if (notebookMapper == null) {
			 notebookMapper = new NotebookMapper();
		}

			return notebookMapper;
	}	

	public NoteBook create(NoteBook notebook) throws ClassNotFoundException, SQLException{
		 Connection con = DBConnection.getConnection();

		    try {
		    	
		      
		    	PreparedStatement stmt =  con.prepareStatement("INSERT INTO notizbuch.notebook(title, subtitle, creation_date, modification_date, author_id) "
		    	+ "VALUES (?,?,?,?,?)",Statement.RETURN_GENERATED_KEYS );
		      
		    	stmt.setString(1, notebook.getTitle());
		    	stmt.setString(2, notebook.getSubtitle());
		    	
		    	stmt.setString(3, "Owner");
		    	stmt.setDate(4, new Date(7777));
		    	stmt.setDate(5, new Date(7777));
		    	
		    	
		    
		    	stmt.executeUpdate();
		    	ResultSet rs = stmt.getGeneratedKeys();
		    	
		    	if(rs.next()){
		    		return findById(rs.getLong(1));
		    		
		    	}
		    	
		    } catch (SQLException sqlExp) {
		    sqlExp.printStackTrace();
		   
		    }
			return notebook;
	
	  }
	
	
	
	
	
	/**
	 * Bestimmtes NoteBook wird anhand der eindeutigen ID gesucht und zur�ckgegeben 
	 * 
	 * @param id
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	
	public NoteBook findById(long id) throws ClassNotFoundException, SQLException{
		
		Connection connection = DBConnection.getConnection();
		
		try {
			
			PreparedStatement stmt = connection.prepareStatement("SELECT * FROM notizbuch.notebook WHERE id = ?");
	         stmt.setLong(1, id);
			
			ResultSet rs=stmt.executeQuery();
			if (rs.next()) {
				
		        return new NoteBook(rs.getInt("NoteBookId"),
		        		rs.getString("Title"),
		        		rs.getString("Subtitle"),
		        		new NoteUser(),
		        		new Date(77777),
		        		new Date(77777));

		    }
		}
			
		 catch (SQLException sqlExp) {
			 sqlExp.printStackTrace();
		      return null;
		    }

		    return null;
		  }
	
	/**
	 * NoteBook-Objekt wird wiederholt in die Datenbank geschrieben
	 * 
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public NoteBook save(NoteBook notebook) throws ClassNotFoundException, SQLException{
		Connection con = DBConnection.getConnection();

	    try {
	      
	      PreparedStatement stmt = con.prepareStatement("UPDATE notizbuch.notebook SET title=?, subtitle=?, creation_date=?, modification_date=?, author_id=? WHERE id = ?");
	      
	      	stmt.setString(1, notebook.getTitle());
	    	stmt.setString(2, notebook.getSubtitle());
	    	stmt.setString(3, "Owner");
	    	stmt.setDate(4, new Date(7777));
	    	stmt.setDate(5, new Date(7777));
	    	
	    	stmt.setLong(6, notebook.getNoteBookId());
	    	
	    	stmt.executeUpdate();

	    

	    }
	    catch (SQLException sqlExp) {
	    	sqlExp.printStackTrace();
	    	throw new IllegalArgumentException();
	    }

	    return findById(notebook.getNoteBookId());
	  }
		  
	
	
	
	/**
	 * Daten eines bestimmten Notebook-Objekts werden aus der Datenbank gelöscht
	 * 
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	 public void delete(NoteBook notebook) throws ClassNotFoundException, SQLException {
			
		 Connection connection = DBConnection.getConnection();

		 try {

	            PreparedStatement stmt = connection.prepareStatement("DELETE FROM Notebook WHERE id = ?");
	            stmt.setLong(1, notebook.getNoteBookId());
	            stmt.executeUpdate();

	        } catch (SQLException e) {
	            e.printStackTrace();
	            throw new IllegalArgumentException();
	        }

	    }
	
	



	 public Vector<NoteBook> getAllNotes() throws ClassNotFoundException, SQLException {
		 	
		 Connection connection = DBConnection.getConnection();
	        Vector<NoteBook> v = new Vector<>();

	        try {

	            Statement stmt = connection.createStatement();
	            ResultSet rs = stmt.executeQuery("SELECT * FROM Notebook");

	            while (rs.next()) {

	                NoteBook notebook = new NoteBook(rs.getInt("NoteBookId"),
			        		rs.getString("Title"),
			        		rs.getString("Subtitle"),
			        		new NoteUser(),
			        		new Date(77777),
			        		new Date(77777));

	                v.add(notebook);

	            } 

	        } catch (SQLException e) {
	            e.printStackTrace();
	        }

	        return v;

	    }
	
	
	
	
	
	
	
}
