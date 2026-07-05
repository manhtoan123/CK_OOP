package com.library.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.model.Reader;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Repository
public class ReaderRepository {
    private static final String FILE_PATH = "data/readers.json";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final List<Reader> readers = new CopyOnWriteArrayList<>();

    public ReaderRepository() {
        loadFromFile();
    }

    public List<Reader> getAll() {
        return Collections.unmodifiableList(readers);
    }

    public Reader findById(String readerId) {
        if (readerId == null) return null;
        return readers.stream()
                .filter(r -> r.getUserId().equalsIgnoreCase(readerId.trim()))
                .findFirst()
                .orElse(null);
    }

    public synchronized void add(Reader reader) {
        if (reader != null) {
            readers.add(reader);
            saveToFile();
        }
    }

    public synchronized void update(Reader updatedReader) {
        if (updatedReader == null) return;
        for (int i = 0; i < readers.size(); i++) {
            if (readers.get(i).getUserId().equalsIgnoreCase(updatedReader.getUserId())) {
                readers.set(i, updatedReader);
                break;
            }
        }
        saveToFile();
    }

    /**
     * HÀM XÓA ĐỘC GIẢ KHỎI KHO DỮ LIỆU JSON (BỔ SUNG CHO FRONTEND CRUD)
     * Tự động giải phóng khỏi danh sách RAM và cập nhật đồng bộ xuống file data/readers.json
     */
    public synchronized void delete(String id) {
        if (id == null) return;

        // 1. Xóa độc giả có mã trùng khớp khỏi danh sách định danh trên RAM
        readers.removeIf(r -> r.getUserId().equalsIgnoreCase(id.trim()));

        // 2. Ghi đè cập nhật lại danh sách mới sạch sẽ xuống file cứng readers.json
        saveToFile();
    }

    private synchronized void saveToFile() {
        try {
            File file = new File(FILE_PATH);
            if (file.getParentFile() != null) {
                file.getParentFile().mkdirs();
            }
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, readers);
        } catch (IOException e) {
            System.err.println("[ReaderRepository] Lỗi ghi file JSON bạn đọc: " + e.getMessage());
        }
    }

    private void loadFromFile() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return;
        try {
            // Jackson tự động dựa vào @JsonTypeInfo ở lớp Reader để ép về đúng class con (Student/Lecturer)
            List<Reader> loadedReaders = objectMapper.readValue(file, new TypeReference<List<Reader>>() {});
            readers.addAll(loadedReaders);
        } catch (IOException e) {
            System.err.println("[ReaderRepository] Lỗi nạp file JSON bạn đọc: " + e.getMessage());
        }
    }
}