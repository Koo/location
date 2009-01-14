package com.mamezou.android.example;

import android.location.Address;

public class AddressWrapper {

	private Address address;

	public AddressWrapper(Address address) {
		this.address = address;
	}
	
	public Address getAddress() {
		return address;
	}

	@Override
	public String toString() {
		
		StringBuffer sb = new StringBuffer();
		appendIfNotNull(sb, address.getFeatureName());
		appendIfNotNull(sb, address.getLocality());
		appendIfNotNull(sb, address.getCountryName());
		appendIfNotNull(sb, address.getAdminArea());
		return sb.toString();
	}

	private void appendIfNotNull(StringBuffer sb, String str) {
		if (str != null) {
			if (sb.length() > 0) {
				sb.append(' ');
			}
			sb.append(str);
		}
	}
}
