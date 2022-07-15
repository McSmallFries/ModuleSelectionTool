package controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;
import javafx.scene.control.TextArea;
import model.Course;
import model.Schedule;
import model.Module;
import model.Name;
import model.StudentProfile;
import view.ModuleSelectionToolRootPane;
import view.OverviewSelectionPane;
import view.ReserveModulesPane;
import view.ReserveModulesTitledPane;
import view.ControlButton;
import view.CreateStudentProfilePane;
import view.ListViewPane;
import view.ModuleSelectionToolMenuBar;
import view.SelectModulesPane;

public class ModuleSelectionToolController  {

	/*
	 * BUGS: 
	 * When loading a profile, if you change a reserved module and re-save, you will be prompted to "select 2 modules per term".
	 * This is because the validation checks that the reserved modules totals 4, and when you swap them, it doesnt remove the old one.
	 * 
	 * Similar to the other bug, but with the selectmodulespane and selectedModules in model.
	 * 
	 * may need to code extra methods, instead of invoking the button clicks programmatically.
	 * 
	 */
	
	//fields to be used throughout this class
	private ModuleSelectionToolRootPane view;
	private StudentProfile model;
	private CreateStudentProfilePane cspp;
	private ModuleSelectionToolMenuBar mstmb;
	private SelectModulesPane smp;
	private ReserveModulesPane rmp;
	private OverviewSelectionPane osp;
	

	public ModuleSelectionToolController(ModuleSelectionToolRootPane view, StudentProfile model) {
		//initialise view and model fields
		this.view = view;
		this.model = model;
			
		//initialise view sub-container fields
		cspp = view.getCreateStudentProfilePane();
		mstmb = view.getModuleSelectionToolMenuBar();
		smp = view.getSelectModulesPane();
		rmp = view.getReserveModulesPane();
		osp = view.getOverviewSelectionPane();
		
		//add courses to combobox in create student profile pane using the generateAndReturnCourses helper method below
		cspp.addCoursesToComboBox(generateAndReturnCourses());
		
		//attach event handlers to view using private helper method
		this.attachEventHandlers();
	}


	//event handler (currently empty), which can be used for creating a profile
	private class CreateStudentProfileHandler implements EventHandler<ActionEvent> {
		public void handle(ActionEvent e) {
			// validate these fields 
			Course selectedCourse = view.getCreateStudentProfilePane().getSelectedCourse();
			String selectedPnum = view.getCreateStudentProfilePane().getStudentPnumber();
			Name name = view.getCreateStudentProfilePane().getStudentName();
			String email = view.getCreateStudentProfilePane().getStudentEmail();
			LocalDate date = view.getCreateStudentProfilePane().getStudentDate();
			System.out.println("Submit button clicked...\nValidating...\n");
			// if fields are valid
			String validationMessage = validateForm(selectedPnum, name, email, date);
			if (validationMessage.isEmpty()) {
				// update model
				setModel(selectedCourse, selectedPnum, name, email, date);
				// reset SMP view to be populated using method
				resetSMPview();
				//switch tab to select modules
				view.changeTab(1);
				//disallow user to change details after submitting profile.
				view.getTabPane().getTabs().get(0).setDisable(true);
			}
			else {
				System.out.println("Validation failed!" + "\n" + validationMessage);
				//show alert with validation message.
				Alert validationError = new Alert(AlertType.ERROR, validationMessage);
				validationError.setHeaderText("Validation Error...");
				validationError.setResizable(false);
				validationError.setTitle("Validation!");
				validationError.show();
			}
		}
	}
	
	private class AddButtonClickHandler implements EventHandler<ActionEvent>  {
		@Override
		public void handle(ActionEvent e) {
			// store the source of the event
			ControlButton clicked = (ControlButton) e.getSource();
			// store conditions as variables.
			boolean parentIsSMP = clicked.getParent().getParent().getParent() instanceof SelectModulesPane;
			//determine whether the click came from SMP or RMP.
			if (parentIsSMP)  {
				moveUnselectedToSelected(smp, clicked);
			}
			else  {
				moveUnselectedToSelected(rmp, clicked);
				System.out.println("RMP add button click");
			}
		}
	}

	private class RemoveButtonClickHandler implements EventHandler<ActionEvent>  {
		@Override
		public void handle(ActionEvent e)  {
			ControlButton clicked = (ControlButton) e.getSource();
			boolean parentIsSMP = clicked.getParent().getParent().getParent() instanceof SelectModulesPane;
			if (parentIsSMP)  {
				moveSelectedToUnselected(smp, clicked); // uses SMP overload
			}
			else {
				moveSelectedToUnselected(rmp, clicked); // uses RMP overload
				System.out.println("RMP remove button click");
			}
		}
	}
	
