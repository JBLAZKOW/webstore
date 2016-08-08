package pl.spring.demo.web.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import pl.spring.demo.enumerations.BookStatus;
import pl.spring.demo.service.BookService;
import pl.spring.demo.to.BookTo;
import pl.spring.demo.web.utils.FileUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@WebAppConfiguration
public class BookRestServiceTest {

	@Autowired
	private BookService bookService;
	@Autowired
	private WebApplicationContext wac;

	private MockMvc mockMvc;

	@Before
	public void setUp() {
		Mockito.reset(bookService);
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
	}

	@Test
	public void testShouldGetAllBooks() throws Exception {

		// given
		final BookTo bookTo1 = new BookTo(1L, "title", "Author1", BookStatus.FREE);

		// register response for bookService.findAllBooks() mock
		Mockito.when(bookService.findAllBooks()).thenReturn(Arrays.asList(bookTo1));
		// when
		ResultActions response = this.mockMvc.perform(get("/findAll").accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON).content("1"));
		// then
		response.andExpect(status().isOk())//
				.andExpect(jsonPath("[0].id").value(bookTo1.getId().intValue()))
				.andExpect(jsonPath("[0].title").value(bookTo1.getTitle()))
				.andExpect(jsonPath("[0].authors").value(bookTo1.getAuthors()));
	}

	@Test
	public void testShouldGetFoundBooksByAuthor() throws Exception {
		// given
		final BookTo bookTo1 = new BookTo(1L, "title", "Author1", BookStatus.FREE);
		// register response for bookService.findAllBooks() mock
		Mockito.when(bookService.findBooksByAuthor(Mockito.anyString())).thenReturn(Arrays.asList(bookTo1));
		// when
		ResultActions response = this.mockMvc.perform(get("/findByAuthor").param("author", bookTo1.getAuthors())
				.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).content("1"));
		// then
		response.andExpect(status().isOk())//
				.andExpect(jsonPath("[0].id").value(bookTo1.getId().intValue()))
				.andExpect(jsonPath("[0].title").value(bookTo1.getTitle()))
				.andExpect(jsonPath("[0].authors").value(bookTo1.getAuthors()));
	}

	@Test
	public void testShouldGetFoundBooksByTitle() throws Exception {
		// given
		final BookTo bookTo1 = new BookTo(1L, "title", "Author1", BookStatus.FREE);
		// register response for bookService.findAllBooks() mock
		Mockito.when(bookService.findBooksByTitle(Mockito.anyString())).thenReturn(Arrays.asList(bookTo1));
		// when
		ResultActions response = this.mockMvc.perform(get("/findByTitle").param("title", bookTo1.getTitle())
				.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).content("1"));
		// then
		response.andExpect(status().isOk())//
				.andExpect(jsonPath("[0].id").value(bookTo1.getId().intValue()))
				.andExpect(jsonPath("[0].title").value(bookTo1.getTitle()))
				.andExpect(jsonPath("[0].authors").value(bookTo1.getAuthors()));
	}

	@Test
	public void testShouldGetFoundBooksByAuthorAndTitle() throws Exception {
		// given
		final BookTo bookTo1 = new BookTo(1L, "title", "Author1", BookStatus.FREE);
		// register response for bookService.findAllBooks() mock
		Mockito.when(bookService.findBooksByAuthorAndTitle(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(Arrays.asList(bookTo1));
		// when
		ResultActions response = this.mockMvc
				.perform(get("/findBooks").param("title", bookTo1.getTitle()).param("author", bookTo1.getAuthors())
						.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).content("1"));
		// then
		response.andExpect(status().isOk())//
				.andExpect(jsonPath("[0].id").value(bookTo1.getId().intValue()))
				.andExpect(jsonPath("[0].title").value(bookTo1.getTitle()))
				.andExpect(jsonPath("[0].authors").value(bookTo1.getAuthors()));
	}

	@Test
	public void testShouldGetFoundBooksByAuthorAndTitleArrayParameters() throws Exception {
		// given
		final BookTo bookTo1 = new BookTo(1L, "title", "Author1", BookStatus.FREE);
		// register response for bookService.findAllBooks() mock
		Mockito.when(bookService.findBooksByAuthorAndTitle(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(Arrays.asList(bookTo1));
		// when
		ResultActions response = this.mockMvc.perform(get("/findBooksArray")
				.param("params", "paramArray[0]").param("params", "paramArray[1]").accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON).content("1"));
		// then
		response.andExpect(status().isOk())//
				.andExpect(jsonPath("[0].id").value(bookTo1.getId().intValue()))
				.andExpect(jsonPath("[0].title").value(bookTo1.getTitle()))
				.andExpect(jsonPath("[0].authors").value(bookTo1.getAuthors()));
	}
	

	@Test
	public void testShouldSaveBook() throws Exception {
		// given
		File file = FileUtils.getFileFromClasspath("classpath:pl/spring/demo/web/json/bookToSave.json");
		String json = FileUtils.readFileToString(file);
		// when
		ResultActions response = this.mockMvc.perform(post("/book").accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON).content(json.getBytes()));
		// then
		response.andExpect(status().isOk());
	}

	@Test
	public void testShouldUpdateBook() throws Exception {
		// given
		File file = FileUtils.getFileFromClasspath("classpath:pl/spring/demo/web/json/bookToSave.json");
		String json = FileUtils.readFileToString(file);
		// when
		ResultActions response = this.mockMvc.perform(put("/book").accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON).content(json.getBytes()));
		// then
		response.andExpect(status().isOk());
	}

	@Test
	public void testShouldDeleteBook() throws Exception {
		// given
		File file = FileUtils.getFileFromClasspath("classpath:pl/spring/demo/web/json/bookToDelete.json");
		String json = FileUtils.readFileToString(file);
		// when
		ResultActions response = this.mockMvc.perform(delete("/book").accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON).content(json.getBytes()));
		// then
		Mockito.verify(bookService).deleteBook(2L);
		response.andExpect(status().isOk());
	}

}
