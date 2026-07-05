import React, { useState, useEffect } from 'react';

export default function ReaderForm({ editingReader, onSuccess, onCancel }) {
    const [reader, setReader] = useState({ userId: '', fullName: '', phoneNumber: '', readerType: '' });

    // ✨ ĐÃ SỬA LỖI: Đưa biến này lên đây để toàn bộ giao diện phía dưới đều đọc được, hết sạch lỗi trắng trang
    const isEditMode = !!editingReader;

    // Theo dõi trạng thái để nạp dữ liệu lên Form khi bấm nút "Sửa"
    useEffect(() => {
        if (editingReader) {
            let typeValue = editingReader.readerType;
            if (typeValue === 'Sinh viên thường') typeValue = 'STUDENT';
            if (typeValue === 'Sinh viên ưu tiên') typeValue = 'PRIORITY_STUDENT';
            if (typeValue === 'Giảng viên') typeValue = 'LECTURER';

            setReader({ ...editingReader, readerType: typeValue });
        } else {
            setReader({ userId: '', fullName: '', phoneNumber: '', readerType: '' });
        }
    }, [editingReader]);

    const handleSubmit = async (e) => {
        e.preventDefault();

        // Sử dụng endpoint linh hoạt dựa trên chế độ Thêm hay Sửa
        const url = isEditMode
            ? `http://localhost:8080/api/v1/readers/${reader.userId}`
            : 'http://localhost:8080/api/v1/readers';
        const method = isEditMode ? 'PUT' : 'POST';

        try {
            const res = await fetch(url, {
                method: method,
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(reader)
            });
            const data = await res.json();

            if (!res.ok) {
                if (data.details) {
                    throw new Error(Object.values(data.details).join('\n'));
                }
                throw new Error(data.message || "Thao tác thất bại!");
            }

            alert(`🎉 Thành công: ${data.message}`);

            if (onSuccess) onSuccess(); // Làm mới danh sách ở bảng
            if (isEditMode && onCancel) onCancel(); // Thoát chế độ sửa
            setReader({ userId: '', fullName: '', phoneNumber: '', readerType: '' });
        } catch (err) {
            alert(`❌ Lỗi nghiệp vụ:\n${err.message}`);
        }
    };

    return (
        <div className={`card p-4 shadow-sm mb-4 bg-white border-${isEditMode ? 'warning' : 'primary'}`}>
            <h4 className={`text-${isEditMode ? 'warning' : 'primary'} mb-3 fw-bold`}>
                {isEditMode ? '📝 Hiệu Chỉnh Độc Giả' : '👥 Đăng Ký Độc Giả Mới'}
            </h4>
            <form onSubmit={handleSubmit}>
                <div className="mb-2">
                    <label className="form-label small fw-bold">Mã độc giả (BDxxx)</label>
                    <input
                        type="text"
                        className="form-control"
                        placeholder="Ví dụ: BD006"
                        required
                        disabled={isEditMode} // Khóa ô nhập mã ID khi đang ở chế độ Sửa
                        value={reader.userId}
                        onChange={e => setReader({...reader, userId: e.target.value})}
                    />
                </div>
                <div className="mb-2">
                    <label className="form-label small fw-bold">Họ và tên</label>
                    <input type="text" className="form-control" placeholder="Nhập họ tên" required value={reader.fullName} onChange={e => setReader({...reader, fullName: e.target.value})} />
                </div>
                <div className="mb-2">
                    <label className="form-label small fw-bold">Số điện thoại</label>
                    <input type="text" className="form-control" placeholder="Nhập SĐT" required value={reader.phoneNumber} onChange={e => setReader({...reader, phoneNumber: e.target.value})} />
                </div>
                <div className="mb-3">
                    <label className="form-label small fw-bold">Phân loại độc giả</label>
                    <select className="form-select" required value={reader.readerType} onChange={e => setReader({...reader, readerType: e.target.value})}>
                        <option value="">-- Chọn loại độc giả --</option>
                        <option value="STUDENT">Sinh viên thường</option>
                        <option value="PRIORITY_STUDENT">Sinh viên ưu tiên</option>
                        <option value="LECTURER">Giảng viên</option>
                    </select>
                </div>

                <div className="d-flex gap-2">
                    <button type="submit" className={`btn btn-${isEditMode ? 'warning text-white' : 'primary'} flex-grow-1 fw-bold`}>
                        {isEditMode ? 'Cập Nhật Thẻ' : 'Xác Nhận Cấp Thẻ'}
                    </button>
                    {isEditMode && (
                        <button type="button" className="btn btn-secondary fw-bold" onClick={onCancel}>
                            Hủy
                        </button>
                    )}
                </div>
            </form>
        </div>
    );
}