package cz.srnet.spring.test;

import org.springframework.http.HttpStatus;
import org.springframework.jdbc.support.MetaDataAccessException;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class DatabaseAccessException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public DatabaseAccessException(Database database, String url, MetaDataAccessException e) {
		super("Error accessing database " + database.getName() + " with URL " + url + ": " + e.getLocalizedMessage(),
				e);
	}

}
