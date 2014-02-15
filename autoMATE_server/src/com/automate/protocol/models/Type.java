package com.automate.protocol.models;

public enum Type {
	STRING,
	INTEGER,
	REAL,
	BOOLEAN,
	PERCENT,;

	/* (non-Javadoc)
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		return super.toString().toLowerCase();
	}
	
	/**
	 * returns the enum for the given string value.
	 * @param type the sting to parse
	 * @return a type that corresponds to the string type
	 */
	public static Type parseType(String type) throws TypeFormatException {
		if(type.equalsIgnoreCase("string")) {
			return STRING;
		} else if(type.equalsIgnoreCase("integer")) {
			return INTEGER;
		} else if(type.equalsIgnoreCase("real")) {
			return REAL;
		} else if(type.equalsIgnoreCase("boolean")) {
			return BOOLEAN;
		} else if(type.equalsIgnoreCase("percent")) {
			return PERCENT;
		} else {
			throw new TypeFormatException(type + " does not represent one of STRING, INTEGER, REAL, BOOLEAN, or PERCENT.");
		}
	}
	
	public static class TypeFormatException extends Exception {

		/**
		 * 
		 */
		private static final long serialVersionUID = 6733363603502697985L;

		public TypeFormatException(String message) {
			super(message);
		}
		
	}
	
}