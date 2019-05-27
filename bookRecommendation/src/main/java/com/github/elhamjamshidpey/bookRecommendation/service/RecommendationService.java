package com.github.elhamjamshidpey.bookRecommendation.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.elhamjamshidpey.bookRecommendation.data.Book;
import com.github.elhamjamshidpey.bookRecommendation.data.Feedback;
import com.github.elhamjamshidpey.bookRecommendation.data.LikeStatus;
import com.github.elhamjamshidpey.bookRecommendation.data.User;
import com.github.elhamjamshidpey.bookRecommendation.repository.BookRepository;

/*
@uthor by Elham
May 27, 2019
*/

@Service
public class RecommendationService {

	private static final Integer RESULT_SIZE = 20;
	
	@Autowired
	private BookRepository bookRepository;

	@Autowired
	private FeedbackService feedbackService;
	
	@Autowired
	private UserService userService;
	
	public List<Book> findAll() {
		return bookRepository.findAll();
	}

	public Book findByASIN(Integer ASIN) {
		return bookRepository.findOne(ASIN);
	}

	public List<Book> recommendBooksForUser(String username) {
		User currentUser = userService.findByUsername(username);
		
		Optional<List<Feedback>> userFeedBacks = feedbackService.get(currentUser);
		
		Map<String, Integer> genresWeight = new HashMap<String, Integer>();
		Map<String, Integer> authorsWeight = new HashMap<String, Integer>();
		
		userFeedBacks.ifPresent(feedbacks -> feedbacks.forEach(feedBack -> {
			
			String genre = feedBack.getBook().getGenre();
			if (!genresWeight.isEmpty() && genresWeight.get(genre) != null) {
				if (feedBack.getLikeStatus().equals(LikeStatus.LIKE))
					genresWeight.put(genre, genresWeight.get(genre) + 2);
				if (feedBack.getLikeStatus().equals(LikeStatus.DISLIKE))
					genresWeight.put(genre, genresWeight.get(genre) - 2);
				if (feedBack.getLikeStatus().equals(LikeStatus.NOT_INTERESTED))
					genresWeight.put(genre, genresWeight.get(genre) - 1);
			} else {
				if (feedBack.getLikeStatus().equals(LikeStatus.LIKE))
					genresWeight.put(genre, 2);
				if (feedBack.getLikeStatus().equals(LikeStatus.DISLIKE))
					genresWeight.put(genre, -2);
				if (feedBack.getLikeStatus().equals(LikeStatus.NOT_INTERESTED))
					genresWeight.put(genre, -1);
			}
			
			String author = feedBack.getBook().getAuthor();
			if (!authorsWeight.isEmpty() && authorsWeight.get(author) != null) {
				if (feedBack.getLikeStatus().equals(LikeStatus.LIKE))
					authorsWeight.put(author, authorsWeight.get(author) + 2);
				if (feedBack.getLikeStatus().equals(LikeStatus.DISLIKE))
					authorsWeight.put(author, authorsWeight.get(author) - 2);
				if (feedBack.getLikeStatus().equals(LikeStatus.NOT_INTERESTED))
					authorsWeight.put(author, authorsWeight.get(author) - 1);
			} else {
				if (feedBack.getLikeStatus().equals(LikeStatus.LIKE))
					authorsWeight.put(author, +2);
				if (feedBack.getLikeStatus().equals(LikeStatus.DISLIKE))
					authorsWeight.put(author, -2);
				if (feedBack.getLikeStatus().equals(LikeStatus.NOT_INTERESTED))
					authorsWeight.put(author, -1);
			}
		}));

		List<Book> books = bookRepository.findAll();

		// remove books which have user feedback
		userFeedBacks.ifPresent(f -> f.forEach(feedBack -> {
			Book feedbakedBook = feedBack.getBook();
			books.remove(feedbakedBook);

		}));

		//Generate recommendation books
		List<Book> recommendedBooks = new ArrayList<Book>();
		Map<Book, Integer> booksWeights = new HashMap<Book, Integer>();

		if(userFeedBacks.isPresent()) {
			books.forEach(book -> {
				Integer bookGenreWeight = Optional.ofNullable(genresWeight) != null ? 0 :genresWeight.get(book.getGenre());
				Integer bookAuthorWeight = Optional.ofNullable(authorsWeight) != null ? 0 :authorsWeight.get(book.getAuthor());
				Integer bookWeight = bookGenreWeight + bookAuthorWeight;
				booksWeights.put(book, bookWeight);
			});
			booksWeights.entrySet().stream().sorted(Map.Entry.<Book, Integer>comparingByValue()).limit(RESULT_SIZE)
					.forEach(item -> recommendedBooks.add((Book) item.getKey()));
			
		}else {
			 books.stream().limit(RESULT_SIZE).forEach(b -> recommendedBooks.add(b));
		}

		return recommendedBooks;
	}

}

