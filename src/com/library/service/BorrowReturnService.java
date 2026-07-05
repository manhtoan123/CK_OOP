package com.library.service;

import com.library.exception.BorrowLimitExceededException;
import com.library.exception.DataNotFoundException;
import com.library.exception.OutOfStockException;
import com.library.model.*;
import com.library.repository.BookRepository;
import com.library.repository.BorrowTicketRepository;
import com.library.repository.ReaderRepository;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
/**
 * Tầng xử lý nghiệp vụ trung tâm quản lý mượn và trả sách.
 * ĐÃ CẬP NHẬT TRUYỀN HẠN TRẢ (DUE DATE) ĐỂ PHỤC VỤ TÍNH PHÍ PHẠT TRÊN FRONTEND REALTIME.
 */
public class BorrowReturnService {
    private final BorrowTicketRepository borrowTicketRepository;
    private final BookRepository bookRepository;
    private final ReaderRepository readerRepository;

    public BorrowReturnService(BorrowTicketRepository borrowTicketRepository,
                               BookRepository bookRepository,
                               ReaderRepository readerRepository) {
        this.borrowTicketRepository = borrowTicketRepository;
        this.bookRepository = bookRepository;
        this.readerRepository = readerRepository;
    }

    /**
     * 🌟 BỔ SUNG CHO FRONTEND TABLE: Lấy toàn bộ danh sách các đầu sách đang được mượn hiện hành
     * ĐÃ CẬP NHẬT: Truyền thêm ticket.getDueDate() sang đối tượng BorrowRecord để hiển thị lên bảng React.
     */
    public List<BorrowRecord> getAllRecords() {
        List<BorrowRecord> activeRecords = new ArrayList<>();
        // 1. Đọc toàn bộ danh sách phiếu mượn từ Repo cứng JSON nạp lên RAM
        List<BorrowTicket> allTickets = borrowTicketRepository.getAll();

        if (allTickets != null) {
            for (BorrowTicket ticket : allTickets) {
                // 2. Chỉ lọc lấy những phiếu mượn có trạng thái đang hoạt động (Chưa hoàn trả)
                if (ticket.getStatus() == BorrowTicket.TicketStatus.BORROWING) {
                    // 3. Duyệt mảng chi tiết bên trong phiếu để bóc tách cặp ReaderId - BookId tương ứng
                    if (ticket.getDetails() != null) {
                        for (BorrowTicketDetail detail : ticket.getDetails()) {
                            BorrowRecord record = new BorrowRecord(
                                    ticket.getReaderId(),
                                    detail.getBookId(),
                                    detail.getQuantity(),
                                    ticket.getBorrowDate() != null ? ticket.getBorrowDate().toString() : "",
                                    ticket.getDueDate() != null ? ticket.getDueDate().toString() : "" // 🌟 ĐÃ THÊM: Đấu nối hạn trả sách sang React
                            );
                            activeRecords.add(record);
                        }
                    }
                }
            }
        }
        return activeRecords;
    }

    /**
     * NGHIỆP VỤ 1: XỬ LÝ ĐĂNG KÝ MƯỢN SÁCH MỚI (GIỮ NGUYÊN HOÀN TOÀN LOGIC CŨ)
     */
    public void borrowBook(String readerId, String bookId, int quantity)
            throws DataNotFoundException, OutOfStockException, BorrowLimitExceededException {
        if (readerId == null || bookId == null || readerId.trim().isEmpty() || bookId.trim().isEmpty()) {
            throw new IllegalArgumentException("Lỗi đầu vào: Mã độc giả và mã sách không được để trống!");
        }

        Reader reader = readerRepository.findById(readerId);
        if (reader == null) {
            throw new DataNotFoundException("Không tìm thấy độc giả mang mã '" + readerId + "' trong hệ thống!");
        }

        Book book = bookRepository.findById(bookId);
        if (book == null) {
            throw new DataNotFoundException("Không tìm thấy đầu sách mang mã '" + bookId + "' trong thư viện!");
        }

        if (book.getQuantity() < quantity) {
            throw new OutOfStockException("Sách mang mã '" + bookId + "' đã hết hoặc không đủ số lượng cung cấp trong kho JSON!");
        }

        int currentBorrowedCount = countCurrentlyBorrowedBooks(readerId);
        if (currentBorrowedCount + quantity > reader.getMaxBorrowLimit()) {
            throw new BorrowLimitExceededException("Độc giả '" + reader.getFullName() + "' đã vượt quá hạn mức mượn tối đa (Hiện đang mượn: "
                    + currentBorrowedCount + " cuốn, hạn mức quy định: " + reader.getMaxBorrowLimit() + " cuốn)!");
        }

        String newTicketId = borrowTicketRepository.generateNextId();

        List<BorrowTicketDetail> details = new ArrayList<>();
        details.add(new BorrowTicketDetail(bookId, quantity));

        LocalDate borrowDate = LocalDate.now();
        LocalDate dueDate = borrowDate.plusDays(14); // Mặc định hạn mượn 14 ngày

        BorrowTicket newTicket = new BorrowTicket(
                newTicketId,
                readerId.trim().toUpperCase(),
                borrowDate,
                dueDate,
                null,
                BorrowTicket.TicketStatus.BORROWING,
                0L,
                details
        );

        book.setQuantity(book.getQuantity() - quantity);
        bookRepository.update(book);
        borrowTicketRepository.add(newTicket);

        System.out.printf("\n✅ CẤP PHIẾU MƯỢN THÀNH CÔNG! Mã phiếu: %s\n", newTicketId);
    }

