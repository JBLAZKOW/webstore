package pl.spring.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import pl.spring.demo.constants.AnnouncementConstants;
import pl.spring.demo.constants.ModelConstants;
import pl.spring.demo.constants.ViewNames;
import pl.spring.demo.enumerations.BookStatus;
import pl.spring.demo.service.BookService;
import pl.spring.demo.to.BookTo;

/**
 * Book controller
 * 
 * @author mmotowid
 *
 */
/**
 * @author JBLAZKOW
 *
 */
@Controller
@RequestMapping("/books")
public class BookController {
	@Autowired
	private BookService bookService;

	@RequestMapping
	public final String list(final Model model) {
		return ViewNames.BOOKS;
	}

	/**
	 * Method collects info about all books.
	 */
	@RequestMapping("/all")
	public final ModelAndView allBooks() {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject(ModelConstants.BOOK_LIST, bookService.findAllBooks());
		modelAndView.setViewName(ViewNames.BOOKS);
		return modelAndView;
	}

	/**
	 * @param id
	 * @return view of single book
	 */
	@RequestMapping("/book")
	public final ModelAndView detailsBook(@RequestParam("id") final Long id) {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject(ModelConstants.BOOK, bookService.findBookById(id));
		modelAndView.setViewName(ViewNames.BOOK);
		return modelAndView;
	}

	/**
	 * @param model
	 * @return view before adding a book
	 */
	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public final String book(final Model model) {
		model.addAttribute(ModelConstants.NEW_BOOK, new BookTo());
		return ViewNames.ADD_BOOK;
	}

	/**
	 * @param newBook
	 * @return retrieving adding book and setting announcement of result as a object
	 */
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public final ModelAndView addBook(
			@ModelAttribute(ModelConstants.NEW_BOOK) final BookTo newBook) {
		String authors = newBook.getAuthors();
		String title = newBook.getTitle();
		BookStatus bookStatus = newBook.getStatus();
		ModelAndView modelAndView = new ModelAndView();
		if (bookStatus == null || title.isEmpty() || authors.isEmpty()) {
			modelAndView.addObject(ModelConstants.ANNOUNCEMENT, AnnouncementConstants.NOT_FILLED);
		} else {
			bookService.saveBook(newBook);
			modelAndView.addObject(ModelConstants.ANNOUNCEMENT, AnnouncementConstants.ADDED);
		}
		modelAndView.setViewName(ViewNames.ADD_BOOK);
		return modelAndView;
	}

	/**
	 * @param model
	 * @return view of searching book
	 */
	@RequestMapping(value = "/find", method = RequestMethod.GET)
	public final String findBook(final Model model) {
		model.addAttribute(ModelConstants.FOUND_BOOK, new BookTo());
		return ViewNames.FIND_BOOK;
	}

	/**
	 * @param bookToFind
	 * @return result of searching books and setting view to resulted books
	 */
	@RequestMapping(value = "/find", method = RequestMethod.POST)
	public final ModelAndView foundBook(
			@ModelAttribute(ModelConstants.FOUND_BOOK) final BookTo bookToFind) {
		ModelAndView modelAndView = new ModelAndView();
		if (bookToFind.getAuthors().isEmpty()) {
			modelAndView.addObject(ModelConstants.BOOK_LIST, 
					bookService.findBooksByTitle(bookToFind.getTitle()));
		} else if (bookToFind.getTitle().isEmpty()) {
			modelAndView.addObject(ModelConstants.BOOK_LIST, 
					bookService.findBooksByAuthor(bookToFind.getAuthors()));
		} else {
			modelAndView.addObject(ModelConstants.BOOK_LIST,
					bookService.findBooksByAuthorAndTitle(bookToFind.getTitle(), 
							bookToFind.getAuthors()));
		}
		modelAndView.setViewName(ViewNames.BOOKS);
		return modelAndView;
	}
	
	/**
	 * @param id of book to delete
	 * @return view informing of book that has been deleted
	 */
	@RequestMapping(value = "/delete")
	public final ModelAndView deleteBook(@RequestParam("id") final Long  id) {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject(ModelConstants.BOOK, bookService.findBookById(id));
		bookService.deleteBook(id);
		modelAndView.setViewName(ViewNames.DELETE);
		return modelAndView;
	}

	/**
	 * Binder initialization.
	 */
	@InitBinder
	public final void initialiseBinder(final WebDataBinder binder) {
		binder.setAllowedFields("id", "title", "authors", "status");
	}

}
