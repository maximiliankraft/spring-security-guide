package at.maxkraft.restsec;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Objects;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;




//@SpringBootTest
@SpringBootTest
@AutoConfigureMockMvc
class RestsecApplicationTests {

	static Gson gson = new Gson();

	@Autowired
	MockMvc mockMvc;

	// autowire all the things needed in the tests
	@Autowired UserRepository userRepository;
	@Autowired AuthorityRepository authorityRepository;
	@Autowired PermissionRepository permissionRepository;
	@Autowired TestRessourceRepository testRessourceRepository;
	@Autowired PermissionChecker permissionChecker;

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
		//
		var initialResource = new TestRessource(null, "Test", "Test 2");
		var initialJson = gson.toJson(initialResource);
		var username = "peter";
		var password = "123";

		mockMvc.perform(get("/user/register/" + username + "/" + password))
				.andExpect(status().is(200));

		MvcResult creationResult = mockMvc.perform(put("/test/")
						.with(user(username).password(password)) // with user needs spring-security-test package
						.content(initialJson)
						.contentType(MediaType.APPLICATION_JSON)
				).andExpect(status().isOk())
				.andReturn();


		TestRessource resultingResource = gson.fromJson(
				creationResult.getResponse().getContentAsString(),
				TestRessource.class
		);

		assert !Objects.equals(resultingResource.getId(), initialResource.getId());


		// check if TestResource with given id is available
		var queryingResult = mockMvc.perform(get("/test/" + resultingResource.getId())
						.with(user(username).password(password)))
				.andExpect(status().isOk())
				.andReturn();

		TestRessource queryingResource = gson.fromJson(queryingResult.getResponse().getContentAsString(), TestRessource.class);

		assert Objects.equals(initialResource.getTitle(), queryingResource.getTitle()) &&
				Objects.equals(initialResource.getDescription(), queryingResource.getDescription());
	}

	// todo test grant resource to other user
	@Test
	void testGrantResource() throws Exception{

		// register users
		mockMvc.perform(get("/user/register/a/a"))
				.andExpect(status().is(200));

		mockMvc.perform(get("/user/register/b/b"))
				.andExpect(status().is(200));

		// create resource
		MvcResult creationResponse = mockMvc.perform(put("/test/")
						.with(user("a").password("a"))
						.contentType("application/json")
						.content("{\"id\": null, \"title\": \"Test\", \"description\": \"Test 2\"}"))
				.andExpect(status().isOk())
						.andReturn();


		TestRessource testRessourceResponse =  gson.fromJson(
				creationResponse.getResponse().getContentAsString(),
				TestRessource.class);

		mockMvc.perform(put("/user/grant/b/WRITE/TestResource/" + testRessourceResponse.getId())
				.with(user("a").password("a"))
		).andExpect(status().isOk());


		// call resource as user b
		mockMvc.perform(get("/test/" + testRessourceResponse.getId())
				.with(user("b").password("b"))
		).andExpect(status().isOk());

		// delete resource
		// todo continue here


	}



}
