package com.nashtech.rootkies.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.nio.charset.Charset;
import java.util.Optional;

import com.nashtech.rootkies.model.Category;
import com.nashtech.rootkies.repository.CategoryRepository;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@RunWith(SpringRunner.class)
public class AssetControllerTest {
    public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryRepository categoryRepository;

    @Test
    // @WithMockUser(username = "admin"/* , roles={"",""} */)
    public void createAsset() throws Exception {

        when(categoryRepository.findById(Mockito.anyString())).thenReturn(Optional.empty());

        String createCategoryRequest = "{\"assetName\": \"test\",\"state\": 2,\"installDate\": \"2021-07-06T14:26:05\",\"specification\": \"intel core i7\",\"categoryCode\": \"PJ\"}";
        String error = this.mockMvc
                .perform(post("/asset").contentType(APPLICATION_JSON_UTF8).content(createCategoryRequest))
                .andExpect(status().is(400)).andReturn().getResolvedException().getMessage();

        assertTrue(error.contains("ERR_CATEGORY_NOT_FOUND"));
    }

}