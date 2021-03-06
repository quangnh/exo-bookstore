/*
 * Copyright (C) 2003-2012 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU Affero General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.book.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.exoplatform.book.base.BaseTestCase;
import org.exoplatform.bookstore.jcr.model.Book;
import org.exoplatform.bookstore.jcr.model.Category;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.JVM)
public class BookServiceTest extends BaseTestCase {
  
  Logger logger = Logger.getLogger(BaseTestCase.class);

	private List<Book> tearDownPollList;
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		tearDownPollList = new ArrayList<Book>();
	}

	@Override
	public void tearDown() throws Exception {
		for(Book b : tearDownPollList) {
			logger.info("Remove book ...");
			bookStoreService.deleteBook(b.getId());
		}
		super.tearDown();
	}
	
	/** 
	 * Check Bookstore service is available or not
	 * @throws Exception
	 */
	public void testBookstoreService() throws Exception {
		assertNotNull(getBookStoreService());
	}
	
	//Test getAllCategories function
	public void testGetAllCategories() throws Exception {
		List<Category> list = bookStoreService.getAllCategories();
		for(Category category : list){
			logger.info("Category id: '"+ category.getId());
			logger.info("Category label: '"+ category.getLblCategory());
		}
		assertTrue(list.size()>0);
	}
	
	//Test insert Book function
	public void testInsertBook() throws Exception {
		Book book = getBook();
		bookStoreService.insertBook(book);
		tearDownPollList.add(book);
		assertNotNull(bookStoreService.findById(book.getId()));
	}
	
	//Test update Book function
	public void testUpdateBook() throws Exception {
		//insert Book
		Book book = getBook();
		bookStoreService.insertBook(book);
		
		String bookId = book.getId();
		Book result = bookStoreService.findById(bookId);
		assertEquals(categoryId, result.getCategory());
		
		//update
		book.setCategory(categoryIdUpdate);
		bookStoreService.updateBook(book);
		tearDownPollList.add(book);
		
		result = bookStoreService.findById(bookId);
		assertEquals(categoryIdUpdate, result.getCategory());
	}
	
	
	/** Test findAll() */
	public void testFindAll() throws Exception {
		//insert Book
		Book book = getBook();
		bookStoreService.insertBook(book);
		boolean isSuccess = false;
		List<Book> bookList = bookStoreService.findAll();
		for(Book b : bookList) {
			logger.info("Book title: '"+ b.getTitle());
			if(b.getId().equals(book.getId())) isSuccess = true;
		}
		tearDownPollList.add(book);
		assertTrue(isSuccess);
	}
	
	/**Test findByTitle function */
	public void testFindByTitle() throws Exception {
	  //insert Book with title 'Title of Book'
	  Book book1 = getBook();
	  bookStoreService.insertBook(book1);
	  
	  //insert Book with title 'Story of Mr.G'
	  Book book2 = new Book();
	  book2.setTitle("Story of Mr.G");
	  book2.setCategory(categoryId);
	  book2.setIsbn("ISBN of Story");
	  book2.setPublisher("NXB DN");
	  
	  bookStoreService.insertBook(book2);
	  boolean foundBook1 = false;
	  boolean foundBook2 = false;
	  List<Book> bookList = bookStoreService.findByTitle("Story");
	  for(Book b : bookList) {
      logger.info("Book title: '"+ b.getTitle());
      if(b.getId().equals(book1.getId())) foundBook1 = true;
      if(b.getId().equals(book2.getId())) foundBook2 = true;
    }
	  tearDownPollList.add(book1);
    tearDownPollList.add(book2);
    assertTrue(!foundBook1 && foundBook2);
	}
	
	/**Test deleteBook*/
	public void testDeleteBook() throws Exception {
		Book book = getBook();
		bookStoreService.insertBook(book);
		
		//check insert is success
		assertNotNull(bookStoreService.findById(book.getId()));
		
		//Delete book
		bookStoreService.deleteBook(book.getId());
		
		assertNull(bookStoreService.findById(book.getId()));
	}
	
	private Book getBook() {
		Book book = new Book();
		book.setCategory(categoryId);
		book.setTitle("Title of Book");
		book.setIsbn("ISBN of Book");
		book.setPublisher("Publisher of Book");
		return book;
	}
	
}
