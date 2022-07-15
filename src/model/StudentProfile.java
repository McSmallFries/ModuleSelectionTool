package model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Set;
import java.util.TreeSet;

/**
 * The data model for this program.
 *
 * @author p2508322
 *
 */
public class StudentProfile implements Serializable {
	

	private static final long serialVersionUID = 7030859692178635726L;
	private String studentPnumber;
	private Name studentName;
	private String studentEmail;
	private LocalDate studentDate;
	private Course studentCourse;
	private Set<Module> selectedModules;
	private Set<Module> reservedModules;
	
	public StudentProfile() {
		studentPnumber = "";
		studentName = new Name();
		studentEmail = "";
		studentDate = null;
		studentCourse = null;
		selectedModules = new TreeSet<Module>();
		reservedModules = new TreeSet<Module>();
	}
	
	// used to load a student profile and set the model.
	public void setStudentProfile(StudentProfile sp)  {
		this.studentPnumber = sp.getStudentPnumber();
		this.studentName = sp.getStudentName();
		this.studentEmail = sp.getStudentEmail();
		this.studentDate = sp.getSubmissionDate();
		this.studentCourse = sp.getStudentCourse();
		this.selectedModules = sp.getAllSelectedModules();
		this.reservedModules = sp.getAllReservedModules();
	}
	
	public String getStudentPnumber() {
		return studentPnumber;
	}
	
	public void setStudentPnumber(String studentPnumber) {
		this.studentPnumber = studentPnumber;
	}
	
	public Name getStudentName() {
		return studentName;
	}
	
	public void setStudentName(Name studentName) {
		this.studentName = studentName;
	}
	
	public String getStudentEmail() {
		return studentEmail;
	}
	
	public void setStudentEmail(String studentEmail) {
		this.studentEmail = studentEmail;
	}
	
	public LocalDate getSubmissionDate() {
		return studentDate;
	}
	
	public void setSubmissionDate(LocalDate studentDate) {
		this.studentDate = studentDate;
	}
	
	public Course getStudentCourse() {
		return studentCourse;
	}
	
	public void setStudentCourse(Course studentCourse) {
		this.studentCourse = studentCourse;
	}
	
	public boolean addSelectedModule(Module m) {
		return selectedModules.add(m);
	}
	
	public Set<Module> getAllSelectedModules() {
		return selectedModules;
	}
	
	public void clearSelectedModules() {
		selectedModules.clear();
	}
	
	public boolean addReservedModule(Module m)   {
		return reservedModules.add(m);
	}
	
	public Set<Module> getAllReservedModules() {
		return reservedModules;
	}
	
	public void clearReservedModules() {
		reservedModules.clear();
	}
	
	// the following are used to determine where to take the user back to 
	public boolean hasPnumber()  {
		return !studentPnumber.isEmpty();
	}
	
	public boolean hasSelectedModules()  {
		return !selectedModules.isEmpty();
	}
	
	public boolean hasReservedModules()  {
		return !reservedModules.isEmpty();
	}
	
	// used to read a saved profile from a folder.
	public void readProfileFromFile(String profilePnumber) throws IOException, ClassNotFoundException, FileNotFoundException  {
		// make new student profile object
		StudentProfile sp = new StudentProfile();
		//method throws exceptions, rather than catching them, so caller must handle them.
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream("profiles/" + profilePnumber + "_profile.dat"));
		sp = (StudentProfile) ois.readObject();
		// set the properties using the loaded object
		this.setStudentProfile(sp);
		
		ois.close();
	}
	
	// used to write a profile to a binary data file.
	public void writeProfileToFile()  {
		// use the pnumber to determine file name
		// if pnum doesnt exist, a new file will be created --
		// if pnum exists, the file will be updated to the new data.
		try (ObjectOutputStream oos = new ObjectOutputStream	
		(new FileOutputStream("profiles/" + studentPnumber + "_profile.dat")))  {
			// write object to output stream
			oos.writeObject(this); 
			oos.flush();
		} 
		catch (IOException ioe)  {
			System.out.println("Error writing profile to file");
			ioe.printStackTrace();
		}
	}
	
	@Override
	public String toString() {
		return "StudentProfile:[Pnumber=" + studentPnumber + ", studentName="
				+ studentName + ", studentEmail=" + studentEmail + ", studentDate="
				+ studentDate + ", studentCourse=" + studentCourse.actualToString() 
				+ ", selectedModules=" + selectedModules
				+ ", reservedModules=" + reservedModules + "]";
	}
	
}
