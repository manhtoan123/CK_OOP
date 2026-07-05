package com.library.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.model.BorrowTicket;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger; // Khai báo thư viện biến nguyên tử an toàn đa luồng

@Repository
/**
 * Tầng quản lý lưu trữ dữ liệu Phiếu mượn - Đọc ghi dữ liệu file JSON 'data/tickets.json'
 * Đã cấu hình Thread-safe (An toàn đa luồng) và tối ưu thuật toán sinh mã tăng tiến tự động O(1).
 */
public class BorrowTicketRepository {
    private static final String FILE_PATH = "data/tickets.json";

    // findAndRegisterModules giúp Jackson hiểu và xử lý tự động cấu trúc java.time.LocalDate
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    // Nhiệm vụ 3.2: Sử dụng CopyOnWriteArrayList để loại bỏ lỗi ConcurrentModificationException khi chạy đa luồng
    private final List<BorrowTicket> tickets = new CopyOnWriteArrayList<>();

    // Nhiệm vụ 3.3: Khai báo biến nguyên tử nội bộ để quản lý số thứ tự phiếu mượn lớn nhất
    private final AtomicInteger currentMaxId = new AtomicInteger(0);

    public BorrowTicketRepository() {
        loadFromFile();
    }

    public List<BorrowTicket> getAll() {
        return Collections.unmodifiableList(tickets);
    }

    public BorrowTicket findById(String ticketId) {
        if (ticketId == null) return null;
        return tickets.stream()
                .filter(t -> t.getTicketId().equalsIgnoreCase(ticketId.trim()))
                .findFirst()
                .orElse(null);
    }

    public synchronized void add(BorrowTicket ticket) {
        if (ticket != null) {
            tickets.add(ticket);
            saveToFile();
        }
    }

    public synchronized void update(BorrowTicket updatedTicket) {
        if (updatedTicket == null) return;
        for (int i = 0; i < tickets.size(); i++) {
            if (tickets.get(i).getTicketId().equalsIgnoreCase(updatedTicket.getTicketId())) {
                tickets.set(i, updatedTicket);
                break;
            }
        }
        saveToFile();
    }

    /**
     * Nhiệm vụ 3.3: Hàm sinh mã phiếu mượn tiếp theo chuẩn thread-safe với độ phức tạp tuyệt đối O(1).
     * Loại bỏ hoàn toàn sự phụ thuộc vào vòng lặp duyệt mảng của IdGenerator cũ.
     * * @return Chuỗi mã phiếu mượn mới định dạng PTxxx (Ví dụ: PT006)
     */
    public String generateNextId() {
        // incrementAndGet() tự động tăng giá trị lên 1 và trả về kết quả theo cơ chế Lock-free nguyên tử
        int nextNum = currentMaxId.incrementAndGet();
        return String.format("PT%03d", nextNum);
    }

    private synchronized void saveToFile() {
        try {
            File file = new File(FILE_PATH);
            if (file.getParentFile() != null) {
                file.getParentFile().mkdirs();
            }
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, tickets);
        } catch (IOException e) {
            System.err.println("[BorrowTicketRepository] Lỗi ghi file JSON phiếu mượn: " + e.getMessage());
        }
    }

    private void loadFromFile() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return;
        try {
            List<BorrowTicket> loadedTickets = objectMapper.readValue(file, new TypeReference<List<BorrowTicket>>() {});
            tickets.addAll(loadedTickets);

            // Nhiệm vụ 3.3: Tìm số thứ tự mã lớn nhất ngay khi nạp file lần đầu tiên để cấu hình biến Atomic
            int maxId = 0;
            for (BorrowTicket t : loadedTickets) {
                if (t.getTicketId() != null && t.getTicketId().toUpperCase().startsWith("PT")) {
                    try {
                        // Trích xuất phần số từ chuỗi "PT005" -> 5
                        int idNum = Integer.parseInt(t.getTicketId().substring(2));
                        if (idNum > maxId) {
                            maxId = idNum;
                        }
                    } catch (NumberFormatException ignored) {
                        // Chủ động bỏ qua các mã hỏng định dạng số
                    }
                }
            }
            // Khởi tạo giá trị mốc ban đầu cho bộ đếm nguyên tử
            currentMaxId.set(maxId);

        } catch (IOException e) {
            System.err.println("[BorrowTicketRepository] Lỗi nạp file JSON phiếu mượn: " + e.getMessage());
        }
    }
}