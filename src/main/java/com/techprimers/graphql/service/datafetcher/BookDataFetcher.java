package com.techprimers.graphql.service.datafetcher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.techprimers.graphql.model.Book;
import com.techprimers.graphql.repository.BookRepository;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

@Component
public class BookDataFetcher implements DataFetcher<Book> {

	@Autowired
	BookRepository bookRepository;

	@Override
	public Book get(DataFetchingEnvironment dataFetchingEnvironment) throws Exception {
		String isn = dataFetchingEnvironment.getArgument("id");
		return bookRepository.findById(isn).get();
	}

}
