import { useState } from "react";
import { inspectionApi } from "../../api/api";
import {
  TextInput,
  TextArea,
  SelectInput,
} from "../../components/FormControls";

const inspectionTypes = ["일상점검", "정기점검", "특별점검"];

const initialForm = {
  formName: "",
  inspectionType: "",
  description: "",
};

function InspectionCreate({ user }) {
  const [form, setForm] = useState(initialForm);
  const [formFile, setFormFile] = useState(null);

  const create = async (e) => {
    e.preventDefault();

    try {
      const formData = new FormData();

      formData.append(
        "data",
        new Blob([JSON.stringify(form)], {
          type: "application/json",
        }),
      );

      if (formFile) {
        formData.append("file", formFile);
      }

      await inspectionApi.create(formData);

      alert("점검 양식 등록 완료");
      setForm(initialForm);
      setFormFile(null);
      setShowCreate(false);
      load();
    } catch (error) {
      alert(error.message);
    }
  };

  return (
    <section className="page">
      <form className="create-card" onSubmit={create}>
        <h3>점검 양식 등록</h3>

        <div className="form-grid">
          <TextInput
            label="양식명"
            value={form.formName}
            onChange={(e) => setForm({ ...form, formName: e.target.value })}
            required
          />

          <SelectInput
            label="점검 유형"
            value={form.inspectionType}
            onChange={(e) =>
              setForm({ ...form, inspectionType: e.target.value })
            }
            required
          >
            <option value="">점검유형을 선택하세요</option>
            {inspectionTypes.map((type) => (
              <option key={type} value={type}>
                {type}
              </option>
            ))}
          </SelectInput>

          <TextInput
            label="점검 양식 파일"
            type="file"
            accept=".html,.htm"
            buttonText="양식 파일 선택"
            helperText="HTML 형식의 점검 양식을 업로드하세요"
            onChange={(e) => setFormFile(e.target.files?.[0] || null)}
          />

          <TextArea
            label="설명"
            value={form.description}
            onChange={(e) => setForm({ ...form, description: e.target.value })}
          />
        </div>

        <button type="submit">등록</button>
      </form>
    </section>
  );
}
export default InspectionCreate;
