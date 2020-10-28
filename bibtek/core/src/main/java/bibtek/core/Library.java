package bibtek.core;

import java.util.HashSet;
import java.util.Set;

/**
 * Class representing a users personal library storing book entries. Persists
 * data continuously in local storage by using a {@link StorageHandler}
 */
public final class Library {

    private Set<BookEntry> bookEntries;

    /**
     * Initializing an empty library.
     *
     */
    public Library() {
        bookEntries = new HashSet<BookEntry>();

    }

    /**
     * @param bookEntry describes a readers relation with a Book
     */
    public void addBookEntry(final BookEntry bookEntry) {
        if (!isValidBookEntry(bookEntry)) {
            throw new IllegalArgumentException("The book entry has illegal formatting!");
        }
        bookEntries.add(bookEntry);

    }

    /**
     * @param bookEntry describes a readers relation with a Book
     */
    public void removeBookEntry(final BookEntry bookEntry) {

        bookEntries.remove(bookEntry);

    }

    /**
     * @return the BookEntries in the Library
     */
    public Set<BookEntry> getBookEntries() {
        return bookEntries;
    }

    // TODO
    /*
     * public void removeBookEntriesByProperty(String property, String value) {
     *
     * }
     */

    /**
     * @param bookEntry describes a readers relation with a Book
     * @return whether or not the BookEntry is valid
     */
    private boolean isValidBookEntry(final BookEntry bookEntry) {

        return bookEntry != null && bookEntry.getBook() != null && bookEntry.getDateAcquired() != null
                && bookEntry.getReadingState() != null && bookEntry.getBook().getAuthor() != null
                && bookEntry.getBook().getTitle() != null;

    }

    @Override
    public String toString() {

        final StringBuilder sb = new StringBuilder();

        if (getBookEntries().isEmpty()) {
            return "No books in library.";
        }

        sb.append("bookEntries: { \n");
        getBookEntries().forEach(bookEntry -> sb.append(bookEntry.toString()).append(",\n"));
        sb.append("}");

        return sb.toString();

    }

}