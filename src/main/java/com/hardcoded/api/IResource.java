package com.hardcoded.api;

/**
 * A resource class for loading and unload resources.
 * 
 * @author HardCoded
 */
public abstract class IResource {
	/**
	 * Initialize data inside this object.
	 * 
	 * This method must be called on the {@code GL} thread.
	 * 
	 * @throws	ResourceException
	 * 			If any resource failed that will hinder the execution of code
	 */
	protected void init() throws ResourceException {
		
	}
	
	/**
	 * Unload all data contained in this object.
	 * This does not mean that data should be set to {@code null}.
	 * 
	 * <p>If any non static buffer has been allocated or texture has
	 * been added to memory it should be unloaded and removed.
	 * 
	 * <p>The task of this method is to reduce the amount of memory
	 * used and to unload the objects from memory in a controlled way.
	 * 
	 * @throws	ResourceException
	 * 			If any resource failed that will hinder the execution of code
	 */
	protected void unload() throws ResourceException {
		
	}
	
	/**
	 * Reload all data that this resource has access to.
	 * 
	 * <p>If this object already has loaded all resources no action
	 * should be taken.
	 * 
	 * @throws	ResourceException
	 * 			If any resource failed that will hinder the execution of code
	 */
	protected void reload() throws ResourceException {
		
	}

	/**
	 * Release all data contained in this object.
	 * 
	 * <p>This call should unload everything and the object should not be usable
	 * after this is called.
	 * 
	 * @throws	ResourceException
	 * 			If any resource failed that will hinder the execution of code
	 */
	protected void cleanup() throws ResourceException {
		
	}
}
