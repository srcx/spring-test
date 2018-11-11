package cz.srnet.spring.test;

import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public final class DatabaseNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public DatabaseNotFoundException(@Nullable String name) {
		super("Database could not be found: " + name);
	}

}
