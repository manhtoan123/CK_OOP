package com.library.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

/**
 * Lớp thực thể phụ thuộc đại diện cho chi tiết từng dòng sách trong phiếu mượn.
 * Thiết kế theo dạng Immutable Object (Đối tượng bất biến) nhằm bảo vệ an toàn toàn vẹn dữ liệu.
 * ĐÃ SỬA LỖI GIAO KÈO EQUALS/HASHCODE VÀ TÍCH HỢP TƯƠNG THÍCH ĐỌC FILE JSON.
 */
public final class BorrowTicketDetail {

    private final String bookId;
    private final int quantity;

    /**
     * [CẢI TIẾN GIAI ĐOẠN 2]: Dùng @JsonCreator và @JsonProperty để Jackson tự động
     * ánh xạ các trường từ tệp JSON ("bookId", "quantity") chui thẳng vào tham số Constructor
     * của đối tượng Immutable (vốn chặn hoàn toàn các hàm Setter).
     */
    @JsonCreator
    public BorrowTicketDetail(
            @JsonProperty("bookId") String bookId,
            @JsonProperty("quantity") int quantity) {
        if (bookId == null || bookId.trim().isEmpty()) {
            throw new IllegalArgumentException("Lỗi dữ liệu: Mã sách không được để trống hoặc nhận giá trị null.");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Lỗi dữ liệu: Số lượng sách đăng ký mượn phải lớn hơn 0.");
        }

        // Tự động chuẩn hóa viết hoa mã sách (b001 -> B001) để khớp đồng bộ dữ liệu
        this.bookId = bookId.trim().toUpperCase();
        this.quantity = quantity;
    }

    // ─── Getters (Không viết hàm Setters để giữ tuyệt đối tính chất Bất biến) ───

    public String getBookId() {
        return bookId;
    }

    public int getQuantity() {
        return quantity;
    }

    // ─── Đồng bộ hóa hiển thị & So sánh đối tượng ────────────────────────────

    @Override
    public String toString() {
        return bookId + ":" + quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BorrowTicketDetail that = (BorrowTicketDetail) o;
        return quantity == that.quantity && Objects.equals(bookId, that.bookId);
    }

    /**
     * 🛠️ ĐÃ SỬA LỖI KIẾN TRÚC: Bắt buộc phải @Override hashCode() khi đã override equals().
     * Nếu không có hàm này, các phép toán tìm kiếm, so sánh hoặc loại bỏ phần tử lặp
     * (ví dụ: các hàm list.contains(), list.remove(), dùng HashSet hoặc gom cụm Stream API)
     * sẽ hoạt động sai lệch một cách ngẫu nhiên và vô cùng khó debug trên RAM.
     */
    @Override
    public int hashCode() {
        return Objects.hash(bookId, quantity);
    }
}