package com.library.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
/**
 * Bộ điều hướng và xử lý lỗi tập trung toàn hệ thống (Global AOP Exception Interceptor).
 * Tự động bắt lỗi từ tầng dưới ném lên và chuyển đổi sang mã HTTP RESTful tương ứng.
 */
public class GlobalExceptionHandler {

    /**
     * BẪY LỖI 1: Xử lý ngoại lệ Không tìm thấy dữ liệu (Sách, Độc giả, Phiếu mượn).
     * Ánh xạ chính xác về HTTP Status: 404 NOT FOUND.
     */
    @ExceptionHandler(DataNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleDataNotFound(DataNotFoundException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    /**
     * BẪY LỖI 2: Xử lý ngoại lệ hết sách trong kho.
     * Ánh xạ chính xác về HTTP Status: 400 BAD REQUEST.
     */
    @ExceptionHandler(OutOfStockException.class)
    public ResponseEntity<Map<String, Object>> handleOutOfStock(OutOfStockException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    /**
     * BẪY LỖI 3: Xử lý ngoại lệ mượn sách vượt hạn mức quy định.
     * Ánh xạ chính xác về HTTP Status: 400 BAD REQUEST.
     */
    @ExceptionHandler(BorrowLimitExceededException.class)
    public ResponseEntity<Map<String, Object>> handleBorrowLimitExceeded(BorrowLimitExceededException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    /**
     * BẪY LỖI 4: Tự động gom các lỗi Validation đầu vào của DTO thông qua từ khóa @Valid.
     * Trích xuất chính xác thuộc tính nào bị lỗi và thông báo cấu hình ở @NotBlank, @Min...
     * Ánh xạ về HTTP Status: 400 BAD REQUEST.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Dữ liệu đầu vào không hợp lệ!");
        body.put("details", errors); // Trả về chi tiết danh sách trường bị lỗi cho Frontend

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    /**
     * BẪY LỖI 5: Bắt các ngoại lệ logic tham số đầu vào trái phép (IllegalArgumentException).
     * Ánh xạ về HTTP Status: 400 BAD REQUEST.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    /**
     * BẪY LỖI SẬP NỀN: Bắt tất cả các lỗi hệ thống không lường trước được (NullPointer, Bug logic RAM...).
     * Ngăn chặn việc lộ thông tin code Java nhạy cảm ra Frontend.
     * Ánh xạ về HTTP Status: 500 INTERNAL SERVER ERROR.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGlobalException(Exception ex) {
        System.err.println("[CRITICAL BUG]: " + ex.getMessage());
        ex.printStackTrace();
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Hệ thống thư viện xảy ra sự cố đột xuất! Vui lòng liên hệ Admin.");
    }

    /**
     * Hàm helper phụ trợ đóng gói cấu trúc JSON phản hồi lỗi chuẩn đồng bộ.
     */
    private ResponseEntity<Map<String, Object>> buildErrorResponse(HttpStatus status, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", status.value());
        body.put("message", message);
        return new ResponseEntity<>(body, status);
    }
}