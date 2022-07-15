package view;

import javafx.geometry.Pos;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import model.Module;

/**
 * A TitledPane to house the ListViews (and their labels) that belong to the Reserve Modules tab.
 * 
 * @author p2508322
 *
 */
public class ReserveModulesTitledPane extends TitledPane {
	
	private ListViewPane<Module> unselected;
	private ListViewPane<Module> reserved;
	private LabelAddRemoveButtonsPane buttons; // put buttons in HBox and add confirm button after next to the others
	private ControlButton btnConfirm; // add as field so it can be accessed via public method
	
	public ReserveModulesTitledPane()  {
		this("#");
	}
	
	/**
	 * Custom constructor for ReserveModulesTitledPane
	 * @param term A string that should contain only an integer that will denote which term the label will display.
	 */
	public ReserveModulesTitledPane(String term)  {
		
		// set text of this TitledPane
		this.setText("Term " + term + " modules");
		
		// build UI Controls
		unselected = new ListViewPane<Module>(250, 500, "Unselected term " + term + " modules");
		reserved = new ListViewPane<Module>(250, 500, "Reserved term " + term + " modules");
		buttons = new LabelAddRemoveButtonsPane("Reserve 30 credits worth of term " + term + " modules");
		btnConfirm = new ControlButton("Confirm");
		
		// build containers
		HBox listBoxContainer = new HBox();
		HBox buttonsContainer = new HBox();
		VBox pageContainer = new VBox(listBoxContainer, buttonsContainer);
		
		// add controls to their respective containers...
		listBoxContainer.getChildren().addAll(unselected, reserved);
		buttonsContainer.getChildren().addAll(buttons, btnConfirm);
		
		//style 
		HBox.setHgrow(unselected, Priority.ALWAYS);
		HBox.setHgrow(reserved, Priority.ALWAYS);
		buttonsContainer.setAlignment(Pos.CENTER);
		buttonsContainer.setSpacing(10);
		//btnConfirm.setPadding(new Insets(4, 19, 4, 19));
		
		//add to this pane
		this.setContent(pageContainer);
	}
	
	// getters

	public ListViewPane<Module> getUnselected() {
		return unselected;
	}

	public ListViewPane<Module> getReserved() {
		return reserved;
	}

	public LabelAddRemoveButtonsPane getButtons() {
		return buttons;
	}

	public ControlButton getBtnConfirm() {
		return btnConfirm;
	}
	
}
