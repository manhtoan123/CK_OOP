package com.library.controller;

import com.library.dto.BookRequestDTO;
import com.library.dto.BorrowRequestDTO;
import com.library.dto.ReaderRequestDTO;
import com.library.dto.ReturnRequestDTO;
import com.library.model.*;
import com.library.service.BookService;
import com.library.service.BorrowReturnService;
import com.library.service.ReaderService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "*") // Hỗ trợ Frontend gọi API không bị lỗi chặn CORS (Cross-Origin)
/**
 * Cổng điều hướng REST API trung tâm đón nhận yêu cầu từ giao diện Web HTML.
 * ĐÃ XÓA 100% TRY-CATCH RƯỜM RÀ, ÁP DỤNG ĐÓNG GÓI DTO VÀ XÁC THỰC @Valid THEO NHIỆM VỤ 5.3.
 */
public class LibraryApiController {

    private final BookService bookService;
    private final ReaderService readerService;
    private final BorrowReturnService borrowReturnService;

    public LibraryApiController(BookService bookService,
                                ReaderService readerService,
                                BorrowReturnService borrowReturnService) {
        this.bookService = bookService;
        this.readerService = readerService;
        this.borrowReturnService = borrowReturnService;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 📚 PHÂN HỆ QUẢN LÝ SÁCH (BOOK API)
    // ─────────────────────────────────────────────────────────────────────────

    @GetMapping("/books")
    public ResponseEntity<List<Book>> getAllBooks() {
        return ResponseEntity.ok(bookService.getAllBooks());
    }

    @GetMapping("/books/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable String id) throws Exception {
        return ResponseEntity.ok(bookService.getBookById(id));
    }

    @PostMapping("/books")
    public ResponseEntity<Map<String, String>> addBook(@Valid @RequestBody BookRequestDTO dto) {
        Book entity = new Book(
                dto.getBookId(),
                dto.getTitle(),
                dto.getAuthor(),
                dto.getCategory(),
                dto.getQuantity(),
                dto.getPrice()
        );
        bookService.addBook(entity);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Thêm mới đầu sách vào kho JSON thành công!"));
    }

    @DeleteMapping("/books/{id}")
    public ResponseEntity<Map<String, String>> deleteBook(@PathVariable String id) throws Exception {
        bookService.deleteBook(id);
        return ResponseEntity.ok(Map.of("message", "Xóa đầu sách khỏi kho dữ liệu thành công!"));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 👥 PHÂN HỆ QUẢN LÝ ĐỘC GIẢ (READER API) - ĐÃ BỔ SUNG ĐẦY ĐỦ CRUD
    // ─────────────────────────────────────────────────────────────────────────

    @GetMapping("/readers")
    public ResponseEntity<List<Reader>> getAllReaders() {
        return ResponseEntity.ok(readerService.getAllReaders());
    }

    @GetMapping("/readers/{id}")
    public ResponseEntity<Reader> getReaderById(@PathVariable String id) throws Exception {
        return ResponseEntity.ok(readerService.getReaderById(id));
    }

    @PostMapping("/readers")
    public ResponseEntity<Map<String, String>> registerReader(@Valid @RequestBody ReaderRequestDTO dto) {
        Reader readerEntity;
        String type = dto.getReaderType().toUpperCase();

        switch (type) {
            case "LECTURER" ->
                    readerEntity = new LecturerReader(dto.getUserId(), dto.getFullName(), dto.getPhoneNumber());
            case "PRIORITY_STUDENT" ->
                    readerEntity = new PriorityStudentReader(dto.getUserId(), dto.getFullName(), dto.getPhoneNumber());
            default ->
                    readerEntity = new StudentReader(dto.getUserId(), dto.getFullName(), dto.getPhoneNumber());
        }

        readerService.registerReader(readerEntity);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Đăng ký tài khoản độc giả thành công!"));
    }

    /**
     * 📝 1. API CẬP NHẬT THÔNG TIN ĐỘC GIẢ (SỬA - PUT)
     * Đón nhận mã ID từ đường dẫn và DTO dữ liệu mới từ React gửi sang.
     */
    @PutMapping("/readers/{id}")
    public ResponseEntity<Map<String, String>> updateReader(@PathVariable String id, @Valid @RequestBody ReaderRequestDTO dto) throws Exception {
        Reader readerEntity;
        String type = dto.getReaderType().toUpperCase();

        // Áp dụng tính chất Đa hình khởi tạo lớp con tương ứng khi sửa đổi phân loại thẻ
        switch (type) {
            case "LECTURER" ->
                    readerEntity = new LecturerReader(id, dto.getFullName(), dto.getPhoneNumber());
            case "PRIORITY_STUDENT" ->
                    readerEntity = new PriorityStudentReader(id, dto.getFullName(), dto.getPhoneNumber());
            default ->
                    readerEntity = new StudentReader(id, dto.getFullName(), dto.getPhoneNumber());
        }

        // Gọi xuống tầng Service để xử lý ghi đè vào tệp cứng JSON độc giả
        readerService.updateReader(id, readerEntity);
        return ResponseEntity.ok(Map.of("message", "Cập nhật thông tin thẻ độc giả thành công!"));
    }

    /**
     * 📝 API CẬP NHẬT THÔNG TIN ĐẦU SÁCH (SỬA - PUT)
     * Nhận mã ID từ đường dẫn và DTO dữ liệu hiệu chỉnh từ React truyền sang.
     */
    @PutMapping("/books/{id}")
    public ResponseEntity<Map<String, String>> updateBook(@PathVariable String id, @Valid @RequestBody BookRequestDTO dto) throws Exception {
        Book entity = new Book(
                id,
                dto.getTitle(),
                dto.getAuthor(),
                dto.getCategory(),
                dto.getQuantity(),
                dto.getPrice()
        );

        // Gọi xuống tầng Service để xử lý cập nhật vào tệp JSON
        // 💡 Mẹo: Nếu file BookService của bạn đặt tên hàm là update hoặc updateBookInfo thì bạn đổi tên chữ .updateBook thành tên tương ứng nhé
        bookService.updateBook(id, entity);
        return ResponseEntity.ok(Map.of("message", "Cập nhật thông tin đầu sách thành công!"));
    }


    /**
     * 🗑️ 2. API XÓA ĐỘC GIẢ KHỎI HỆ THỐNG (XOÁ - DELETE)
     * Tiếp nhận mã Độc giả cần giải phóng khỏi kho dữ liệu JSON.
     */
    @DeleteMapping("/readers/{id}")
    public ResponseEntity<Map<String, String>> deleteReader(@PathVariable String id) throws Exception {
        // Gọi xuống Service xử lý xoá bản ghi. Nếu vướng ràng buộc mượn trả, Service sẽ ném Exception
        // và được GlobalExceptionHandler tự động bắt lấy trả về thông báo lỗi cho React.
        readerService.deleteReader(id);
        return ResponseEntity.ok(Map.of("message", "Xóa tài khoản độc giả khỏi hệ thống thành công!"));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 💸 PHÂN HỆ GIAO DỊCH MƯỢN / TRẢ SÁCH (TRANSACTION API)
    // ─────────────────────────────────────────────────────────────────────────

    @PostMapping("/borrow")
    public ResponseEntity<Map<String, String>> processBorrow(@Valid @RequestBody BorrowRequestDTO dto) throws Exception {
        borrowReturnService.borrowBook(dto.getReaderId(), dto.getBookId(), dto.getQuantity());
        return ResponseEntity.ok(Map.of("message", "Cấp phiếu mượn sách thành công! Kho cứng JSON đã đồng bộ."));
    }

    @GetMapping("/borrow")
    /**
     * API LẤY DANH SÁCH PHIẾU MƯỢN THỰC TẾ TRÊN HỆ THỐNG
     */
    public ResponseEntity<List<BorrowRecord>> getAllBorrowRecords() {
        // Gọi xuống service mượn trả để lấy danh sách phiếu mượn hoạt động
        // 💡 Mẹo: Nếu file BorrowReturnService của nhóm đặt tên hàm lấy danh sách là .getAll() hoặc .getRecords()
        // thì bạn chỉ cần đổi chữ .getAllRecords() dưới đây thành tên hàm thực tế đó nhé!
        return ResponseEntity.ok(borrowReturnService.getAllRecords());
    }

    @PostMapping("/return")
    /**
     * API Hoàn trả sách và thực hiện đóng phiếu phạt đa hình.
     * ĐÃ NÂNG CẤP: Trả về chi tiết số tiền phạt trực quan thời gian thực lên alert React.
     */
    public ResponseEntity<Map<String, String>> processReturn(@Valid @RequestBody ReturnRequestDTO dto) throws Exception {
        // Tiếp nhận số tiền phạt từ Service ném lên
        long fineAmount = borrowReturnService.returnBook(dto.getReaderId(), dto.getBookId());

        String responseMessage;
        if (fineAmount > 0) {
            // Thiết lập câu thông báo tài chính chi tiết nếu độc giả trả muộn quá hạn quy định
            responseMessage = String.format("Nhận lại sách hoàn trả thành công! Độc giả trả sách QUÁ HẠN. Hệ thống tự động quyết toán phí phạt trễ hạn: %,d đ.", fineAmount);
        } else {
            // Thông báo êm ái nếu độc giả hoàn sách đúng hạn
            responseMessage = "Nhận lại sách hoàn trả thành công! Độc giả hoàn trả sách ĐÚNG HẠN, không phát sinh phí phạt.";
        }

        return ResponseEntity.ok(Map.of("message", responseMessage));
    }
}