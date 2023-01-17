package at.spengergasse.springsecurity;

import at.spengergasse.springsecurity.entity.Assignment;
import at.spengergasse.springsecurity.entity.RsaKeyProperties;
import at.spengergasse.springsecurity.entity.UserEntity;
import at.spengergasse.springsecurity.permission.PermissionEntity;
import at.spengergasse.springsecurity.permission.PermissionType;
import at.spengergasse.springsecurity.repository.AssignmentRepository;
import at.spengergasse.springsecurity.repository.UserRepository;
import com.google.gson.Gson;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.logging.Logger;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;


@AutoConfigureMockMvc
@SpringBootTest
@DirtiesContext
class RestsecApplicationTests {

	Logger logger = Logger.getLogger("testLogger");

	static Gson gson = new Gson();

	@Autowired
    UserRepository userRepository;

	@Autowired
    AssignmentRepository assignmentRepository;

	String userAJWT;

	@Autowired
	MockMvc mockMvc;

	void createNewUser(String username, String password) throws Exception {
		// check if user gets created
		var response = mockMvc
				.perform(put(String.format("/user/register/%s/%s", username, password)))
				.andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();

		var returnedUser = gson.fromJson(response, UserEntity.class);

		assert !returnedUser.getPassword().equals(password);

	}


	void userGrantsPermissionToUser(UserEntity granter, UserEntity grantee, PermissionType type, Long objectId, String targetType) throws Exception {

		PermissionEntity permissionEntity = PermissionEntity.builder()
				.principal(grantee)
				.targetTypeName(targetType)
				.objectId(objectId)
				.permName(type)
				.build();

		mockMvc.perform(
						put("/permission/grant")
								.with(user(granter.getUsername()).password(granter.getPassword()))
								.content(gson.toJson(permissionEntity))
								.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());
	}

	Assignment addAssignment(Assignment assignment, String username, String password) throws Exception {
		var result = mockMvc.perform(
						put("/assignment/")
								.with(user(username).password(password))
								.contentType(MediaType.APPLICATION_JSON)
								.content(gson.toJson(assignment)))
				.andExpect(status().isCreated())
				.andReturn().getResponse().getContentAsString();

		return gson.fromJson(result, Assignment.class);
	}

	@Test
	void contextLoads() {}

	/** create a new user a
	 * the user should be able to write resources in his name
	 * */
	@BeforeEach
	@Transactional
	void setupCreateUsers() throws Exception {
		createNewUser("a", "a");
		createNewUser("b", "b");
	}

	@Autowired
	private RsaKeyProperties rsaKeys;


	JwtDecoder jwtDecoder() {
		return NimbusJwtDecoder.withPublicKey(rsaKeys.publicKey()).build();
	}

	@Test
	@Transactional
	@Order(1)
	@DirtiesContext
	void testReceiveAdminsJWTAtLogin() throws Exception {
		var result = mockMvc.perform(
				get("/user/login")
						.with(user("a").password("a")))
				.andExpect(status().isOk())
				.andReturn();

		userAJWT = result.getResponse().getContentAsString();

		//  check if admin is owner of the key

		JwtDecoder decoder = jwtDecoder();
		var JWT = decoder.decode(userAJWT);

		var claims = JWT.getClaims();
		assert claims.containsKey("sub");
		assert claims.get("sub").equals("a");
	}

	/** users have full rights on their resources
	 *  test adding a new assignment on a newly created
	 *  user
	 * */
	@Test
	@Transactional
	@Order(2)
	@DirtiesContext
	void testAddAssignmentOnNewUser() throws Exception {

		// setup
		userGrantsPermissionToUser(
				UserEntity.builder().username("admin").password("admin").build(),
				UserEntity.builder().username("a").password("a").build(),
				PermissionType.write,
				PermissionEntity.NEW_OBJECT,
				"Assignment");

		// execution
		Assignment newOnUserA = Assignment.builder()
				.title("new")
				.description("new")
				.owner(null)
				.id(null)
				.build();


		var parsedResult = addAssignment(newOnUserA, "a", "a");

		// check
		assert parsedResult.getOwner() != null;
		assert parsedResult.getId() != null;
	}

	/** user a grants user b full access to one of its resources */
	@Test
	@Transactional
	@Order(3)
	@DirtiesContext
	void testUserGrantsPermissionToOtherUser() throws Exception{
		// setup
		userGrantsPermissionToUser(
				UserEntity.builder().username("admin").password("admin").build(),
				UserEntity.builder().username("a").build(),
				PermissionType.grant,
				PermissionEntity.ALL_OBJECTS,
				"Assignment");

		// user a creates his own assignment
		// execution
		Assignment newOnUserA = Assignment.builder()
				.title("new")
				.description("new")
				.owner(null)
				.id(null)
				.build();

		var assignmentUserA = addAssignment(newOnUserA, "a", "a");

		// user a grants user b access to is
		userGrantsPermissionToUser(
				UserEntity.builder().username("a").password("a").build(),
				UserEntity.builder().username("b").build(),
				PermissionType.write,
				assignmentUserA.getId(),
				"Assignment");

		// user b can read the ressource
		var userBQueryResult = gson.fromJson(mockMvc.perform(
				get(String.format("/assignment/%s", assignmentUserA.getId()))
						.with(user("b").password("b"))
		).andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString(), Assignment.class);

		// check
		assert Objects.equals(userBQueryResult.getId(), assignmentUserA.getId());

		assignmentUserA.setTitle("bchangedthis");

		// subtest

		// user b updates the ressource from user a
		// executiom
		var changedAssignment = gson.fromJson(
				mockMvc.perform(patch("/assignment/"+assignmentUserA.getId())
				.with(user("b").password("b"))
				.content(gson.toJson(assignmentUserA))
				.contentType(MediaType.APPLICATION_JSON)
		).andExpect(status().isAccepted())
				.andReturn().getResponse().getContentAsString(), Assignment.class);

		// check
		assert changedAssignment.getTitle().equals(assignmentUserA.getTitle());

		// subtest 2
		// user b deletes the ressource from user a
		mockMvc.perform(
				delete("/assignment/"+changedAssignment.getId())
						.with(user("b").password("b"))
		).andExpect(
				status().isAccepted()
		);

		// user a tries to access the deleted ressource
		mockMvc.perform(
				get("/assignment/"+changedAssignment.getId())
						.with(user("a").password("a"))
		).andExpect(
				status().isNotFound()
		);
	}

	@Order(4)
	@Test
	@DirtiesContext
	@Transactional
	void testChangePassword() throws Exception {

		var username = "b";

		var oldPassword = "b";
		var newPassword = "c";

		mockMvc.perform(post(String.format("/user/changePassword/%s/%s", oldPassword, newPassword))
				.with(user(username).password(oldPassword)))
				.andExpect(status().isOk());

		mockMvc.perform(get("/user/login")
						.with(user(username).password(oldPassword)))
				.andExpect(status().isForbidden());

		mockMvc.perform(get("/user/login")
				.with(user(username).password(newPassword)))
				.andExpect(status().isOk());

	}


}
