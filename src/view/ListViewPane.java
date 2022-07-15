package view;


import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;


/**
 * This class is a ListView that has a label above it that can be set upon creation
 * @author p2508322
 *
 * @param <T> The reference type objects that you wish the ListView to house.
 */
public class ListViewPane<T> extends VBox {

	private Label label;
	private ListView<T> listView;
	private ObservableList<T> observableList;

	//custom
	public ListViewPane(int width, int height, String labelText)  {
		ArrayList<T> list = new ArrayList<>();
		// create observable list from an empty array list
		// this solves the nullpointers
		observableList = FXCollections.observableArrayList(list);
		// build UI controls
		listView = new ListView<T>(observableList);
		setListView(observableList);
		label = new Label(labelText);
		
		//style 
		this.setPadding(new Insets(10));
		setListViewSize(width, height);
		listView.setMaxHeight(Double.MAX_VALUE);
		
		// set V grow properties of this and the list view box.
		VBox.setVgrow(this, Priority.ALWAYS);
		VBox.setVgrow(listView, Priority.ALWAYS);
		
		// add nodes to pane.
		this.getChildren().addAll(label, listView);
	}
	
	// PUBLIC methods
	// getters/setters... 
	public ObservableList<T> getObservableList() {
		return observableList;
	}
	
	public int getObservableListCount()  {
		return observableList.size();
	}
	
	public void addItemToListView(T item)  {
		ObservableList<T> ol = listView.getItems();
		ol.add(item);
	}
	
	public void removeItemFromListView(T item)  {
		ObservableList<T> ol = listView.getItems();
		ol.remove(item);
	}
	
	public void setObservableList(ObservableList<T> observableList) {
		this.observableList = observableList;
	}
	
	public ListView<T> getListView() {
		return listView;
	}
	
	public void setListView(ObservableList<T> lv)  {
		listView.setItems(lv);;
	}
	
	// set listview size
	public void setListViewSize(int x, int y)  {
		listView.setPrefSize(x, y);
	}
}
