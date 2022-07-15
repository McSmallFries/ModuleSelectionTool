package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


public class Course implements Serializable{
	

	private static final long serialVersionUID = 2971607279473774903L;
	private String courseName;
	private Map<String, Module> modulesOnCourse;
	
	public Course(String courseName) {
		this.courseName = courseName;
		modulesOnCourse = new HashMap<String, Module>();
	}
	
	public String getCourseName() {
		return courseName;
	}
	
	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}
	
	public void addModuleToCourse(Module m) {
		modulesOnCourse.put(m.getModuleCode(), m);
	}
	
	public Module getModuleByCode(String code) {
		return modulesOnCourse.get(code);
	}
	
	public Collection<Module> getAllModulesOnCourse() {
		return modulesOnCourse.values();
	}
	
	public Collection<Module> filterByMandatory(boolean mand)  {
		ArrayList<Module> mandatory = new ArrayList<>();
		modulesOnCourse.values().stream().filter(e -> e.isMandatory() == mand).forEach(n -> mandatory.add(n));
		return mandatory;
	}
	
	public Collection<Module> filterBySchedule(Schedule sch)  {
		ArrayList<Module> schedule = new ArrayList<>();
		modulesOnCourse.values().stream().filter(e -> e.getDelivery().equals(sch)).forEach(n -> schedule.add(n));
		return schedule;
	}
	
	/**
	 * A filtered collection of Module items.
	 * @param sch A schedule enum
	 * @param mandatory true if you want to retrieve the mandatory modules
	 * @return a filtered collection.
	 */
	//TODO remove
	public Collection<Module> getFilteredModulesOnCourse(Schedule sch, boolean mandatory)  {
		Collection<Module> filtered = new ArrayList<>();
		if (mandatory)  {
			Collection<Module> mandatoryModules = filterByMandatory(true);
			mandatoryModules.stream().filter(e -> e.getDelivery().equals(sch)).forEach(n -> filtered.add(n));
		}
		else  {
			Collection<Module> nonMandatoryModules = filterByMandatory(false);
			nonMandatoryModules.stream().filter(e -> e.getDelivery().equals(sch)).forEach(n -> filtered.add(n));
		}
		return filtered;
	}
	
	@Override
	public String toString() {
		//a non-standard toString that simply returns the course name,
		//so as to assist in displaying courses correctly in a ComboBox<Course>
		return courseName;
	}
	
	public String actualToString() {
		return "Course:[courseName=" + courseName + ", modulesOnCourse=" + modulesOnCourse + "]";
	}
	
}
