package view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Accordion;
import javafx.scene.layout.VBox;


/**
 * A GUI for a window where a user can reserve modules.
 * 
 * @author p2508322
 *
 */
public class ReserveModulesPane extends VBox {
	
	private Accordion accordion;
	private ReserveModulesTitledPane term1tp, term2tp;
	
	public ReserveModulesPane()  {
		// construct the controls
		term1tp = new ReserveModulesTitledPane("1");
		term2tp = new ReserveModulesTitledPane("2");
		accordion = new Accordion();
		
		// add to the accordion
		accordion.getPanes().addAll(term1tp, term2tp);
		
		// styling 
		this.setPadding(new Insets(20));
		
		// add to the window
		this.getChildren().addAll(accordion);
	}
	
	public ReserveModulesTitledPane getTerm1tp()  {
		return term1tp;
	}
	
	public ReserveModulesTitledPane getTerm2tp()  {
		return term2tp;
	}
	
	public Accordion getAccordion()  {
		return accordion;
	}
	
	public void addAddButtonClickHandler(EventHandler<ActionEvent> handler) {
		term1tp.getButtons().getBtnAdd().setOnAction(handler);
		term2tp.getButtons().getBtnAdd().setOnAction(handler);
	}
	
	public void addRemoveButtonClickHandler(EventHandler<ActionEvent> handler) {
		term1tp.getButtons().getBtnRemove().setOnAction(handler);
		term2tp.getButtons().getBtnRemove().setOnAction(handler);
	}
	
	public void addConfirmTerm2ButtonClickHandler(EventHandler<ActionEvent> handler)  {
		term2tp.getBtnConfirm().setOnAction(handler);
	}
	
	public void addConfirmTerm1ButtonClickHandler(EventHandler<ActionEvent> handler)  {
		term1tp.getBtnConfirm().setOnAction(handler);
	}
}
