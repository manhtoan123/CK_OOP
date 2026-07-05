package com.library.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.model.Book;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Repository
public class BookRepository {
    private static final String FILE_PATH = "data/books.json";
    private final ObjectMapper objectMapper = new ObjectMapper();

    // CẢI TIẾN: Sử dụng CopyOnWriteArrayList để đảm bảo an toàn đa luồng trên Web API
    private final List<Book> books = new CopyOnWriteArrayList<>();

    public BookRepository() {
        loadFromFile();
    }

    public List<Book> getAll() {
        return Collections.unmodifiableList(books);
    }

    public Book findById(String bookId) {
        if (bookId == null) return null;
        return books.stream()
                .filter(b -> b.getBookId().equalsIgnoreCase(bookId.trim()))
                .findFirst()
                .orElse(null);
    }

    public synchronized void addBook(Book book) {
        if (book != null) {
            books.add(book);
            saveToFile();
        }
    }

    public synchronized void deleteBook(String bookId) {
        if (bookId != null) {
            books.removeIf(b -> b.getBookId().equalsIgnoreCase(bookId.trim()));
            saveToFile();
        }
    }

    /**
     * Đồng bộ cập nhật trạng thái sách từ tầng nghiệp vụ và tự động đồng bộ hóa xuống file JSON.
     */
    public synchronized void update(Book updatedBook) {
        if (updatedBook == null) return;
        for (int i = 0; i < books.size(); i++) {
            if (books.get(i).getBookId().equalsIgnoreCase(updatedBook.getBookId())) {
                books.set(i, updatedBook);
                break;
            }
        }
        saveToFile();
    }

    private synchronized void saveToFile() {
        try {
            File file = new File(FILE_PATH);
            if (file.getParentFile() != null) {
                file.getParentFile().mkdirs();
            }
            // Ghi mảng JSON sạch đẹp, định dạng thụt lề dễ đọc (Pretty Printer)
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, books);
        } catch (IOException e) {
            System.err.println("[BookRepository] Lỗi ghi file JSON sách: " + e.getMessage());
        }
    }

    private void loadFromFile() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return;
        try {
            List<Book> loadedBooks = objectMapper.readValue(file, new TypeReference<List<Book>>() {});
            books.addAll(loadedBooks);
        } catch (IOException e) {
            System.err.println("[BookRepository] Lỗi nạp file JSON sách: " + e.getMessage());
        }
    }
}