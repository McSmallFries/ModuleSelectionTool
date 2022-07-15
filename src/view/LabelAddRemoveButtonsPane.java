package view;


import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

/**
 * This is a reusable GridPane pane consisting of a label, 1 add, and 1 remove button.
 * @author mh
 *
 */
public class LabelAddRemoveButtonsPane extends GridPane {

	private Label label;
	private ControlButton btnAdd;
	private ControlButton btnRemove;
	

	// default constructor
	public LabelAddRemoveButtonsPane()  {
		this("This is a label");
	}
	
	// custom constructor
	public LabelAddRemoveButtonsPane(String labelText)  {
		// create UI elements for this pane.
		label = new Label(labelText);
		btnAdd = new ControlButton("Add");
		btnRemove = new ControlButton("Remove");
		
		// style UI elements
		this.setHgap(20);
		this.setMaxHeight(Double.MAX_VALUE);
		this.setPadding(new Insets(10));
		
		GridPane.setVgrow(this, Priority.ALWAYS);
		
		
		// set alignment of this pane
		this.setAlignment(Pos.CENTER);
		
		// add controls to grid
		this.add(label, 0, 0);
		this.add(btnAdd, 1, 0);
		this.add(btnRemove, 2, 0);
	}
	
	
	public ControlButton getBtnAdd() {
		return btnAdd;
	}

	public ControlButton getBtnRemove() {
		return btnRemove;
	}

	public void setBtnRemove(ControlButton btnRemove) {
		this.btnRemove = btnRemove;
	}

}
