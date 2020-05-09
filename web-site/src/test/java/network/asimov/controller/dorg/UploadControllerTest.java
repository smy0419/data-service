package network.asimov.controller.dorg;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * @author sunmengyuan
 * @date 2020-03-31
 */
@RunWith(SpringRunner.class)
@SpringBootTest(properties = "spring.profiles.active=dev,mysql-dev,chainrpc-dev,mongodb-dev")
public class UploadControllerTest {

    private String contractCreator = "0x66c0309edea8dcd06a18c2c2d1b6bc8027ad89c812";

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mvc;

    @Before
    public void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    public void uploadPhoto() throws Exception {
        mvc.perform(
                MockMvcRequestBuilders
                        .fileUpload("/dao/upload/photo")
                        .file(
                                new MockMultipartFile("pic", "test.txt", ",multipart/form-data", new byte[512])
                        )
                        .header("token", contractCreator)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(0))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        // pic size less than minimum
        mvc.perform(
                MockMvcRequestBuilders
                        .fileUpload("/dao/upload/photo")
                        .file(
                                new MockMultipartFile("pic", "test.txt", ",multipart/form-data", new byte[127])
                        )
                        .header("token", contractCreator)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(2500))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }
}