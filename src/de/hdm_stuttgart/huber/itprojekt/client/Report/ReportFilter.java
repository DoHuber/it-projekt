package de.hdm_stuttgart.huber.itprojekt.client.Report;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import de.hdm_stuttgart.huber.itprojekt.client.BasicView;


/**
 * 
 * @author dominik erdmann
 *
 */

public class ReportFilter extends BasicViewR{
	
	VerticalPanel vPanel = new VerticalPanel();

	Anchor showUsers = new Anchor("Ausgabe aller Nutzer");
	Anchor showAllNotesR = new Anchor("Ausgabe aller Notizen");
	Anchor showAllNotebooks = new Anchor("Ausgabe aller Notizbücher");

	
	@Override
	public String getHeadlineText() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getSubHeadlineText() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void run() {
		
		vPanel.add(showUsers);
		vPanel.add(showAllNotesR);
		vPanel.add(showAllNotebooks);
		
		RootPanel.get().add(vPanel);

		showUsers.addClickHandler(new ShowUsersHandler());
		showAllNotesR.addClickHandler(new ShowAllNotesHandler());
		showAllNotebooks.addClickHandler(new ShowAllNotebooksHandler());

		
	}

	

private class ShowUsersHandler implements ClickHandler {

	@Override
	public void onClick(ClickEvent event) {
		ReportFilter rpF = new ReportFilter();
		RootPanel.get().clear();
		RootPanel.get().add(rpF);

		ShowUsers sU = new ShowUsers();
		RootPanel.get().add(sU);
	}
}

private class ShowAllNotesHandler implements ClickHandler {

	@Override
	public void onClick(ClickEvent event) {
		ReportFilter rpF = new ReportFilter();
		RootPanel.get().clear();
		RootPanel.get().add(rpF);

		ShowAllNotesR sanR = new ShowAllNotesR();
		RootPanel.get().add(sanR);
	}
}

private class ShowAllNotebooksHandler implements ClickHandler {

	@Override
	public void onClick(ClickEvent event) {
		ReportFilter rpF = new ReportFilter();
		RootPanel.get().clear();
		RootPanel.get().add(rpF);

		ShowAllNotebooksR saNr = new ShowAllNotebooksR();
		RootPanel.get().add(saNr);
	}
}


}
