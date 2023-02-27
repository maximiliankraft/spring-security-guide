package at.maxkraft.restsec;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;


@SpringBootTest
@AutoConfigureMockMvc
class RestsecApplicationTests {

	@Autowired
	MockMvc mockMvc;


	@Test
	void registerAndLoginNewUser() throws Exception {
		mockMvc.perform(get("/user/register/test/abc"))
				.andExpect(result -> {
					assert result.getResponse().getStatus() == 200;
				});

		mockMvc.perform(get("/user/login/test/abc"))
				.andExpect(result -> {
					assert result.getResponse().getStatus() == 200;
				});


	}


	@Test
	void contextLoads() {
	}




}
