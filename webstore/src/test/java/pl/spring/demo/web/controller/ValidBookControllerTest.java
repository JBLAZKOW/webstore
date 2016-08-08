package pl.spring.demo.web.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import com.fasterxml.jackson.databind.ObjectMapper;

import pl.spring.demo.constants.AnnouncementConstants;
import pl.spring.demo.constants.ModelConstants;
import pl.spring.demo.constants.ViewNames;
import pl.spring.demo.controller.BookController;
import pl.spring.demo.enumerations.BookStatus;
import pl.spring.demo.service.BookService;
import pl.spring.demo.to.BookTo;

/**
 * @author JBLAZKOW
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "controller-test-configuration.xml")
@WebAppConfiguration
public class ValidBookControllerTest {

	@Autowired
	private BookService bookService;

	private MockMvc mockMvc;

	@Before
	public void setup() {
		Mockito.reset(bookService);

		InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
		viewResolver.setPrefix("/WEB-INF/views/");
		viewResolver.setSuffix(".jsp");

		BookController bookController = new BookController();
		mockMvc = MockMvcBuilders.standaloneSetup(bookController).setViewResolvers(viewResolver).build();
		// Due to fact, that We are trying to construct real Bean - Book
		// Controller, we have to use reflection to mock existing field book
		// service
		ReflectionTestUtils.setField(bookController, "bookService", bookService);
	}
	
	@Test 
	public void testDisplayAddPage() throws Exception {
		// given
		BookTo testBook = new BookTo(1L, "title", "author", BookStatus.FREE);
		// when
		ResultActions resultActions = mockMvc.perform(get("/books/add").flashAttr(ModelConstants.NEW_BOOK, testBook));
		// then
		Mockito.verifyZeroInteractions(bookService);
		resultActions.andExpect(view().name(ViewNames.ADD_BOOK));
	}

	@Test
	public void testAddBookPage() throws Exception {
		// given
		BookTo testBook = new BookTo(1L, "Test title", "Test Author", BookStatus.FREE);
		Mockito.when(bookService.saveBook(Mockito.any())).thenReturn(testBook);
		// when
		ResultActions resultActions = mockMvc.perform(post("/books/add").flashAttr(ModelConstants.NEW_BOOK, testBook));
		// then
		Mockito.verify(bookService).saveBook(testBook);
		resultActions.andExpect(view().name(ViewNames.ADD_BOOK)).andExpect(model().attribute(ModelConstants.ANNOUNCEMENT, AnnouncementConstants.ADDED))
				.andExpect(model().attribute(ModelConstants.NEW_BOOK, new ArgumentMatcher<Object>() {
					@Override
					public boolean matches(Object argument) {
						BookTo book = (BookTo) argument;
						return null != book && testBook.getTitle().equals(book.getTitle());
					}
				}));
	}
	
	@Test
	public void testShouldNotAddBookPageWithoutParameter() throws Exception {
		// given
		BookTo testBook = new BookTo(1L, "", "Test Author", BookStatus.FREE);
		// when
		ResultActions resultActions = mockMvc.perform(post("/books/add").flashAttr(ModelConstants.NEW_BOOK, testBook));
		// then
		Mockito.verifyZeroInteractions(bookService);
		resultActions.andExpect(view().name(ViewNames.ADD_BOOK)).andExpect(model().attribute(ModelConstants.ANNOUNCEMENT, AnnouncementConstants.NOT_FILLED))
		.andExpect(model().attribute(ModelConstants.NEW_BOOK, new ArgumentMatcher<Object>() {
			@Override
			public boolean matches(Object argument) {
				BookTo book = (BookTo) argument;
				return null != book && testBook.getTitle().equals(book.getTitle());
			}
		}));
	}

	@Test
	public void testDisplayAllBooksPage() throws Exception {
		// given
		BookTo testBook = new BookTo(1L, "testTitle", "testAuthor", BookStatus.FREE);
		BookTo testBook2 = new BookTo(2L, "testTitle2", "testAuthor2", BookStatus.LOAN);
		List<BookTo> testBooks = new ArrayList<BookTo>();
		testBooks.add(testBook);
		testBooks.add(testBook2);
		Mockito.when(bookService.findAllBooks()).thenReturn(testBooks);
		// when
		ResultActions resultActions = mockMvc.perform(get("/books/all").flashAttr(ModelConstants.BOOK_LIST, testBooks));
		// then
		resultActions.andExpect(view().name(ViewNames.BOOKS))
				.andExpect(model().attribute(ModelConstants.BOOK_LIST, new ArgumentMatcher<Object>() {
					@Override
					public boolean matches(Object argument) {
						List<BookTo> books = (List<BookTo>) argument;
						return books.size() == 2 && books.get(0).getTitle().equals(testBooks.get(0).getTitle())
								&& books.get(0).getAuthors().equals(testBooks.get(0).getAuthors())
								&& books.get(1).getTitle().equals(testBooks.get(1).getTitle())
								&& books.get(1).getAuthors().equals(testBooks.get(1).getAuthors());

					}
				}));
	}

	@Test
	public void testDisplayBookDetails() throws Exception {
		// given
		long id = 1L;
		BookTo testBook = new BookTo(id, "testTitle", "testAuthor", BookStatus.FREE);
		Mockito.when(bookService.findBookById(id)).thenReturn(testBook);
		// when
		ResultActions resultActions = mockMvc
				.perform(get("/books/book").param("id", Long.toString(id)).flashAttr(ModelConstants.BOOK, testBook));
		// then
		resultActions.andExpect(view().name("book")).andExpect(model().attribute(ModelConstants.BOOK, new ArgumentMatcher<Object>() {
			@Override
			public boolean matches(Object argument) {
				BookTo book = (BookTo) argument;
				return book != null && testBook.getTitle().equals(book.getTitle());
			}
		}));
	}

	@Test
	public void testDisplayFindPage() throws Exception {
		// given
		// when
		ResultActions resultActions = mockMvc.perform(get("/books/find"));
		// then
		resultActions.andExpect(view().name("findBook"));
	}

	@Test
	public void testFindBookByTitle() throws Exception {
		// given
		BookTo testBookFirst = new BookTo(1L, "title", "", BookStatus.LOAN);
		List<BookTo> testBooks = new ArrayList<BookTo>();
		testBooks.add(testBookFirst);
		Mockito.when(bookService.findBooksByTitle(Mockito.anyString())).thenReturn(testBooks);
		// when
		ResultActions resultActions = mockMvc.perform(post("/books/find").flashAttr(ModelConstants.FOUND_BOOK, testBookFirst));
		// then
		resultActions.andExpect(view().name(ViewNames.BOOKS))
				.andExpect(model().attribute(ModelConstants.BOOK_LIST, new ArgumentMatcher<Object>() {
					@Override
					public boolean matches(Object argument) {
						List<BookTo> books = (List<BookTo>) argument;
						return books != null && books.get(0).getTitle().equals(testBooks.get(0).getTitle()) ;
					}
				}));
	}
	
	@Test
	public void testFindBookByAuthors() throws Exception {
		// given
		BookTo testBookFirst = new BookTo(1L, "", "authors", BookStatus.LOAN);
		List<BookTo> testBooks = new ArrayList<BookTo>();
		testBooks.add(testBookFirst);
		Mockito.when(bookService.findBooksByAuthor(Mockito.anyString())).thenReturn(testBooks);
		// when
		ResultActions resultActions = mockMvc.perform(post("/books/find").flashAttr(ModelConstants.FOUND_BOOK, testBookFirst));
		// then
		resultActions.andExpect(view().name(ViewNames.BOOKS))
		.andExpect(model().attribute(ModelConstants.BOOK_LIST, new ArgumentMatcher<Object>() {
			@Override
			public boolean matches(Object argument) {
				List<BookTo> books = (List<BookTo>) argument;
				return books != null && books.get(0).getAuthors().equals(testBooks.get(0).getAuthors()) ;
			}
		}));
	}
	
	@Test
	public void testFindBookByAuthorsAndTitle() throws Exception {
		// given
		BookTo testBookFirst = new BookTo(1L, "title", "authors", BookStatus.LOAN);
		List<BookTo> testBooks = new ArrayList<BookTo>();
		testBooks.add(testBookFirst);
		Mockito.when(bookService.findBooksByAuthorAndTitle(Mockito.anyString(),Mockito.anyString())).thenReturn(testBooks);
		// when
		ResultActions resultActions = mockMvc.perform(post("/books/find").flashAttr(ModelConstants.FOUND_BOOK, testBookFirst));
		// then
		resultActions.andExpect(view().name(ViewNames.BOOKS))
		.andExpect(model().attribute(ModelConstants.BOOK_LIST, new ArgumentMatcher<Object>() {
			@Override
			public boolean matches(Object argument) {
				List<BookTo> books = (List<BookTo>) argument;
				return books != null && books.get(0).getAuthors().equals(testBooks.get(0).getAuthors())
						&& books.get(0).getTitle().equals(testBooks.get(0).getTitle()) ;
			}
		}));
	}

	@Test
	public void testDeleteBook() throws Exception {
		// given
		long id = 1L;
		BookTo testBook = new BookTo(id, "t", "a", BookStatus.FREE);
		// when
		ResultActions resultActions = mockMvc
				.perform(get("/books/delete").param("id", Long.toString(id)).flashAttr(ModelConstants.BOOK, testBook));
		Mockito.verify(bookService).deleteBook(1L);
		// then
		resultActions.andExpect(view().name(ViewNames.DELETE));
	}

	/**
	 * (Example) Sample method which convert's any object from Java to String
	 */
	private static String asJsonString(final Object obj) {
		try {
			final ObjectMapper mapper = new ObjectMapper();
			final String jsonContent = mapper.writeValueAsString(obj);
			return jsonContent;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
