package de.hdm_stuttgart.huber.itprojekt.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestBox;
import de.hdm_stuttgart.huber.itprojekt.client.gui.Notificator;
import de.hdm_stuttgart.huber.itprojekt.shared.EditorAsync;
import de.hdm_stuttgart.huber.itprojekt.shared.PermissionServiceAsync;
import de.hdm_stuttgart.huber.itprojekt.shared.domainobjects.Permission.Level;
import de.hdm_stuttgart.huber.itprojekt.shared.domainobjects.Shareable;
import de.hdm_stuttgart.huber.itprojekt.shared.domainobjects.UserInfo;

import java.util.Vector;

public class ShareShareable extends BasicVerticalView {

    MultiWordSuggestOracle oracle = new MultiWordSuggestOracle();
    Button confirmButton = new Button("Share");
    ListBox levelOptions;
    SuggestBox userPicker;

    EditorAsync editor = ClientsideSettings.getEditorVerwaltung();
    PermissionServiceAsync permissionVerwaltung = ClientsideSettings.getPermissionVerwaltung();
    Shareable toShare;

    public ShareShareable() {

    }

    public ShareShareable(Shareable s) {
        this.toShare = s;
    }

    @Override
    public String getHeadlineText() {
        return "Share with..";
    }

    @Override
    public String getSubHeadlineText() {
        return "";
    }

    @Override
    public void run() {

        populateTheOracle();

        setUpTheListBox();

        userPicker = new SuggestBox(oracle);

        setUpButtonClickHandler();

        setupUpDisplay();

    }

    private void setupUpDisplay() {

        this.add(userPicker);
        this.add(levelOptions);
        this.add(confirmButton);

    }

    private void setUpButtonClickHandler() {
        confirmButton.addClickHandler(new ConfirmClickHandler());
    }

    private void setUpTheListBox() {

        levelOptions = new ListBox();
        levelOptions.addItem("Read");
        levelOptions.addItem("Edit");
        levelOptions.addItem("Delete");
        levelOptions.setVisibleItemCount(1);

    }

    private void populateTheOracle() {

        editor.getAllEmails(new AsyncCallback<Vector<String>>() {

            @Override
            public void onFailure(Throwable caught) {
                GWT.log("Fehlschlag, s.u.:");
                GWT.log(caught.toString());
            }

            @Override
            public void onSuccess(Vector<String> result) {

                oracle.addAll(result);

            }
        });

    }

    private class ConfirmClickHandler implements ClickHandler {

        @Override
        public void onClick(ClickEvent event) {

            String userEmail = userPicker.getValue();
            String selectedLevelAsString = levelOptions.getSelectedItemText();
            Level l = Level.NONE;

            switch (selectedLevelAsString) {
                case "Read":
                    l = Level.READ;
                    break;
                case "Edit":
                    l = Level.EDIT;
                    break;
                case "Delete":
                    l = Level.DELETE;
                    break;

            }

            GWT.log("Selected Level:" + l);

            UserInfo loggedInUser = IT_Projekt.getLoggedInUser();
            if (userEmail.equals(loggedInUser.getEmailAddress())) {
                Notificator.getNotificator().showError("You cannot share with yourself.");
                return;
            }

            permissionVerwaltung.shareWith(userEmail, toShare, l, new PermissionCallback());
        }

    }

    private class PermissionCallback implements AsyncCallback<Void> {

        @Override
        public void onFailure(Throwable caught) {
            GWT.log("Failure beim Versuch zu Teilen!");
            GWT.log(caught.toString());
        }

        @Override
        public void onSuccess(Void result) {
            Notificator.getNotificator().showSuccess("Note was shared with " + userPicker.getValue());
            ApplicationPanel.getApplicationPanel().replaceContentWith(new ShowAllPermissions());
        }

    }


}

