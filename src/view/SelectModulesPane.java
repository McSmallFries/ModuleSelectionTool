package view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import model.Module;
import model.Schedule;

/**
 * A GUI for the Select Modules window.
 * @author p2508322
 *
 */
public class SelectModulesPane extends GridPane {
	
	private ListViewPane<Module> unselectedTerm1, unselectedTerm2, selectedYearLong, selectedTerm1, selectedTerm2;
	private LabelAddRemoveButtonsPane term1Btns, term2Btns;
	private LabelTextAreaPane creditsTerm1, creditsTerm2;
	private ControlButton btnReset, btnSubmit;
	
	public SelectModulesPane()   {
		
		// construct containers.
		VBox leftContainer = new VBox();
		VBox rightContainer = new VBox();
		
		// contruct the controls
		term1Btns = new LabelAddRemoveButtonsPane("Term 1");
		term2Btns = new LabelAddRemoveButtonsPane("Term 2");
		unselectedTerm1 = new ListViewPane<>(200,100, "Unselected Term 1 modules");
		selectedYearLong = new ListViewPane<>(200,50, "Selected Year Long modules");
		unselectedTerm2 = new ListViewPane<>(200,100, "Unselected Term 2 modules");
		selectedTerm1 = new ListViewPane<>(200,50, "Selected Term 1 modules");
		selectedTerm2 = new ListViewPane<>(200,100, "Selected Term 2 modules");
		creditsTerm1 = new LabelTextAreaPane("Current term 1 credits: ");
		creditsTerm2 = new LabelTextAreaPane("Current term 2 credits: ");
		btnReset = new ControlButton("Reset");
		btnSubmit = new ControlButton("Submit");
		HBox buttonContainer = new HBox(btnReset, btnSubmit);
		
		// add style
		this.setAlignment(Pos.CENTER);
		this.setHgap(30);
		this.setVgap(30);
		this.setPadding(new Insets(30));
		leftContainer.setSpacing(10);
		rightContainer.setSpacing(10);
		buttonContainer.setSpacing(30);
		buttonContainer.setMaxHeight(100);
		buttonContainer.setAlignment(Pos.CENTER);
	
		// grow with window width/height change
		GridPane.setHgrow(leftContainer, Priority.ALWAYS);
		GridPane.setHgrow(rightContainer, Priority.ALWAYS);
		GridPane.setHgrow(buttonContainer, Priority.ALWAYS);
		GridPane.setVgrow(rightContainer, Priority.ALWAYS);
		GridPane.setVgrow(leftContainer, Priority.ALWAYS);
		
		// align left and right columns central.
		leftContainer.setAlignment(Pos.CENTER);
		rightContainer.setAlignment(Pos.CENTER);	
		
		// add controls to left/right accordingly
		leftContainer.getChildren().addAll(unselectedTerm1, term1Btns, unselectedTerm2, term2Btns, creditsTerm1);
		rightContainer.getChildren().addAll(selectedYearLong, selectedTerm1, selectedTerm2, creditsTerm2);
		
		//add to this pane
		this.add(leftContainer, 0,0,1,1);
		this.add(rightContainer, 1,0,1,1);
		
		// should span 2 columns
		this.add(buttonContainer, 0, 1, 2, 1);
	}
	
	public ListViewPane<Module> getUnselectedTerm1() {
		return unselectedTerm1;
	}

	public void setUnselectedTerm1(ListViewPane<Module> unselectedTerm1) {
		this.unselectedTerm1 = unselectedTerm1;
	}

	public ListViewPane<Module> getSelectedYearLong() {
		return selectedYearLong;
	}

	public void setSelectedYearLong(ListViewPane<Module> selectedYearLong) {
		this.selectedYearLong = selectedYearLong;
	}

	public ListViewPane<Module> getSelectedTerm1() {
		return selectedTerm1;
	}

	public void setSelectedTerm1(ListViewPane<Module> selectedTerm1) {
		this.selectedTerm1 = selectedTerm1;
	}

	public ListViewPane<Module> getSelectedTerm2() {
		return selectedTerm2;
	}

	public void setSelectedTerm2(ListViewPane<Module> selectedTerm2) {
		this.selectedTerm2 = selectedTerm2;
	}

	public ListViewPane<Module> getUnselectedTerm2() {
		return unselectedTerm2;
	}

	public LabelAddRemoveButtonsPane getTerm1Btns() {
		return term1Btns;
	}

	public LabelAddRemoveButtonsPane getTerm2Btns() {
		return term2Btns;
	}

