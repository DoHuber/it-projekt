package de.hdm_stuttgart.huber.itprojekt.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.datepicker.client.DateBox;

import de.hdm_stuttgart.huber.itprojekt.client.gui.RichTextToolbar;
import de.hdm_stuttgart.huber.itprojekt.shared.EditorAsync;
import de.hdm_stuttgart.huber.itprojekt.shared.domainobjects.Note;

public class ShowNote extends BasicView {
	/**
	 * @author Nikita Nalivayko
	 */
	/**
	 * Funktionen: Löschen, Editieren, Freigeben, Fähligkeitsdatum setzen -
	 * Ebene: einzelne Notizen
	 */
	private EditorAsync editorVerwaltung = ClientsideSettings.getEditorVerwaltung();
	private Note n = null;
	private HorizontalPanel vp = new HorizontalPanel();
	private Button deleteBtn = new Button("Delete");
	private Button editBtn = new Button("Update");
	private Button releseBtn = new Button("Release");
	private TextBox titleTextBox = new TextBox();
	private RichTextArea noteArea = new RichTextArea();
	private RichTextToolbar richTextToolbar=new RichTextToolbar(noteArea);
	private DateBox dueDateBox = new DateBox();
	private TextBox subtitleTextBox = new TextBox();
	private Grid grid = new Grid(2,1);
	
	

	public ShowNote(Note note) {
		this.n = note;
		
	}

	@Override
	public String getSubHeadlineText() {
		if (n.getNoteBook()==null){
			return "There is no notebook deposited for the note.";
		}
		else{
			return "Notebook:"+ n.getNoteBook().getTitle();
		}
		
	}

	@Override
	public String getHeadlineText() {
		// TODO Auto-generated method stub
		return "Note: "+n.getTitle();
	}
	

	@Override
	public void run() {
		
		FlowPanel contentPanel = new FlowPanel();
		vp.add(deleteBtn);
		deleteBtn.addClickHandler(new DeleteClickHandler());
		vp.add(editBtn);
		editBtn.addClickHandler(new UpdateClickHandler());
		vp.add(releseBtn);
		releseBtn.addClickHandler(new ShareClickHandler());
		
		noteArea.setText(n.getContent());
		dueDateBox.setValue(n.getDueDate());
		titleTextBox.setText(n.getTitle());
		subtitleTextBox.setText(n.getSubtitle());
		
		grid.setWidget(0, 0, richTextToolbar);
		noteArea.setSize("30%", "10%px");
		grid.setWidget(1, 0, noteArea);
		contentPanel.add(vp);
		contentPanel.add(titleTextBox);
		contentPanel.add(subtitleTextBox);
		contentPanel.add(dueDateBox);
		contentPanel.add(grid);
		//contentPanel.add(noteArea);
		noteArea.setStyleName("noteArea");
		
		RootPanel.get("main").add(contentPanel);
		RootPanel.get("table").clear();
		RootPanel.get("tableNotebook").clear();
		
	}
	private class DeleteClickHandler implements ClickHandler {

		@Override
		public void onClick(ClickEvent event) {
		if(	Window.confirm("Möchten Sie die Notiz "+ n.getTitle()+ " wirklich löschen?")){
			editorVerwaltung.deleteNote(n, new DeleteCallback());
		}
		MenuView navigation = new MenuView();
		RootPanel.get("menu").clear();
		RootPanel.get("menu").add(navigation);	
		
		ShowAllNotes san =  new ShowAllNotes();
		RootPanel.get("main").clear();
		RootPanel.get("main").add(san);
			
		}
		
	}
	private class DeleteCallback implements AsyncCallback<Void>{

		@Override
		public void onFailure(Throwable caught) {
			caught.printStackTrace();
			vp.add(new Label(caught.toString()));
			
		}

		@Override
		public void onSuccess(Void result) {
		
			
			
		}
		
	}
	private class UpdateClickHandler implements ClickHandler {

		@Override
		public void onClick(ClickEvent event) {
		if(Window.confirm("Möchten Sie die Änderungen speichern?")){
		updateNote();	
		}
		
			
		}
		
	}
	public void updateNote(){
		n.setTitle(titleTextBox.getText());
		n.setSubtitle(subtitleTextBox.getText());
		n.setContent(noteArea.getText());
		java.util.Date utilDate = dueDateBox.getValue();
	    java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
	    n.setDueDate(sqlDate);
		//n.setNoteBook(nb);	
		editorVerwaltung.saveNote(n, new UpdateCallback());
		
	}
	private class UpdateCallback implements AsyncCallback<Note>{

		@Override
		public void onFailure(Throwable caught) {
			caught.printStackTrace();
			vp.add(new Label(caught.toString()));
			
		}

		@Override
		public void onSuccess(Note result) {
		Window.alert("Saved");	
			
		}

		
	}
	
	private class ShareClickHandler implements ClickHandler{

		@Override
		public void onClick(ClickEvent event) {
			MenuView mView = new MenuView();
			RootPanel.get("menu").clear();
			RootPanel.get("menu").add(mView);

			ShareNote sN = new ShareNote(n);
			RootPanel.get("main").clear();
			RootPanel.get("main").add(sN);
			
		}
		
	}
	
	


}
