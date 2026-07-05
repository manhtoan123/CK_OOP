package com.library.service;

import com.library.exception.DataNotFoundException;
import com.library.model.Book;
import com.library.repository.BookRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
/**
 * Tầng xử lý logic nghiệp vụ liên quan đến Sách.
 * ĐÃ CẬP NHẬT BỘ NGOẠI LỆ CHUYÊN BIỆT THEO NHIỆM VỤ 4.3 VÀ BỔ SUNG CRUD ĐỒNG BỘ FRONTEND REACT.
 */
public class BookService {
    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<Book> getAllBooks() {
        return bookRepository.getAll();
    }

    /**
     * 🛠️ Nhiệm vụ 4.3: Áp dụng DataNotFoundException cho phương thức tìm kiếm đầu sách.
     */
    public Book getBookById(String bookId) throws DataNotFoundException {
        if (bookId == null || bookId.trim().isEmpty()) {
            throw new IllegalArgumentException("Lỗi: Mã sách tìm kiếm không được để trống!");
        }

        Book book = bookRepository.findById(bookId.trim());
        if (book == null) {
            throw new DataNotFoundException("Lỗi nghiệp vụ: Không tìm thấy đầu sách mang mã '" + bookId + "' trong hệ thống thư viện!");
        }
        return book;
    }

    public void addBook(Book book) {
        if (book == null || book.getBookId() == null || book.getBookId().trim().isEmpty()) {
            throw new IllegalArgumentException("Lỗi: Thông tin sách hoặc mã sách không được để trống!");
        }
        if (bookRepository.findById(book.getBookId().trim()) != null) {
            throw new IllegalArgumentException("Lỗi: Mã sách '" + book.getBookId() + "' đã tồn tại trong kho dữ liệu!");
        }
        bookRepository.addBook(book);
    }

    /**
     * 🛠️ BỔ SUNG CHO FRONTEND CRUD: Hàm hiệu chỉnh thông tin toàn bộ đầu sách (Sửa - PUT)
     * Khớp nối chính xác 100% với cổng @PutMapping("/books/{id}") tại LibraryApiController.
     */
    public void updateBook(String id, Book updatedBook) throws DataNotFoundException {
        if (id == null || id.trim().isEmpty() || updatedBook == null) {
            throw new IllegalArgumentException("Lỗi: Dữ liệu cập nhật đầu sách không hợp lệ!");
        }

        // 1. Kiểm tra sự tồn tại của đầu sách trong kho JSON trước khi sửa
        if (bookRepository.findById(id.trim()) == null) {
            throw new DataNotFoundException("Lỗi: Không tìm thấy đầu sách mang mã '" + id + "' trong kho cứng JSON để thực hiện hiệu chỉnh!");
        }

        // 2. Kiểm tra ràng buộc an toàn dữ liệu đầu vào nghiệp vụ kho sách
        if (updatedBook.getTitle() == null || updatedBook.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Lỗi: Tên cuốn sách không được để trống!");
        }
        if (updatedBook.getAuthor() == null || updatedBook.getAuthor().trim().isEmpty()) {
            throw new IllegalArgumentException("Lỗi: Tên tác giả không được để trống!");
        }
        if (updatedBook.getQuantity() < 0) {
            throw new IllegalArgumentException("Lỗi: Số lượng tồn kho không được là số âm!");
        }
        if (updatedBook.getPrice() < 0) {
            throw new IllegalArgumentException("Lỗi: Đơn giá sách không được nhỏ hơn 0!");
        }

        // 3. Gọi xuống tầng Repository để ghi đè cập nhật lại file JSON cứng
        bookRepository.update(updatedBook);
    }

    /**
     * 🛠️ Nhiệm vụ 4.3: Áp dụng DataNotFoundException khi cố gắng xóa sách không tồn tại.
     */
    public void deleteBook(String bookId) throws DataNotFoundException {
        if (bookId == null || bookId.trim().isEmpty()) {
            throw new IllegalArgumentException("Lỗi: Mã sách cần xóa không hợp lệ!");
        }
        if (bookRepository.findById(bookId.trim()) == null) {
            throw new DataNotFoundException("Lỗi: Không tìm thấy đầu sách mang mã '" + bookId + "' trong kho cứng JSON để thực hiện lệnh xóa!");
        }
        bookRepository.deleteBook(bookId);
    }

    /**
     * Cập nhật số lượng sách tồn kho và tự động đồng bộ xuống file cứng JSON ngầm.
     */
    public void updateBookQuantity(String bookId, int newQuantity) throws DataNotFoundException {
        if (newQuantity < 0) {
            throw new IllegalArgumentException("Lỗi: Số lượng sách trong kho không được là số âm!");
        }

        Book book = getBookById(bookId);
        book.setQuantity(newQuantity);
        bookRepository.update(book);
    }
}