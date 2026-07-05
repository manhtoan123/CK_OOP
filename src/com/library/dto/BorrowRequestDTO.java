package com.library.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO đóng gói Payload gửi lên khi thực hiện đăng ký mượn sách.
 * Khớp hoàn toàn với các tham số yêu cầu tại hàm borrowReturnService.borrowBook(...)
 */
public class BorrowRequestDTO {

    @NotBlank(message = "Yêu cầu cung cấp mã độc giả thực hiện mượn!")
    private String readerId;

    @NotBlank(message = "Yêu cầu cung cấp mã cuốn sách đăng ký mượn!")
    private String bookId;

    @NotNull(message = "Số lượng sách đăng ký mượn không được để trống!")
    @Min(value = 1, message = "Số lượng sách đăng ký mượn tối thiểu phải từ 1 cuốn trở lên!")
    private Integer quantity;

    // ─── Constructors ────────────────────────────────────────────────────────
    public BorrowRequestDTO() {
    }

    // ─── Getters & Setters ───────────────────────────────────────────────────
    public String getReaderId() { return readerId; }
    public void setReaderId(String readerId) { this.readerId = readerId; }

    public String getBookId() { return bookId; }
    public void setBookId(String bookId) { this.bookId = bookId; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}