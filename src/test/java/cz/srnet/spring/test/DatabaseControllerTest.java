package cz.srnet.spring.test;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public final class DatabaseControllerTest {

	private final String PATH = "/databases";
	private final String TEST_DB_NAME = "test";
	private final String PATH_TO_TEST_DB = PATH + "/" + TEST_DB_NAME;

	@Autowired
	private MockMvc mvc;
	private Database testDb = new Database(TEST_DB_NAME, "localhost", 3306, "testDb", "testUser", "testPass");
	private Database testDb2 = new Database("test2", "localhost2", 9999, "testDb2", "testUser2", "testPass2");

	@Before
	public void setUp() throws Exception {
		assertListCall(0);
	}

	@Test
	public void list() throws Exception {
		createTestDatabase();
		assertListCall(1).andExpect(jsonPath("$[0].name", is(testDb.getName())))
				.andExpect(jsonPath("$[0].hostname", is(testDb.getHostname())))
				.andExpect(jsonPath("$[0].port", is(testDb.getPort())))
				.andExpect(jsonPath("$[0].databaseName", is(testDb.getDatabaseName())))
				.andExpect(jsonPath("$[0].username", is(testDb.getUsername())))
				.andExpect(jsonPath("$[0].password", is(testDb.getPassword())));
		createDatabase(testDb2);
		assertListCall(2).andExpect(jsonPath("$[0].name", is(testDb.getName())))
				.andExpect(jsonPath("$[0].hostname", is(testDb.getHostname())))
				.andExpect(jsonPath("$[0].port", is(testDb.getPort())))
				.andExpect(jsonPath("$[0].databaseName", is(testDb.getDatabaseName())))
				.andExpect(jsonPath("$[0].username", is(testDb.getUsername())))
				.andExpect(jsonPath("$[0].password", is(testDb.getPassword())))
				.andExpect(jsonPath("$[1].name", is(testDb2.getName())))
				.andExpect(jsonPath("$[1].hostname", is(testDb2.getHostname())))
				.andExpect(jsonPath("$[1].port", is(testDb2.getPort())))
				.andExpect(jsonPath("$[1].databaseName", is(testDb2.getDatabaseName())))
				.andExpect(jsonPath("$[1].username", is(testDb2.getUsername())))
				.andExpect(jsonPath("$[1].password", is(testDb2.getPassword())));
	}

	private ResultActions assertListCall(int expectedSize) throws Exception {
		return mvc.perform(get(PATH).contentType(APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(expectedSize)));
	}

	private ResultActions createTestDatabase() throws Exception {
		return createDatabase(testDb);
	}

	private ResultActions createDatabase(Database db) throws JsonProcessingException, Exception {
		String json = new ObjectMapper().writeValueAsString(db);
		return mvc.perform(post(PATH).contentType(APPLICATION_JSON).content(json)).andExpect(status().isOk());
	}

	private ResultActions assertTestDatabaseJson(ResultActions actions) throws Exception {
		return actions.andExpect(jsonPath("name", is(testDb.getName())))
				.andExpect(jsonPath("hostname", is(testDb.getHostname())))
				.andExpect(jsonPath("port", is(testDb.getPort())))
				.andExpect(jsonPath("databaseName", is(testDb.getDatabaseName())))
				.andExpect(jsonPath("username", is(testDb.getUsername())))
				.andExpect(jsonPath("password", is(testDb.getPassword())));
	}

	@Test
	public void create() throws Exception {
		assertTestDatabaseJson(createTestDatabase());
	}

	@Test
	public void getByName() throws Exception {
		createTestDatabase();
		assertTestDatabaseJson(
				mvc.perform(get(PATH_TO_TEST_DB).contentType(APPLICATION_JSON)).andExpect(status().isOk()));
	}

	@Test
	public void update() throws Exception {
		createTestDatabase();
		assertTestDatabaseJson(
				mvc.perform(get(PATH_TO_TEST_DB).contentType(APPLICATION_JSON)).andExpect(status().isOk()));
		testDb.setPort(9999);
		createTestDatabase();
		assertTestDatabaseJson(
				mvc.perform(get(PATH_TO_TEST_DB).contentType(APPLICATION_JSON)).andExpect(status().isOk()));
	}

	@Test
	public void delete() throws Exception {
		createTestDatabase();
		assertListCall(1);
		mvc.perform(MockMvcRequestBuilders.delete(PATH_TO_TEST_DB).contentType(APPLICATION_JSON))
				.andExpect(status().isOk());
		assertListCall(0);
	}

}
