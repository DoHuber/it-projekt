package de.hdm_stuttgart.huber.itprojekt.server;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;

import javax.servlet.ServletException;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import de.hdm_stuttgart.huber.itprojekt.server.db.NoteMapper;
import de.hdm_stuttgart.huber.itprojekt.shared.BullshitException;
import de.hdm_stuttgart.huber.itprojekt.shared.Editor;
import de.hdm_stuttgart.huber.itprojekt.shared.domainobjects.Note;
import de.hdm_stuttgart.huber.itprojekt.shared.domainobjects.NoteBook;

public class EditorImpl extends RemoteServiceServlet implements Editor {
	
	/**
	 * AUTO-GENERATED
	 */
	private static final long serialVersionUID = 1L;
	private NoteMapper noteMapper;


	@Override
	public void init() throws IllegalArgumentException {
		
		this.noteMapper = NoteMapper.getNoteMapper();

	}

	@Override
	public String getHelloWorld() {
		// Sinnlose Methode, gibt einen zufälligen String zurück
		// Zu Testzwecken, wird dann bezeiten rausgeworfen
		SecureRandom random = new SecureRandom();
		return new BigInteger(256, random).toString();
	}

	@Override
	public NoteBook createNoteBook(NoteBook noteBook) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NoteBook saveNoteBook(NoteBook noteBook) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NoteBook getNoteBookById(NoteBook noteBook) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteNoteBook(NoteBook noteBook) {
		// TODO Auto-generated method stub

	}

	@Override
	public Note createNote(Note note) {
		
		Note newNote = note;
	
		try {
			
			noteMapper.create(newNote);
			newNote = noteMapper.findById(newNote.getNoteId());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return newNote;
	}

	@Override
	public Note saveNote(Note note) {
		
		// Aus Gründen der Sichtbarkeit (oder war das PHP?)
		Note newNote = note;
		
		try { 
			
			noteMapper.save(newNote);
			newNote = noteMapper.findById(newNote.getNoteId());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return newNote;
	}

	@Override
	public Note getNoteById(Note note) throws Exception {
	
			return noteMapper.findById(note.getNoteId());
	
	}

	@Override
	public void deleteNote(Note note) {
		
		try {
			noteMapper.delete(note);
		} catch (ClassNotFoundException e) {
			
			e.printStackTrace();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}

	}

	@Override
	public Vector<Note> getAllNotes() throws BullshitException {
		Vector<Note> result = new Vector<Note>();
		try {
		
		ArrayList<Note> toCopy = noteMapper.getAllNotes();
		
		Collections.copy(result, toCopy);
		
		} catch (Exception e) {
			throw new BullshitException("");
		}
	
		return result;
	}

	@Override
	public Vector<NoteBook> getAllNoteBooks() {
		// TODO Auto-generated method stub
		return null;
	}
	
	

}