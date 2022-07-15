package view;

import javafx.geometry.Insets;
import javafx.scene.control.Button;

/**
 * A reusable button class designed based on the buttons in the assignment spec.
 * 
 * @author p2508322
 *
 */
public class ControlButton extends Button {
	
	private Button button;
	
	/**
	 * ControlButton constructor.
	 * @param btnText The text displayed by the button.
	 */
	public ControlButton(String btnText)  {
		// create button 
		button = new Button(btnText);
		
		// add styling.
		this.setText(btnText);
		this.setPrefWidth(90);
		this.setMaxWidth(90);
		this.setPadding(new Insets(5, 20, 5, 20));
	}
	
	/**
	 * Provides access to the encapsulated button.
	 * @return button
	 */
	public Button getButton()  {
		return button;
	}
	
	/**
	 * button field can be turned into a big button, adding emphasis to the control if/where needed.
	 */
	public void makeButtonBig()  {
		this.setPrefWidth(110);
		this.setMaxWidth(110);
		this.setPadding(new Insets(5, 0, 5, 0));
	}
	
	@Override
	public boolean equals(Object other)  {
		if (other == this)  {
			return true;
		}
		if (!(other instanceof ControlButton))  {
			return false;
		}
		ControlButton cb = (ControlButton) other;
		
		return this.getButton().equals(cb.getButton());
		
	}
}
