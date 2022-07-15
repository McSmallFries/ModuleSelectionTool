package view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class OverviewSelectionPane extends GridPane {

	private TextArea profile, selected, reserved;
	private ControlButton btnSave;
	
	public  OverviewSelectionPane()  {
		
		// build controls
		profile = new TextArea();
		selected = new TextArea();
		reserved = new TextArea();
		btnSave = new ControlButton("Save Overview");
		btnSave.makeButtonBig();
		
		// button container 
		HBox btnContainer = new HBox(btnSave);

		// styling
		this.setVgap(20);
		this.setHgap(20);
		this.setPadding(new Insets(30));
		this.setAlignment(Pos.CENTER);
		
		profile.setEditable(false);
		reserved.setEditable(false);
		selected.setEditable(false);
		selected.setMinHeight(200);
		
		btnContainer.setAlignment(Pos.CENTER);
		btnContainer.setMaxHeight(30);
		
		//set H/V grow
		GridPane.setHgrow(profile, Priority.SOMETIMES);
		GridPane.setHgrow(selected, Priority.ALWAYS);
		GridPane.setHgrow(reserved, Priority.ALWAYS);
		GridPane.setHgrow(btnContainer, Priority.SOMETIMES);
		
		GridPane.setVgrow(profile, Priority.SOMETIMES);
		GridPane.setVgrow(selected, Priority.ALWAYS);
		GridPane.setVgrow(reserved, Priority.ALWAYS);
		GridPane.setVgrow(btnContainer, Priority.SOMETIMES);
		
		// Add to the GridPane
		this.add(profile, 0, 0, 2, 1);
		this.add(selected, 0, 1, 1, 1);
		this.add(reserved, 1, 1, 1, 1);
		this.add(btnContainer, 0, 2, 2, 1);
	}
	
	// getters and setters

	public TextArea getProfile() {
		return profile;
	}

	public void setProfile(TextArea profile) {
		this.profile = profile;
	}

	public TextArea getSelected() {
		return selected;
	}

	public void setSelected(TextArea selected) {
		this.selected = selected;
	}

	public TextArea getReserved() {
		return reserved;
	}

	public void setReserved(TextArea reserved) {
		this.reserved = reserved;
	}

	public ControlButton getBtnSave() {
		return btnSave;
	}
	
	// set the text areas' text property.
	
	
	public void setProfileText(String txt)  {
		profile.setText(txt);
	}
	public void setSelectedText(String txt)  {
		selected.setText(txt);
	}
	
	public void setReservedText(String txt)  {
		reserved.setText(txt);
	}
	
	
	public void addBtnSaveClickHandler(EventHandler<ActionEvent> handler)  {
		btnSave.setOnAction(handler);
	}
}
