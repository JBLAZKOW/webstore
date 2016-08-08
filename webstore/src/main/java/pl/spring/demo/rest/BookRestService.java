package pl.spring.demo.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import pl.spring.demo.enumerations.BookStatus;
import pl.spring.demo.service.BookService;
import pl.spring.demo.to.BookTo;

/**
 * @author JBLAZKOW
 *
 */
@Controller
@ResponseBody
public class BookRestService {

	@Autowired
	private BookService bookService;

	@RequestMapping(value = "/rest/books", method = RequestMethod.GET, 
			produces = MediaType.APPLICATION_JSON_VALUE)
	public final ResponseEntity<BookTo> getBook() {
		BookTo currentBook = new BookTo(1L, "title", "author", null);
		currentBook.setStatus(BookStatus.FREE);
		return new ResponseEntity<BookTo>(currentBook, HttpStatus.OK);
	}

	/**
	 * @return collection of all books
	 */
	@RequestMapping(value = "/findAll", method = RequestMethod.GET)
	public final List<BookTo> findAllBooks() {
		return bookService.findAllBooks();
	}

	/**
	 * @param title
	 * @return founded books by title
	 */
	@RequestMapping(value = "/findByTitle", method = RequestMethod.GET)
	public final List<BookTo> findBooksByTitle(@RequestParam("title") final String title) {
		return bookService.findBooksByTitle(title);
	}

	/**
	 * @param author
	 * @return founded books by author
	 */
	@RequestMapping(value = "/findByAuthor", method = RequestMethod.GET)
	public final List<BookTo> findBooksByAuthor(@RequestParam("author") final String author) {
		return bookService.findBooksByAuthor(author);
	}

	/**
	 * @param author
	 * @param title
	 * @return founded books by author and title
	 */
	@RequestMapping(value = "/findBooks", method = RequestMethod.GET)
	public final List<BookTo> findBooksByTitleAndAuthor(@RequestParam("author") final String author,
			@RequestParam("title") final String title) {
		return bookService.findBooksByAuthorAndTitle(title, author);
	}

	/**
	 * @param paramArray
	 * @return founded books by author and title
	 */
	@RequestMapping(value = "/findBooksArray", method = RequestMethod.GET)
	public final List<BookTo> findBooksByTitleAndAuthor(
			@RequestParam(value = "params") final String[] paramArray) {
		return bookService.findBooksByAuthorAndTitle(paramArray[0], paramArray[1]);
	}

	/**
	 * @param book
	 *            to save
	 * @return saved book
	 */
	@RequestMapping(value = "/book", method = RequestMethod.POST)
	public final BookTo saveBook(@RequestBody final BookTo book) {
		return bookService.saveBook(book);
	}

	/**
	 * @param book
	 *            to update
	 * @return updated book
	 */
	@RequestMapping(value = "/book", method = RequestMethod.PUT)
	public final BookTo addBook(@RequestBody final BookTo book) {
		return bookService.saveBook(book);
	}

	/**
	 * @param book
	 *            to delete
	 */
	@RequestMapping(value = "/book", method = RequestMethod.DELETE)
	public final void deleteBook(@RequestBody final BookTo book) {
		bookService.deleteBook(book.getId());
	}

}