	private class ResetButtonClickHandler implements EventHandler<ActionEvent>  {
		@Override
		public void handle(ActionEvent e)  {
			resetSMPview();
		}
	}
	
	private class SubmitButtonClickHandler implements EventHandler<ActionEvent>  {
		@Override
		public void handle(ActionEvent e) {
			// get the number of credits outstanding from both 
			int term1credits = view.getSelectModulesPane().getCreditsTerm1().getCredits();
			int term2credits = view.getSelectModulesPane().getCreditsTerm2().getCredits();
			// get the ReserveModulesPane
			ReserveModulesPane rmp = view.getReserveModulesPane();
			// check to see both credits boxes are at 0
			if (term1credits + term2credits == 120)   {
				resetRMPview();
			}
			else {
				System.out.println("Credits outstanding");
				Alert alert = new Alert(AlertType.ERROR, "Please select 120 total credits.");
				alert.setHeaderText("Insufficient credits");
				alert.show();
			}
		}
	}
		
	private class ConfirmButtonClickHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent e) {
			// capture details of reserved modules for model
			// only if both module reserved has 2 items
			ObservableList<Module> reserved1 = rmp.getTerm1tp().getReserved().getObservableList();
			ObservableList<Module> reserved2 = rmp.getTerm2tp().getReserved().getObservableList();
			ArrayList<Module> moduleArray = new ArrayList<>(reserved1);
			moduleArray.addAll(reserved2);
			// if 4 modules selected (2 reserved modules each term)
			if (moduleArray.size() == 4)  {
				// save data to model
				for (Module m : moduleArray)  {
					model.addReservedModule(m);
				}
				// change tab to overview 
				view.changeTab(3);
				System.out.println("Reserved Modules: " + model.getAllReservedModules());
				// then populate the Overview selection pane
				// store each text area from pane...
				TextArea profile = osp.getProfile();
				TextArea selected = osp.getSelected();
				TextArea reserved = osp.getReserved();
				// store student profile data from model
				String pNum = model.getStudentPnumber();
				String name = model.getStudentName().getFullName();
				String email = model.getStudentEmail();
				LocalDate date = model.getSubmissionDate();
				Course course = model.getStudentCourse();
				// get the reserved and selected modules
				Set<Module> selectedSet = model.getAllSelectedModules();
				Set<Module> reservedSet = model.getAllReservedModules();
				
				// set profile text area
				profile.setText("Name: " + name + "\nP Number: " + pNum + "\nEmail: " + email + "\nDate: " + 
				date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)) +"\nCourse: " + course);
				String underline = "\n==============\n";
				String selectedHeader = new String("Selected modules: " + underline);
				String reservedHeader = new String("Reserved modules: " + underline);
				String selectedModulesString = selectedHeader;
				String reservedModulesString = reservedHeader;
				// amend ths output strings to be populated with their respective
				// modules into the required string format.
				for (Module m : selectedSet)  {
					selectedModulesString +=  m.getModuleTextOutput() + "\n\n";
				}
				for (Module m : reservedSet)  {
					reservedModulesString += m.getModuleTextOutput() + "\n\n";
				}
				
				// set the modules text areas
				selected.setText(selectedModulesString);
				reserved.setText(reservedModulesString);
			}
			else {
				// show alert
				Alert alert = new Alert(AlertType.ERROR, "Please select 2 modules"
						+ " from each term.");
				alert.setTitle("Invalid selection.");
				alert.setResizable(false);
				alert.show();
			}
		}
	}
	
	private class SaveHandler implements EventHandler<ActionEvent>  {
		@Override
		public void handle(ActionEvent e) {
			// must at least have created a profile
			if (!model.getStudentPnumber().equals("")) {
				model.writeProfileToFile();
			}
			else  {
				// push alert
				Alert noProfile = new Alert(AlertType.ERROR);
				noProfile.setHeaderText("Profile not created.");
				noProfile.setContentText("Please create a profile.");
				noProfile.setResizable(false);
				noProfile.show();
			}
		}
	}
	
	private class LoadFileHandler implements EventHandler<ActionEvent>  {
		@Override
		public void handle(ActionEvent e) {
			FileChooser fileChooser = new FileChooser();
			//set initial directory to 'profiles' folder
			File defDir = new File("profiles/");
			fileChooser.setInitialDirectory(defDir);
			File selectedFile = fileChooser.showOpenDialog(null);
			
			// instantiate StudentProfile object
			StudentProfile sp = new StudentProfile();
			// try with resources
			try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(selectedFile)))  {
				sp = (StudentProfile) ois.readObject();
				ois.close();
			} 
			catch (IOException ioe)  {
				ioe.printStackTrace();
				System.out.println("Error reading from file.");
			}
			catch (ClassNotFoundException cnf)  {
				System.out.println("Class not found");
			}
			catch (NullPointerException np)  {
				System.out.println("User closed window.");
			}
			// check to see object loaded in correctly, using pnumber
			if (!sp.getStudentPnumber().equals(""))  {
				// try to use StudentProfile method to read in 
				// the profile object based on P number.
				try {
					model.readProfileFromFile(sp.getStudentPnumber());
				} 
				catch (IOException ioe) {	
					ioe.printStackTrace();
					System.out.println(ioe.getMessage());
				}
				catch (ClassNotFoundException cnf)  {
					cnf.printStackTrace();
					System.out.println(cnf.getMessage());
				}
				System.out.println(model.getStudentPnumber());
				System.out.println(model.toString());
				// saved file will always have student data
				// so can be loaded straight in.
				restoreCreateStudentProfile(model);
				
				if (model.hasSelectedModules())  {
					if (model.hasReservedModules())  {
						// take to selection overview
						restoreSelectModulesPane();
						restoreReserveModulesPane();
					}
					else  {
						// take to reserve modules pane
						restoreSelectModulesPane();
					}
				}
			}
			else  {
				// show alert
				System.out.println("Profile doesnt have P number.");
			}
		}
	}
	
	private class AboutMenuHandler implements EventHandler<ActionEvent>  {
		@Override
		public void handle(ActionEvent event) {
			Alert about = new Alert(AlertType.INFORMATION, "About", ButtonType.OK);
			String headerText = "Final Year Module Selection Tool";
			String contentText = "Version: v1.1\n\nDeveloped by: Maxim Handley  -  2021";
			about.setTitle("About");
			about.setHeaderText(headerText);
			about.setContentText(contentText);
			about.show();
		}
		
	}
	
	
	//helper method - generates course and module data and returns courses within an array
	// TODO this can be done dynamically using a Scanner and text file
	private Course[] generateAndReturnCourses() {
		Module imat3423 = new Module("IMAT3423", "Systems Building: Methods", 15, true, Schedule.TERM_1);
		Module ctec3451 = new Module("CTEC3451", "Development Project", 30, true, Schedule.YEAR_LONG);
		Module ctec3902_SoftEng = new Module("CTEC3902", "Rigorous Systems", 15, true, Schedule.TERM_2);	
		Module ctec3902_CompSci = new Module("CTEC3902", "Rigorous Systems", 15, false, Schedule.TERM_2);
		Module ctec3110 = new Module("CTEC3110", "Secure Web Application Development", 15, false, Schedule.TERM_1);
		Module ctec3605 = new Module("CTEC3605", "Multi-service Networks 1", 15, false, Schedule.TERM_1);	
		Module ctec3606 = new Module("CTEC3606", "Multi-service Networks 2", 15, false, Schedule.TERM_2);	
		Module ctec3410 = new Module("CTEC3410", "Web Application Penetration Testing", 15, false, Schedule.TERM_2);
		Module ctec3904 = new Module("CTEC3904", "Functional Software Development", 15, false, Schedule.TERM_2);
		Module ctec3905 = new Module("CTEC3905", "Front-End Web Development", 15, false, Schedule.TERM_2);
		Module ctec3906 = new Module("CTEC3906", "Interaction Design", 15, false, Schedule.TERM_1);
		Module ctec3911 = new Module("CTEC3911", "Mobile Application Development", 15, false, Schedule.TERM_1);
		Module imat3410 = new Module("IMAT3104", "Database Management and Programming", 15, false, Schedule.TERM_2);
		Module imat3406 = new Module("IMAT3406", "Fuzzy Logic and Knowledge Based Systems", 15, false, Schedule.TERM_1);
		Module imat3611 = new Module("IMAT3611", "Computer Ethics and Privacy", 15, false, Schedule.TERM_1);
		Module imat3613 = new Module("IMAT3613", "Data Mining", 15, false, Schedule.TERM_1);
		Module imat3614 = new Module("IMAT3614", "Big Data and Business Models", 15, false, Schedule.TERM_2);
		Module imat3428_CompSci = new Module("IMAT3428", "Information Technology Services Practice", 15, false, Schedule.TERM_2);
		
		Course compSci = new Course("Computer Science");
		compSci.addModuleToCourse(imat3423);
		compSci.addModuleToCourse(ctec3451);
		compSci.addModuleToCourse(ctec3902_CompSci);
		compSci.addModuleToCourse(ctec3110);
		compSci.addModuleToCourse(ctec3605);
		compSci.addModuleToCourse(ctec3606);
		compSci.addModuleToCourse(ctec3410);
		compSci.addModuleToCourse(ctec3904);
		compSci.addModuleToCourse(ctec3905);
		compSci.addModuleToCourse(ctec3906);
		compSci.addModuleToCourse(ctec3911);
		compSci.addModuleToCourse(imat3410);
		compSci.addModuleToCourse(imat3406);
		compSci.addModuleToCourse(imat3611);
		compSci.addModuleToCourse(imat3613);
		compSci.addModuleToCourse(imat3614);
		compSci.addModuleToCourse(imat3428_CompSci);

		Course softEng = new Course("Software Engineering");
		softEng.addModuleToCourse(imat3423);
		softEng.addModuleToCourse(ctec3451);
		softEng.addModuleToCourse(ctec3902_SoftEng);
		softEng.addModuleToCourse(ctec3110);
		softEng.addModuleToCourse(ctec3605);
		softEng.addModuleToCourse(ctec3606);
		softEng.addModuleToCourse(ctec3410);
		softEng.addModuleToCourse(ctec3904);
		softEng.addModuleToCourse(ctec3905);
		softEng.addModuleToCourse(ctec3906);
		softEng.addModuleToCourse(ctec3911);
		softEng.addModuleToCourse(imat3410);
		softEng.addModuleToCourse(imat3406);
		softEng.addModuleToCourse(imat3611);
		softEng.addModuleToCourse(imat3613);
		softEng.addModuleToCourse(imat3614);
		
		
		Course[] courses = new Course[2];
		courses[0] = compSci;
		courses[1] = softEng;

		return courses;
	}

	
	//helper method - used to attach event handlers
	private void attachEventHandlers() {
		//attach an event handler to the create student profile pane
		cspp.addCreateStudentProfileHandler(new CreateStudentProfileHandler());
		smp.addAddButtonClickHandler(new AddButtonClickHandler());
		smp.addResetButtonClickHandler(new ResetButtonClickHandler());
		smp.addRemoveButtonClickHandler(new RemoveButtonClickHandler());
		smp.addSubmitButtonClickHandler(new SubmitButtonClickHandler());
		rmp.addAddButtonClickHandler(new AddButtonClickHandler());
		rmp.addRemoveButtonClickHandler(new RemoveButtonClickHandler());
		rmp.addConfirmTerm2ButtonClickHandler(new ConfirmButtonClickHandler());
		rmp.addConfirmTerm1ButtonClickHandler(e -> rmp.getAccordion().setExpandedPane(rmp.getTerm2tp()));
		osp.addBtnSaveClickHandler(new SaveHandler());
		mstmb.addSaveHandler(new SaveHandler()); //saves model state to file
		mstmb.addLoadHandler(new LoadFileHandler()); // load model state from file
		mstmb.addAboutHandler(new AboutMenuHandler());
		//attach an event handler to the menu bar that closes the application
		mstmb.addExitHandler(e -> System.exit(0));
		
	}
	
	/**
	 * This method will validate user input from the form on the CreateStudentProfilePane.
	 * An empty string indicating a valid result OR an error message of what needs resubmitting.
	 * @param pNum
	 * @param nm
	 * @param email
	 * @param dt
	 * @return
	 * 		
	 */
	private String validateForm(String pNum, Name nm, String email, LocalDate dt)  {
		String msg = pNumIsValid(pNum) + nameIsValid(nm) + emailIsValid(email, pNum) + dateIsValid(dt);
		if (msg.isEmpty())  {
			return "";
		}
		return msg;
	}
	
	private String pNumIsValid(String pnum)  {
		String valid = "Invalid P number.\n";
		// check string is not empty
		if (!pnum.equals(""))  { 
			// check string starts with p
			if (pnum.startsWith("P") || pnum.startsWith("p"))  { 
				// get the pNum excluding 'P'
				String numbers = pnum.substring(1, pnum.length());
				// return true if all of the characters after 'p' are numbers.
				if (numbers.chars().allMatch(c -> Character.isDigit(c)))  {
					return "";
				}
			}
		}
		// return result
		return valid;
	}
	
	private String emailIsValid(String email, String pnum)  {
		String msg = "Invalid email.\n";
		// email should be in this format...
		if (email.equals(pnum.toLowerCase() + "@my365.dmu.ac.uk"))  {
			return "";
		}
		return msg;
	}
	
	private String nameIsValid(Name nm)  {
		String msg = "Please enter your name.\n";
		// check that neither of the name fields are empty.
		if (!nm.getFirstName().equals("") && !nm.getFamilyName().equals(""))  {
			return "";
		}
 		return msg;
	}
	
	private String dateIsValid(LocalDate dt)  {
		String msg = "Invalid date.\n";
		try  {
			// if date is not null
			if (!dt.equals(null)) {
				// return empty string
				return "";
			}
			return msg;
		}
		// null pointer will throw if date is null
		catch (NullPointerException ne)  {
			return "Date not selected.\n";
		}
	}
	
	/**
	 * Will restore the CreateStudentProfilePane from loaded binary file.
	 * 
	 * @param sp
	 */
	private void restoreCreateStudentProfile(StudentProfile sp)  {
		// store student name
		Name name = sp.getStudentName();
		// set interface values
		cspp.setSelectedCourse(sp.getStudentCourse());
		cspp.getTxtPnumber().setText(sp.getStudentPnumber());
		cspp.getTxtFirstName().setText(name.getFirstName());
		cspp.getTxtSurname().setText(name.getFamilyName());
		cspp.getTxtEmail().setText(sp.getStudentEmail());
		cspp.getInputDate().setValue(sp.getSubmissionDate());
		
		// invoke save profile programatically
		view.getCreateStudentProfilePane().getBtnCreateProfile().fire();
	}
	
	/**
	 * Restore selected modules from model, back to the selected ListViews in the
	 * view.
	 */
	private void restoreSelectModulesPane()  {
		resetSMPview();
		//may need to do mandatory ones.
		for (Module m : model.getAllSelectedModules())  {
			// all mandatory/year long modules have been processed at this point
			if (!m.isMandatory() && m.getDelivery() == Schedule.TERM_1)  {
				// add
				smp.getSelectedTerm1().addItemToListView(m);
				smp.getUnselectedTerm1().removeItemFromListView(m);
				System.out.println(m.toString());
			}
			else if (!m.isMandatory() && m.getDelivery() == Schedule.TERM_2)  {
				smp.getSelectedTerm2().addItemToListView(m);
				smp.getUnselectedTerm2().removeItemFromListView(m);
				System.out.println(m.toString());
			}
		}
		// set credits to total 120
		smp.getCreditsTerm1().setCredits(60);
		smp.getCreditsTerm2().setCredits(60);
		// programmatically invoke submit
		// reset the model's selected
		smp.getBtnSubmit().fire();
	}
	
	private void restoreReserveModulesPane() {
		resetRMPview();
		// should total 4, totals 5 if user loads profile > swaps module > saves > loads again
		for (Module m : model.getAllReservedModules())  {
			// all mandatory/year long modules have been processed at this
			// point
			if (!m.isMandatory() && m.getDelivery() == Schedule.TERM_1)  {
				// add
				rmp.getTerm1tp().getReserved().addItemToListView(m);
				rmp.getTerm1tp().getUnselected().removeItemFromListView(m);
				System.out.println(m.toString());
			}
			else if (!m.isMandatory() && m.getDelivery() == Schedule.TERM_2)  {
				rmp.getTerm2tp().getReserved().addItemToListView(m);
				rmp.getTerm2tp().getUnselected().removeItemFromListView(m);
				System.out.println(m.toString());
			}
		}
		// reset model reserved modules
		// programmatically invoke confirm
		rmp.getTerm2tp().getBtnConfirm().fire();
	}

	/**
	 * returns a tree set consisting of any items that are in the 'selected' boxes
	 * in the SelectModulePane tab.
	 * 
	 * @return TreeSet of Modules to be stored in the model.
	 */
	private TreeSet<Module> getAllSelectedModules()  {
		// get all selected ListView items
		Collection<Module> yearLong = view.getSelectModulesPane().getSelectedYearLong().getListView().getItems();
		Collection<Module> term1 = view.getSelectModulesPane().getSelectedTerm1().getListView().getItems();
		Collection<Module> term2 = view.getSelectModulesPane().getSelectedTerm2().getListView().getItems();
		// stream all 3 collections
		Stream<Module> selected = Stream.concat(Stream.concat(yearLong.stream(), term1.stream()), term2.stream());
		// make a new tree set to add the modules in collections to
		TreeSet<Module> selectedModules = new TreeSet<>();
		// add modules to tree set
		selected.forEach(m -> selectedModules.add(m));
		// return tree set
		return selectedModules;
	}
	
	/**
	 * stream a collection of modules and return the sum of all of the module credits
	 * @param modules
	 * @return a sum of the credits from the collection passed to this function
	 */
	private int getCreditsFromModuleList(Collection<Module> modules)  {
		return modules.stream().mapToInt(m -> m.getModuleCredits()).sum();
	}
	
	/**
	 * Gets an ArrayList of all of the modules that are left unselected by the user
	 * based on the modules' schedule.
	 * @param sch 
	 * @return
	 */
	private ArrayList<Module> getAllUnselectedModules(Schedule sch)  {
		// make a new array list to add the modules in the collections to
		ArrayList<Module> unselectedModules = new ArrayList<>();
		// make the collections (unselected term1 and term2 respectively)
		Collection<Module> list;
		// if we want term 1 modules
		if (sch == Schedule.TERM_1)  {
			// store unselected items from term 1 box in Collection
			list = view.getSelectModulesPane().getUnselectedTerm1().getListView().getItems();
			// stream the collection list and add 
			list.stream().forEach(m -> unselectedModules.add(m));
		}
		// if we want term 2 modules
		else if (sch == Schedule.TERM_2)  {
			// store unselected items from term 2 box in Collection
			list = view.getSelectModulesPane().getUnselectedTerm2().getListView().getItems();
			list.stream().forEach(m -> unselectedModules.add(m));
		}
		
		// year long module is mandatory and therefore never 'unselected'.
		// return 1 ArrayList populated with unselected modules with the given 
		return unselectedModules;
	}
	
	// saved as function as this functionality is used to initialise SMP after creating new profile.
	// and when a profile is reloaded
	private void resetSMPview()  {
		Course selectedCourse = model.getStudentCourse();
		// create a Collection to store both term1 and term2 unselected
		Collection<Module> unselectedTerm1 = selectedCourse.getFilteredModulesOnCourse(Schedule.TERM_1, false);
		Collection<Module> unselectedTerm2 = selectedCourse.getFilteredModulesOnCourse(Schedule.TERM_2, false);
		// create observable lists and pass the collections to it
		ObservableList<Module> t1 = FXCollections.observableArrayList(unselectedTerm1);
		ObservableList<Module> t2 = FXCollections.observableArrayList(unselectedTerm2);
		Collection<Module> mandatoryYL = selectedCourse.getFilteredModulesOnCourse(Schedule.YEAR_LONG, true);
		Collection<Module> mandatoryT1 = selectedCourse.getFilteredModulesOnCourse(Schedule.TERM_1, true);
		Collection<Module> mandatoryT2 = selectedCourse.getFilteredModulesOnCourse(Schedule.TERM_2, true);
		ObservableList<Module> mandyl = FXCollections.observableArrayList(mandatoryYL);
		ObservableList<Module> mandt1 = FXCollections.observableArrayList(mandatoryT1);
		ObservableList<Module> mandt2 = FXCollections.observableArrayList(mandatoryT2);
		// set modules lists from the created collections/observableLists
		view.getSelectModulesPane().getUnselectedTerm1().setListView(t1); // unselectedTerm1
		view.getSelectModulesPane().getUnselectedTerm2().setListView(t2);  // unselectedTerm2
		view.getSelectModulesPane().getSelectedYearLong().setListView(mandyl); // selectedYearLong
		view.getSelectModulesPane().getSelectedTerm1().setListView(mandt1);  // selectedTerm1
		view.getSelectModulesPane().getSelectedTerm2().setListView(mandt2);  // selectedTerm2
		// set credits text boxes using data from model
		// make collections of the modules in each 'selected' box.
		Collection<Module> selectedYear = view.getSelectModulesPane().getSelectedYearLong().getListView().getItems();
		Collection<Module> selectedT1 = view.getSelectModulesPane().getSelectedTerm1().getListView().getItems();
		Collection<Module> selectedT2 = view.getSelectModulesPane().getSelectedTerm2().getListView().getItems();
		
		// store the amount of credits any year long modules contribute. (per term = divided by 2);
		int yearlongCreditsPerTerm = getCreditsFromModuleList(selectedYear) / 2;
		// store term1 and term2 credits
		// then take away what the year long credits contribute
		int term1Credits = getCreditsFromModuleList(selectedT1) + yearlongCreditsPerTerm;
		int term2Credits = getCreditsFromModuleList(selectedT2) + yearlongCreditsPerTerm;
		//set...
		view.getSelectModulesPane().getCreditsTerm1().setCredits(term1Credits);
		view.getSelectModulesPane().getCreditsTerm2().setCredits(term2Credits);
	}
	
	// saved as function as this functionality is used to initialise RMP after creating new profile.
	// and when a profile is reloaded
	private void resetRMPview() {
		// get all selected modules from SelectModulePane
		TreeSet<Module> selected = getAllSelectedModules();
		// gets the TreeSet of selected modules from model
		// and then updates it by adding the TreeSet<Module> selected, to it.
		model.getAllSelectedModules().addAll(selected);
		// change tab to ReserveModulesPane
		view.changeTab(2);
		// get all the UNselected modules (Both from term 1 and term 2)
		ArrayList<Module> unselectedModulesT1 = getAllUnselectedModules(Schedule.TERM_1);
		ArrayList<Module> unselectedModulesT2 = getAllUnselectedModules(Schedule.TERM_2);
		// make an observable list from both ArrayLists
		ObservableList<Module> ol1 = FXCollections.observableArrayList(unselectedModulesT1);
		ObservableList<Module> ol2 = FXCollections.observableArrayList(unselectedModulesT2);
		// use the observable lists to update the list view in the ReserveModulesPane
		rmp.getTerm1tp().getUnselected().setListView(ol1);
		rmp.getTerm2tp().getUnselected().setListView(ol2);
		
		// print to console for debugging purposes...
		System.out.println("Submit Clicked: " + model.getAllSelectedModules().toString());
	}
	
	/**
	 * Move an unselected module to the 'reserved' list in the 
	 * SelectModulesPane
	 * @param smp
	 * @param clicked
	 */
	private void moveUnselectedToSelected(SelectModulesPane smp, ControlButton clicked)  {
		// make the alert (just in case :))
		Alert alert = new Alert(AlertType.ERROR, "Please select an item.");
		alert.setTitle("Error");
		alert.setResizable(false);
		
		// get the list view panes in smp
		ListViewPane<Module> smpUnselectedT1 = smp.getUnselectedTerm1();
		ListViewPane<Module> smpUnselectedT2 = smp.getUnselectedTerm2();
		ListViewPane<Module> smpSelectedT1 = smp.getSelectedTerm1();
		ListViewPane<Module> smpSelectedT2 = smp.getSelectedTerm2();
		// year long too, although there is no need for now.
		// store buttons to compare with the source which 'add' button was clicked.
		ControlButton term1BtnAdd = smp.getTerm1Btns().getBtnAdd();
		ControlButton term2BtnAdd = smp.getTerm2Btns().getBtnAdd();
		Module moduleToMove;
		// if term 1's add button was clicked
		if (term1BtnAdd.equals(clicked))  {
			//find selected module
			moduleToMove = smpUnselectedT1.getListView().getSelectionModel().getSelectedItem();
			// try update the credits
			try  {
				smp.increaseTerm1CreditsBy(moduleToMove.getModuleCredits());
				// if result will be less than 0, throw exception.
				if (smp.getCreditsTerm1().getCredits() > 60)  { 
					throw new IllegalStateException("Cannot be more than 60"); 
				}
				// add selected to other list
				smpSelectedT1.addItemToListView(moduleToMove);
				// remove the selected module from first list
				smpUnselectedT1.removeItemFromListView(moduleToMove);	
			} 
			catch (IllegalStateException max)  {
				System.out.println("Term 1 credits are at max");
				smp.getCreditsTerm1().setCredits(60); // reset credits to max value.
			}
			// will throw if the user clicks 'add' before selecting an item.
			catch (NullPointerException ne)  {
				System.out.println("No items selected...");
				// push alert
				alert.show();
			}
		}
		// if term 2's add button was clicked
		else if (term2BtnAdd.equals(clicked))  {
			//find selected module
			moduleToMove = smpUnselectedT2.getListView().getSelectionModel().getSelectedItem();
			// try update the credits
			try  {
				smp.increaseTerm2CreditsBy(moduleToMove.getModuleCredits());
				// if result will be more than 60, throw exception.
				if (smp.getCreditsTerm2().getCredits() > 60)  { 
					throw new IllegalStateException("Cannot be more than 60");
				}
				// add selected to other list
				smpSelectedT2.addItemToListView(moduleToMove);
				// remove the selected module from first list
				smpUnselectedT2.removeItemFromListView(moduleToMove);	
			} //catch exception
			catch (IllegalStateException max)  {
				System.out.println("Term 2 credits are at max"); 
				smp.getCreditsTerm2().setCredits(60); // set credits to max value.
			}
			// will throw if the user clicks 'add' before selecting an item.
			catch (NullPointerException ne)  {
				System.out.println("No items selected...");
				// push alert
				alert.show();
			}
		}
	}
	
	/*
	 * used when Remove button is pressed
	 */
	private void moveSelectedToUnselected(SelectModulesPane smp, ControlButton clicked)  {
		// get the list view panes in smp
		ListViewPane<Module> smpUnselectedT1 = smp.getUnselectedTerm1();
		ListViewPane<Module> smpUnselectedT2 = smp.getUnselectedTerm2();
		ListViewPane<Module> smpSelectedT1 = smp.getSelectedTerm1();
		ListViewPane<Module> smpSelectedT2 = smp.getSelectedTerm2();
		
		// store buttons to compare with the source which 'remove' button was clicked.
		ControlButton term1BtnRemove = smp.getTerm1Btns().getBtnRemove();
		ControlButton term2BtnRemove = smp.getTerm2Btns().getBtnRemove();
		Module moduleToMove;
		// will throw nullpointer if no item is selected.
		try {
			if (term1BtnRemove.equals(clicked))  {
			
				//find selected module
				moduleToMove = smpSelectedT1.getListView().getSelectionModel().getSelectedItem();
				// check if the option selected is a mandatory module
				boolean validSelection = !moduleToMove.isMandatory();
				if (validSelection)  {
					// update the credits
					smp.deductTerm1CreditsBy(moduleToMove.getModuleCredits());
					// add selected to other list
					smpUnselectedT1.addItemToListView(moduleToMove);
					// remove the selected module from first list
					smpSelectedT1.removeItemFromListView(moduleToMove);	
				}	
			} 
			else if (term2BtnRemove.equals(clicked))  {
				//find selected module
				moduleToMove = smpSelectedT2.getListView().getSelectionModel().getSelectedItem();
				// check if the option selected is a mandatory module
				boolean validSelection = !moduleToMove.isMandatory();
				if (validSelection)  {
					// update the credits
					smp.deductTerm2CreditsBy(moduleToMove.getModuleCredits());
					// add selected to other list
					smpUnselectedT2.addItemToListView(moduleToMove);
					// remove the selected module from first list
					smpSelectedT2.removeItemFromListView(moduleToMove);
				}
			}
		} 
		catch (NullPointerException ne)  {
			System.out.println("No item selected.");
			// push alert
			Alert alert = new Alert(AlertType.ERROR, "Please select an item.");
			alert.setTitle("Error");
			alert.setResizable(false);
			alert.show();
		}
	}
	
	/**
	 * Moves a selected item from the 'unselected' list view, to the 'reserved'
	 * list view in the ReserveModulesPane
	 * @param rmp ReserveModulesPane
	 * @param clicked the source of the click event.
	 */
	private void moveSelectedToUnselected(ReserveModulesPane rmp, ControlButton clicked)   {
		// make the alert
		Alert alert = new Alert(AlertType.ERROR, "Please select a reserved item.");
		alert.setTitle("Error");
		alert.setResizable(false);
		
		// get interface controls
		ReserveModulesTitledPane activeTp = (ReserveModulesTitledPane) rmp.getAccordion().getExpandedPane();
		ListViewPane<Module> unselected = activeTp.getUnselected();
		ListViewPane<Module> reserved = activeTp.getReserved();
		
		//get selected module from reserved
		Module moduleToMove = reserved.getListView().getSelectionModel().getSelectedItem();
		try {
			if (!moduleToMove.equals(null))  {
				reserved.removeItemFromListView(moduleToMove);
				unselected.addItemToListView(moduleToMove);
			}
			else {
				System.out.println("No item selected");
			}
		}
		catch (NullPointerException ne)  {
			System.out.println("No item selected, null pointer thrown.\n");
			// push alert
			alert.show();
		}
	}

	/**
	 * Move an unselected item from the unselected list view, to the reserved listview
	 * in the reserve modules pane.
	 * @param rmp
	 * @param clicked
	 */
	private void moveUnselectedToSelected(ReserveModulesPane rmp, ControlButton clicked)  {
		// get interface controls
		ReserveModulesTitledPane activeTp = (ReserveModulesTitledPane) rmp.getAccordion().getExpandedPane();
		ListViewPane<Module> unselected = activeTp.getUnselected();
		ListViewPane<Module> reserved = activeTp.getReserved();
		// get selected module from reserved
		Module moduleToMove = unselected.getListView().getSelectionModel().getSelectedItem();
		// get number of items currently in the 'reserved' list
		int numItems = activeTp.getReserved().getObservableListCount();
		// only allow the swap if there are fewer than 2 items in the 
		// destination list
		if (numItems < 2)  {
			unselected.removeItemFromListView(moduleToMove);
			reserved.addItemToListView(moduleToMove);
		}
		else {
			System.out.println("Reserved list is full.");
			Alert alert = new Alert(AlertType.ERROR, "Reserve list is full.");
			alert.setTitle("Error");
			alert.setResizable(false);
			alert.show();
		}
	}
	
	public StudentProfile getModel()  {
		return model;
	}
	
	/**
	 * set the fields saved in the model.
	 * @param c Course
	 * @param pNum P number
	 * @param n Name
	 * @param email Email
	 * @param dt DateOfSubmission
	 */
	public void setModel(Course c, String pNum, Name n, String email, LocalDate dt)  {
		model.setStudentCourse(c);
		model.setStudentPnumber(pNum);
		model.setStudentName(n);
		model.setStudentEmail(email);
		model.setSubmissionDate(dt);
	}
}