	public LabelTextAreaPane getCreditsTerm1() {
		return creditsTerm1;
	}

	public LabelTextAreaPane getCreditsTerm2() {
		return creditsTerm2;
	}

	public ControlButton getBtnReset() {
		return btnReset;
	}

	public ControlButton getBtnSubmit() {
		return btnSubmit;
	}
	
	public void deductTerm1CreditsBy(int credits)  {
		// delegate
		creditsTerm1.deductTxtCreditBoxBy(credits);
	}
	public void deductTerm2CreditsBy(int credits)  {
		// delegate
		creditsTerm2.deductTxtCreditBoxBy(credits);
	}
	public void increaseTerm1CreditsBy(int credits)  {
		// delegate
		creditsTerm1.increaseTxtCreditBoxBy(credits);
	}
	public void increaseTerm2CreditsBy(int credits)  {
		// delegate
		creditsTerm2.increaseTxtCreditBoxBy(credits);
	}
	

	@Override
	public String toString()  {
		String unselectedTerm1s = unselectedTerm1.toString();
		String unselectedTerm2s = unselectedTerm2.toString();
		String selectedYearLongS = selectedYearLong.toString();
		String selectedTerm1s = selectedTerm1.toString();
		String selectedTerm2s = selectedTerm2.toString();
		
		
		return "SelectModulesPane:[" + "unselectedTerm1=" + unselectedTerm1s + ", unselectedTerm2=" + unselectedTerm2s
				+ ", selectedYearLong=" + selectedYearLongS + ", selectedTerm1=" + selectedTerm1s
				+ ", selectedTerm2=" + selectedTerm2s + " ]";
	}

	/**
	 * Inner Class that groups a TextArea with a Label. Access only needed for controlling text area.
	 * Unable to construct outside SelectModulesPane as this pane shouldn't exist outside of the Select Modules tab.
	 * 
	 * @author p2508322
	 *
	 */
	public class LabelTextAreaPane extends HBox {
		
		private Label label;
		// needs access from outside of class for controller
		private TextArea txtCreditBox;
		private int numOfCredits; // 120 
		
		// cannot be constructed outside of this class
		private LabelTextAreaPane(String labelText)  {
			// construct controls
			label = new Label(labelText);
			numOfCredits = 60; // set initial value of credits
			txtCreditBox = new TextArea(String.valueOf(numOfCredits));
			// edit properties
			txtCreditBox.setEditable(false);
			txtCreditBox.setMinSize(10, 27);
			txtCreditBox.setMaxWidth(70);
			txtCreditBox.setMaxHeight(10);
			this.setSpacing(15);
			this.setAlignment(Pos.CENTER);
			
			// add to HBox
			this.getChildren().addAll(label, txtCreditBox);
		}
		
		public TextArea getTxtCreditBox()  {
			return txtCreditBox;
		}
		public int getCredits()  {
			return numOfCredits;
		}
		
		/** 
		 * sets both the value in the textBox and the int value
		 * @param newVal
		 */
		public void setCredits(int newVal)  {
			numOfCredits = newVal; // set the private num field
			txtCreditBox.setText(String.valueOf(newVal)); // update txtbox value;
		}
		
		/**
		 * decrease the credits text area and the integer credits value
		 * by an amount.
		 * @param credits Amount to decrease by.
		 */
		public void deductTxtCreditBoxBy(int credits)  {
			int currently = Integer.valueOf(txtCreditBox.getText());
			setCredits(currently -= credits);
		}
		
		/**
		 * increase the credits text area and the integer credits value
		 * by an amount.
		 * @param credits Amount to increase by.
		 */
		public void increaseTxtCreditBoxBy(int credits)  {
			int currently = Integer.valueOf(txtCreditBox.getText());
			setCredits(currently += credits);
		}
	}

	public void addAddButtonClickHandler(EventHandler<ActionEvent> handler) {
		term1Btns.getBtnAdd().setOnAction(handler);
		term2Btns.getBtnAdd().setOnAction(handler);
	}
	
	public void addRemoveButtonClickHandler(EventHandler<ActionEvent> handler)  {
		term1Btns.getBtnRemove().setOnAction(handler);
		term2Btns.getBtnRemove().setOnAction(handler);
	}
	
	public void addResetButtonClickHandler(EventHandler<ActionEvent> handler)  {
		btnReset.setOnAction(handler);
	}
	
	public void addSubmitButtonClickHandler(EventHandler<ActionEvent> handler)  {
		btnSubmit.setOnAction(handler);
	}
	
	
}
