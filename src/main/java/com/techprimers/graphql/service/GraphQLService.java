package com.techprimers.graphql.service;

import java.io.File;
import java.io.IOException;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.techprimers.graphql.model.Book;
import com.techprimers.graphql.repository.BookRepository;
import com.techprimers.graphql.service.datafetcher.AllBooksDataFetcher;
import com.techprimers.graphql.service.datafetcher.BookDataFetcher;

import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;

@Service
public class GraphQLService {
	
	@Autowired
	BookRepository bookRepository;

	@Value("classpath:books.graphql")
	Resource resource;

	private GraphQL graphQL;

	@Autowired
	private AllBooksDataFetcher allBooksDataFetcher;
	@Autowired
	private BookDataFetcher bookDataFetcher;

	// load schema at application start up
	@PostConstruct
	private void loadSchema() throws IOException {
		// Load Books into Book Repository
		loadDataIntoHSQL();

		// get the schema
		File schemaFile = resource.getFile();
		// parse schema
		TypeDefinitionRegistry typeRegistry = new SchemaParser().parse(schemaFile);
		RuntimeWiring wiring = buildRuntimeWiring();
		GraphQLSchema schema = new SchemaGenerator().makeExecutableSchema(typeRegistry, wiring);
		graphQL = GraphQL.newGraphQL(schema).build();

	}

	private void loadDataIntoHSQL() {
		Stream.of(
				new Book("101", "Books of Clouds", "Kindle Edition", new String[] {"Chloe Aridjis"}, "Nov 2017"),
				new Book("102", "Cloud Arch & Engineering", "Orielly", new String[] {"Peter", "Sam"}, "Jan 2015"),
				new Book("103", "Java 9 Programming", "Kindle Edition", new String[] {"Venket", "Ram"}, "Dec 2016"),
				new Book("104", "Books of AWS", "Orielly", new String[] {"Jeff", "Sandeep"}, "July 2018"),
				new Book("105", "Books of GCP", "Kindle Edition", new String[] {"Sundar", "Steben"}, "Feb 2012"),
				new Book("106", "Books of Azure", "Orielly", new String[] {"Bill", "Rohit"}, "Sep 2014")
		).forEach(book -> {
			bookRepository.save(book);
		});

	}

	private RuntimeWiring buildRuntimeWiring() {
		return RuntimeWiring.newRuntimeWiring().type("Query", typewiring -> typewiring
				.dataFetcher("allBooks", allBooksDataFetcher)
				.dataFetcher("book", bookDataFetcher))
				.build();
	}

	public GraphQL getGrapgQL() {
		return graphQL;
	}

}
