package de.bananaco.bpermissions.api.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import de.bananaco.bpermissions.api.Group;

/**
 * This class contains the main calculations for
 * a GroupCarrier/PermissionCarrier.
 * 
 * It calculates inherited permissions all the way down the line of the object.
 * This does not include checking for infinite loops, you can break this if you want to.
 */
public abstract class Calculable extends CalculableMeta {

	Set<Permission> effectivePermissions;
	String name;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Calculable(String name, Set<String> groups,
			Set<Permission> permissions, String world) {
		super(groups, permissions, world);
		this.name = name;
		this.effectivePermissions = new HashSet();
	}
	
	/**
	 * Debugging code: used to print the total effective Permissions of an object at a specified point in time
	 */
	protected void print() {
		String[] perms = new String[effectivePermissions.size()];
		int i=0;
		for(Permission perm : effectivePermissions) {
			if(perm == null)
				System.err.println("PERM IS NULL?");
			else if(perm.isTrue())
				perms[i] = perm.name();
			else
				perms[i] = "^"+perm.name();
			i++;
		}
		System.out.println(getName()+": "+Arrays.toString(perms));
	}

	/**
	 * Used to calculate the total permissions gained by the object
	 * @throws RecursiveGroupException 
	 */
	public void calculateEffectivePermissions() throws RecursiveGroupException {
		try {
		effectivePermissions.clear();
		for (Group group : getGroups()) {
			group.calculateEffectivePermissions();
			for(Permission perm : group.getEffectivePermissions()) {
				if(effectivePermissions.contains(perm))
					effectivePermissions.remove(perm);
				effectivePermissions.add(perm);
			}
		}
		for(Permission perm : this.getPermissions()) {
			if(effectivePermissions.contains(perm))
				effectivePermissions.remove(perm);
			effectivePermissions.add(perm);
		}
		//print();
		} catch (StackOverflowError e) {
			throw new RecursiveGroupException(this);
		}
	}

	/**
	 * Return the total permissions gained by the object
	 * 
	 * @return Set<Permission>
	 */
	public Set<Permission> getEffectivePermissions() {
		return effectivePermissions;
	}

	/**
	 * Returns the name of the calculable object
	 * 
	 * @return String
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Returns the lowercased name of the calculable object
	 * 
	 * @return String
	 */
	public String getNameLowerCase() {
		return name.toLowerCase();
	}
	
	@Override
	public int hashCode() {
		return getNameLowerCase().hashCode();
	}
	
	/**
	 * Another way of checking the type
	 * besides instanceof
	 * @return CalculableType
	 */
	public abstract CalculableType getType();

}
