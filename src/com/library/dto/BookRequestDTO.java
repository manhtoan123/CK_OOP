package com.library.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/**
 * DTO tiếp nhận yêu cầu xử lý thông tin Sách từ Giao diện Frontend.
 */
public class BookRequestDTO {

    @NotBlank(message = "Mã sách không được để trống!")
    @Pattern(regexp = "^B\\d{3}$", message = "Mã sách phải tuân thủ định dạng 'Bxxx' (Ví dụ: B001, B002)!")
    private String bookId;

    @NotBlank(message = "Tên cuốn sách không được để trống!")
    private String title;

    @NotBlank(message = "Tên tác giả không được để trống!")
    private String author;

    @NotBlank(message = "Thể loại sách không được để trống!")
    private String category;

    @NotNull(message = "Số lượng sách không được để trống!")
    @Min(value = 0, message = "Số lượng sách trong kho không được nhỏ hơn 0!")
    private Integer quantity;

    @NotNull(message = "Giá tiền của sách không được để trống!")
    @Min(value = 0, message = "Giá trị của sách phải lớn hơn hoặc bằng 0đ!")
    private Long price;

    // ─── Constructors ────────────────────────────────────────────────────────
    public BookRequestDTO() {
    }

    // ─── Getters & Setters ───────────────────────────────────────────────────
    public String getBookId() { return bookId; }
    public void setBookId(String bookId) { this.bookId = bookId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public Long getPrice() { return price; }
    public void setPrice(Long price) { this.price = price; }
}