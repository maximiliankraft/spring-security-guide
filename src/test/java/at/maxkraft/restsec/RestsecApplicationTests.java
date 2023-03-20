package at.maxkraft.restsec;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class RestsecApplicationTests {

	static Gson gson = new GsonBuilder()
			.setPrettyPrinting()
			.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeJsonAdapter())
			.create();

	@Autowired
	MockMvc mockMvc;

	@Test
	void contextLoads() {
	}

	@Test
	void registerAndLoginNewUser() throws Exception {
		mockMvc.perform(get("/user/register/test/abc"))
				.andExpect(result -> {
					assert result.getResponse().getStatus() == 200;
				});

	}


	@Test
	void createNewDocument() throws Exception {

		// create new user
		mockMvc.perform(get("/user/register/hans/abc"))
				.andExpect(result -> {
					assert result.getResponse().getStatus() == 200;
				});

		// create document
		/*
		POST http://localhost/docs
		Authorization: Basic hans abc
		Content-Type: application/json
		Host: localhost

		{"id": null, "text": "Formular XY", "creationDate": "2020-01-01", "permissions": []}
		**/

		var jsonString = gson.toJson(new DocumentEntity(null, "Formular XY", LocalDateTime.now(), List.of()));

		var docCreationResult = mockMvc.perform(post("/docs/")
				.with(user("hans").password("abc"))
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonString)
		).andExpect(status().isOk()).andReturn();

		var returnedDocEntity = gson.fromJson(docCreationResult.getResponse().getContentAsString(), DocumentEntity.class);

		// fetch document by id
		var fetchedDocResult = mockMvc.perform(get("/docs/" + returnedDocEntity.getId())
				.with(user("hans").password("abc")))
				.andExpect(status().isOk()).andReturn();

		var fetchDocEntity = gson.fromJson(fetchedDocResult.getResponse().getContentAsString(), DocumentEntity.class);

		assert fetchDocEntity.getId().equals(returnedDocEntity.getId());
	}

	// todo grant document to another user




}
