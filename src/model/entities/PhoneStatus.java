package model.entities;

public enum PhoneStatus {
	OnHook,
	OffHook,
	Unknown;
	
	public static PhoneStatus fromString(String s) {
		if(s!=null) {
			for(PhoneStatus pa : values()) {
				if(pa.toString().equalsIgnoreCase(s)) return pa;
			}
		}
		return Unknown;
	}
}
