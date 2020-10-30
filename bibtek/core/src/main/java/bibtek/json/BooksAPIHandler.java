package bibtek.json;

import bibtek.core.Book;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Handler responsible for communicating with the Google Books API.
 */
public final class BooksAPIHandler {

    private static final String BOOKS_API_URI_PREFIX = "https://www.googleapis.com/books/v1/volumes?q=isbn:";

    /**
     * Request book data from Google Books API based on ISBN number.
     *
     * @param isbn Unique book identification
     * @return Book loaded with the retrieved API data, or null if isbn gave no results
     * @throws IOException          if the fetch request could not be created validly
     * @throws InterruptedException if the fetch operation is interrupted
     */
    public Book fetchBook(final String isbn) throws IOException, InterruptedException {

        final HttpRequest fetchRequest = HttpRequest.newBuilder(URI.create(getFetchURIForISBN(isbn)))
                .GET()
                .header("accept", "application/json")
                .build();

        final HttpClient client = HttpClient.newHttpClient();

        final HttpResponse<String> fetchResponse = client.send(fetchRequest, HttpResponse.BodyHandlers.ofString());

        final JsonObject responseObject = JsonParser.parseString(fetchResponse.body()).getAsJsonObject();

        // Make sure there is at least one search result
        if (responseObject.get("totalItems").getAsInt() == 0) {
            return null;
        }

        // Pick the first element from search results
        final JsonObject bookObject = responseObject.get("items").getAsJsonArray().get(0).getAsJsonObject();

        final JsonObject bookInfo = (JsonObject) bookObject.get("volumeInfo");

        final String bookTitle = bookInfo.get("title").getAsString();
        final String bookAuthor = jsonArrayToSimpleString(bookInfo.get("authors").getAsJsonArray());

        String bookImgPath = "";
        try {
            bookImgPath = bookInfo.get("imageLinks").getAsJsonObject().get("smallThumbnail").getAsString();
        } catch (NullPointerException ignored) {
            // No thumbnail available for this book
        }

        int bookYearPublished = Book.YEAR_PUBLISHED_MISSING;

        // Check if publishing date is provided in the fetched data
        final JsonElement publishedDateField = bookInfo.get("publishedDate");
        if (publishedDateField != null) {

            final String bookPublishedString = publishedDateField.getAsString();

            // Check if published date one a single integer
            if (bookPublishedString.matches("\\d+")) {
                bookYearPublished = Integer.parseInt(bookPublishedString);
            } else {
                // Attempt to parse date with LocalDate
                try {
                    bookYearPublished = LocalDate.parse(bookPublishedString).getYear();
                } catch (DateTimeParseException e) {
                    e.printStackTrace();
                }
            }

        }

        return new Book(bookTitle, bookAuthor, bookYearPublished, bookImgPath);

    }

    /**
     * Builds valid URI for fetching book data from Google Books API by ISBN.
     *
     * @param isbn Unique book identification
     * @return URI as string
     */
    public String getFetchURIForISBN(final String isbn) {

        return BOOKS_API_URI_PREFIX + isbn;

    }

    /**
     * Parses a {@link JsonArray} and creates a comma separated string of elements.
     *
     * @param array Array with elements of type {@link JsonElement}
     * @return string concatenation of {@link JsonElement} strings separated by comma.
     */
    public String jsonArrayToSimpleString(final JsonArray array) {

        final StringBuilder builder = new StringBuilder();

        builder.append(array.remove(0).getAsString());

        for (final JsonElement author : array) {
            builder.append(", ").append(author.getAsString());
        }

        return builder.toString();

    }


}