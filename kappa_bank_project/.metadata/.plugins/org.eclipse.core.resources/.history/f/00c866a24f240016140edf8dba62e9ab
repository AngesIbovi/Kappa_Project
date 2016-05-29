package model.query.response;

import serialization.JsonImpl;

/**
 * Communication class. See the protocol's documentation for more details.
 * @version R3 sprint 1 - 13/04/2016
 * @author Kappa-V
 * @changes
 * 		R1 sprint 4 -> R3 sprint 1 : </br>
 * 			-changed the toString method from abstract to real. Gson's toJson method permitted this code factorization.
 */
public abstract class ServerResponse {
	@Override
	public String toString(){
		return "OK " + JsonImpl.toJson(this);
	}
}