    /**
     * NGHIỆP VỤ 2: XỬ LÝ NHẬN LẠI SÁCH HOÀN TRẢ VÀ TÍNH PHẠT (GIỮ NGUYÊN LOGIC PHẠT ĐA HÌNH)
     */
    public long returnBook(String readerId, String bookId) throws DataNotFoundException {
        if (readerId == null || bookId == null || readerId.trim().isEmpty() || bookId.trim().isEmpty()) {
            throw new IllegalArgumentException("Lỗi đầu vào: Mã độc giả và mã sách hoàn trả không được để trống!");
        }

        String cleanReaderId = readerId.trim().toUpperCase();
        String cleanBookId = bookId.trim().toUpperCase();

        List<BorrowTicket> allTickets = borrowTicketRepository.getAll();
        BorrowTicket targetTicket = null;

        for (BorrowTicket ticket : allTickets) {
            if (ticket.getReaderId().equalsIgnoreCase(cleanReaderId)
                    && ticket.getStatus() == BorrowTicket.TicketStatus.BORROWING) {

                boolean containsBook = ticket.getDetails().stream()
                        .anyMatch(detail -> detail.getBookId().equalsIgnoreCase(cleanBookId));

                if (containsBook) {
                    targetTicket = ticket;
                    break;
                }
            }
        }

        if (targetTicket == null) {
            throw new DataNotFoundException("Không tìm thấy phiếu mượn đang hoạt động (Đang mượn) nào của độc giả '"
                    + readerId + "' cho đầu sách mang mã '" + bookId + "'!");
        }

        Reader reader = readerRepository.findById(cleanReaderId);
        if (reader == null) {
            throw new DataNotFoundException("Lỗi hệ thống dữ liệu: Phiếu mượn tồn tại nhưng thông tin độc giả gốc '" + cleanReaderId + "' không tìm thấy!");
        }

        Book book = bookRepository.findById(cleanBookId);
        if (book != null) {
            int borrowedQuantity = targetTicket.getDetails().stream()
                    .filter(detail -> detail.getBookId().equalsIgnoreCase(cleanBookId))
                    .mapToInt(BorrowTicketDetail::getQuantity)
                    .sum();

            book.setQuantity(book.getQuantity() + borrowedQuantity);
            bookRepository.update(book);
        }

        LocalDate returnDate = LocalDate.now();
        targetTicket.setReturnDate(returnDate);
        targetTicket.setStatus(BorrowTicket.TicketStatus.RETURNED);

        long overdueDays = ChronoUnit.DAYS.between(targetTicket.getDueDate(), returnDate);
        long fineAmount = 0L;

        if (overdueDays > 0) {
            fineAmount = (long) reader.getFinePolicy().calculateFine((int) overdueDays);
            targetTicket.setFinePaid(fineAmount);
        }

        borrowTicketRepository.update(targetTicket);
        System.out.printf("\n✅ XỬ XUẤT TRẢ SÁCH THÀNH CÔNG! Phiếu mượn [%s] đã đóng.\n", targetTicket.getTicketId());

        return fineAmount;
    }

    private int countCurrentlyBorrowedBooks(String readerId) {
        if (readerId == null) return 0;
        int total = 0;
        List<BorrowTicket> allTickets = borrowTicketRepository.getAll();
        for (BorrowTicket t : allTickets) {
            if (t.getReaderId().equalsIgnoreCase(readerId.trim())
                    && t.getStatus() == BorrowTicket.TicketStatus.BORROWING) {
                total += t.getDetails().stream().mapToInt(BorrowTicketDetail::getQuantity).sum();
            }
        }
        return total;
    }
}