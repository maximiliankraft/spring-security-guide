package at.maxkraft.restsec;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class RestsecApplicationTests {

	@Autowired
	MockMvc mockMvc;

	@Test
	void contextLoads() {
	}

	@Test
	void testRegisterNewUser() throws Exception {

		mockMvc.perform(get("/user/register/peter/123"))
				.andExpect(status().is(200));

		mockMvc.perform(get("/user/register/"))
				.andExpect(status().is(404));

		mockMvc.perform(get("/user/register///"))
				.andExpect(status().is(404));

	}



	@Test
	void testLoginNewUser() throws Exception{

		// setup - prepare user
		mockMvc.perform(get("/user/register/peter/123"))
				.andExpect(status().is(200));

		// execute & check
		mockMvc.perform(get("/user/login/peter/123"))
				.andExpect(status().is(200));

		// test if admin is available
		mockMvc.perform(get("/user/login/admin/admin"))
				.andExpect(status().is(200));

	}

	@Test
	void testAddNewResource() throws Exception {
		/*
		PUT http://localhost:8080/test/
		Authorization: Basic admin admin
		Content-Type: application/json

		{"id": null, "title": "Test", "description": "Test 2"}		* */

		mockMvc.perform(put("/test/")
				.header("Authorization", "Basic admin admin")
				.contentType("application/json")
				.content("{\"id\": null, \"title\": \"Test\", \"description\": \"Test 2\"}"))
				.andExpect(status().isOk());

	}

}
