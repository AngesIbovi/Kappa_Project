package model.query;

public interface ClientQuery {
	/**
	 * @return prefix + ' ' + JsonImpl.toJson(this);
	 */
	public String toString();
}
