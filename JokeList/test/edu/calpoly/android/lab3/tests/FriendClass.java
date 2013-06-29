package edu.calpoly.android.lab3.tests;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

public  class FriendClass {
	/**************************************/
	/**	Java Friend-Class Helper Methods **/
	/**************************************/
	
	/**
	 * This method returns the version code specified the applications manifest
	 * corresponding to the context passed in.
	 * 
	 * @param context	The context from which to retrieve the version code.
	 * @return			The version of the application or 1 if the retrieval of
	 * 					the version was unsuccessful.
	 */
	public static int getAppVersion(Context context) {
		int version = 1;
		try {
			version = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e) {} 
		
		return version;
	}

	/**
	 * This method returns true or false depending on whether the application that
	 * owns the given context has been granted the given permission.
	 * 
	 * @param context	The context on which the permission check is to be performed.
	 * @param permName	The name of the permission to check for.
	 * @return			True if the application the context belongs to has been 
	 * 					granted the permission; false otherwise.
	 */
	public static boolean hasPermission(Context context, String permName) {
		return context.getPackageManager().checkPermission(permName, context.getPackageName()) == PackageManager.PERMISSION_GRANTED;
	}
	
	/**
	 * This method is used to retrieve a hidden (Protected/Private) member
	 * variable. The return value and type are those of the the member variable. 
	 * 
	 * @param <T> 
	 * @param memberName	The name of the member variable to retrieve.
	 * @param type			The type of the member variable to retrieve.
	 * @param sourceObj		The object from which to retrieve the member variable.
	 * @return				The value contained in the member variable.
	 * 
	 * @throws WrongFieldNameException	If no member variable can be found with
	 * 									the name supplied by 'memberName'.
	 * 
	 * @throws WrongFieldTypeException	If the member variable identified by 
	 * 									'memberName' cannot be cast to the type
	 * 									indicated by the 'type' argument.
	 * 
	 * @throws ImproperUseException		Most likely thrown if there is a bug in
	 * 									the method.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T retrieveHiddenMember(String memberName, T type, Object sourceObj) throws WrongFieldNameException, WrongFieldTypeException, ImproperUseException{
		Field field = null;
		T returnVal = null;
		//Test for proper existence
		try {
			field = sourceObj.getClass().getDeclaredField(memberName);
		} catch (NoSuchFieldException e) {
			throw new WrongFieldNameException(memberName);
		}
		field.setAccessible(true);
		
		//Test for proper type
		try {
			returnVal = (T)field.get(sourceObj);
		} catch (ClassCastException exc) {
			throw new WrongFieldTypeException(memberName);
		}  
		
		// Boiler Plate Exception Checking. If any of these Exceptions are 
		// thrown it was becuase this method was called improperly.
		catch (IllegalArgumentException e) {
			throw new ImproperUseException("IllegalArgumentException:\n Passed in the wrong object to Field.get(...)");
		} catch (IllegalAccessException e) {
			throw new ImproperUseException("IllegalAccessException:\n Field.setAccessible(true) should be called.");
		}
		return returnVal; 
	}
	
	/**
	 * This method is used to invoke a hidden (Protected/Private) method. The 
	 * return value and type is whatever the method returns. Note that if the 
	 * methods return type is void then null will be returned and null should 
	 * also be passed in as the return type argument.If the method does not 
	 * accept arguments then pass in "null".
	 * 
	 * @param <T> 
	 * @param methodName	The name of the method to invoke.
	 * @param source		The object on which to invoke the method call.
	 * @param returnType	Used to identify the return type of the method you 
	 * 						are invoking. If the return type is void then pass in 
	 * 						"null".
	 * @param args			List of Arguments passed to the invocation call. If
	 * 						the method does not accept arguments then pass in "null"
	 * @return				The value returned by the method invocation. 
	 * 
	 * @throws WrongMethodNameException	If no member variable can be found with
	 * 									the name supplied by 'memberName'.
	 * 
	 * @throws WrongReturnTypeException	If the member variable identified by 
	 * 									'memberName' cannot be cast to the type
	 * 									indicated by the 'type' argument.
	 * 
	 * @throws WrongArgumentException	If the 'args' argument does not contain
	 * 									the correct arguments needed to invoke 
	 * 									the method identified by 'methodName'.
	 *  
	 * @throws MethodInvocationException If the method invoked throws an exception.
	 * 									 The message from the original exception 
	 * 									 will be contained in this message.
	 *  
	 * @throws ImproperUseException		Most likely thrown if there is a bug in
	 * 									the method.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T invokeHiddenMethod(String methodName, Object source, T returnType, Object... args) throws WrongMethodNameException, WrongReturnTypeException, WrongArgumentException, MethodInvocationException, ImproperUseException{
		Method method = null;
		T returnVal = null;
		int numArgs = (args == null? 0 : args.length);
		Class[] argTypes = new Class[numArgs];
		for (int ndx = 0; ndx < numArgs; ndx++) {
			argTypes[ndx] = args[ndx].getClass();
		}
		//Test for proper existence
		
		try {
			method = source.getClass().getDeclaredMethod(methodName, argTypes);
		} catch (NoSuchMethodException e) {
			throw new WrongMethodNameException(methodName);
		}
		method.setAccessible(true);
		
		//Test for proper type
		try {
			//Special check for Void because invoke will return null for a void 
			//which can be validly cast to anything.
			
			//If we're checking the type anyways why not just check the type for
			//everything what? B/C either way we have to cast it to (T) so why
			//repeat ourselves, just let the ClassCastException pop the catch.
			if ((returnType == null && !method.getReturnType().equals(Void.TYPE)) ||
				(returnType != null && method.getReturnType().equals(Void.TYPE))) {
				throw new WrongReturnTypeException(methodName);
			}
				
			returnVal = (T)method.invoke(source, args);
		} catch (IllegalArgumentException exc) {
			throw new WrongArgumentException(methodName);
		} catch (IllegalAccessException exc) {
			throw new ImproperUseException("IllegalAccessException:\n Method.setAccessible(true) should be called.");
		} catch (InvocationTargetException exc) {
			throw new MethodInvocationException(methodName, exc.getMessage());
		} catch (ClassCastException exc) {
			throw new WrongReturnTypeException(methodName);
		} 
		return returnVal;
	}
	
	public static class FriendClassException extends Exception {
		private static final long serialVersionUID = 1L;
		public FriendClassException(String message) {super(message);}
	}
	public static class WrongFieldNameException extends FriendClassException {
		private static final long serialVersionUID = 1L;
		public WrongFieldNameException(String memberName) {
			super("The field \"" + memberName + "\" was renamed or removed. Do not rename or remove this member variable.");
		}
	}
	public static class WrongFieldTypeException extends FriendClassException {
		private static final long serialVersionUID = 1L;
		public WrongFieldTypeException(String memberName){
			super("The field \"" + memberName + "\" had its type changed. Do not change the type on this member variable.");
		}
	}
	public static class WrongMethodNameException extends FriendClassException {
		private static final long serialVersionUID = 1L;
		public WrongMethodNameException(String methodName) {
			super("The method \"" + methodName + "\" was renamed or removed. Do not rename or remove this method.");
		}
	}
	public static class WrongReturnTypeException extends FriendClassException {
		private static final long serialVersionUID = 1L;
		public WrongReturnTypeException(String methodName){
			super("The method \"" + methodName + "\" had its return type changed. Do not change the type for this method.");
		}
	}
	public static class WrongArgumentException extends FriendClassException {
		private static final long serialVersionUID = 1L;
		public WrongArgumentException(String methodName){
			super("The method \"" + methodName + "\" had its argument list changed. Do not change the types, order, or number of arguments for this method.");
		}
	}
	public static class MethodInvocationException extends FriendClassException {
		private static final long serialVersionUID = 1L;
		public MethodInvocationException(String methodName, String message){
			super("The method \"" + methodName + "\" threw an exception:\n" + message);
		}
	}
	public static class ImproperUseException extends FriendClassException {
		private static final long serialVersionUID = 1L;
		public ImproperUseException(String details) {
			super("This is an error caused by the Unit Test not your application, please notify the Professor. Improper user of FriendClass method -- " + details);
		}
	}
}